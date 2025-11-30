package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO para registrar una consulta durante una cita.
 * 
 * Contiene toda la información médica necesaria para documentar
 * una consulta veterinaria.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Data
@Schema(description = "Request para registrar una consulta veterinaria")
public class ConsultaRequest {

    @NotNull(message = "El ID de la cita es obligatorio")
    @Schema(description = "ID de la cita asociada", example = "1")
    private Long citaId;

    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Schema(description = "Motivo principal de la consulta", example = "Dolor abdominal")
    private String motivo;

    @NotBlank(message = "El diagnóstico es obligatorio")
    @Schema(description = "Diagnóstico realizado", example = "Gastroenteritis")
    private String diagnostico;

    @Schema(description = "Signos vitales", example = "{\"temperatura\": 38.5, \"frecuencia_cardiaca\": 90, \"respiracion\": 25}")
    private Map<String, Object> signosVitales;

    @Schema(description = "Tratamiento prescrito", example = "Reposo 48h, dieta blanda, antibiótico")
    private String tratamiento;

    @Schema(description = "Observaciones adicionales", example = "Paciente muy asustado, requiere próxima cita")
    private String observaciones;

    @Schema(description = "Insumos utilizados en la consulta", example = "[{\"productoId\": 5, \"cantidad\": 1}]")
    private List<Map<String, Object>> insumosUsados;

    @Schema(description = "Referencias de documentos adjuntos", example = "[\"radiografia_url\", \"laboratorio_url\"]")
    private List<String> archivos;
}
