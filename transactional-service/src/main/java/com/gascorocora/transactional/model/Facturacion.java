package com.gascorocora.transactional.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "facturacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Facturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_key", nullable = false, unique = true)
    private String businessKey;

    @Column(name = "cedula_cliente", nullable = false)
    private String cedulaCliente;

    @Column(name = "monto_recibido")
    private Double montoRecibido;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "numero_transaccion")
    private String numeroTransaccion;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "comprobante_url")
    private String comprobanteUrl;

    @Column(name = "observaciones_pago")
    private String observacionesPago;

    @Column(name = "numero_servicio", unique = true)
    private String numeroServicio;

    @Column(name = "fecha_activacion")
    private LocalDateTime fechaActivacion;

    @Column(name = "estado")
    private String estado; // PENDIENTE, PAGADO, ACTIVADO

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        if (estado == null) {
            estado = "PENDIENTE";
        }
    }
}
