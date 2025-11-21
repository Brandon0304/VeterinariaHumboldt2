package com.tuorg.veterinaria.gestioninventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Información de un producto del inventario")
public class ProductoResponse {

    @Schema(description = "Identificador del producto", example = "10")
    private Long id;

    @Schema(description = "SKU", example = "MED-001")
    private String sku;

    @Schema(description = "Nombre", example = "Antibiótico X")
    private String nombre;

    @Schema(description = "Descripción")
    private String descripcion;

    @Schema(description = "Tipo de producto")
    private String tipo;

    @Schema(description = "Stock actual", example = "45")
    private Integer stock;

    @Schema(description = "Precio unitario", example = "35.50")
    private BigDecimal precioUnitario;

    @Schema(description = "Unidad de medida", example = "unidad")
    private String um;

    @Schema(description = "Metadatos adicionales")
    private Map<String, Object> metadatos;
}


