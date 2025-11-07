package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa un usuario del sistema.
 * 
 * Esta clase extiende de Persona y agrega información específica
 * de autenticación y autorización del usuario.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "usuarios", schema = "public")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends Persona {

    /**
     * Identificador del usuario reutiliza la clave primaria de Persona.
     */
    @Transient
    public Long getIdUsuario() {
        return getIdPersona();
    }

    public void setIdUsuario(Long idUsuario) {
        setIdPersona(idUsuario);
    }

    /**
     * Nombre de usuario único para autenticación.
     * Debe ser único en toda la tabla (constraint UNIQUE).
     */
    @Column(name = "username", nullable = false, unique = true, length = 60)
    private String username;

    /**
     * Hash de la contraseña del usuario.
     * La contraseña nunca debe almacenarse en texto plano.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * Indica si la cuenta del usuario está activa.
     * Los usuarios inactivos no pueden autenticarse.
     */
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /**
     * Fecha y hora del último acceso del usuario al sistema.
     * Incluye información de zona horaria.
     */
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    /**
     * Rol asignado al usuario.
     * Relación Many-to-One con la entidad Rol.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
}

