package com.tuorg.veterinaria.gestionusuarios.repository;

import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 *
 * Proporciona métodos de acceso a datos para usuarios
 * utilizando Spring Data JPA.
 *
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario
     * @return Optional con el usuario si existe, vacío en caso contrario
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     *
     * @param username Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param correo Correo electrónico
     * @return Optional con el usuario si existe, vacío en caso contrario
     */
    Optional<Usuario> findByCorreo(String correo);

    /**
     * Verifica si existe un usuario con el correo especificado.
     *
     * @param correo Correo electrónico
     * @return true si existe, false en caso contrario
     */
    boolean existsByCorreo(String correo);
}
