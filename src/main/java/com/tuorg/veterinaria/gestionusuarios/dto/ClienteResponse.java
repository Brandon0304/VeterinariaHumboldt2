package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Información pública de un cliente")
public class ClienteResponse {

    @Schema(description = "Identificador del cliente", example = "4")
    private Long id;

    @Schema(description = "Nombre", example = "Diego")
    private String nombre;

    @Schema(description = "Apellido", example = "López")
    private String apellido;

    @Schema(description = "Correo electrónico", example = "diego.cliente@email.com")
    private String correo;

    @Schema(description = "Teléfono", example = "3100000004")
    private String telefono;

    @Schema(description = "Dirección", example = "Transversal 9 #6-15")
    private String direccion;

    @Schema(description = "Documento de identidad", example = "CC-100200300")
    private String documentoIdentidad;

    @Schema(description = "Fecha de registro", example = "2025-10-05T10:15:30")
    private LocalDateTime fechaRegistro;

    @Schema(description = "Nombre de usuario asociado", example = "cliente_diego")
    private String username;

    @Schema(description = "Indica si el cliente está activo", example = "true")
    private Boolean activo;
}


