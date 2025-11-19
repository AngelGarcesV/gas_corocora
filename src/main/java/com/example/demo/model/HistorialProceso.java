package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "historial_proceso")
public class HistorialProceso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "solicitud_id", nullable = false)
    private String solicitudId;
    
    @Column(name = "proceso_instance_id")
    private String procesoInstanceId;
    
    @Column(nullable = false)
    private String actividad;
    
    @Column(length = 1000)
    private String descripcion;
    
    @Column(name = "usuario_responsable")
    private String usuarioResponsable;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_evento")
    private Date fechaEvento;
    
    @Column(name = "tipo_evento")
    private String tipoEvento; // INICIO, TAREA_COMPLETADA, NOTIFICACION_ENVIADA, ERROR, etc.
    
    // Constructors
    public HistorialProceso() {
        this.fechaEvento = new Date();
    }
    
    public HistorialProceso(String solicitudId, String actividad, String descripcion) {
        this();
        this.solicitudId = solicitudId;
        this.actividad = actividad;
        this.descripcion = descripcion;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSolicitudId() {
        return solicitudId;
    }
    
    public void setSolicitudId(String solicitudId) {
        this.solicitudId = solicitudId;
    }
    
    public String getProcesoInstanceId() {
        return procesoInstanceId;
    }
    
    public void setProcesoInstanceId(String procesoInstanceId) {
        this.procesoInstanceId = procesoInstanceId;
    }
    
    public String getActividad() {
        return actividad;
    }
    
    public void setActividad(String actividad) {
        this.actividad = actividad;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }
    
    public void setUsuarioResponsable(String usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }
    
    public Date getFechaEvento() {
        return fechaEvento;
    }
    
    public void setFechaEvento(Date fechaEvento) {
        this.fechaEvento = fechaEvento;
    }
    
    public String getTipoEvento() {
        return tipoEvento;
    }
    
    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
}