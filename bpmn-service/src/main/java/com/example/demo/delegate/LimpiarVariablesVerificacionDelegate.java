package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("limpiarVariablesVerificacionDelegate")
public class LimpiarVariablesVerificacionDelegate implements JavaDelegate {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LimpiarVariablesVerificacionDelegate.class);
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Limpiando variables de verificación para proceso: {}", execution.getProcessInstanceId());
        
        // Limpiar variables relacionadas con la verificación anterior
        execution.removeVariable("instalacion_correcta");
        execution.removeVariable("problemas_instalacion");
        execution.removeVariable("observaciones_tecnico");
        
        // Incrementar contador de reintentos
        Integer intentos = (Integer) execution.getVariable("intentos_verificacion");
        if (intentos == null) {
            intentos = 0;
        }
        intentos++;
        execution.setVariable("intentos_verificacion", intentos);
        execution.setVariable("fecha_ultimo_reintento", new java.util.Date());
        
        LOGGER.info("Variables limpiadas. Intento de verificación #: {}", intentos);
    }
}