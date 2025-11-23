package com.example.demo.delegate;

import com.example.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Delegate Java Class implementation (camunda:class)
 * Activa el servicio en la base de datos y notifica al cliente
 */
@Component("activarServicioDelegate")
public class ActivarServicioDelegate implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivarServicioDelegate.class);
    
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    @Value("${transactional.service.url:http://localhost:8083}")
    private String transactionalServiceUrl;

    public ActivarServicioDelegate(EmailService emailService, RestTemplate restTemplate) {
        this.emailService = emailService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("=== Activando Servicio de Gas ===");
        LOGGER.info("Proceso: {}", execution.getProcessInstanceId());
        
        String businessKey = execution.getBusinessKey();
        String cedula = (String) execution.getVariable("cedula");
        String email = (String) execution.getVariable("email");
        
        try {
            // Llamar al transactional-service para activar el servicio
            String url = transactionalServiceUrl + "/api/facturacion/activar" +
                        "?businessKey=" + businessKey +
                        "&cedulaCliente=" + cedula;
            
            LOGGER.info("Activando servicio en: {}", url);
            
            Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);
            
            if (response != null) {
                String numeroServicio = (String) response.get("numeroServicio");
                
                LOGGER.info("Servicio activado: {}", numeroServicio);
                
                // Registrar activación en el proceso
                execution.setVariable("servicio_activado", true);
                execution.setVariable("numero_servicio", numeroServicio);
                execution.setVariable("fecha_activacion", new java.util.Date());
                
                // Enviar correo de activación
                emailService.notificarActivacionServicio(email, businessKey, numeroServicio);
            }
            
        } catch (Exception e) {
            LOGGER.error("Error al activar servicio: {}", e.getMessage(), e);
            execution.setVariable("servicio_activado", false);
            throw new RuntimeException("Error al activar servicio", e);
        }
        
        LOGGER.info("Servicio activado y notificación enviada exitosamente");
    }
}