package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Información del rol asignado al usuario")
public class RolResponse {

    @Schema(description = "Identificador del rol", example = "1")
    private Long id;

    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String nombre;

    @Schema(description = "Descripción del rol", example = "Acceso total al sistema")
    private String descripcion;

    @ArraySchema(arraySchema = @Schema(description = "Permisos asociados al rol"))
    private Set<PermisoResponse> permisos;
}


