package com.tuorg.veterinaria.gestionfacturacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO utilizado para solicitar la creación de una factura.
 */
@Data
@Schema(name = "FacturaRequest", description = "Datos necesarios para emitir una factura")
public class FacturaRequest {

    @NotNull(message = "El identificador del cliente es obligatorio")
    @Schema(description = "Identificador del cliente al que se factura", example = "8", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long clienteId;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El total debe ser mayor o igual a cero")
    @Schema(description = "Monto total de la factura", example = "145000.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal total;

    @Schema(description = "Forma de pago pactada", example = "TARJETA")
    private String formaPago;

    @Schema(description = "Contenido detallado de la factura en formato JSON", example = "{\"servicios\":[{\"nombre\":\"Consulta\",\"monto\":40000}]}")
    private Map<String, Object> contenido;
}

