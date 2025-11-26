package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.context.Context;
import org.springframework.stereotype.Component;

/**
 * Delegado: Prepara datos para el formulario de validación de factura
 * Copia datos de la solicitud anterior a las variables del formulario
 */
@Component("prepararValidacionFacturaDelegate")
public class PrepararValidacionFacturaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("\n=== PREPARANDO DATOS PARA VALIDACIÓN DE FACTURA ===");

        try {
            // Capturar usuario autenticado
            String usuario = null;
            try {
                if (Context.getProcessEngineConfiguration() != null && 
                    Context.getProcessEngineConfiguration().getIdentityService() != null &&
                    Context.getProcessEngineConfiguration().getIdentityService().getCurrentAuthentication() != null) {
                    usuario = Context.getProcessEngineConfiguration()
                        .getIdentityService()
                        .getCurrentAuthentication()
                        .getUserId();
                }
            } catch (Exception e) {
                System.out.println("⚠ No se pudo obtener usuario de Camunda");
            }
            if (usuario == null || usuario.isEmpty()) {
                usuario = "demo";
            }

            // Copiar datos de la solicitud anterior
            String proveedorSeleccionado = (String) execution.getVariable("proveedor_seleccionado");
            Double costoTotal = (Double) execution.getVariable("costo_total");

            System.out.println("✓ Usuario validador: " + usuario);
            System.out.println("✓ Proveedor: " + proveedorSeleccionado);
            System.out.println("✓ Costo total: " + costoTotal);

            // Pre-llenar variables para el formulario
            execution.setVariable("usuario_validador", usuario);
            execution.setVariable("proveedor_factura", proveedorSeleccionado);
            execution.setVariable("monto_esperado", costoTotal != null ? costoTotal : 0.0);

            System.out.println("✅ Datos pre-llenados correctamente\n");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace(System.out);
            throw e;
        }
    }
}
