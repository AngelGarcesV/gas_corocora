package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("rejectionDelegate")
public class RejectionDelegate implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RejectionDelegate.class);
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Procesando rechazo para instancia: {}", execution.getProcessInstanceId());
        
        // Obtener variables del proceso
        String requestTitle = (String) execution.getVariable("requestTitle");
        String requestDescription = (String) execution.getVariable("requestDescription");
        
        LOGGER.info("Rechazando solicitud: '{}' - {}", requestTitle, requestDescription);
        
        // Simular procesamiento
        Thread.sleep(1000);
        
        // Establecer variables de resultado
        execution.setVariable("processResult", "RECHAZADO");
        execution.setVariable("processedBy", "Sistema Automático");
        execution.setVariable("processDate", new java.util.Date());
        execution.setVariable("rejectionReason", "La solicitud no cumple con los criterios establecidos");
        execution.setVariable("comments", "Solicitud rechazada automáticamente");
        
        LOGGER.info("Rechazo completado para: {}", requestTitle);
    }
}