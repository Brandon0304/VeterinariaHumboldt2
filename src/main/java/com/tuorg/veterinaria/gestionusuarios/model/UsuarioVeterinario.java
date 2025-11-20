package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Entidad que representa un usuario veterinario del sistema.
 * 
 * Esta clase extiende de Usuario y agrega información específica
 * de los veterinarios, como licencia profesional y especialidad.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "usuarios_veterinarios", schema = "public")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioVeterinario extends Usuario {

    /**
     * Número de licencia profesional del veterinario.
     */
    @Column(name = "licencia_profesional", length = 100)
    private String licenciaProfesional;

    /**
     * Especialidad del veterinario (ej: "Cirugía", "Medicina General").
     */
    @Column(name = "especialidad", length = 100)
    private String especialidad;

    /**
     * Disponibilidad del veterinario en formato JSON.
     * Almacena los horarios disponibles por día de la semana.
     * Ejemplo: {"lunes": [{"inicio": "09:00", "fin": "13:00"}, ...]}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "disponibilidad", columnDefinition = "JSONB")
    private Map<String, Object> disponibilidad;
}

