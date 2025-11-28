package com.tuorg.veterinaria.gestionpacientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Información resumida del cliente dueño del paciente")
public class PacienteOwnerResponse {

    @Schema(description = "Identificador del cliente", example = "4")
    private Long id;

    @Schema(description = "Nombre del cliente", example = "Diego")
    private String nombre;

    @Schema(description = "Apellido del cliente", example = "López")
    private String apellido;

    @Schema(description = "Correo electrónico del cliente", example = "diego.cliente@email.com")
    private String correo;
}


