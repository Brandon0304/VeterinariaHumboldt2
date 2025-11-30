package com.tuorg.veterinaria.configuracion.controller;

import com.tuorg.veterinaria.configuracion.model.AuditoriaDetallada;
import com.tuorg.veterinaria.configuracion.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para consultas de auditoría detallada.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/configuracion/auditoria")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    /**
     * Busca auditorías con filtros y paginación.
     */
    @GetMapping
    public ResponseEntity<Page<AuditoriaDetallada>> buscarAuditorias(
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) Long entidadId,
            @RequestParam(required = false) AuditoriaDetallada.TipoOperacion operacion,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            Pageable pageable) {
        
        log.info("GET /api/v1/configuracion/auditoria - Buscando auditorías");
        Page<AuditoriaDetallada> auditorias = auditoriaService.buscarAuditorias(
                entidad, entidadId, operacion, usuarioId, fechaDesde, fechaHasta, pageable
        );
        return ResponseEntity.ok(auditorias);
    }

    /**
     * Obtiene el historial completo de una entidad específica.
     */
    @GetMapping("/historial")
    public ResponseEntity<List<AuditoriaDetallada>> obtenerHistorial(
            @RequestParam String entidad,
            @RequestParam Long entidadId) {
        log.info("GET /api/v1/configuracion/auditoria/historial?entidad={}&entidadId={}", 
                entidad, entidadId);
        List<AuditoriaDetallada> historial = auditoriaService.obtenerHistorial(entidad, entidadId);
        return ResponseEntity.ok(historial);
    }

    /**
     * Obtiene auditorías de un usuario específico.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<AuditoriaDetallada>> obtenerPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable) {
        log.info("GET /api/v1/configuracion/auditoria/usuario/{}", usuarioId);
        Page<AuditoriaDetallada> auditorias = auditoriaService.obtenerPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(auditorias);
    }

    /**
     * Obtiene auditorías recientes (últimas N operaciones).
     */
    @GetMapping("/recientes")
    public ResponseEntity<List<AuditoriaDetallada>> obtenerRecientes(
            @RequestParam(defaultValue = "50") int limite) {
        log.info("GET /api/v1/configuracion/auditoria/recientes?limite={}", limite);
        List<AuditoriaDetallada> recientes = auditoriaService.obtenerRecientes(limite);
        return ResponseEntity.ok(recientes);
    }

    /**
     * Cuenta operaciones en un rango de fechas.
     */
    @GetMapping("/estadisticas/count")
    public ResponseEntity<Long> contarOperaciones(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {
        log.info("GET /api/v1/configuracion/auditoria/estadisticas/count");
        long count = auditoriaService.contarOperaciones(fechaDesde, fechaHasta);
        return ResponseEntity.ok(count);
    }
}
