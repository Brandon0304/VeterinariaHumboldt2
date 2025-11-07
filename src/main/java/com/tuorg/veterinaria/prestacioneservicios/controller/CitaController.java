package com.tuorg.veterinaria.prestacioneservicios.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.prestacioneservicios.model.Cita;
import com.tuorg.veterinaria.prestacioneservicios.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/api/citas")
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
    public ResponseEntity<ApiResponse<Cita>> programar(@RequestBody Cita cita) {
        Cita citaCreada = citaService.programar(cita);
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
    public ResponseEntity<ApiResponse<Cita>> reprogramar(
            @PathVariable Long citaId,
            @RequestBody Map<String, String> requestBody) {
        LocalDateTime nuevaFechaHora = LocalDateTime.parse(requestBody.get("fechaHora"));
        Cita cita = citaService.reprogramar(citaId, nuevaFechaHora);
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
    public ResponseEntity<ApiResponse<Cita>> cancelar(
            @PathVariable Long citaId,
            @RequestBody Map<String, String> requestBody) {
        String motivo = requestBody.get("motivo");
        Cita cita = citaService.cancelar(citaId, motivo);
        return ResponseEntity.ok(ApiResponse.success("Cita cancelada exitosamente", cita));
    }

    /**
     * Marca una cita como completada.
     * 
     * @param citaId ID de la cita
     * @return Respuesta con la cita completada
     */
    @PutMapping("/{citaId}/completar")
    public ResponseEntity<ApiResponse<Cita>> completar(@PathVariable Long citaId) {
        Cita cita = citaService.completar(citaId);
        return ResponseEntity.ok(ApiResponse.success("Cita completada exitosamente", cita));
    }

    /**
     * Obtiene todas las citas de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Respuesta con la lista de citas
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<ApiResponse<List<Cita>>> obtenerPorPaciente(@PathVariable Long pacienteId) {
        List<Cita> citas = citaService.obtenerPorPaciente(pacienteId);
        return ResponseEntity.ok(ApiResponse.success("Citas obtenidas exitosamente", citas));
    }

    /**
     * Obtiene todas las citas de un veterinario.
     * 
     * @param veterinarioId ID del veterinario
     * @return Respuesta con la lista de citas
     */
    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<ApiResponse<List<Cita>>> obtenerPorVeterinario(@PathVariable Long veterinarioId) {
        List<Cita> citas = citaService.obtenerPorVeterinario(veterinarioId);
        return ResponseEntity.ok(ApiResponse.success("Citas obtenidas exitosamente", citas));
    }
}

