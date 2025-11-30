package com.tuorg.veterinaria.configuracion.service;

import com.tuorg.veterinaria.configuracion.model.RespaldoSistema;
import com.tuorg.veterinaria.configuracion.repository.RespaldoSistemaRepository;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

/**
 * Servicio para gestión de respaldos del sistema.
 * Implementa patrón Memento para backup/restore de datos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RespaldoService {

    private final RespaldoSistemaRepository respaldoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Crea un nuevo respaldo (patrón Memento).
     */
    @Transactional
    public RespaldoSistema crearRespaldo(
            RespaldoSistema.TipoRespaldo tipo,
            String ruta,
            Long tamanioBytes,
            String descripcion) {
        
        log.info("Creando respaldo: tipo={}, ruta={}", tipo, ruta);
        
        RespaldoSistema respaldo = new RespaldoSistema();
        respaldo.setTipo(tipo);
        respaldo.setRuta(ruta);
        respaldo.setTamanioBytes(tamanioBytes);
        respaldo.setDescripcion(descripcion);
        respaldo.setFechaRespaldo(LocalDateTime.now());
        respaldo.setEstado(RespaldoSistema.EstadoRespaldo.EN_PROCESO);
        
        // Asignar usuario
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                    .orElse(null);
            respaldo.setUsuario(usuario);
        }
        
        return respaldoRepository.save(respaldo);
    }

    /**
     * Marca un respaldo como completado y calcula su hash SHA-256.
     */
    @Transactional
    public RespaldoSistema completarRespaldo(Long idRespaldo, byte[] contenido) {
        log.info("Completando respaldo: {}", idRespaldo);
        
        RespaldoSistema respaldo = respaldoRepository.findById(idRespaldo)
                .orElseThrow(() -> new IllegalArgumentException("Respaldo no encontrado: " + idRespaldo));
        
        // Calcular hash SHA-256
        String hash = calcularSHA256(contenido);
        respaldo.setHashVerificacion(hash);
        respaldo.setEstado(RespaldoSistema.EstadoRespaldo.COMPLETADO);
        
        log.info("Respaldo completado con hash: {}", hash);
        return respaldoRepository.save(respaldo);
    }

    /**
     * Marca un respaldo como fallido.
     */
    @Transactional
    public RespaldoSistema marcarComoFallido(Long idRespaldo, String mensajeError) {
        log.error("Respaldo fallido: {} - {}", idRespaldo, mensajeError);
        
        RespaldoSistema respaldo = respaldoRepository.findById(idRespaldo)
                .orElseThrow(() -> new IllegalArgumentException("Respaldo no encontrado: " + idRespaldo));
        
        respaldo.setEstado(RespaldoSistema.EstadoRespaldo.FALLIDO);
        respaldo.setDescripcion(respaldo.getDescripcion() + " | ERROR: " + mensajeError);
        
        return respaldoRepository.save(respaldo);
    }

    /**
     * Verifica la integridad de un respaldo mediante hash SHA-256.
     */
    @Transactional(readOnly = true)
    public boolean verificarIntegridad(Long idRespaldo, byte[] contenido) {
        log.info("Verificando integridad de respaldo: {}", idRespaldo);
        
        RespaldoSistema respaldo = respaldoRepository.findById(idRespaldo)
                .orElseThrow(() -> new IllegalArgumentException("Respaldo no encontrado: " + idRespaldo));
        
        String hashCalculado = calcularSHA256(contenido);
        boolean integro = hashCalculado.equals(respaldo.getHashVerificacion());
        
        log.info("Verificación de integridad: {} (esperado: {}, calculado: {})",
                integro ? "EXITOSA" : "FALLIDA", respaldo.getHashVerificacion(), hashCalculado);
        
        return integro;
    }

    /**
     * Obtiene el último respaldo exitoso.
     */
    @Transactional(readOnly = true)
    public RespaldoSistema obtenerUltimoRespaldoExitoso() {
        return respaldoRepository.findUltimoRespaldoExitoso()
                .orElse(null);
    }

    /**
     * Busca respaldos con paginación.
     */
    @Transactional(readOnly = true)
    public Page<RespaldoSistema> buscarRespaldos(
            RespaldoSistema.TipoRespaldo tipo,
            RespaldoSistema.EstadoRespaldo estado,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Pageable pageable) {
        
        log.debug("Buscando respaldos: tipo={}, estado={}", tipo, estado);
        return respaldoRepository.findByFiltros(tipo, estado, fechaDesde, fechaHasta, pageable);
    }

    /**
     * Obtiene respaldos por tipo.
     */
    @Transactional(readOnly = true)
    public List<RespaldoSistema> obtenerPorTipo(RespaldoSistema.TipoRespaldo tipo) {
        return respaldoRepository.findByTipoOrderByFechaRespaldoDesc(tipo);
    }

    /**
     * Cuenta respaldos por estado.
     */
    @Transactional(readOnly = true)
    public long contarPorEstado(RespaldoSistema.EstadoRespaldo estado) {
        return respaldoRepository.countByEstado(estado);
    }

    /**
     * Elimina respaldos antiguos.
     */
    @Transactional
    public void eliminarRespaldosAntiguos(LocalDateTime fechaLimite) {
        log.info("Eliminando respaldos anteriores a {}", fechaLimite);
        respaldoRepository.deleteByFechaRespaldoBefore(fechaLimite);
    }

    /**
     * Calcula hash SHA-256 de un array de bytes.
     */
    private String calcularSHA256(byte[] contenido) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contenido);
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            log.error("Error calculando hash SHA-256", e);
            throw new RuntimeException("Error al calcular hash", e);
        }
    }
}
