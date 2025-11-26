package com.gascorocora.transactional.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequest {
    private String destinatario;
    private String asunto;
    private String mensaje;
    private String tipo; // EMAIL, SMS
}
