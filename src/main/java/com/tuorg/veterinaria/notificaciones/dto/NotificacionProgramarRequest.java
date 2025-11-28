package com.tuorg.veterinaria.notificaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO que representa los datos necesarios para programar una notificación.
 */
@Data
@Schema(name = "NotificacionProgramarRequest", description = "Parámetros requeridos para programar el envío diferido de una notificación")
public class NotificacionProgramarRequest {

    @NotBlank(message = "El tipo de notificación es obligatorio")
    @Size(max = 50, message = "El tipo de notificación no puede superar 50 caracteres")
    @Schema(description = "Tipo de la notificación (recordatorio, alerta, info)", example = "recordatorio", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tipo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Schema(description = "Mensaje a enviar al destinatario", example = "Recuerda que tienes una cita mañana a las 9:00 a.m.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mensaje;

    @NotNull(message = "La fecha y hora de envío es obligatoria")
    @Future(message = "La fecha de envío debe ser futura")
    @Schema(description = "Fecha y hora programada para realizar el envío", example = "2025-11-15T08:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fechaEnvio;

    @Schema(description = "Datos dinámicos para renderizar plantillas", example = "{\"paciente\":\"Bobby\",\"veterinario\":\"Dr. Martínez\"}")
    private Map<String, Object> datos;
}


