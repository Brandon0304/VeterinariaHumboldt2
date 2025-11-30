package com.tuorg.veterinaria.prestacioneservicios.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.prestacioneservicios.dto.ConsultaRequest;
import com.tuorg.veterinaria.prestacioneservicios.dto.ConsultaResponse;
import com.tuorg.veterinaria.prestacioneservicios.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de consultas veterinarias.
 * 
 * Endpoints para registrar consultas, obtener historial y visualizar detalles.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/consultas")
@Tag(name = "Consultas", description = "Gestión de consultas veterinarias")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;
    private final com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository usuarioRepository;

    /**
     * Registra una nueva consulta para una cita.
     * 
     * Solo los veterinarios pueden registrar consultas.
     * 
     * @param request Datos de la consulta
     * @param authentication Usuario autenticado (veterinario)
     * @return Respuesta con la consulta registrada
     */
    @PostMapping("/registrar")
    @PreAuthorize("hasRole('VETERINARIO')")
    @Operation(summary = "Registrar una nueva consulta", description = "Registra una consulta veterinaria para una cita específica")
    public ResponseEntity<ApiResponse<ConsultaResponse>> registrarConsulta(
            @Valid @RequestBody ConsultaRequest request,
            Authentication authentication) {
        
        log.info("📝 Registro de consulta solicitado por veterinario: {}", authentication.getName());
        
        // Obtener el ID del usuario autenticado desde username
        String username = authentication.getName();
        Long veterinarioId = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getIdUsuario();
        
        ConsultaResponse consulta = consultaService.registrarConsulta(request, veterinarioId);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Consulta registrada exitosamente", consulta));
    }

    /**
     * Obtiene el historial de consultas de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Lista de consultas
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'SECRETARIO', 'ADMIN')")
    @Operation(summary = "Obtener historial de consultas", description = "Obtiene todas las consultas registradas para un paciente")
    public ResponseEntity<ApiResponse<List<ConsultaResponse>>> obtenerHistorial(
            @PathVariable Long pacienteId) {
        
        log.info("🔍 Obteniendo historial de consultas para paciente: {}", pacienteId);
        
        List<ConsultaResponse> consultas = consultaService.obtenerHistorialPaciente(pacienteId);
        
        return ResponseEntity.ok(ApiResponse.success("Historial obtenido", consultas));
    }

    /**
     * Obtiene una consulta específica.
     * 
     * @param registroId ID del registro médico
     * @return Datos de la consulta
     */
    @GetMapping("/{registroId}")
    @PreAuthorize("hasAnyRole('VETERINARIO', 'SECRETARIO', 'ADMIN')")
    @Operation(summary = "Obtener una consulta", description = "Obtiene los detalles de una consulta específica")
    public ResponseEntity<ApiResponse<ConsultaResponse>> obtenerConsulta(
            @PathVariable Long registroId) {
        
        log.info("🔍 Obteniendo consulta: {}", registroId);
        
        ConsultaResponse consulta = consultaService.obtenerConsulta(registroId);
        
        return ResponseEntity.ok(ApiResponse.success("Consulta obtenida", consulta));
    }
}
