package com.tuorg.veterinaria.prestacioneservicios.model;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.gestionpacientes.model.Paciente;
import com.tuorg.veterinaria.gestionusuarios.model.UsuarioVeterinario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa una cita médica.
 * 
 * Esta clase almacena información sobre las citas programadas
 * entre pacientes y veterinarios.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "citas", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    /**
     * Identificador único de la cita (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Long idCita;

    /**
     * Paciente de la cita.
     * Relación Many-to-One con la entidad Paciente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    /**
     * Veterinario asignado a la cita.
     * Relación Many-to-One con la entidad UsuarioVeterinario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private UsuarioVeterinario veterinario;

    /**
     * Fecha y hora de la cita.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Tipo de servicio de la cita.
     */
    @Column(name = "tipo_servicio", length = 50)
    private String tipoServicio;

    /**
     * Estado de la cita: PROGRAMADA, REALIZADA, CANCELADA.
     * Debe cumplir con el constraint CHECK en la base de datos.
     */
    @Column(name = "estado", nullable = false, length = 30)
    private String estado = AppConstants.ESTADO_CITA_PROGRAMADA;

    /**
     * Motivo de la cita.
     */
    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    /**
     * Nivel de triage (urgencia) de la cita.
     */
    @Column(name = "triage_nivel", length = 30)
    private String triageNivel;
}

