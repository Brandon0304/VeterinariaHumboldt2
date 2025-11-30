package com.tuorg.veterinaria.gestionpacientes.repository;

import com.tuorg.veterinaria.gestionpacientes.model.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad HistoriaClinica.
 * 
 * Proporciona métodos de acceso a datos para historias clínicas
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

    /**
     * Busca la historia clínica de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Optional con la historia clínica si existe
     */
    @Query("SELECT h FROM HistoriaClinica h WHERE h.paciente.idPaciente = :pacienteId")
    Optional<HistoriaClinica> findByPacienteId(@Param("pacienteId") Long pacienteId);
}

