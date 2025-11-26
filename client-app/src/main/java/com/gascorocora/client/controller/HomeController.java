package com.gascorocora.client.controller;

import com.gascorocora.client.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProcessService processService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/track")
    public String trackForm() {
        return "track";
    }

    @PostMapping("/track")
    public String trackProcess(@RequestParam String businessKey, Model model) {
        try {
            var processInstance = processService.getProcessInstanceByBusinessKey(businessKey);
            var history = processService.getProcessHistory(processInstance.get("id").toString());
            
            model.addAttribute("processInstance", processInstance);
            model.addAttribute("history", history);
            model.addAttribute("found", true);
        } catch (Exception e) {
            model.addAttribute("found", false);
            model.addAttribute("error", "No se encontró ningún trámite con ese número de solicitud");
        }
        
        return "track";
    }

    @GetMapping("/new-request")
    public String newRequestForm() {
        return "new-request";
    }

    @PostMapping("/new-request")
    public String submitRequest(@RequestParam String nombre,
                               @RequestParam String cedula,
                               @RequestParam String direccion,
                               @RequestParam String telefono,
                               @RequestParam String email,
                               @RequestParam String estrato,
                               Model model) {
        try {
            var processInstance = processService.startNewRequest(nombre, cedula, direccion, telefono, email, estrato);
            var businessKey = processInstance.get("businessKey");
            
            model.addAttribute("success", true);
            model.addAttribute("businessKey", businessKey);
            model.addAttribute("message", "Su solicitud ha sido radicada exitosamente. Su número de radicado es: " + businessKey);
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("error", "Error al procesar su solicitud: " + e.getMessage());
        }
        
        return "new-request";
    }
}
