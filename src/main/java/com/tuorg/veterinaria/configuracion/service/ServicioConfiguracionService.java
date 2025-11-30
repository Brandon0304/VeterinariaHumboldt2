package com.tuorg.veterinaria.configuracion.service;

import com.tuorg.veterinaria.configuracion.model.ServicioConfiguracion;
import com.tuorg.veterinaria.configuracion.repository.ServicioConfiguracionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio para gestión de catálogo de servicios veterinarios.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServicioConfiguracionService {

    private final ServicioConfiguracionRepository servicioConfiguracionRepository;

    /**
     * Obtiene todos los servicios activos (cacheado).
     */
    @Cacheable(value = "serviciosActivos")
    @Transactional(readOnly = true)
    public List<ServicioConfiguracion> obtenerServiciosActivos() {
        log.debug("Obteniendo servicios activos");
        return servicioConfiguracionRepository.findByActivoTrue();
    }

    /**
     * Busca servicios por nombre.
     */
    @Transactional(readOnly = true)
    public Page<ServicioConfiguracion> buscarPorNombre(String nombre, Pageable pageable) {
        log.debug("Buscando servicios por nombre: {}", nombre);
        return servicioConfiguracionRepository.searchByNombre(nombre, pageable);
    }

    /**
     * Obtiene servicios por categoría.
     */
    @Cacheable(value = "serviciosPorCategoria", key = "#categoria")
    @Transactional(readOnly = true)
    public List<ServicioConfiguracion> obtenerPorCategoria(String categoria) {
        log.debug("Obteniendo servicios de categoría: {}", categoria);
        return servicioConfiguracionRepository.findByCategoriaAndActivoTrue(categoria);
    }

    /**
     * Obtiene servicios dentro de un rango de precio.
     */
    @Transactional(readOnly = true)
    public List<ServicioConfiguracion> obtenerPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        log.debug("Obteniendo servicios entre {} y {}", precioMin, precioMax);
        return servicioConfiguracionRepository.findByPrecioBetweenAndActivoTrue(precioMin, precioMax);
    }

    /**
     * Obtiene todas las categorías distintas.
     */
    @Cacheable(value = "categorias")
    @Transactional(readOnly = true)
    public List<String> obtenerCategorias() {
        return servicioConfiguracionRepository.findDistinctCategorias();
    }

    /**
     * Obtiene un servicio por ID.
     */
    @Transactional(readOnly = true)
    public ServicioConfiguracion obtenerPorId(Long id) {
        return servicioConfiguracionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado: " + id));
    }

    /**
     * Crea o actualiza un servicio.
     */
    @CacheEvict(value = {"serviciosActivos", "serviciosPorCategoria", "categorias"}, allEntries = true)
    @Transactional
    public ServicioConfiguracion guardarServicio(ServicioConfiguracion servicio) {
        log.info("Guardando servicio: {}", servicio.getNombreServicio());
        
        // Validar precio positivo
        if (servicio.getPrecio() != null && servicio.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        
        return servicioConfiguracionRepository.save(servicio);
    }

    /**
     * Desactiva un servicio (soft delete).
     */
    @CacheEvict(value = {"serviciosActivos", "serviciosPorCategoria"}, allEntries = true)
    @Transactional
    public void desactivarServicio(Long id) {
        log.info("Desactivando servicio: {}", id);
        ServicioConfiguracion servicio = obtenerPorId(id);
        servicio.setActivo(false);
        servicioConfiguracionRepository.save(servicio);
    }

    /**
     * Actualiza el precio de un servicio.
     */
    @CacheEvict(value = {"serviciosActivos", "serviciosPorCategoria"}, allEntries = true)
    @Transactional
    public ServicioConfiguracion actualizarPrecio(Long id, BigDecimal nuevoPrecio) {
        log.info("Actualizando precio del servicio {}: {}", id, nuevoPrecio);
        
        if (nuevoPrecio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        
        ServicioConfiguracion servicio = obtenerPorId(id);
        servicio.setPrecio(nuevoPrecio);
        return servicioConfiguracionRepository.save(servicio);
    }

    /**
     * Cuenta servicios activos.
     */
    @Transactional(readOnly = true)
    public long contarServiciosActivos() {
        return servicioConfiguracionRepository.countByActivoTrue();
    }
}
