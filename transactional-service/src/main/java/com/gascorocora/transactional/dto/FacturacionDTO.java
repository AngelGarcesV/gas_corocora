package com.gascorocora.transactional.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturacionDTO {
    private String businessKey;
    private String cedulaCliente;
    private Double montoRecibido;
    private String metodoPago;
    private String numeroTransaccion;
    private LocalDateTime fechaPago;
    private String comprobanteUrl;
    private String observacionesPago;
    private String numeroServicio;
    private LocalDateTime fechaActivacion;
    private String estado;
}
