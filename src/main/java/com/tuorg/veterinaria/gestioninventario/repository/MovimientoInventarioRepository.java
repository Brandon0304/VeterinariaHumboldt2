package com.tuorg.veterinaria.gestioninventario.repository;

import com.tuorg.veterinaria.gestioninventario.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad MovimientoInventario.
 * 
 * Proporciona métodos de acceso a datos para movimientos de inventario
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /**
     * Busca movimientos por producto.
     * 
     * @param productoId ID del producto
     * @return Lista de movimientos del producto
     */
    @Query("SELECT mi FROM MovimientoInventario mi WHERE mi.producto.idProducto = :productoId")
    List<MovimientoInventario> findByProductoId(@Param("productoId") Long productoId);

    /**
     * Busca movimientos por tipo.
     * 
     * @param tipoMovimiento Tipo de movimiento (IN, OUT, AJUSTE)
     * @return Lista de movimientos del tipo especificado
     */
    List<MovimientoInventario> findByTipoMovimiento(String tipoMovimiento);

    /**
     * Busca movimientos en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de movimientos en el rango especificado
     */
    @Query("SELECT m FROM MovimientoInventario m WHERE m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<MovimientoInventario> findByFechaBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Verifica si existe un movimiento con una referencia específica.
     * 
     * @param referencia Referencia del movimiento
     * @return true si existe, false en caso contrario
     */
    boolean existsByReferencia(String referencia);
}

