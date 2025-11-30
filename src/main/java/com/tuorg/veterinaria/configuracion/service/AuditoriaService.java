package com.tuorg.veterinaria.configuracion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuorg.veterinaria.configuracion.model.AuditoriaDetallada;
import com.tuorg.veterinaria.configuracion.repository.AuditoriaDetalladaRepository;
import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de auditoría detallada.
 * Implementa patrón Memento para capturar estados antes/después de operaciones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaDetalladaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    /**
     * Registra una operación de auditoría con patrón Memento.
     * 
     * @param modulo Módulo del sistema
     * @param entidad Nombre de la entidad auditada
     * @param entidadId ID de la entidad
     * @param rolNombre Nombre del rol del usuario
     * @param datosAntes Estado anterior (null para CREATE)
     * @param datosDespues Estado posterior (null para DELETE)
     */
    @Transactional
    public AuditoriaDetallada registrarOperacion(
            String modulo,
            String entidad,
            Long entidadId,
            String rolNombre,
            Object datosAntes,
            Object datosDespues) {
        
        log.info("Registrando auditoría en módulo {} para {}.{}", modulo, entidad, entidadId);
        
        AuditoriaDetallada auditoria = new AuditoriaDetallada();
        auditoria.setModulo(modulo);
        auditoria.setEntidad(entidad);
        auditoria.setEntidadId(entidadId);
        auditoria.setRolNombre(rolNombre);
        auditoria.setFechaAccion(LocalDateTime.now());
        
        // Serializar datos (patrón Memento)
        try {
            if (datosAntes != null) {
                auditoria.setDatosAnteriores(objectMapper.writeValueAsString(datosAntes));
            }
            if (datosDespues != null) {
                auditoria.setDatosNuevos(objectMapper.writeValueAsString(datosDespues));
            }
        } catch (Exception e) {
            log.error("Error serializando datos de auditoría", e);
            throw new RuntimeException("Error al registrar auditoría", e);
        }
        
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                    .orElse(null);
            auditoria.setUsuario(usuario);
        }
        
        // Obtener IP del usuario (podría venir de un contexto HTTP)
        auditoria.setIpAddress(obtenerIpUsuario());
        
        return auditoriaRepository.save(auditoria);
    }

    /**
     * Busca auditorías con paginación.
     */
    @Transactional(readOnly = true)
    public Page<AuditoriaDetallada> buscarAuditorias(
            String entidad,
            Long entidadId,
            String modulo,
            Long usuarioId,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Pageable pageable) {
        
        log.debug("Buscando auditorías: entidad={}, modulo={}, usuario={}", 
                entidad, modulo, usuarioId);
        
        return auditoriaRepository.findByFiltros(
                entidad, entidadId, modulo, usuarioId, fechaDesde, fechaHasta, pageable
        );
    }

    /**
     * Obtiene el historial de una entidad específica.
     */
    @Transactional(readOnly = true)
    public List<AuditoriaDetallada> obtenerHistorial(String entidad, Long entidadId) {
        log.debug("Obteniendo historial de {}.{}", entidad, entidadId);
        return auditoriaRepository.findByEntidadAndEntidadIdOrderByFechaAccionDesc(entidad, entidadId);
    }

    /**
     * Obtiene auditorías de un usuario.
     */
    @Transactional(readOnly = true)
    public Page<AuditoriaDetallada> obtenerPorUsuario(Long usuarioId, Pageable pageable) {
        return auditoriaRepository.findByUsuarioIdOrderByFechaAccionDesc(usuarioId, pageable);
    }

    /**
     * Obtiene auditorías recientes.
     */
    @Transactional(readOnly = true)
    public List<AuditoriaDetallada> obtenerRecientes(int limite) {
        return auditoriaRepository.findRecientes(limite);
    }

    /**
     * Obtiene estadísticas de auditoría.
     */
    @Transactional(readOnly = true)
    public long contarOperaciones(LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return auditoriaRepository.countByFechaAccionBetween(fechaDesde, fechaHasta);
    }

    /**
     * Obtiene IP del usuario (placeholder - implementar según contexto HTTP).
     */
    private String obtenerIpUsuario() {
        // TODO: Implementar obtención de IP desde HttpServletRequest
        return "0.0.0.0";
    }
}
