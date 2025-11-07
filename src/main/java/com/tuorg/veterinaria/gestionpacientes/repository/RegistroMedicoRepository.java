package com.tuorg.veterinaria.gestionpacientes.repository;

import com.tuorg.veterinaria.gestionpacientes.model.RegistroMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad RegistroMedico.
 * 
 * Proporciona métodos de acceso a datos para registros médicos
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface RegistroMedicoRepository extends JpaRepository<RegistroMedico, Long> {

    /**
     * Busca registros médicos por historia clínica.
     * 
     * @param historiaId ID de la historia clínica
     * @return Lista de registros médicos de la historia
     */
    @Query("SELECT rm FROM RegistroMedico rm WHERE rm.historia.idHistoria = :historiaId")
    List<RegistroMedico> findByHistoriaId(@Param("historiaId") Long historiaId);
}
