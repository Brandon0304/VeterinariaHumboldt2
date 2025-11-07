package com.tuorg.veterinaria.gestionpacientes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidad que representa un registro de desparasitación de un paciente.
 * 
 * Esta clase almacena información sobre las desparasitaciones realizadas
 * a las mascotas y programa las próximas aplicaciones.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "desparasitaciones", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Desparasitacion {

    /**
     * Identificador único de la desparasitación (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_desparasitacion")
    private Long idDesparasitacion;

    /**
     * Paciente al que se le realizó la desparasitación.
     * Relación Many-to-One con la entidad Paciente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    /**
     * Producto utilizado para la desparasitación.
     */
    @Column(name = "producto_usado", nullable = false, length = 150)
    private String productoUsado;

    /**
     * Fecha en que se realizó la desparasitación.
     */
    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    /**
     * Fecha programada para la próxima desparasitación.
     */
    @Column(name = "proxima_aplicacion")
    private LocalDate proximaAplicacion;
}

