package com.tuorg.veterinaria.gestionpacientes.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionpacientes.dto.PacienteEstadisticasResponse;
import com.tuorg.veterinaria.gestionpacientes.dto.PacienteRequest;
import com.tuorg.veterinaria.gestionpacientes.dto.PacienteResponse;
import com.tuorg.veterinaria.gestionpacientes.dto.PacienteUpdateRequest;
import com.tuorg.veterinaria.gestionpacientes.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pacientes.
 * 
 * Este controlador expone endpoints para crear, consultar, actualizar
 * y gestionar pacientes (mascotas).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    /**
     * Servicio de gestión de pacientes.
     */
    private final PacienteService pacienteService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param pacienteService Servicio de pacientes
     */
    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /**
     * Registra un nuevo paciente.
     * 
     * @param paciente Paciente a registrar
     * @return Respuesta con el paciente creado
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PacienteResponse>> registrar(@Valid @RequestBody PacienteRequest request) {
        PacienteResponse pacienteCreado = pacienteService.registrarPaciente(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Paciente registrado exitosamente", pacienteCreado));
    }

    /**
     * Obtiene un paciente por su ID.
     * 
     * @param id ID del paciente
     * @return Respuesta con el paciente
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PacienteResponse>> obtener(@PathVariable Long id) {
        PacienteResponse paciente = pacienteService.obtener(id);
        return ResponseEntity.ok(ApiResponse.success("Paciente obtenido exitosamente", paciente));
    }

    /**
     * Obtiene todos los pacientes.
     * 
     * @return Respuesta con la lista de pacientes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PacienteResponse>>> obtenerTodos() {
        List<PacienteResponse> pacientes = pacienteService.obtenerTodos();
        return ResponseEntity.ok(ApiResponse.success("Pacientes obtenidos exitosamente", pacientes));
    }

    /**
     * Obtiene pacientes por cliente (dueño).
     * 
     * @param clienteId ID del cliente
     * @return Respuesta con la lista de pacientes del cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<PacienteResponse>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<PacienteResponse> pacientes = pacienteService.obtenerPorCliente(clienteId);
        return ResponseEntity.ok(ApiResponse.success("Pacientes obtenidos exitosamente", pacientes));
    }

    /**
     * Actualiza los datos de un paciente.
     * 
     * @param id ID del paciente
     * @param paciente Datos actualizados del paciente
     * @return Respuesta con el paciente actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PacienteResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteUpdateRequest request) {
        PacienteResponse pacienteActualizado = pacienteService.actualizarDatos(id, request);
        return ResponseEntity.ok(ApiResponse.success("Paciente actualizado exitosamente", pacienteActualizado));
    }

    /**
     * Genera un resumen clínico del paciente.
     * 
     * @param id ID del paciente
     * @return Respuesta con el resumen clínico
     */
    @GetMapping("/{id}/resumen")
    public ResponseEntity<ApiResponse<String>> generarResumen(@PathVariable Long id) {
        String resumen = pacienteService.generarResumenClinico(id);
        return ResponseEntity.ok(ApiResponse.success("Resumen clínico generado exitosamente", resumen));
    }

    /**
     * Obtiene estadísticas generales de pacientes.
     * 
     * @return Respuesta con las estadísticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponse<PacienteEstadisticasResponse>> obtenerEstadisticas() {
        PacienteEstadisticasResponse estadisticas = pacienteService.obtenerEstadisticas();
        return ResponseEntity.ok(ApiResponse.success("Estadísticas obtenidas exitosamente", estadisticas));
    }
}

