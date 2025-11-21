package com.tuorg.veterinaria.reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * DTO que encapsula los parámetros necesarios para generar un reporte.
 */
@Data
@Schema(name = "ReporteRequest", description = "Parámetros de entrada para generar un nuevo reporte")
public class ReporteRequest {

    @NotBlank(message = "El nombre del reporte es obligatorio")
    @Size(max = 120, message = "El nombre del reporte no puede superar 120 caracteres")
    @Schema(description = "Nombre descriptivo del reporte", example = "Resumen Diario de Consultas", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Size(max = 50, message = "El tipo de reporte no puede superar 50 caracteres")
    @Schema(description = "Tipo de reporte (diario, mensual, personalizado)", example = "DIARIO")
    private String tipo;

    @Schema(description = "Identificador del usuario que genera el reporte", example = "3")
    private Long generadoPor;

    @Schema(description = "Parámetros utilizados para la generación del reporte", example = "{\"fechaInicio\":\"2025-11-01\",\"fechaFin\":\"2025-11-10\"}")
    private Map<String, Object> parametros;
}


