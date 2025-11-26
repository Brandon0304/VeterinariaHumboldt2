package com.tuorg.veterinaria.dashboard.controller;

import com.tuorg.veterinaria.dashboard.dto.AdminDashboardResponse;
import com.tuorg.veterinaria.dashboard.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el dashboard del administrador.
 * Solo accesible para usuarios con rol ADMIN.
 */
@RestController
@RequestMapping("/dashboard/admin")
@Tag(name = "Dashboard Administrador", description = "Endpoints para el dashboard del administrador con métricas completas")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Autowired
    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Obtener dashboard completo del administrador",
            description = "Retorna todas las métricas, estadísticas y datos para gráficos del dashboard administrativo. Incluye resumen general, finanzas, pacientes, inventario, personal y datos para visualizaciones."
    )
    @ApiResponse(responseCode = "200", description = "Dashboard obtenido exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    public ResponseEntity<AdminDashboardResponse> obtenerDashboard() {
        AdminDashboardResponse dashboard = adminDashboardService.obtenerDashboard();
        return ResponseEntity.ok(dashboard);
    }
}
