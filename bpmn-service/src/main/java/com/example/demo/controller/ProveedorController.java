// bpmn-service/src/main/java/com/example/demo/controller/ProveedorController.java
package com.example.demo.controller;

import com.example.demo.model.Proveedor;
import com.example.demo.service.ProveedorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para Proveedor
 * Proporciona endpoints para acceder a datos de proveedores
 */
@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProveedorController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);
    
    @Autowired
    private ProveedorService proveedorService;
    
    /**
     * GET /api/proveedores
     * Obtiene todos los proveedores activos
     */
    @GetMapping
    public ResponseEntity<List<Proveedor>> getAllActiveProviders() {
        logger.info("GET /api/proveedores - Obteniendo proveedores activos");
        try {
            List<Proveedor> proveedores = proveedorService.getAllActiveProviders();
            logger.info("Se retornaron {} proveedores activos", proveedores.size());
            return ResponseEntity.ok(proveedores);
        } catch (Exception e) {
            logger.error("Error al obtener proveedores activos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/proveedores/todos
     * Obtiene todos los proveedores (incluyendo inactivos)
     */
    @GetMapping("/todos")
    public ResponseEntity<List<Proveedor>> getAllProviders() {
        logger.info("GET /api/proveedores/todos - Obteniendo todos los proveedores");
        try {
            List<Proveedor> proveedores = proveedorService.getAllProviders();
            logger.info("Se retornaron {} proveedores en total", proveedores.size());
            return ResponseEntity.ok(proveedores);
        } catch (Exception e) {
            logger.error("Error al obtener todos los proveedores", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/proveedores/ciudad/{ciudad}
     * Obtiene proveedores activos de una ciudad
     */
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<Proveedor>> getProvidersByCity(@PathVariable String ciudad) {
        logger.info("GET /api/proveedores/ciudad/{} - Obteniendo proveedores por ciudad", ciudad);
        try {
            List<Proveedor> proveedores = proveedorService.getActiveProvidersByCity(ciudad);
            logger.info("Se retornaron {} proveedores de la ciudad {}", proveedores.size(), ciudad);
            return ResponseEntity.ok(proveedores);
        } catch (Exception e) {
            logger.error("Error al obtener proveedores de la ciudad: {}", ciudad, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/proveedores/precio
     * Obtiene proveedores ordenados por mejor precio
     */
    @GetMapping("/precio")
    public ResponseEntity<List<Proveedor>> getProvidersByBestPrice() {
        logger.info("GET /api/proveedores/precio - Obteniendo proveedores ordenados por precio");
        try {
            List<Proveedor> proveedores = proveedorService.getProvidersByBestPrice();
            logger.info("Se retornaron {} proveedores ordenados por precio", proveedores.size());
            return ResponseEntity.ok(proveedores);
        } catch (Exception e) {
            logger.error("Error al obtener proveedores ordenados por precio", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/proveedores/{id}
     * Obtiene un proveedor por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getProviderById(@PathVariable Long id) {
        logger.info("GET /api/proveedores/{} - Obteniendo proveedor por ID", id);
        try {
            Optional<Proveedor> proveedor = proveedorService.getProviderById(id);
            if (proveedor.isPresent()) {
                logger.info("Proveedor encontrado: {}", proveedor.get().getNombre());
                return ResponseEntity.ok(proveedor.get());
            } else {
                logger.warn("Proveedor no encontrado: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error al obtener proveedor: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/proveedores/nombre/{nombre}
     * Obtiene un proveedor por nombre
     */
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Proveedor> getProviderByName(@PathVariable String nombre) {
        logger.info("GET /api/proveedores/nombre/{} - Obteniendo proveedor por nombre", nombre);
        try {
            Optional<Proveedor> proveedor = proveedorService.getProviderByName(nombre);
            if (proveedor.isPresent()) {
                logger.info("Proveedor encontrado: {}", nombre);
                return ResponseEntity.ok(proveedor.get());
            } else {
                logger.warn("Proveedor no encontrado: {}", nombre);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error al obtener proveedor: {}", nombre, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * POST /api/proveedores
     * Crea un nuevo proveedor
     */
    @PostMapping
    public ResponseEntity<?> createProvider(@RequestBody Proveedor proveedor) {
        logger.info("POST /api/proveedores - Creando nuevo proveedor: {}", proveedor.getNombre());
        try {
            Proveedor proveedorCreado = proveedorService.createProvider(proveedor);
            logger.info("Proveedor creado exitosamente: {} (ID: {})", proveedorCreado.getNombre(), proveedorCreado.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(proveedorCreado);
        } catch (IllegalArgumentException e) {
            logger.warn("Argumento inválido al crear proveedor: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al crear proveedor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error al crear proveedor"));
        }
    }
    
    /**
     * PUT /api/proveedores/{id}
     * Actualiza un proveedor existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvider(@PathVariable Long id, @RequestBody Proveedor proveedor) {
        logger.info("PUT /api/proveedores/{} - Actualizando proveedor", id);
        try {
            Proveedor proveedorActualizado = proveedorService.updateProvider(id, proveedor);
            logger.info("Proveedor actualizado exitosamente: {}", proveedorActualizado.getNombre());
            return ResponseEntity.ok(proveedorActualizado);
        } catch (IllegalArgumentException e) {
            logger.warn("Argumento inválido al actualizar proveedor: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al actualizar proveedor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error al actualizar proveedor"));
        }
    }
    
    /**
     * PATCH /api/proveedores/{id}/estado/{nuevoEstado}
     * Cambia el estado de un proveedor
     */
    @PatchMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<?> changeProviderStatus(@PathVariable Long id, @PathVariable String nuevoEstado) {
        logger.info("PATCH /api/proveedores/{}/estado/{} - Cambiando estado de proveedor", id, nuevoEstado);
        try {
            Proveedor proveedor = proveedorService.changeProviderStatus(id, nuevoEstado);
            logger.info("Estado de proveedor cambiado a: {}", nuevoEstado);
            return ResponseEntity.ok(proveedor);
        } catch (IllegalArgumentException e) {
            logger.warn("Argumento inválido al cambiar estado: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al cambiar estado de proveedor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error al cambiar estado"));
        }
    }
    
    /**
     * Clase auxiliar para respuestas de error
     */
    public static class ErrorResponse {
        public String mensaje;
        public long timestamp;
        
        public ErrorResponse(String mensaje) {
            this.mensaje = mensaje;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getMensaje() { return mensaje; }
        public long getTimestamp() { return timestamp; }
    }
}
