package com.tuorg.veterinaria.configuracion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa un log del sistema.
 * 
 * Esta clase almacena eventos y logs del sistema para auditoría
 * y seguimiento de actividades importantes.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "logs_sistema", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogSistema {

    /**
     * Identificador único del log (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long idLog;

    /**
     * Fecha y hora del evento registrado.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Nivel del log (INFO, WARN, ERROR, DEBUG).
     */
    @Column(name = "nivel", nullable = false, length = 20)
    private String nivel;

    /**
     * Componente del sistema que generó el log.
     * Ejemplos: 'gestionusuarios', 'gestioninventario', etc.
     */
    @Column(name = "componente", length = 100)
    private String componente;

    /**
     * Mensaje descriptivo del evento.
     */
    @Column(name = "mensaje", columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    /**
     * Metadatos adicionales en formato JSON.
     * Puede incluir información contextual del evento.
     */
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;
}

