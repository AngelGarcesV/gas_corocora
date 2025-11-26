package com.gascorocora.transactional.controller;

import com.gascorocora.transactional.dto.FacturacionDTO;
import com.gascorocora.transactional.model.Facturacion;
import com.gascorocora.transactional.service.FacturacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturacion")
@RequiredArgsConstructor
@Slf4j
public class FacturacionController {

    private final FacturacionService facturacionService;

    @PostMapping("/pago")
    public ResponseEntity<Facturacion> registrarPago(@RequestBody FacturacionDTO facturacionDTO) {
        log.info("REST: Registrando pago para {}", facturacionDTO.getBusinessKey());
        Facturacion facturacion = facturacionService.registrarPago(facturacionDTO);
        return ResponseEntity.ok(facturacion);
    }

    @PostMapping("/activar")
    public ResponseEntity<Facturacion> activarServicio(@RequestParam String businessKey, 
                                                        @RequestParam String cedulaCliente) {
        log.info("REST: Activando servicio para {}", businessKey);
        Facturacion facturacion = facturacionService.activarServicio(businessKey, cedulaCliente);
        return ResponseEntity.ok(facturacion);
    }

    @GetMapping("/{businessKey}")
    public ResponseEntity<Facturacion> obtenerFacturacion(@PathVariable String businessKey) {
        try {
            Facturacion facturacion = facturacionService.obtenerPorBusinessKey(businessKey);
            return ResponseEntity.ok(facturacion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
