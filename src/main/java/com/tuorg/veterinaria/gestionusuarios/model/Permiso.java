package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Permiso del sistema.
 * Representa los permisos que pueden tener los roles.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "permisos", schema = "public")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long idPermiso;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToMany(mappedBy = "permisos", fetch = FetchType.LAZY)
    private Set<Rol> roles = new HashSet<>();

    /**
     * Constructor por defecto.
     */
    public Permiso() {
    }

    /**
     * Constructor con parámetros.
     */
    public Permiso(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters

    public Long getIdPermiso() {
        return idPermiso;
    }

    public void setIdPermiso(Long idPermiso) {
        this.idPermiso = idPermiso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }
}
