package com.tuorg.veterinaria.notificaciones.model;

import com.tuorg.veterinaria.common.constants.AppConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación del sistema.
 * 
 * Esta clase almacena información sobre las notificaciones que se envían
 * a través de diferentes canales (Email, App).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "notificaciones", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    /**
     * Identificador único de la notificación (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    /**
     * Tipo de notificación (recordatorio, alerta, info).
     */
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    /**
     * Mensaje de la notificación.
     */
    @Column(name = "mensaje", columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    /**
     * Fecha y hora programada para el envío.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_envio_programada")
    private LocalDateTime fechaEnvioProgramada;

    /**
     * Fecha y hora real en que se envió la notificación.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_envio_real")
    private LocalDateTime fechaEnvioReal;

    /**
     * Estado de la notificación: PENDIENTE, ENVIADA, FALLIDA.
     * Debe cumplir con el constraint CHECK en la base de datos.
     */
    @Column(name = "estado", nullable = false, length = 30)
    private String estado = AppConstants.ESTADO_NOTIFICACION_PENDIENTE;

    /**
     * Plantilla de mensaje utilizada (opcional).
     * Relación Many-to-One con la entidad PlantillaMensaje.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantilla_id")
    private PlantillaMensaje plantilla;

    /**
     * Datos adicionales en formato JSON.
     * Puede incluir información contextual para el renderizado de plantillas.
     */
    @Column(name = "datos", columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private String datos;
}

