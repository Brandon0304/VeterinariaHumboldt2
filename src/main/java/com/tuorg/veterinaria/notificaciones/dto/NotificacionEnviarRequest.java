package com.tuorg.veterinaria.notificaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * DTO que encapsula los datos para enviar una notificación inmediatamente.
 */
@Data
@Schema(name = "NotificacionEnviarRequest", description = "Parámetros para ejecutar el envío inmediato de una notificación")
public class NotificacionEnviarRequest {

    @NotBlank(message = "El tipo de notificación es obligatorio")
    @Size(max = 50, message = "El tipo de notificación no puede superar 50 caracteres")
    @Schema(description = "Tipo de la notificación (recordatorio, alerta, info)", example = "alerta", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tipo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Schema(description = "Mensaje que se enviará al destinatario", example = "Se ha detectado una baja en el inventario de vacunas.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mensaje;

    @NotNull(message = "El identificador del canal es obligatorio")
    @Schema(description = "Identificador del canal de envío (EMAIL, SMS, APP)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long canalId;

    @Schema(description = "Datos dinámicos para personalizar la notificación", example = "{\"nivel\":\"alto\",\"producto\":\"Vacuna Rabia\"}")
    private Map<String, Object> datos;
}


