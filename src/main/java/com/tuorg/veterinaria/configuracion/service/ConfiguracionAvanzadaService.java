package com.tuorg.veterinaria.configuracion.service;

import com.tuorg.veterinaria.configuracion.model.ConfiguracionAvanzada;
import com.tuorg.veterinaria.configuracion.repository.ConfiguracionAvanzadaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestión de configuración avanzada del sistema (key-value store).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguracionAvanzadaService {

    private final ConfiguracionAvanzadaRepository configuracionRepository;

    /**
     * Obtiene el valor de una configuración por clave (cacheado).
     */
    @Cacheable(value = "configuracion", key = "#clave")
    @Transactional(readOnly = true)
    public String obtenerValor(String clave) {
        log.debug("Obteniendo configuración: {}", clave);
        return configuracionRepository.findByClave(clave)
                .map(ConfiguracionAvanzada::getValor)
                .orElse(null);
    }

    /**
     * Obtiene el valor tipado de una configuración.
     */
    @Transactional(readOnly = true)
    public <T> T obtenerValorTipado(String clave, Class<T> tipo) {
        ConfiguracionAvanzada config = configuracionRepository.findByClave(clave)
                .orElse(null);
        
        if (config == null) {
            return null;
        }
        
        return convertirValor(config.getValor(), config.getTipoDato(), tipo);
    }

    /**
     * Obtiene una configuración completa por clave.
     */
    @Transactional(readOnly = true)
    public ConfiguracionAvanzada obtenerConfiguracion(String clave) {
        return configuracionRepository.findByClave(clave)
                .orElseThrow(() -> new IllegalArgumentException("Configuración no encontrada: " + clave));
    }

    /**
     * Obtiene todas las configuraciones activas.
     */
    @Cacheable(value = "configuracionesActivas")
    @Transactional(readOnly = true)
    public List<ConfiguracionAvanzada> obtenerActivas() {
        return configuracionRepository.findByActivoTrue();
    }

    /**
     * Obtiene configuraciones editables.
     */
    @Transactional(readOnly = true)
    public List<ConfiguracionAvanzada> obtenerEditables() {
        return configuracionRepository.findEditables();
    }

    /**
     * Busca configuraciones con paginación.
     */
    @Transactional(readOnly = true)
    public Page<ConfiguracionAvanzada> buscarConfiguraciones(
            String clave,
            ConfiguracionAvanzada.TipoDato tipoDato,
            Boolean editable,
            Pageable pageable) {
        
        log.debug("Buscando configuraciones: clave={}, tipo={}", clave, tipoDato);
        return configuracionRepository.findByFiltros(clave, tipoDato, editable, pageable);
    }

    /**
     * Actualiza el valor de una configuración.
     */
    @CacheEvict(value = {"configuracion", "configuracionesActivas"}, allEntries = true)
    @Transactional
    public ConfiguracionAvanzada actualizarValor(String clave, String nuevoValor) {
        log.info("Actualizando configuración: {} = {}", clave, nuevoValor);
        
        ConfiguracionAvanzada config = obtenerConfiguracion(clave);
        
        if (!config.getEditable()) {
            throw new IllegalStateException("La configuración '" + clave + "' no es editable");
        }
        
        // Validar tipo de dato
        validarValor(nuevoValor, config.getTipoDato());
        
        config.setValor(nuevoValor);
        return configuracionRepository.save(config);
    }

    /**
     * Crea una nueva configuración.
     */
    @CacheEvict(value = {"configuracion", "configuracionesActivas"}, allEntries = true)
    @Transactional
    public ConfiguracionAvanzada crearConfiguracion(ConfiguracionAvanzada config) {
        log.info("Creando nueva configuración: {}", config.getClave());
        
        // Validar que no exista
        if (configuracionRepository.findByClave(config.getClave()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una configuración con clave: " + config.getClave());
        }
        
        // Validar valor según tipo
        validarValor(config.getValor(), config.getTipoDato());
        
        return configuracionRepository.save(config);
    }

    /**
     * Desactiva una configuración (soft delete).
     */
    @CacheEvict(value = {"configuracion", "configuracionesActivas"}, allEntries = true)
    @Transactional
    public void desactivarConfiguracion(String clave) {
        log.info("Desactivando configuración: {}", clave);
        
        ConfiguracionAvanzada config = obtenerConfiguracion(clave);
        config.setActivo(false);
        configuracionRepository.save(config);
    }

    /**
     * Restaura valores por defecto.
     */
    @CacheEvict(value = {"configuracion", "configuracionesActivas"}, allEntries = true)
    @Transactional
    public void restaurarDefaults() {
        log.warn("Restaurando configuraciones a valores por defecto");
        // TODO: Implementar restauración de valores default desde un archivo de configuración
    }

    /**
     * Valida que el valor sea compatible con el tipo de dato.
     */
    private void validarValor(String valor, ConfiguracionAvanzada.TipoDato tipoDato) {
        try {
            switch (tipoDato) {
                case INTEGER -> Integer.parseInt(valor);
                case DECIMAL -> Double.parseDouble(valor);
                case BOOLEAN -> {
                    if (!valor.equalsIgnoreCase("true") && !valor.equalsIgnoreCase("false")) {
                        throw new IllegalArgumentException("Valor booleano inválido");
                    }
                }
                case STRING, JSON -> { /* Siempre válidos */ }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Valor '" + valor + "' no es compatible con tipo " + tipoDato, e
            );
        }
    }

    /**
     * Convierte un valor string al tipo especificado.
     */
    @SuppressWarnings("unchecked")
    private <T> T convertirValor(String valor, ConfiguracionAvanzada.TipoDato tipoDato, Class<T> tipo) {
        try {
            return (T) switch (tipoDato) {
                case INTEGER -> Integer.parseInt(valor);
                case DECIMAL -> Double.parseDouble(valor);
                case BOOLEAN -> Boolean.parseBoolean(valor);
                case STRING, JSON -> valor;
                default -> {
                    log.warn("Tipo de dato no reconocido: {}, retornando valor como String", tipoDato);
                    yield valor;
                }
            };
        } catch (Exception e) {
            log.error("Error convirtiendo valor {} a tipo {}", valor, tipo, e);
            return null;
        }
    }
}
