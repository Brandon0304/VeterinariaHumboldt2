package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para el login de usuarios.
 */
@Data
public class LoginRequest {

    @Schema(example = "admin", description = "Nombre de usuario")
    @NotBlank
    private String username;

    @Schema(example = "Admin123*", description = "Contraseña del usuario")
    @NotBlank
    private String password;
}


