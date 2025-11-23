package com.example.demo.delegate;

import com.example.demo.service.EmailService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("enviarCotizacionDelegate")
public class EnviarCotizacionDelegate implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EnviarCotizacionDelegate.class);
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Enviando cotización para proceso: {}", execution.getProcessInstanceId());
        
        // Obtener datos del proceso
        String clienteEmail = (String) execution.getVariable("cliente_email");
        String solicitudId = (String) execution.getVariable("solicitud_id");
        
        // Obtener datos de cotización
        Double conexion = (Double) execution.getVariable("conexion");
        Double descuento = (Double) execution.getVariable("descuento");
        Double conexionAPagar = (Double) execution.getVariable("conexion_a_pagar");
        
        // Valores por defecto si no existen
        if (clienteEmail == null || clienteEmail.isEmpty()) {
            clienteEmail = "cliente@ejemplo.com";
            LOGGER.warn("No se encontró email del cliente, usando email por defecto");
        }
        
        if (solicitudId == null) {
            solicitudId = execution.getProcessInstanceId();
        }
        
        if (conexion == null) {
            conexion = 500000.0; // Valor por defecto
        }
        
        if (descuento == null) {
            descuento = 0.0;
        }
        
        LOGGER.info("Enviando cotización a: {} | Conexión: {} | Descuento: {}% | A pagar: {}", 
            clienteEmail, conexion, descuento, conexionAPagar);
        
        // Enviar correo
        emailService.enviarCotizacion(clienteEmail, solicitudId, conexion, descuento);
        
        // Registrar en el proceso
        execution.setVariable("cotizacion_enviada", true);
        execution.setVariable("fecha_envio_cotizacion", new java.util.Date());
        
        LOGGER.info("Cotización enviada exitosamente");
    }
}