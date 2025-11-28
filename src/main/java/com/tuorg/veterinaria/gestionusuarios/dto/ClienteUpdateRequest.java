package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@Schema(description = "Request para actualizar un cliente")
public class ClienteUpdateRequest {

    @Schema(description = "Nombre del cliente", example = "Jorge")
    private String nombre;

    @Schema(description = "Apellido del cliente", example = "Ríos")
    private String apellido;

    @Email(message = "El correo electrónico debe ser válido")
    @Schema(description = "Correo electrónico único", example = "jorge.rios@correo.com")
    private String correo;

    @Schema(description = "Teléfono de contacto", example = "+57 3001234567")
    private String telefono;

    @Schema(description = "Dirección del cliente", example = "Calle 123 #45-67")
    private String direccion;

    @Schema(description = "Documento de identidad", example = "CC-123456789")
    private String documentoIdentidad;
}

