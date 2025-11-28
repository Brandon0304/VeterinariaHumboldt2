package com.tuorg.veterinaria.prestacioneservicios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad que representa un servicio ofrecido por la clínica.
 * 
 * Esta clase almacena información sobre los servicios que se pueden
 * prestar a los pacientes (consultas, cirugías, vacunaciones, etc.).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "servicios", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    /**
     * Identificador único del servicio (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Long idServicio;

    /**
     * Nombre del servicio.
     */
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    /**
     * Descripción detallada del servicio.
     */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Tipo de servicio (consulta, cirugía, vacunación, control).
     */
    @Column(name = "tipo", length = 50)
    private String tipo;

    /**
     * Precio base del servicio.
     * Debe ser mayor o igual a cero (constraint CHECK).
     */
    @Column(name = "precio_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    /**
     * Duración estimada del servicio en minutos.
     */
    @Column(name = "duracion_min")
    private Integer duracionMin;
}

