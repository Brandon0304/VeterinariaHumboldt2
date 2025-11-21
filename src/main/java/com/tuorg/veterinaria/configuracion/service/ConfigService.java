package com.tuorg.veterinaria.configuracion.service;

import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.configuracion.model.ParametroSistema;
import com.tuorg.veterinaria.configuracion.repository.ParametroSistemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para la gestión de parámetros del sistema.
 * 
 * Esta clase implementa el patrón Singleton para proporcionar una única
 * fuente de configuración en tiempo de ejecución con caché local thread-safe.
 * 
 * El servicio mantiene un caché en memoria de los parámetros para mejorar
 * el rendimiento, evitando consultas repetidas a la base de datos.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    /**
     * Instancia única del servicio (Singleton).
     * En Spring, esto se maneja automáticamente con @Service.
     */
    private static ConfigService instance;

    /**
     * Caché local thread-safe para almacenar parámetros en memoria.
     * Utiliza ConcurrentHashMap para garantizar thread-safety.
     */
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    /**
     * Repositorio de parámetros del sistema.
     */
    private final ParametroSistemaRepository parametroSistemaRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param parametroSistemaRepository Repositorio de parámetros
     */
    @Autowired
    public ConfigService(ParametroSistemaRepository parametroSistemaRepository) {
        this.parametroSistemaRepository = parametroSistemaRepository;
        instance = this;
        // Cargar todos los parámetros al inicializar
        cargarTodos();
    }

    /**
     * Obtiene la instancia única del servicio (Singleton).
     * 
     * @return Instancia del ConfigService
     */
    public static ConfigService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConfigService no ha sido inicializado");
        }
        return instance;
    }

    /**
     * Obtiene el valor de un parámetro por su clave.
     * 
     * Primero busca en el caché, si no existe, consulta la base de datos
     * y actualiza el caché.
     * 
     * @param clave Clave del parámetro
     * @return Valor del parámetro
     * @throws ResourceNotFoundException Si el parámetro no existe
     */
    @Transactional(readOnly = true)
    public String obtener(String clave) {
        // Buscar en caché primero
        String valor = cache.get(clave);
        if (valor != null) {
            return valor;
        }

        // Si no está en caché, buscar en base de datos
        ParametroSistema parametro = parametroSistemaRepository.findByClave(clave)
                .orElseThrow(() -> new ResourceNotFoundException("ParametroSistema", "clave", clave));

        // Actualizar caché
        cache.put(clave, parametro.getValor());
        return parametro.getValor();
    }

    /**
     * Actualiza el valor de un parámetro.
     * 
     * Actualiza tanto la base de datos como el caché local.
     * 
     * @param clave Clave del parámetro
     * @param valor Nuevo valor del parámetro
     * @throws ResourceNotFoundException Si el parámetro no existe
     */
    @Transactional
    public void actualizarValor(String clave, String valor) {
        ParametroSistema parametro = parametroSistemaRepository.findByClave(clave)
                .orElseThrow(() -> new ResourceNotFoundException("ParametroSistema", "clave", clave));

        parametro.setValor(valor);
        parametroSistemaRepository.save(parametro);

        // Actualizar caché
        cache.put(clave, valor);
        logger.info("Parámetro actualizado: {} = {}", clave, valor);
    }

    /**
     * Carga todos los parámetros del sistema en el caché.
     * 
     * Este método se ejecuta al inicializar el servicio y puede
     * ser llamado manualmente para refrescar el caché.
     */
    @Transactional(readOnly = true)
    public void cargarTodos() {
        List<ParametroSistema> parametros = parametroSistemaRepository.findAll();
        cache.clear();
        for (ParametroSistema parametro : parametros) {
            cache.put(parametro.getClave(), parametro.getValor());
        }
        logger.info("Caché de parámetros cargado: {} parámetros", parametros.size());
    }

    /**
     * Obtiene todos los parámetros como un Map.
     * 
     * @return Map con todos los parámetros (clave -> valor)
     */
    @Transactional(readOnly = true)
    public Map<String, String> obtenerTodos() {
        // Si el caché está vacío, cargar desde la base de datos
        if (cache.isEmpty()) {
            cargarTodos();
        }
        return new HashMap<>(cache);
    }

    /**
     * Limpia el caché de parámetros.
     * 
     * Útil cuando se necesita forzar la recarga desde la base de datos.
     */
    public void limpiarCache() {
        cache.clear();
        logger.info("Caché de parámetros limpiado");
    }
}


