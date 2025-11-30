package com.tuorg.veterinaria.prestacioneservicios.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.prestacioneservicios.dto.SolicitudCitaRequest;
import com.tuorg.veterinaria.prestacioneservicios.dto.SolicitudCitaResponse;
import com.tuorg.veterinaria.prestacioneservicios.service.SolicitudCitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para solicitudes de cita del portal del cliente.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/solicitudes-citas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Solicitudes de Citas", description = "API para solicitudes de cita desde el portal del cliente")
public class SolicitudCitaController {

    private final SolicitudCitaService solicitudCitaService;
    private final com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository usuarioRepository;

    /**
     * Crea una nueva solicitud de cita.
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Solicitar cita", description = "Permite a un cliente solicitar una cita")
    public ResponseEntity<ApiResponse<SolicitudCitaResponse>> crear(
            @Valid @RequestBody SolicitudCitaRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        Long clienteId = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getIdUsuario();
        SolicitudCitaResponse solicitud = solicitudCitaService.crear(request, clienteId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Solicitud de cita creada exitosamente", solicitud));
    }

    /**
     * Obtiene las solicitudes de cita del cliente autenticado.
     */
    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Mis solicitudes", description = "Obtiene las solicitudes de cita del cliente")
    public ResponseEntity<ApiResponse<Page<SolicitudCitaResponse>>> obtenerMias(
            Pageable pageable,
            Authentication authentication) {

        String username = authentication.getName();
        Long clienteId = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getIdUsuario();
        Page<SolicitudCitaResponse> solicitudes = solicitudCitaService.obtenerPorCliente(clienteId, pageable);

        return ResponseEntity.ok(ApiResponse.success("Solicitudes obtenidas exitosamente", solicitudes));
    }

    /**
     * Obtiene una solicitud específica.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'SECRETARIO', 'ADMIN')")
    @Operation(summary = "Obtener solicitud", description = "Obtiene los detalles de una solicitud")
    public ResponseEntity<ApiResponse<SolicitudCitaResponse>> obtener(@PathVariable Long id) {

        SolicitudCitaResponse solicitud = solicitudCitaService.obtener(id);

        return ResponseEntity.ok(ApiResponse.success("Solicitud obtenida exitosamente", solicitud));
    }

    /**
     * Obtiene las solicitudes pendientes (solo secretario/admin).
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('SECRETARIO', 'ADMIN')")
    @Operation(summary = "Solicitudes pendientes", description = "Obtiene todas las solicitudes pendientes de aprobación")
    public ResponseEntity<ApiResponse<Page<SolicitudCitaResponse>>> obtenerPendientes(Pageable pageable) {

        Page<SolicitudCitaResponse> solicitudes = solicitudCitaService.obtenerPendientes(pageable);

        return ResponseEntity.ok(ApiResponse.success("Solicitudes pendientes obtenidas", solicitudes));
    }

    /**
     * Aprueba una solicitud de cita.
     * 
     * 🟡 AUDIT TRAIL: Se registra el usuario que aprobó
     */
    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('SECRETARIO', 'ADMIN')")
    @Operation(summary = "Aprobar solicitud", description = "Aprueba una solicitud de cita")
    public ResponseEntity<ApiResponse<SolicitudCitaResponse>> aprobar(
            @PathVariable Long id,
            @RequestParam Long veterinarioId,
            Authentication authentication) {

        String username = authentication.getName();
        Long secretarioId = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getIdUsuario();
        SolicitudCitaResponse solicitud = solicitudCitaService.aprobar(id, veterinarioId, secretarioId);

        return ResponseEntity.ok(ApiResponse.success("Solicitud aprobada exitosamente", solicitud));
    }

    /**
     * Rechaza una solicitud de cita.
     * 
     * 🟡 AUDIT TRAIL: Se registra el usuario que rechazó
     */
    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('SECRETARIO', 'ADMIN')")
    @Operation(summary = "Rechazar solicitud", description = "Rechaza una solicitud de cita con motivo")
    public ResponseEntity<ApiResponse<SolicitudCitaResponse>> rechazar(
            @PathVariable Long id,
            @RequestParam String motivo,
            Authentication authentication) {

        String username = authentication.getName();
        Long secretarioId = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getIdUsuario();
        SolicitudCitaResponse solicitud = solicitudCitaService.rechazar(id, motivo, secretarioId);

        return ResponseEntity.ok(ApiResponse.success("Solicitud rechazada", solicitud));
    }

    /**
     * Cancela una solicitud de cita.
     * 
     * 🟡 AUDIT TRAIL: Se registra el usuario que canceló
     */
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'SECRETARIO', 'ADMIN')")
    @Operation(summary = "Cancelar solicitud", description = "Cancela una solicitud de cita")
    public ResponseEntity<ApiResponse<SolicitudCitaResponse>> cancelar(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        Long usuarioId = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getIdUsuario();
        SolicitudCitaResponse solicitud = solicitudCitaService.cancelar(id, usuarioId);

        return ResponseEntity.ok(ApiResponse.success("Solicitud cancelada", solicitud));
    }

    /**
     * Obtiene el conteo de solicitudes pendientes.
     */
    @GetMapping("/estadisticas/pendientes")
    @PreAuthorize("hasAnyRole('SECRETARIO', 'ADMIN')")
    @Operation(summary = "Contar pendientes", description = "Obtiene el número de solicitudes pendientes")
    public ResponseEntity<ApiResponse<Long>> contarPendientes() {

        Long pendientes = solicitudCitaService.contarPendientes();

        return ResponseEntity.ok(ApiResponse.success("Conteo de solicitudes pendientes", pendientes));
    }
}
