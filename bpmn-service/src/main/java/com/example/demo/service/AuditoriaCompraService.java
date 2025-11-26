// bpmn-service/src/main/java/com/example/demo/service/AuditoriaCompraService.java
package com.example.demo.service;

import com.example.demo.model.AuditoriaCompra;
import com.example.demo.repository.AuditoriaCompraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * Servicio de Auditoría de Compra
 * Gestiona la trazabilidad completa de órdenes de compra
 */
@Service
public class AuditoriaCompraService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditoriaCompraService.class);
    
    @Autowired
    private AuditoriaCompraRepository auditoriaRepository;
    
    /**
     * Registra una acción en la auditoría de compra
     */
    public AuditoriaCompra registrarAccion(String ordenId, String accion, String usuario, String descripcion) {
        return registrarAccion(ordenId, accion, usuario, descripcion, null, null, null);
    }
    
    /**
     * Registra una acción con cambio de estado
     */
    public AuditoriaCompra registrarAccion(String ordenId, String accion, String usuario, String descripcion, 
                                          String estadoAnterior, String estadoNuevo) {
        return registrarAccion(ordenId, accion, usuario, descripcion, estadoAnterior, estadoNuevo, null);
    }
    
    /**
     * Registra una acción con todos los detalles
     */
    public AuditoriaCompra registrarAccion(String ordenId, String accion, String usuario, String descripcion,
                                          String estadoAnterior, String estadoNuevo, String detalles) {
        logger.info("Registrando acción de auditoría - Orden: {}, Acción: {}, Usuario: {}", ordenId, accion, usuario);
        
        try {
            // Validar parámetros requeridos
            if (ordenId == null || ordenId.trim().isEmpty()) {
                logger.error("Error: ordenId es obligatorio");
                throw new IllegalArgumentException("El ID de la orden es obligatorio");
            }
            
            if (accion == null || accion.trim().isEmpty()) {
                logger.error("Error: acción es obligatoria");
                throw new IllegalArgumentException("La acción es obligatoria");
            }
            
            if (usuario == null || usuario.trim().isEmpty()) {
                logger.error("Error: usuario es obligatorio");
                throw new IllegalArgumentException("El usuario es obligatorio");
            }
            
            // Crear registro de auditoría
            AuditoriaCompra auditoria = new AuditoriaCompra();
            auditoria.setOrdenId(ordenId);
            auditoria.setAccion(accion);
            auditoria.setUsuario(usuario);
            auditoria.setDescripcion(descripcion != null ? descripcion : "");
            auditoria.setEstadoAnterior(estadoAnterior);
            auditoria.setEstadoNuevo(estadoNuevo);
            auditoria.setDetalles(detalles);
            auditoria.setFechaAccion(new Date());
            
            // Guardar en base de datos
            AuditoriaCompra auditoriaGuardada = auditoriaRepository.save(auditoria);
            
            logger.info("Acción registrada exitosamente - Auditoría ID: {}, Orden: {}, Acción: {}", 
                       auditoriaGuardada.getId(), ordenId, accion);
            
            return auditoriaGuardada;
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación al registrar acción: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error al registrar acción de auditoría para orden: {}", ordenId, e);
            throw new RuntimeException("Error al registrar auditoría", e);
        }
    }
    
    /**
     * Obtiene el historial completo de una orden
     */
    public List<AuditoriaCompra> getHistorialOrden(String ordenId) {
        logger.debug("Obteniendo historial de auditoría para orden: {}", ordenId);
        
        if (ordenId == null || ordenId.trim().isEmpty()) {
            logger.warn("Error: ordenId es obligatorio");
            throw new IllegalArgumentException("El ID de la orden es obligatorio");
        }
        
        try {
            List<AuditoriaCompra> historial = auditoriaRepository.findByOrdenIdOrderByFechaAccionDesc(ordenId);
            logger.info("Se obtuvieron {} registros de auditoría para la orden: {}", historial.size(), ordenId);
            return historial;
        } catch (Exception e) {
            logger.error("Error al obtener historial de auditoría para orden: {}", ordenId, e);
            throw new RuntimeException("Error al obtener historial de auditoría", e);
        }
    }
    
    /**
     * Obtiene los registros de auditoría por acción
     */
    public List<AuditoriaCompra> getRegistrosPorAccion(String accion) {
        logger.debug("Obteniendo registros de auditoría por acción: {}", accion);
        
        if (accion == null || accion.trim().isEmpty()) {
            logger.warn("Error: acción es obligatoria");
            throw new IllegalArgumentException("La acción es obligatoria");
        }
        
        try {
            List<AuditoriaCompra> registros = auditoriaRepository.findByAccionOrderByFechaAccionDesc(accion);
            logger.info("Se obtuvieron {} registros de auditoría para la acción: {}", registros.size(), accion);
            return registros;
        } catch (Exception e) {
            logger.error("Error al obtener registros por acción: {}", accion, e);
            throw new RuntimeException("Error al obtener registros de auditoría", e);
        }
    }
    
    /**
     * Obtiene los registros de auditoría por usuario
     */
    public List<AuditoriaCompra> getRegistrosPorUsuario(String usuario) {
        logger.debug("Obteniendo registros de auditoría por usuario: {}", usuario);
        
        if (usuario == null || usuario.trim().isEmpty()) {
            logger.warn("Error: usuario es obligatorio");
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        
        try {
            List<AuditoriaCompra> registros = auditoriaRepository.findByUsuarioOrderByFechaAccionDesc(usuario);
            logger.info("Se obtuvieron {} registros de auditoría para el usuario: {}", registros.size(), usuario);
            return registros;
        } catch (Exception e) {
            logger.error("Error al obtener registros por usuario: {}", usuario, e);
            throw new RuntimeException("Error al obtener registros de auditoría", e);
        }
    }
    
    /**
     * Obtiene el número de acciones para una orden
     */
    public long contarAccionesOrden(String ordenId) {
        logger.debug("Contando acciones para la orden: {}", ordenId);
        
        if (ordenId == null || ordenId.trim().isEmpty()) {
            logger.warn("Error: ordenId es obligatorio");
            throw new IllegalArgumentException("El ID de la orden es obligatorio");
        }
        
        try {
            long cantidad = auditoriaRepository.countByOrdenId(ordenId);
            logger.info("La orden {} tiene {} acciones registradas", ordenId, cantidad);
            return cantidad;
        } catch (Exception e) {
            logger.error("Error al contar acciones para orden: {}", ordenId, e);
            throw new RuntimeException("Error al contar acciones de auditoría", e);
        }
    }
    
    /**
     * Obtiene registros de auditoría dentro de un rango de fechas
     */
    public List<AuditoriaCompra> getRegistrosPorFecha(Date fechaInicio, Date fechaFin) {
        logger.debug("Obteniendo registros de auditoría entre {} y {}", fechaInicio, fechaFin);
        
        if (fechaInicio == null || fechaFin == null) {
            logger.warn("Error: las fechas son obligatorias");
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        }
        
        try {
            List<AuditoriaCompra> registros = auditoriaRepository.findByFechaAccionBetweenOrderByFechaAccionDesc(fechaInicio, fechaFin);
            logger.info("Se obtuvieron {} registros de auditoría en el rango de fechas especificado", registros.size());
            return registros;
        } catch (Exception e) {
            logger.error("Error al obtener registros por fecha", e);
            throw new RuntimeException("Error al obtener registros de auditoría", e);
        }
    }
    
    /**
     * Obtiene registros de auditoría para una orden dentro de un rango de fechas
     */
    public List<AuditoriaCompra> getHistorialOrdenPorFecha(String ordenId, Date fechaInicio, Date fechaFin) {
        logger.debug("Obteniendo historial de orden {} entre {} y {}", ordenId, fechaInicio, fechaFin);
        
        if (ordenId == null || ordenId.trim().isEmpty()) {
            logger.warn("Error: ordenId es obligatorio");
            throw new IllegalArgumentException("El ID de la orden es obligatorio");
        }
        
        if (fechaInicio == null || fechaFin == null) {
            logger.warn("Error: las fechas son obligatorias");
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        }
        
        try {
            List<AuditoriaCompra> registros = auditoriaRepository.findByOrdenIdAndFechaAccionBetweenOrderByFechaAccionDesc(
                    ordenId, fechaInicio, fechaFin);
            logger.info("Se obtuvieron {} registros para la orden {} en el rango especificado", registros.size(), ordenId);
            return registros;
        } catch (Exception e) {
            logger.error("Error al obtener historial de orden por fecha", e);
            throw new RuntimeException("Error al obtener historial de auditoría", e);
        }
    }
    
    /**
     * Obtiene acciones específicas para una orden
     */
    public List<AuditoriaCompra> getAccionesOrdenPorTipo(String ordenId, String accion) {
        logger.debug("Obteniendo acciones {} para la orden: {}", accion, ordenId);
        
        if (ordenId == null || ordenId.trim().isEmpty()) {
            logger.warn("Error: ordenId es obligatorio");
            throw new IllegalArgumentException("El ID de la orden es obligatorio");
        }
        
        if (accion == null || accion.trim().isEmpty()) {
            logger.warn("Error: acción es obligatoria");
            throw new IllegalArgumentException("La acción es obligatoria");
        }
        
        try {
            List<AuditoriaCompra> registros = auditoriaRepository.findByOrdenIdAndAccionOrderByFechaAccionDesc(ordenId, accion);
            logger.info("Se obtuvieron {} registros de acción {} para la orden {}", registros.size(), accion, ordenId);
            return registros;
        } catch (Exception e) {
            logger.error("Error al obtener acciones de orden por tipo", e);
            throw new RuntimeException("Error al obtener registros de auditoría", e);
        }
    }
}
