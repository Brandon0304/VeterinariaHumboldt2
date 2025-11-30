package com.tuorg.veterinaria.configuracion.controller;

import com.tuorg.veterinaria.configuracion.model.ConfiguracionAvanzada;
import com.tuorg.veterinaria.configuracion.service.ConfiguracionAvanzadaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de configuración avanzada del sistema.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/configuracion/avanzada")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConfiguracionAvanzadaController {

    private final ConfiguracionAvanzadaService configuracionService;

    /**
     * Obtiene el valor de una configuración por clave.
     */
    @GetMapping("/valor/{clave}")
    public ResponseEntity<String> obtenerValor(@PathVariable String clave) {
        log.debug("GET /api/v1/configuracion/avanzada/valor/{}", clave);
        String valor = configuracionService.obtenerValor(clave);
        if (valor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(valor);
    }

    /**
     * Obtiene una configuración completa por clave.
     */
    @GetMapping("/{clave}")
    public ResponseEntity<ConfiguracionAvanzada> obtenerConfiguracion(@PathVariable String clave) {
        log.info("GET /api/v1/configuracion/avanzada/{}", clave);
        try {
            ConfiguracionAvanzada config = configuracionService.obtenerConfiguracion(clave);
            return ResponseEntity.ok(config);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene todas las configuraciones activas.
     */
    @GetMapping("/activas")
    public ResponseEntity<List<ConfiguracionAvanzada>> obtenerActivas() {
        log.info("GET /api/v1/configuracion/avanzada/activas");
        List<ConfiguracionAvanzada> configuraciones = configuracionService.obtenerActivas();
        return ResponseEntity.ok(configuraciones);
    }

    /**
     * Obtiene configuraciones editables.
     */
    @GetMapping("/editables")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConfiguracionAvanzada>> obtenerEditables() {
        log.info("GET /api/v1/configuracion/avanzada/editables");
        List<ConfiguracionAvanzada> configuraciones = configuracionService.obtenerEditables();
        return ResponseEntity.ok(configuraciones);
    }

    /**
     * Busca configuraciones con filtros y paginación.
     */
    @GetMapping
    public ResponseEntity<Page<ConfiguracionAvanzada>> buscarConfiguraciones(
            @RequestParam(required = false) String clave,
            @RequestParam(required = false) ConfiguracionAvanzada.TipoDato tipoDato,
            @RequestParam(required = false) Boolean editable,
            Pageable pageable) {
        
        log.info("GET /api/v1/configuracion/avanzada - Buscando configuraciones");
        Page<ConfiguracionAvanzada> configuraciones = configuracionService.buscarConfiguraciones(
                clave, tipoDato, editable, pageable
        );
        return ResponseEntity.ok(configuraciones);
    }

    /**
     * Actualiza el valor de una configuración.
     */
    @PatchMapping("/{clave}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfiguracionAvanzada> actualizarValor(
            @PathVariable String clave,
            @RequestParam String nuevoValor) {
        log.info("PATCH /api/v1/configuracion/avanzada/{} - Nuevo valor: {}", clave, nuevoValor);
        try {
            ConfiguracionAvanzada actualizada = configuracionService.actualizarValor(clave, nuevoValor);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Crea una nueva configuración.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfiguracionAvanzada> crearConfiguracion(
            @RequestBody ConfiguracionAvanzada config) {
        log.info("POST /api/v1/configuracion/avanzada - Creando configuración");
        try {
            ConfiguracionAvanzada creada = configuracionService.crearConfiguracion(config);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Desactiva una configuración (soft delete).
     */
    @DeleteMapping("/{clave}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarConfiguracion(@PathVariable String clave) {
        log.info("DELETE /api/v1/configuracion/avanzada/{} - Desactivando", clave);
        configuracionService.desactivarConfiguracion(clave);
        return ResponseEntity.noContent().build();
    }

    /**
     * Restaura valores por defecto.
     */
    @PostMapping("/restaurar-defaults")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> restaurarDefaults() {
        log.warn("POST /api/v1/configuracion/avanzada/restaurar-defaults");
        configuracionService.restaurarDefaults();
        return ResponseEntity.ok().build();
    }
}
