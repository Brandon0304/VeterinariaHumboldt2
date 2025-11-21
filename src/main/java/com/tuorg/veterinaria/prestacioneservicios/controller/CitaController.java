package com.tuorg.veterinaria.prestacioneservicios.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.prestacioneservicios.dto.CitaCancelarRequest;
import com.tuorg.veterinaria.prestacioneservicios.dto.CitaRequest;
import com.tuorg.veterinaria.prestacioneservicios.dto.CitaResponse;
import com.tuorg.veterinaria.prestacioneservicios.dto.CitaReprogramarRequest;
import com.tuorg.veterinaria.prestacioneservicios.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de citas.
 * 
 * Este controlador expone endpoints para programar, reprogramar,
 * cancelar y consultar citas médicas.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/citas")
public class CitaController {

    /**
     * Servicio de gestión de citas.
     */
    private final CitaService citaService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param citaService Servicio de citas
     */
    @Autowired
    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    /**
     * Programa una nueva cita.
     * 
     * @param cita Cita a programar
     * @return Respuesta con la cita creada
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CitaResponse>> programar(@RequestBody @Valid CitaRequest cita) {
        CitaResponse citaCreada = citaService.programar(cita);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cita programada exitosamente", citaCreada));
    }

    /**
     * Reprograma una cita existente.
     * 
     * @param citaId ID de la cita
     * @param requestBody Cuerpo con la nueva fecha y hora
     * @return Respuesta con la cita actualizada
     */
    @PutMapping("/{citaId}/reprogramar")
    public ResponseEntity<ApiResponse<CitaResponse>> reprogramar(
            @PathVariable Long citaId,
            @RequestBody @Valid CitaReprogramarRequest request) {
        CitaResponse cita = citaService.reprogramar(citaId, request);
        return ResponseEntity.ok(ApiResponse.success("Cita reprogramada exitosamente", cita));
    }

    /**
     * Cancela una cita.
     * 
     * @param citaId ID de la cita
     * @param requestBody Cuerpo con el motivo de cancelación
     * @return Respuesta con la cita cancelada
     */
    @PutMapping("/{citaId}/cancelar")
    public ResponseEntity<ApiResponse<CitaResponse>> cancelar(
            @PathVariable Long citaId,
            @RequestBody @Valid CitaCancelarRequest request) {
        CitaResponse cita = citaService.cancelar(citaId, request);
        return ResponseEntity.ok(ApiResponse.success("Cita cancelada exitosamente", cita));
    }

    /**
     * Marca una cita como completada.
     * 
     * @param citaId ID de la cita
     * @return Respuesta con la cita completada
     */
    @PutMapping("/{citaId}/completar")
    public ResponseEntity<ApiResponse<CitaResponse>> completar(@PathVariable Long citaId) {
        CitaResponse cita = citaService.completar(citaId);
        return ResponseEntity.ok(ApiResponse.success("Cita completada exitosamente", cita));
    }

    /**
     * Obtiene todas las citas.
     * 
     * @return Respuesta con la lista de todas las citas
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CitaResponse>>> obtenerTodas() {
        List<CitaResponse> citas = citaService.obtenerTodas();
        return ResponseEntity.ok(ApiResponse.success("Citas obtenidas exitosamente", citas));
    }

    /**
     * Obtiene todas las citas de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Respuesta con la lista de citas
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<ApiResponse<List<CitaResponse>>> obtenerPorPaciente(@PathVariable Long pacienteId) {
        List<CitaResponse> citas = citaService.obtenerPorPaciente(pacienteId);
        return ResponseEntity.ok(ApiResponse.success("Citas obtenidas exitosamente", citas));
    }

    /**
     * Obtiene todas las citas de un veterinario.
     * 
     * @param veterinarioId ID del veterinario
     * @return Respuesta con la lista de citas
     */
    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<ApiResponse<List<CitaResponse>>> obtenerPorVeterinario(@PathVariable Long veterinarioId) {
        List<CitaResponse> citas = citaService.obtenerPorVeterinario(veterinarioId);
        return ResponseEntity.ok(ApiResponse.success("Citas obtenidas exitosamente", citas));
    }

    /**
     * Verifica si una fecha y hora está disponible para un veterinario.
     * 
     * @param veterinarioId ID del veterinario
     * @param fechaHora Fecha y hora a verificar (formato: yyyy-MM-ddTHH:mm)
     * @return Respuesta indicando si está disponible
     */
    @GetMapping("/disponibilidad")
    public ResponseEntity<ApiResponse<Boolean>> verificarDisponibilidad(
            @RequestParam Long veterinarioId,
            @RequestParam String fechaHora) {
        java.time.LocalDateTime fechaHoraParsed = java.time.LocalDateTime.parse(fechaHora);
        boolean disponible = citaService.verificarDisponibilidad(veterinarioId, fechaHoraParsed);
        return ResponseEntity.ok(ApiResponse.success(
                disponible ? "La fecha y hora están disponibles" : "La fecha y hora no están disponibles",
                disponible));
    }
}

