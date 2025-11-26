package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("calcularFacturaSinAjustes")
public class CalcularFacturaSinAjustes implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        Double subtotal = (Double) execution.getVariable("subtotal");

        if (subtotal == null) {
            throw new RuntimeException("Error: 'subtotal' es nulo.");
        }

        double montoTotal = subtotal;

        execution.setVariable("valorSubsidioAplicado", 0.0);
        execution.setVariable("valorContribucion", 0.0);
        execution.setVariable("montoTotal", montoTotal);
    }
}
