package com.tuorg.veterinaria.gestionpacientes.model;

import com.tuorg.veterinaria.gestionusuarios.model.UsuarioVeterinario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidad que representa un registro de vacunación de un paciente.
 * 
 * Esta clase almacena información sobre las vacunas aplicadas
 * a las mascotas y programa las próximas dosis.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "vacunaciones", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vacunacion {

    /**
     * Identificador único de la vacunación (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vacunacion")
    private Long idVacunacion;

    /**
     * Paciente al que se le aplicó la vacuna.
     * Relación Many-to-One con la entidad Paciente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    /**
     * Tipo de vacuna aplicada.
     */
    @Column(name = "tipo_vacuna", nullable = false, length = 100)
    private String tipoVacuna;

    /**
     * Fecha en que se aplicó la vacuna.
     */
    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    /**
     * Fecha programada para la próxima dosis.
     */
    @Column(name = "proxima_dosis")
    private LocalDate proximaDosis;

    /**
     * Veterinario que aplicó la vacuna.
     * Relación Many-to-One con la entidad UsuarioVeterinario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id")
    private UsuarioVeterinario veterinario;
}

