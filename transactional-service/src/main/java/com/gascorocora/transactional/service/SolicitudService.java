package com.gascorocora.transactional.service;

import com.gascorocora.transactional.dto.SolicitudDTO;
import com.gascorocora.transactional.model.Solicitud;
import com.gascorocora.transactional.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;

    @Transactional
    public Solicitud crearSolicitud(SolicitudDTO dto) {
        log.info("Creando solicitud con businessKey: {}", dto.getBusinessKey());
        
        Solicitud solicitud = new Solicitud();
        solicitud.setBusinessKey(dto.getBusinessKey());
        solicitud.setNombreCliente(dto.getNombreCliente());
        solicitud.setCedulaCliente(dto.getCedulaCliente());
        solicitud.setDireccion(dto.getDireccion());
        solicitud.setTelefono(dto.getTelefono());
        solicitud.setEmail(dto.getEmail());
        solicitud.setEstrato(dto.getEstrato());
        
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public Solicitud actualizarCobertura(String businessKey, Boolean cobertura) {
        log.info("Actualizando cobertura para: {}", businessKey);
        
        Solicitud solicitud = solicitudRepository.findByBusinessKey(businessKey)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + businessKey));
        
        solicitud.setCobertura(cobertura);
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public Solicitud actualizarCotizacion(String businessKey, Double cotizacion) {
        log.info("Actualizando cotización para: {}", businessKey);
        
        Solicitud solicitud = solicitudRepository.findByBusinessKey(businessKey)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + businessKey));
        
        solicitud.setCotizacion(cotizacion);
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public Solicitud actualizarInspeccion(String businessKey, String resultado) {
        log.info("Actualizando inspección para: {}", businessKey);
        
        Solicitud solicitud = solicitudRepository.findByBusinessKey(businessKey)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + businessKey));
        
        solicitud.setEstadoInspeccion(resultado);
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public Solicitud actualizarInstalacion(String businessKey, String resultado) {
        log.info("Actualizando instalación para: {}", businessKey);
        
        Solicitud solicitud = solicitudRepository.findByBusinessKey(businessKey)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + businessKey));
        
        solicitud.setEstadoInstalacion(resultado);
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public Solicitud registrarPago(String businessKey, Boolean pagado) {
        log.info("Registrando pago para: {}", businessKey);
        
        Solicitud solicitud = solicitudRepository.findByBusinessKey(businessKey)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + businessKey));
        
        solicitud.setPagado(pagado);
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public Solicitud actualizarEstado(String businessKey, String estado) {
        log.info("Actualizando estado para: {} a {}", businessKey, estado);
        
        Solicitud solicitud = solicitudRepository.findByBusinessKey(businessKey)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + businessKey));
        
        solicitud.setEstado(estado);
        return solicitudRepository.save(solicitud);
    }

    @Transactional(readOnly = true)
    public Solicitud obtenerSolicitud(String businessKey) {
        return solicitudRepository.findByBusinessKey(businessKey)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + businessKey));
    }

    @Transactional(readOnly = true)
    public List<Solicitud> obtenerTodasSolicitudes() {
        return solicitudRepository.findAll();
    }
}
