package com.tuorg.veterinaria.gestionpacientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con la información de un registro médico")
public class RegistroMedicoResponse {

    @Schema(description = "Identificador del registro", example = "12")
    private Long id;

    @Schema(description = "Historia clínica asociada", example = "5")
    private Long historiaId;

    @Schema(description = "Fecha y hora del registro", example = "2025-03-12T10:30:00")
    private LocalDateTime fecha;

    @Schema(description = "Motivo de la consulta")
    private String motivo;

    @Schema(description = "Diagnóstico realizado")
    private String diagnostico;

    @Schema(description = "Signos vitales registrados")
    private Map<String, Object> signosVitales;

    @Schema(description = "Tratamiento indicado")
    private String tratamiento;

    @Schema(description = "Veterinario responsable")
    private VacunacionResponse.VeterinarioSummary veterinario;

    @Schema(description = "Insumos utilizados")
    private List<Map<String, Object>> insumosUsados;

    @Schema(description = "Archivos asociados")
    private List<String> archivos;
}


