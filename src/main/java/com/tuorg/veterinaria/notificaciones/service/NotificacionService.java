package com.tuorg.veterinaria.notificaciones.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.notificaciones.dto.NotificacionEnviarRequest;
import com.tuorg.veterinaria.notificaciones.dto.NotificacionProgramarRequest;
import com.tuorg.veterinaria.notificaciones.dto.NotificacionResponse;
import com.tuorg.veterinaria.notificaciones.model.CanalEnvio;
import com.tuorg.veterinaria.notificaciones.model.Notificacion;
import com.tuorg.veterinaria.common.event.NotificacionEvent;
import com.tuorg.veterinaria.notificaciones.repository.CanalEnvioRepository;
import com.tuorg.veterinaria.notificaciones.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Servicio para la gestión de notificaciones.
 *
 * Implementa el patrón Strategy para delegar el envío en los canales concretos
 * y expone DTOs desacoplados de las entidades JPA.
 */
@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final CanalEnvioRepository canalEnvioRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public NotificacionService(NotificacionRepository notificacionRepository,
                               CanalEnvioRepository canalEnvioRepository,
                               ObjectMapper objectMapper,
                               ApplicationEventPublisher eventPublisher) {
        this.notificacionRepository = notificacionRepository;
        this.canalEnvioRepository = canalEnvioRepository;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Programa el envío de una notificación para una fecha futura.
     */
    @Transactional
    public NotificacionResponse programarEnvio(NotificacionProgramarRequest request) {
        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(request.getTipo());
        notificacion.setMensaje(request.getMensaje());
        notificacion.setFechaEnvioProgramada(request.getFechaEnvio());
        notificacion.setEstado(AppConstants.ESTADO_NOTIFICACION_PENDIENTE);
        notificacion.setDatos(toJson(request.getDatos()));

        Notificacion guardada = notificacionRepository.save(notificacion);
        
        // Publicar evento (Observer pattern)
        eventPublisher.publishEvent(new NotificacionEvent(this, guardada, "PROGRAMADA"));
        
        return mapToResponse(guardada);
    }

    /**
     * Envía una notificación inmediatamente usando el canal especificado (Strategy pattern).
     */
    @Transactional
    public NotificacionResponse enviarAhora(NotificacionEnviarRequest request) {
        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(request.getTipo());
        notificacion.setMensaje(request.getMensaje());
        notificacion.setEstado(AppConstants.ESTADO_NOTIFICACION_PENDIENTE);
        notificacion.setDatos(toJson(request.getDatos()));

        CanalEnvio canal = canalEnvioRepository.findById(request.getCanalId())
                .orElseThrow(() -> new ResourceNotFoundException("CanalEnvio", "id", request.getCanalId()));

        boolean enviado = canal.enviar(notificacion);

        notificacion.setFechaEnvioReal(LocalDateTime.now());
        notificacion.setEstado(enviado
                ? AppConstants.ESTADO_NOTIFICACION_ENVIADA
                : AppConstants.ESTADO_NOTIFICACION_FALLIDA);

        Notificacion guardada = notificacionRepository.save(notificacion);
        
        // Publicar evento (Observer pattern)
        String tipoEvento = enviado ? "ENVIADA" : "FALLIDA";
        eventPublisher.publishEvent(new NotificacionEvent(this, guardada, tipoEvento));
        
        return mapToResponse(guardada);
    }

    /**
     * Obtiene notificaciones pendientes programadas para enviar.
     */
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerPendientes() {
        return notificacionRepository.findNotificacionesPendientes(LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Obtiene todas las notificaciones.
     */
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerTodas() {
        return notificacionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private NotificacionResponse mapToResponse(Notificacion notificacion) {
        return NotificacionResponse.builder()
                .id(notificacion.getIdNotificacion())
                .tipo(notificacion.getTipo())
                .mensaje(notificacion.getMensaje())
                .estado(notificacion.getEstado())
                .fechaEnvioProgramada(notificacion.getFechaEnvioProgramada())
                .fechaEnvioReal(notificacion.getFechaEnvioReal())
                .datos(toMap(notificacion.getDatos()))
                .build();
    }

    private String toJson(Map<String, Object> datos) {
        if (datos == null || datos.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(datos);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Los datos de la notificación no tienen un formato JSON válido");
        }
    }

    private Map<String, Object> toMap(String datosJson) {
        if (datosJson == null || datosJson.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(datosJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }
}


