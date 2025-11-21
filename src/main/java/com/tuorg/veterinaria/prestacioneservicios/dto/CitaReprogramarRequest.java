package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO utilizado para reprogramar la fecha y hora de una cita.
 */
@Data
@Schema(name = "CitaReprogramarRequest", description = "Fecha y hora objetivo al reprogramar una cita")
public class CitaReprogramarRequest {

    @NotNull(message = "La nueva fecha y hora es obligatoria")
    @Future(message = "La nueva fecha y hora debe ser futura")
    @Schema(description = "Nueva fecha y hora para la cita", example = "2025-11-16T11:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fechaHora;
}


