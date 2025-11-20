package com.tuorg.veterinaria.gestioninventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Schema(description = "Request para crear un producto de inventario")
public class ProductoRequest {

    @NotBlank(message = "El SKU es obligatorio")
    @Schema(description = "Código SKU único", example = "MED-001")
    private String sku;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre comercial del producto", example = "Antibiótico X")
    private String nombre;

    @Schema(description = "Descripción detallada", example = "Antibiótico de amplio espectro")
    private String descripcion;

    @Schema(description = "Tipo de producto", example = "MEDICAMENTO")
    private String tipo;

    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @Schema(description = "Precio unitario", example = "35.50")
    private BigDecimal precioUnitario;

    @Schema(description = "Unidad de medida", example = "unidad")
    private String um;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Schema(description = "Stock inicial", example = "50")
    private Integer stock;

    @Schema(description = "Metadatos adicionales en formato clave/valor")
    private Map<String, Object> metadatos;
}


