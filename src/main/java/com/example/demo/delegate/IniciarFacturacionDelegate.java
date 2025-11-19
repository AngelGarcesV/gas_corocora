package com.example.demo.delegate;

import com.example.demo.model.Cliente;
import com.example.demo.model.InfoFact;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.InfoFactRepository;
import com.example.demo.service.EmailService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component("iniciarFacturacionDelegate")
public class IniciarFacturacionDelegate implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(IniciarFacturacionDelegate.class);
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private InfoFactRepository infoFactRepository;
    
    @Override
    @Transactional
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Iniciando facturación para proceso: {}", execution.getProcessInstanceId());
        
        // Obtener datos del proceso
        String clienteEmail = (String) execution.getVariable("cliente_email");
        String solicitudId = (String) execution.getVariable("solicitud_id");
        String numeroMedidor = (String) execution.getVariable("numero_medidor");
        String nombre = (String) execution.getVariable("nombre");
        String apellido = (String) execution.getVariable("apellido");
        String telefono = (String) execution.getVariable("telefono");
        String direccion = (String) execution.getVariable("direccion");
        String ciudad = (String) execution.getVariable("ciudad");
        String estrato = (String) execution.getVariable("estrato");
        
        // Datos de facturación
        Object montoConexionObj = execution.getVariable("conexion");
        Object descuentoObj = execution.getVariable("descuento");
        String tecnicoAsignado = (String) execution.getVariable("tecnico_asignado");
        
        if (clienteEmail == null || clienteEmail.isEmpty()) {
            clienteEmail = "cliente@ejemplo.com";
            LOGGER.warn("No se encontró email del cliente, usando email por defecto");
        }
        
        if (solicitudId == null) {
            solicitudId = execution.getProcessInstanceId();
        }
        
        LOGGER.info("Iniciando facturación para solicitud: {} | Medidor: {}", solicitudId, numeroMedidor);
        
        // Crear registro de Cliente
        Cliente cliente = new Cliente();
        cliente.setSolicitudId(solicitudId);
        cliente.setNombre(nombre != null ? nombre : "N/A");
        cliente.setApellido(apellido != null ? apellido : "N/A");
        cliente.setEmail(clienteEmail);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion != null ? direccion : "N/A");
        cliente.setCiudad(ciudad);
        cliente.setEstrato(estrato);
        cliente.setNumeroMedidor(numeroMedidor);
        cliente.setFechaActivacion(LocalDateTime.now());
        cliente.setEstado("ACTIVO");
        
        cliente = clienteRepository.save(cliente);
        LOGGER.info("Cliente registrado con ID: {}", cliente.getId());
        
        // Crear registro de InfoFact
        InfoFact infoFact = new InfoFact();
        infoFact.setCliente(cliente);
        
        if (montoConexionObj != null) {
            BigDecimal montoConexion = new BigDecimal(montoConexionObj.toString());
            infoFact.setMontoConexion(montoConexion);
            
            if (descuentoObj != null) {
                BigDecimal descuento = new BigDecimal(descuentoObj.toString());
                infoFact.setDescuentoAplicado(descuento);
                BigDecimal totalPagado = montoConexion.subtract(
                    montoConexion.multiply(descuento).divide(new BigDecimal("100"))
                );
                infoFact.setTotalPagado(totalPagado);
            } else {
                infoFact.setTotalPagado(montoConexion);
            }
        }
        
        infoFact.setTipoInstalacion("Residencial");
        infoFact.setFechaCotizacion(LocalDateTime.now());
        infoFact.setFechaInstalacion(LocalDateTime.now());
        infoFact.setTecnicoAsignado(tecnicoAsignado);
        infoFact.setObservaciones("Instalación completada exitosamente");
        
        infoFact = infoFactRepository.save(infoFact);
        LOGGER.info("InfoFact registrada con ID: {}", infoFact.getId());
        
        // Registrar inicio de facturación
        execution.setVariable("facturacion_iniciada", true);
        execution.setVariable("fecha_inicio_facturacion", new java.util.Date());
        execution.setVariable("ciclo_facturacion", determineCicloFacturacion());
        execution.setVariable("cliente_id", cliente.getId());
        execution.setVariable("info_fact_id", infoFact.getId());
        
        // Enviar correo de inicio de facturación
        emailService.notificarFacturacionIniciada(clienteEmail, solicitudId);
        
        LOGGER.info("✅ Proceso completado: Cliente y InfoFact registrados, facturación iniciada");
    }
    
    private String determineCicloFacturacion() {
        // Asignar ciclo de facturación basado en alguna lógica
        int ciclo = (int) (Math.random() * 4) + 1;
        return "CICLO-" + ciclo;
    }
}