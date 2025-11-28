package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para solicitar recuperación de contraseña.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para recuperar contraseña")
public class ForgotPasswordRequest {

    @NotBlank(message = "El correo electrónico o nombre de usuario es obligatorio")
    @Schema(description = "Correo electrónico o nombre de usuario", example = "usuario@ejemplo.com")
    private String emailOrUsername;
}

