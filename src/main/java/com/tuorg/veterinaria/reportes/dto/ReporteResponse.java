package com.tuorg.veterinaria.reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO de salida que expone la información relevante de un reporte generado.
 */
@Data
@Builder
@Schema(name = "ReporteResponse", description = "Información del reporte generado y sus estadísticas relacionadas")
public class ReporteResponse {

    @Schema(description = "Identificador del reporte", example = "25")
    private Long id;

    @Schema(description = "Nombre asignado al reporte", example = "Resumen Diario de Consultas")
    private String nombre;

    @Schema(description = "Tipo o categoría del reporte", example = "DIARIO")
    private String tipo;

    @Schema(description = "Fecha y hora en la que se generó el reporte", example = "2025-11-10T15:30:00")
    private LocalDateTime fechaGeneracion;

    @Schema(description = "Identificador del usuario o sistema que generó el reporte", example = "3")
    private Long generadoPor;

    @Schema(description = "Parámetros utilizados durante la generación del reporte")
    private Map<String, Object> parametros;

    @Schema(description = "Colección de estadísticas generadas junto con el reporte")
    private List<EstadisticaResponse> estadisticas;
}


