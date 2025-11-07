package com.tuorg.veterinaria.gestioninventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un proveedor de productos.
 * 
 * Esta clase almacena información sobre los proveedores que
 * suministran productos al inventario de la clínica.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "proveedores", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    /**
     * Identificador único del proveedor (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

    /**
     * Nombre del proveedor.
     */
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    /**
     * Persona de contacto en el proveedor.
     */
    @Column(name = "contacto", length = 100)
    private String contacto;

    /**
     * Teléfono de contacto del proveedor.
     */
    @Column(name = "telefono", length = 30)
    private String telefono;

    /**
     * Dirección del proveedor.
     */
    @Column(name = "direccion", length = 255)
    private String direccion;

    /**
     * Correo electrónico del proveedor.
     */
    @Column(name = "correo", length = 150)
    private String correo;
}

