package com.tuorg.veterinaria.gestionfacturacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
}

