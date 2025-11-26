package com.example.demo.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class EstimarConsumo implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        // Asumimos que lecturaAnterior se carga al inicio del proceso y lecturaActual viene del formulario
        Integer lecturaActual = (Integer) execution.getVariable("lecturaActual");
        
        // *** IMPORTANTE: Si estás en la ruta de ajuste manual, el valor de la lectura puede venir de otra variable (ej. valorLectura del ajusteManual.form).
        // Si no se encuentra lecturaActual, intentamos obtenerla de valorLectura (del ajusteManual.form)
        if (lecturaActual == null) {
            lecturaActual = (Integer) execution.getVariable("valorLectura");
        }
        
        // Asumimos que la lectura anterior es una variable de proceso.
        Integer lecturaAnterior = (Integer) execution.getVariable("lecturaAnterior"); 
        
        // Valor quemado para pruebas, ya que no tenemos lecturaAnterior
        if (lecturaAnterior == null) {
             lecturaAnterior = 100; // Valor inicial solo para que compile/pruebe
        }

        int montoTotal = lecturaActual - lecturaAnterior;

        // Guardar el resultado para usarlo en la siguiente tarea (el DMN de subsidios)
        execution.setVariable("consumoFinal", montoTotal);
        
        System.out.println("Consumo final calculado: " + montoTotal + " m³");
    }
}
