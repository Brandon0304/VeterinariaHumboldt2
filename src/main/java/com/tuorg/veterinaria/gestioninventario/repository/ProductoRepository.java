package com.tuorg.veterinaria.gestioninventario.repository;

import com.tuorg.veterinaria.gestioninventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Producto.
 * 
 * Proporciona métodos de acceso a datos para productos
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca un producto por su SKU.
     * 
     * @param sku SKU del producto
     * @return Optional con el producto si existe
     */
    Optional<Producto> findBySku(String sku);

    /**
     * Verifica si existe un producto con el SKU especificado.
     * 
     * @param sku SKU del producto
     * @return true si existe, false en caso contrario
     */
    boolean existsBySku(String sku);

    /**
     * Busca productos por tipo.
     * 
     * @param tipo Tipo de producto
     * @return Lista de productos del tipo especificado
     */
    List<Producto> findByTipo(String tipo);

    /**
     * Busca productos con stock bajo (menor o igual al nivel especificado).
     * 
     * @param nivelStock Nivel de stock mínimo
     * @return Lista de productos con stock bajo
     */
    @Query("SELECT p FROM Producto p WHERE p.stock <= :nivelStock")
    List<Producto> findProductosConStockBajo(@Param("nivelStock") Integer nivelStock);
}

