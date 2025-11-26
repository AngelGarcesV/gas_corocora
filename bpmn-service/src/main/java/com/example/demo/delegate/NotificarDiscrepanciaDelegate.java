package com.example.demo.delegate;

import com.example.demo.service.AuditoriaCompraService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service Task: Notificar discrepancia al proveedor
 * Se ejecuta cuando se detecta una discrepancia en el pedido
 *
 * Variables de entrada:
 * - discrepanciaExistente (Boolean): true si hay discrepancia
 * - detallesDiscrepancia (String): descripción del problema
 * - tipo_discrepancia (String) - opcional
 * - numero_orden (String)
 * - proveedor_seleccionado (String)
 * - cantidad_gas (Integer): cantidad esperada
 * - initiator (String): usuario de Camunda
 *
 * Variables de salida:
 * - notificacion_discrepancia_enviada (Boolean)
 * - fecha_notificacion_discrepancia (String)
 * - ticket_discrepancia (String): número de ticket generado
 * - estado_discrepancia (String)
 */
@Component("notificarDiscrepanciaDelegate")
public class NotificarDiscrepanciaDelegate implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(NotificarDiscrepanciaDelegate.class);

    @Autowired
    private AuditoriaCompraService auditoriaCompraService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("=== Iniciando notificación de discrepancia ===");

        try {
            // Capturar usuario desde Camunda
            String usuario = (String) execution.getVariable("initiator");
            if (usuario == null || usuario.isEmpty()) {
                usuario = "SISTEMA";
            }
            logger.info("Usuario que notifica discrepancia: {}", usuario);

            // Obtener información de la discrepancia
            String detallesDiscrepancia = (String) execution.getVariable("detallesDiscrepancia");
            String numeroOrden = (String) execution.getVariable("numero_orden");
            String proveedor = (String) execution.getVariable("proveedor_seleccionado");
            Integer cantidadEsperada = (Integer) execution.getVariable("cantidad_gas");
            String tipoDiscrepancia = (String) execution.getVariable("tipo_discrepancia");

            // En contexto de subprocess, puede que no tengamos todos los datos
            // Esto es normal cuando se completa el formulario de verificación
            if (numeroOrden == null) {
                logger.warn("⚠️ numeroOrden no disponible en contexto subprocess");
                numeroOrden = "NO_ESPECIFICADO";
            }
            if (proveedor == null) {
                logger.warn("⚠️ proveedor no disponible en contexto subprocess");
                proveedor = "NO_ESPECIFICADO";
            }

            // Generar ticket único para la discrepancia
            String ticketDiscrepancia = generarTicketDiscrepancia();

            logger.info("🎟️ Ticket generado: {} para orden: {}", ticketDiscrepancia, numeroOrden);

            // Construir mensaje de notificación
            String mensaje = construirMensajeDiscrepancia(
                    ticketDiscrepancia, numeroOrden, proveedor,
                    cantidadEsperada, detallesDiscrepancia, tipoDiscrepancia
            );

            // Simular envío de notificación
            boolean envioExitoso = enviarNotificacion(proveedor, mensaje);

            // Registrar en logs
            System.out.println("\n⚠️ ========== NOTIFICACIÓN DE DISCREPANCIA ==========");
            System.out.println(mensaje);
            System.out.println("====================================================\n");

            if (envioExitoso) {
                logger.info("✅ Notificación enviada exitosamente al proveedor: {}", proveedor);

                // Guardar variables
                String fechaNotificacion = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                execution.setVariable("notificacion_discrepancia_enviada", true);
                execution.setVariable("fecha_notificacion_discrepancia", fechaNotificacion);
                execution.setVariable("ticket_discrepancia", ticketDiscrepancia);
                execution.setVariable("estado_discrepancia", "NOTIFICADA");
                execution.setVariable("requiere_seguimiento", true);
                execution.setVariable("usuario_notificacion", usuario);

                // Registrar en auditoría (sin fallar si hay error)
                try {
                    auditoriaCompraService.registrarAccion(
                        numeroOrden,
                        "DISCREPANCIA_NOTIFICADA",
                        usuario,
                        "Discrepancia notificada al proveedor - Ticket: " + ticketDiscrepancia + " - Tipo: " + (tipoDiscrepancia != null ? tipoDiscrepancia : "NO ESPECIFICADO"),
                        "PEDIDO_RECIBIDO",
                        "DISCREPANCIA_DETECTADA"
                    );
                } catch (Exception e) {
                    logger.warn("⚠️ No se pudo registrar en auditoría: {}", e.getMessage());
                    // No lanzar excepción, continuar normalmente
                }
            } else {
                logger.error("❌ Error al enviar notificación de discrepancia");
                execution.setVariable("notificacion_discrepancia_enviada", false);
                execution.setVariable("error_discrepancia", "Fallo en el envío");
            }
        } catch (Exception e) {
            // NUNCA lanzar excepciones - loguear y continuar
            logger.error("❌ Error en delegado de discrepancia: {}", e.getMessage(), e);
            execution.setVariable("notificacion_discrepancia_enviada", false);
            execution.setVariable("error_discrepancia", "Error: " + e.getMessage());
        }
    }

    private String generarTicketDiscrepancia() {
        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "DISC-" + timestamp + "-" +
                String.format("%04d", (int)(Math.random() * 9999));
    }

    private String construirMensajeDiscrepancia(String ticket, String orden,
                                                String proveedor, Integer cantidadEsperada,
                                                String detalles, String tipo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaActual = LocalDateTime.now().format(formatter);

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("╔═══════════════════════════════════════════════════╗\n");
        mensaje.append("║     ⚠️  NOTIFICACIÓN DE DISCREPANCIA EN PEDIDO    ║\n");
        mensaje.append("╚═══════════════════════════════════════════════════╝\n\n");

        mensaje.append("Ticket: ").append(ticket).append("\n");
        mensaje.append("Fecha: ").append(fechaActual).append("\n");
        mensaje.append("Proveedor: ").append(proveedor).append("\n");
        mensaje.append("Número de Orden: ").append(orden).append("\n\n");

        mensaje.append("───────────────────────────────────────────────────\n");
        mensaje.append("PROBLEMA DETECTADO:\n");
        mensaje.append("───────────────────────────────────────────────────\n");
        if (cantidadEsperada != null) {
            mensaje.append("Cantidad esperada: ").append(cantidadEsperada).append(" kg\n");
        }
        if (tipo != null && !tipo.isEmpty()) {
            mensaje.append("Tipo de discrepancia: ").append(tipo).append("\n");
        }

        mensaje.append("\nDetalles de la discrepancia:\n");
        if (detalles != null && !detalles.isEmpty()) {
            mensaje.append(detalles).append("\n");
        } else {
            mensaje.append("Sin descripción adicional\n");
        }

        mensaje.append("\n───────────────────────────────────────────────────\n");
        mensaje.append("ACCIÓN REQUERIDA:\n");
        mensaje.append("───────────────────────────────────────────────────\n");
        mensaje.append("1. Revisar el pedido enviado\n");
        mensaje.append("2. Verificar documentación de envío\n");
        mensaje.append("3. Coordinar devolución o corrección\n");
        mensaje.append("4. Responder con plan de acción\n");
        mensaje.append("5. Referenciar ticket: ").append(ticket).append("\n\n");

        mensaje.append("Por favor, contactar al departamento de compras\n");
        mensaje.append("para resolver esta situación a la brevedad.\n\n");

        mensaje.append("═══════════════════════════════════════════════════\n");
        mensaje.append("   Sistema Automático de Gestión de Discrepancias  \n");
        mensaje.append("═══════════════════════════════════════════════════\n");

        return mensaje.toString();
    }

    private boolean enviarNotificacion(String proveedor, String mensaje) {
        logger.debug("📧 Enviando notificación de discrepancia a: {}", proveedor);
        try {
            // Aquí se integraría con:
            // - Email
            // - Sistema del proveedor (API)
            // - Plataforma de gestión de incidencias

            // Por ahora, simulamos el envío exitoso
            logger.debug("✅ Notificación de discrepancia simulada enviada correctamente");
            return true;
        } catch (Exception e) {
            logger.error("❌ Error al enviar notificación: {}", e.getMessage());
            return false;
        }
    }
}