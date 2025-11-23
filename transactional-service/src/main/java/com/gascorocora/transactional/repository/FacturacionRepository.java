package com.gascorocora.transactional.repository;

import com.gascorocora.transactional.model.Facturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacturacionRepository extends JpaRepository<Facturacion, Long> {
    Optional<Facturacion> findByBusinessKey(String businessKey);
    Optional<Facturacion> findByNumeroServicio(String numeroServicio);
}
