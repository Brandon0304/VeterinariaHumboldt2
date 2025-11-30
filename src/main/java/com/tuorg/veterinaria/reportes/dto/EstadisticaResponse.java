package com.tuorg.veterinaria.reportes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de salida que representa una estadística calculada.
 */
@Data
@Builder
@Schema(name = "EstadisticaResponse", description = "Información resumida de una estadística generada")
public class EstadisticaResponse {

    @Schema(description = "Identificador único de la estadística", example = "12")
    private Long id;

    @Schema(description = "Nombre de la estadística", example = "Consultas completadas")
    private String nombre;

    @Schema(description = "Valor numérico de la estadística", example = "42")
    private BigDecimal valor;

    @Schema(description = "Fecha inicial del período analizado", example = "2025-11-01")
    private LocalDate periodoInicio;

    @Schema(description = "Fecha final del período analizado", example = "2025-11-10")
    private LocalDate periodoFin;
}


