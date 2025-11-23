package com.gascorocora.transactional.repository;

import com.gascorocora.transactional.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    
    Optional<Solicitud> findByBusinessKey(String businessKey);
    
    boolean existsByBusinessKey(String businessKey);
}
