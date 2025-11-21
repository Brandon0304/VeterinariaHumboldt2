package com.tuorg.veterinaria.gestionusuarios.repository;

import com.tuorg.veterinaria.gestionusuarios.model.HistorialAccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad HistorialAccion.
 *
 * Proporciona métodos de acceso a datos para el historial de acciones
 * utilizando Spring Data JPA.
 *
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface HistorialAccionRepository extends JpaRepository<HistorialAccion, Long> {

    /**
     * Busca acciones por usuario.
     *
     * @param usuarioId ID del usuario
     * @return Lista de acciones del usuario
     */
    @Query("SELECT h FROM HistorialAccion h WHERE h.usuario.idPersona = :usuarioId")
    List<HistorialAccion> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Busca acciones en un rango de fechas.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de acciones en el rango especificado
     */
    @Query("SELECT h FROM HistorialAccion h WHERE h.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fechaHora DESC")
    List<HistorialAccion> findByFechaHoraBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}
