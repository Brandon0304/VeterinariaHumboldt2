package com.tuorg.veterinaria.reportes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.reportes.dto.EstadisticaResponse;
import com.tuorg.veterinaria.reportes.dto.ReporteRequest;
import com.tuorg.veterinaria.reportes.dto.ReporteResponse;
import com.tuorg.veterinaria.reportes.model.Estadistica;
import com.tuorg.veterinaria.reportes.model.Reporte;
import com.tuorg.veterinaria.reportes.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de reportes (Facade pattern).
 *
 * Coordina la generación de reportes combinando cálculos estadísticos y persistencia.
 */
@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final EstadisticaService estadisticaService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReporteService(ReporteRepository reporteRepository,
                          EstadisticaService estadisticaService,
                          ObjectMapper objectMapper) {
        this.reporteRepository = reporteRepository;
        this.estadisticaService = estadisticaService;
        this.objectMapper = objectMapper;
    }

    /**
     * Genera un reporte y retorna un DTO listo para ser expuesto en la API.
     */
    @Transactional
    public ReporteResponse generar(ReporteRequest request) {
        Reporte reporte = new Reporte();
        reporte.setNombre(request.getNombre());
        reporte.setTipo(request.getTipo());
        reporte.setGeneradoPor(request.getGeneradoPor());
        reporte.setFechaGeneracion(LocalDateTime.now());
        reporte.setParametros(toJson(request.getParametros()));

        List<Estadistica> estadisticas = estadisticaService.calcularEstadisticasParaReporte(
                request.getTipo(),
                request.getParametros() != null ? request.getParametros() : Collections.emptyMap()
        );

        Reporte guardado = reporteRepository.save(reporte);
        return mapToResponse(guardado, estadisticas);
    }

    /**
     * Exporta un reporte como PDF (implementación placeholder).
     */
    @Transactional(readOnly = true)
    public byte[] exportarPDF(Long reporteId) {
        reporteRepository.findById(reporteId)
                .orElseThrow(() -> new BusinessException("Reporte no encontrado para exportar a PDF"));
        // TODO: Implementar generación real de PDF con iText o JasperReports
        return new byte[0];
    }

    /**
     * Exporta un reporte como Excel (implementación placeholder).
     */
    @Transactional(readOnly = true)
    public byte[] exportarExcel(Long reporteId) {
        reporteRepository.findById(reporteId)
                .orElseThrow(() -> new BusinessException("Reporte no encontrado para exportar a Excel"));
        // TODO: Implementar generación real de Excel con Apache POI
        return new byte[0];
    }

    private ReporteResponse mapToResponse(Reporte reporte, List<Estadistica> estadisticas) {
        return ReporteResponse.builder()
                .id(reporte.getIdReporte())
                .nombre(reporte.getNombre())
                .tipo(reporte.getTipo())
                .fechaGeneracion(reporte.getFechaGeneracion())
                .generadoPor(reporte.getGeneradoPor())
                .parametros(toMap(reporte.getParametros()))
                .estadisticas(estadisticas.stream()
                        .map(this::mapEstadistica)
                        .collect(Collectors.toList()))
                .build();
    }

    private EstadisticaResponse mapEstadistica(Estadistica estadistica) {
        return EstadisticaResponse.builder()
                .id(estadistica.getIdEstadistica())
                .nombre(estadistica.getNombre())
                .valor(estadistica.getValor())
                .periodoInicio(estadistica.getPeriodoInicio())
                .periodoFin(estadistica.getPeriodoFin())
                .build();
    }

    private String toJson(Map<String, Object> parametros) {
        if (parametros == null || parametros.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(parametros);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Los parámetros del reporte no tienen un formato JSON válido");
        }
    }

    private Map<String, Object> toMap(String parametrosJson) {
        if (parametrosJson == null || parametrosJson.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(parametrosJson, Map.class);
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }
}

