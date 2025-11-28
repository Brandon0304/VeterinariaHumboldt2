package com.tuorg.veterinaria.gestionpacientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "Request para agregar un registro médico a una historia clínica")
public class RegistroMedicoRequest {

    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha y hora del registro", example = "2025-03-12T10:30:00")
    private LocalDateTime fecha;

    @NotBlank(message = "El motivo es obligatorio")
    @Schema(description = "Motivo de la consulta", example = "Control postoperatorio")
    private String motivo;

    @NotBlank(message = "El diagnóstico es obligatorio")
    @Schema(description = "Diagnóstico realizado", example = "Recuperación favorable")
    private String diagnostico;

    @Schema(description = "Signos vitales en formato clave/valor", example = "{\"temperatura\": 38.2, \"fc\": 90}")
    private Map<String, Object> signosVitales;

    @Schema(description = "Tratamiento indicado", example = "Continuar antibioticoterapia por 5 días")
    private String tratamiento;

    @Schema(description = "Identificador del veterinario que atiende", example = "2")
    private Long veterinarioId;

    @Schema(description = "Insumos utilizados", example = "[{\"productoId\":1,\"cantidad\":2}]")
    private List<Map<String, Object>> insumosUsados;

    @Schema(description = "Archivos asociados (URLs)", example = "[\"https://...\", \"https://...\"]")
    private List<String> archivos;
}


