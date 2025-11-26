package com.gascorocora.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CamundaService {

    @Value("${camunda.base-url}")
    private String camundaBaseUrl;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    private WebClient getCamundaWebClient() {
        return webClientBuilder.baseUrl(camundaBaseUrl).build();
    }

    public List<Map<String, Object>> getAllTasks() {
        String response = getCamundaWebClient()
                .get()
                .uri("/task")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Map<String, Object> getTask(String taskId) {
        String response = getCamundaWebClient()
                .get()
                .uri("/task/" + taskId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public Map<String, Object> getTaskForm(String taskId) {
        try {
            String deployedForm = getCamundaWebClient()
                    .get()
                    .uri("/task/" + taskId + "/deployed-form")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(deployedForm, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public Map<String, Object> getProcessVariables(String processInstanceId) {
        try {
            String response = getCamundaWebClient()
                    .get()
                    .uri("/process-instance/" + processInstanceId + "/variables")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public void completeTask(String taskId, Map<String, String> variables, Map<String, String> fieldTypes) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("variables", convertToVariableMap(variables, fieldTypes));

        getCamundaWebClient()
                .post()
                .uri("/task/" + taskId + "/complete")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    private Map<String, Object> convertToVariableMap(Map<String, String> variables, Map<String, String> fieldTypes) {
        Map<String, Object> result = new HashMap<>();
        
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            Map<String, Object> variableValue = new HashMap<>();
            String key = entry.getKey();
            String value = entry.getValue();
            String fieldType = fieldTypes.getOrDefault(key, "textfield");
            
            // Determine the type based on the field type from Camunda form
            if ("checkbox".equals(fieldType)) {
                variableValue.put("value", Boolean.parseBoolean(value));
                variableValue.put("type", "Boolean");
            } else if ("number".equals(fieldType)) {
                // Number field - check if decimal or integer
                if (value.contains(".")) {
                    try {
                        variableValue.put("value", Double.parseDouble(value));
                        variableValue.put("type", "Double");
                    } catch (NumberFormatException e) {
                        variableValue.put("value", value);
                        variableValue.put("type", "String");
                    }
                } else {
                    try {
                        variableValue.put("value", Long.parseLong(value));
                        variableValue.put("type", "Long");
                    } catch (NumberFormatException e) {
                        variableValue.put("value", value);
                        variableValue.put("type", "String");
                    }
                }
            } else if ("datetime".equals(fieldType)) {
                // DateTime format from HTML5 datetime-local
                // Camunda REST API expects ISO 8601 string WITHOUT specifying type as Date
                try {
                    String dateStr = value;
                    if (!dateStr.endsWith("Z") && !dateStr.contains("+")) {
                        dateStr = dateStr + ":00Z";
                    }
                    // Keep as String, Camunda will auto-detect it as Date
                    variableValue.put("value", dateStr);
                    variableValue.put("type", "String");
                } catch (Exception e) {
                    // If parsing fails, keep as string
                    variableValue.put("value", value);
                    variableValue.put("type", "String");
                }
            } else {
                // All textfield, textarea, select, etc. remain as String
                variableValue.put("value", value);
                variableValue.put("type", "String");
            }
            
            result.put(key, variableValue);
        }
        
        return result;
    }

    private boolean isDate(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            sdf.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
