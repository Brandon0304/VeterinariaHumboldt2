package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con los datos públicos de un usuario")
public class UsuarioResponse {

    @Schema(description = "Identificador del usuario", example = "5")
    private Long id;

    @Schema(description = "Nombre", example = "Laura")
    private String nombre;

    @Schema(description = "Apellido", example = "Martínez")
    private String apellido;

    @Schema(description = "Correo electrónico", example = "laura.martinez@veterinaria.com")
    private String correo;

    @Schema(description = "Teléfono de contacto", example = "+52 55 1234 5678")
    private String telefono;

    @Schema(description = "Dirección", example = "Av. Siempre Viva 742")
    private String direccion;

    @Schema(description = "Nombre de usuario", example = "lauram")
    private String username;

    @Schema(description = "Estado de la cuenta", example = "true")
    private Boolean activo;

    @Schema(description = "Último acceso registrado", example = "2025-11-07T02:19:05")
    private LocalDateTime ultimoAcceso;

    @Schema(description = "Rol asignado")
    private RolResponse rol;
}


