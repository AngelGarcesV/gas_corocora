// bpmn-service/src/main/java/com/example/demo/controller/AuditoriaCompraController.java
package com.example.demo.controller;

import com.example.demo.model.AuditoriaCompra;
import com.example.demo.service.AuditoriaCompraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para Auditoría de Compra
 * Proporciona endpoints para consultar historial de auditoría
 */
@RestController
@RequestMapping("/api/auditoria-compra")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuditoriaCompraController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditoriaCompraController.class);
    
    @Autowired
    private AuditoriaCompraService auditoriaService;
    
    /**
     * GET /api/auditoria-compra/orden/{ordenId}
     * Obtiene el historial completo de una orden
     */
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<Map<String, Object>> getHistorialOrden(@PathVariable String ordenId) {
        logger.info("GET /api/auditoria-compra/orden/{} - Obteniendo historial", ordenId);
        try {
            List<AuditoriaCompra> historial = auditoriaService.getHistorialOrden(ordenId);
            long totalAcciones = auditoriaService.contarAccionesOrden(ordenId);
            
            return ResponseEntity.ok(Map.of(
                "ordenId", ordenId,
                "totalAcciones", totalAcciones,
                "historial", historial,
                "timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
            ));
        } catch (Exception e) {
            logger.error("Error al obtener historial de orden: {}", ordenId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener historial"));
        }
    }
    
    /**
     * GET /api/auditoria-compra/accion/{accion}
     * Obtiene registros por acción
     */
    @GetMapping("/accion/{accion}")
    public ResponseEntity<?> getRegistrosPorAccion(@PathVariable String accion) {
        logger.info("GET /api/auditoria-compra/accion/{} - Obteniendo registros", accion);
        try {
            List<AuditoriaCompra> registros = auditoriaService.getRegistrosPorAccion(accion);
            return ResponseEntity.ok(Map.of(
                "accion", accion,
                "cantidad", registros.size(),
                "registros", registros
            ));
        } catch (Exception e) {
            logger.error("Error al obtener registros por acción: {}", accion, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener registros"));
        }
    }
    
    /**
     * GET /api/auditoria-compra/usuario/{usuario}
     * Obtiene registros por usuario
     */
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<?> getRegistrosPorUsuario(@PathVariable String usuario) {
        logger.info("GET /api/auditoria-compra/usuario/{} - Obteniendo registros", usuario);
        try {
            List<AuditoriaCompra> registros = auditoriaService.getRegistrosPorUsuario(usuario);
            return ResponseEntity.ok(Map.of(
                "usuario", usuario,
                "cantidad", registros.size(),
                "registros", registros
            ));
        } catch (Exception e) {
            logger.error("Error al obtener registros por usuario: {}", usuario, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener registros"));
        }
    }
    
    /**
     * GET /api/auditoria-compra/resumen/{ordenId}
     * Obtiene resumen de auditoría de una orden
     */
    @GetMapping("/resumen/{ordenId}")
    public ResponseEntity<?> getResumenOrden(@PathVariable String ordenId) {
        logger.info("GET /api/auditoria-compra/resumen/{} - Obteniendo resumen", ordenId);
        try {
            List<AuditoriaCompra> historial = auditoriaService.getHistorialOrden(ordenId);
            long totalAcciones = auditoriaService.contarAccionesOrden(ordenId);
            
            // Contar por tipo de acción
            long creadas = historial.stream().filter(a -> a.getAccion().equals("CREADA")).count();
            long enviadas = historial.stream().filter(a -> a.getAccion().equals("ENVIADA")).count();
            long recibidas = historial.stream().filter(a -> a.getAccion().equals("RECIBIDA")).count();
            long aceptadas = historial.stream().filter(a -> a.getAccion().equals("ACEPTADA")).count();
            long discrepancias = historial.stream().filter(a -> a.getAccion().equals("DISCREPANCIA_NOTIFICADA")).count();
            
            return ResponseEntity.ok(Map.of(
                "ordenId", ordenId,
                "totalAcciones", totalAcciones,
                "estadisticas", Map.of(
                    "creadas", creadas,
                    "enviadas", enviadas,
                    "recibidas", recibidas,
                    "aceptadas", aceptadas,
                    "discrepancias", discrepancias
                ),
                "ultimaAccion", historial.isEmpty() ? null : historial.get(0),
                "primeraAccion", historial.isEmpty() ? null : historial.get(historial.size() - 1)
            ));
        } catch (Exception e) {
            logger.error("Error al obtener resumen de orden: {}", ordenId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener resumen"));
        }
    }
    
    /**
     * GET /api/auditoria-compra/salud
     * Endpoint de salud para verificar que el servicio está funcionando
     */
    @GetMapping("/salud")
    public ResponseEntity<?> salud() {
        logger.info("GET /api/auditoria-compra/salud - Verificando servicio");
        return ResponseEntity.ok(Map.of(
            "estado", "ACTIVO",
            "servicio", "Auditoría de Compra",
            "timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
        ));
    }
}
