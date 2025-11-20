package com.tuorg.veterinaria.gestioninventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con los datos de un movimiento de inventario")
public class MovimientoInventarioResponse {

    @Schema(description = "Identificador del movimiento", example = "100")
    private Long id;

    @Schema(description = "Tipo de movimiento", example = "IN")
    private String tipoMovimiento;

    @Schema(description = "Cantidad movida", example = "20")
    private Integer cantidad;

    @Schema(description = "Fecha y hora del movimiento", example = "2025-03-10T15:45:00")
    private LocalDateTime fecha;

    @Schema(description = "Referencia del movimiento", example = "OC-2025-0012")
    private String referencia;

    @Schema(description = "Producto afectado")
    private ProductoSummary producto;

    @Schema(description = "Proveedor asociado")
    private ProveedorSummary proveedor;

    @Schema(description = "Usuario que registró el movimiento")
    private UsuarioSummary usuario;

    @Schema(description = "Stock resultante después del movimiento", example = "120")
    private Integer stockResultante;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Resumen del producto")
    public static class ProductoSummary {
        private Long id;
        private String sku;
        private String nombre;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Resumen del proveedor")
    public static class ProveedorSummary {
        private Long id;
        private String nombre;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Resumen del usuario")
    public static class UsuarioSummary {
        private Long id;
        private String username;
        private String nombre;
        private String apellido;
    }
}


