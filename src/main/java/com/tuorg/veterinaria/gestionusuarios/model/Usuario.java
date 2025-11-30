package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad Usuario que extiende de Persona.
 * Representa un usuario autenticado en el sistema.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "usuarios", schema = "public")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Usuario extends Persona {

    @Column(name = "username", nullable = false, unique = true, length = 60)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private LocalDateTime passwordResetTokenExpiry;

    /**
     * Constructor por defecto.
     */
    public Usuario() {
        super();
    }

    /**
     * Constructor con parámetros básicos.
     */
    public Usuario(String nombre, String apellido, String correo, String telefono, 
                   String direccion, String username, String passwordHash, Rol rol) {
        super(nombre, apellido, correo, telefono, direccion);
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = Boolean.TRUE;
    }

    // Getters y Setters

    @Transient
    public Long getIdUsuario() {
        return getIdPersona();
    }

    @Transient
    public void setIdUsuario(Long idUsuario) {
        setIdPersona(idUsuario);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public LocalDateTime getPasswordResetTokenExpiry() {
        return passwordResetTokenExpiry;
    }

    public void setPasswordResetTokenExpiry(LocalDateTime passwordResetTokenExpiry) {
        this.passwordResetTokenExpiry = passwordResetTokenExpiry;
    }
}
