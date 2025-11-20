package com.tuorg.veterinaria.gestionpacientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con la información de una vacunación")
public class VacunacionResponse {

    @Schema(description = "Identificador de la vacunación", example = "10")
    private Long id;

    @Schema(description = "Paciente asociado")
    private PacienteSummary paciente;

    @Schema(description = "Tipo de vacuna aplicada", example = "Rabia")
    private String tipoVacuna;

    @Schema(description = "Fecha de aplicación", example = "2025-03-10")
    private LocalDate fechaAplicacion;

    @Schema(description = "Fecha programada para la próxima dosis", example = "2026-03-10")
    private LocalDate proximaDosis;

    @Schema(description = "Veterinario que aplicó la vacuna")
    private VeterinarioSummary veterinario;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Resumen del paciente")
    public static class PacienteSummary {
        private Long id;
        private String nombre;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Resumen del veterinario")
    public static class VeterinarioSummary {
        private Long id;
        private String nombre;
        private String apellido;
        private String especialidad;
    }
}


