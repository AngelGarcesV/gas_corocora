package com.example.demo.delegate;

import com.example.demo.model.OrdenCompra;
import com.example.demo.repository.OrdenCompraRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.AuditoriaCompraService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Service Task: Enviar orden de compra al proveedor
 * Se ejecuta para notificar al proveedor sobre la orden de compra
 *
 * Variables de entrada:
 * - ordenId (String): ID de la orden
 * - initiator (String): usuario de Camunda
 *
 * Variables de salida:
 * - estado_orden (String): "ORDEN_ENVIADA"
 * - fecha_envio_orden (String)
 * - orden_enviada (Boolean)
 */
@Component("enviarOrdenCompraDelegate")
public class EnviarOrdenCompraDelegate implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(EnviarOrdenCompraDelegate.class);

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuditoriaCompraService auditoriaCompraService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("=== Iniciando envío de orden de compra ===");

        try {
            // Capturar usuario desde Camunda
            String usuario = (String) execution.getVariable("initiator");
            if (usuario == null || usuario.isEmpty()) {
                usuario = "SISTEMA";
            }
            logger.info("Usuario que envía orden: {}", usuario);

            String ordenId = (String) execution.getVariable("ordenId");

            // Validar que exista ordenId
            if (ordenId == null || ordenId.isEmpty()) {
                logger.error("❌ Variable 'ordenId' no encontrada o vacía");
                execution.setVariable("orden_enviada", false);
                execution.setVariable("error_envio", "OrderId no disponible");
                throw new IllegalArgumentException("Variable 'ordenId' es requerida");
            }

            logger.info("📮 Enviando orden de compra: {}", ordenId);

            // Cargar la orden
            OrdenCompra orden = ordenCompraRepository.findByOrdenId(ordenId);
            if (orden == null) {
                logger.error("❌ Orden no encontrada en BD: {}", ordenId);
                execution.setVariable("orden_enviada", false);
                execution.setVariable("error_envio", "Orden no encontrada");
                throw new IllegalArgumentException("Orden no encontrada: " + ordenId);
            }

            // Guardar estado anterior
            String estadoAnterior = orden.getEstado();

            // Actualizar estado y fecha
            orden.setEstado("ORDEN_ENVIADA");
            orden.setUsuarioModifica(usuario);
            orden.setFechaActualizacion(Date.from(LocalDateTime.now()
                    .atZone(ZoneId.systemDefault()).toInstant()));
            ordenCompraRepository.save(orden);

            logger.info("✅ Orden actualizada en BD");

            // Preparar información de la orden
            String proveedor = orden.getProveedor() != null ? orden.getProveedor() : "Proveedor";
            Integer cantidad = orden.getCantidadGas();
            Double costo = orden.getCostoTotal();
            String fechaEnvio = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            // Preparar cuerpo del correo
            String cuerpo = construirCuerpoCorreo(ordenId, proveedor, cantidad, costo, fechaEnvio);

            logger.info("📧 Enviando correo de orden a: {}", proveedor);

            // Enviar correo
            try {
                emailService.sendSimpleEmail(
                        "gascorocora@gmail.com",
                        "Compra Gas - Orden enviada: " + ordenId,
                        cuerpo
                );
                logger.info("✅ Correo enviado exitosamente");
            } catch (Exception e) {
                logger.error("⚠️ Error al enviar correo: {}", e.getMessage());
                // No lanzar excepción aquí, solo registrar el error
            }

            // Registrar variables de salida
            execution.setVariable("estado_orden", "ORDEN_ENVIADA");
            execution.setVariable("fecha_envio_orden", fechaEnvio);
            execution.setVariable("orden_enviada", true);
            execution.setVariable("usuario_envio", usuario);

            // Registrar en auditoría
            auditoriaCompraService.registrarAccion(
                ordenId,
                "ENVIADA",
                usuario,
                "Orden de compra enviada al proveedor: " + proveedor,
                estadoAnterior,
                "ORDEN_ENVIADA"
            );

            logger.info("✅ Orden enviada exitosamente");
            System.out.println("✅ ORDEN DE COMPRA ENVIADA: " + ordenId);
            execution.setVariable("estado_orden", "ORDEN_ENVIADA");
            execution.setVariable("fecha_envio_orden", fechaEnvio);
            execution.setVariable("orden_enviada", true);

            System.out.println("✅ ORDEN DE COMPRA ENVIADA");
            System.out.println("   ID: " + ordenId);
            System.out.println("   Proveedor: " + proveedor);
            System.out.println("   Cantidad: " + cantidad + " kg");
            System.out.println("   Costo: $" + costo);
            System.out.println("   Fecha: " + fechaEnvio);

        } catch (IllegalArgumentException e) {
            logger.error("❌ Error de validación: {}", e.getMessage());
            execution.setVariable("orden_enviada", false);
            throw e;
        } catch (Exception e) {
            logger.error("❌ Error inesperado al enviar orden: {}", e.getMessage(), e);
            execution.setVariable("orden_enviada", false);
            execution.setVariable("error_envio", e.getMessage());
            throw new RuntimeException("Error enviando orden de compra", e);
        }
    }

    private String construirCuerpoCorreo(String ordenId, String proveedor, 
                                         Integer cantidad, Double costo, String fecha) {
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append("╔═══════════════════════════════════════════════════╗\n");
        cuerpo.append("║         ✅ ORDEN DE COMPRA ENVIADA                ║\n");
        cuerpo.append("╚═══════════════════════════════════════════════════╝\n\n");

        cuerpo.append("Estimado ").append(proveedor).append(",\n\n");
        cuerpo.append("Le informamos que su orden de compra ha sido procesada.\n\n");

        cuerpo.append("───────────────────────────────────────────────────\n");
        cuerpo.append("DETALLES DE LA ORDEN:\n");
        cuerpo.append("───────────────────────────────────────────────────\n");
        cuerpo.append("ID Orden: ").append(ordenId).append("\n");
        cuerpo.append("Fecha: ").append(fecha).append("\n");
        if (cantidad != null) {
            cuerpo.append("Cantidad: ").append(cantidad).append(" kg\n");
        }
        if (costo != null) {
            cuerpo.append("Monto: $").append(costo).append("\n");
        }

        cuerpo.append("\n───────────────────────────────────────────────────\n");
        cuerpo.append("PRÓXIMOS PASOS:\n");
        cuerpo.append("───────────────────────────────────────────────────\n");
        cuerpo.append("1. Confirmar recepción de esta orden\n");
        cuerpo.append("2. Coordinar entrega\n");
        cuerpo.append("3. Enviar factura al departamento de compras\n\n");

        cuerpo.append("Para consultas, contacte a: gascorocora@gmail.com\n\n");

        cuerpo.append("═══════════════════════════════════════════════════\n");
        cuerpo.append("       Sistema Automático de Gestión de Compras    \n");
        cuerpo.append("═══════════════════════════════════════════════════\n");

        return cuerpo.toString();
    }
}