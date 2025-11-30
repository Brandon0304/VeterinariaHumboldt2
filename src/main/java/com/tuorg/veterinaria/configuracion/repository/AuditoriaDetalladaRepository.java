package com.tuorg.veterinaria.configuracion.repository;

import com.tuorg.veterinaria.configuracion.model.AuditoriaDetallada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para AuditoriaDetallada (patrón Memento).
 * 
 * Permite consultas avanzadas de auditoría y análisis forense.
 */
@Repository
public interface AuditoriaDetalladaRepository extends JpaRepository<AuditoriaDetallada, Long> {

    /**
     * Busca auditorías con filtros avanzados.
     * 
     * @param entidad Nombre de la entidad (opcional)
     * @param entidadId ID del registro (opcional)
     * @param modulo Módulo del sistema (opcional)
     * @param usuarioId ID del usuario (opcional)
     * @param fechaDesde Fecha desde (opcional)
     * @param fechaHasta Fecha hasta (opcional)
     * @param pageable Configuración de paginación
     * @return Página de auditorías que cumplen los criterios
     */
    @Query("SELECT ad FROM AuditoriaDetallada ad WHERE " +
           "(:entidad IS NULL OR ad.entidad = :entidad) AND " +
           "(:entidadId IS NULL OR ad.entidadId = :entidadId) AND " +
           "(:modulo IS NULL OR ad.modulo = :modulo) AND " +
           "(:usuarioId IS NULL OR ad.usuario.id = :usuarioId) AND " +
           "(:fechaDesde IS NULL OR ad.fechaAccion >= :fechaDesde) AND " +
           "(:fechaHasta IS NULL OR ad.fechaAccion <= :fechaHasta) " +
           "ORDER BY ad.fechaAccion DESC")
    Page<AuditoriaDetallada> findByFiltros(
        @Param("entidad") String entidad,
        @Param("entidadId") Long entidadId,
        @Param("modulo") String modulo,
        @Param("usuarioId") Long usuarioId,
        @Param("fechaDesde") LocalDateTime fechaDesde,
        @Param("fechaHasta") LocalDateTime fechaHasta,
        Pageable pageable
    );

    /**
     * Obtiene historial de una entidad ordenado por fecha.
     * 
     * @param entidad Nombre de la entidad
     * @param entidadId ID del registro
     * @return Lista de auditorías ordenadas por fecha descendente
     */
    @Query("SELECT ad FROM AuditoriaDetallada ad WHERE ad.entidad = :entidad AND ad.entidadId = :entidadId ORDER BY ad.fechaAccion DESC")
    List<AuditoriaDetallada> findByEntidadAndEntidadIdOrderByFechaAccionDesc(
        @Param("entidad") String entidad,
        @Param("entidadId") Long entidadId
    );

    /**
     * Obtiene auditorías de un usuario ordenadas por fecha.
     * 
     * @param usuarioId ID del usuario
     * @param pageable Configuración de paginación
     * @return Página de auditorías del usuario
     */
    @Query("SELECT ad FROM AuditoriaDetallada ad WHERE ad.usuario.id = :usuarioId ORDER BY ad.fechaAccion DESC")
    Page<AuditoriaDetallada> findByUsuarioIdOrderByFechaAccionDesc(
        @Param("usuarioId") Long usuarioId,
        Pageable pageable
    );

    /**
     * Obtiene las N auditorías más recientes.
     * 
     * @param limite Número máximo de registros
     * @return Lista de auditorías recientes
     */
    @Query(value = "SELECT * FROM auditoria_detallada ORDER BY fecha_accion DESC LIMIT :limite", nativeQuery = true)
    List<AuditoriaDetallada> findRecientes(@Param("limite") int limite);

    /**
     * Cuenta auditorías en un rango de fechas.
     * 
     * @param fechaDesde Fecha desde
     * @param fechaHasta Fecha hasta
     * @return Número de auditorías
     */
    @Query("SELECT COUNT(ad) FROM AuditoriaDetallada ad WHERE ad.fechaAccion BETWEEN :fechaDesde AND :fechaHasta")
    long countByFechaAccionBetween(
        @Param("fechaDesde") LocalDateTime fechaDesde,
        @Param("fechaHasta") LocalDateTime fechaHasta
    );
}
