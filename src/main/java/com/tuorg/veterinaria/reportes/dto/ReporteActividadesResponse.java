package com.tuorg.veterinaria.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para reporte de actividades veterinarias.
 * 
 * Contiene estadísticas de servicios realizados.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteActividadesResponse {
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    private Long totalConsultas;
    private Long totalVacunaciones;
    private Long totalDesparasitaciones;
    private Long totalInterenciones;
    
    private Long pacientesAtendidos;
    private Long pacientesNuevos;
    private Long pacientesRecurrentes;
    
    private Long serviciosPrestados;
    private Long serviciosCompletados;
    private Long serviciosPendientes;
    
    private String veterinarioMasActivo;
    private Long serviciosVeterinarioMasActivo;
}
