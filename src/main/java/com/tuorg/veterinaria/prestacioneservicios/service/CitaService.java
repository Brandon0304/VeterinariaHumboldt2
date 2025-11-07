package com.tuorg.veterinaria.prestacioneservicios.service;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.gestionpacientes.model.Paciente;
import com.tuorg.veterinaria.gestionpacientes.repository.PacienteRepository;
import com.tuorg.veterinaria.gestionusuarios.model.UsuarioVeterinario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import com.tuorg.veterinaria.prestacioneservicios.model.Cita;
import com.tuorg.veterinaria.prestacioneservicios.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de citas.
 * 
 * Este servicio proporciona métodos para programar, reprogramar,
 * cancelar y completar citas médicas.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class CitaService {

    /**
     * Repositorio de citas.
     */
    private final CitaRepository citaRepository;

    /**
     * Repositorio de pacientes.
     */
    private final PacienteRepository pacienteRepository;

    /**
     * Repositorio de usuarios (para obtener veterinarios).
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param citaRepository Repositorio de citas
     * @param pacienteRepository Repositorio de pacientes
     * @param usuarioRepository Repositorio de usuarios
     */
    @Autowired
    public CitaService(CitaRepository citaRepository,
                      PacienteRepository pacienteRepository,
                      UsuarioRepository usuarioRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Programa una nueva cita.
     * 
     * Valida que no haya doble reserva para el mismo veterinario
     * en la misma fecha y hora.
     * 
     * @param cita Cita a programar
     * @return Cita creada
     */
    @Transactional
    public Cita programar(Cita cita) {
        // Validar que el paciente exista
        Paciente paciente = pacienteRepository.findById(cita.getPaciente().getIdPaciente())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", 
                        cita.getPaciente().getIdPaciente()));

        // Validar que el veterinario exista
        UsuarioVeterinario veterinario = (UsuarioVeterinario) usuarioRepository
                .findById(cita.getVeterinario().getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "id", 
                        cita.getVeterinario().getIdUsuario()));

        // Validar fecha/hora
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BusinessException("No se puede programar una cita en el pasado");
        }

        // Validar que no haya doble reserva
        if (citaRepository.existeCitaEnFechaHora(
                veterinario.getIdUsuario(), cita.getFechaHora(), AppConstants.ESTADO_CITA_PROGRAMADA)) {
            throw new BusinessException("El veterinario ya tiene una cita programada en esa fecha y hora");
        }

        cita.setEstado(AppConstants.ESTADO_CITA_PROGRAMADA);
        return citaRepository.save(cita);
    }

    /**
     * Reprograma una cita existente.
     * 
     * @param citaId ID de la cita
     * @param nuevaFechaHora Nueva fecha y hora para la cita
     * @return Cita actualizada
     */
    @Transactional
    public Cita reprogramar(Long citaId, LocalDateTime nuevaFechaHora) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", citaId));

        if (!AppConstants.ESTADO_CITA_PROGRAMADA.equals(cita.getEstado())) {
            throw new BusinessException("Solo se pueden reprogramar citas en estado PROGRAMADA");
        }

        if (nuevaFechaHora.isBefore(LocalDateTime.now())) {
            throw new BusinessException("No se puede reprogramar una cita al pasado");
        }

        // Validar que no haya doble reserva
        if (citaRepository.existeCitaEnFechaHora(
                cita.getVeterinario().getIdUsuario(), nuevaFechaHora, AppConstants.ESTADO_CITA_PROGRAMADA)) {
            throw new BusinessException("El veterinario ya tiene una cita programada en esa fecha y hora");
        }

        cita.setFechaHora(nuevaFechaHora);
        return citaRepository.save(cita);
    }

    /**
     * Cancela una cita.
     * 
     * @param citaId ID de la cita
     * @param motivo Motivo de la cancelación
     * @return Cita cancelada
     */
    @Transactional
    public Cita cancelar(Long citaId, String motivo) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", citaId));

        if (AppConstants.ESTADO_CITA_REALIZADA.equals(cita.getEstado())) {
            throw new BusinessException("No se puede cancelar una cita ya realizada");
        }

        cita.setEstado(AppConstants.ESTADO_CITA_CANCELADA);
        if (motivo != null) {
            cita.setMotivo((cita.getMotivo() != null ? cita.getMotivo() + "\n" : "") + 
                          "Cancelada: " + motivo);
        }
        return citaRepository.save(cita);
    }

    /**
     * Marca una cita como completada (REALIZADA).
     * 
     * @param citaId ID de la cita
     * @return Cita completada
     */
    @Transactional
    public Cita completar(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", citaId));

        if (!AppConstants.ESTADO_CITA_PROGRAMADA.equals(cita.getEstado())) {
            throw new BusinessException("Solo se pueden completar citas en estado PROGRAMADA");
        }

        cita.setEstado(AppConstants.ESTADO_CITA_REALIZADA);
        return citaRepository.save(cita);
    }

    /**
     * Obtiene todas las citas de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Lista de citas del paciente
     */
    @Transactional(readOnly = true)
    public List<Cita> obtenerPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId);
    }

    /**
     * Obtiene todas las citas de un veterinario.
     * 
     * @param veterinarioId ID del veterinario
     * @return Lista de citas del veterinario
     */
    @Transactional(readOnly = true)
    public List<Cita> obtenerPorVeterinario(Long veterinarioId) {
        return citaRepository.findByVeterinarioId(veterinarioId);
    }
}

