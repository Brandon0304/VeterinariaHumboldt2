package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para solicitud de cita desde el portal del cliente.
 */
@Data
@Schema(description = "Request para solicitar una cita")
public class SolicitudCitaRequest {

    @NotNull(message = "El paciente es obligatorio")
    @Schema(description = "ID del paciente (mascota)", example = "1")
    private Long pacienteId;

    @NotNull(message = "La fecha solicitada es obligatoria")
    @Schema(description = "Fecha deseada para la cita", example = "2025-12-15")
    private LocalDate fechaSolicitada;

    @NotNull(message = "La hora solicitada es obligatoria")
    @Schema(description = "Hora deseada para la cita", example = "14:30")
    private LocalTime horaSolicitada;

    @Schema(description = "Tipo de servicio", example = "Consulta general")
    private String tipoServicio;

    @Schema(description = "Motivo de la cita", example = "Revisión de rutina")
    private String motivo;

    @Schema(description = "Observaciones adicionales")
    private String observaciones;
}
