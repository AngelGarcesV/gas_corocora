package com.example.demo.delegate;

import com.example.demo.service.EmailService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("notificarSolicitudInviableDelegate")
public class NotificarSolicitudInviableDelegate implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificarSolicitudInviableDelegate.class);
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Notificando solicitud inviable para proceso: {}", execution.getProcessInstanceId());
        
        // Obtener datos del proceso
        String clienteEmail = (String) execution.getVariable("cliente_email");
        String solicitudId = (String) execution.getVariable("solicitud_id");
        String clienteNombre = (String) execution.getVariable("cliente_nombre");
        
        // Validar email
        if (clienteEmail == null || clienteEmail.isEmpty()) {
            clienteEmail = "cliente@ejemplo.com"; // Email por defecto para pruebas
            LOGGER.warn("No se encontró email del cliente, usando email por defecto");
        }
        
        if (solicitudId == null) {
            solicitudId = execution.getProcessInstanceId();
        }
        
        LOGGER.info("Enviando notificación de solicitud inviable a: {} para solicitud: {}", 
            clienteEmail, solicitudId);
        
        // Enviar correo
        emailService.notificarSolicitudInviable(clienteEmail, solicitudId);
        
        // Registrar en el proceso
        execution.setVariable("notificacion_enviada", true);
        execution.setVariable("fecha_notificacion", new java.util.Date());
        execution.setVariable("tipo_notificacion", "SOLICITUD_INVIABLE");
        
        LOGGER.info("Notificación de solicitud inviable enviada exitosamente");
    }
}