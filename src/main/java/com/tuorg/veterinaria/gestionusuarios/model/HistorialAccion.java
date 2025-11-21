package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa un registro en el historial de acciones.
 *
 * Esta clase almacena todas las acciones importantes realizadas
 * por los usuarios del sistema para auditoría y seguimiento.
 *
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "historial_acciones", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialAccion {

    /**
     * Identificador único de la acción (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_accion")
    private Long idAccion;

    /**
     * Usuario que realizó la acción.
     * Relación Many-to-One con la entidad Usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Fecha y hora en que se realizó la acción.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Descripción detallada de la acción realizada.
     */
    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    /**
     * Metadatos adicionales de la acción en formato JSON.
     * Puede incluir información contextual como IDs de recursos afectados.
     */
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;
}
