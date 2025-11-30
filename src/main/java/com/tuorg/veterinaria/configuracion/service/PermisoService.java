package com.tuorg.veterinaria.configuracion.service;

import com.tuorg.veterinaria.configuracion.model.PermisoRol;
import com.tuorg.veterinaria.configuracion.repository.PermisoRolRepository;
import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestión de permisos RBAC (Role-Based Access Control).
 * Valida permisos dinámicamente según rol, módulo y acción.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermisoService {

    private final PermisoRolRepository permisoRolRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Valida si un rol tiene permiso para realizar una acción en un módulo.
     */
    @Cacheable(value = "permisos", key = "#idRol + '_' + #modulo + '_' + #accion")
    @Transactional(readOnly = true)
    public boolean tienePermiso(Long idRol, String modulo, String accion) {
        log.debug("Validando permiso: rol={}, modulo={}, accion={}", idRol, modulo, accion);
        return permisoRolRepository.existsByRolIdAndModuloAndAccionAndActivoTrue(idRol, modulo, accion);
    }

    /**
     * Valida si el usuario autenticado tiene permiso para una acción.
     */
    @Transactional(readOnly = true)
    public boolean usuarioTienePermiso(String modulo, String accion) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("Usuario no autenticado intentando acceder a {}.{}", modulo, accion);
            return false;
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + userDetails.getUsername()));
        
        return tienePermiso(usuario.getRol().getIdRol(), modulo, accion);
    }

    /**
     * Obtiene todos los permisos de un rol.
     */
    @Cacheable(value = "permisosPorRol", key = "#idRol")
    @Transactional(readOnly = true)
    public List<PermisoRol> obtenerPermisosPorRol(Long idRol) {
        log.debug("Obteniendo permisos para rol: {}", idRol);
        return permisoRolRepository.findByRolIdAndActivoTrue(idRol);
    }

    /**
     * Obtiene permisos por módulo.
     */
    @Transactional(readOnly = true)
    public List<PermisoRol> obtenerPermisosPorModulo(String modulo) {
        return permisoRolRepository.findByModuloAndActivoTrue(modulo);
    }

    /**
     * Busca permisos con paginación.
     */
    @Transactional(readOnly = true)
    public Page<PermisoRol> buscarPermisos(Long idRol, String modulo, String accion, Pageable pageable) {
        log.debug("Buscando permisos: rol={}, modulo={}, accion={}", idRol, modulo, accion);
        return permisoRolRepository.findByFiltros(idRol, modulo, accion, pageable);
    }

    /**
     * Crea o actualiza un permiso.
     */
    @CacheEvict(value = {"permisos", "permisosPorRol"}, allEntries = true)
    @Transactional
    public PermisoRol guardarPermiso(PermisoRol permiso) {
        log.info("Guardando permiso: rol={}, modulo={}, accion={}", 
                permiso.getRol().getIdRol(), permiso.getModulo(), permiso.getAccion());
        
        // Validar duplicados
        if (permiso.getIdPermiso() == null) {
            boolean existe = permisoRolRepository.existsByRolIdAndModuloAndAccionAndActivoTrue(
                    permiso.getRol().getIdRol(), permiso.getModulo(), permiso.getAccion()
            );
            if (existe) {
                throw new IllegalArgumentException(
                        "Ya existe un permiso activo para este rol, módulo y acción"
                );
            }
        }
        
        return permisoRolRepository.save(permiso);
    }

    /**
     * Desactiva un permiso (soft delete).
     */
    @CacheEvict(value = {"permisos", "permisosPorRol"}, allEntries = true)
    @Transactional
    public void desactivarPermiso(Long idPermiso) {
        log.info("Desactivando permiso: {}", idPermiso);
        PermisoRol permiso = permisoRolRepository.findById(idPermiso)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado: " + idPermiso));
        
        permiso.setActivo(false);
        permisoRolRepository.save(permiso);
    }

    /**
     * Obtiene todos los módulos distintos.
     */
    @Cacheable(value = "modulos")
    @Transactional(readOnly = true)
    public List<String> obtenerModulos() {
        return permisoRolRepository.findDistinctModulos();
    }

    /**
     * Obtiene todas las acciones distintas.
     */
    @Cacheable(value = "acciones")
    @Transactional(readOnly = true)
    public List<String> obtenerAcciones() {
        return permisoRolRepository.findDistinctAcciones();
    }
}
