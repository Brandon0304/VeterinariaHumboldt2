package com.tuorg.veterinaria.reportes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa una estadística calculada.
 * 
 * Esta clase almacena valores estadísticos calculados para un período
 * específico, utilizados en reportes y dashboards.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "estadisticas", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estadistica {

    /**
     * Identificador único de la estadística (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadistica")
    private Long idEstadistica;

    /**
     * Nombre de la estadística.
     */
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    /**
     * Valor calculado de la estadística.
     */
    @Column(name = "valor", precision = 18, scale = 4)
    private BigDecimal valor;

    /**
     * Fecha de inicio del período de cálculo.
     */
    @Column(name = "periodo_inicio")
    private LocalDate periodoInicio;

    /**
     * Fecha de fin del período de cálculo.
     */
    @Column(name = "periodo_fin")
    private LocalDate periodoFin;
}

