package com.tuorg.veterinaria.configuracion.service;

import com.tuorg.veterinaria.configuracion.model.HorarioAtencion;
import com.tuorg.veterinaria.configuracion.repository.HorarioAtencionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Servicio para gestión de horarios de atención de la clínica.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HorarioAtencionService {

    private final HorarioAtencionRepository horarioAtencionRepository;

    /**
     * Obtiene todos los horarios activos (cacheado).
     */
    @Cacheable(value = "horariosActivos")
    @Transactional(readOnly = true)
    public List<HorarioAtencion> obtenerHorariosActivos() {
        log.debug("Obteniendo horarios activos");
        return horarioAtencionRepository.findByActivoTrue();
    }

    /**
     * Obtiene el horario de un día específico.
     */
    @Cacheable(value = "horarioPorDia", key = "#dia.name()")
    @Transactional(readOnly = true)
    public HorarioAtencion obtenerPorDia(HorarioAtencion.DiaSemana dia) {
        return horarioAtencionRepository.findByDiaSemanaAndActivoTrue(dia)
                .orElse(null);
    }

    /**
     * Obtiene los días en que la clínica está abierta.
     */
    @Cacheable(value = "diasAbiertos")
    @Transactional(readOnly = true)
    public List<HorarioAtencion> obtenerDiasAbiertos() {
        return horarioAtencionRepository.findDiasAbiertos();
    }

    /**
     * Verifica si la clínica está abierta en un día y hora específicos.
     */
    @Transactional(readOnly = true)
    public boolean estaAbierta(HorarioAtencion.DiaSemana dia, LocalTime hora) {
        log.debug("Verificando si clínica está abierta: {} a las {}", dia, hora);
        return horarioAtencionRepository.isClinicaAbierta(dia);
    }

    /**
     * Verifica si la clínica está abierta ahora.
     */
    @Transactional(readOnly = true)
    public boolean estaAbiertaAhora() {
        LocalDate hoy = LocalDate.now();
        DayOfWeek dayOfWeek = hoy.getDayOfWeek();
        HorarioAtencion.DiaSemana dia = convertirDayOfWeek(dayOfWeek);
        LocalTime ahora = LocalTime.now();
        
        return estaAbierta(dia, ahora);
    }

    /**
     * Crea o actualiza un horario.
     */
    @CacheEvict(value = {"horariosActivos", "horarioPorDia", "diasAbiertos"}, allEntries = true)
    @Transactional
    public HorarioAtencion guardarHorario(HorarioAtencion horario) {
        log.info("Guardando horario para {}", horario.getDiaSemana());
        
        // Validar que hora apertura sea antes que hora cierre
        if (!horario.getCerrado() && horario.getHoraApertura() != null && horario.getHoraCierre() != null) {
            if (horario.getHoraApertura().isAfter(horario.getHoraCierre())) {
                throw new IllegalArgumentException(
                        "La hora de apertura debe ser anterior a la hora de cierre"
                );
            }
        }
        
        // Validar que no haya duplicados activos para el mismo día
        if (horario.getIdHorario() == null && horario.getActivo()) {
            boolean existe = horarioAtencionRepository.findByDiaSemanaAndActivoTrue(horario.getDiaSemana()).isPresent();
            if (existe) {
                throw new IllegalArgumentException(
                        "Ya existe un horario activo para " + horario.getDiaSemana()
                );
            }
        }
        
        return horarioAtencionRepository.save(horario);
    }

    /**
     * Desactiva un horario (soft delete).
     */
    @CacheEvict(value = {"horariosActivos", "horarioPorDia", "diasAbiertos"}, allEntries = true)
    @Transactional
    public void desactivarHorario(Long id) {
        log.info("Desactivando horario: {}", id);
        HorarioAtencion horario = horarioAtencionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado: " + id));
        
        horario.setActivo(false);
        horarioAtencionRepository.save(horario);
    }

    /**
     * Actualiza si un día está abierto o cerrado.
     */
    @CacheEvict(value = {"horariosActivos", "horarioPorDia", "diasAbiertos"}, allEntries = true)
    @Transactional
    public HorarioAtencion actualizarEstadoDia(HorarioAtencion.DiaSemana dia, boolean abierto) {
        log.info("Actualizando estado de {}: {}", dia, abierto ? "ABIERTO" : "CERRADO");
        
        HorarioAtencion horario = horarioAtencionRepository.findByDiaSemanaAndActivoTrue(dia)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado para " + dia));
        
        horario.setCerrado(!abierto);
        return horarioAtencionRepository.save(horario);
    }

    /**
     * Convierte DayOfWeek de Java a DiaSemana de la entidad.
     */
    private HorarioAtencion.DiaSemana convertirDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> HorarioAtencion.DiaSemana.LUNES;
            case TUESDAY -> HorarioAtencion.DiaSemana.MARTES;
            case WEDNESDAY -> HorarioAtencion.DiaSemana.MIERCOLES;
            case THURSDAY -> HorarioAtencion.DiaSemana.JUEVES;
            case FRIDAY -> HorarioAtencion.DiaSemana.VIERNES;
            case SATURDAY -> HorarioAtencion.DiaSemana.SABADO;
            case SUNDAY -> HorarioAtencion.DiaSemana.DOMINGO;
        };
    }
}
