package com.example.demo.repository;

import com.example.demo.model.SolicitudInstalacion;
import com.example.demo.model.SolicitudInstalacion.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudInstalacionRepository extends JpaRepository<SolicitudInstalacion, Long> {
    
    Optional<SolicitudInstalacion> findBySolicitudId(String solicitudId);
    
    Optional<SolicitudInstalacion> findByProcesoInstanceId(String procesoInstanceId);
    
    List<SolicitudInstalacion> findByEstado(EstadoSolicitud estado);
    
    List<SolicitudInstalacion> findByEmail(String email);
    
    List<SolicitudInstalacion> findByEstadoIn(List<EstadoSolicitud> estados);
    
    long countByEstado(EstadoSolicitud estado);
}