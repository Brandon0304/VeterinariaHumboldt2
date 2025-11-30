package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request para la actualización de un usuario")
public class UsuarioUpdateRequest {

    @Schema(description = "Nombre de la persona", example = "Jorge")
    private String nombre;

    @Schema(description = "Apellido de la persona", example = "Ríos")
    private String apellido;

    @Email(message = "El correo electrónico debe ser válido")
    @Schema(description = "Correo electrónico único", example = "jorge.rios@correo.com")
    private String correo;

    @Schema(description = "Teléfono de contacto", example = "+57 300 123 4567")
    private String telefono;

    @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
    private String direccion;

    @Schema(description = "Nombre de usuario", example = "jorgerios25")
    private String username;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Schema(description = "Nueva contraseña del usuario", example = "ClaveSegura123*")
    private String password;

    @Schema(description = "Identificador del rol a asignar", example = "2")
    private Long rolId;

    @Schema(description = "Estado de la cuenta", example = "true")
    private Boolean activo;
}


