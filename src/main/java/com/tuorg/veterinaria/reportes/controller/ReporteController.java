package com.tuorg.veterinaria.reportes.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.reportes.model.Reporte;
import com.tuorg.veterinaria.reportes.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para la gestión de reportes (Facade pattern).
 * 
 * Este controlador expone endpoints para generar y exportar reportes,
 * simplificando operaciones complejas mediante el patrón Facade.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    /**
     * Servicio de gestión de reportes (Facade).
     */
    private final ReporteService reporteService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param reporteService Servicio de reportes
     */
    @Autowired
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * Genera un nuevo reporte (Facade pattern).
     * 
     * @param requestBody Cuerpo con los datos del reporte
     * @return Respuesta con el reporte generado
     */
    @PostMapping("/generar")
    public ResponseEntity<ApiResponse<Reporte>> generar(@RequestBody Map<String, Object> requestBody) {
        String nombre = (String) requestBody.get("nombre");
        String tipo = (String) requestBody.get("tipo");
        @SuppressWarnings("unchecked")
        Map<String, Object> parametros = (Map<String, Object>) requestBody.get("parametros");
        
        Reporte reporte = reporteService.generar(nombre, tipo, parametros);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reporte generado exitosamente", reporte));
    }

    /**
     * Exporta un reporte como PDF.
     * 
     * @param id ID del reporte
     * @return Respuesta con el PDF en bytes
     */
    @GetMapping("/{id}/exportar-pdf")
    public ResponseEntity<byte[]> exportarPDF(@PathVariable Long id) {
        byte[] pdf = reporteService.exportarPDF(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=reporte_" + id + ".pdf")
                .body(pdf);
    }

    /**
     * Exporta un reporte como Excel.
     * 
     * @param id ID del reporte
     * @return Respuesta con el archivo Excel en bytes
     */
    @GetMapping("/{id}/exportar-excel")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable Long id) {
        byte[] excel = reporteService.exportarExcel(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=reporte_" + id + ".xlsx")
                .body(excel);
    }
}

