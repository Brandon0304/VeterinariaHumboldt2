package com.tuorg.veterinaria.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO para el dashboard del administrador con métricas de toda la clínica.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard completo del administrador con métricas y estadísticas")
public class AdminDashboardResponse {

    @Schema(description = "Resumen general de la clínica")
    private ResumenGeneral resumenGeneral;

    @Schema(description = "Estadísticas financieras")
    private EstadisticasFinancieras finanzas;

    @Schema(description = "Métricas de pacientes y citas")
    private MetricasPacientes pacientes;

    @Schema(description = "Estado del inventario")
    private EstadoInventario inventario;

    @Schema(description = "Rendimiento del personal")
    private RendimientoPersonal personal;

    @Schema(description = "Datos para gráficos mensuales")
    private DatosGraficos graficos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenGeneral {
        private Long totalPacientes;
        private Long totalClientes;
        private Long totalCitas;
        private Long totalFacturas;
        private BigDecimal ingresosTotales;
        private Long citasPendientes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadisticasFinancieras {
        private BigDecimal ingresosMesActual;
        private BigDecimal ingresosMesAnterior;
        private BigDecimal porcentajeCrecimiento;
        private Long facturasPendientes;
        private BigDecimal montoFacturasPendientes;
        private Long facturasRealizadas;
        private BigDecimal promedioIngresoPorCita;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricasPacientes {
        private Long pacientesActivos;
        private Long pacientesNuevosEsteMes;
        private Long citasRealizadasEsteMes;
        private Long citasPendientes;
        private Long citasCanceladasEsteMes;
        private Long vacunacionesPendientes;
        private Long desparasitacionesPendientes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadoInventario {
        private Long totalProductos;
        private Long productosStockBajo;
        private Long productosAgotados;
        private BigDecimal valorTotalInventario;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RendimientoPersonal {
        private Long totalVeterinarios;
        private Long totalSecretarios;
        private Map<String, Long> citasPorVeterinario;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatosGraficos {
        private List<DatoMensual> ingresosPorMes;
        private List<DatoMensual> citasPorMes;
        private List<DistribucionPorTipo> distribucionServicios;
        private List<TendenciaClientes> tendenciaClientes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatoMensual {
        private String mes;
        private Integer anio;
        private BigDecimal valor;
        private Long cantidad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistribucionPorTipo {
        private String tipo;
        private Long cantidad;
        private BigDecimal porcentaje;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TendenciaClientes {
        private String periodo;
        private Long nuevos;
        private Long activos;
    }
}
