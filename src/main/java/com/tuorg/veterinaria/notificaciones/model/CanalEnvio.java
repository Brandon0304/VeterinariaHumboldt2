package com.tuorg.veterinaria.notificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase base que representa un canal de envío de notificaciones.
 * 
 * Esta clase implementa el patrón Strategy, permitiendo diferentes
 * estrategias de envío (Email, App) sin modificar el código cliente.
 * 
 * Nota: En JPA, no podemos usar @Entity en clases abstractas, por lo que
 * esta clase es concreta pero las implementaciones específicas usan
 * @PrimaryKeyJoinColumn para la herencia.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "canales_envio", schema = "public")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanalEnvio {

    /**
     * Identificador único del canal (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_canal")
    private Long idCanal;

    /**
     * Nombre del canal (EMAIL, APP).
     */
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    /**
     * Configuración específica del canal en formato JSON.
     * Almacena parámetros de configuración según el tipo de canal.
     */
    @Column(name = "configuracion", columnDefinition = "JSONB")
    private String configuracion;

    /**
     * Método para enviar una notificación (Strategy pattern).
     * 
     * Las clases hijas deben sobrescribir este método para proporcionar
     * su propia lógica de envío.
     * 
     * @param notificacion Notificación a enviar
     * @return true si el envío fue exitoso, false en caso contrario
     */
    public boolean enviar(Notificacion notificacion) {
        // Implementación por defecto (debe ser sobrescrita por las clases hijas)
        throw new UnsupportedOperationException("Este método debe ser implementado por las clases hijas");
    }
}

