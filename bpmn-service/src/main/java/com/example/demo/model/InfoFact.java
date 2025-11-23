package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "info_fact")
public class InfoFact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(name = "monto_conexion", precision = 10, scale = 2)
    private BigDecimal montoConexion;
    
    @Column(name = "descuento_aplicado", precision = 5, scale = 2)
    private BigDecimal descuentoAplicado;
    
    @Column(name = "total_pagado", precision = 10, scale = 2)
    private BigDecimal totalPagado;
    
    @Column(name = "tipo_instalacion", length = 50)
    private String tipoInstalacion;
    
    @Column(name = "fecha_cotizacion")
    private LocalDateTime fechaCotizacion;
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(name = "fecha_instalacion")
    private LocalDateTime fechaInstalacion;
    
    @Column(name = "tecnico_asignado", length = 100)
    private String tecnicoAsignado;
    
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    // Constructores
    public InfoFact() {
        this.fechaRegistro = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public BigDecimal getMontoConexion() {
        return montoConexion;
    }
    
    public void setMontoConexion(BigDecimal montoConexion) {
        this.montoConexion = montoConexion;
    }
    
    public BigDecimal getDescuentoAplicado() {
        return descuentoAplicado;
    }
    
    public void setDescuentoAplicado(BigDecimal descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }
    
    public BigDecimal getTotalPagado() {
        return totalPagado;
    }
    
    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
    }
    
    public String getTipoInstalacion() {
        return tipoInstalacion;
    }
    
    public void setTipoInstalacion(String tipoInstalacion) {
        this.tipoInstalacion = tipoInstalacion;
    }
    
    public LocalDateTime getFechaCotizacion() {
        return fechaCotizacion;
    }
    
    public void setFechaCotizacion(LocalDateTime fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }
    
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
    
    public LocalDateTime getFechaInstalacion() {
        return fechaInstalacion;
    }
    
    public void setFechaInstalacion(LocalDateTime fechaInstalacion) {
        this.fechaInstalacion = fechaInstalacion;
    }
    
    public String getTecnicoAsignado() {
        return tecnicoAsignado;
    }
    
    public void setTecnicoAsignado(String tecnicoAsignado) {
        this.tecnicoAsignado = tecnicoAsignado;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
