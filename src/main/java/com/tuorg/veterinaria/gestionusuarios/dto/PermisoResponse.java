package com.tuorg.veterinaria.gestionusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Información resumida de un permiso asociado a un rol")
public class PermisoResponse {

    @Schema(description = "Identificador del permiso", example = "1")
    private Long id;

    @Schema(description = "Nombre del permiso", example = "GESTIONAR_USUARIOS")
    private String nombre;

    @Schema(description = "Descripción del permiso", example = "Permite crear, editar y eliminar usuarios")
    private String descripcion;
}


