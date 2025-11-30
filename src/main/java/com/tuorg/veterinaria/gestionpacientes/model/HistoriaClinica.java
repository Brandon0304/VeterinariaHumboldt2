package com.tuorg.veterinaria.gestionpacientes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entidad que representa la historia clínica de un paciente.
 * 
 * Cada paciente debe tener al menos una historia clínica que se crea
 * automáticamente al registrar el paciente.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "historias_clinicas", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaClinica {

    /**
     * Identificador único de la historia clínica (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historia")
    private Long idHistoria;

    /**
     * Paciente al que pertenece esta historia clínica.
     * Relación Many-to-One con la entidad Paciente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    /**
     * Fecha y hora de apertura de la historia clínica.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    /**
     * Resumen general de la historia clínica.
     */
    @Column(name = "resumen", columnDefinition = "TEXT")
    private String resumen;

    /**
     * Metadatos adicionales en formato JSON.
     * Puede incluir información contextual adicional.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadatos", columnDefinition = "JSONB")
    private Map<String, Object> metadatos;
}

