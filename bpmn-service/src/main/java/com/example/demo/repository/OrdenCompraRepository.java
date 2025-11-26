// bpmn-service/src/main/java/com/example/demo/repository/OrdenCompraRepository.java
package com.example.demo.repository;

import com.example.demo.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    OrdenCompra findByOrdenId(String ordenId);
    
    List<OrdenCompra> findByEstado(String estado);
    
    List<OrdenCompra> findByUsuarioSolicitante(String usuarioSolicitante);
}