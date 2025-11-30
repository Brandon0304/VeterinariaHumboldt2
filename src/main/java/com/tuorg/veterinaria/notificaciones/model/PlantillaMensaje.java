package com.tuorg.veterinaria.notificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una plantilla de mensaje para notificaciones.
 * 
 * Esta clase almacena plantillas reutilizables que pueden ser renderizadas
 * con datos dinámicos para generar mensajes personalizados.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "plantillas_mensajes", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaMensaje {

    /**
     * Identificador único de la plantilla (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla")
    private Long idPlantilla;

    /**
     * Nombre de la plantilla.
     */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Asunto del mensaje (para emails).
     */
    @Column(name = "asunto", length = 150)
    private String asunto;

    /**
     * Cuerpo del mensaje con variables placeholders.
     * Ejemplo: "Hola {{nombre}}, su cita es el {{fecha}}"
     */
    @Column(name = "cuerpo", columnDefinition = "TEXT", nullable = false)
    private String cuerpo;

    /**
     * Variables utilizadas en la plantilla en formato JSON.
     * Ejemplo: ["nombre", "fecha", "hora"]
     */
    @Column(name = "variables", columnDefinition = "JSONB")
    private String variables;
}

