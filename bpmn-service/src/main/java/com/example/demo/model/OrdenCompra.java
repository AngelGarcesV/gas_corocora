// bpmn-service/src/main/java/com/example/demo/model/OrdenCompra.java
package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entidad OrdenCompra - Gestiona las órdenes de compra de gas
 * Incluye trazabilidad completa de quién, cuándo y qué acciones se realizaron
 */
@Entity
@Table(name = "orden_compra")
public class OrdenCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ordenId;

    @Column(nullable = false)
    private String estado;
    
    private String proveedor;
    private Integer cantidadGas;
    private Double costoTotal;
    private Double costoUnitario;
    private Integer tiempoEntregaDias;
    private String justificacionNecesidad;
    private String observaciones;
    
    // Campos automáticos para trazabilidad
    @Column(name = "usuario_solicitante")
    private String usuarioSolicitante;
    
    @Column(name = "usuario_aprueba")
    private String usuarioAprueba;
    
    @Column(name = "usuario_modifica")
    private String usuarioModifica;
    
    @Column(name = "rol_solicitante")
    private String rolSolicitante;
    
    @Column(name = "departamento_solicitante")
    private String departamentoSolicitante;
    
    @Column(name = "email_solicitante")
    private String emailSolicitante;
    
    private Date fechaCreacion;
    private Date fechaActualizacion;
    
    @Column(name = "fecha_aprobacion")
    private Date fechaAprobacion;
    
    @Column(name = "fecha_rechazo")
    private Date fechaRechazo;

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

    public String getUsuarioModifica() { return usuarioModifica; }
    public void setUsuarioModifica(String usuarioModifica) { this.usuarioModifica = usuarioModifica; }

    public String getRolSolicitante() { return rolSolicitante; }
    public void setRolSolicitante(String rolSolicitante) { this.rolSolicitante = rolSolicitante; }

    public String getDepartamentoSolicitante() { return departamentoSolicitante; }
    public void setDepartamentoSolicitante(String departamentoSolicitante) { this.departamentoSolicitante = departamentoSolicitante; }

    public String getEmailSolicitante() { return emailSolicitante; }
    public void setEmailSolicitante(String emailSolicitante) { this.emailSolicitante = emailSolicitante; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Date getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Date fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public Date getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(Date fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public Date getFechaRechazo() { return fechaRechazo; }
    public void setFechaRechazo(Date fechaRechazo) { this.fechaRechazo = fechaRechazo; }

    @Override
    public String toString() {
        return "OrdenCompra{" +
                "id=" + id +
                ", ordenId='" + ordenId + '\'' +
                ", estado='" + estado + '\'' +
                ", proveedor='" + proveedor + '\'' +
                ", cantidadGas=" + cantidadGas +
                ", costoTotal=" + costoTotal +
                '}';
    }
}