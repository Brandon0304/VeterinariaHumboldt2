package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para el proceso de autenticación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con token y datos básicos del usuario")
public class LoginResponse {

    @Schema(description = "Token JWT emitido por el sistema", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo de token devuelto", example = "Bearer")
    private String type;

    @Schema(description = "Información del usuario autenticado")
    private UsuarioLoginResponse usuario;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Datos básicos del usuario autenticado")
    public static class UsuarioLoginResponse {

        @Schema(description = "Identificador del usuario", example = "5")
        private Long idUsuario;

        @Schema(description = "Nombre del usuario", example = "Diego")
        private String nombre;

        @Schema(description = "Apellido del usuario", example = "Martínez")
        private String apellido;

        @Schema(description = "Correo electrónico del usuario", example = "diego.martinez@clinica.com")
        private String correo;

        @Schema(description = "Rol asignado al usuario", example = "VETERINARIO")
        private String rol;
    }
}


