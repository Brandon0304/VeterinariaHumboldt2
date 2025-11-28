package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO utilizado para cancelar una cita especificando el motivo.
 */
@Data
@Schema(name = "CitaCancelarRequest", description = "Motivo de cancelación de la cita")
public class CitaCancelarRequest {

    @NotBlank(message = "El motivo de cancelación es obligatorio")
    @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
    @Schema(description = "Motivo por el cual se cancela la cita", example = "Paciente presenta signos de mejora y reprogramará", requiredMode = Schema.RequiredMode.REQUIRED)
    private String motivo;
}


