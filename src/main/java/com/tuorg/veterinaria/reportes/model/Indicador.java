package com.tuorg.veterinaria.reportes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad que representa un indicador clave de rendimiento (KPI).
 * 
 * Esta clase almacena indicadores que se evalúan para determinar
 * tendencias y rendimiento del sistema.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "indicadores", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Indicador {

    /**
     * Identificador único del indicador (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_indicador")
    private Long idIndicador;

    /**
     * Nombre del indicador.
     */
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    /**
     * Descripción del indicador y su propósito.
     */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Valor actual del indicador.
     */
    @Column(name = "valor_actual", precision = 18, scale = 4)
    private BigDecimal valorActual;
}

