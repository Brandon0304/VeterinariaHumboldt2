package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request para la creación de un cliente")
public class ClienteRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Nombre del cliente", example = "Jorge")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Schema(description = "Apellido del cliente", example = "Ríos")
    private String apellido;

    @Email(message = "El correo electrónico debe ser válido")
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Schema(description = "Correo electrónico único", example = "jorge.rios@correo.com")
    private String correo;

    @Schema(description = "Teléfono de contacto", example = "+57 3001234567")
    private String telefono;

    @Schema(description = "Dirección del cliente", example = "Calle 123 #45-67")
    private String direccion;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Schema(description = "Nombre de usuario único", example = "jorgerios25")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Schema(description = "Contraseña del cliente", example = "ClaveSegura123*")
    private String password;

    @Schema(description = "Documento de identidad", example = "CC-123456789")
    private String documentoIdentidad;
}


