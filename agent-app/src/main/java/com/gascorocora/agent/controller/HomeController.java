package com.gascorocora.agent.controller;

import com.gascorocora.agent.service.CamundaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CamundaService camundaService;

    @GetMapping("/")
    public String home(Model model) {
        var taskCount = camundaService.getTaskCount();
        model.addAttribute("taskCount", taskCount);
        return "index";
    }

    @GetMapping("/processes")
    public String listProcesses(Model model) {
        var processes = camundaService.getProcessDefinitions();
        model.addAttribute("processes", processes);
        return "processes/list";
    }

    @GetMapping("/processes/{processKey}/start")
    public String showStartForm(@PathVariable String processKey, Model model) {
        var form = camundaService.getStartForm(processKey);
        model.addAttribute("processKey", processKey);
        model.addAttribute("form", form);
        return "processes/start";
    }

    @PostMapping("/processes/{processKey}/start")
    public String startProcess(@PathVariable String processKey,
                              @RequestParam Map<String, Object> variables,
                              Model model) {
        var processInstance = camundaService.startProcess(processKey, variables);
        model.addAttribute("processInstance", processInstance);
        return "redirect:/tasks";
    }
}
