package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
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
 * - numero_orden (String)
 * - proveedor_seleccionado (String)
 * - cantidad_gas (Integer): cantidad esperada
 *
 * Variables de salida:
 * - notificacion_discrepancia_enviada (Boolean)
 * - fecha_notificacion_discrepancia (String)
 * - ticket_discrepancia (String): número de ticket generado
 */
@Component("notificarDiscrepanciaDelegate")
public class NotificarDiscrepanciaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Obtener información de la discrepancia
        String detallesDiscrepancia = (String) execution.getVariable("detallesDiscrepancia");
        String numeroOrden = (String) execution.getVariable("numero_orden");
        String proveedor = (String) execution.getVariable("proveedor_seleccionado");
        Integer cantidadEsperada = (Integer) execution.getVariable("cantidad_gas");

        // Generar ticket único para la discrepancia
        String ticketDiscrepancia = generarTicketDiscrepancia();

        // Construir mensaje de notificación
        String mensaje = construirMensajeDiscrepancia(
                ticketDiscrepancia, numeroOrden, proveedor,
                cantidadEsperada, detallesDiscrepancia
        );

        // Simular envío de notificación
        boolean envioExitoso = enviarNotificacion(proveedor, mensaje);

        // Registrar en logs
        System.out.println("\n⚠️ ========== NOTIFICACIÓN DE DISCREPANCIA ==========");
        System.out.println(mensaje);
        System.out.println("====================================================\n");

        if (envioExitoso) {
            System.out.println("✅ Notificación enviada exitosamente al proveedor");

            // Guardar variables
            execution.setVariable("notificacion_discrepancia_enviada", true);
            execution.setVariable("fecha_notificacion_discrepancia",
                    LocalDateTime.now().toString());
            execution.setVariable("ticket_discrepancia", ticketDiscrepancia);
            execution.setVariable("estado_discrepancia", "NOTIFICADA");
            execution.setVariable("requiere_seguimiento", true);
        } else {
            System.err.println("❌ Error al enviar notificación");
            execution.setVariable("notificacion_discrepancia_enviada", false);
            execution.setVariable("error_notificacion", "Fallo en el envío");
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
                                                String detalles) {
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
        mensaje.append("Cantidad esperada: ").append(cantidadEsperada).append(" kg\n");
        mensaje.append("\nDetalles de la discrepancia:\n");
        mensaje.append(detalles).append("\n\n");

        mensaje.append("───────────────────────────────────────────────────\n");
        mensaje.append("ACCIÓN REQUERIDA:\n");
        mensaje.append("───────────────────────────────────────────────────\n");
        mensaje.append("1. Revisar el pedido enviado\n");
        mensaje.append("2. Verificar documentación de envío\n");
        mensaje.append("3. Coordinar devolución o corrección\n");
        mensaje.append("4. Responder con plan de acción\n\n");

        mensaje.append("Por favor, contactar al departamento de compras\n");
        mensaje.append("para resolver esta situación a la brevedad.\n\n");

        mensaje.append("═══════════════════════════════════════════════════\n");
        mensaje.append("   Sistema Automático de Gestión de Discrepancias  \n");
        mensaje.append("═══════════════════════════════════════════════════\n");

        return mensaje.toString();
    }

    private boolean enviarNotificacion(String proveedor, String mensaje) {
        // Aquí se integraría con:
        // - Email
        // - Sistema del proveedor (API)
        // - Plataforma de gestión de incidencias

        // Por ahora, simulamos el envío
        System.out.println("📧 Enviando notificación a: " + proveedor);
        return true;
    }
}