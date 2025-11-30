package com.tuorg.veterinaria.gestionpacientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estadísticas generales de pacientes.
 * 
 * Proporciona métricas agregadas sobre los pacientes registrados
 * en el sistema veterinario.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Estadísticas generales de pacientes")
public class PacienteEstadisticasResponse {

    @Schema(description = "Total de pacientes registrados", example = "127")
    private Long totalPacientes;

    @Schema(description = "Total de perros registrados", example = "82")
    private Long totalPerros;

    @Schema(description = "Total de gatos registrados", example = "45")
    private Long totalGatos;

    @Schema(description = "Pacientes nuevos este mes", example = "8")
    private Long nuevosEsteMes;

    @Schema(description = "Pacientes con estado de salud crítico o en tratamiento", example = "3")
    private Long pacientesEnTratamiento;

    @Schema(description = "Promedio de edad de pacientes en años", example = "3.5")
    private Double edadPromedio;
}
