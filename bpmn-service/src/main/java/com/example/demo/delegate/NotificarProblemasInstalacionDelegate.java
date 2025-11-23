package com.example.demo.delegate;

import com.example.demo.service.EmailService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("notificarProblemasInstalacionDelegate")
public class NotificarProblemasInstalacionDelegate implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificarProblemasInstalacionDelegate.class);
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Notificando problemas en instalación para proceso: {}", execution.getProcessInstanceId());
        
        // Obtener datos del proceso
        String clienteEmail = (String) execution.getVariable("cliente_email");
        String solicitudId = (String) execution.getVariable("solicitud_id");
        String problemasDetectados = (String) execution.getVariable("problemas_instalacion");
        
        if (clienteEmail == null || clienteEmail.isEmpty()) {
            clienteEmail = "cliente@ejemplo.com";
            LOGGER.warn("No se encontró email del cliente, usando email por defecto");
        }
        
        if (solicitudId == null) {
            solicitudId = execution.getProcessInstanceId();
        }
        
        if (problemasDetectados == null || problemasDetectados.isEmpty()) {
            problemasDetectados = "Se detectaron problemas técnicos que requieren corrección";
        }
        
        LOGGER.info("Enviando notificación de problemas de instalación a: {} para solicitud: {}", 
            clienteEmail, solicitudId);
        
        // Enviar correo
        emailService.notificarProblemasInstalacion(clienteEmail, solicitudId, problemasDetectados);
        
        // Registrar en el proceso
        execution.setVariable("notificacion_problemas_enviada", true);
        execution.setVariable("fecha_notificacion_problemas", new java.util.Date());
        
        LOGGER.info("Notificación de problemas enviada exitosamente");
    }
}