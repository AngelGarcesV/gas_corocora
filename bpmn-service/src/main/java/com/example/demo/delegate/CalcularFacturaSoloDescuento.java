package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("calcularFacturaSoloDescuento")
public class CalcularFacturaSoloDescuento implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        // Obtener subtotal y porcentaje de subsidio
        Double subtotal = (Double) execution.getVariable("subtotal");
        Double porcentajeSubsidio = (Double) execution.getVariable("porcentajeSubsidio");

        if (subtotal == null) {
            throw new RuntimeException("Error: 'subtotal' es nulo.");
        }

        double valorSubsidioAplicado = 0.0;
        double montoTotal = subtotal;

        // Aplicar subsidio si existe
        if (porcentajeSubsidio != null && porcentajeSubsidio > 0.0) {
            valorSubsidioAplicado = subtotal * porcentajeSubsidio;
            montoTotal = subtotal - valorSubsidioAplicado;
        }

        // Guardar variables
        execution.setVariable("valorSubsidioAplicado", valorSubsidioAplicado);
        execution.setVariable("valorContribucion", 0.0);
        execution.setVariable("montoTotal", montoTotal);
    }
}
