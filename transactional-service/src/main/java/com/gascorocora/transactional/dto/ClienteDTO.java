package com.gascorocora.transactional.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private String cedula;
    private String nombre;
    private String apellido;
    private String direccion;
    private String ciudad;
    private Integer estrato;
    private String telefono;
    private String email;
}
