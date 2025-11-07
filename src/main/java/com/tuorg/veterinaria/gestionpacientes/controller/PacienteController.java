package com.tuorg.veterinaria.gestionpacientes.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionpacientes.model.Paciente;
import com.tuorg.veterinaria.gestionpacientes.service.PacienteService;
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
@RequestMapping("/api/pacientes")
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
    public ResponseEntity<ApiResponse<Paciente>> registrar(@RequestBody Paciente paciente) {
        Paciente pacienteCreado = pacienteService.registrarPaciente(paciente);
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
    public ResponseEntity<ApiResponse<Paciente>> obtener(@PathVariable Long id) {
        Paciente paciente = pacienteService.obtener(id);
        return ResponseEntity.ok(ApiResponse.success("Paciente obtenido exitosamente", paciente));
    }

    /**
     * Obtiene todos los pacientes.
     * 
     * @return Respuesta con la lista de pacientes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Paciente>>> obtenerTodos() {
        List<Paciente> pacientes = pacienteService.obtenerTodos();
        return ResponseEntity.ok(ApiResponse.success("Pacientes obtenidos exitosamente", pacientes));
    }

    /**
     * Obtiene pacientes por cliente (dueño).
     * 
     * @param clienteId ID del cliente
     * @return Respuesta con la lista de pacientes del cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<Paciente>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<Paciente> pacientes = pacienteService.obtenerPorCliente(clienteId);
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
    public ResponseEntity<ApiResponse<Paciente>> actualizar(
            @PathVariable Long id,
            @RequestBody Paciente paciente) {
        Paciente pacienteActualizado = pacienteService.actualizarDatos(id, paciente);
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
}

