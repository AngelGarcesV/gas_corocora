package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("approvalDelegate")
public class ApprovalDelegate implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ApprovalDelegate.class);
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Procesando aprobación para instancia: {}", execution.getProcessInstanceId());
        
        // Obtener variables del proceso
        String requestTitle = (String) execution.getVariable("requestTitle");
        String requestDescription = (String) execution.getVariable("requestDescription");
        
        LOGGER.info("Aprobando solicitud: '{}' - {}", requestTitle, requestDescription);
        
        // Simular procesamiento
        Thread.sleep(2000);
        
        // Establecer variables de resultado
        execution.setVariable("processResult", "APROBADO");
        execution.setVariable("processedBy", "Sistema Automático");
        execution.setVariable("processDate", new java.util.Date());
        execution.setVariable("comments", "Solicitud procesada y aprobada exitosamente");
        
        LOGGER.info("Aprobación completada para: {}", requestTitle);
    }
}