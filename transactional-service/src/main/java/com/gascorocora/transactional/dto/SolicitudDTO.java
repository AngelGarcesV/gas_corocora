package com.gascorocora.transactional.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDTO {
    private String businessKey;
    private String nombreCliente;
    private String cedulaCliente;
    private String direccion;
    private String telefono;
    private String email;
    private Integer estrato;
}
