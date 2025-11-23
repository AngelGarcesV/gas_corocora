package com.gascorocora.transactional.controller;

import com.gascorocora.transactional.dto.CoberturaResponse;
import com.gascorocora.transactional.service.CoberturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cobertura")
@RequiredArgsConstructor
public class CoberturaController {

    private final CoberturaService coberturaService;

    @PostMapping("/verificar")
    public ResponseEntity<CoberturaResponse> verificarCobertura(@RequestBody Map<String, String> request) {
        String direccion = request.get("direccion");
        return ResponseEntity.ok(coberturaService.verificarCobertura(direccion));
    }
}
