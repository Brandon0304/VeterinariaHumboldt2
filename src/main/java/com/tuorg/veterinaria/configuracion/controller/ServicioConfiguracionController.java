package com.tuorg.veterinaria.configuracion.controller;

import com.tuorg.veterinaria.configuracion.model.ServicioConfiguracion;
import com.tuorg.veterinaria.configuracion.service.ServicioConfiguracionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestión de catálogo de servicios.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/configuracion/servicios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServicioConfiguracionController {

    private final ServicioConfiguracionService servicioService;

    /**
     * Obtiene todos los servicios activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<List<ServicioConfiguracion>> obtenerServiciosActivos() {
        log.info("GET /api/v1/configuracion/servicios/activos");
        List<ServicioConfiguracion> servicios = servicioService.obtenerServiciosActivos();
        return ResponseEntity.ok(servicios);
    }

    /**
     * Busca servicios por nombre.
     */
    @GetMapping("/buscar")
    public ResponseEntity<Page<ServicioConfiguracion>> buscarPorNombre(
            @RequestParam String nombre,
            Pageable pageable) {
        log.info("GET /api/v1/configuracion/servicios/buscar?nombre={}", nombre);
        Page<ServicioConfiguracion> servicios = servicioService.buscarPorNombre(nombre, pageable);
        return ResponseEntity.ok(servicios);
    }

    /**
     * Obtiene servicios por categoría.
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ServicioConfiguracion>> obtenerPorCategoria(@PathVariable String categoria) {
        log.info("GET /api/v1/configuracion/servicios/categoria/{}", categoria);
        List<ServicioConfiguracion> servicios = servicioService.obtenerPorCategoria(categoria);
        return ResponseEntity.ok(servicios);
    }

    /**
     * Obtiene servicios por rango de precio.
     */
    @GetMapping("/rango-precio")
    public ResponseEntity<List<ServicioConfiguracion>> obtenerPorRangoPrecio(
            @RequestParam BigDecimal precioMin,
            @RequestParam BigDecimal precioMax) {
        log.info("GET /api/v1/configuracion/servicios/rango-precio?min={}&max={}", precioMin, precioMax);
        List<ServicioConfiguracion> servicios = servicioService.obtenerPorRangoPrecio(precioMin, precioMax);
        return ResponseEntity.ok(servicios);
    }

    /**
     * Obtiene todas las categorías.
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        List<String> categorias = servicioService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Obtiene un servicio por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServicioConfiguracion> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/v1/configuracion/servicios/{}", id);
        ServicioConfiguracion servicio = servicioService.obtenerPorId(id);
        return ResponseEntity.ok(servicio);
    }

    /**
     * Crea un nuevo servicio.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<ServicioConfiguracion> crearServicio(@RequestBody ServicioConfiguracion servicio) {
        log.info("POST /api/v1/configuracion/servicios - Creando servicio");
        ServicioConfiguracion creado = servicioService.guardarServicio(servicio);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Actualiza un servicio existente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<ServicioConfiguracion> actualizarServicio(
            @PathVariable Long id,
            @RequestBody ServicioConfiguracion servicio) {
        log.info("PUT /api/v1/configuracion/servicios/{} - Actualizando", id);
        servicio.setIdServicioConfig(id);
        ServicioConfiguracion actualizado = servicioService.guardarServicio(servicio);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Actualiza solo el precio de un servicio.
     */
    @PatchMapping("/{id}/precio")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicioConfiguracion> actualizarPrecio(
            @PathVariable Long id,
            @RequestParam BigDecimal nuevoPrecio) {
        log.info("PATCH /api/v1/configuracion/servicios/{}/precio - Nuevo precio: {}", id, nuevoPrecio);
        ServicioConfiguracion actualizado = servicioService.actualizarPrecio(id, nuevoPrecio);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Desactiva un servicio (soft delete).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarServicio(@PathVariable Long id) {
        log.info("DELETE /api/v1/configuracion/servicios/{} - Desactivando", id);
        servicioService.desactivarServicio(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cuenta servicios activos.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> contarServiciosActivos() {
        long count = servicioService.contarServiciosActivos();
        return ResponseEntity.ok(count);
    }
}
