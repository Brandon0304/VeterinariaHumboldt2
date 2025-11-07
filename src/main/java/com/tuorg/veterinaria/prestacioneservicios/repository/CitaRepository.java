package com.tuorg.veterinaria.prestacioneservicios.repository;

import com.tuorg.veterinaria.prestacioneservicios.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Cita.
 * 
 * Proporciona métodos de acceso a datos para citas
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Busca citas por paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Lista de citas del paciente
     */
    @Query("SELECT c FROM Cita c WHERE c.paciente.idPaciente = :pacienteId")
    List<Cita> findByPacienteId(@Param("pacienteId") Long pacienteId);

    /**
     * Busca citas por veterinario.
     * 
     * @param veterinarioId ID del veterinario
     * @return Lista de citas del veterinario
     */
    @Query("SELECT c FROM Cita c WHERE c.veterinario.idPersona = :veterinarioId")
    List<Cita> findByVeterinarioId(@Param("veterinarioId") Long veterinarioId);

    /**
     * Busca citas por estado.
     * 
     * @param estado Estado de la cita
     * @return Lista de citas con el estado especificado
     */
    List<Cita> findByEstado(String estado);

    /**
     * Verifica si existe una cita programada para un veterinario en una fecha/hora específica.
     * 
     * @param veterinarioId ID del veterinario
     * @param fechaHora Fecha y hora de la cita
     * @param estado Estado de la cita (solo PROGRAMADA)
     * @return true si existe conflicto, false en caso contrario
     */
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE c.veterinario.idPersona = :veterinarioId " +
           "AND c.fechaHora = :fechaHora AND c.estado = :estado")
    boolean existeCitaEnFechaHora(@Param("veterinarioId") Long veterinarioId,
                                   @Param("fechaHora") LocalDateTime fechaHora,
                                   @Param("estado") String estado);
}

