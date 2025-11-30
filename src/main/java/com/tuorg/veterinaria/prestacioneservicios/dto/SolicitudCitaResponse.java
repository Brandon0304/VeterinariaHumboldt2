package com.tuorg.veterinaria.prestacioneservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO para respuesta de solicitud de cita.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response de solicitud de cita")
public class SolicitudCitaResponse {

    @Schema(description = "ID de la solicitud", example = "1")
    private Long idSolicitud;

    @Schema(description = "ID del cliente")
    private Long clienteId;

    @Schema(description = "Nombre del cliente")
    private String nombreCliente;

    @Schema(description = "ID del paciente (mascota)")
    private Long pacienteId;

    @Schema(description = "Nombre del paciente")
    private String nombrePaciente;

    @Schema(description = "Fecha solicitada")
    private LocalDate fechaSolicitada;

    @Schema(description = "Hora solicitada")
    private LocalTime horaSolicitada;

    @Schema(description = "Tipo de servicio")
    private String tipoServicio;

    @Schema(description = "Motivo de la cita")
    private String motivo;

    @Schema(description = "Estado: PENDIENTE, APROBADA, RECHAZADA, CANCELADA")
    private String estado;

    @Schema(description = "Motivo del rechazo (si aplica)")
    private String motivoRechazo;

    @Schema(description = "ID de cita generada")
    private Long citaId;

    @Schema(description = "Observaciones")
    private String observaciones;

    @Schema(description = "Fecha de creación de la solicitud")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Última actualización")
    private LocalDateTime fechaActualizacion;

    // ==================== AUDIT TRAIL ====================

    @Schema(description = "ID del usuario que aprobó la solicitud")
    private Long aprobadoPor;

    @Schema(description = "Fecha y hora cuando fue aprobada")
    private LocalDateTime aprobadoEn;

    @Schema(description = "ID del usuario que rechazó la solicitud")
    private Long rechazadoPor;

    @Schema(description = "Fecha y hora cuando fue rechazada")
    private LocalDateTime rechazadoEn;

    @Schema(description = "ID del usuario que canceló la solicitud")
    private Long canceladoPor;

    @Schema(description = "Fecha y hora cuando fue cancelada")
    private LocalDateTime canceladoEn;
}
