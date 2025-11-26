// bpmn-service/src/main/java/com/example/demo/repository/AuditoriaCompraRepository.java
package com.example.demo.repository;

import com.example.demo.model.AuditoriaCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Date;

/**
 * Repositorio para la entidad AuditoriaCompra
 * Proporciona acceso a datos de auditoría de órdenes de compra
 */
@Repository
public interface AuditoriaCompraRepository extends JpaRepository<AuditoriaCompra, Long> {
    
    /**
     * Busca todos los registros de auditoría para una orden específica
     */
    List<AuditoriaCompra> findByOrdenIdOrderByFechaAccionDesc(String ordenId);
    
    /**
     * Busca registros de auditoría por acción
     */
    List<AuditoriaCompra> findByAccionOrderByFechaAccionDesc(String accion);
    
    /**
     * Busca registros de auditoría por usuario
     */
    List<AuditoriaCompra> findByUsuarioOrderByFechaAccionDesc(String usuario);
    
    /**
     * Busca registros de auditoría para una orden por acción
     */
    List<AuditoriaCompra> findByOrdenIdAndAccionOrderByFechaAccionDesc(String ordenId, String accion);
    
    /**
     * Busca registros de auditoría dentro de un rango de fechas
     */
    List<AuditoriaCompra> findByFechaAccionBetweenOrderByFechaAccionDesc(Date fechaInicio, Date fechaFin);
    
    /**
     * Busca registros de auditoría para una orden dentro de un rango de fechas
     */
    List<AuditoriaCompra> findByOrdenIdAndFechaAccionBetweenOrderByFechaAccionDesc(String ordenId, Date fechaInicio, Date fechaFin);
    
    /**
     * Cuenta el número de registros de auditoría para una orden
     */
    long countByOrdenId(String ordenId);
}
