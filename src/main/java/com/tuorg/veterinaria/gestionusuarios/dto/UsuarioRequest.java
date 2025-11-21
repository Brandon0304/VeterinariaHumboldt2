package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request para la creación de un usuario")
public class UsuarioRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Nombre de la persona", example = "Jorge")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Schema(description = "Apellido de la persona", example = "Ríos")
    private String apellido;

    @Email(message = "El correo electrónico debe ser válido")
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Schema(description = "Correo electrónico único", example = "jorge.rios@correo.com")
    private String correo;

    @Schema(description = "Teléfono de contacto", example = "+57 300 123 4567")
    private String telefono;

    @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
    private String direccion;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Schema(description = "Nombre de usuario único", example = "jorgerios25")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Schema(description = "Contraseña del usuario", example = "ClaveSegura123*")
    private String password;

    @NotNull(message = "Debe especificar el rol del usuario")
    @Schema(description = "Identificador del rol a asignar", example = "2")
    private Long rolId;

    @Schema(description = "Estado de la cuenta. Si no se envía, se activa por defecto", example = "true")
    private Boolean activo;
}


