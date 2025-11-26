package com.example.demo.repository;

import com.example.demo.model.InfoFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InfoFactRepository extends JpaRepository<InfoFact, Long> {
    
    Optional<InfoFact> findByClienteId(Long clienteId);
    
    List<InfoFact> findByClienteIdOrderByFechaRegistroDesc(Long clienteId);
}
