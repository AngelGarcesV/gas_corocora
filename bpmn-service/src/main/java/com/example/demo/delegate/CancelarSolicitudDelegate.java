package com.example.demo.delegate;

import com.example.demo.service.EmailService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("cancelarSolicitudDelegate")
public class CancelarSolicitudDelegate implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CancelarSolicitudDelegate.class);
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Cancelando solicitud por timeout para proceso: {}", execution.getProcessInstanceId());
        
        // Obtener datos del proceso
        String clienteEmail = (String) execution.getVariable("cliente_email");
        String solicitudId = (String) execution.getVariable("solicitud_id");
        
        if (clienteEmail == null || clienteEmail.isEmpty()) {
            clienteEmail = "cliente@ejemplo.com";
            LOGGER.warn("No se encontró email del cliente, usando email por defecto");
        }
        
        if (solicitudId == null) {
            solicitudId = execution.getProcessInstanceId();
        }
        
        LOGGER.info("Enviando notificación de cancelación a: {} para solicitud: {}", 
            clienteEmail, solicitudId);
        
        // Enviar correo
        emailService.notificarCancelacionSolicitud(clienteEmail, solicitudId);
        
        // Registrar en el proceso
        execution.setVariable("solicitud_cancelada", true);
        execution.setVariable("motivo_cancelacion", "TIMEOUT - Sin respuesta del cliente");
        execution.setVariable("fecha_cancelacion", new java.util.Date());
        
        LOGGER.info("Notificación de cancelación enviada exitosamente");
    }
}