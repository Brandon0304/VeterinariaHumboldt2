package com.tuorg.veterinaria.gestionpacientes.service;

import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.gestionpacientes.model.Paciente;
import com.tuorg.veterinaria.gestionpacientes.model.Vacunacion;
import com.tuorg.veterinaria.gestionpacientes.repository.PacienteRepository;
import com.tuorg.veterinaria.gestionpacientes.repository.VacunacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para la gestión de vacunaciones.
 * 
 * Este servicio proporciona métodos para registrar vacunaciones
 * y programar próximas dosis.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class VacunacionService {

    /**
     * Repositorio de vacunaciones.
     */
    private final VacunacionRepository vacunacionRepository;

    /**
     * Repositorio de pacientes.
     */
    private final PacienteRepository pacienteRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param vacunacionRepository Repositorio de vacunaciones
     * @param pacienteRepository Repositorio de pacientes
     */
    @Autowired
    public VacunacionService(VacunacionRepository vacunacionRepository,
                             PacienteRepository pacienteRepository) {
        this.vacunacionRepository = vacunacionRepository;
        this.pacienteRepository = pacienteRepository;
    }

    /**
     * Registra una nueva vacunación.
     * 
     * @param vacunacion Vacunación a registrar
     * @return Vacunación creada
     */
    @Transactional
    public Vacunacion registrarVacuna(Vacunacion vacunacion) {
        // Validar que el paciente exista
        Paciente paciente = pacienteRepository.findById(vacunacion.getPaciente().getIdPaciente())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", 
                        vacunacion.getPaciente().getIdPaciente()));

        // Validar fecha de aplicación
        if (vacunacion.getFechaAplicacion().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de aplicación no puede ser futura");
        }

        return vacunacionRepository.save(vacunacion);
    }

    /**
     * Programa la próxima dosis de una vacunación.
     * 
     * @param vacunacionId ID de la vacunación
     * @param proximaDosis Fecha de la próxima dosis
     * @return Vacunación actualizada
     */
    @Transactional
    public Vacunacion programarProximaDosis(Long vacunacionId, LocalDate proximaDosis) {
        Vacunacion vacunacion = vacunacionRepository.findById(vacunacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Vacunacion", "id", vacunacionId));

        if (proximaDosis.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de próxima dosis no puede ser pasada");
        }

        vacunacion.setProximaDosis(proximaDosis);
        return vacunacionRepository.save(vacunacion);
    }

    /**
     * Obtiene todas las vacunaciones de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Lista de vacunaciones
     */
    @Transactional(readOnly = true)
    public List<Vacunacion> obtenerPorPaciente(Long pacienteId) {
        return vacunacionRepository.findByPacienteId(pacienteId);
    }

    /**
     * Obtiene vacunaciones pendientes (próximas a vencer).
     * 
     * @param dias Días de anticipación para considerar pendiente
     * @return Lista de vacunaciones pendientes
     */
    @Transactional(readOnly = true)
    public List<Vacunacion> obtenerVacunacionesPendientes(int dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return vacunacionRepository.findVacunacionesPendientes(fechaLimite);
    }
}

