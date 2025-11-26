package com.gascorocora.transactional.controller;

import com.gascorocora.transactional.dto.SolicitudDTO;
import com.gascorocora.transactional.model.Solicitud;
import com.gascorocora.transactional.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @PostMapping
    public ResponseEntity<Solicitud> crearSolicitud(@RequestBody SolicitudDTO dto) {
        return ResponseEntity.ok(solicitudService.crearSolicitud(dto));
    }

    @GetMapping("/{businessKey}")
    public ResponseEntity<Solicitud> obtenerSolicitud(@PathVariable String businessKey) {
        return ResponseEntity.ok(solicitudService.obtenerSolicitud(businessKey));
    }

    @GetMapping
    public ResponseEntity<List<Solicitud>> obtenerTodasSolicitudes() {
        return ResponseEntity.ok(solicitudService.obtenerTodasSolicitudes());
    }

    @PutMapping("/{businessKey}/cobertura")
    public ResponseEntity<Solicitud> actualizarCobertura(
            @PathVariable String businessKey,
            @RequestBody Map<String, Boolean> request) {
        return ResponseEntity.ok(solicitudService.actualizarCobertura(businessKey, request.get("cobertura")));
    }

    @PutMapping("/{businessKey}/cotizacion")
    public ResponseEntity<Solicitud> actualizarCotizacion(
            @PathVariable String businessKey,
            @RequestBody Map<String, Double> request) {
        return ResponseEntity.ok(solicitudService.actualizarCotizacion(businessKey, request.get("cotizacion")));
    }

    @PutMapping("/{businessKey}/inspeccion")
    public ResponseEntity<Solicitud> actualizarInspeccion(
            @PathVariable String businessKey,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(solicitudService.actualizarInspeccion(businessKey, request.get("resultado")));
    }

    @PutMapping("/{businessKey}/instalacion")
    public ResponseEntity<Solicitud> actualizarInstalacion(
            @PathVariable String businessKey,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(solicitudService.actualizarInstalacion(businessKey, request.get("resultado")));
    }

    @PutMapping("/{businessKey}/pago")
    public ResponseEntity<Solicitud> registrarPago(
            @PathVariable String businessKey,
            @RequestBody Map<String, Boolean> request) {
        return ResponseEntity.ok(solicitudService.registrarPago(businessKey, request.get("pagado")));
    }

    @PutMapping("/{businessKey}/estado")
    public ResponseEntity<Solicitud> actualizarEstado(
            @PathVariable String businessKey,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(solicitudService.actualizarEstado(businessKey, request.get("estado")));
    }
}
