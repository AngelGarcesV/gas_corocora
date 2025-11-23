package com.gascorocora.transactional.controller;

import com.gascorocora.transactional.dto.NotificacionRequest;
import com.gascorocora.transactional.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @PostMapping
    public ResponseEntity<Map<String, String>> enviarNotificacion(@RequestBody NotificacionRequest request) {
        notificacionService.enviarNotificacion(request);
        return ResponseEntity.ok(Map.of("mensaje", "Notificación enviada exitosamente"));
    }
}
