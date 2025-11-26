package com.gascorocora.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompraGasService {

        private final WebClient camundaWebClient;

        /**
         * Inicia un nuevo proceso de compra de gas
         */
        public Map<String, Object> startCompraGasProcess(String cantidadGas, String proveedorPreferido,
                        String usuarioSolicitante, String justificacion,
                        String fechaRequerida) {
                String businessKey = "COMPRA-" + System.currentTimeMillis();

                Map<String, Object> variables = new HashMap<>();
                variables.put("cantidadGas", Map.of("value", Integer.parseInt(cantidadGas), "type", "Integer"));
                variables.put("proveedorPreferido", Map.of("value", proveedorPreferido, "type", "String"));
                variables.put("usuarioSolicitante", Map.of("value", usuarioSolicitante, "type", "String"));
                variables.put("justificacion", Map.of("value", justificacion, "type", "String"));
                variables.put("fechaRequerida", Map.of("value", fechaRequerida, "type", "String"));
                variables.put("necesidad_detectada", Map.of("value", "si", "type", "String"));

                Map<String, Object> body = new HashMap<>();
                body.put("businessKey", businessKey);
                body.put("variables", variables);

                try {
                        // Iniciar proceso de compra de gas
                        Map<String, Object> processInstance = camundaWebClient.post()
                                        .uri("/process-definition/key/Compra_gas/start")
                                        .bodyValue(body)
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                                        })
                                        .block();

                        log.info("Proceso de compra de gas iniciado: {}", businessKey);
                        return processInstance;
                } catch (WebClientResponseException e) {
                        log.error("Error al iniciar proceso de compra: {}", e.getMessage());
                        throw new RuntimeException("Error al iniciar proceso de compra: " + e.getMessage());
                }
        }

        /**
         * Obtiene una instancia de proceso por business key
         */
        public Map<String, Object> getProcessInstanceByBusinessKey(String businessKey) {
                try {
                        log.info("Buscando proceso con businessKey: {}", businessKey);

                        List<Map<String, Object>> instances = camundaWebClient.get()
                                        .uri(uriBuilder -> uriBuilder
                                                        .path("/process-instance")
                                                        .queryParam("businessKey", businessKey)
                                                        .build())
                                        .retrieve()
                                        .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {
                                        })
                                        .collectList()
                                        .block();

                        if (instances == null || instances.isEmpty()) {
                                log.warn("No se encontró proceso con businessKey: {}", businessKey);
                                throw new RuntimeException("Process instance not found");
                        }

                        log.info("Proceso encontrado: {}", instances.get(0).get("id"));
                        return instances.get(0);
                } catch (WebClientResponseException e) {
                        log.error("Error al buscar proceso: {}", e.getMessage());
                        throw new RuntimeException("Error al buscar proceso: " + e.getMessage());
                }
        }

        /**
         * Obtiene el historial de actividades de un proceso
         */
        public List<Map<String, Object>> getProcessHistory(String processInstanceId) {
                try {
                        log.info("Obteniendo historial para proceso: {}", processInstanceId);

                        List<Map<String, Object>> history = camundaWebClient.get()
                                        .uri("/history/activity-instance?processInstanceId=" + processInstanceId
                                                        + "&sortBy=startTime&sortOrder=asc")
                                        .retrieve()
                                        .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {
                                        })
                                        .collectList()
                                        .block();

                        log.info("Historial obtenido: {} actividades", history != null ? history.size() : 0);
                        return history != null ? history : new ArrayList<>();
                } catch (WebClientResponseException e) {
                        log.error("Error al obtener historial: {}", e.getMessage());
                        // No lanzar excepción, retornar lista vacía
                        return new ArrayList<>();
                } catch (Exception e) {
                        log.error("Error inesperado al obtener historial: {}", e.getMessage(), e);
                        return new ArrayList<>();
                }
        }
}
