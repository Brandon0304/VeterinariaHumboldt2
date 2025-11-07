package com.tuorg.veterinaria.notificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Implementación concreta de CanalEnvio para envío por email.
 * 
 * Esta clase extiende CanalEnvio e implementa la estrategia de envío
 * por correo electrónico (Strategy pattern).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "canales_email", schema = "public")
@PrimaryKeyJoinColumn(name = "id_canal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanalEmail extends CanalEnvio {

    /**
     * Servidor SMTP para el envío de emails.
     */
    @Column(name = "smtp_server", length = 150)
    private String smtpServer;

    /**
     * Dirección de correo del remitente.
     */
    @Column(name = "from_address", length = 150)
    private String fromAddress;

    /**
     * Implementación del método enviar para email.
     * 
     * @param notificacion Notificación a enviar
     * @return true si el envío fue exitoso, false en caso contrario
     */
    @Override
    public boolean enviar(Notificacion notificacion) {
        // TODO: Implementar envío real de email usando JavaMailSender
        // Por ahora retornamos true como simulación
        System.out.println("Enviando email a través de " + smtpServer + ": " + notificacion.getMensaje());
        return true;
    }
}

