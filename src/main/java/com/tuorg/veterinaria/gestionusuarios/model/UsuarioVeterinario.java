package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;

/**
 * Entidad UsuarioVeterinario que extiende de Usuario.
 * Representa a los veterinarios del sistema.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "usuarios_veterinarios", schema = "public")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class UsuarioVeterinario extends Usuario {

    @Column(name = "licencia_profesional", nullable = false, length = 50)
    private String licenciaProfesional;

    @Column(name = "especialidad", length = 100)
    private String especialidad;

    @Type(JsonBinaryType.class)
    @Column(name = "disponibilidad", columnDefinition = "jsonb")
    private String disponibilidad;

    /**
     * Constructor por defecto.
     */
    public UsuarioVeterinario() {
        super();
    }

    /**
     * Constructor con parámetros.
     */
    public UsuarioVeterinario(String licenciaProfesional, String especialidad, String disponibilidad) {
        super();
        this.licenciaProfesional = licenciaProfesional;
        this.especialidad = especialidad;
        this.disponibilidad = disponibilidad;
    }

    // Getters y Setters

    public String getLicenciaProfesional() {
        return licenciaProfesional;
    }

    public void setLicenciaProfesional(String licenciaProfesional) {
        this.licenciaProfesional = licenciaProfesional;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}
