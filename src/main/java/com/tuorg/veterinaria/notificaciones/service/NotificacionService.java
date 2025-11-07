package com.tuorg.veterinaria.notificaciones.service;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.notificaciones.model.CanalEnvio;
import com.tuorg.veterinaria.notificaciones.model.Notificacion;
import com.tuorg.veterinaria.notificaciones.repository.CanalEnvioRepository;
import com.tuorg.veterinaria.notificaciones.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de notificaciones.
 * 
 * Este servicio utiliza el patrón Strategy para enviar notificaciones
 * a través de diferentes canales (Email, SMS, App) sin modificar
 * el código cliente.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class NotificacionService {

    /**
     * Repositorio de notificaciones.
     */
    private final NotificacionRepository notificacionRepository;

    /**
     * Repositorio de canales de envío.
     */
    private final CanalEnvioRepository canalEnvioRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param notificacionRepository Repositorio de notificaciones
     * @param canalEnvioRepository Repositorio de canales de envío
     */
    @Autowired
    public NotificacionService(NotificacionRepository notificacionRepository,
                              CanalEnvioRepository canalEnvioRepository) {
        this.notificacionRepository = notificacionRepository;
        this.canalEnvioRepository = canalEnvioRepository;
    }

    /**
     * Programa el envío de una notificación para una fecha futura.
     * 
     * @param notificacion Notificación a programar
     * @param fechaEnvio Fecha programada para el envío
     * @return Notificacion programada
     */
    @Transactional
    public Notificacion programarEnvio(Notificacion notificacion, LocalDateTime fechaEnvio) {
        notificacion.setFechaEnvioProgramada(fechaEnvio);
        notificacion.setEstado(AppConstants.ESTADO_NOTIFICACION_PENDIENTE);
        return notificacionRepository.save(notificacion);
    }

    /**
     * Envía una notificación inmediatamente usando el canal especificado (Strategy pattern).
     * 
     * @param notificacion Notificación a enviar
     * @param canalId ID del canal de envío
     * @return Notificacion enviada
     */
    @Transactional
    public Notificacion enviarAhora(Notificacion notificacion, Long canalId) {
        CanalEnvio canal = canalEnvioRepository.findById(canalId)
                .orElseThrow(() -> new ResourceNotFoundException("CanalEnvio", "id", canalId));

        // Usar Strategy pattern: el canal concreto decide cómo enviar
        boolean enviado = canal.enviar(notificacion);

        notificacion.setFechaEnvioReal(LocalDateTime.now());
        notificacion.setEstado(enviado ? 
                AppConstants.ESTADO_NOTIFICACION_ENVIADA : 
                AppConstants.ESTADO_NOTIFICACION_FALLIDA);

        return notificacionRepository.save(notificacion);
    }

    /**
     * Obtiene notificaciones pendientes programadas para enviar.
     * 
     * @return Lista de notificaciones pendientes
     */
    @Transactional(readOnly = true)
    public List<Notificacion> obtenerPendientes() {
        return notificacionRepository.findNotificacionesPendientes(LocalDateTime.now());
    }

    /**
     * Obtiene todas las notificaciones.
     * 
     * @return Lista de todas las notificaciones
     */
    @Transactional(readOnly = true)
    public List<Notificacion> obtenerTodas() {
        return notificacionRepository.findAll();
    }
}

