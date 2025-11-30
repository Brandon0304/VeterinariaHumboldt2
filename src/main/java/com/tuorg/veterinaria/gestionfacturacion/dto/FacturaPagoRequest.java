package com.tuorg.veterinaria.gestionfacturacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO utilizado para registrar el pago de una factura.
 */
@Data
@Schema(name = "FacturaPagoRequest", description = "Datos necesarios para registrar el pago de una factura")
public class FacturaPagoRequest {

    @NotBlank(message = "La forma de pago es obligatoria")
    @Size(max = 50, message = "La forma de pago no puede superar los 50 caracteres")
    @Schema(description = "Medio de pago utilizado", example = "TARJETA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String formaPago;

    @NotNull(message = "El monto pagado es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto pagado debe ser mayor a cero")
    @Schema(description = "Monto efectivamente pagado", example = "145000.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal montoPagado;
}

