package com.tuorg.veterinaria.configuracion.controller;

import com.tuorg.veterinaria.configuracion.model.RespaldoSistema;
import com.tuorg.veterinaria.configuracion.service.RespaldoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestión de respaldos del sistema.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/configuracion/respaldos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class RespaldoController {

    private final RespaldoService respaldoService;

    /**
     * Obtiene el último respaldo exitoso.
     */
    @GetMapping("/ultimo-exitoso")
    public ResponseEntity<RespaldoSistema> obtenerUltimoRespaldoExitoso() {
        log.info("GET /api/v1/configuracion/respaldos/ultimo-exitoso");
        RespaldoSistema respaldo = respaldoService.obtenerUltimoRespaldoExitoso();
        if (respaldo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(respaldo);
    }

    /**
     * Busca respaldos con filtros y paginación.
     */
    @GetMapping
    public ResponseEntity<Page<RespaldoSistema>> buscarRespaldos(
            @RequestParam(required = false) RespaldoSistema.TipoRespaldo tipo,
            @RequestParam(required = false) RespaldoSistema.EstadoRespaldo estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            Pageable pageable) {
        
        log.info("GET /api/v1/configuracion/respaldos - Buscando respaldos");
        Page<RespaldoSistema> respaldos = respaldoService.buscarRespaldos(
                tipo, estado, fechaDesde, fechaHasta, pageable
        );
        return ResponseEntity.ok(respaldos);
    }

    /**
     * Obtiene respaldos por tipo.
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<RespaldoSistema>> obtenerPorTipo(
            @PathVariable RespaldoSistema.TipoRespaldo tipo) {
        log.info("GET /api/v1/configuracion/respaldos/tipo/{}", tipo);
        List<RespaldoSistema> respaldos = respaldoService.obtenerPorTipo(tipo);
        return ResponseEntity.ok(respaldos);
    }

    /**
     * Cuenta respaldos por estado.
     */
    @GetMapping("/estadisticas/count-por-estado")
    public ResponseEntity<Long> contarPorEstado(
            @RequestParam RespaldoSistema.EstadoRespaldo estado) {
        long count = respaldoService.contarPorEstado(estado);
        return ResponseEntity.ok(count);
    }

    /**
     * Crea un nuevo respaldo (inicia el proceso).
     */
    @PostMapping
    public ResponseEntity<RespaldoSistema> crearRespaldo(
            @RequestParam RespaldoSistema.TipoRespaldo tipo,
            @RequestParam String ruta,
            @RequestParam Long tamanioBytes,
            @RequestParam(required = false) String descripcion) {
        
        log.info("POST /api/v1/configuracion/respaldos - Creando respaldo tipo: {}", tipo);
        RespaldoSistema respaldo = respaldoService.crearRespaldo(tipo, ruta, tamanioBytes, descripcion);
        return ResponseEntity.status(HttpStatus.CREATED).body(respaldo);
    }

    /**
     * Marca un respaldo como completado.
     */
    @PatchMapping("/{id}/completar")
    public ResponseEntity<RespaldoSistema> completarRespaldo(
            @PathVariable Long id,
            @RequestBody byte[] contenido) {
        log.info("PATCH /api/v1/configuracion/respaldos/{}/completar", id);
        RespaldoSistema respaldo = respaldoService.completarRespaldo(id, contenido);
        return ResponseEntity.ok(respaldo);
    }

    /**
     * Marca un respaldo como fallido.
     */
    @PatchMapping("/{id}/fallar")
    public ResponseEntity<RespaldoSistema> marcarComoFallido(
            @PathVariable Long id,
            @RequestParam String mensajeError) {
        log.error("PATCH /api/v1/configuracion/respaldos/{}/fallar - {}", id, mensajeError);
        RespaldoSistema respaldo = respaldoService.marcarComoFallido(id, mensajeError);
        return ResponseEntity.ok(respaldo);
    }

    /**
     * Verifica la integridad de un respaldo.
     */
    @PostMapping("/{id}/verificar")
    public ResponseEntity<Boolean> verificarIntegridad(
            @PathVariable Long id,
            @RequestBody byte[] contenido) {
        log.info("POST /api/v1/configuracion/respaldos/{}/verificar", id);
        boolean integro = respaldoService.verificarIntegridad(id, contenido);
        return ResponseEntity.ok(integro);
    }

    /**
     * Elimina respaldos antiguos.
     */
    @DeleteMapping("/antiguos")
    public ResponseEntity<Void> eliminarRespaldosAntiguos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaLimite) {
        log.warn("DELETE /api/v1/configuracion/respaldos/antiguos?fechaLimite={}", fechaLimite);
        respaldoService.eliminarRespaldosAntiguos(fechaLimite);
        return ResponseEntity.noContent().build();
    }
}
