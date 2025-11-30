package com.tuorg.veterinaria.notificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación básica del canal SMS (stub).
 * 
 * Esta clase es un placeholder que simula el envío de SMS
 * escribiendo en los logs. No realiza envíos reales.
 * 
 * Para implementar envíos reales de SMS, se requeriría:
 * - Integración con proveedor SMS (Twilio, AWS SNS, etc.) - REQUIERE PAGO
 * - Configuración de credenciales de API
 * - Gestión de costos por mensaje
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Slf4j
@Entity
@Table(name = "canales_app", schema = "public")
@PrimaryKeyJoinColumn(name = "id_canal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanalSMS extends CanalEnvio {

    /**
     * Tipo de dispositivo (iOS, Android, Web)
     */
    @Column(name = "tipo_dispositivo", length = 50)
    private String tipoDispositivo;

    /**
     * Token del dispositivo para push notifications
     */
    @Column(name = "token_dispositivo", length = 500)
    private String tokenDispositivo;

    /**
     * Implementación stub que simula el envío de SMS.
     * 
     * @param notificacion Notificación a enviar
     * @return true (siempre simula éxito)
     */
    @Override
    public boolean enviar(Notificacion notificacion) {
        log.info("📱 [SIMULACIÓN] Enviando SMS:");
        log.info("   Destinatario: {}", notificacion.getUsuarioReceptor() != null ? 
            notificacion.getUsuarioReceptor().getEmail() : "Sin usuario");
        log.info("   Mensaje: {}", notificacion.getMensaje());
        log.info("   Tipo: {}", notificacion.getTipo());
        log.info("   ⚠️ Nota: Envío real de SMS requiere integración con proveedor de pago");
        
        // Simular éxito
        return true;
    }
}

