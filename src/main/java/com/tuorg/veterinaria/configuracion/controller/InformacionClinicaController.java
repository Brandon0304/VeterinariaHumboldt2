package com.tuorg.veterinaria.configuracion.controller;

import com.tuorg.veterinaria.configuracion.model.InformacionClinica;
import com.tuorg.veterinaria.configuracion.service.InformacionClinicaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de información de la clínica.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/configuracion/clinica")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InformacionClinicaController {

    private final InformacionClinicaService informacionClinicaService;

    /**
     * Obtiene la información activa de la clínica (Singleton).
     */
    @GetMapping
    public ResponseEntity<InformacionClinica> obtenerInformacionActiva() {
        log.info("GET /api/v1/configuracion/clinica - Obteniendo información de clínica");
        InformacionClinica informacion = informacionClinicaService.obtenerInformacionActiva();
        return ResponseEntity.ok(informacion);
    }

    /**
     * Actualiza la información de la clínica.
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InformacionClinica> actualizarInformacion(
            @RequestBody InformacionClinica informacion) {
        log.info("PUT /api/v1/configuracion/clinica - Actualizando información");
        InformacionClinica actualizada = informacionClinicaService.actualizarInformacion(informacion);
        return ResponseEntity.ok(actualizada);
    }

    /**
     * Verifica si existe configuración de clínica.
     */
    @GetMapping("/existe")
    public ResponseEntity<Boolean> existeConfiguracion() {
        boolean existe = informacionClinicaService.existeConfiguracion();
        return ResponseEntity.ok(existe);
    }
}
