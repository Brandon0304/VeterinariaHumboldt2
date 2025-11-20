package com.tuorg.veterinaria.gestioninventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Request para registrar una entrada de inventario")
public class MovimientoEntradaRequest {

    @NotNull(message = "Debe indicar el producto")
    @Schema(description = "Identificador del producto", example = "5")
    private Long productoId;

    @Positive(message = "La cantidad debe ser mayor que cero")
    @Schema(description = "Cantidad a ingresar", example = "20")
    private Integer cantidad;

    @Schema(description = "Identificador del proveedor", example = "3")
    private Long proveedorId;

    @Schema(description = "Referencia del movimiento", example = "OC-2025-0012")
    private String referencia;

    @Schema(description = "Usuario que registra el movimiento", example = "1")
    private Long usuarioId;
}


