package com.tuorg.veterinaria.notificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Implementación concreta de CanalEnvio para notificaciones en la app.
 * 
 * Esta clase extiende CanalEnvio e implementa la estrategia de envío
 * de notificaciones push dentro de la aplicación (Strategy pattern).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "canales_app", schema = "public")
@PrimaryKeyJoinColumn(name = "id_canal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanalApp extends CanalEnvio {

    /**
     * Tópico de la aplicación para notificaciones push.
     */
    @Column(name = "app_topic", length = 150)
    private String appTopic;

    /**
     * Implementación del método enviar para notificaciones en app.
     * 
     * @param notificacion Notificación a enviar
     * @return true si el envío fue exitoso, false en caso contrario
     */
    @Override
    public boolean enviar(Notificacion notificacion) {
        // Nota: El envío real de notificación push
        // se implementará cuando se requiera la funcionalidad completa de notificaciones
        System.out.println("Enviando notificación push al tópico " + appTopic + ": " + notificacion.getMensaje());
        return true;
    }
}

