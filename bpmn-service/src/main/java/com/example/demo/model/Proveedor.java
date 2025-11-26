// bpmn-service/src/main/java/com/example/demo/model/Proveedor.java
package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entidad Proveedor - Gestiona la información de proveedores de gas
 * Utilizada en el proceso de compra de gas
 */
@Entity
@Table(name = "proveedor")
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Column(name = "precio_por_kg", nullable = false)
    private Double precioPorKg;
    
    @Column(name = "tiempo_entrega_dias", nullable = false)
    private Integer tiempoEntregaDias;
    
    @Column(name = "vigencia_meses", nullable = false)
    private Integer vigenciaMeses;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "telefono_contacto")
    private String telefonoContacto;
    
    @Column(nullable = false)
    private String estado; // ACTIVO, INACTIVO, SUSPENDIDO
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "ciudad")
    private String ciudad;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Date fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private Date fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = new Date();
        fechaActualizacion = new Date();
        if (estado == null) {
            estado = "ACTIVO";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = new Date();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecioPorKg() { return precioPorKg; }
    public void setPrecioPorKg(Double precioPorKg) { this.precioPorKg = precioPorKg; }

    public Integer getTiempoEntregaDias() { return tiempoEntregaDias; }
    public void setTiempoEntregaDias(Integer tiempoEntregaDias) { this.tiempoEntregaDias = tiempoEntregaDias; }

    public Integer getVigenciaMeses() { return vigenciaMeses; }
    public void setVigenciaMeses(Integer vigenciaMeses) { this.vigenciaMeses = vigenciaMeses; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Date getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Date fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precioPorKg=" + precioPorKg +
                ", tiempoEntregaDias=" + tiempoEntregaDias +
                ", estado='" + estado + '\'' +
                '}';
    }
}
