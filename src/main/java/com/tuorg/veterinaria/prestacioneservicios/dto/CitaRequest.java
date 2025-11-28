package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO utilizado para programar una nueva cita médica.
 */
@Data
@Schema(name = "CitaRequest", description = "Datos requeridos para programar una cita veterinaria")
public class CitaRequest {

    @NotNull(message = "El identificador del paciente es obligatorio")
    @Schema(description = "Identificador del paciente", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pacienteId;

    @NotNull(message = "El identificador del veterinario es obligatorio")
    @Schema(description = "Identificador del veterinario", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long veterinarioId;

    @NotNull(message = "La fecha y hora de la cita es obligatoria")
    @Future(message = "La fecha de la cita debe estar en el futuro")
    @Schema(description = "Fecha y hora programada para la cita", example = "2025-11-15T09:30:00")
    private LocalDateTime fechaHora;

    @Size(max = 50, message = "El tipo de servicio no puede superar los 50 caracteres")
    @Schema(description = "Tipo de servicio programado", example = "Consulta General")
    private String tipoServicio;

    @Schema(description = "Motivo de la consulta", example = "Chequeo anual y vacunas")
    private String motivo;

    @Size(max = 30, message = "El nivel de triage no puede superar los 30 caracteres")
    @Schema(description = "Nivel de prioridad o triage", example = "MEDIA")
    private String triageNivel;
}


