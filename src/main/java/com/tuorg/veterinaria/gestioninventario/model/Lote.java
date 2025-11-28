package com.tuorg.veterinaria.gestioninventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidad que representa un lote de productos.
 * 
 * Esta clase almacena información sobre lotes de productos,
 * especialmente importante para productos con fecha de vencimiento.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "lotes", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lote {

    /**
     * Identificador único del lote (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Long idLote;

    /**
     * Producto al que pertenece el lote.
     * Relación Many-to-One con la entidad Producto.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /**
     * Fecha de vencimiento del lote.
     * Debe ser mayor o igual a la fecha actual (constraint CHECK).
     */
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    /**
     * Cantidad de productos en el lote.
     * Debe ser mayor que cero (constraint CHECK).
     */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /**
     * Número de lote (identificador del lote del proveedor).
     */
    @Column(name = "numero_lote", length = 100)
    private String numeroLote;
}

