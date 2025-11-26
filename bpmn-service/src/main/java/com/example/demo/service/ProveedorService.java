// bpmn-service/src/main/java/com/example/demo/service/ProveedorService.java
package com.example.demo.service;

import com.example.demo.model.Proveedor;
import com.example.demo.repository.ProveedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Proveedor
 * Gestiona la lógica de negocio para proveedores de gas
 */
@Service
public class ProveedorService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);
    
    @Autowired
    private ProveedorRepository proveedorRepository;
    
    /**
     * Obtiene todos los proveedores activos
     */
    public List<Proveedor> getAllActiveProviders() {
        logger.debug("Obteniendo todos los proveedores activos");
        try {
            List<Proveedor> proveedores = proveedorRepository.findByEstado("ACTIVO");
            logger.info("Se obtuvieron {} proveedores activos", proveedores.size());
            return proveedores;
        } catch (Exception e) {
            logger.error("Error al obtener proveedores activos", e);
            throw new RuntimeException("Error al obtener proveedores activos", e);
        }
    }
    
    /**
     * Obtiene todos los proveedores activos de una ciudad
     */
    public List<Proveedor> getActiveProvidersByCity(String ciudad) {
        logger.debug("Obteniendo proveedores activos de la ciudad: {}", ciudad);
        try {
            List<Proveedor> proveedores = proveedorRepository.findByEstadoAndCiudadOrderByPrecioPorKgAsc("ACTIVO", ciudad);
            logger.info("Se obtuvieron {} proveedores activos en {}", proveedores.size(), ciudad);
            return proveedores;
        } catch (Exception e) {
            logger.error("Error al obtener proveedores de la ciudad: {}", ciudad, e);
            throw new RuntimeException("Error al obtener proveedores por ciudad", e);
        }
    }
    
    /**
     * Obtiene proveedores ordenados por mejor precio
     */
    public List<Proveedor> getProvidersByBestPrice() {
        logger.debug("Obteniendo proveedores ordenados por mejor precio");
        try {
            List<Proveedor> proveedores = proveedorRepository.findByEstadoOrderByPrecioPorKgAsc("ACTIVO");
            logger.info("Se obtuvieron {} proveedores ordenados por precio", proveedores.size());
            return proveedores;
        } catch (Exception e) {
            logger.error("Error al obtener proveedores por precio", e);
            throw new RuntimeException("Error al obtener proveedores por precio", e);
        }
    }
    
    /**
     * Obtiene un proveedor por ID
     */
    public Optional<Proveedor> getProviderById(Long id) {
        logger.debug("Obteniendo proveedor con ID: {}", id);
        if (id == null || id <= 0) {
            logger.warn("ID de proveedor inválido: {}", id);
            return Optional.empty();
        }
        try {
            Optional<Proveedor> proveedor = proveedorRepository.findById(id);
            if (proveedor.isPresent()) {
                logger.info("Proveedor encontrado: {}", proveedor.get().getNombre());
            } else {
                logger.warn("Proveedor no encontrado con ID: {}", id);
            }
            return proveedor;
        } catch (Exception e) {
            logger.error("Error al obtener proveedor con ID: {}", id, e);
            throw new RuntimeException("Error al obtener proveedor", e);
        }
    }
    
    /**
     * Obtiene un proveedor por nombre
     */
    public Optional<Proveedor> getProviderByName(String nombre) {
        logger.debug("Obteniendo proveedor con nombre: {}", nombre);
        if (nombre == null || nombre.trim().isEmpty()) {
            logger.warn("Nombre de proveedor inválido");
            return Optional.empty();
        }
        try {
            Optional<Proveedor> proveedor = proveedorRepository.findByNombre(nombre);
            if (proveedor.isPresent()) {
                logger.info("Proveedor encontrado: {}", nombre);
            } else {
                logger.warn("Proveedor no encontrado: {}", nombre);
            }
            return proveedor;
        } catch (Exception e) {
            logger.error("Error al obtener proveedor: {}", nombre, e);
            throw new RuntimeException("Error al obtener proveedor por nombre", e);
        }
    }
    
    /**
     * Crea un nuevo proveedor
     */
    public Proveedor createProvider(Proveedor proveedor) {
        logger.info("Creando nuevo proveedor: {}", proveedor.getNombre());
        
        if (proveedor == null || proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            logger.error("Datos de proveedor inválidos");
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
        }
        
        if (proveedor.getPrecioPorKg() == null || proveedor.getPrecioPorKg() <= 0) {
            logger.error("Precio inválido para proveedor: {}", proveedor.getNombre());
            throw new IllegalArgumentException("El precio por kg debe ser mayor a 0");
        }
        
        try {
            // Verificar que el nombre sea único
            Optional<Proveedor> existente = proveedorRepository.findByNombre(proveedor.getNombre());
            if (existente.isPresent()) {
                logger.warn("Ya existe un proveedor con el nombre: {}", proveedor.getNombre());
                throw new IllegalArgumentException("Ya existe un proveedor con este nombre");
            }
            
            Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
            logger.info("Proveedor creado exitosamente: {} (ID: {})", proveedorGuardado.getNombre(), proveedorGuardado.getId());
            return proveedorGuardado;
        } catch (Exception e) {
            logger.error("Error al crear proveedor: {}", proveedor.getNombre(), e);
            throw new RuntimeException("Error al crear proveedor", e);
        }
    }
    
    /**
     * Actualiza un proveedor existente
     */
    public Proveedor updateProvider(Long id, Proveedor proveedorActualizado) {
        logger.info("Actualizando proveedor con ID: {}", id);
        
        if (id == null || id <= 0) {
            logger.error("ID de proveedor inválido: {}", id);
            throw new IllegalArgumentException("ID de proveedor inválido");
        }
        
        if (proveedorActualizado == null || proveedorActualizado.getNombre() == null) {
            logger.error("Datos de actualización inválidos");
            throw new IllegalArgumentException("Datos de actualización inválidos");
        }
        
        try {
            Optional<Proveedor> proveedorExistente = proveedorRepository.findById(id);
            if (!proveedorExistente.isPresent()) {
                logger.warn("Proveedor no encontrado para actualizar: {}", id);
                throw new IllegalArgumentException("Proveedor no encontrado");
            }
            
            Proveedor proveedor = proveedorExistente.get();
            
            // Actualizar campos
            if (proveedorActualizado.getNombre() != null && !proveedorActualizado.getNombre().isEmpty()) {
                proveedor.setNombre(proveedorActualizado.getNombre());
            }
            if (proveedorActualizado.getPrecioPorKg() != null && proveedorActualizado.getPrecioPorKg() > 0) {
                proveedor.setPrecioPorKg(proveedorActualizado.getPrecioPorKg());
            }
            if (proveedorActualizado.getTiempoEntregaDias() != null) {
                proveedor.setTiempoEntregaDias(proveedorActualizado.getTiempoEntregaDias());
            }
            if (proveedorActualizado.getVigenciaMeses() != null) {
                proveedor.setVigenciaMeses(proveedorActualizado.getVigenciaMeses());
            }
            if (proveedorActualizado.getEmail() != null && !proveedorActualizado.getEmail().isEmpty()) {
                proveedor.setEmail(proveedorActualizado.getEmail());
            }
            if (proveedorActualizado.getTelefonoContacto() != null && !proveedorActualizado.getTelefonoContacto().isEmpty()) {
                proveedor.setTelefonoContacto(proveedorActualizado.getTelefonoContacto());
            }
            if (proveedorActualizado.getEstado() != null && !proveedorActualizado.getEstado().isEmpty()) {
                proveedor.setEstado(proveedorActualizado.getEstado());
            }
            if (proveedorActualizado.getDescripcion() != null && !proveedorActualizado.getDescripcion().isEmpty()) {
                proveedor.setDescripcion(proveedorActualizado.getDescripcion());
            }
            if (proveedorActualizado.getCiudad() != null && !proveedorActualizado.getCiudad().isEmpty()) {
                proveedor.setCiudad(proveedorActualizado.getCiudad());
            }
            
            Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
            logger.info("Proveedor actualizado exitosamente: {} (ID: {})", proveedorGuardado.getNombre(), id);
            return proveedorGuardado;
        } catch (Exception e) {
            logger.error("Error al actualizar proveedor: {}", id, e);
            throw new RuntimeException("Error al actualizar proveedor", e);
        }
    }
    
    /**
     * Cambia el estado de un proveedor
     */
    public Proveedor changeProviderStatus(Long id, String nuevoEstado) {
        logger.info("Cambiando estado de proveedor ID: {} a {}", id, nuevoEstado);
        
        if (id == null || id <= 0) {
            logger.error("ID de proveedor inválido: {}", id);
            throw new IllegalArgumentException("ID de proveedor inválido");
        }
        
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            logger.error("Estado inválido");
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        
        // Validar que sea uno de los estados permitidos
        if (!nuevoEstado.matches("ACTIVO|INACTIVO|SUSPENDIDO")) {
            logger.error("Estado no permitido: {}", nuevoEstado);
            throw new IllegalArgumentException("Estado no válido. Use: ACTIVO, INACTIVO, SUSPENDIDO");
        }
        
        try {
            Optional<Proveedor> proveedorOpt = proveedorRepository.findById(id);
            if (!proveedorOpt.isPresent()) {
                logger.warn("Proveedor no encontrado: {}", id);
                throw new IllegalArgumentException("Proveedor no encontrado");
            }
            
            Proveedor proveedor = proveedorOpt.get();
            String estadoAnterior = proveedor.getEstado();
            proveedor.setEstado(nuevoEstado);
            proveedorRepository.save(proveedor);
            
            logger.info("Estado de proveedor {} cambiado de {} a {}", proveedor.getNombre(), estadoAnterior, nuevoEstado);
            return proveedor;
        } catch (Exception e) {
            logger.error("Error al cambiar estado de proveedor: {}", id, e);
            throw new RuntimeException("Error al cambiar estado de proveedor", e);
        }
    }
    
    /**
     * Obtiene todos los proveedores (sin filtro de estado)
     */
    public List<Proveedor> getAllProviders() {
        logger.debug("Obteniendo todos los proveedores");
        try {
            List<Proveedor> proveedores = proveedorRepository.findAll();
            logger.info("Se obtuvieron {} proveedores en total", proveedores.size());
            return proveedores;
        } catch (Exception e) {
            logger.error("Error al obtener todos los proveedores", e);
            throw new RuntimeException("Error al obtener proveedores", e);
        }
    }
}
