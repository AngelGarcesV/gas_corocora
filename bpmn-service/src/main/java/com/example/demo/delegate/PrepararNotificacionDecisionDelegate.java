package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.context.Context;
import org.springframework.stereotype.Component;

/**
 * Delegado: Prepara datos para el formulario de notificación de decisión
 * Copia datos de la solicitud anterior a las variables del formulario
 */
@Component("prepararNotificacionDecisionDelegate")
public class PrepararNotificacionDecisionDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("\n=== PREPARANDO DATOS PARA NOTIFICACIÓN DE DECISIÓN ===");

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
            String usuarioSolicitante = (String) execution.getVariable("usuario_solicitante");
            Double costoTotal = (Double) execution.getVariable("costo_total");

            System.out.println("✓ Usuario aprobador: " + usuario);
            System.out.println("✓ Usuario solicitante: " + usuarioSolicitante);
            System.out.println("✓ Costo total: " + costoTotal);

            // Pre-llenar variables para el formulario (usando las keys correctas del formulario)
            execution.setVariable("usuario_aprobador", usuario);
            execution.setVariable("costo_total", costoTotal != null ? costoTotal : 0.0);

            System.out.println("✅ Datos pre-llenados correctamente\n");

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace(System.out);
            throw e;
        }
    }
}
