package com.example.demo.delegate;

import com.example.demo.service.EmailService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component("activarServicioDelegate")
public class ActivarServicioDelegate implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivarServicioDelegate.class);
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Activando servicio de gas para proceso: {}", execution.getProcessInstanceId());
        
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
        
        // Generar número de medidor aleatorio
        String numeroMedidor = "MED-" + String.format("%08d", new Random().nextInt(100000000));
        
        LOGGER.info("Activando servicio para solicitud: {} | Medidor: {}", solicitudId, numeroMedidor);
        
        // Registrar activación en el proceso
        execution.setVariable("servicio_activado", true);
        execution.setVariable("numero_medidor", numeroMedidor);
        execution.setVariable("fecha_activacion", new java.util.Date());
        
        // Enviar correo de activación
        emailService.notificarActivacionServicio(clienteEmail, solicitudId, numeroMedidor);
        
        LOGGER.info("Servicio activado y notificación enviada exitosamente");
    }
}