package com.gascorocora.transactional.service;

import com.gascorocora.transactional.dto.FacturacionDTO;
import com.gascorocora.transactional.model.Facturacion;
import com.gascorocora.transactional.repository.FacturacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturacionService {

    private final FacturacionRepository facturacionRepository;

    @Transactional
    public Facturacion registrarPago(FacturacionDTO facturacionDTO) {
        log.info("Registrando pago para businessKey: {}", facturacionDTO.getBusinessKey());
        
        Facturacion facturacion = facturacionRepository.findByBusinessKey(facturacionDTO.getBusinessKey())
                .orElse(new Facturacion());
        
        facturacion.setBusinessKey(facturacionDTO.getBusinessKey());
        facturacion.setCedulaCliente(facturacionDTO.getCedulaCliente());
        facturacion.setMontoRecibido(facturacionDTO.getMontoRecibido());
        facturacion.setMetodoPago(facturacionDTO.getMetodoPago());
        facturacion.setNumeroTransaccion(facturacionDTO.getNumeroTransaccion());
        facturacion.setFechaPago(facturacionDTO.getFechaPago());
        facturacion.setComprobanteUrl(facturacionDTO.getComprobanteUrl());
        facturacion.setObservacionesPago(facturacionDTO.getObservacionesPago());
        facturacion.setEstado("PAGADO");
        
        Facturacion saved = facturacionRepository.save(facturacion);
        log.info("Pago registrado con ID: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Facturacion activarServicio(String businessKey, String cedulaCliente) {
        log.info("Activando servicio para businessKey: {}", businessKey);
        
        Facturacion facturacion = facturacionRepository.findByBusinessKey(businessKey)
                .orElse(new Facturacion());
        
        facturacion.setBusinessKey(businessKey);
        facturacion.setCedulaCliente(cedulaCliente);
        facturacion.setNumeroServicio(generarNumeroServicio());
        facturacion.setFechaActivacion(LocalDateTime.now());
        facturacion.setEstado("ACTIVADO");
        
        Facturacion saved = facturacionRepository.save(facturacion);
        log.info("Servicio activado con número: {}", saved.getNumeroServicio());
        return saved;
    }

    private String generarNumeroServicio() {
        Random random = new Random();
        return String.format("GC-%06d-%04d", 
            random.nextInt(999999), 
            random.nextInt(9999));
    }

    public Facturacion obtenerPorBusinessKey(String businessKey) {
        return facturacionRepository.findByBusinessKey(businessKey)
                .orElseThrow(() -> new RuntimeException("Facturación no encontrada"));
    }
}
