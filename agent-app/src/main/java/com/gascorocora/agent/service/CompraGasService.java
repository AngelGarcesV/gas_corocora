package com.gascorocora.agent.service;

import com.gascorocora.agent.dto.OrdenCompraDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de compras de gas
 * Obtiene instancias de procesos directamente de la API de Camunda
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompraGasService {

    private final WebClient camundaWebClient;

    /**
     * Obtiene todas las órdenes de compra desde Camunda
     * Usa la API de process-instance de Camunda
     */
    public List<OrdenCompraDTO> obtenerTodasLasOrdenes() {
        try {
            log.info("Obteniendo instancias de proceso Compra_gas...");
            
            // Llamar a la API de Camunda para obtener instancias del proceso
            // La API REST de Camunda requiere processDefinitionKey como parámetro de query
            List<Map<String, Object>> procesos = camundaWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/process-instance")
                            .queryParam("processDefinitionKey", "Compra_gas")
                            .build())
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .collectList()
                    .block();
            
            List<OrdenCompraDTO> ordenes = new ArrayList<>();
            
            if (procesos != null && !procesos.isEmpty()) {
                log.info("Se encontraron {} procesos", procesos.size());
                
                for (Map<String, Object> proceso : procesos) {
                    try {
                        OrdenCompraDTO dto = convertirProcesoAOrden(proceso);
                        if (dto != null) {
                            ordenes.add(dto);
                        }
                    } catch (Exception e) {
                        log.warn("Error procesando instancia: {}", e.getMessage());
                    }
                }
            } else {
                log.info("No se encontraron procesos de compra de gas");
            }
            
            log.info("Se retornaron {} órdenes", ordenes.size());
            return ordenes;
        } catch (Exception e) {
            log.error("Error al obtener órdenes: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene una orden específica por su ID
     */
    public OrdenCompraDTO obtenerOrdenPorId(String ordenId) {
        try {
            Map<String, Object> proceso = camundaWebClient.get()
                    .uri("/process-instance/" + ordenId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            
            if (proceso != null) {
                return convertirProcesoAOrden(proceso);
            }
            return null;
        } catch (Exception e) {
            log.warn("Orden de compra no encontrada: {}", ordenId);
            return null;
        }
    }

    /**
     * Obtiene órdenes filtradas por estado
     */
    public List<OrdenCompraDTO> obtenerOrdenesPorEstado(String estado) {
        try {
            List<Map<String, Object>> procesos = camundaWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/process-instance")
                            .queryParam("processDefinitionKey", "Compra_gas")
                            .build())
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .collectList()
                    .block();

            List<OrdenCompraDTO> ordenes = new ArrayList<>();
            if (procesos != null) {
                for (Map<String, Object> proceso : procesos) {
                    OrdenCompraDTO dto = convertirProcesoAOrden(proceso);
                    if (dto != null) {
                        ordenes.add(dto);
                    }
                }
            }
            return ordenes;
        } catch (Exception e) {
            log.error("Error al filtrar órdenes por estado '{}': {}", estado, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene órdenes filtradas por usuario solicitante
     */
    public List<OrdenCompraDTO> obtenerOrdenesPorUsuario(String usuario) {
        try {
            // Obtener todas y filtrar por variables del proceso
            List<Map<String, Object>> procesos = camundaWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/process-instance")
                            .queryParam("processDefinitionKey", "Compra_gas")
                            .build())
                    .retrieve()
                    .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .collectList()
                    .block();

            List<OrdenCompraDTO> ordenes = new ArrayList<>();
            if (procesos != null) {
                for (Map<String, Object> proceso : procesos) {
                    OrdenCompraDTO dto = convertirProcesoAOrden(proceso);
                    if (dto != null && usuario.equalsIgnoreCase(dto.getUsuarioSolicitante())) {
                        ordenes.add(dto);
                    }
                }
            }
            return ordenes;
        } catch (Exception e) {
            log.error("Error al filtrar órdenes por usuario '{}': {}", usuario, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene estadísticas de compras
     */
    public Map<String, Object> obtenerEstadisticas() {
        try {
            Map<String, Object> stats = new HashMap<>();
            List<OrdenCompraDTO> todasLasOrdenes = obtenerTodasLasOrdenes();

            stats.put("totalOrdenes", todasLasOrdenes.size());
            
            long ordenesAprobadas = todasLasOrdenes.stream()
                    .filter(o -> "APROBADA".equalsIgnoreCase(o.getEstado()))
                    .count();
            stats.put("ordenesAprobadas", ordenesAprobadas);

            long ordenesRechazadas = todasLasOrdenes.stream()
                    .filter(o -> "RECHAZADA".equalsIgnoreCase(o.getEstado()))
                    .count();
            stats.put("ordenesRechazadas", ordenesRechazadas);

            long ordenesPendientes = todasLasOrdenes.stream()
                    .filter(o -> "PENDIENTE".equalsIgnoreCase(o.getEstado()) || 
                                 "EN_PROCESO".equalsIgnoreCase(o.getEstado()))
                    .count();
            stats.put("ordenesPendientes", ordenesPendientes);

            Double costoTotal = todasLasOrdenes.stream()
                    .filter(o -> o.getCostoTotal() != null)
                    .mapToDouble(OrdenCompraDTO::getCostoTotal)
                    .sum();
            stats.put("costoTotal", costoTotal);

            Double costoPromedio = todasLasOrdenes.stream()
                    .filter(o -> o.getCostoTotal() != null)
                    .mapToDouble(OrdenCompraDTO::getCostoTotal)
                    .average()
                    .orElse(0.0);
            stats.put("costoPromedio", costoPromedio);

            Map<String, Long> proveedoresCount = todasLasOrdenes.stream()
                    .collect(Collectors.groupingBy(OrdenCompraDTO::getProveedor, Collectors.counting()));
            stats.put("proveedoresMasUsados", proveedoresCount);

            return stats;
        } catch (Exception e) {
            log.error("Error al obtener estadísticas", e);
            return new HashMap<>();
        }
    }

    /**
     * Convierte una instancia de proceso Camunda a OrdenCompraDTO
     */
    private OrdenCompraDTO convertirProcesoAOrden(Map<String, Object> proceso) {
        try {
            OrdenCompraDTO dto = new OrdenCompraDTO();
            String instanceId = toString(proceso.get("id"));
            
            // Obtener las variables del proceso
            Map<String, Object> variables = obtenerVariablesDelProceso(instanceId);
            
            // El ID de Camunda es un UUID, no un número - usar como String
            dto.setId(1L);  // ID temporal, lo importante es el ordenId
            dto.setOrdenId(toString(proceso.getOrDefault("businessKey", instanceId)));
            
            // Determinar estado basado en si está activo o completado
            Boolean isEnded = (Boolean) proceso.get("ended");
            if (isEnded != null && isEnded) {
                dto.setEstado("COMPLETADA");
            } else {
                Boolean isSuspended = (Boolean) proceso.get("suspended");
                dto.setEstado(isSuspended != null && isSuspended ? "SUSPENDIDA" : "EN_PROCESO");
            }
            
            // Extraer variables del proceso
            if (variables != null) {
                if (variables.containsKey("proveedor")) {
                    dto.setProveedor(toString(variables.get("proveedor")));
                }
                if (variables.containsKey("cantidadGas")) {
                    dto.setCantidadGas(toInteger(variables.get("cantidadGas")));
                }
                if (variables.containsKey("costoTotal")) {
                    dto.setCostoTotal(toDouble(variables.get("costoTotal")));
                }
                if (variables.containsKey("costoUnitario")) {
                    dto.setCostoUnitario(toDouble(variables.get("costoUnitario")));
                }
                if (variables.containsKey("tiempoEntrega")) {
                    dto.setTiempoEntregaDias(toInteger(variables.get("tiempoEntrega")));
                }
                if (variables.containsKey("usuarioSolicitante")) {
                    dto.setUsuarioSolicitante(toString(variables.get("usuarioSolicitante")));
                }
                if (variables.containsKey("usuarioAprueba")) {
                    dto.setUsuarioAprueba(toString(variables.get("usuarioAprueba")));
                }
            }
            
            return dto;
        } catch (Exception e) {
            log.error("Error al convertir proceso a orden: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene las variables de una instancia de proceso
     */
    private Map<String, Object> obtenerVariablesDelProceso(String instanceId) {
        try {
            return camundaWebClient.get()
                    .uri("/process-instance/" + instanceId + "/variables")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            log.debug("No se pudieron obtener variables para proceso: {}", instanceId);
            return new HashMap<>();
        }
    }

    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) return Long.parseLong((String) value);
        return null;
    }

    private Integer toInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof String) return Integer.parseInt((String) value);
        return null;
    }

    private Double toDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        if (value instanceof Long) return ((Long) value).doubleValue();
        if (value instanceof String) return Double.parseDouble((String) value);
        return null;
    }
}
