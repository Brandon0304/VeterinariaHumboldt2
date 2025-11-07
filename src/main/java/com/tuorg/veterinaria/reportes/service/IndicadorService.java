package com.tuorg.veterinaria.reportes.service;

import com.tuorg.veterinaria.reportes.model.Indicador;
import com.tuorg.veterinaria.reportes.repository.IndicadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para la gestión de indicadores.
 * 
 * Este servicio proporciona métodos para evaluar tendencias
 * y gestionar indicadores clave de rendimiento (KPIs).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class IndicadorService {

    /**
     * Repositorio de indicadores.
     */
    private final IndicadorRepository indicadorRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param indicadorRepository Repositorio de indicadores
     */
    @Autowired
    public IndicadorService(IndicadorRepository indicadorRepository) {
        this.indicadorRepository = indicadorRepository;
    }

    /**
     * Evalúa la tendencia de un indicador.
     * 
     * @param indicadorId ID del indicador
     * @return String con la tendencia (creciente, decreciente, estable)
     */
    @Transactional(readOnly = true)
    public String evaluarTendencia(Long indicadorId) {
        Indicador indicador = indicadorRepository.findById(indicadorId)
                .orElseThrow(() -> new RuntimeException("Indicador no encontrado"));
        
        // TODO: Implementar evaluación real de tendencia comparando valores históricos
        // Por ahora retornamos "estable"
        return "estable";
    }

    /**
     * Obtiene todos los indicadores.
     * 
     * @return Lista de indicadores
     */
    @Transactional(readOnly = true)
    public List<Indicador> obtenerTodos() {
        return indicadorRepository.findAll();
    }
}

