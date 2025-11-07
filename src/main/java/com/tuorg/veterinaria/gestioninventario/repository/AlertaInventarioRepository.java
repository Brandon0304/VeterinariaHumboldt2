package com.tuorg.veterinaria.gestioninventario.repository;

import com.tuorg.veterinaria.gestioninventario.model.AlertaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad AlertaInventario.
 * 
 * Proporciona métodos de acceso a datos para alertas de inventario
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface AlertaInventarioRepository extends JpaRepository<AlertaInventario, Long> {

    /**
     * Busca alertas por producto.
     * 
     * @param productoId ID del producto
     * @return Lista de alertas del producto
     */
    @Query("SELECT ai FROM AlertaInventario ai WHERE ai.producto.idProducto = :productoId")
    List<AlertaInventario> findByProductoId(@Param("productoId") Long productoId);
}

