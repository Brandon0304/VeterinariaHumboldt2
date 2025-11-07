package com.tuorg.veterinaria.gestionpacientes.service;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.common.util.ValidationUtil;
import com.tuorg.veterinaria.gestionpacientes.model.HistoriaClinica;
import com.tuorg.veterinaria.gestionpacientes.model.Paciente;
import com.tuorg.veterinaria.gestionpacientes.repository.HistoriaClinicaRepository;
import com.tuorg.veterinaria.gestionpacientes.repository.PacienteRepository;
import com.tuorg.veterinaria.gestionusuarios.model.Cliente;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para la gestión de pacientes.
 * 
 * Este servicio proporciona métodos para crear, actualizar, eliminar
 * y consultar pacientes. Al crear un paciente, automáticamente se crea
 * su historia clínica asociada.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class PacienteService {

    /**
     * Repositorio de pacientes.
     */
    private final PacienteRepository pacienteRepository;

    /**
     * Repositorio de historias clínicas.
     */
    private final HistoriaClinicaRepository historiaClinicaRepository;

    /**
     * Repositorio de usuarios (para validar cliente).
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param pacienteRepository Repositorio de pacientes
     * @param historiaClinicaRepository Repositorio de historias clínicas
     * @param usuarioRepository Repositorio de usuarios
     */
    @Autowired
    public PacienteService(PacienteRepository pacienteRepository,
                          HistoriaClinicaRepository historiaClinicaRepository,
                          UsuarioRepository usuarioRepository) {
        this.pacienteRepository = pacienteRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un nuevo paciente y crea su historia clínica asociada.
     * 
     * Esta operación es transaccional: si falla la creación de la historia
     * clínica, se revierte la creación del paciente.
     * 
     * @param paciente Paciente a registrar
     * @return Paciente creado con su historia clínica
     */
    @Transactional
    public Paciente registrarPaciente(Paciente paciente) {
        // Validar especie
        if (!AppConstants.ESPECIE_PERRO.equalsIgnoreCase(paciente.getEspecie()) &&
            !AppConstants.ESPECIE_GATO.equalsIgnoreCase(paciente.getEspecie())) {
            throw new BusinessException("La especie debe ser 'perro' o 'gato'");
        }

        // Validar fecha de nacimiento
        if (paciente.getFechaNacimiento() != null &&
            paciente.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new BusinessException("La fecha de nacimiento no puede ser futura");
        }

        // Validar peso
        if (paciente.getPesoKg() != null) {
            ValidationUtil.validatePositiveNumber(
                    paciente.getPesoKg().doubleValue(), "peso_kg");
        }

        // Validar que el cliente exista
        Cliente cliente = (Cliente) usuarioRepository.findById(paciente.getCliente().getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", 
                        paciente.getCliente().getIdUsuario()));

        // Generar identificador externo si no existe
        if (paciente.getIdentificadorExterno() == null) {
            paciente.setIdentificadorExterno(UUID.randomUUID());
        }

        // Guardar paciente
        Paciente pacienteGuardado = pacienteRepository.save(paciente);

        // Crear historia clínica asociada (invariante: cada paciente tiene al menos 1 historia)
        HistoriaClinica historiaClinica = new HistoriaClinica();
        historiaClinica.setPaciente(pacienteGuardado);
        historiaClinica.setFechaApertura(LocalDateTime.now());
        historiaClinica.setResumen("Historia clínica creada automáticamente al registrar el paciente");
        historiaClinicaRepository.save(historiaClinica);

        return pacienteGuardado;
    }

    /**
     * Obtiene un paciente por su ID.
     * 
     * @param id ID del paciente
     * @return Paciente encontrado
     */
    @Transactional(readOnly = true)
    public Paciente obtener(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id));
    }

    /**
     * Obtiene todos los pacientes.
     * 
     * @return Lista de pacientes
     */
    @Transactional(readOnly = true)
    public List<Paciente> obtenerTodos() {
        return pacienteRepository.findAll();
    }

    /**
     * Obtiene pacientes por cliente (dueño).
     * 
     * @param clienteId ID del cliente
     * @return Lista de pacientes del cliente
     */
    @Transactional(readOnly = true)
    public List<Paciente> obtenerPorCliente(Long clienteId) {
        return pacienteRepository.findByClienteId(clienteId);
    }

    /**
     * Actualiza los datos de un paciente.
     * 
     * @param id ID del paciente
     * @param paciente Datos actualizados del paciente
     * @return Paciente actualizado
     */
    @Transactional
    public Paciente actualizarDatos(Long id, Paciente paciente) {
        Paciente pacienteExistente = obtener(id);

        // Actualizar campos permitidos
        if (paciente.getNombre() != null) {
            pacienteExistente.setNombre(paciente.getNombre());
        }
        if (paciente.getRaza() != null) {
            pacienteExistente.setRaza(paciente.getRaza());
        }
        if (paciente.getFechaNacimiento() != null) {
            if (paciente.getFechaNacimiento().isAfter(LocalDate.now())) {
                throw new BusinessException("La fecha de nacimiento no puede ser futura");
            }
            pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());
        }
        if (paciente.getSexo() != null) {
            pacienteExistente.setSexo(paciente.getSexo());
        }
        if (paciente.getPesoKg() != null) {
            ValidationUtil.validatePositiveNumber(
                    paciente.getPesoKg().doubleValue(), "peso_kg");
            pacienteExistente.setPesoKg(paciente.getPesoKg());
        }
        if (paciente.getEstadoSalud() != null) {
            pacienteExistente.setEstadoSalud(paciente.getEstadoSalud());
        }

        return pacienteRepository.save(pacienteExistente);
    }

    /**
     * Genera un resumen clínico del paciente.
     * 
     * @param id ID del paciente
     * @return Resumen clínico en formato String
     */
    @Transactional(readOnly = true)
    public String generarResumenClinico(Long id) {
        Paciente paciente = obtener(id);
        HistoriaClinica historia = historiaClinicaRepository.findByPacienteId(id)
                .orElseThrow(() -> new ResourceNotFoundException("HistoriaClinica", "paciente_id", id));

        StringBuilder resumen = new StringBuilder();
        resumen.append("Resumen Clínico - ").append(paciente.getNombre()).append("\n");
        resumen.append("Especie: ").append(paciente.getEspecie()).append("\n");
        resumen.append("Raza: ").append(paciente.getRaza() != null ? paciente.getRaza() : "N/A").append("\n");
        resumen.append("Peso: ").append(paciente.getPesoKg() != null ? 
                paciente.getPesoKg() + " kg" : "N/A").append("\n");
        resumen.append("Estado de salud: ").append(paciente.getEstadoSalud() != null ? 
                paciente.getEstadoSalud() : "N/A").append("\n");
        resumen.append("Historia clínica abierta: ").append(historia.getFechaApertura()).append("\n");

        return resumen.toString();
    }
}
