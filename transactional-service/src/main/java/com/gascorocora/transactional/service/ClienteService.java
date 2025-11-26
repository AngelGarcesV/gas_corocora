package com.gascorocora.transactional.service;

import com.gascorocora.transactional.dto.ClienteDTO;
import com.gascorocora.transactional.model.Cliente;
import com.gascorocora.transactional.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente registrarCliente(ClienteDTO clienteDTO) {
        log.info("Registrando cliente con cédula: {}", clienteDTO.getCedula());
        
        // Verificar si ya existe
        Optional<Cliente> existente = clienteRepository.findByCedula(clienteDTO.getCedula());
        if (existente.isPresent()) {
            log.info("Cliente ya existe, actualizando datos");
            Cliente cliente = existente.get();
            actualizarDatosCliente(cliente, clienteDTO);
            return clienteRepository.save(cliente);
        }
        
        // Crear nuevo cliente
        Cliente cliente = new Cliente();
        cliente.setCedula(clienteDTO.getCedula());
        actualizarDatosCliente(cliente, clienteDTO);
        
        Cliente saved = clienteRepository.save(cliente);
        log.info("Cliente registrado con ID: {}", saved.getId());
        return saved;
    }

    private void actualizarDatosCliente(Cliente cliente, ClienteDTO dto) {
        cliente.setNombre(dto.getNombre());
        cliente.setDireccion(dto.getDireccion());
        cliente.setCiudad(dto.getCiudad());
        cliente.setEstrato(dto.getEstrato());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
    }

    public Optional<Cliente> obtenerPorCedula(String cedula) {
        return clienteRepository.findByCedula(cedula);
    }

    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
}
