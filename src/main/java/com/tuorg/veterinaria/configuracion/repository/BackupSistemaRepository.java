package com.tuorg.veterinaria.configuracion.repository;

import com.tuorg.veterinaria.configuracion.model.BackupSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad BackupSistema.
 * 
 * Proporciona métodos de acceso a datos para los backups del sistema
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface BackupSistemaRepository extends JpaRepository<BackupSistema, Long> {
    // Métodos adicionales pueden agregarse aquí según sea necesario
}

