package com.gascorocora.transactional.service;

import com.gascorocora.transactional.dto.CoberturaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class CoberturaService {

    // Direcciones simuladas con cobertura
    private static final List<String> ZONAS_CON_COBERTURA = Arrays.asList(
            "CENTRO", "NORTE", "SUR", "ORIENTE", "OCCIDENTE",
            "CALLE", "CARRERA", "AVENIDA"
    );

    public CoberturaResponse verificarCobertura(String direccion) {
        log.info("Verificando cobertura para dirección: {}", direccion);
        
        String direccionUpper = direccion.toUpperCase();
        boolean tieneCobiertura = ZONAS_CON_COBERTURA.stream()
                .anyMatch(direccionUpper::contains);
        
        String mensaje = tieneCobiertura 
                ? "La dirección tiene cobertura de servicio de gas natural"
                : "La dirección NO tiene cobertura de servicio de gas natural";
        
        log.info("Resultado verificación cobertura: {}", tieneCobiertura);
        return new CoberturaResponse(tieneCobiertura, mensaje);
    }
}
