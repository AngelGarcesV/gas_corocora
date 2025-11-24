package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Service Task: Registrar recepción de pedido
 * Primera tarea del subproceso "Recibir pedido"
 */
@Component("registrarRecepcionPedidoDelegate")
public class RegistrarRecepcionPedidoDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Capturar momento exacto de recepción
        LocalDateTime fechaRecepcion = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Obtener información del pedido
        String numeroOrden = (String) execution.getVariable("numero_orden");
        String proveedor = (String) execution.getVariable("proveedor_seleccionado");
        Integer cantidadGas = (Integer) execution.getVariable("cantidad_gas");
        Number costoTotal = (Number) execution.getVariable("costo_total");

        // Calcular días transcurridos desde el envío
        Long diasDesdeEnvio = null;
        String fechaEnvioStr = (String) execution.getVariable("fecha_envio_orden");

        if (fechaEnvioStr != null) {
            try {
                LocalDateTime fechaEnvio = LocalDateTime.parse(fechaEnvioStr);
                diasDesdeEnvio = ChronoUnit.DAYS.between(fechaEnvio, fechaRecepcion);
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo calcular días desde envío: " + e.getMessage());
            }
        }

        // Generar reporte de recepción
        System.out.println("\n📦 ========== REGISTRO DE RECEPCIÓN ==========");
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║      PEDIDO RECIBIDO - REGISTRO INICIAL    ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Fecha y Hora: " + fechaRecepcion.format(formatter));
        System.out.println("Número de Orden: " + numeroOrden);
        System.out.println("Proveedor: " + proveedor);
        System.out.println("Cantidad Esperada: " + cantidadGas + " kg");
        System.out.println("Costo Total: $" + costoTotal);

        if (diasDesdeEnvio != null) {
            System.out.println("Días desde envío: " + diasDesdeEnvio + " días");
        }

        System.out.println();
        System.out.println("Estado: ✅ Pedido recibido - Pendiente verificación");
        System.out.println("Siguiente paso: Verificar cantidad y calidad");
        System.out.println("==============================================\n");

        // Guardar variables del proceso
        execution.setVariable("fecha_recepcion", fechaRecepcion.toString());
        execution.setVariable("hora_recepcion", fechaRecepcion.format(formatter));
        execution.setVariable("estado_recepcion", "RECIBIDO");
        execution.setVariable("dias_desde_envio", diasDesdeEnvio);
        execution.setVariable("timestamp_recepcion", fechaRecepcion.toEpochSecond(
                java.time.ZoneOffset.UTC));

        // Inicializar variables para el subproceso
        execution.setVariable("pedido_verificado", false);
        execution.setVariable("verificacion_completada", false);
    }
}
