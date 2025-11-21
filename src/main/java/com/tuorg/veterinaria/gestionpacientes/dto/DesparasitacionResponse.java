package com.tuorg.veterinaria.gestionpacientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response con información de una desparasitación")
public class DesparasitacionResponse {

    @Schema(description = "ID de la desparasitación", example = "1")
    private Long idDesparasitacion;

    @Schema(description = "Información del paciente")
    private PacienteSummary paciente;

    @Schema(description = "Producto utilizado", example = "Desparasitante X")
    private String productoUsado;

    @Schema(description = "Fecha de aplicación", example = "2024-01-15")
    private LocalDate fechaAplicacion;

    @Schema(description = "Fecha programada para la próxima aplicación", example = "2024-04-15")
    private LocalDate proximaAplicacion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Resumen del paciente")
    public static class PacienteSummary {
        @Schema(description = "ID del paciente", example = "1")
        private Long id;

        @Schema(description = "Nombre del paciente", example = "Max")
        private String nombre;
    }
}

