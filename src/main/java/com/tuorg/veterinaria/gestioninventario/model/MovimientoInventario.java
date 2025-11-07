package com.tuorg.veterinaria.gestioninventario.model;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa un movimiento de inventario.
 * 
 * Esta clase implementa el patrón Command para registrar
 * movimientos de entrada (IN), salida (OUT) y ajustes (AJUSTE).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "movimientos_inventario", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventario {

    /**
     * Identificador único del movimiento (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;

    /**
     * Producto afectado por el movimiento.
     * Relación Many-to-One con la entidad Producto.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /**
     * Tipo de movimiento: IN (entrada), OUT (salida), AJUSTE (ajuste).
     * Debe cumplir con el constraint CHECK en la base de datos.
     */
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private String tipoMovimiento;

    /**
     * Cantidad movida (positiva para entradas, negativa para salidas).
     */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /**
     * Fecha y hora del movimiento.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    /**
     * Proveedor asociado (solo para movimientos de entrada).
     * Relación Many-to-One con la entidad Proveedor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    /**
     * Referencia del movimiento (número de compra, nota, etc.).
     */
    @Column(name = "referencia", length = 100)
    private String referencia;

    /**
     * Usuario que realizó el movimiento.
     * Relación Many-to-One con la entidad Usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}

