package com.tuorg.veterinaria.gestionpacientes.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionpacientes.model.Vacunacion;
import com.tuorg.veterinaria.gestionpacientes.service.VacunacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/api/vacunaciones")
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
    public ResponseEntity<ApiResponse<Vacunacion>> registrar(@RequestBody Vacunacion vacunacion) {
        Vacunacion vacunacionCreada = vacunacionService.registrarVacuna(vacunacion);
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
    public ResponseEntity<ApiResponse<Vacunacion>> programarProximaDosis(
            @PathVariable Long vacunacionId,
            @RequestBody Map<String, String> requestBody) {
        LocalDate proximaDosis = LocalDate.parse(requestBody.get("proximaDosis"));
        Vacunacion vacunacion = vacunacionService.programarProximaDosis(vacunacionId, proximaDosis);
        return ResponseEntity.ok(ApiResponse.success("Próxima dosis programada exitosamente", vacunacion));
    }

    /**
     * Obtiene todas las vacunaciones de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Respuesta con la lista de vacunaciones
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<ApiResponse<List<Vacunacion>>> obtenerPorPaciente(@PathVariable Long pacienteId) {
        List<Vacunacion> vacunaciones = vacunacionService.obtenerPorPaciente(pacienteId);
        return ResponseEntity.ok(ApiResponse.success("Vacunaciones obtenidas exitosamente", vacunaciones));
    }

    /**
     * Obtiene vacunaciones pendientes.
     * 
     * @param dias Días de anticipación (por defecto 30)
     * @return Respuesta con la lista de vacunaciones pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<List<Vacunacion>>> obtenerPendientes(
            @RequestParam(defaultValue = "30") int dias) {
        List<Vacunacion> vacunaciones = vacunacionService.obtenerVacunacionesPendientes(dias);
        return ResponseEntity.ok(ApiResponse.success("Vacunaciones pendientes obtenidas exitosamente", vacunaciones));
    }
}
