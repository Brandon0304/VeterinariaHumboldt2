package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para la respuesta de registro de consulta.
 * 
 * Contiene la información de la consulta registrada incluyendo
 * timestamp y datos del veterinario.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de una consulta registrada")
public class ConsultaResponse {

    @Schema(description = "ID del registro médico", example = "123")
    private Long registroId;

    @Schema(description = "ID de la cita asociada", example = "1")
    private Long citaId;

    @Schema(description = "ID del paciente", example = "5")
    private Long pacienteId;

    @Schema(description = "Nombre del paciente", example = "Firulais")
    private String nombrePaciente;

    @Schema(description = "Motivo de consulta", example = "Dolor abdominal")
    private String motivo;

    @Schema(description = "Diagnóstico", example = "Gastroenteritis")
    private String diagnostico;

    @Schema(description = "Signos vitales", example = "{\"temperatura\": 38.5}")
    private Map<String, Object> signosVitales;

    @Schema(description = "Tratamiento", example = "Reposo 48h")
    private String tratamiento;

    @Schema(description = "Observaciones", example = "Próxima cita en 1 semana")
    private String observaciones;

    @Schema(description = "Veterinario que atendió", example = "Dr. Juan Pérez")
    private String nombreVeterinario;

    @Schema(description = "Fecha de registro", example = "2025-03-12T14:30:00")
    private LocalDateTime fechaRegistro;

    @Schema(description = "Insumos utilizados", example = "[{\"productoId\": 5, \"nombre\": \"Antibiótico\", \"cantidad\": 1}]")
    private List<Map<String, Object>> insumosUsados;

    @Schema(description = "Archivos adjuntos", example = "[\"radiografia.pdf\"]")
    private List<String> archivos;
}
