package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO utilizado para registrar la ejecución de un servicio.
 */
@Data
@Schema(name = "ServicioPrestadoRequest", description = "Datos requeridos para registrar un servicio prestado")
public class ServicioPrestadoRequest {

    @NotNull(message = "El identificador de la cita es obligatorio")
    @Schema(description = "Identificador de la cita asociada", example = "55", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long citaId;

    @NotNull(message = "El identificador del servicio es obligatorio")
    @Schema(description = "Identificador del catálogo de servicios", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long servicioId;

    @Schema(description = "Fecha y hora de ejecución del servicio", example = "2025-11-10T15:00:00")
    private LocalDateTime fechaEjecucion;

    @Schema(description = "Observaciones realizadas durante la prestación del servicio", example = "Paciente respondió favorablemente al procedimiento.")
    private String observaciones;

    @DecimalMin(value = "0.0", inclusive = false, message = "El costo total debe ser mayor que cero")
    @Schema(description = "Costo total del servicio prestado", example = "85000.00")
    private BigDecimal costoTotal;

    @Schema(description = "Listado de insumos consumidos durante el servicio")
    private @Valid List<ServicioPrestadoInsumoRequest> insumosConsumidos;
}


