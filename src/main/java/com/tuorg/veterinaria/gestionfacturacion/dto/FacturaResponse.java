package com.tuorg.veterinaria.gestionfacturacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO de salida para exponer la información de una factura emitida.
 */
@Data
@Builder
@Schema(name = "FacturaResponse", description = "Información visible de una factura veterinaria")
public class FacturaResponse {

    @Schema(description = "Identificador de la factura", example = "120")
    private Long idFactura;

    @Schema(description = "Número único de la factura", example = "FACT-20251110-0001")
    private String numero;

    @Schema(description = "Fecha y hora de emisión", example = "2025-11-10T14:25:00")
    private LocalDateTime fechaEmision;

    @Schema(description = "Fecha y hora del pago", example = "2025-11-10T16:45:00")
    private LocalDateTime fechaPago;

    @Schema(description = "Monto total cobrado", example = "145000.50")
    private BigDecimal total;

    @Schema(description = "Forma de pago utilizada", example = "EFECTIVO")
    private String formaPago;

    @Schema(description = "Estado actual de la factura", example = "PENDIENTE")
    private String estado;

    @Schema(description = "Contenido detallado de la factura")
    private Map<String, Object> contenido;

    @Schema(description = "Resumen del cliente al que se factura")
    private ClienteSummary cliente;

    /**
     * Resumen del cliente vinculado a la factura.
     */
    @Data
    @Builder
    @Schema(name = "FacturaClienteSummary", description = "Datos básicos del cliente")
    public static class ClienteSummary {

        @Schema(description = "Identificador del cliente", example = "8")
        private Long id;

        @Schema(description = "Nombre completo del cliente", example = "Diego Ramírez")
        private String nombreCompleto;

        @Schema(description = "Correo electrónico del cliente", example = "diego.ramirez@email.com")
        private String correo;

        @Schema(description = "Teléfono de contacto", example = "+57 3001234567")
        private String telefono;
    }
}

