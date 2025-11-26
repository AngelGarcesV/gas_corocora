package com.example.demo.repository;

import com.example.demo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findBySolicitudId(String solicitudId);
    
    Optional<Cliente> findByEmail(String email);
    
    boolean existsBySolicitudId(String solicitudId);
    
    boolean existsByEmail(String email);
}
