package com.gascorocora.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessService {

    private final WebClient camundaWebClient;

    public Map<String, Object> getProcessInstanceByBusinessKey(String businessKey) {
        List<Map<String, Object>> instances = camundaWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/process-instance")
                        .queryParam("businessKey", businessKey)
                        .build())
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .block();

        if (instances == null || instances.isEmpty()) {
            throw new RuntimeException("Process instance not found");
        }

        return instances.get(0);
    }

    public List<Map<String, Object>> getProcessHistory(String processInstanceId) {
        return camundaWebClient.get()
                .uri("/history/activity-instance?processInstanceId=" + processInstanceId + "&sortBy=startTime&sortOrder=asc")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .block();
    }

    public Map<String, Object> startNewRequest(String nombre, String cedula, String direccion, 
                                               String telefono, String email, String estrato) {
        String businessKey = "SOL-" + System.currentTimeMillis();
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("nombreCliente", Map.of("value", nombre, "type", "String"));
        variables.put("cedulaCliente", Map.of("value", cedula, "type", "String"));
        variables.put("direccion", Map.of("value", direccion, "type", "String"));
        variables.put("telefono", Map.of("value", telefono, "type", "String"));
        variables.put("email", Map.of("value", email, "type", "String"));
        variables.put("estrato", Map.of("value", Integer.parseInt(estrato), "type", "Integer"));
        
        // Add additional variables for radicar solicitud task
        variables.put("nombre", Map.of("value", nombre, "type", "String"));
        variables.put("apellido", Map.of("value", "", "type", "String")); // Empty for now
        variables.put("cliente_email", Map.of("value", email, "type", "String"));
        variables.put("ciudad", Map.of("value", "Villavicencio", "type", "String")); // Default city
        
        Map<String, Object> body = new HashMap<>();
        body.put("businessKey", businessKey);
        body.put("variables", variables);

        // Start process instance
        Map<String, Object> processInstance = camundaWebClient.post()
                .uri("/process-definition/key/Process_19yx4pt/start")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
        
        if (processInstance != null && processInstance.containsKey("id")) {
            String processInstanceId = (String) processInstance.get("id");
            
            // Get the first task (should be "Radicar solicitud en el sistema")
            try {
                List<Map<String, Object>> tasks = camundaWebClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/task")
                                .queryParam("processInstanceId", processInstanceId)
                                .build())
                        .retrieve()
                        .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .collectList()
                        .block();
                
                if (tasks != null && !tasks.isEmpty()) {
                    String taskId = (String) tasks.get(0).get("id");
                    String taskName = (String) tasks.get(0).get("name");
                    
                    // Only auto-complete if it's the "Radicar solicitud" task
                    if (taskName != null && taskName.contains("Radicar")) {
                        // Complete the task with the form data
                        Map<String, Object> taskVariables = new HashMap<>();
                        taskVariables.put("nombre", Map.of("value", nombre, "type", "String"));
                        taskVariables.put("apellido", Map.of("value", "", "type", "String"));
                        taskVariables.put("cliente_email", Map.of("value", email, "type", "String"));
                        taskVariables.put("telefono", Map.of("value", telefono, "type", "String"));
                        taskVariables.put("direccion", Map.of("value", direccion, "type", "String"));
                        taskVariables.put("ciudad", Map.of("value", "Villavicencio", "type", "String"));
                        taskVariables.put("estrato", Map.of("value", Integer.parseInt(estrato), "type", "Integer"));
                        taskVariables.put("comments", Map.of("value", "Radicación automática desde portal de cliente", "type", "String"));
                        
                        camundaWebClient.post()
                                .uri("/task/" + taskId + "/complete")
                                .bodyValue(Map.of("variables", taskVariables))
                                .retrieve()
                                .bodyToMono(Void.class)
                                .block();
                        
                        log.info("Tarea de radicar solicitud completada automáticamente para: {}", businessKey);
                    }
                }
            } catch (Exception e) {
                log.warn("No se pudo completar automáticamente la tarea de radicar solicitud: {}", e.getMessage());
            }
        }

        return processInstance;
    }
}
