package com.tuorg.veterinaria.gestioninventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa una alerta de inventario.
 * 
 * Esta clase almacena alertas generadas cuando el stock de un producto
 * alcanza niveles críticos.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "alertas_inventario", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertaInventario {

    /**
     * Identificador único de la alerta (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long idAlerta;

    /**
     * Producto que generó la alerta.
     * Relación Many-to-One con la entidad Producto.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /**
     * Nivel de stock que activó la alerta.
     */
    @Column(name = "nivel_stock", nullable = false)
    private Integer nivelStock;

    /**
     * Fecha y hora en que se generó la alerta.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_generada", nullable = false)
    private LocalDateTime fechaGenerada;

    /**
     * Mensaje descriptivo de la alerta.
     */
    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;
}

