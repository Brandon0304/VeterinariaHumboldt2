package com.tuorg.veterinaria.gestionpacientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Request para programar la próxima dosis de una vacuna")
public class ProgramarProximaDosisRequest {

    @NotNull(message = "La fecha de próxima dosis es obligatoria")
    @Schema(description = "Fecha programada para la próxima dosis", example = "2026-03-10")
    private LocalDate proximaDosis;
}


