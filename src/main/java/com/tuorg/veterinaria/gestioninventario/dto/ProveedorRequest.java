package com.tuorg.veterinaria.gestioninventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request para crear o actualizar un proveedor")
public class ProveedorRequest {

    @NotBlank(message = "El nombre es obligatorio")
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

