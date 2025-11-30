package com.tuorg.veterinaria.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para reporte de citas.
 * 
 * Contiene estadísticas de citas por período.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteCitasResponse {
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    private Long totalCitas;
    private Long citasCompletadas;
    private Long citasCanceladas;
    private Long citasEnProceso;
    
    private Long caudanoPacientes;
    private Long totalVeterinarios;
    
    private Double tasaCompletitud;
    private Double tasaCancelacion;
    
    private BigDecimal ingresosTotales;
    private BigDecimal ingresoPromedio;
}
