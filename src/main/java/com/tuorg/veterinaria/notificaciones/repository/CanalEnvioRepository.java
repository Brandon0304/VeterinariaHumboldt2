package com.tuorg.veterinaria.notificaciones.repository;

import com.tuorg.veterinaria.notificaciones.model.CanalEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad CanalEnvio.
 * 
 * Proporciona métodos de acceso a datos para canales de envío
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface CanalEnvioRepository extends JpaRepository<CanalEnvio, Long> {
    // Métodos adicionales pueden agregarse aquí según sea necesario
}

