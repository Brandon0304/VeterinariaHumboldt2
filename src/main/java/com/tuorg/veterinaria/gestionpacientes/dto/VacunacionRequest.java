package com.tuorg.veterinaria.gestionpacientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Request para registrar una vacunación")
public class VacunacionRequest {

    @NotNull(message = "Debe indicar el paciente")
    @Schema(description = "Identificador del paciente", example = "1")
    private Long pacienteId;

    @NotBlank(message = "El tipo de vacuna es obligatorio")
    @Schema(description = "Nombre de la vacuna", example = "Rabia")
    private String tipoVacuna;

    @NotNull(message = "La fecha de aplicación es obligatoria")
    @Schema(description = "Fecha en que se aplicó la vacuna", example = "2025-03-10")
    private LocalDate fechaAplicacion;

    @Schema(description = "Fecha de la próxima dosis", example = "2026-03-10")
    private LocalDate proximaDosis;

    @Schema(description = "Identificador del veterinario que aplica la vacuna", example = "2")
    private Long veterinarioId;
}


