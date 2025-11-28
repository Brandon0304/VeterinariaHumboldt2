package com.tuorg.veterinaria.gestionpacientes.repository;

import com.tuorg.veterinaria.gestionpacientes.model.Desparasitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Desparasitacion.
 * 
 * Proporciona métodos de acceso a datos para desparasitaciones
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface DesparasitacionRepository extends JpaRepository<Desparasitacion, Long> {

    /**
     * Busca todas las desparasitaciones de un paciente.
     * 
     * @param pacienteId ID del paciente
     * @return Lista de desparasitaciones
     */
    List<Desparasitacion> findByPacienteIdPaciente(Long pacienteId);

    /**
     * Busca desparasitaciones pendientes (con próxima aplicación en el rango de fechas).
     * 
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de desparasitaciones pendientes
     */
    List<Desparasitacion> findByProximaAplicacionBetween(LocalDate fechaInicio, LocalDate fechaFin);
}

