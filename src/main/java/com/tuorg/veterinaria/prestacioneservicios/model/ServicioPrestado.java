package com.tuorg.veterinaria.prestacioneservicios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un servicio que ha sido prestado.
 * 
 * Esta clase almacena información sobre la ejecución real de un servicio,
 * incluyendo insumos consumidos y costo total.
 * 
 * Implementa el patrón Factory/Builder para su creación.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "servicios_prestados", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicioPrestado {

    /**
     * Identificador único del servicio prestado (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestado")
    private Long idPrestado;

    /**
     * Cita asociada al servicio prestado.
     * Relación Many-to-One con la entidad Cita.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    /**
     * Servicio que fue prestado.
     * Relación Many-to-One con la entidad Servicio.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    /**
     * Fecha y hora de ejecución del servicio.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_ejecucion", nullable = false)
    private LocalDateTime fechaEjecucion;

    /**
     * Observaciones sobre la ejecución del servicio.
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Costo total del servicio prestado.
     * Debe ser mayor o igual a cero (constraint CHECK).
     */
    @Column(name = "costo_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoTotal;

    /**
     * Insumos consumidos durante el servicio en formato JSON.
     * Ejemplo: [{"productoId": 1, "cantidad": 2, "precio": 10.50}, ...]
     */
    @Column(name = "insumos_consumidos", columnDefinition = "JSONB")
    private String insumosConsumidos;
}

