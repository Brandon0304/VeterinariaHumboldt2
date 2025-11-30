package com.tuorg.veterinaria.gestioninventario.model;

import com.tuorg.veterinaria.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Entidad que representa un producto en el inventario.
 * 
 * Esta clase almacena información sobre los productos disponibles
 * en el inventario de la clínica veterinaria.
 * Extiende de Auditable para trazabilidad automática.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "productos", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto extends Auditable {

    /**
     * Identificador único del producto (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    /**
     * SKU (Stock Keeping Unit) único del producto.
     * Debe ser único en toda la tabla (constraint UNIQUE).
     */
    @Column(name = "sku", nullable = false, unique = true, length = 60)
    private String sku;

    /**
     * Nombre del producto.
     */
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    /**
     * Descripción detallada del producto.
     */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Tipo de producto (medicamento, insumo, producto).
     */
    @Column(name = "tipo", length = 50)
    private String tipo;

    /**
     * Stock actual del producto.
     * Debe ser mayor o igual a cero (constraint CHECK).
     */
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    /**
     * Precio unitario del producto.
     * Debe ser mayor o igual a cero (constraint CHECK).
     */
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    /**
     * Unidad de medida (ej: unidad, kg, litro).
     */
    @Column(name = "um", length = 20)
    private String um;

    /**
     * Metadatos adicionales en formato JSON.
     * Puede incluir información como fecha de creación, proveedor preferido, etc.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadatos", columnDefinition = "JSONB")
    private Map<String, Object> metadatos;
}

