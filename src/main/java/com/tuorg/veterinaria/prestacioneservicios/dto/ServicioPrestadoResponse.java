package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida para presentar la información de un servicio prestado.
 */
@Data
@Builder
@Schema(name = "ServicioPrestadoResponse", description = "Información visible de un servicio realizado")
public class ServicioPrestadoResponse {

    @Schema(description = "Identificador del servicio prestado", example = "90")
    private Long idPrestado;

    @Schema(description = "Fecha y hora de ejecución", example = "2025-11-10T15:00:00")
    private LocalDateTime fechaEjecucion;

    @Schema(description = "Observaciones registradas", example = "Se administró sedación leve previo al procedimiento")
    private String observaciones;

    @Schema(description = "Costo total registrado", example = "85000.00")
    private BigDecimal costoTotal;

    @Schema(description = "Listado de insumos consumidos")
    private List<InsumoConsumido> insumos;

    @Schema(description = "Resumen de la cita asociada")
    private CitaResponse cita;

    @Schema(description = "Resumen del servicio perteneciente al catálogo")
    private ServicioCatalogo servicio;

    /**
     * DTO interno que representa un insumo consumido.
     */
    @Data
    @Builder
    @Schema(name = "ServicioPrestadoInsumoResponse", description = "Detalle de insumo consumido")
    public static class InsumoConsumido {

        @Schema(description = "Identificador del producto", example = "14")
        private Long productoId;

        @Schema(description = "Cantidad consumida", example = "2")
        private BigDecimal cantidad;

        @Schema(description = "Precio unitario usado para valoración", example = "4500.00")
        private BigDecimal precioUnitario;
    }

    /**
     * Resumen del servicio del catálogo.
     */
    @Data
    @Builder
    @Schema(name = "ServicioCatalogoSummary", description = "Datos básicos del servicio del catálogo")
    public static class ServicioCatalogo {

        @Schema(description = "Identificador del servicio", example = "4")
        private Long id;

        @Schema(description = "Nombre comercial del servicio", example = "Vacunación Antirrábica")
        private String nombre;

        @Schema(description = "Precio base registrado en el catálogo", example = "60000.00")
        private BigDecimal precioBase;
    }
}


