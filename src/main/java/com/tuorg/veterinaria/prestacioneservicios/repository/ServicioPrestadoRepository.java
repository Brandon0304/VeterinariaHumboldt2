package com.tuorg.veterinaria.prestacioneservicios.repository;

import com.tuorg.veterinaria.prestacioneservicios.model.ServicioPrestado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad ServicioPrestado.
 * 
 * Proporciona métodos de acceso a datos para servicios prestados
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface ServicioPrestadoRepository extends JpaRepository<ServicioPrestado, Long> {

    /**
     * Busca servicios prestados por cita.
     * 
     * @param citaId ID de la cita
     * @return Lista de servicios prestados de la cita
     */
    @Query("SELECT sp FROM ServicioPrestado sp WHERE sp.cita.idCita = :citaId")
    List<ServicioPrestado> findByCitaId(@Param("citaId") Long citaId);
}

