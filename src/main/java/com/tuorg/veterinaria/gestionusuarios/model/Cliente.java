package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad Cliente que extiende de Usuario.
 * Representa a los clientes/dueños de mascotas en el sistema.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "clientes", schema = "public")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Cliente extends Usuario {

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "documento_identidad", length = 50)
    private String documentoIdentidad;

    /**
     * Constructor por defecto.
     */
    public Cliente() {
        super();
    }

    /**
     * Constructor con parámetros.
     */
    public Cliente(LocalDateTime fechaRegistro, String documentoIdentidad) {
        super();
        this.fechaRegistro = fechaRegistro;
        this.documentoIdentidad = documentoIdentidad;
    }

    // Getters y Setters

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }
}
