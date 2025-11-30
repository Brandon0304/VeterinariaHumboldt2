package com.tuorg.veterinaria.prestacioneservicios.service;

import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.gestionpacientes.model.HistoriaClinica;
import com.tuorg.veterinaria.gestionpacientes.model.Paciente;
import com.tuorg.veterinaria.gestionpacientes.model.RegistroMedico;
import com.tuorg.veterinaria.gestionpacientes.repository.HistoriaClinicaRepository;
import com.tuorg.veterinaria.gestionpacientes.repository.PacienteRepository;
import com.tuorg.veterinaria.gestionpacientes.repository.RegistroMedicoRepository;
import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.model.UsuarioVeterinario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import com.tuorg.veterinaria.prestacioneservicios.dto.ConsultaRequest;
import com.tuorg.veterinaria.prestacioneservicios.dto.ConsultaResponse;
import com.tuorg.veterinaria.prestacioneservicios.model.Cita;
import com.tuorg.veterinaria.prestacioneservicios.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de consultas veterinarias.
 * 
 * Coordina el registro de consultas médicas asociadas a citas,
 * creando registros médicos en la historia clínica del paciente.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final RegistroMedicoRepository registroMedicoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Registra una consulta para una cita específica.
     * 
     * Esta operación:
     * 1. Valida que la cita existe y está en estado REALIZADA o PROGRAMADA
     * 2. Obtiene la historia clínica del paciente
     * 3. Crea un nuevo registro médico con la información de la consulta
     * 4. Marca la cita como REALIZADA
     * 
     * @param request Datos de la consulta
     * @param veterinarioId ID del veterinario que registra la consulta
     * @return Respuesta con la consulta registrada
     */
    @Transactional
    public ConsultaResponse registrarConsulta(ConsultaRequest request, Long veterinarioId) {
        log.info("📝 Registrando consulta para cita ID: {}", request.getCitaId());

        // Validar que la cita existe
        Cita cita = citaRepository.findById(request.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", request.getCitaId()));

        // Obtener paciente
        Paciente paciente = cita.getPaciente();
        if (paciente == null) {
            throw new BusinessException("La cita no tiene paciente asociado");
        }

        // Obtener historia clínica del paciente
        HistoriaClinica historia = historiaClinicaRepository.findByPacienteId(paciente.getIdPaciente())
                .orElseThrow(() -> new ResourceNotFoundException("HistoriaClinica", "pacienteId", paciente.getIdPaciente()));

        // Obtener veterinario
        Usuario usuarioVet = usuarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", veterinarioId));
        
        if (!(usuarioVet instanceof UsuarioVeterinario)) {
            throw new BusinessException("El usuario no corresponde a un veterinario");
        }
        UsuarioVeterinario veterinario = (UsuarioVeterinario) usuarioVet;

        // Crear registro médico
        RegistroMedico registro = new RegistroMedico();
        registro.setHistoria(historia);
        registro.setFecha(LocalDateTime.now());
        registro.setMotivo(request.getMotivo());
        registro.setDiagnostico(request.getDiagnostico());
        registro.setSignosVitales(request.getSignosVitales());
        registro.setTratamiento(request.getTratamiento());
        registro.setInsumosUsados(request.getInsumosUsados());
        registro.setArchivos(request.getArchivos());
        registro.setVeterinario(veterinario);

        RegistroMedico registroGuardado = registroMedicoRepository.save(registro);

        // Marcar cita como REALIZADA
        cita.setEstado("REALIZADA");
        citaRepository.save(cita);

        log.info("✅ Consulta registrada exitosamente. Registro ID: {}", registroGuardado.getIdRegistro());

        return mapToResponse(registroGuardado, cita, paciente, veterinario);
    }

    /**
     * Obtiene el historial de consultas de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Lista de consultas registradas
     */
    @Transactional(readOnly = true)
    public List<ConsultaResponse> obtenerHistorialPaciente(Long pacienteId) {
        log.info("🔍 Obteniendo historial de consultas para paciente ID: {}", pacienteId);

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", pacienteId));

        HistoriaClinica historia = historiaClinicaRepository.findByPacienteId(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("HistoriaClinica", "pacienteId", pacienteId));

        List<RegistroMedico> registros = registroMedicoRepository.findByHistoriaId(historia.getIdHistoria());

        return registros.stream()
                .map(r -> ConsultaResponse.builder()
                        .registroId(r.getIdRegistro())
                        .pacienteId(paciente.getIdPaciente())
                        .nombrePaciente(paciente.getNombre())
                        .motivo(r.getMotivo())
                        .diagnostico(r.getDiagnostico())
                        .signosVitales(r.getSignosVitales())
                        .tratamiento(r.getTratamiento())
                        .nombreVeterinario(r.getVeterinario() != null ? 
                                r.getVeterinario().getNombre() + " " + r.getVeterinario().getApellido() : 
                                "Sin asignar")
                        .fechaRegistro(r.getFecha())
                        .insumosUsados(r.getInsumosUsados())
                        .archivos(r.getArchivos())
                        .build())
                .toList();
    }

    /**
     * Obtiene una consulta específica.
     * 
     * @param registroId ID del registro médico
     * @return Datos de la consulta
     */
    @Transactional(readOnly = true)
    public ConsultaResponse obtenerConsulta(Long registroId) {
        log.info("🔍 Obteniendo consulta ID: {}", registroId);

        RegistroMedico registro = registroMedicoRepository.findById(registroId)
                .orElseThrow(() -> new ResourceNotFoundException("RegistroMedico", "id", registroId));

        Paciente paciente = registro.getHistoria().getPaciente();

        return ConsultaResponse.builder()
                .registroId(registro.getIdRegistro())
                .pacienteId(paciente.getIdPaciente())
                .nombrePaciente(paciente.getNombre())
                .motivo(registro.getMotivo())
                .diagnostico(registro.getDiagnostico())
                .signosVitales(registro.getSignosVitales())
                .tratamiento(registro.getTratamiento())
                .nombreVeterinario(registro.getVeterinario() != null ? 
                        registro.getVeterinario().getNombre() + " " + registro.getVeterinario().getApellido() : 
                        "Sin asignar")
                .fechaRegistro(registro.getFecha())
                .insumosUsados(registro.getInsumosUsados())
                .archivos(registro.getArchivos())
                .build();
    }

    /**
     * Mapea un RegistroMedico a ConsultaResponse.
     */
    private ConsultaResponse mapToResponse(RegistroMedico registro, Cita cita, Paciente paciente, UsuarioVeterinario veterinario) {
        return ConsultaResponse.builder()
                .registroId(registro.getIdRegistro())
                .citaId(cita.getIdCita())
                .pacienteId(paciente.getIdPaciente())
                .nombrePaciente(paciente.getNombre())
                .motivo(registro.getMotivo())
                .diagnostico(registro.getDiagnostico())
                .signosVitales(registro.getSignosVitales())
                .tratamiento(registro.getTratamiento())
                .nombreVeterinario(veterinario.getNombre() + " " + veterinario.getApellido())
                .fechaRegistro(registro.getFecha())
                .insumosUsados(registro.getInsumosUsados())
                .archivos(registro.getArchivos())
                .build();
    }
}
