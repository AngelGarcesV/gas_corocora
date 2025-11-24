package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service Task: Notificar aceptación del pedido
 * Se ejecuta cuando NO hay discrepancias y el pedido es aceptado
 *
 * Variables de entrada:
 * - numero_orden (String)
 * - proveedor_seleccionado (String)
 * - cantidad_gas (Integer)
 * - fecha_recepcion (String)
 *
 * Variables de salida:
 * - notificacion_aceptacion_enviada (Boolean)
 * - fecha_notificacion_aceptacion (String)
 * - pedido_aprobado (Boolean)
 */
@Component("notificarAceptacionDelegate")
public class NotificarAceptacionDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Obtener información del pedido
        String numeroOrden = (String) execution.getVariable("numero_orden");
        String proveedor = (String) execution.getVariable("proveedor_seleccionado");
        Integer cantidadGas = (Integer) execution.getVariable("cantidad_gas");
        Number costoTotal = (Number) execution.getVariable("costo_total");
        Long diasDesdeEnvio = (Long) execution.getVariable("dias_desde_envio");

        // Construir mensaje de aceptación
        String mensaje = construirMensajeAceptacion(
                numeroOrden, proveedor, cantidadGas, costoTotal, diasDesdeEnvio
        );

        // Simular envío de notificación
        boolean envioExitoso = enviarNotificacion(proveedor, mensaje);

        // Registrar en logs
        System.out.println("\n✅ ========== NOTIFICACIÓN DE ACEPTACIÓN ==========");
        System.out.println(mensaje);
        System.out.println("===================================================\n");

        if (envioExitoso) {
            System.out.println("✅ Notificación de aceptación enviada al proveedor");

            // Guardar variables
            execution.setVariable("notificacion_aceptacion_enviada", true);
            execution.setVariable("fecha_notificacion_aceptacion",
                    LocalDateTime.now().toString());
            execution.setVariable("pedido_aprobado", true);
            execution.setVariable("estado_pedido", "ACEPTADO");
            execution.setVariable("listo_para_facturacion", true);
        } else {
            System.err.println("❌ Error al enviar notificación de aceptación");
            execution.setVariable("notificacion_aceptacion_enviada", false);
        }
    }

    private String construirMensajeAceptacion(String orden, String proveedor,
                                              Integer cantidad, Number costo,
                                              Long diasEntrega) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaActual = LocalDateTime.now().format(formatter);

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("╔═══════════════════════════════════════════════════╗\n");
        mensaje.append("║     ✅  CONFIRMACIÓN DE ACEPTACIÓN DE PEDIDO      ║\n");
        mensaje.append("╚═══════════════════════════════════════════════════╝\n\n");

        mensaje.append("Fecha: ").append(fechaActual).append("\n");
        mensaje.append("Proveedor: ").append(proveedor).append("\n");
        mensaje.append("Número de Orden: ").append(orden).append("\n\n");

        mensaje.append("───────────────────────────────────────────────────\n");
        mensaje.append("PEDIDO ACEPTADO:\n");
        mensaje.append("───────────────────────────────────────────────────\n");
        mensaje.append("Producto: Gas Natural\n");
        mensaje.append("Cantidad recibida: ").append(cantidad).append(" kg\n");
        mensaje.append("Monto total: $").append(costo).append("\n");

        if (diasEntrega != null) {
            mensaje.append("Tiempo de entrega: ").append(diasEntrega).append(" días\n");
        }

        mensaje.append("\n✅ El pedido ha sido verificado y aceptado sin observaciones.\n\n");

        mensaje.append("───────────────────────────────────────────────────\n");
        mensaje.append("PRÓXIMOS PASOS:\n");
        mensaje.append("───────────────────────────────────────────────────\n");
        mensaje.append("1. Validación de factura\n");
        mensaje.append("2. Procesamiento de pago según términos acordados\n");
        mensaje.append("3. Actualización de registro de proveedor\n\n");

        mensaje.append("Gracias por su servicio y entrega oportuna.\n\n");

        mensaje.append("═══════════════════════════════════════════════════\n");
        mensaje.append("       Sistema Automático de Gestión de Compras    \n");
        mensaje.append("═══════════════════════════════════════════════════\n");

        return mensaje.toString();
    }

    private boolean enviarNotificacion(String proveedor, String mensaje) {
        // Aquí se integraría con:
        // - Email al proveedor
        // - Portal del proveedor (API)
        // - Sistema ERP

        // Por ahora, simulamos el envío
        System.out.println("📧 Enviando confirmación a: " + proveedor);
        return true;
    }
}