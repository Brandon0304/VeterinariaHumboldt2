package com.tuorg.veterinaria.configuracion.repository;

import com.tuorg.veterinaria.configuracion.model.PermisoRol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para PermisoRol (RBAC - Role-Based Access Control).
 */
@Repository
public interface PermisoRolRepository extends JpaRepository<PermisoRol, Long> {

    /**
     * Obtiene todos los permisos activos de un rol.
     * 
     * @param rolId ID del rol
     * @return Lista de permisos activos del rol
     */
    @Query("SELECT pr FROM PermisoRol pr WHERE pr.rol.idRol = :rolId AND pr.activo = true")
    List<PermisoRol> findByRolIdAndActivoTrue(@Param("rolId") Long rolId);

    /**
     * Obtiene permisos de un rol para un módulo específico.
     * 
     * @param rolId ID del rol
     * @param modulo Nombre del módulo
     * @return Lista de permisos del rol en ese módulo
     */
    @Query("SELECT pr FROM PermisoRol pr WHERE pr.rol.idRol = :rolId AND pr.modulo = :modulo AND pr.activo = true")
    List<PermisoRol> findByRolIdAndModulo(@Param("rolId") Long rolId, @Param("modulo") String modulo);

    /**
     * Verifica si un rol tiene un permiso específico.
     * 
     * @param rolId ID del rol
     * @param modulo Nombre del módulo
     * @param accion Acción a verificar
     * @return true si el rol tiene ese permiso activo
     */
    @Query("SELECT COUNT(pr) > 0 FROM PermisoRol pr WHERE pr.rol.idRol = :rolId AND pr.modulo = :modulo AND pr.accion = :accion AND pr.activo = true")
    boolean existsByRolIdAndModuloAndAccionAndActivoTrue(
        @Param("rolId") Long rolId, 
        @Param("modulo") String modulo, 
        @Param("accion") String accion
    );

    /**
     * Obtiene un permiso específico por rol, módulo y acción.
     * 
     * @param rolId ID del rol
     * @param modulo Nombre del módulo
     * @param accion Nombre de la acción
     * @return Permiso encontrado
     */
    @Query("SELECT pr FROM PermisoRol pr WHERE pr.rol.idRol = :rolId AND pr.modulo = :modulo AND pr.accion = :accion")
    Optional<PermisoRol> findByRolIdAndModuloAndAccion(
        @Param("rolId") Long rolId, 
        @Param("modulo") String modulo, 
        @Param("accion") String accion
    );

    /**
     * Obtiene todos los permisos activos.
     * 
     * @return Lista de todos los permisos activos
     */
    List<PermisoRol> findByActivoTrue();

    /**
     * Obtiene permisos de un módulo específico (todos los roles).
     * 
     * @param modulo Nombre del módulo
     * @return Lista de permisos activos de ese módulo
     */
    List<PermisoRol> findByModuloAndActivoTrue(@Param("modulo") String modulo);

    /**
     * Busca permisos con filtros opcionales.
     * 
     * @param idRol ID del rol (opcional)
     * @param modulo Nombre del módulo (opcional)
     * @param accion Nombre de la acción (opcional)
     * @param pageable Configuración de paginación
     * @return Página de permisos que coinciden con los filtros
     */
    @Query("SELECT pr FROM PermisoRol pr WHERE " +
           "(:idRol IS NULL OR pr.rol.idRol = :idRol) AND " +
           "(:modulo IS NULL OR pr.modulo = :modulo) AND " +
           "(:accion IS NULL OR pr.accion = :accion) AND " +
           "pr.activo = true " +
           "ORDER BY pr.rol.idRol, pr.modulo, pr.accion")
    Page<PermisoRol> findByFiltros(
        @Param("idRol") Long idRol,
        @Param("modulo") String modulo,
        @Param("accion") String accion,
        Pageable pageable
    );

    /**
     * Obtiene todos los nombres de módulos distintos.
     * 
     * @return Lista de nombres de módulos únicos
     */
    @Query("SELECT DISTINCT pr.modulo FROM PermisoRol pr WHERE pr.activo = true ORDER BY pr.modulo")
    List<String> findDistinctModulos();

    /**
     * Obtiene todos los nombres de acciones distintas.
     * 
     * @return Lista de nombres de acciones únicas
     */
    @Query("SELECT DISTINCT pr.accion FROM PermisoRol pr WHERE pr.activo = true ORDER BY pr.accion")
    List<String> findDistinctAcciones();
}
