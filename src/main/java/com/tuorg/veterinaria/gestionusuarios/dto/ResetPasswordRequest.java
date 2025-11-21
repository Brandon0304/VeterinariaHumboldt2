package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para restablecer contraseña con token.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para restablecer contraseña con token")
public class ResetPasswordRequest {

    @NotBlank(message = "El token es obligatorio")
    @Schema(description = "Token de recuperación de contraseña", example = "abc123def456...")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Schema(description = "Nueva contraseña", example = "NuevaClave123*")
    private String newPassword;
}

