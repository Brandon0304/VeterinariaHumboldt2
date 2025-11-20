package com.tuorg.veterinaria.gestionusuarios.repository;

import com.tuorg.veterinaria.gestionusuarios.model.UsuarioVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad UsuarioVeterinario.
 * 
 * Proporciona métodos de acceso a datos para veterinarios
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface UsuarioVeterinarioRepository extends JpaRepository<UsuarioVeterinario, Long> {

    /**
     * Obtiene todos los veterinarios activos.
     * 
     * @return Lista de veterinarios activos
     */
    @Query("SELECT v FROM UsuarioVeterinario v WHERE v.activo = true ORDER BY v.nombre, v.apellido")
    List<UsuarioVeterinario> findAllActivos();
}

