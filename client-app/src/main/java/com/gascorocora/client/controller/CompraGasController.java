package com.gascorocora.client.controller;

import com.gascorocora.client.service.CompraGasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CompraGasController {

    private final CompraGasService compraGasService;

    @GetMapping("/compra-gas/new")
    public String newCompraForm() {
        return "new-compra";
    }

    @PostMapping("/compra-gas/new")
    public String submitCompra(@RequestParam String cantidadGas,
            @RequestParam String proveedorPreferido,
            @RequestParam String usuarioSolicitante,
            @RequestParam String justificacion,
            @RequestParam String fechaRequerida,
            Model model) {
        try {
            log.info("Iniciando nueva solicitud de compra - Usuario: {}, Cantidad: {}",
                    usuarioSolicitante, cantidadGas);

            var processInstance = compraGasService.startCompraGasProcess(
                    cantidadGas, proveedorPreferido, usuarioSolicitante, justificacion, fechaRequerida);
            var businessKey = processInstance.get("businessKey");

            log.info("Solicitud de compra creada exitosamente: {}", businessKey);

            model.addAttribute("success", true);
            model.addAttribute("businessKey", businessKey);
            model.addAttribute("message",
                    "Su solicitud de compra ha sido radicada exitosamente. Su número de orden es: " + businessKey);
        } catch (Exception e) {
            log.error("Error al procesar solicitud de compra", e);
            model.addAttribute("success", false);
            model.addAttribute("error", "Error al procesar su solicitud: " + e.getMessage());
        }

        return "new-compra";
    }

    @GetMapping("/compra-gas/track")
    public String trackCompraForm() {
        return "track-compra";
    }

    @PostMapping("/compra-gas/track")
    public String trackCompra(@RequestParam String businessKey, Model model) {
        try {
            log.info("Consultando orden de compra: {}", businessKey);

            var processInstance = compraGasService.getProcessInstanceByBusinessKey(businessKey);
            log.info("Proceso encontrado, obteniendo historial...");

            var history = compraGasService.getProcessHistory(processInstance.get("id").toString());
            log.info("Historial obtenido: {} actividades", history.size());

            model.addAttribute("processInstance", processInstance);
            model.addAttribute("history", history);
            model.addAttribute("found", true);
        } catch (Exception e) {
            log.error("Error al consultar orden de compra: {}", businessKey, e);
            model.addAttribute("found", false);
            model.addAttribute("error", "No se encontró ninguna orden de compra con ese número: " + e.getMessage());
        }

        return "track-compra";
    }
}
