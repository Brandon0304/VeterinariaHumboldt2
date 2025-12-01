package com.tuorg.veterinaria.reportes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Entidad que representa un reporte del sistema.
 * 
 * Esta clase implementa el patrón Facade para simplificar la generación
 * de reportes y dashboards, agrupando múltiples consultas complejas.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "reportes", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    /**
     * Identificador único del reporte (clave primaria).
     * Se genera automáticamente mediante BIGSERIAL en PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Long idReporte;

    /**
     * Nombre del reporte.
     */
    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    /**
     * Tipo de reporte (diario, mensual, anual, personalizado).
     */
    @Column(name = "tipo", length = 50)
    private String tipo;

    /**
     * Fecha y hora de generación del reporte.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    /**
     * Usuario o sistema que generó el reporte.
     * Puede ser null si fue generado automáticamente.
     */
    @Column(name = "generado_por")
    private Long generadoPor;

    /**
     * Parámetros utilizados para generar el reporte en formato JSON.
     * Ejemplo: {"fechaInicio": "2024-01-01", "fechaFin": "2024-01-31"}
     */
    @Column(name = "parametros", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private String parametros;
}

