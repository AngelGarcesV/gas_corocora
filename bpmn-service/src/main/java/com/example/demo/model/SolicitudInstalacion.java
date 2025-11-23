package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "solicitud_instalacion")
public class SolicitudInstalacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "solicitud_id", unique = true, nullable = false)
    private String solicitudId;
    
    @Column(name = "proceso_instance_id")
    private String procesoInstanceId;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String telefono;
    
    @Column(nullable = false)
    private String direccion;
    
    private String estrato;
    
    @Column(name = "tipo_instalacion")
    private String tipoInstalacion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_solicitud")
    private Date fechaSolicitud;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_actualizacion")
    private Date fechaActualizacion;
    
    private Boolean cobertura;
    
    @Column(name = "monto_conexion")
    private Double montoConexion;
    
    private Double descuento;
    
    @Column(name = "monto_pagar")
    private Double montoPagar;
    
    @Column(name = "numero_medidor")
    private String numeroMedidor;
    
    @Column(length = 1000)
    private String observaciones;
    
    public enum EstadoSolicitud {
        PENDIENTE,
        EN_REVISION_COBERTURA,
        SIN_COBERTURA,
        EN_COTIZACION,
        ESPERANDO_PAGO,
        PAGO_RECIBIDO,
        EN_INSTALACION,
        EN_VERIFICACION,
        COMPLETADA,
        RECHAZADA,
        CANCELADA
    }
    
    // Constructors
    public SolicitudInstalacion() {
        this.fechaSolicitud = new Date();
        this.fechaActualizacion = new Date();
        this.estado = EstadoSolicitud.PENDIENTE;
    }
    
    public SolicitudInstalacion(String solicitudId, String nombre, String email, String telefono, String direccion) {
        this();
        this.solicitudId = solicitudId;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
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
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getEstrato() {
        return estrato;
    }
    
    public void setEstrato(String estrato) {
        this.estrato = estrato;
    }
    
    public String getTipoInstalacion() {
        return tipoInstalacion;
    }
    
    public void setTipoInstalacion(String tipoInstalacion) {
        this.tipoInstalacion = tipoInstalacion;
    }
    
    public EstadoSolicitud getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
        this.fechaActualizacion = new Date();
    }
    
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }
    
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
    
    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public Boolean getCobertura() {
        return cobertura;
    }
    
    public void setCobertura(Boolean cobertura) {
        this.cobertura = cobertura;
    }
    
    public Double getMontoConexion() {
        return montoConexion;
    }
    
    public void setMontoConexion(Double montoConexion) {
        this.montoConexion = montoConexion;
    }
    
    public Double getDescuento() {
        return descuento;
    }
    
    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }
    
    public Double getMontoPagar() {
        return montoPagar;
    }
    
    public void setMontoPagar(Double montoPagar) {
        this.montoPagar = montoPagar;
    }
    
    public String getNumeroMedidor() {
        return numeroMedidor;
    }
    
    public void setNumeroMedidor(String numeroMedidor) {
        this.numeroMedidor = numeroMedidor;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    @Override
    public String toString() {
        return "SolicitudInstalacion{" +
                "id=" + id +
                ", solicitudId='" + solicitudId + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", estado=" + estado +
                ", fechaSolicitud=" + fechaSolicitud +
                '}';
    }
}