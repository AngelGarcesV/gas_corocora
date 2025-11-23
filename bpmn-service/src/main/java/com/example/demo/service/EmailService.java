package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EmailService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Value("${app.mail.from}")
    private String fromEmail;
    
    @Value("${app.mail.enabled:true}")
    private boolean emailEnabled;
    
    /**
     * Envía un correo simple
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            LOGGER.info("Email deshabilitado. No se envió: {} a {}", subject, to);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            LOGGER.info("Email enviado exitosamente a: {}", to);
        } catch (Exception e) {
            LOGGER.error("Error enviando email a {}: {}", to, e.getMessage(), e);
        }
    }
    
    /**
     * Envía un correo HTML usando plantilla Thymeleaf
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!emailEnabled) {
            LOGGER.info("Email deshabilitado. No se envió: {} a {}", subject, to);
            return;
        }
        
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process(templateName, context);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            LOGGER.info("Email HTML enviado exitosamente a: {}", to);
        } catch (MessagingException e) {
            LOGGER.error("Error enviando email HTML a {}: {}", to, e.getMessage(), e);
        }
    }
    
    /**
     * Notifica solicitud inviable
     */
    public void notificarSolicitudInviable(String clienteEmail, String solicitudId) {
        String subject = "Gas Corocora - Solicitud Inviable";
        String text = String.format(
            "Estimado cliente,\n\n" +
            "Lamentamos informarle que su solicitud de conexión #%s no puede ser procesada " +
            "debido a que la zona solicitada no cuenta con red de gas disponible.\n\n" +
            "Para más información, puede contactarnos.\n\n" +
            "Atentamente,\n" +
            "Gas Corocora - Área Técnica",
            solicitudId
        );
        
        sendSimpleEmail(clienteEmail, subject, text);
    }
    
    /**
     * Envía cotización al cliente
     */
    public void enviarCotizacion(String clienteEmail, String solicitudId, double montoConexion, double descuento) {
        String subject = "Gas Corocora - Cotización de Instalación";
        double montoAPagar = montoConexion - (montoConexion * descuento / 100);
        
        String text = String.format(
            "Estimado cliente,\n\n" +
            "Nos complace enviarle la cotización para la instalación de gas:\n\n" +
            "Solicitud: %s\n" +
            "Costo de conexión: $%.2f\n" +
            "Descuento aplicado: %.0f%%\n" +
            "Total a pagar: $%.2f\n\n" +
            "Esta cotización es válida por 5 días hábiles.\n\n" +
            "Para continuar con el proceso, debe confirmar su aceptación y realizar el pago.\n\n" +
            "Atentamente,\n" +
            "Gas Corocora - Área Comercial",
            solicitudId, montoConexion, descuento, montoAPagar
        );
        
        sendSimpleEmail(clienteEmail, subject, text);
    }
    
    /**
     * Notifica cancelación de solicitud
     */
    public void notificarCancelacionSolicitud(String clienteEmail, String solicitudId) {
        String subject = "Gas Corocora - Solicitud Cancelada";
        String text = String.format(
            "Estimado cliente,\n\n" +
            "Su solicitud de conexión #%s ha sido cancelada por falta de respuesta " +
            "dentro del plazo establecido de 5 días.\n\n" +
            "Si desea reiniciar el proceso, puede realizar una nueva solicitud.\n\n" +
            "Atentamente,\n" +
            "Gas Corocora",
            solicitudId
        );
        
        sendSimpleEmail(clienteEmail, subject, text);
    }
    
    /**
     * Notifica problemas en instalación
     */
    public void notificarProblemasInstalacion(String clienteEmail, String solicitudId, String problemas) {
        String subject = "Gas Corocora - Problemas en Instalación";
        String text = String.format(
            "Estimado cliente,\n\n" +
            "Durante la verificación de su instalación #%s se detectaron los siguientes problemas:\n\n" +
            "%s\n\n" +
            "Nuestro equipo técnico se pondrá en contacto para resolverlos.\n\n" +
            "Atentamente,\n" +
            "Gas Corocora - Área Técnica",
            solicitudId, problemas
        );
        
        sendSimpleEmail(clienteEmail, subject, text);
    }
    
    /**
     * Notifica activación del servicio
     */
    public void notificarActivacionServicio(String clienteEmail, String solicitudId, String numeroMedidor) {
        String subject = "Gas Corocora - Servicio Activado";
        String text = String.format(
            "Estimado cliente,\n\n" +
            "¡Felicitaciones! Su servicio de gas ha sido activado exitosamente.\n\n" +
            "Solicitud: %s\n" +
            "Número de medidor: %s\n\n" +
            "En breve recibirá su primera factura.\n\n" +
            "Atentamente,\n" +
            "Gas Corocora - Área de Facturación",
            solicitudId, numeroMedidor
        );
        
        sendSimpleEmail(clienteEmail, subject, text);
    }
    
    /**
     * Notifica facturación iniciada
     */
    public void notificarFacturacionIniciada(String clienteEmail, String solicitudId) {
        String subject = "Gas Corocora - Facturación Iniciada";
        String text = String.format(
            "Estimado cliente,\n\n" +
            "Su servicio de gas ha sido habilitado para facturación.\n\n" +
            "Solicitud: %s\n\n" +
            "Recibirá su factura mensualmente según el ciclo de facturación asignado.\n\n" +
            "Atentamente,\n" +
            "Gas Corocora - Área de Facturación",
            solicitudId
        );
        
        sendSimpleEmail(clienteEmail, subject, text);
    }
}