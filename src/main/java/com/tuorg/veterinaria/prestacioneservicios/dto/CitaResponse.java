package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de salida para exponer la información de una cita médica.
 */
@Data
@Builder
@Schema(name = "CitaResponse", description = "Representa los datos visibles de una cita veterinaria")
public class CitaResponse {

    @Schema(description = "Identificador de la cita", example = "42")
    private Long idCita;

    @Schema(description = "Fecha y hora programada de la cita", example = "2025-11-15T09:30:00")
    private LocalDateTime fechaHora;

    @Schema(description = "Estado actual de la cita", example = "PROGRAMADA")
    private String estado;

    @Schema(description = "Tipo de servicio programado", example = "Consulta General")
    private String tipoServicio;

    @Schema(description = "Motivo declarado para la cita", example = "Chequeo anual y refuerzo de vacunas")
    private String motivo;

    @Schema(description = "Nivel de triage asignado", example = "MEDIA")
    private String triageNivel;

    @Schema(description = "Información resumida del paciente")
    private PacienteSummary paciente;

    @Schema(description = "Información resumida del veterinario asignado")
    private VeterinarioSummary veterinario;

    /**
     * Resumen del paciente relacionado con la cita.
     */
    @Data
    @Builder
    @Schema(name = "CitaPacienteSummary", description = "Datos del paciente asociados a la cita")
    public static class PacienteSummary {

        @Schema(description = "Identificador del paciente", example = "10")
        private Long id;

        @Schema(description = "Nombre del paciente", example = "Bobby")
        private String nombre;

        @Schema(description = "Especie del paciente", example = "Perro")
        private String especie;

        @Schema(description = "Nombre completo del propietario", example = "Ana López")
        private String propietario;
    }

    /**
     * Resumen del veterinario que atenderá la cita.
     */
    @Data
    @Builder
    @Schema(name = "CitaVeterinarioSummary", description = "Datos básicos del veterinario asignado")
    public static class VeterinarioSummary {

        @Schema(description = "Identificador del veterinario", example = "3")
        private Long id;

        @Schema(description = "Nombre completo del veterinario", example = "Dr. Martínez")
        private String nombreCompleto;

        @Schema(description = "Especialidad declarada", example = "Medicina General")
        private String especialidad;
    }
}


