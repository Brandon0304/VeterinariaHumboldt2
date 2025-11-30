package com.tuorg.veterinaria.configuracion.controller;

import com.tuorg.veterinaria.configuracion.model.PermisoRol;
import com.tuorg.veterinaria.configuracion.service.PermisoService;
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
 * Controlador REST para gestión de permisos RBAC.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/configuracion/permisos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PermisoController {

    private final PermisoService permisoService;

    /**
     * Valida si un rol tiene permiso para una acción.
     */
    @GetMapping("/validar")
    public ResponseEntity<Boolean> validarPermiso(
            @RequestParam Long idRol,
            @RequestParam String modulo,
            @RequestParam String accion) {
        log.debug("GET /api/v1/configuracion/permisos/validar - rol={}, modulo={}, accion={}", 
                idRol, modulo, accion);
        boolean tienePermiso = permisoService.tienePermiso(idRol, modulo, accion);
        return ResponseEntity.ok(tienePermiso);
    }

    /**
     * Obtiene todos los permisos de un rol.
     */
    @GetMapping("/rol/{idRol}")
    public ResponseEntity<List<PermisoRol>> obtenerPermisosPorRol(@PathVariable Long idRol) {
        log.info("GET /api/v1/configuracion/permisos/rol/{} - Obteniendo permisos", idRol);
        List<PermisoRol> permisos = permisoService.obtenerPermisosPorRol(idRol);
        return ResponseEntity.ok(permisos);
    }

    /**
     * Obtiene permisos por módulo.
     */
    @GetMapping("/modulo/{modulo}")
    public ResponseEntity<List<PermisoRol>> obtenerPermisosPorModulo(@PathVariable String modulo) {
        log.info("GET /api/v1/configuracion/permisos/modulo/{}", modulo);
        List<PermisoRol> permisos = permisoService.obtenerPermisosPorModulo(modulo);
        return ResponseEntity.ok(permisos);
    }

    /**
     * Busca permisos con filtros y paginación.
     */
    @GetMapping
    public ResponseEntity<Page<PermisoRol>> buscarPermisos(
            @RequestParam(required = false) Long idRol,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String accion,
            Pageable pageable) {
        log.info("GET /api/v1/configuracion/permisos - Buscando con filtros");
        Page<PermisoRol> permisos = permisoService.buscarPermisos(idRol, modulo, accion, pageable);
        return ResponseEntity.ok(permisos);
    }

    /**
     * Crea un nuevo permiso.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermisoRol> crearPermiso(@RequestBody PermisoRol permiso) {
        log.info("POST /api/v1/configuracion/permisos - Creando permiso");
        PermisoRol creado = permisoService.guardarPermiso(permiso);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Actualiza un permiso existente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermisoRol> actualizarPermiso(
            @PathVariable Long id,
            @RequestBody PermisoRol permiso) {
        log.info("PUT /api/v1/configuracion/permisos/{} - Actualizando", id);
        permiso.setId(id);
        PermisoRol actualizado = permisoService.guardarPermiso(permiso);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Desactiva un permiso (soft delete).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarPermiso(@PathVariable Long id) {
        log.info("DELETE /api/v1/configuracion/permisos/{} - Desactivando", id);
        permisoService.desactivarPermiso(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todos los módulos disponibles.
     */
    @GetMapping("/modulos")
    public ResponseEntity<List<String>> obtenerModulos() {
        List<String> modulos = permisoService.obtenerModulos();
        return ResponseEntity.ok(modulos);
    }

    /**
     * Obtiene todas las acciones disponibles.
     */
    @GetMapping("/acciones")
    public ResponseEntity<List<String>> obtenerAcciones() {
        List<String> acciones = permisoService.obtenerAcciones();
        return ResponseEntity.ok(acciones);
    }
}
