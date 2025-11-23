package com.gascorocora.client.controller;

import com.gascorocora.client.service.CamundaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final CamundaService camundaService;

    @GetMapping
    public String listTasks(Model model) {
        List<Map<String, Object>> allTasks = camundaService.getAllTasks();
        
        // Filtrar solo las tareas del cliente
        List<Map<String, Object>> clientTasks = allTasks.stream()
            .filter(task -> isClientTask(task.get("name").toString()))
            .collect(Collectors.toList());
        
        model.addAttribute("tasks", clientTasks);
        return "tasks/list";
    }

    @GetMapping("/{id}")
    public String taskDetail(@PathVariable String id, Model model) {
        Map<String, Object> task = camundaService.getTask(id);
        Map<String, Object> form = camundaService.getTaskForm(id);
        Map<String, Object> variables = camundaService.getProcessVariables(task.get("processInstanceId").toString());
        
        model.addAttribute("task", task);
        model.addAttribute("form", form);
        model.addAttribute("variables", variables);
        
        return "tasks/detail";
    }

    @PostMapping("/{id}/complete")
    public String completeTask(@PathVariable String id, @RequestParam Map<String, String> formData, Model model) {
        try {
            // Remover parámetros de control de Spring
            formData.remove("_csrf");
            
            // Get valid form field keys and their types
            Map<String, Object> form = camundaService.getTaskForm(id);
            Map<String, String> fieldTypes = new java.util.HashMap<>();
            
            if (form.containsKey("components")) {
                @SuppressWarnings("unchecked")
                java.util.List<Map<String, Object>> components = 
                    (java.util.List<Map<String, Object>>) form.get("components");
                
                for (Map<String, Object> component : components) {
                    // Only include components that have a key (form fields)
                    if (component.containsKey("key")) {
                        String key = (String) component.get("key");
                        String type = (String) component.get("type");
                        fieldTypes.put(key, type);
                        
                        // Handle checkboxes: if not present, set to false
                        if ("checkbox".equals(type) && !formData.containsKey(key)) {
                            formData.put(key, "false");
                        }
                    }
                }
            }
            
            // Remover prefijos _ de campos checkbox
            formData.keySet().removeIf(key -> key.startsWith("_"));
            
            // Filter only valid form variables
            Map<String, String> processVariables = new java.util.HashMap<>();
            for (String key : fieldTypes.keySet()) {
                if (formData.containsKey(key)) {
                    processVariables.put(key, formData.get(key));
                }
            }
            
            camundaService.completeTask(id, processVariables, fieldTypes);
            
            model.addAttribute("success", true);
            model.addAttribute("message", "Tarea completada exitosamente");
            return "redirect:/tasks";
        } catch (Exception e) {
            model.addAttribute("error", "Error al completar la tarea: " + e.getMessage());
            return taskDetail(id, model);
        }
    }

    private boolean isClientTask(String taskName) {
        // Tareas que corresponden al lane del Cliente según el BPMN
        return taskName != null && (
            taskName.contains("Radicar solicitud") ||
            taskName.contains("respuesta de cotización") ||
            taskName.contains("Enviar respuesta")
        );
    }
}
