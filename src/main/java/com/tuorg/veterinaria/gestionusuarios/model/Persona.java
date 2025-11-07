package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase abstracta que representa una persona en el sistema.
 *
 * Esta clase actúa como entidad base para todas las personas
 * que interactúan con el sistema (usuarios, clientes, etc.).
 *
 * Utiliza la estrategia de herencia JOINED para mapear la jerarquía
 * de clases a tablas separadas en la base de datos.
 *
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "personas", schema = "public")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Persona {

    /**
     * Identificador único de la persona (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long idPersona;

    /**
     * Nombre de la persona.
     */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Apellido de la persona.
     */
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    /**
     * Correo electrónico de la persona.
     * Debe ser único en toda la tabla (constraint UNIQUE).
     */
    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    /**
     * Teléfono de contacto de la persona.
     */
    @Column(name = "telefono", length = 30)
    private String telefono;

    /**
     * Dirección de residencia de la persona.
     */
    @Column(name = "direccion", length = 255)
    private String direccion;
}

