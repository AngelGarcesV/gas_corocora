package com.example.demo.config;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResourceDeploymentConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDeploymentConfig.class);
    
    @Autowired
    private RepositoryService repositoryService;
    
    @EventListener(ApplicationReadyEvent.class)
    public void deployResources() {
        LOGGER.info("=== Iniciando deployment manual de recursos ===");
        
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            
            // Crear un deployment único con todos los recursos
            DeploymentBuilder builder = repositoryService.createDeployment()
                .name("Instalación Gas - Recursos Completos")
                .enableDuplicateFiltering(true);
            
            // Agregar archivos BPMN
            Resource[] bpmnResources = resolver.getResources("classpath*:**/*.bpmn");
            LOGGER.info("Encontrados {} archivos BPMN", bpmnResources.length);
            for (Resource resource : bpmnResources) {
                if (resource.exists() && resource.isReadable()) {
                    LOGGER.info("  ✅ Agregando BPMN: {}", resource.getFilename());
                    builder.addInputStream(resource.getFilename(), resource.getInputStream());
                }
            }
            
            // Agregar archivos DMN
            Resource[] dmnResources = resolver.getResources("classpath*:**/*.dmn");
            LOGGER.info("Encontrados {} archivos DMN", dmnResources.length);
            for (Resource resource : dmnResources) {
                if (resource.exists() && resource.isReadable()) {
                    LOGGER.info("  ✅ Agregando DMN: {}", resource.getFilename());
                    builder.addInputStream(resource.getFilename(), resource.getInputStream());
                }
            }
            
            // Agregar archivos FORM
            Resource[] formResources = resolver.getResources("classpath*:**/*.form");
            LOGGER.info("Encontrados {} archivos FORM", formResources.length);
            for (Resource resource : formResources) {
                if (resource.exists() && resource.isReadable()) {
                    LOGGER.info("  ✅ Agregando FORM: {}", resource.getFilename());
                    builder.addInputStream(resource.getFilename(), resource.getInputStream());
                }
            }
            
            // Ejecutar deployment
            Deployment deployment = builder.deploy();
            
            LOGGER.info("=== Deployment completado exitosamente ===");
            LOGGER.info("Deployment ID: {}", deployment.getId());
            LOGGER.info("Deployment Name: {}", deployment.getName());
            
            // Verificar recursos desplegados
            long processCount = repositoryService.createProcessDefinitionQuery().count();
            long decisionCount = repositoryService.createDecisionDefinitionQuery().count();
            
            LOGGER.info("📋 Total procesos BPMN desplegados: {}", processCount);
            LOGGER.info("📊 Total decisiones DMN desplegadas: {}", decisionCount);
            LOGGER.info("📝 Total formularios desplegados: {}", formResources.length);
            
        } catch (IOException e) {
            LOGGER.error("❌ Error durante el deployment de recursos: {}", e.getMessage(), e);
        }
    }
}