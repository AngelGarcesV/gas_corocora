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
 * Service Task: Notificar aceptación del pedido
 * Se ejecuta cuando NO hay discrepancias y el pedido es aceptado
 *
 * Variables de entrada:
 * - numero_orden (String)
 * - proveedor_seleccionado (String)
 * - cantidad_gas (Integer)
 * - costo_total (Number) - opcional
 * - dias_desde_envio (Long) - opcional
 * - initiator (String): usuario de Camunda
 *
 * Variables de salida:
 * - notificacion_aceptacion_enviada (Boolean)
 * - fecha_notificacion_aceptacion (String)
 * - pedido_aprobado (Boolean)
 * - estado_pedido (String)
 */
@Component("notificarAceptacionDelegate")
public class NotificarAceptacionDelegate implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(NotificarAceptacionDelegate.class);

    @Autowired
    private AuditoriaCompraService auditoriaCompraService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("=== Iniciando notificación de aceptación de pedido ===");

        try {
            // Capturar usuario desde Camunda
            String usuario = (String) execution.getVariable("initiator");
            if (usuario == null || usuario.isEmpty()) {
                usuario = "SISTEMA";
            }
            logger.info("Usuario que aprueba: {}", usuario);

            // Obtener información del pedido
            String numeroOrden = (String) execution.getVariable("numero_orden");
            String proveedor = (String) execution.getVariable("proveedor_seleccionado");
            Integer cantidadGas = (Integer) execution.getVariable("cantidad_gas");
            Number costoTotal = (Number) execution.getVariable("costo_total");
            Long diasDesdeEnvio = (Long) execution.getVariable("dias_desde_envio");

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

            // Construir mensaje de aceptación
            String mensaje = construirMensajeAceptacion(
                    numeroOrden, proveedor, cantidadGas, costoTotal, diasDesdeEnvio
            );

            logger.info("📧 Enviando notificación de aceptación para orden: {}", numeroOrden);

            // Simular envío de notificación
            boolean envioExitoso = enviarNotificacion(proveedor, mensaje);

            // Registrar en logs
            System.out.println("\n✅ ========== NOTIFICACIÓN DE ACEPTACIÓN ==========");
            System.out.println(mensaje);
            System.out.println("===================================================\n");

            if (envioExitoso) {
                logger.info("✅ Notificación de aceptación enviada al proveedor: {}", proveedor);

                // Guardar variables
                String fechaNotificacion = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                execution.setVariable("notificacion_aceptacion_enviada", true);
                execution.setVariable("fecha_notificacion_aceptacion", fechaNotificacion);
                execution.setVariable("pedido_aprobado", true);
                execution.setVariable("estado_pedido", "ACEPTADO");
                execution.setVariable("listo_para_facturacion", true);
                execution.setVariable("usuario_aprobacion", usuario);

                // Registrar en auditoría (sin fallar si hay error)
                try {
                    auditoriaCompraService.registrarAccion(
                        numeroOrden,
                        "ACEPTADA",
                        usuario,
                        "Pedido aceptado sin discrepancias",
                        "PEDIDO_RECIBIDO",
                        "ACEPTADA"
                    );
                } catch (Exception e) {
                    logger.warn("⚠️ No se pudo registrar en auditoría: {}", e.getMessage());
                    // No lanzar excepción, continuar normalmente
                }
            } else {
                logger.error("❌ Error al enviar notificación de aceptación");
                execution.setVariable("notificacion_aceptacion_enviada", false);
                execution.setVariable("error_aceptacion", "Falló el envío de la notificación");
            }
        } catch (Exception e) {
            // NUNCA lanzar excepciones - loguear y continuar
            logger.error("❌ Error en delegado de aceptación: {}", e.getMessage(), e);
            execution.setVariable("notificacion_aceptacion_enviada", false);
            execution.setVariable("error_aceptacion", "Error: " + e.getMessage());
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
        if (cantidad != null) {
            mensaje.append("Cantidad recibida: ").append(cantidad).append(" kg\n");
        }
        if (costo != null) {
            mensaje.append("Monto total: $").append(costo).append("\n");
        }
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
        logger.debug("📧 Enviando confirmación a: {}", proveedor);
        try {
            // Aquí se integraría con:
            // - Email al proveedor
            // - Portal del proveedor (API)
            // - Sistema ERP

            // Por ahora, simulamos el envío exitoso
            logger.debug("✅ Notificación simulada enviada correctamente");
            return true;
        } catch (Exception e) {
            logger.error("❌ Error al enviar notificación: {}", e.getMessage());
            return false;
        }
    }
}