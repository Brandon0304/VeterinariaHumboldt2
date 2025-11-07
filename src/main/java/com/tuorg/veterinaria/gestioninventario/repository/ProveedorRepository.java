package com.tuorg.veterinaria.gestioninventario.repository;

import com.tuorg.veterinaria.gestioninventario.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Proveedor.
 * 
 * Proporciona métodos de acceso a datos para proveedores
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    // Métodos adicionales pueden agregarse aquí según sea necesario
}

