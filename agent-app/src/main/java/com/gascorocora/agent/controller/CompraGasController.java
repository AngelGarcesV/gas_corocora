package com.gascorocora.agent.controller;

import com.gascorocora.agent.service.CamundaService;
import com.gascorocora.agent.service.CompraGasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión de compras de gas
 * Muestra tareas del proceso BPMN Compra_gas
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/compras-gas")
@Slf4j
public class CompraGasController {

    private final CamundaService camundaService;
    private final CompraGasService compraGasService;

    /**
     * Obtiene estadísticas de compras
     */
    @GetMapping("/estadisticas")
    public String verEstadisticas(Model model) {
        var stats = compraGasService.obtenerEstadisticas();
        model.addAttribute("estadisticas", stats);
        return "compras-gas/estadisticas";
    }

    /**
     * Lista todas las tareas de compra de gas
     */
    @GetMapping
    public String listarTareas(Model model) {
        List<Map<String, Object>> tareas = camundaService.getCompraGasTasks();
        model.addAttribute("tareas", tareas);
        model.addAttribute("totalTareas", tareas.size());
        return "compras-gas/list";
    }

    /**
     * Muestra detalles de una tarea específica
     */
    @GetMapping("/{taskId}")
    public String verDetalle(@PathVariable String taskId, Model model) {
        var tarea = camundaService.getTask(taskId);
        var variables = camundaService.getTaskVariables(taskId);
        var form = camundaService.getTaskForm(taskId);
        
        model.addAttribute("task", tarea);
        model.addAttribute("variables", variables);
        model.addAttribute("form", form);
        return "compras-gas/detail";
    }

    /**
     * Reclama una tarea para el usuario actual
     */
    @PostMapping("/{taskId}/claim")
    public String claimTask(@PathVariable String taskId) {
        log.info("Reclamando tarea: {}", taskId);
        try {
            camundaService.claimTask(taskId);
            log.info("Tarea reclamada exitosamente");
            return "redirect:/compras-gas/" + taskId;
        } catch (Exception e) {
            log.error("Error reclamando tarea", e);
            return "redirect:/compras-gas";
        }
    }

    /**
     * Completa una tarea con variables del formulario
     */
    @PostMapping("/{taskId}/complete")
    public String completeTask(@PathVariable String taskId, 
                              @RequestParam Map<String, Object> variables) {
        log.info("Completando tarea de compra: {}", taskId);
        log.info("Variables recibidas: {}", variables);
        
        try {
            // Remover parámetros de control de Spring
            variables.keySet().removeIf(key -> key.startsWith("_"));
            
            // Obtener tipos de campos del formulario
            Map<String, Object> form = camundaService.getTaskForm(taskId);
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
                        log.info("Componente: {} -> {}", key, type);
                        
                        // Manejar checkboxes
                        if ("checkbox".equals(type) && !variables.containsKey(key)) {
                            variables.put(key, false);
                        }
                    }
                }
            }
            
            // Filtrar solo variables válidas
            Map<String, Object> processVariables = new java.util.HashMap<>();
            for (String key : fieldTypes.keySet()) {
                if (variables.containsKey(key)) {
                    processVariables.put(key, variables.get(key));
                    log.info("Variable final: {} = {}", key, variables.get(key));
                }
            }
            
            log.info("Variables a enviar a Camunda: {}", processVariables);
            camundaService.completeTask(taskId, processVariables, fieldTypes);
            log.info("Tarea completada exitosamente: {}", taskId);
            
            // Pequeño delay para que Camunda procese la tarea completada
            Thread.sleep(1000);
            
            return "redirect:/compras-gas";
        } catch (Exception e) {
            log.error("Error completando tarea: {}", taskId, e);
            return "redirect:/compras-gas";
        }
    }
}
