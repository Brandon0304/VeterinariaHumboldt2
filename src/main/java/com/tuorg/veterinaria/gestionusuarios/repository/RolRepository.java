package com.tuorg.veterinaria.gestionusuarios.repository;

import com.tuorg.veterinaria.gestionusuarios.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Rol.
 *
 * Proporciona métodos de acceso a datos para roles
 * utilizando Spring Data JPA.
 *
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol por su nombre.
     *
     * @param nombreRol Nombre del rol
     * @return Optional con el rol si existe, vacío en caso contrario
     */
    Optional<Rol> findByNombreRol(String nombreRol);
}
