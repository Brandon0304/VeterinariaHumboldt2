package com.tuorg.veterinaria.reportes.repository;

import com.tuorg.veterinaria.reportes.model.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Estadistica.
 * 
 * Proporciona métodos de acceso a datos para estadísticas
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {

    /**
     * Busca estadísticas por nombre.
     * 
     * @param nombre Nombre de la estadística
     * @return Lista de estadísticas con el nombre especificado
     */
    List<Estadistica> findByNombre(String nombre);
}

