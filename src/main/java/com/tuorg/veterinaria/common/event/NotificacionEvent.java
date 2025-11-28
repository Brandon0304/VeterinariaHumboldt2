package com.tuorg.veterinaria.common.event;

import com.tuorg.veterinaria.notificaciones.model.Notificacion;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se publica cuando se envía una notificación.
 * 
 * Implementa el patrón Observer para notificar a los listeners
 * cuando ocurre un evento de notificación en el sistema.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public class NotificacionEvent extends ApplicationEvent {

    private final Notificacion notificacion;
    private final String tipoEvento; // "ENVIADA", "PROGRAMADA", "FALLIDA"

    /**
     * Constructor del evento.
     * 
     * @param source Fuente del evento (normalmente el servicio que lo publica)
     * @param notificacion Notificación relacionada con el evento
     * @param tipoEvento Tipo de evento (ENVIADA, PROGRAMADA, FALLIDA)
     */
    public NotificacionEvent(Object source, Notificacion notificacion, String tipoEvento) {
        super(source);
        this.notificacion = notificacion;
        this.tipoEvento = tipoEvento;
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }
}

