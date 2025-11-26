// bpmn-service/src/main/java/com/example/demo/model/AuditoriaCompra.java
package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entidad AuditoriaCompra - Registra el historial de cambios en órdenes de compra
 * Trazabilidad completa del proceso
 */
@Entity
@Table(name = "auditoria_compra")
public class AuditoriaCompra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "orden_id", nullable = false)
    private String ordenId;
    
    @Column(name = "accion", nullable = false)
    private String accion; // CREADA, APROBADA, RECHAZADA, ENTREGADA, FACTURADA, PAGADA
    
    @Column(name = "usuario", nullable = false)
    private String usuario; // Usuario que realizó la acción
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "estado_anterior")
    private String estadoAnterior;
    
    @Column(name = "estado_nuevo")
    private String estadoNuevo;
    
    @Column(name = "fecha_accion", nullable = false)
    private Date fechaAccion;
    
    @Column(name = "detalles")
    private String detalles; // JSON con información adicional
    
    @PrePersist
    protected void onCreate() {
        if (fechaAccion == null) {
            fechaAccion = new Date();
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public Date getFechaAccion() { return fechaAccion; }
    public void setFechaAccion(Date fechaAccion) { this.fechaAccion = fechaAccion; }

    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }

    @Override
    public String toString() {
        return "AuditoriaCompra{" +
                "id=" + id +
                ", ordenId='" + ordenId + '\'' +
                ", accion='" + accion + '\'' +
                ", usuario='" + usuario + '\'' +
                ", fechaAccion=" + fechaAccion +
                '}';
    }
}
