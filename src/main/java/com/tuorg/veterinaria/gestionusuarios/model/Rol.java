package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un rol en el sistema.
 * 
 * Los roles definen los permisos y capacidades que tiene un usuario
 * dentro del sistema. Un rol puede tener múltiples permisos asociados.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "roles", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    /**
     * Identificador único del rol (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    /**
     * Nombre del rol (ej: ADMIN, VETERINARIO, SECRETARIO, CLIENTE).
     */
    @Column(name = "nombre_rol", nullable = false, unique = true, length = 50)
    private String nombreRol;

    /**
     * Descripción del rol y sus responsabilidades.
     */
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /**
     * Permisos asociados al rol.
     * Relación Many-to-Many con la entidad Permiso.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rol_permisos",
            schema = "public",
            joinColumns = @JoinColumn(name = "rol_id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();
}

