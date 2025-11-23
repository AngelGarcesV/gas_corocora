package com.example.demo.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableProcessApplication
public class CamundaConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaConfig.class);
    
    @Autowired
    private ProcessEngine processEngine;
    
    @Autowired
    private ResourcePatternResolver resourceResolver;
    
    @PostConstruct
    public void deployProcesses() {
        try {
            LOGGER.info("=== Desplegando procesos BPMN manualmente ===");
            
            RepositoryService repositoryService = processEngine.getRepositoryService();
            
            // Buscar archivos BPMN
            Resource[] resources = resourceResolver.getResources("classpath*:**/*.bpmn");
            
            LOGGER.info("Archivos BPMN encontrados: {}", resources.length);
            
            for (Resource resource : resources) {
                LOGGER.info("Desplegando: {}", resource.getFilename());
                
                repositoryService.createDeployment()
                    .addInputStream(resource.getFilename(), resource.getInputStream())
                    .deploy();
                    
                LOGGER.info("✅ Proceso desplegado: {}", resource.getFilename());
            }
            
        } catch (Exception e) {
            LOGGER.error("❌ Error desplegando procesos: {}", e.getMessage(), e);
        }
    }
}