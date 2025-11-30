package com.tuorg.veterinaria.configuracion.controller;

import com.tuorg.veterinaria.configuracion.model.HorarioAtencion;
import com.tuorg.veterinaria.configuracion.service.HorarioAtencionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

/**
 * Controlador REST para gestión de horarios de atención.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/configuracion/horarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HorarioAtencionController {

    private final HorarioAtencionService horarioService;

    /**
     * Obtiene todos los horarios activos.
     */
    @GetMapping
    public ResponseEntity<List<HorarioAtencion>> obtenerHorariosActivos() {
        log.info("GET /api/v1/configuracion/horarios");
        List<HorarioAtencion> horarios = horarioService.obtenerHorariosActivos();
        return ResponseEntity.ok(horarios);
    }

    /**
     * Obtiene el horario de un día específico.
     */
    @GetMapping("/dia/{dia}")
    public ResponseEntity<HorarioAtencion> obtenerPorDia(@PathVariable HorarioAtencion.DiaSemana dia) {
        log.info("GET /api/v1/configuracion/horarios/dia/{}", dia);
        HorarioAtencion horario = horarioService.obtenerPorDia(dia);
        if (horario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(horario);
    }

    /**
     * Obtiene los días en que la clínica está abierta.
     */
    @GetMapping("/dias-abiertos")
    public ResponseEntity<List<HorarioAtencion>> obtenerDiasAbiertos() {
        log.info("GET /api/v1/configuracion/horarios/dias-abiertos");
        List<HorarioAtencion> horarios = horarioService.obtenerDiasAbiertos();
        return ResponseEntity.ok(horarios);
    }

    /**
     * Verifica si la clínica está abierta en un día y hora específicos.
     */
    @GetMapping("/abierta")
    public ResponseEntity<Boolean> verificarAbierta(
            @RequestParam HorarioAtencion.DiaSemana dia,
            @RequestParam LocalTime hora) {
        log.debug("GET /api/v1/configuracion/horarios/abierta?dia={}&hora={}", dia, hora);
        boolean abierta = horarioService.estaAbierta(dia, hora);
        return ResponseEntity.ok(abierta);
    }

    /**
     * Verifica si la clínica está abierta ahora.
     */
    @GetMapping("/abierta-ahora")
    public ResponseEntity<Boolean> verificarAbiertaAhora() {
        boolean abierta = horarioService.estaAbiertaAhora();
        return ResponseEntity.ok(abierta);
    }

    /**
     * Crea o actualiza un horario.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HorarioAtencion> guardarHorario(@RequestBody HorarioAtencion horario) {
        log.info("POST /api/v1/configuracion/horarios - Guardando horario");
        HorarioAtencion guardado = horarioService.guardarHorario(horario);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    /**
     * Actualiza un horario existente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HorarioAtencion> actualizarHorario(
            @PathVariable Long id,
            @RequestBody HorarioAtencion horario) {
        log.info("PUT /api/v1/configuracion/horarios/{} - Actualizando", id);
        horario.setIdHorario(id);
        HorarioAtencion actualizado = horarioService.guardarHorario(horario);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Actualiza solo el estado (abierto/cerrado) de un día.
     */
    @PatchMapping("/dia/{dia}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HorarioAtencion> actualizarEstadoDia(
            @PathVariable HorarioAtencion.DiaSemana dia,
            @RequestParam boolean abierto) {
        log.info("PATCH /api/v1/configuracion/horarios/dia/{}/estado - {}", dia, abierto ? "ABIERTO" : "CERRADO");
        HorarioAtencion actualizado = horarioService.actualizarEstadoDia(dia, abierto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Desactiva un horario (soft delete).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarHorario(@PathVariable Long id) {
        log.info("DELETE /api/v1/configuracion/horarios/{} - Desactivando", id);
        horarioService.desactivarHorario(id);
        return ResponseEntity.noContent().build();
    }
}
