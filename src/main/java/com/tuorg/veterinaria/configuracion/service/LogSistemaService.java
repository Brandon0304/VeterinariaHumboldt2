package com.tuorg.veterinaria.configuracion.service;

import com.tuorg.veterinaria.configuracion.model.LogSistema;
import com.tuorg.veterinaria.configuracion.repository.LogSistemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de logs del sistema.
 * 
 * Este servicio proporciona métodos para registrar y consultar
 * eventos del sistema para auditoría y seguimiento.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class LogSistemaService {

    private static final Logger logger = LoggerFactory.getLogger(LogSistemaService.class);

    /**
     * Repositorio de logs del sistema.
     */
    private final LogSistemaRepository logSistemaRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param logSistemaRepository Repositorio de logs
     */
    @Autowired
    public LogSistemaService(LogSistemaRepository logSistemaRepository) {
        this.logSistemaRepository = logSistemaRepository;
    }

    /**
     * Registra un evento en el sistema.
     * 
     * @param componente Componente que genera el log
     * @param nivel Nivel del log (INFO, WARN, ERROR, DEBUG)
     * @param mensaje Mensaje descriptivo del evento
     * @return LogSistema creado
     */
    @Transactional
    public LogSistema registrarEvento(String componente, String nivel, String mensaje) {
        LogSistema logSistema = new LogSistema();
        logSistema.setFechaHora(LocalDateTime.now());
        logSistema.setComponente(componente);
        logSistema.setNivel(nivel);
        logSistema.setMensaje(mensaje);

        LogSistema saved = logSistemaRepository.save(logSistema);
        logger.debug("Evento registrado: {} - {} - {}", componente, nivel, mensaje);
        return saved;
    }

    /**
     * Obtiene todos los logs del sistema.
     * 
     * @return Lista de todos los logs
     */
    @Transactional(readOnly = true)
    public List<LogSistema> obtenerTodos() {
        return logSistemaRepository.findAll();
    }

    /**
     * Obtiene logs por nivel.
     * 
     * @param nivel Nivel del log (INFO, WARN, ERROR, DEBUG)
     * @return Lista de logs con el nivel especificado
     */
    @Transactional(readOnly = true)
    public List<LogSistema> obtenerPorNivel(String nivel) {
        return logSistemaRepository.findByNivel(nivel);
    }

    /**
     * Obtiene logs por componente.
     * 
     * @param componente Nombre del componente
     * @return Lista de logs del componente especificado
     */
    @Transactional(readOnly = true)
    public List<LogSistema> obtenerPorComponente(String componente) {
        return logSistemaRepository.findByComponente(componente);
    }

    /**
     * Obtiene logs en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de logs en el rango especificado
     */
    @Transactional(readOnly = true)
    public List<LogSistema> obtenerPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return logSistemaRepository.findByFechaHoraBetween(fechaInicio, fechaFin);
    }
}

