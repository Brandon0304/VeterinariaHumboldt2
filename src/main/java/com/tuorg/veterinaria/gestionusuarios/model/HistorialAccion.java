package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad HistorialAccion para auditoría de acciones de usuarios.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "historial_acciones", schema = "public")
public class HistorialAccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_accion")
    private Long idAccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_accion", nullable = false, length = 100)
    private String tipoAccion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Constructor por defecto.
     */
    public HistorialAccion() {
    }

    /**
     * Constructor con parámetros.
     */
    public HistorialAccion(Usuario usuario, String tipoAccion, String descripcion, 
                          LocalDateTime fechaHora, String ipAddress) {
        this.usuario = usuario;
        this.tipoAccion = tipoAccion;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.ipAddress = ipAddress;
    }

    // Getters y Setters

    public Long getIdAccion() {
        return idAccion;
    }

    public void setIdAccion(Long idAccion) {
        this.idAccion = idAccion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
