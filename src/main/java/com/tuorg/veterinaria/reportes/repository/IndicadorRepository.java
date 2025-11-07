package com.tuorg.veterinaria.reportes.repository;

import com.tuorg.veterinaria.reportes.model.Indicador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Indicador.
 * 
 * Proporciona métodos de acceso a datos para indicadores
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface IndicadorRepository extends JpaRepository<Indicador, Long> {
    // Métodos adicionales pueden agregarse aquí según sea necesario
}

