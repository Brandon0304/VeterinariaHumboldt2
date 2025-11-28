package com.tuorg.veterinaria.gestionpacientes.repository;

import com.tuorg.veterinaria.gestionpacientes.model.Vacunacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Vacunacion.
 * 
 * Proporciona métodos de acceso a datos para vacunaciones
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface VacunacionRepository extends JpaRepository<Vacunacion, Long> {

    /**
     * Busca vacunaciones por paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Lista de vacunaciones del paciente
     */
    @Query("SELECT v FROM Vacunacion v WHERE v.paciente.idPaciente = :pacienteId")
    List<Vacunacion> findByPacienteId(@Param("pacienteId") Long pacienteId);

    /**
     * Busca vacunaciones pendientes (próxima dosis próxima a vencer).
     * 
     * @param fechaLimite Fecha límite para considerar pendiente
     * @return Lista de vacunaciones con próxima dosis antes de la fecha límite
     */
    @Query("SELECT v FROM Vacunacion v WHERE v.proximaDosis IS NOT NULL AND v.proximaDosis <= :fechaLimite")
    List<Vacunacion> findVacunacionesPendientes(@Param("fechaLimite") LocalDate fechaLimite);
}
