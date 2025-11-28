package com.tuorg.veterinaria.configuracion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un parámetro de configuración del sistema.
 * 
 * Esta clase implementa el patrón Singleton a nivel de servicio para
 * proporcionar una única fuente de configuración en tiempo de ejecución.
 * 
 * Los parámetros del sistema permiten configurar comportamientos
 * sin necesidad de reiniciar la aplicación.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "parametros_sistema", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParametroSistema {

    /**
     * Identificador único del parámetro (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Long idParametro;

    /**
     * Clave única del parámetro (ej: "notificaciones.email.enabled").
     * Debe ser única en toda la tabla.
     */
    @Column(name = "clave", nullable = false, unique = true, length = 150)
    private String clave;

    /**
     * Valor del parámetro.
     * Puede almacenar valores simples o JSON para configuraciones complejas.
     */
    @Column(name = "valor", nullable = false, length = 500)
    private String valor;

    /**
     * Descripción del parámetro y su propósito.
     */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Ámbito de aplicación del parámetro.
     * Ejemplos: 'notificaciones', 'inventario', 'global'
     */
    @Column(name = "aplicacion", length = 50)
    private String aplicacion;
}

