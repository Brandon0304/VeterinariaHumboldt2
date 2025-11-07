package com.tuorg.veterinaria.prestacioneservicios.repository;

import com.tuorg.veterinaria.prestacioneservicios.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Servicio.
 * 
 * Proporciona métodos de acceso a datos para servicios
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    /**
     * Busca servicios por tipo.
     * 
     * @param tipo Tipo de servicio
     * @return Lista de servicios del tipo especificado
     */
    List<Servicio> findByTipo(String tipo);
}

