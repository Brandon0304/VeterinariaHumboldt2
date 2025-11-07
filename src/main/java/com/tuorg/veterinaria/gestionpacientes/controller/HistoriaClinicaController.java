package com.tuorg.veterinaria.gestionpacientes.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionpacientes.model.HistoriaClinica;
import com.tuorg.veterinaria.gestionpacientes.model.RegistroMedico;
import com.tuorg.veterinaria.gestionpacientes.service.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de historias clínicas.
 * 
 * Este controlador expone endpoints para consultar historias clínicas,
 * agregar registros médicos y exportar información.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/historias-clinicas")
public class HistoriaClinicaController {

    /**
     * Servicio de gestión de historias clínicas.
     */
    private final HistoriaClinicaService historiaClinicaService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param historiaClinicaService Servicio de historias clínicas
     */
    @Autowired
    public HistoriaClinicaController(HistoriaClinicaService historiaClinicaService) {
        this.historiaClinicaService = historiaClinicaService;
    }

    /**
     * Obtiene la historia clínica de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Respuesta con la historia clínica
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<ApiResponse<HistoriaClinica>> obtenerPorPaciente(@PathVariable Long pacienteId) {
        HistoriaClinica historia = historiaClinicaService.obtenerPorPaciente(pacienteId);
        return ResponseEntity.ok(ApiResponse.success("Historia clínica obtenida exitosamente", historia));
    }

    /**
     * Agrega un registro médico a una historia clínica.
     * 
     * @param historiaId ID de la historia clínica
     * @param registro Registro médico a agregar
     * @return Respuesta con el registro médico creado
     */
    @PostMapping("/{historiaId}/registros")
    public ResponseEntity<ApiResponse<RegistroMedico>> agregarRegistro(
            @PathVariable Long historiaId,
            @RequestBody RegistroMedico registro) {
        RegistroMedico registroCreado = historiaClinicaService.agregarRegistro(historiaId, registro);
        return ResponseEntity.ok(ApiResponse.success("Registro médico agregado exitosamente", registroCreado));
    }

    /**
     * Obtiene todos los registros médicos de una historia clínica.
     * 
     * @param historiaId ID de la historia clínica
     * @return Respuesta con la lista de registros médicos
     */
    @GetMapping("/{historiaId}/registros")
    public ResponseEntity<ApiResponse<List<RegistroMedico>>> obtenerRegistros(@PathVariable Long historiaId) {
        List<RegistroMedico> registros = historiaClinicaService.obtenerRegistros(historiaId);
        return ResponseEntity.ok(ApiResponse.success("Registros médicos obtenidos exitosamente", registros));
    }

    /**
     * Exporta la historia clínica como PDF.
     * 
     * @param historiaId ID de la historia clínica
     * @return Respuesta con el PDF en bytes
     */
    @GetMapping("/{historiaId}/exportar-pdf")
    public ResponseEntity<byte[]> exportarPDF(@PathVariable Long historiaId) {
        byte[] pdf = historiaClinicaService.exportarPDF(historiaId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=historia_clinica_" + historiaId + ".pdf")
                .body(pdf);
    }
}
