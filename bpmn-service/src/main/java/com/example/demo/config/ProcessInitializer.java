package com.example.demo.config;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProcessInitializer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInitializer.class);
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @EventListener(ApplicationReadyEvent.class)
    @Order(2) // Ejecutar después del ResourceDeploymentConfig
    public void verifyAndInitialize() {
        LOGGER.info("=== Verificando recursos desplegados ===");
        
        // Listar todos los procesos BPMN desplegados
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
        
        if (processDefinitions.isEmpty()) {
            LOGGER.warn("❌ No se encontraron procesos BPMN desplegados");
        } else {
            LOGGER.info("✅ Procesos BPMN desplegados ({}): ", processDefinitions.size());
            for (ProcessDefinition processDefinition : processDefinitions) {
                LOGGER.info("   📋 ID: {} | Key: {} | Name: {} | Version: {}", 
                    processDefinition.getId(),
                    processDefinition.getKey(), 
                    processDefinition.getName(),
                    processDefinition.getVersion());
            }
        }
        
        // Listar todas las decisiones DMN desplegadas
        long decisionCount = repositoryService.createDecisionDefinitionQuery().count();
        LOGGER.info("✅ Decisiones DMN desplegadas: {}", decisionCount);
        if (decisionCount > 0) {
            repositoryService.createDecisionDefinitionQuery().list().forEach(decision -> {
                LOGGER.info("   📊 ID: {} | Key: {} | Name: {}", 
                    decision.getId(), 
                    decision.getKey(), 
                    decision.getName());
            });
        }
        
        // Listar todos los deployments
        long deploymentCount = repositoryService.createDeploymentQuery().count();
        LOGGER.info("✅ Total de deployments: {}", deploymentCount);
        
        repositoryService.createDeploymentQuery().list().forEach(deployment -> {
            LOGGER.info("   📦 Deployment: {} | Nombre: {} | Fecha: {}", 
                deployment.getId(), 
                deployment.getName(), 
                deployment.getDeploymentTime());
            
            // Listar recursos en cada deployment
            repositoryService.getDeploymentResources(deployment.getId()).forEach(resource -> {
                LOGGER.info("      📄 Recurso: {}", resource.getName());
            });
        });
        
        
        LOGGER.info("=== Inicialización completada ===");
        LOGGER.info("🌐 Accede a Camunda Cockpit: http://localhost:8080/camunda/app/cockpit");
        LOGGER.info("📋 Accede a Camunda Tasklist: http://localhost:8080/camunda/app/tasklist");
        LOGGER.info("🔑 Credenciales: demo/demo");
    }
}