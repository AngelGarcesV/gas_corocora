package com.example.demo.repository;

import com.example.demo.model.HistorialProceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialProcesoRepository extends JpaRepository<HistorialProceso, Long> {
    
    List<HistorialProceso> findBySolicitudIdOrderByFechaEventoDesc(String solicitudId);
    
    List<HistorialProceso> findByProcesoInstanceId(String procesoInstanceId);
    
    List<HistorialProceso> findByTipoEvento(String tipoEvento);
}