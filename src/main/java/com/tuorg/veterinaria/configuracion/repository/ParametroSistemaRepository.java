package com.tuorg.veterinaria.configuracion.repository;

import com.tuorg.veterinaria.configuracion.model.ParametroSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad ParametroSistema.
 * 
 * Proporciona métodos de acceso a datos para los parámetros del sistema
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface ParametroSistemaRepository extends JpaRepository<ParametroSistema, Long> {

    /**
     * Busca un parámetro por su clave única.
     * 
     * @param clave Clave del parámetro a buscar
     * @return Optional con el parámetro si existe, vacío en caso contrario
     */
    Optional<ParametroSistema> findByClave(String clave);

    /**
     * Verifica si existe un parámetro con la clave especificada.
     * 
     * @param clave Clave del parámetro a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByClave(String clave);
}

