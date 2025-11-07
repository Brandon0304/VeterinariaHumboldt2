package com.tuorg.veterinaria.reportes.repository;

import com.tuorg.veterinaria.reportes.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Reporte.
 * 
 * Proporciona métodos de acceso a datos para reportes
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    // Métodos adicionales pueden agregarse aquí según sea necesario
}

