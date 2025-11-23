package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Clase pública con método público para ser llamado mediante expresión desde Camunda
 * Ejemplo en BPMN: camunda:expression="${calculadoraService.calcularDescuento(estrato, conexion)}"
 */
@Component("calculadoraService")
@Slf4j
public class CalculadoraService {

    /**
     * Calcula el descuento basado en el estrato y el monto de conexión
     * Este método será llamado directamente desde el BPMN usando expresiones
     */
    public double calcularDescuento(Integer estrato, Double conexion) {
        log.info("Calculando descuento para estrato {} y conexión {}", estrato, conexion);
        
        if (conexion == null || estrato == null) {
            return 0.0;
        }
        
        // Lógica de negocio: estratos bajos reciben mayor descuento
        double porcentajeDescuento = switch (estrato) {
            case 1 -> 0.40; // 40% de descuento
            case 2 -> 0.30; // 30% de descuento
            case 3 -> 0.20; // 20% de descuento
            case 4 -> 0.10; // 10% de descuento
            default -> 0.0; // Sin descuento
        };
        
        double descuento = conexion * porcentajeDescuento;
        log.info("Descuento calculado: {} ({}%)", descuento, porcentajeDescuento * 100);
        
        return Math.round(descuento * 100.0) / 100.0; // Redondear a 2 decimales
    }

    /**
     * Calcula el monto final a pagar después del descuento
     */
    public double calcularMontoFinal(Double conexion, Double descuento) {
        if (conexion == null) return 0.0;
        if (descuento == null) descuento = 0.0;
        
        return Math.round((conexion - descuento) * 100.0) / 100.0;
    }

    /**
     * Realiza cálculo aritmético básico
     * Ejemplo: camunda:expression="${calculadoraService.sumar(monto1, monto2)}"
     */
    public double sumar(Double a, Double b) {
        if (a == null) a = 0.0;
        if (b == null) b = 0.0;
        return a + b;
    }

    public double restar(Double a, Double b) {
        if (a == null) a = 0.0;
        if (b == null) b = 0.0;
        return a - b;
    }

    public double multiplicar(Double a, Double b) {
        if (a == null) a = 1.0;
        if (b == null) b = 1.0;
        return a * b;
    }
}
