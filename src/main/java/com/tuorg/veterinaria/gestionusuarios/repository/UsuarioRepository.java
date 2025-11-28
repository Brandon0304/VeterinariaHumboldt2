package com.tuorg.veterinaria.gestionusuarios.repository;

import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Busca un usuario por su nombre de usuario cargando el rol (fetch join).
     * 
     * @param username Nombre de usuario
     * @return Optional con el usuario si existe, vacío en caso contrario
     */
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.rol WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithRol(@Param("username") String username);

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

    /**
     * Verifica si existe un usuario con el correo especificado excluyendo un ID.
     * 
     * @param correo Correo electrónico
     * @param idPersona ID de la persona (usuario) a excluir
     * @return true si existe, false en caso contrario
     */
    boolean existsByCorreoAndIdPersonaNot(String correo, Long idPersona);

    /**
     * Busca un usuario por su token de recuperación de contraseña.
     * 
     * @param token Token de recuperación
     * @return Optional con el usuario si existe, vacío en caso contrario
     */
    Optional<Usuario> findByPasswordResetToken(String token);
}

