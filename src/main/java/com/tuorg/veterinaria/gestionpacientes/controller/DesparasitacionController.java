package com.tuorg.veterinaria.gestionpacientes.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionpacientes.dto.DesparasitacionRequest;
import com.tuorg.veterinaria.gestionpacientes.dto.DesparasitacionResponse;
import com.tuorg.veterinaria.gestionpacientes.service.DesparasitacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de desparasitaciones.
 * 
 * Este controlador expone endpoints para registrar y consultar desparasitaciones.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/desparasitaciones")
public class DesparasitacionController {

    /**
     * Servicio de gestión de desparasitaciones.
     */
    private final DesparasitacionService desparasitacionService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param desparasitacionService Servicio de desparasitaciones
     */
    @Autowired
    public DesparasitacionController(DesparasitacionService desparasitacionService) {
        this.desparasitacionService = desparasitacionService;
    }

    /**
     * Registra una nueva desparasitación.
     * 
     * @param request Datos de la desparasitación
     * @return Respuesta con la desparasitación creada
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DesparasitacionResponse>> registrar(@Valid @RequestBody DesparasitacionRequest request) {
        DesparasitacionResponse desparasitacionCreada = desparasitacionService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Desparasitación registrada exitosamente", desparasitacionCreada));
    }

    /**
     * Obtiene todas las desparasitaciones de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Respuesta con la lista de desparasitaciones
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<ApiResponse<List<DesparasitacionResponse>>> obtenerPorPaciente(@PathVariable Long pacienteId) {
        List<DesparasitacionResponse> desparasitaciones = desparasitacionService.obtenerPorPaciente(pacienteId);
        return ResponseEntity.ok(ApiResponse.success("Desparasitaciones obtenidas exitosamente", desparasitaciones));
    }

    /**
     * Obtiene desparasitaciones pendientes.
     * 
     * @param dias Días de anticipación (por defecto 30)
     * @return Respuesta con la lista de desparasitaciones pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<List<DesparasitacionResponse>>> obtenerPendientes(
            @RequestParam(defaultValue = "30") int dias) {
        List<DesparasitacionResponse> desparasitaciones = desparasitacionService.obtenerDesparasitacionesPendientes(dias);
        return ResponseEntity.ok(ApiResponse.success("Desparasitaciones pendientes obtenidas exitosamente", desparasitaciones));
    }

    /**
     * Obtiene todas las desparasitaciones.
     * 
     * @return Respuesta con la lista de desparasitaciones
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DesparasitacionResponse>>> obtenerTodas() {
        List<DesparasitacionResponse> desparasitaciones = desparasitacionService.obtenerTodas();
        return ResponseEntity.ok(ApiResponse.success("Desparasitaciones obtenidas exitosamente", desparasitaciones));
    }
}

