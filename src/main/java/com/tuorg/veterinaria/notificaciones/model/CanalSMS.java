package com.tuorg.veterinaria.notificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Implementación concreta de CanalEnvio para envío por SMS.
 * 
 * Esta clase extiende CanalEnvio e implementa la estrategia de envío
 * por mensaje de texto SMS (Strategy pattern).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "canales_sms", schema = "public")
@PrimaryKeyJoinColumn(name = "id_canal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanalSMS extends CanalEnvio {

    /**
     * Proveedor de API para envío de SMS.
     */
    @Column(name = "proveedor_api", length = 150)
    private String proveedorApi;

    /**
     * Implementación del método enviar para SMS.
     * 
     * @param notificacion Notificación a enviar
     * @return true si el envío fue exitoso, false en caso contrario
     */
    @Override
    public boolean enviar(Notificacion notificacion) {
        // TODO: Implementar envío real de SMS usando API del proveedor
        // Por ahora retornamos true como simulación
        System.out.println("Enviando SMS a través de " + proveedorApi + ": " + notificacion.getMensaje());
        return true;
    }
}

