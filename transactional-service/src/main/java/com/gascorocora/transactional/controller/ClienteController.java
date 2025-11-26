package com.gascorocora.transactional.controller;

import com.gascorocora.transactional.dto.ClienteDTO;
import com.gascorocora.transactional.model.Cliente;
import com.gascorocora.transactional.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> registrarCliente(@RequestBody ClienteDTO clienteDTO) {
        log.info("REST: Registrando cliente {}", clienteDTO.getCedula());
        Cliente cliente = clienteService.registrarCliente(clienteDTO);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/{cedula}")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable String cedula) {
        return clienteService.obtenerPorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
