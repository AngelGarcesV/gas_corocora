package com.gascorocora.transactional.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoberturaResponse {
    private boolean tieneCobiertura;
    private String mensaje;
}
