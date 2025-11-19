package com.example.demo.delegate; // Usando tu paquete original. Se recomienda moverlo a com.gascorocora.delegate

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class CalcularFacturaDelegate implements JavaDelegate {

    // Contribución para estratos 5 y 6 (ejemplo 20%). 
    // Nota: Esta constante debe reflejar el valor REAL de la contribución.
    private static final double CONTRIBUCION_PORCENTAJE = 0.20; 

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        // 1. OBTENER VARIABLES
        // 'subtotal' viene de la clase CalcularCostoBaseDelegate.
        Double subtotal = (Double) execution.getVariable("subtotal"); 
        
        // Variables obtenidas de la Tarea de Regla de Negocio (DMN)
        Double porcentajeSubsidio = (Double) execution.getVariable("PorcentajeSubsidio"); 
        Boolean aplicaContribucion = (Boolean) execution.getVariable("ContribucionAplicada"); 
        
        // Variables de salida a calcular
        double valorContribucion = 0.0;
        double valorSubsidioAplicado = 0.0;
        double montoTotal;

        if (subtotal == null) {
            throw new RuntimeException("Error: 'subtotal' es nulo. Verifique la ejecución de la tarea 'Calcular Costo Base'.");
        }
        
        // 2. APLICACIÓN DE AJUSTES (Subsidio o Contribución)
        
        if (aplicaContribucion != null && aplicaContribucion.booleanValue()) {
            // Caso 1: Aplica Contribución (Estrato 5 o 6)
            valorContribucion = subtotal * CONTRIBUCION_PORCENTAJE;
            montoTotal = subtotal + valorContribucion;
            porcentajeSubsidio = 0.0; // Garantizamos que no hay subsidio
        
        } else if (porcentajeSubsidio != null && porcentajeSubsidio > 0.0) {
            // Caso 2: Aplica Subsidio (Estrato 1, 2, o 3)
            valorSubsidioAplicado = subtotal * porcentajeSubsidio;
            montoTotal = subtotal - valorSubsidioAplicado;
        
        } else {
            // Caso 3: No aplica subsidio ni contribución (Estrato 4)
            montoTotal = subtotal;
            porcentajeSubsidio = 0.0;
        }

        // 3. GUARDAR VARIABLES DE SALIDA EN EL PROCESO (para el formulario de factura)
        // Ya no guardamos el subtotal aquí, solo lo usamos.
        execution.setVariable("subsidioPorcentaje", porcentajeSubsidio); 
        execution.setVariable("valorContribucion", valorContribucion); 
        execution.setVariable("valorSubsidioAplicado", valorSubsidioAplicado);
        execution.setVariable("montoTotal", montoTotal); // El valor final a pagar
        
        System.out.println("Subtotal: " + String.format("%.2f", subtotal));
        System.out.println("Subsidio aplicado: " + String.format("%.2f", valorSubsidioAplicado));
        System.out.println("Contribución aplicada: " + String.format("%.2f", valorContribucion));
        System.out.println(">>> MONTO TOTAL FACTURA: " + String.format("%.2f", montoTotal));
    }
}