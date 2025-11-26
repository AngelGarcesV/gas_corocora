package com.gascorocora.agent.controller;

import com.gascorocora.agent.service.CamundaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final CamundaService camundaService;

    @GetMapping
    public String listTasks(@RequestParam(required = false) String role, Model model) {
        var tasks = camundaService.getAssignedTasks();
        
        tasks = tasks.stream()
                .filter(task -> {
                    String taskName = (String) task.get("name");
                    return !isClientTask(taskName);
                })
                .collect(java.util.stream.Collectors.toList());
        
        if (role != null && !role.isEmpty()) {
            tasks = tasks.stream()
                    .filter(task -> {
                        String taskName = (String) task.get("name");
                        return isTaskForRole(taskName, role);
                    })
                    .collect(java.util.stream.Collectors.toList());
        }
        
        model.addAttribute("tasks", tasks);
        model.addAttribute("currentRole", role);
        return "tasks/list";
    }
    
    private boolean isTaskForRole(String taskName, String role) {
        if (isClientTask(taskName)) {
            return false;
        }
        
        switch (role) {
            case "comercial":
                return taskName != null && (
                    taskName.contains("pago de instalación") ||
                    taskName.contains("Recibir pago") ||
                    taskName.contains("Procesar cotización")
                );
            case "tecnico":
                return taskName != null && (
                    taskName.contains("Verificar cobertura") ||
                    taskName.contains("inspección") ||
                    taskName.contains("instalación") ||
                    taskName.contains("Verificar instalación") ||
                    taskName.contains("corrección") ||
                    taskName.contains("Inspeccionar") ||
                    taskName.contains("Agendar cita") ||
                    taskName.contains("Solicitar corrección")
                );
            case "facturacion":
                return taskName != null && (
                    taskName.contains("facturación") ||
                    taskName.contains("Registrar en sistema")
                );
            default:
                return true;
        }
    }
    
    private boolean isClientTask(String taskName) {
        return taskName != null && (
            taskName.contains("Radicar solicitud") ||
            taskName.contains("respuesta de cotización") ||
            taskName.contains("Enviar respuesta")
        );
    }

    @GetMapping("/{taskId}")
    public String viewTask(@PathVariable String taskId, Model model) {
        var task = camundaService.getTask(taskId);
        var variables = camundaService.getTaskVariables(taskId);
        var form = camundaService.getTaskForm(taskId);
        
        model.addAttribute("task", task);
        model.addAttribute("variables", variables);
        model.addAttribute("form", form);
        return "tasks/detail";
    }

    @PostMapping("/{taskId}/complete")
    public String completeTask(@PathVariable String taskId, 
                              @RequestParam Map<String, Object> variables) {
        log.info("=== Complete Task Debug ===");
        log.info("Task ID: {}", taskId);
        log.info("Received variables: {}", variables);
        
        variables.keySet().removeIf(key -> key.startsWith("_"));
        log.info("After removing _ params: {}", variables);
        
        Map<String, Object> form = camundaService.getTaskForm(taskId);
        log.info("Form retrieved: {}", form);
        Map<String, String> fieldTypes = new java.util.HashMap<>();
        
        if (form.containsKey("components")) {
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> components = 
                (java.util.List<Map<String, Object>>) form.get("components");
            
            for (Map<String, Object> component : components) {
                if (component.containsKey("key")) {
                    String key = (String) component.get("key");
                    String type = (String) component.get("type");
                    fieldTypes.put(key, type);
                    
                    if ("checkbox".equals(type) && !variables.containsKey(key)) {
                        variables.put(key, false);
                    }
                }
            }
        }
        
        log.info("Valid form field types: {}", fieldTypes);
        
        Map<String, Object> processVariables = new java.util.HashMap<>();
        for (String key : fieldTypes.keySet()) {
            if (variables.containsKey(key)) {
                processVariables.put(key, variables.get(key));
            }
        }
        
        log.info("Filtered process variables to send: {}", processVariables);
        
        camundaService.completeTask(taskId, processVariables, fieldTypes);
        return "redirect:/tasks";
    }

    @PostMapping("/{taskId}/claim")
    public String claimTask(@PathVariable String taskId) {
        camundaService.claimTask(taskId);
        return "redirect:/tasks/" + taskId;
    }
}
