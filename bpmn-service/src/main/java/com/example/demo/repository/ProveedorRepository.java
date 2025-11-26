// bpmn-service/src/main/java/com/example/demo/repository/ProveedorRepository.java
package com.example.demo.repository;

import com.example.demo.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Proveedor
 * Proporciona acceso a datos de proveedores de gas
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
    /**
     * Busca un proveedor por su nombre
     */
    Optional<Proveedor> findByNombre(String nombre);
    
    /**
     * Busca todos los proveedores activos
     */
    List<Proveedor> findByEstado(String estado);
    
    /**
     * Busca proveedores por ciudad
     */
    List<Proveedor> findByCiudad(String ciudad);
    
    /**
     * Busca proveedores ordenados por precio (más económicos primero)
     */
    List<Proveedor> findByEstadoOrderByPrecioPorKgAsc(String estado);
    
    /**
     * Busca proveedores activos por ciudad, ordenados por precio
     */
    List<Proveedor> findByEstadoAndCiudadOrderByPrecioPorKgAsc(String estado, String ciudad);
}
