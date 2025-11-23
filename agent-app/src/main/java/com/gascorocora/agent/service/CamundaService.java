package com.gascorocora.agent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CamundaService {

    private final WebClient camundaWebClient;

    public List<Map<String, Object>> getAssignedTasks() {
        return camundaWebClient.get()
                .uri("/task?sortBy=created&sortOrder=desc")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .block();
    }

    public Map<String, Object> getTask(String taskId) {
        return camundaWebClient.get()
                .uri("/task/" + taskId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public Map<String, Object> getTaskVariables(String taskId) {
        return camundaWebClient.get()
                .uri("/task/" + taskId + "/variables")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public Map<String, Object> getTaskForm(String taskId) {
        try {
            // Get the form key from task
            Map<String, Object> formInfo = camundaWebClient.get()
                    .uri("/task/" + taskId + "/form")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            
            // If there's a formKey, try to get the deployed form
            if (formInfo != null && formInfo.containsKey("key")) {
                String formKey = (String) formInfo.get("key");
                try {
                    String deployedForm = camundaWebClient.get()
                            .uri("/task/" + taskId + "/deployed-form")
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
                    
                    // Parse the JSON form
                    if (deployedForm != null) {
                        return new com.fasterxml.jackson.databind.ObjectMapper()
                                .readValue(deployedForm, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                    }
                } catch (Exception e) {
                    log.warn("Could not get deployed form for task: {}", taskId, e);
                }
            }
            
            return formInfo != null ? formInfo : Map.of();
        } catch (Exception e) {
            log.warn("No form found for task: {}", taskId);
            return Map.of();
        }
    }

    public void completeTask(String taskId, Map<String, Object> variables, Map<String, String> fieldTypes) {
        Map<String, Object> convertedVars = convertToVariableMap(variables, fieldTypes);
        log.info("=== Camunda API Call ===");
        log.info("Converted variables: {}", convertedVars);
        
        Map<String, Object> body = Map.of("variables", convertedVars);
        log.info("Request body: {}", body);
        
        try {
            camundaWebClient.post()
                    .uri("/task/" + taskId + "/complete")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response -> {
                        return response.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("Camunda API Error Response: {}", errorBody);
                            return reactor.core.publisher.Mono.error(
                                new RuntimeException("Camunda API Error: " + errorBody)
                            );
                        });
                    })
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            log.error("Error completing task", e);
            throw e;
        }
    }

    public void claimTask(String taskId) {
        camundaWebClient.post()
                .uri("/task/" + taskId + "/claim")
                .bodyValue(Map.of("userId", "demo"))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public long getTaskCount() {
        return camundaWebClient.get()
                .uri("/task/count")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(result -> ((Number) result.get("count")).longValue())
                .block();
    }

    public List<Map<String, Object>> getProcessDefinitions() {
        return camundaWebClient.get()
                .uri("/process-definition?latestVersion=true")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .block();
    }

    public Map<String, Object> getStartForm(String processKey) {
        try {
            return camundaWebClient.get()
                    .uri("/process-definition/key/" + processKey + "/startForm")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception e) {
            log.warn("No start form found for process: {}", processKey);
            return Map.of();
        }
    }

    public Map<String, Object> startProcess(String processKey, Map<String, Object> variables) {
        // For startProcess, use empty fieldTypes map (will auto-detect types)
        Map<String, Object> body = Map.of("variables", convertToVariableMap(variables, new java.util.HashMap<>()));
        
        return camundaWebClient.post()
                .uri("/process-definition/key/" + processKey + "/start")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    private Map<String, Object> convertToVariableMap(Map<String, Object> variables, Map<String, String> fieldTypes) {
        return variables.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().toString().isEmpty())
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Object value = entry.getValue();
                            String key = entry.getKey();
                            String fieldType = fieldTypes.getOrDefault(key, "textfield");
                            String type = "String";
                            
                            // Determine the type based on the field type from Camunda form
                            if ("checkbox".equals(fieldType)) {
                                type = "Boolean";
                                if (!(value instanceof Boolean)) {
                                    value = Boolean.parseBoolean(value.toString());
                                }
                            } else if ("number".equals(fieldType)) {
                                // Number field - check if decimal or integer
                                if (value.toString().contains(".")) {
                                    try {
                                        value = Double.parseDouble(value.toString());
                                        type = "Double";
                                    } catch (NumberFormatException e) {
                                        log.warn("Could not parse number: {}", value);
                                    }
                                } else {
                                    try {
                                        value = Long.parseLong(value.toString());
                                        type = "Long";
                                    } catch (NumberFormatException e) {
                                        log.warn("Could not parse number: {}", value);
                                    }
                                }
                            } else if ("datetime".equals(fieldType)) {
                                // DateTime format from HTML5 datetime-local
                                // Camunda REST API expects ISO 8601 string WITHOUT specifying type as Date
                                // It will auto-detect it as Date from the format
                                try {
                                    String dateStr = value.toString();
                                    if (!dateStr.endsWith("Z") && !dateStr.contains("+")) {
                                        dateStr = dateStr + ":00Z";
                                    }
                                    // Keep as String, Camunda will parse it
                                    value = dateStr;
                                    type = "String";
                                } catch (Exception e) {
                                    log.warn("Could not parse date: {}, error: {}", value, e.getMessage());
                                }
                            } else if (value instanceof Boolean) {
                                type = "Boolean";
                            } else if (value instanceof Integer || value instanceof Long) {
                                type = "Long";
                            } else if (value instanceof Double || value instanceof Float) {
                                type = "Double";
                            }
                            // All textfield, textarea, select, etc. remain as String
                            
                            return Map.of(
                                "value", value,
                                "type", type
                            );
                        }
                ));
    }
}
