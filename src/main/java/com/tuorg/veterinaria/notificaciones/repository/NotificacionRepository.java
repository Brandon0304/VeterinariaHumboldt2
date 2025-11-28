package com.tuorg.veterinaria.notificaciones.repository;

import com.tuorg.veterinaria.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Notificacion.
 * 
 * Proporciona métodos de acceso a datos para notificaciones
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Busca notificaciones por estado.
     * 
     * @param estado Estado de la notificación
     * @return Lista de notificaciones con el estado especificado
     */
    List<Notificacion> findByEstado(String estado);

    /**
     * Busca notificaciones pendientes programadas para enviar.
     * 
     * @param fechaLimite Fecha límite para considerar pendiente
     * @return Lista de notificaciones pendientes
     */
    @Query("SELECT n FROM Notificacion n WHERE n.estado = 'PENDIENTE' " +
           "AND (n.fechaEnvioProgramada IS NULL OR n.fechaEnvioProgramada <= :fechaLimite)")
    List<Notificacion> findNotificacionesPendientes(@Param("fechaLimite") LocalDateTime fechaLimite);
}

