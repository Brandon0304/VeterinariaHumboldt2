package com.tuorg.veterinaria.gestionpacientes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con la información de la historia clínica de un paciente")
public class HistoriaClinicaResponse {

    @Schema(description = "Identificador de la historia clínica", example = "5")
    private Long id;

    @Schema(description = "Paciente asociado")
    private VacunacionResponse.PacienteSummary paciente;

    @Schema(description = "Fecha de apertura", example = "2025-03-10T12:30:00")
    private LocalDateTime fechaApertura;

    @Schema(description = "Resumen general de la historia")
    private String resumen;

    @Schema(description = "Metadatos adicionales")
    private Map<String, Object> metadatos;
}


