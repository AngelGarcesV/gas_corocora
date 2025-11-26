package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class NotificarClienteDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        // Obtener datos clave de la factura
        Double montoTotal = (Double) execution.getVariable("montoTotal");
        String direccion = (String) execution.getVariable("direccion"); // Asumiendo que esta variable existe
        
        // Simulación de la lógica de envío (Aquí iría la llamada a un servicio de Email o API externa)
        System.out.println("==================================================");
        System.out.println(">>> SIMULACIÓN DE ENVÍO DE FACTURA");
        System.out.println("  Destinatario: " + direccion);
        System.out.println("  Monto Total: $" + String.format("%.2f", montoTotal));
        System.out.println("  Factura enviada por email/SMS al cliente.");
        System.out.println("==================================================");

        // Se puede establecer una variable de confirmación
        execution.setVariable("facturaEnviada", true);
    }
}
