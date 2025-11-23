package com.gascorocora.transactional.service;

import com.gascorocora.transactional.dto.NotificacionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final JavaMailSender mailSender;

    public void enviarNotificacion(NotificacionRequest request) {
        log.info("Enviando notificación a: {} - Tipo: {}", request.getDestinatario(), request.getTipo());
        
        if ("EMAIL".equalsIgnoreCase(request.getTipo())) {
            enviarEmail(request);
        } else if ("SMS".equalsIgnoreCase(request.getTipo())) {
            enviarSMS(request);
        }
    }

    private void enviarEmail(NotificacionRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getDestinatario());
            message.setSubject(request.getAsunto());
            message.setText(request.getMensaje());
            message.setFrom("noreply@gascorocora.com");
            
            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", request.getDestinatario());
        } catch (Exception e) {
            log.error("Error al enviar email: {}", e.getMessage());
            // No lanzar excepción para no detener el proceso
        }
    }

    private void enviarSMS(NotificacionRequest request) {
        // Simulación de envío de SMS
        log.info("SMS simulado enviado a: {} - Mensaje: {}", request.getDestinatario(), request.getMensaje());
    }
}
