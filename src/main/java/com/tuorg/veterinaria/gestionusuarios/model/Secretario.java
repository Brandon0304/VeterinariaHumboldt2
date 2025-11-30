package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;

/**
 * Entidad Secretario que extiende de Usuario.
 * Representa a los secretarios del sistema.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "secretarios", schema = "public")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Secretario extends Usuario {

    @Column(name = "extension", length = 10)
    private String extension;

    /**
     * Constructor por defecto.
     */
    public Secretario() {
        super();
    }

    /**
     * Constructor con parámetros.
     */
    public Secretario(String extension) {
        super();
        this.extension = extension;
    }

    // Getters y Setters

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
