package com.tuorg.veterinaria.notificaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO de salida para representar una notificación.
 */
@Data
@Builder
@Schema(name = "NotificacionResponse", description = "Datos expuestos de una notificación")
public class NotificacionResponse {

    @Schema(description = "Identificador único de la notificación", example = "15")
    private Long id;

    @Schema(description = "Tipo de la notificación", example = "recordatorio")
    private String tipo;

    @Schema(description = "Mensaje enviado", example = "Recuerda la cita de Bobby mañana a las 9:00 a.m.")
    private String mensaje;

    @Schema(description = "Estado actual de la notificación", example = "ENVIADA")
    private String estado;

    @Schema(description = "Fecha programada para el envío", example = "2025-11-15T08:30:00")
    private LocalDateTime fechaEnvioProgramada;

    @Schema(description = "Fecha real en la que se realizó el envío", example = "2025-11-15T08:29:55")
    private LocalDateTime fechaEnvioReal;

    @Schema(description = "Datos adicionales asociados a la notificación", example = "{\"paciente\":\"Bobby\",\"veterinario\":\"Dr. Martínez\"}")
    private Map<String, Object> datos;
}


