package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta de disponibilidad de horarios.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de disponibilidad de un horario específico")
public class HorarioDisponibilidadResponse {

    @Schema(description = "Fecha y hora del horario", example = "2025-11-29T10:00:00")
    private LocalDateTime fechaHora;

    @Schema(description = "Indica si el horario está disponible", example = "true")
    private boolean disponible;

    @Schema(description = "Estado del horario", example = "DISPONIBLE")
    private EstadoHorario estado;

    @Schema(description = "Duración estimada en minutos", example = "30")
    private Integer duracionMinutos;

    @Schema(description = "ID de la cita existente (si está ocupado)", example = "123")
    private Long citaId;

    @Schema(description = "Nombre del paciente (si está ocupado)", example = "Firulais")
    private String nombrePaciente;

    /**
     * Estados posibles de un horario.
     */
    public enum EstadoHorario {
        DISPONIBLE,      // Verde - Horario libre
        OCUPADO,         // Rojo - Horario con cita programada
        FUERA_HORARIO,   // Gris - Fuera del horario laboral
        BLOQUEADO        // Amarillo - Horario bloqueado/mantenimiento
    }
}
