package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Rol del sistema.
 * Representa los roles de usuarios (ADMIN, VETERINARIO, SECRETARIO, CLIENTE).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "roles", schema = "public")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "nombre_rol", nullable = false, unique = true, length = 50)
    private String nombreRol;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rol_permisos",
        schema = "public",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();

    /**
     * Constructor por defecto.
     */
    public Rol() {
    }

    /**
     * Constructor con parámetros.
     */
    public Rol(String nombreRol, String descripcion) {
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
    }

    // Getters y Setters

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<Permiso> permisos) {
        this.permisos = permisos;
    }
}
