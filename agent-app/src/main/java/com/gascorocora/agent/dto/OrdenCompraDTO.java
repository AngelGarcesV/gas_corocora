package com.gascorocora.agent.dto;

import java.time.LocalDateTime;

/**
 * DTO: Orden de Compra
 * Transporte de datos de órdenes de compra desde el bpmn-service
 */
public class OrdenCompraDTO {
    
    private Long id;
    private String ordenId;
    private String estado;
    private String proveedor;
    private Integer cantidadGas;
    private Double costoTotal;
    private Double costoUnitario;
    private Integer tiempoEntregaDias;
    private String justificacionNecesidad;
    private String observaciones;
    private String usuarioSolicitante;
    private String usuarioAprueba;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAprobacion;
    private LocalDateTime fechaRechazo;

    // Constructores
    public OrdenCompraDTO() {}

    public OrdenCompraDTO(Long id, String ordenId, String estado, String proveedor, 
                          Integer cantidadGas, Double costoTotal, String usuarioSolicitante,
                          LocalDateTime fechaCreacion) {
        this.id = id;
        this.ordenId = ordenId;
        this.estado = estado;
        this.proveedor = proveedor;
        this.cantidadGas = cantidadGas;
        this.costoTotal = costoTotal;
        this.usuarioSolicitante = usuarioSolicitante;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public Integer getCantidadGas() { return cantidadGas; }
    public void setCantidadGas(Integer cantidadGas) { this.cantidadGas = cantidadGas; }

    public Double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(Double costoTotal) { this.costoTotal = costoTotal; }

    public Double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(Double costoUnitario) { this.costoUnitario = costoUnitario; }

    public Integer getTiempoEntregaDias() { return tiempoEntregaDias; }
    public void setTiempoEntregaDias(Integer tiempoEntregaDias) { this.tiempoEntregaDias = tiempoEntregaDias; }

    public String getJustificacionNecesidad() { return justificacionNecesidad; }
    public void setJustificacionNecesidad(String justificacionNecesidad) { this.justificacionNecesidad = justificacionNecesidad; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getUsuarioSolicitante() { return usuarioSolicitante; }
    public void setUsuarioSolicitante(String usuarioSolicitante) { this.usuarioSolicitante = usuarioSolicitante; }

    public String getUsuarioAprueba() { return usuarioAprueba; }
    public void setUsuarioAprueba(String usuarioAprueba) { this.usuarioAprueba = usuarioAprueba; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDateTime fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public LocalDateTime getFechaRechazo() { return fechaRechazo; }
    public void setFechaRechazo(LocalDateTime fechaRechazo) { this.fechaRechazo = fechaRechazo; }
}
