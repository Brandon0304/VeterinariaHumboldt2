package com.tuorg.veterinaria.notificaciones.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.notificaciones.dto.NotificacionEnviarRequest;
import com.tuorg.veterinaria.notificaciones.dto.NotificacionProgramarRequest;
import com.tuorg.veterinaria.notificaciones.dto.NotificacionResponse;
import com.tuorg.veterinaria.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de notificaciones.
 * 
 * Este controlador expone endpoints para programar y enviar notificaciones
 * utilizando el patrón Strategy para diferentes canales.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    /**
     * Servicio de gestión de notificaciones.
     */
    private final NotificacionService notificacionService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param notificacionService Servicio de notificaciones
     */
    @Autowired
    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    /**
     * Programa el envío de una notificación.
     * 
     * @param requestBody Cuerpo con la notificación y fecha de envío
     * @return Respuesta con la notificación programada
     */
    @PostMapping("/programar")
    public ResponseEntity<ApiResponse<NotificacionResponse>> programar(
            @RequestBody @Valid NotificacionProgramarRequest request) {
        NotificacionResponse notificacionProgramada = notificacionService.programarEnvio(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notificación programada exitosamente", notificacionProgramada));
    }

    /**
     * Envía una notificación inmediatamente.
     * 
     * @param requestBody Cuerpo con la notificación y canal de envío
     * @return Respuesta con la notificación enviada
     */
    @PostMapping("/enviar")
    public ResponseEntity<ApiResponse<NotificacionResponse>> enviar(
            @RequestBody @Valid NotificacionEnviarRequest request) {
        NotificacionResponse notificacionEnviada = notificacionService.enviarAhora(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notificación enviada exitosamente", notificacionEnviada));
    }

    /**
     * Obtiene notificaciones pendientes.
     * 
     * @return Respuesta con la lista de notificaciones pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<List<NotificacionResponse>>> obtenerPendientes() {
        List<NotificacionResponse> notificaciones = notificacionService.obtenerPendientes();
        return ResponseEntity.ok(ApiResponse.success("Notificaciones pendientes obtenidas exitosamente", notificaciones));
    }

    /**
     * Obtiene todas las notificaciones.
     * 
     * @return Respuesta con la lista de notificaciones
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificacionResponse>>> obtenerTodas() {
        List<NotificacionResponse> notificaciones = notificacionService.obtenerTodas();
        return ResponseEntity.ok(ApiResponse.success("Notificaciones obtenidas exitosamente", notificaciones));
    }
}

