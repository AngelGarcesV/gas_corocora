// bpmn-service/src/main/java/com/example/demo/delegate/IniciarOrdenCompraDelegate.java
package com.example.demo.delegate;

import com.example.demo.model.OrdenCompra;
import com.example.demo.service.OrdenCompraService;
import com.example.demo.service.AuditoriaCompraService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service Task: Crear orden de compra
 * Se ejecuta cuando se evalúa la necesidad y se aprueba la compra
 *
 * Variables de entrada:
 * - cantidad_estimada (Integer)
 * - fecha_requerido (String)
 * - justificacion_necesidad (String)
 * - observaciones (String) - opcional
 * - necesidad_detectada (String)
 * - initiator (String): usuario de Camunda que inicia el proceso
 *
 * Variables de salida:
 * - ordenId (String): ID único de la orden generado
 * - estado_orden (String): "NECESIDAD_EVALUADA"
 * - orden_creada (Boolean)
 * - fecha_creacion_orden (String)
 * - usuario_solicitante (String): usuario que creó la orden
 */
@Component("iniciarOrdenCompraDelegate")
public class IniciarOrdenCompraDelegate implements JavaDelegate {

    @Autowired
    private OrdenCompraService ordenCompraService;

    @Autowired
    private AuditoriaCompraService auditoriaCompraService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("\n========== INICIANDO CREACIÓN DE ORDEN DE COMPRA ==========");
        
        try {
            // Capturar usuario autenticado de Camunda
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
                System.out.println("⚠ No se pudo obtener usuario autenticado");
            }
            
            if (usuario == null || usuario.isEmpty()) {
                usuario = "demo";
            }
            System.out.println("✓ Usuario: " + usuario);

            // Obtener variables del formulario EvaluarNecesidad
            // Convertir cantidad a Integer desde cualquier tipo numérico
            Object cantidadObj = execution.getVariable("cantidad_estimada");
            Integer cantidadEstimada = null;
            
            if (cantidadObj != null) {
                if (cantidadObj instanceof Integer) {
                    cantidadEstimada = (Integer) cantidadObj;
                } else if (cantidadObj instanceof Long) {
                    cantidadEstimada = ((Long) cantidadObj).intValue();
                } else if (cantidadObj instanceof String) {
                    try {
                        cantidadEstimada = Integer.parseInt((String) cantidadObj);
                    } catch (NumberFormatException e) {
                        System.out.println("⚠ No se pudo parsear cantidad_estimada como Integer: " + cantidadObj);
                    }
                } else if (cantidadObj instanceof Double) {
                    cantidadEstimada = ((Double) cantidadObj).intValue();
                }
            }
            
            String fechaRequerido = (String) execution.getVariable("fecha_requerido");
            String justificacionNecesidad = (String) execution.getVariable("justificacion_necesidad");
            String observaciones = (String) execution.getVariable("observaciones");
            String necesidadDetectada = (String) execution.getVariable("necesidad_detectada");

            System.out.println("✓ Cantidad estimada (objeto original): " + cantidadObj + " (" + (cantidadObj != null ? cantidadObj.getClass().getSimpleName() : "null") + ")");
            System.out.println("✓ Cantidad estimada (convertida): " + cantidadEstimada);
            System.out.println("✓ Fecha requerido: " + fechaRequerido);
            System.out.println("✓ Justificación: " + justificacionNecesidad);
            System.out.println("✓ Necesidad detectada: " + necesidadDetectada);

            // Validar datos requeridos
            if (cantidadEstimada == null || cantidadEstimada <= 0) {
                System.out.println("❌ VALIDACIÓN FALLIDA: Cantidad estimada inválida");
                System.out.println("   Valor recibido: " + cantidadObj + " (tipo: " + (cantidadObj != null ? cantidadObj.getClass().getName() : "null") + ")");
                throw new IllegalArgumentException("Cantidad estimada debe ser mayor a 0. Recibido: " + cantidadObj);
            }

            if (justificacionNecesidad == null || justificacionNecesidad.isEmpty()) {
                System.out.println("❌ VALIDACIÓN FALLIDA: Justificación vacía");
                throw new IllegalArgumentException("Justificación de necesidad es requerida");
            }

            System.out.println("\n→ Llamando a ordenCompraService.crearOrdenDesdeNecesidad()...");
            
            // Crear orden en base de datos
            OrdenCompra orden = ordenCompraService.crearOrdenDesdeNecesidad(
                    cantidadEstimada, fechaRequerido, justificacionNecesidad,
                    observaciones, necesidadDetectada
            );

            // Validar que se creó la orden
            if (orden == null) {
                System.out.println("❌ ERROR: ordenCompraService retornó NULL");
                throw new RuntimeException("El servicio retornó NULL");
            }

            if (orden.getOrdenId() == null) {
                System.out.println("❌ ERROR: OrdenId es NULL");
                throw new RuntimeException("OrdenId no fue generado");
            }

            // Guardar ID y estado en variables del proceso
            String ordenId = orden.getOrdenId();
            String estado = orden.getEstado();
            String fechaCreacion = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            System.out.println("✓ Orden creada en BD: " + ordenId);
            System.out.println("✓ Estado: " + estado);

            // Actualizar usuario solicitante
            orden.setUsuarioSolicitante(usuario);
            orden.setDepartamentoSolicitante("Compras");
            ordenCompraService.actualizarOrden(orden);
            System.out.println("✓ Usuario solicitante actualizado: " + usuario);

            // Guardar variables para el proceso
            execution.setVariable("ordenId", ordenId);
            execution.setVariable("estado_orden", estado);
            execution.setVariable("orden_creada", true);
            execution.setVariable("fecha_creacion_orden", fechaCreacion);
            execution.setVariable("usuario_solicitante", usuario);
            System.out.println("✓ Variables del proceso establecidas");

            // Registrar en auditoría
            auditoriaCompraService.registrarAccion(
                ordenId,
                "CREADA",
                usuario,
                "Orden de compra creada desde evaluación de necesidad",
                null,
                "NECESIDAD_EVALUADA"
            );
            System.out.println("✓ Auditoría registrada");

            System.out.println("\n✅ ORDEN DE COMPRA CREADA EXITOSAMENTE");
            System.out.println("   ID: " + ordenId);
            System.out.println("   Cantidad: " + cantidadEstimada + " kg");
            System.out.println("   Fecha: " + fechaCreacion);
            System.out.println("========================================================\n");

        } catch (Exception e) {
            System.out.println("\n❌ EXCEPCIÓN EN DELEGADO:");
            System.out.println("   Tipo: " + e.getClass().getName());
            System.out.println("   Mensaje: " + e.getMessage());
            e.printStackTrace(System.out);
            System.out.println("========================================================\n");
            
            execution.setVariable("orden_creada", false);
            execution.setVariable("error_orden", e.getMessage());
            throw new RuntimeException("Error al crear orden de compra: " + e.getMessage(), e);
        }
    }
}
