package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service Task: Enviar orden de compra al proveedor
 * Reemplaza la tarea manual "Enviar orden de compra"
 *
 * Variables de entrada esperadas:
 * - proveedor_seleccionado (String)
 * - cantidad_gas (Integer)
 * - costo_total (Double)
 * - numero_orden (String)
 * - tiempo_entrega_dias (Integer)
 */
@Component("enviarOrdenCompraDelegate")
public class EnviarOrdenCompraDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Obtener información de la orden
        String numeroOrden = (String) execution.getVariable("numero_orden");
        String proveedor = (String) execution.getVariable("proveedor_seleccionado");
        Integer cantidadGas = (Integer) execution.getVariable("cantidad_gas");
        Number costoTotal = (Number) execution.getVariable("costo_total");
        Integer tiempoEntrega = (Integer) execution.getVariable("tiempo_entrega_dias");

        // Generar contenido de la orden
        String ordenCompra = generarOrdenCompra(numeroOrden, proveedor, cantidadGas,
                costoTotal, tiempoEntrega);

        // Simular envío al proveedor (aquí podrías integrar con email, API, etc.)
        boolean envioExitoso = enviarAlProveedor(proveedor, ordenCompra);

        if (envioExitoso) {
            System.out.println("✅ Orden enviada exitosamente al proveedor: " + proveedor);

            // Guardar información del envío
            execution.setVariable("orden_enviada", true);
            execution.setVariable("fecha_envio_orden", LocalDateTime.now().toString());
            execution.setVariable("estado_orden", "ENVIADA");
            execution.setVariable("contenido_orden", ordenCompra);
        } else {
            System.err.println("❌ Error al enviar orden al proveedor");
            execution.setVariable("orden_enviada", false);
            execution.setVariable("error_envio", "No se pudo contactar al proveedor");
        }
    }

    private String generarOrdenCompra(String numeroOrden, String proveedor,
                                      Integer cantidad, Number costo, Integer tiempoEntrega) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaActual = LocalDateTime.now().format(formatter);

        StringBuilder orden = new StringBuilder();
        orden.append("═══════════════════════════════════════════════════\n");
        orden.append("           ORDEN DE COMPRA DE GAS NATURAL          \n");
        orden.append("═══════════════════════════════════════════════════\n\n");
        orden.append("Número de Orden: ").append(numeroOrden).append("\n");
        orden.append("Fecha de Emisión: ").append(fechaActual).append("\n");
        orden.append("Proveedor: ").append(proveedor).append("\n\n");
        orden.append("---------------------------------------------------\n");
        orden.append("DETALLES DEL PEDIDO:\n");
        orden.append("---------------------------------------------------\n");
        orden.append("Producto: Gas Natural\n");
        orden.append("Cantidad: ").append(cantidad).append(" kg\n");
        orden.append("Costo Total: $").append(costo).append("\n");
        orden.append("Tiempo de Entrega Esperado: ").append(tiempoEntrega).append(" días\n\n");
        orden.append("---------------------------------------------------\n");
        orden.append("INSTRUCCIONES DE ENTREGA:\n");
        orden.append("---------------------------------------------------\n");
        orden.append("- Coordinar entrega con anticipación\n");
        orden.append("- Presentar documentación de calidad\n");
        orden.append("- Incluir certificados de seguridad\n");
        orden.append("- Factura debe coincidir con esta orden\n\n");
        orden.append("═══════════════════════════════════════════════════\n");
        orden.append("    Sistema Automático de Gestión de Compras       \n");
        orden.append("═══════════════════════════════════════════════════\n");

        return orden.toString();
    }

    private boolean enviarAlProveedor(String proveedor, String ordenCompra) {
        // Aquí irían las integraciones reales:
        // - Envío por email
        // - API REST del proveedor
        // - Sistema EDI
        // - Generación de PDF

        // Por ahora, simulamos el envío imprimiendo en consola
        System.out.println("\n📤 ========== ENVIANDO ORDEN A PROVEEDOR ==========");
        System.out.println("Destinatario: " + proveedor);
        System.out.println("Método: Email/API (simulado)");
        System.out.println("\nContenido de la orden:\n");
        System.out.println(ordenCompra);
        System.out.println("====================================================\n");

        // Simular éxito (en producción, verificar respuesta real)
        return true;
    }
}
