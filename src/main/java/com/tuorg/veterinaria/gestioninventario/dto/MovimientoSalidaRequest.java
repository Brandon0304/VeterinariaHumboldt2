package com.tuorg.veterinaria.gestioninventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Request para registrar una salida de inventario")
public class MovimientoSalidaRequest {

    @NotNull(message = "Debe indicar el producto")
    @Schema(description = "Identificador del producto", example = "5")
    private Long productoId;

    @Positive(message = "La cantidad debe ser mayor que cero")
    @Schema(description = "Cantidad a retirar", example = "5")
    private Integer cantidad;

    @Schema(description = "Referencia del movimiento", example = "CONSUMO-CONSULTA-123")
    private String referencia;

    @Schema(description = "Usuario que registra el movimiento", example = "2")
    private Long usuarioId;
}


