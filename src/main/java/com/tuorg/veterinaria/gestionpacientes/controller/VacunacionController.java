package com.tuorg.veterinaria.gestionpacientes.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionpacientes.dto.ProgramarProximaDosisRequest;
import com.tuorg.veterinaria.gestionpacientes.dto.VacunacionRequest;
import com.tuorg.veterinaria.gestionpacientes.dto.VacunacionResponse;
import com.tuorg.veterinaria.gestionpacientes.service.VacunacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de vacunaciones.
 * 
 * Este controlador expone endpoints para registrar vacunaciones
 * y programar próximas dosis.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/vacunaciones")
public class VacunacionController {

    /**
     * Servicio de gestión de vacunaciones.
     */
    private final VacunacionService vacunacionService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param vacunacionService Servicio de vacunaciones
     */
    @Autowired
    public VacunacionController(VacunacionService vacunacionService) {
        this.vacunacionService = vacunacionService;
    }

    /**
     * Registra una nueva vacunación.
     * 
     * @param vacunacion Vacunación a registrar
     * @return Respuesta con la vacunación creada
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VacunacionResponse>> registrar(@Valid @RequestBody VacunacionRequest request) {
        VacunacionResponse vacunacionCreada = vacunacionService.registrarVacuna(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vacunación registrada exitosamente", vacunacionCreada));
    }

    /**
     * Programa la próxima dosis de una vacunación.
     * 
     * @param vacunacionId ID de la vacunación
     * @param requestBody Cuerpo con la fecha de próxima dosis
     * @return Respuesta con la vacunación actualizada
     */
    @PutMapping("/{vacunacionId}/proxima-dosis")
    public ResponseEntity<ApiResponse<VacunacionResponse>> programarProximaDosis(
            @PathVariable Long vacunacionId,
            @Valid @RequestBody ProgramarProximaDosisRequest request) {
        VacunacionResponse vacunacion = vacunacionService.programarProximaDosis(vacunacionId, request);
        return ResponseEntity.ok(ApiResponse.success("Próxima dosis programada exitosamente", vacunacion));
    }

    /**
     * Obtiene todas las vacunaciones de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Respuesta con la lista de vacunaciones
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<ApiResponse<List<VacunacionResponse>>> obtenerPorPaciente(@PathVariable Long pacienteId) {
        List<VacunacionResponse> vacunaciones = vacunacionService.obtenerPorPaciente(pacienteId);
        return ResponseEntity.ok(ApiResponse.success("Vacunaciones obtenidas exitosamente", vacunaciones));
    }

    /**
     * Obtiene vacunaciones pendientes.
     * 
     * @param dias Días de anticipación (por defecto 30)
     * @return Respuesta con la lista de vacunaciones pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<List<VacunacionResponse>>> obtenerPendientes(
            @RequestParam(defaultValue = "30") int dias) {
        List<VacunacionResponse> vacunaciones = vacunacionService.obtenerVacunacionesPendientes(dias);
        return ResponseEntity.ok(ApiResponse.success("Vacunaciones pendientes obtenidas exitosamente", vacunaciones));
    }
}

