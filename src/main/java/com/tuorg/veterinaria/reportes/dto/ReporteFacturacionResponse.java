package com.tuorg.veterinaria.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para reporte de facturación.
 * 
 * Contiene estadísticas de ingresos y facturas por período.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteFacturacionResponse {
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    private Long totalFacturas;
    private Long facturasEmitidas;
    private Long facturasPagadas;
    private Long facturasPendientes;
    
    private BigDecimal ingresosBrutos;
    private BigDecimal ingresosPagados;
    private BigDecimal ingresosPendientes;
    
    private BigDecimal gastosInventario;
    private BigDecimal utilidadNeta;
    
    private Double tasaPago;
    private Double margenUtilidad;
    
    private Long totalClientes;
    private Long clientesActivos;
}
