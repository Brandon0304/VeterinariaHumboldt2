package com.tuorg.veterinaria.gestionpacientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Request para registrar una desparasitación")
public class DesparasitacionRequest {

    @NotNull(message = "El ID del paciente es obligatorio")
    @Schema(description = "ID del paciente", example = "1")
    private Long pacienteId;

    @NotBlank(message = "El producto usado es obligatorio")
    @Schema(description = "Producto utilizado para la desparasitación", example = "Desparasitante X")
    private String productoUsado;

    @NotNull(message = "La fecha de aplicación es obligatoria")
    @Schema(description = "Fecha de aplicación", example = "2024-01-15")
    private LocalDate fechaAplicacion;

    @Schema(description = "Fecha programada para la próxima aplicación", example = "2024-04-15")
    private LocalDate proximaAplicacion;
}

