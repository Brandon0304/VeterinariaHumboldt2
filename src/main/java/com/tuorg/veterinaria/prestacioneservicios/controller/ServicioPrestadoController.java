package com.tuorg.veterinaria.prestacioneservicios.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.prestacioneservicios.dto.ServicioPrestadoRequest;
import com.tuorg.veterinaria.prestacioneservicios.dto.ServicioPrestadoResponse;
import com.tuorg.veterinaria.prestacioneservicios.service.ServicioPrestadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST para orquestar la ejecución de servicios prestados.
 */
@RestController
@RequestMapping("/servicios-prestados")
public class ServicioPrestadoController {

    private final ServicioPrestadoService servicioPrestadoService;

    @Autowired
    public ServicioPrestadoController(ServicioPrestadoService servicioPrestadoService) {
        this.servicioPrestadoService = servicioPrestadoService;
    }

    /**
     * Registra la ejecución de un servicio y desencadena las acciones asociadas
     * (facturación, actualización de cita, consumo de insumos).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ServicioPrestadoResponse>> registrar(
            @RequestBody @Valid ServicioPrestadoRequest request) {
        ServicioPrestadoResponse response = servicioPrestadoService.registrarEjecucion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Servicio prestado registrado exitosamente", response));
    }

    /**
     * Obtiene todos los servicios registrados para una cita específica.
     */
    @GetMapping("/cita/{citaId}")
    public ResponseEntity<ApiResponse<List<ServicioPrestadoResponse>>> obtenerPorCita(@PathVariable Long citaId) {
        List<ServicioPrestadoResponse> servicios = servicioPrestadoService.obtenerPorCita(citaId);
        return ResponseEntity.ok(ApiResponse.success("Servicios prestados obtenidos exitosamente", servicios));
    }

    /**
     * Genera un resumen en texto plano del servicio prestado.
     */
    @GetMapping("/{servicioPrestadoId}/resumen")
    public ResponseEntity<ApiResponse<String>> generarResumen(@PathVariable Long servicioPrestadoId) {
        String resumen = servicioPrestadoService.generarResumen(servicioPrestadoId);
        return ResponseEntity.ok(ApiResponse.success("Resumen generado exitosamente", resumen));
    }
}


