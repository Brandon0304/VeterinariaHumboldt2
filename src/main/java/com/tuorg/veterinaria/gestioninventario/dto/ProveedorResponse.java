package com.tuorg.veterinaria.gestioninventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response con información de un proveedor")
public class ProveedorResponse {

    @Schema(description = "ID del proveedor", example = "1")
    private Long idProveedor;

    @Schema(description = "Nombre del proveedor", example = "Distribuidora Veterinaria S.A.")
    private String nombre;

    @Schema(description = "Persona de contacto", example = "Juan Pérez")
    private String contacto;

    @Schema(description = "Teléfono de contacto", example = "+57 300 1234567")
    private String telefono;

    @Schema(description = "Dirección del proveedor", example = "Calle 123 #45-67")
    private String direccion;

    @Schema(description = "Correo electrónico", example = "contacto@proveedor.com")
    private String correo;
}

