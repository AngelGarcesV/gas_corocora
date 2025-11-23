package com.example.demo.controller;

import com.example.demo.model.HistorialProceso;
import com.example.demo.model.SolicitudInstalacion;
import com.example.demo.service.SolicitudInstalacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProcessController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessController.class);
    
    @Autowired
    private SolicitudInstalacionService solicitudService;
    
    /**
     * Crear nueva solicitud e iniciar proceso BPMN
     */
    @PostMapping("/solicitudes")
    public ResponseEntity<Map<String, Object>> crearSolicitud(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam String direccion,
            @RequestParam(required = false, defaultValue = "3") String estrato,
            @RequestParam(required = false, defaultValue = "Residencial") String tipoInstalacion) {
        
        try {
            LOGGER.info("Creando solicitud para: {} - {}", nombre, email);
            
            SolicitudInstalacion solicitud = solicitudService.crearSolicitud(
                nombre, email, telefono, direccion, estrato, tipoInstalacion
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("solicitudId", solicitud.getSolicitudId());
            response.put("procesoInstanceId", solicitud.getProcesoInstanceId());
            response.put("estado", solicitud.getEstado());
            response.put("mensaje", "Solicitud creada exitosamente. Proceso BPMN iniciado.");
            response.put("data", solicitud);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            LOGGER.error("Error creando solicitud: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Obtener solicitud por ID
     */
    @GetMapping("/solicitudes/{solicitudId}")
    public ResponseEntity<Map<String, Object>> obtenerSolicitud(@PathVariable String solicitudId) {
        Optional<SolicitudInstalacion> optional = solicitudService.obtenerPorSolicitudId(solicitudId);
        
        if (optional.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", optional.get());
            
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", "Solicitud no encontrada");
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Obtener todas las solicitudes
     */
    @GetMapping("/solicitudes")
    public ResponseEntity<Map<String, Object>> obtenerTodasLasSolicitudes() {
        List<SolicitudInstalacion> solicitudes = solicitudService.obtenerTodas();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", solicitudes.size());
        response.put("data", solicitudes);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtener historial de una solicitud
     */
    @GetMapping("/solicitudes/{solicitudId}/historial")
    public ResponseEntity<Map<String, Object>> obtenerHistorial(@PathVariable String solicitudId) {
        List<HistorialProceso> historial = solicitudService.obtenerHistorial(solicitudId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("solicitudId", solicitudId);
        response.put("total", historial.size());
        response.put("data", historial);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtener estadísticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Long> stats = solicitudService.obtenerEstadisticas();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Sistema de Instalación de Gas - Gas Corocora");
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }
}