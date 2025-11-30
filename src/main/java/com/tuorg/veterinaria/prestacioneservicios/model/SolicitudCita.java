package com.tuorg.veterinaria.prestacioneservicios.model;

import com.tuorg.veterinaria.common.audit.Auditable;
import com.tuorg.veterinaria.gestionpacientes.model.Paciente;
import com.tuorg.veterinaria.gestionusuarios.model.Cliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad que representa una solicitud de cita del portal del cliente.
 * 
 * Permite que los clientes soliciten citas directamente desde el portal,
 * requiriendo aprobación de secretario antes de confirmarse.
 * 
 * Estados: PENDIENTE, APROBADA, RECHAZADA, CANCELADA
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "solicitudes_citas", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudCita extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long idSolicitud;

    /**
     * Cliente que realiza la solicitud.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /**
     * Paciente (mascota) para la cita.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    /**
     * Fecha solicitada para la cita.
     */
    @Column(name = "fecha_solicitada", nullable = false)
    private LocalDate fechaSolicitada;

    /**
     * Hora solicitada para la cita.
     */
    @Column(name = "hora_solicitada", nullable = false)
    private LocalTime horaSolicitada;

    /**
     * Tipo de servicio solicitado.
     */
    @Column(name = "tipo_servicio", length = 100)
    private String tipoServicio;

    /**
     * Motivo o descripción de la cita.
     */
    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    /**
     * Estado de la solicitud: PENDIENTE, APROBADA, RECHAZADA, CANCELADA
     */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "PENDIENTE";

    /**
     * Motivo del rechazo (si aplica).
     */
    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    /**
     * ID de cita generada cuando se aprueba la solicitud.
     */
    @Column(name = "cita_id")
    private Long citaId;

    /**
     * Observaciones adicionales del cliente.
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // ==================== CAMPOS DE AUDIT TRAIL ====================

    /**
     * ID del usuario que aprobó la solicitud.
     */
    @Column(name = "aprobado_por")
    private Long aprobadoPor;

    /**
     * Fecha y hora cuando fue aprobada la solicitud.
     */
    @Column(name = "aprobado_en")
    private java.time.LocalDateTime aprobadoEn;

    /**
     * ID del usuario que rechazó la solicitud.
     */
    @Column(name = "rechazado_por")
    private Long rechazadoPor;

    /**
     * Fecha y hora cuando fue rechazada la solicitud.
     */
    @Column(name = "rechazado_en")
    private java.time.LocalDateTime rechazadoEn;

    /**
     * ID del usuario que canceló la solicitud.
     */
    @Column(name = "cancelado_por")
    private Long canceladoPor;

    /**
     * Fecha y hora cuando fue cancelada la solicitud.
     */
    @Column(name = "cancelado_en")
    private java.time.LocalDateTime canceladoEn;
}
