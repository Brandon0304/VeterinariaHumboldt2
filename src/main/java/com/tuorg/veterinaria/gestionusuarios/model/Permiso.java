package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un permiso en el sistema.
 * 
 * Los permisos definen acciones específicas que un usuario puede realizar.
 * Los permisos se asignan a roles, y los usuarios heredan los permisos
 * de su rol asignado.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "permisos", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permiso {

    /**
     * Identificador único del permiso (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long idPermiso;

    /**
     * Nombre del permiso (ej: CREAR_USUARIO, VER_REPORTES).
     * Debe ser único en toda la tabla.
     */
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Descripción detallada del permiso y qué acción permite.
     */
    @Column(name = "descripcion", length = 255)
    private String descripcion;
}

