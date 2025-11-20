package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para el registro de usuarios desde el endpoint de autenticación.
 */
@Data
public class RegisterRequest {

    @Schema(example = "nuevo_usuario", description = "Nombre de usuario único para autenticación")
    @NotBlank
    private String username;

    @Schema(example = "ClaveSegura123*", description = "Contraseña del usuario")
    @NotBlank
    private String password;

    @Schema(example = "nuevo.usuario@midominio.com", description = "Correo electrónico del usuario")
    @Email
    @NotBlank
    private String email;

    @Schema(example = "Nuevo", description = "Nombre de la persona")
    @NotBlank
    private String nombre;

    @Schema(example = "Usuario", description = "Apellido de la persona")
    @NotBlank
    private String apellido;
}


