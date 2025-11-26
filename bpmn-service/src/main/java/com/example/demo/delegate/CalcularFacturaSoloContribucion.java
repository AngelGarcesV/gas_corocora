package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("calcularFacturaSoloContribucion")
public class CalcularFacturaSoloContribucion implements JavaDelegate {

    private static final double PORCENTAJE_CONTRIBUCION = 0.20;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        Double subtotal = (Double) execution.getVariable("subtotal");
        Boolean contribucionAplicada = (Boolean) execution.getVariable("contribucionAplicada");

        if (subtotal == null) {
            throw new RuntimeException("Error: 'subtotal' es nulo.");
        }

        double valorContribucion = 0.0;
        double montoTotal = subtotal;

        if (contribucionAplicada != null && contribucionAplicada) {
            valorContribucion = subtotal * PORCENTAJE_CONTRIBUCION;
            montoTotal = subtotal + valorContribucion;
        }

        execution.setVariable("valorContribucion", valorContribucion);
        execution.setVariable("valorSubsidioAplicado", 0.0);
        execution.setVariable("montoTotal", montoTotal);
    }
}
