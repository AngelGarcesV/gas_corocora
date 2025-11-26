

package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class CalcularCostoBaseDelegate implements JavaDelegate {

    // =========================================================================
    // !!! CONSTANTES CRÍTICAS: CONFIRMAR LOS VALORES REALES DE TU PROVEEDOR !!!
    // =========================================================================
    // Factor de Conversión: m³ (Volumen) a kWh (Energía)
    private static final double FACTOR_CONVERSION_M3_A_KWH = 11.6; 
    
    // Precio unitario por kWh (Tarifa base)
    // Usa el precio base por unidad de energía. (ej: $52.70 por kWh)
    private static final double PRECIO_POR_KWH = 52.70; 
    // =========================================================================

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        // Obtener el Consumo Final en m³ (calculado por CalcularConsumoDelegate)
        Integer consumoFinal = (Integer) execution.getVariable("lecturaActual");

        if (consumoFinal == null) {
            throw new RuntimeException("Error de cálculo: La variable 'consumoFinal' (m³) es nula. Asegúrese de ejecutar 'Calcular Consumo' primero.");
        }

        // 1. CONVERSIÓN DE CONSUMO (m³ a kWh)
        // Término: Consumo (m³) x Factor de Conversión
        double consumoKWh = consumoFinal * FACTOR_CONVERSION_M3_A_KWH;

        // 2. CÁLCULO DEL SUBTOTAL (Costo Base antes de Subsidios)
        // Subtotal: kWh x Precio del kWh
        double subtotal = consumoKWh * PRECIO_POR_KWH; 
        
        // 3. GUARDAR VARIABLES DE SALIDA EN EL PROCESO
        // Estas variables serán usadas por el DMN y la clase de aplicación de subsidios/contribuciones.
        execution.setVariable("consumoKWh", consumoKWh); // Consumo convertido
        execution.setVariable("subtotal", subtotal);     // Costo base, la referencia para aplicar descuentos/recargos
        
        System.out.println("Consumo en m³: " + consumoFinal);
        System.out.println("Consumo en kWh: " + String.format("%.2f", consumoKWh));
        System.out.println(">>> Subtotal (Costo Base): " + String.format("%.2f", subtotal));
    }
}
