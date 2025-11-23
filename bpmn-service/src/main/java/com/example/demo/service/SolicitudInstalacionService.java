package com.example.demo.service;

import com.example.demo.model.HistorialProceso;
import com.example.demo.model.SolicitudInstalacion;
import com.example.demo.model.SolicitudInstalacion.EstadoSolicitud;
import com.example.demo.repository.HistorialProcesoRepository;
import com.example.demo.repository.SolicitudInstalacionRepository;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SolicitudInstalacionService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudInstalacionService.class);
    
    @Autowired
    private SolicitudInstalacionRepository solicitudRepository;
    
    @Autowired
    private HistorialProcesoRepository historialRepository;
    
    @Autowired
    private RuntimeService runtimeService;
    
    /**
     * Crear una nueva solicitud de instalación e iniciar el proceso BPMN
     */
    public SolicitudInstalacion crearSolicitud(String nombre, String email, String telefono, 
                                               String direccion, String estrato, String tipoInstalacion) {
        
        LOGGER.info("Creando nueva solicitud de instalación para: {}", nombre);
        
        // Generar ID único
        String solicitudId = "SOL-" + System.currentTimeMillis();
        
        // Crear entidad
        SolicitudInstalacion solicitud = new SolicitudInstalacion(solicitudId, nombre, email, telefono, direccion);
        solicitud.setEstrato(estrato);
        solicitud.setTipoInstalacion(tipoInstalacion);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        
        // Guardar en BD
        solicitud = solicitudRepository.save(solicitud);
        
        // Registrar en historial
        registrarHistorial(solicitudId, null, "INICIO", 
            "Solicitud creada por " + nombre, "SISTEMA");
        
        // Preparar variables del proceso
        Map<String, Object> variables = new HashMap<>();
        variables.put("solicitud_id", solicitudId);
        variables.put("cliente_nombre", nombre);
        variables.put("cliente_email", email);
        variables.put("cliente_telefono", telefono);
        variables.put("cliente_direccion", direccion);
        variables.put("estrato", estrato);
        variables.put("tipo_instalacion", tipoInstalacion);
        variables.put("fecha_solicitud", solicitud.getFechaSolicitud());
        
        // Iniciar proceso Camunda
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "Process_19yx4pt", 
                solicitudId, 
                variables
            );
            
            solicitud.setProcesoInstanceId(processInstance.getId());
            solicitud.setEstado(EstadoSolicitud.EN_REVISION_COBERTURA);
            solicitud = solicitudRepository.save(solicitud);
            
            registrarHistorial(solicitudId, processInstance.getId(), "PROCESO_INICIADO",
                "Proceso BPMN iniciado con ID: " + processInstance.getId(), "SISTEMA");
            
            LOGGER.info("Proceso iniciado exitosamente. Solicitud ID: {}, Proceso ID: {}", 
                solicitudId, processInstance.getId());
            
        } catch (Exception e) {
            LOGGER.error("Error al iniciar proceso BPMN: {}", e.getMessage(), e);
            registrarHistorial(solicitudId, null, "ERROR",
                "Error al iniciar proceso: " + e.getMessage(), "SISTEMA");
            throw new RuntimeException("Error al iniciar proceso de instalación", e);
        }
        
        return solicitud;
    }
    
    /**
     * Actualizar estado de la solicitud
     */
    public SolicitudInstalacion actualizarEstado(String solicitudId, EstadoSolicitud nuevoEstado) {
        Optional<SolicitudInstalacion> optional = solicitudRepository.findBySolicitudId(solicitudId);
        
        if (optional.isPresent()) {
            SolicitudInstalacion solicitud = optional.get();
            EstadoSolicitud estadoAnterior = solicitud.getEstado();
            solicitud.setEstado(nuevoEstado);
            solicitud = solicitudRepository.save(solicitud);
            
            registrarHistorial(solicitudId, solicitud.getProcesoInstanceId(), "CAMBIO_ESTADO",
                "Estado cambiado de " + estadoAnterior + " a " + nuevoEstado, "SISTEMA");
            
            return solicitud;
        }
        
        throw new RuntimeException("Solicitud no encontrada: " + solicitudId);
    }
    
    /**
     * Actualizar información de cotización
     */
    public SolicitudInstalacion actualizarCotizacion(String solicitudId, Double montoConexion, 
                                                     Double descuento, Double montoPagar) {
        Optional<SolicitudInstalacion> optional = solicitudRepository.findBySolicitudId(solicitudId);
        
        if (optional.isPresent()) {
            SolicitudInstalacion solicitud = optional.get();
            solicitud.setMontoConexion(montoConexion);
            solicitud.setDescuento(descuento);
            solicitud.setMontoPagar(montoPagar);
            solicitud.setEstado(EstadoSolicitud.EN_COTIZACION);
            solicitud = solicitudRepository.save(solicitud);
            
            registrarHistorial(solicitudId, solicitud.getProcesoInstanceId(), "COTIZACION_GENERADA",
                String.format("Cotización: $%.2f, Descuento: %.1f%%, Total: $%.2f", 
                    montoConexion, descuento, montoPagar), "SISTEMA");
            
            return solicitud;
        }
        
        throw new RuntimeException("Solicitud no encontrada: " + solicitudId);
    }
    
    /**
     * Actualizar número de medidor
     */
    public SolicitudInstalacion actualizarMedidor(String solicitudId, String numeroMedidor) {
        Optional<SolicitudInstalacion> optional = solicitudRepository.findBySolicitudId(solicitudId);
        
        if (optional.isPresent()) {
            SolicitudInstalacion solicitud = optional.get();
            solicitud.setNumeroMedidor(numeroMedidor);
            solicitud.setEstado(EstadoSolicitud.COMPLETADA);
            solicitud = solicitudRepository.save(solicitud);
            
            registrarHistorial(solicitudId, solicitud.getProcesoInstanceId(), "MEDIDOR_ASIGNADO",
                "Medidor asignado: " + numeroMedidor, "SISTEMA");
            
            return solicitud;
        }
        
        throw new RuntimeException("Solicitud no encontrada: " + solicitudId);
    }
    
    /**
     * Registrar evento en historial
     */
    public void registrarHistorial(String solicitudId, String procesoInstanceId, 
                                   String tipoEvento, String descripcion, String usuario) {
        HistorialProceso historial = new HistorialProceso();
        historial.setSolicitudId(solicitudId);
        historial.setProcesoInstanceId(procesoInstanceId);
        historial.setTipoEvento(tipoEvento);
        historial.setActividad(tipoEvento);
        historial.setDescripcion(descripcion);
        historial.setUsuarioResponsable(usuario);
        
        historialRepository.save(historial);
        LOGGER.debug("Historial registrado: {} - {}", solicitudId, tipoEvento);
    }
    
    /**
     * Obtener solicitud por ID
     */
    public Optional<SolicitudInstalacion> obtenerPorSolicitudId(String solicitudId) {
        return solicitudRepository.findBySolicitudId(solicitudId);
    }
    
    /**
     * Obtener solicitud por ID de proceso
     */
    public Optional<SolicitudInstalacion> obtenerPorProcesoId(String procesoInstanceId) {
        return solicitudRepository.findByProcesoInstanceId(procesoInstanceId);
    }
    
    /**
     * Obtener todas las solicitudes
     */
    public List<SolicitudInstalacion> obtenerTodas() {
        return solicitudRepository.findAll();
    }
    
    /**
     * Obtener solicitudes por estado
     */
    public List<SolicitudInstalacion> obtenerPorEstado(EstadoSolicitud estado) {
        return solicitudRepository.findByEstado(estado);
    }
    
    /**
     * Obtener historial de una solicitud
     */
    public List<HistorialProceso> obtenerHistorial(String solicitudId) {
        return historialRepository.findBySolicitudIdOrderByFechaEventoDesc(solicitudId);
    }
    
    /**
     * Obtener estadísticas
     */
    public Map<String, Long> obtenerEstadisticas() {
        Map<String, Long> stats = new HashMap<>();
        
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            stats.put(estado.name(), solicitudRepository.countByEstado(estado));
        }
        
        stats.put("TOTAL", solicitudRepository.count());
        
        return stats;
    }
}