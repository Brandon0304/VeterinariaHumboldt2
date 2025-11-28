package com.tuorg.veterinaria.configuracion.repository;

import com.tuorg.veterinaria.configuracion.model.LogSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad LogSistema.
 * 
 * Proporciona métodos de acceso a datos para los logs del sistema
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface LogSistemaRepository extends JpaRepository<LogSistema, Long> {

    /**
     * Busca logs por nivel.
     * 
     * @param nivel Nivel del log (INFO, WARN, ERROR, etc.)
     * @return Lista de logs con el nivel especificado
     */
    List<LogSistema> findByNivel(String nivel);

    /**
     * Busca logs por componente.
     * 
     * @param componente Nombre del componente
     * @return Lista de logs del componente especificado
     */
    List<LogSistema> findByComponente(String componente);

    /**
     * Busca logs en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de logs en el rango especificado
     */
    @Query("SELECT l FROM LogSistema l WHERE l.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY l.fechaHora DESC")
    List<LogSistema> findByFechaHoraBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}

