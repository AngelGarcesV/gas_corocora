// bpmn-service/src/main/java/com/example/demo/delegate/RegistrarRecepcionPedidoDelegate.java
package com.example.demo.delegate;

import com.example.demo.service.OrdenCompraService;
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
 * Service Task: Registrar recepción de pedido
 * Se ejecuta cuando se recibe el pedido del proveedor
 *
 * Variables de entrada:
 * - ordenId (String)
 * - cantidad_recibida (Integer) - opcional
 * - fecha_recepcion (String) - opcional
 * - initiator (String): usuario de Camunda
 *
 * Variables de salida:
 * - estado_orden (String): "PEDIDO_RECIBIDO"
 * - fecha_registro_recepcion (String)
 * - pedido_registrado (Boolean)
 */
@Component("registrarRecepcionPedidoDelegate")
public class RegistrarRecepcionPedidoDelegate implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(RegistrarRecepcionPedidoDelegate.class);

    @Autowired
    private OrdenCompraService ordenCompraService;

    @Autowired
    private AuditoriaCompraService auditoriaCompraService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("=== Iniciando registro de recepción de pedido ===");

        try {
            // Capturar usuario desde Camunda
            String usuario = (String) execution.getVariable("initiator");
            if (usuario == null || usuario.isEmpty()) {
                usuario = "SISTEMA";
            }
            logger.info("Usuario que registra recepción: {}", usuario);

            String ordenId = (String) execution.getVariable("ordenId");
            // También intentar obtener numero_orden como fallback
            if (ordenId == null || ordenId.isEmpty()) {
                ordenId = (String) execution.getVariable("numero_orden");
            }

            // Validar que exista ordenId
            if (ordenId == null || ordenId.isEmpty()) {
                logger.warn("⚠️ Variable 'ordenId' no encontrada, usando timestamp como ID");
                ordenId = "ORD_" + System.currentTimeMillis();
            }

            // Obtener datos opcionales
            Integer cantidadRecibida = (Integer) execution.getVariable("cantidad_recibida");

            logger.info("📦 Registrando recepción para orden: {}", ordenId);
            if (cantidadRecibida != null) {
                logger.info("   Cantidad recibida: {} kg", cantidadRecibida);
            }

            // Actualizar estado en base de datos
            String estadoAnterior = "ORDEN_ENVIADA";
            ordenCompraService.actualizarEstado(ordenId, "PEDIDO_RECIBIDO");

            // Registrar en variables del proceso
            String fechaRegistro = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            execution.setVariable("estado_orden", "PEDIDO_RECIBIDO");
            execution.setVariable("fecha_registro_recepcion", fechaRegistro);
            execution.setVariable("pedido_registrado", true);

            // Registrar en auditoría
            auditoriaCompraService.registrarAccion(
                ordenId,
                "RECIBIDA",
                usuario,
                "Pedido recibido del proveedor",
                estadoAnterior,
                "PEDIDO_RECIBIDO"
            );

            logger.info("✅ Pedido registrado exitosamente por: {}", usuario);
            System.out.println("✅ Recepción de pedido registrada: " + ordenId + " - " + fechaRegistro);

        } catch (IllegalArgumentException e) {
            logger.error("❌ Error de validación: {}", e.getMessage());
            execution.setVariable("pedido_registrado", false);
            execution.setVariable("error_registro", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Error al registrar recepción de pedido: {}", e.getMessage(), e);
            execution.setVariable("pedido_registrado", false);
            execution.setVariable("error_registro", e.getMessage());
            throw new RuntimeException("Error registrando recepción de pedido", e);
        }
    }
}