package com.tuorg.veterinaria.prestacioneservicios.service;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.gestionpacientes.model.Paciente;
import com.tuorg.veterinaria.gestionpacientes.repository.PacienteRepository;
import com.tuorg.veterinaria.gestionusuarios.model.Cliente;
import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.model.UsuarioVeterinario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import com.tuorg.veterinaria.prestacioneservicios.dto.CitaCancelarRequest;
import com.tuorg.veterinaria.prestacioneservicios.dto.CitaRequest;
import com.tuorg.veterinaria.prestacioneservicios.dto.CitaResponse;
import com.tuorg.veterinaria.prestacioneservicios.dto.CitaReprogramarRequest;
import com.tuorg.veterinaria.prestacioneservicios.model.Cita;
import com.tuorg.veterinaria.prestacioneservicios.repository.CitaRepository;
import com.tuorg.veterinaria.notificaciones.service.NotificacionService;
import com.tuorg.veterinaria.notificaciones.dto.NotificacionEnviarRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Servicio para la gestión de citas.
 *
 * Se encarga de coordinar la lógica de negocio relacionada con el ciclo de vida
 * de una cita (programar, reprogramar, cancelar y completar) y de exponer las
 * operaciones en DTOs desacoplados de las entidades JPA.
 */
@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    @Autowired
    public CitaService(CitaRepository citaRepository,
                       PacienteRepository pacienteRepository,
                       UsuarioRepository usuarioRepository,
                       NotificacionService notificacionService) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * Programa una nueva cita validando disponibilidad y referencias.
     */
    @Transactional
    public CitaResponse programar(CitaRequest request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", request.getPacienteId()));

        Usuario usuario = usuarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "id", request.getVeterinarioId()));

        if (!(usuario instanceof UsuarioVeterinario veterinario)) {
            throw new BusinessException("El usuario indicado no corresponde a un veterinario activo");
        }

        LocalDateTime fechaHora = request.getFechaHora();
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new BusinessException("No se puede programar una cita en el pasado");
        }

        // Validar disponibilidad considerando la duración de la cita
        if (!validarDisponibilidad(veterinario.getIdUsuario(), fechaHora)) {
            throw new BusinessException("El veterinario ya tiene una cita programada en ese horario. Por favor, seleccione otra fecha y hora");
        }

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setVeterinario(veterinario);
        cita.setFechaHora(fechaHora);
        cita.setTipoServicio(request.getTipoServicio());
        cita.setMotivo(request.getMotivo());
        cita.setTriageNivel(request.getTriageNivel());
        cita.setEstado(AppConstants.ESTADO_CITA_PROGRAMADA);

        Cita guardada = citaRepository.save(cita);
        
        // Enviar notificación por email al cliente
        enviarNotificacionCitaCreada(guardada);
        
        return mapToResponse(guardada);
    }

    /**
     * Reprograma una cita existente garantizando la disponibilidad del veterinario.
     */
    @Transactional
    public CitaResponse reprogramar(Long citaId, CitaReprogramarRequest request) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", citaId));

        if (!AppConstants.ESTADO_CITA_PROGRAMADA.equals(cita.getEstado())) {
            throw new BusinessException("Solo se pueden reprogramar citas en estado PROGRAMADA");
        }

        LocalDateTime nuevaFechaHora = request.getFechaHora();
        if (nuevaFechaHora.isBefore(LocalDateTime.now())) {
            throw new BusinessException("No se puede reprogramar una cita al pasado");
        }

        // Validar disponibilidad considerando la duración de la cita (excluyendo la cita actual)
        LocalDateTime fechaHoraActual = cita.getFechaHora();
        if (!validarDisponibilidadExcluyendo(cita.getVeterinario().getIdUsuario(), nuevaFechaHora, fechaHoraActual)) {
            throw new BusinessException("El veterinario ya tiene una cita programada en ese horario. Por favor, seleccione otra fecha y hora");
        }

        cita.setFechaHora(nuevaFechaHora);
        Cita actualizada = citaRepository.save(cita);
        
        // Enviar notificación de reprogramación al cliente
        enviarNotificacionCitaReprogramada(actualizada, fechaHoraActual);
        
        return mapToResponse(actualizada);
    }

    /**
     * Cancela una cita agregando el motivo y cambiando su estado.
     */
    @Transactional
    public CitaResponse cancelar(Long citaId, CitaCancelarRequest request) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", citaId));

        if (AppConstants.ESTADO_CITA_REALIZADA.equals(cita.getEstado())) {
            throw new BusinessException("No se puede cancelar una cita ya realizada");
        }

        cita.setEstado(AppConstants.ESTADO_CITA_CANCELADA);
        cita.setMotivo((cita.getMotivo() != null ? cita.getMotivo() + "\n" : "") +
                "Cancelada: " + request.getMotivo());

        Cita cancelada = citaRepository.save(cita);
        return mapToResponse(cancelada);
    }

    /**
     * Marca la cita como realizada cuando el servicio concluye.
     */
    @Transactional
    public CitaResponse completar(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", citaId));

        if (!AppConstants.ESTADO_CITA_PROGRAMADA.equals(cita.getEstado())) {
            throw new BusinessException("Solo se pueden completar citas en estado PROGRAMADA");
        }

        cita.setEstado(AppConstants.ESTADO_CITA_REALIZADA);
        Cita completada = citaRepository.save(cita);
        return mapToResponse(completada);
    }

    /**
     * Obtiene todas las citas devolviéndolas en formato DTO.
     */
    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerTodas() {
        return citaRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Obtiene todas las citas de un paciente devolviéndolas en formato DTO.
     */
    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Obtiene todas las citas de un veterinario devolviéndolas en formato DTO.
     */
    @Transactional(readOnly = true)
    public List<CitaResponse> obtenerPorVeterinario(Long veterinarioId) {
        return citaRepository.findByVeterinarioId(veterinarioId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Verifica si una fecha y hora está disponible para un veterinario.
     * Considera la duración estándar de las citas.
     * 
     * @param veterinarioId ID del veterinario
     * @param fechaHora Fecha y hora a verificar
     * @return true si está disponible, false si hay conflicto
     */
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(Long veterinarioId, LocalDateTime fechaHora) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            return false;
        }
        return validarDisponibilidad(veterinarioId, fechaHora);
    }

    /**
     * Valida la disponibilidad de un veterinario en una fecha/hora específica.
     * Considera un rango de tiempo basado en la duración estándar de las citas.
     * 
     * @param veterinarioId ID del veterinario
     * @param fechaHora Fecha y hora a validar
     * @return true si está disponible, false si hay conflicto
     */
    private boolean validarDisponibilidad(Long veterinarioId, LocalDateTime fechaHora) {
        LocalDateTime inicioRango = fechaHora.minusMinutes(AppConstants.DURACION_CITA_MINUTOS);
        LocalDateTime finRango = fechaHora.plusMinutes(AppConstants.DURACION_CITA_MINUTOS);
        
        return !citaRepository.existeCitaEnRango(
                veterinarioId,
                inicioRango,
                finRango,
                AppConstants.ESTADO_CITA_PROGRAMADA);
    }

    /**
     * Valida la disponibilidad excluyendo una cita específica (útil para reprogramar).
     * 
     * @param veterinarioId ID del veterinario
     * @param nuevaFechaHora Nueva fecha y hora a validar
     * @param fechaHoraExcluir Fecha y hora de la cita a excluir de la validación
     * @return true si está disponible, false si hay conflicto
     */
    private boolean validarDisponibilidadExcluyendo(Long veterinarioId, LocalDateTime nuevaFechaHora, LocalDateTime fechaHoraExcluir) {
        // Si la nueva fecha es la misma que la actual, está disponible
        if (nuevaFechaHora.equals(fechaHoraExcluir)) {
            return true;
        }
        
        // Verificar si hay otras citas en el rango (excluyendo la cita actual)
        LocalDateTime inicioRango = nuevaFechaHora.minusMinutes(AppConstants.DURACION_CITA_MINUTOS);
        LocalDateTime finRango = nuevaFechaHora.plusMinutes(AppConstants.DURACION_CITA_MINUTOS);
        
        // Obtener todas las citas en el rango
        List<Cita> citasEnRango = citaRepository.findByVeterinarioId(veterinarioId)
                .stream()
                .filter(c -> AppConstants.ESTADO_CITA_PROGRAMADA.equals(c.getEstado()))
                .filter(c -> {
                    LocalDateTime cFecha = c.getFechaHora();
                    return cFecha.isAfter(inicioRango) && cFecha.isBefore(finRango);
                })
                .filter(c -> !c.getFechaHora().equals(fechaHoraExcluir)) // Excluir la cita actual
                .toList();
        
        return citasEnRango.isEmpty();
    }

    private CitaResponse mapToResponse(Cita cita) {
        Paciente paciente = cita.getPaciente();
        Cliente propietario = paciente != null ? paciente.getCliente() : null;
        UsuarioVeterinario veterinario = cita.getVeterinario();

        return CitaResponse.builder()
                .idCita(cita.getIdCita())
                .fechaHora(cita.getFechaHora())
                .estado(cita.getEstado())
                .tipoServicio(cita.getTipoServicio())
                .motivo(cita.getMotivo())
                .triageNivel(cita.getTriageNivel())
                .paciente(CitaResponse.PacienteSummary.builder()
                        .id(paciente != null ? paciente.getIdPaciente() : null)
                        .nombre(paciente != null ? paciente.getNombre() : null)
                        .especie(paciente != null ? paciente.getEspecie() : null)
                        .propietario(propietario != null ? propietario.getNombre() + " " + propietario.getApellido() : null)
                        .build())
                .veterinario(veterinario != null ? CitaResponse.VeterinarioSummary.builder()
                        .id(veterinario.getIdUsuario())
                        .nombreCompleto(veterinario.getNombre() + " " + veterinario.getApellido())
                        .especialidad(veterinario.getEspecialidad())
                        .build() : null)
                .build();
    }
    
    /**
     * Envía notificación por email al cliente cuando se programa una cita.
     * 
     * @param cita La cita que se acaba de crear
     */
    private void enviarNotificacionCitaCreada(Cita cita) {
        try {
            Paciente paciente = cita.getPaciente();
            Cliente cliente = paciente != null ? paciente.getCliente() : null;
            
            if (cliente == null || cliente.getCorreo() == null || cliente.getCorreo().isEmpty()) {
                return; // No enviar si no hay cliente o email
            }
            
            UsuarioVeterinario veterinario = cita.getVeterinario();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaFormateada = cita.getFechaHora().format(formatter);
            
            String mensaje = String.format(
                "🐾 *Confirmación de Cita - Clínica Veterinaria Humboldt*\n\n" +
                "Estimado/a %s %s,\n\n" +
                "Su cita ha sido programada exitosamente:\n\n" +
                "📅 *Fecha y Hora:* %s\n" +
                "🐕 *Paciente:* %s (%s)\n" +
                "👨‍⚕️ *Veterinario:* Dr. %s %s\n" +
                "🏥 *Tipo de Servicio:* %s\n" +
                "%s\n\n" +
                "Por favor, llegue 10 minutos antes de su cita.\n\n" +
                "Si necesita cancelar o reprogramar, contáctenos con al menos 24 horas de anticipación.\n\n" +
                "Saludos cordiales,\n" +
                "Clínica Veterinaria Humboldt",
                cliente.getNombre(),
                cliente.getApellido(),
                fechaFormateada,
                paciente.getNombre(),
                paciente.getEspecie(),
                veterinario.getNombre(),
                veterinario.getApellido(),
                cita.getTipoServicio() != null ? cita.getTipoServicio() : "Consulta General",
                cita.getMotivo() != null ? "*Motivo:* " + cita.getMotivo() : ""
            );
            
            Map<String, Object> datos = new HashMap<>();
            datos.put("destinatario", cliente.getCorreo());
            datos.put("nombreCliente", cliente.getNombre() + " " + cliente.getApellido());
            datos.put("nombrePaciente", paciente.getNombre());
            datos.put("fechaCita", fechaFormateada);
            
            NotificacionEnviarRequest notifRequest = new NotificacionEnviarRequest();
            notifRequest.setTipo("CITA_PROGRAMADA");
            notifRequest.setMensaje(mensaje);
            notifRequest.setCanalId(1L); // ID del canal EMAIL (debe existir en la BD)
            notifRequest.setDatos(datos);
            
            notificacionService.enviarAhora(notifRequest);
            
        } catch (Exception e) {
            // Log del error pero no falla la creación de la cita
            System.err.println("⚠️ Error al enviar notificación de cita: " + e.getMessage());
        }
    }
    
    /**
     * Envía notificación por email al cliente cuando se reprograma una cita.
     * 
     * @param cita La cita que se acaba de reprogramar
     * @param fechaAnterior La fecha anterior de la cita
     */
    private void enviarNotificacionCitaReprogramada(Cita cita, LocalDateTime fechaAnterior) {
        try {
            Paciente paciente = cita.getPaciente();
            Cliente cliente = paciente != null ? paciente.getCliente() : null;
            
            if (cliente == null || cliente.getCorreo() == null || cliente.getCorreo().isEmpty()) {
                return;
            }
            
            UsuarioVeterinario veterinario = cita.getVeterinario();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaNueva = cita.getFechaHora().format(formatter);
            String fechaVieja = fechaAnterior.format(formatter);
            
            String mensaje = String.format(
                "🔄 *Cita Reprogramada - Clínica Veterinaria Humboldt*\n\n" +
                "Estimado/a %s %s,\n\n" +
                "Su cita ha sido reprogramada:\n\n" +
                "❌ *Fecha Anterior:* %s\n" +
                "✅ *Nueva Fecha:* %s\n\n" +
                "🐕 *Paciente:* %s (%s)\n" +
                "👨‍⚕️ *Veterinario:* Dr. %s %s\n\n" +
                "Por favor, tome nota del nuevo horario.\n\n" +
                "Saludos cordiales,\n" +
                "Clínica Veterinaria Humboldt",
                cliente.getNombre(),
                cliente.getApellido(),
                fechaVieja,
                fechaNueva,
                paciente.getNombre(),
                paciente.getEspecie(),
                veterinario.getNombre(),
                veterinario.getApellido()
            );
            
            Map<String, Object> datos = new HashMap<>();
            datos.put("destinatario", cliente.getCorreo());
            datos.put("nombreCliente", cliente.getNombre() + " " + cliente.getApellido());
            datos.put("fechaNueva", fechaNueva);
            datos.put("fechaAnterior", fechaVieja);
            
            NotificacionEnviarRequest notifRequest = new NotificacionEnviarRequest();
            notifRequest.setTipo("CITA_REPROGRAMADA");
            notifRequest.setMensaje(mensaje);
            notifRequest.setCanalId(1L);
            notifRequest.setDatos(datos);
            
            notificacionService.enviarAhora(notifRequest);
            
        } catch (Exception e) {
            System.err.println("⚠️ Error al enviar notificación de reprogramación: " + e.getMessage());
        }
    }
}


