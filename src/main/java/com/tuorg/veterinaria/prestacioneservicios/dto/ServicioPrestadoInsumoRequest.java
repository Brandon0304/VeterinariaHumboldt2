package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que describe un insumo consumido durante un servicio prestado.
 */
@Data
@Schema(name = "ServicioPrestadoInsumoRequest", description = "Detalle de un insumo consumido")
public class ServicioPrestadoInsumoRequest {

    @NotNull(message = "El identificador del producto es obligatorio")
    @Schema(description = "Identificador del producto del inventario", example = "14", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productoId;

    @NotNull(message = "La cantidad consumida es obligatoria")
    @DecimalMin(value = "0.0", inclusive = false, message = "La cantidad debe ser mayor que cero")
    @Schema(description = "Cantidad consumida", example = "2")
    private BigDecimal cantidad;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio unitario no puede ser negativo")
    @Schema(description = "Precio unitario registrado para el insumo", example = "4500.00")
    private BigDecimal precioUnitario;
}


