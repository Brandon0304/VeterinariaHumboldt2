package com.tuorg.veterinaria.configuracion.repository;

import com.tuorg.veterinaria.configuracion.model.ConfiguracionAvanzada;
import com.tuorg.veterinaria.configuracion.model.ConfiguracionAvanzada.TipoDato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para ConfiguracionAvanzada (parámetros del sistema).
 */
@Repository
public interface ConfiguracionAvanzadaRepository extends JpaRepository<ConfiguracionAvanzada, Long> {

    /**
     * Obtiene una configuración por su clave.
     * 
     * @param clave Clave de la configuración
     * @return Configuración encontrada
     */
    Optional<ConfiguracionAvanzada> findByClave(String clave);

    /**
     * Obtiene todas las configuraciones activas.
     * 
     * @return Lista de configuraciones activas
     */
    List<ConfiguracionAvanzada> findByActivoTrueOrderByCategoria();

    /**
     * Obtiene configuraciones por categoría.
     * 
     * @param categoria Categoría de configuración
     * @return Lista de configuraciones de esa categoría
     */
    @Query("SELECT ca FROM ConfiguracionAvanzada ca WHERE ca.categoria = :categoria AND ca.activo = true ORDER BY ca.clave")
    List<ConfiguracionAvanzada> findByCategoriaAndActivoTrue(@Param("categoria") String categoria);

    /**
     * Obtiene configuraciones editables.
     * 
     * @return Lista de configuraciones que pueden editarse desde la UI
     */
    @Query("SELECT ca FROM ConfiguracionAvanzada ca WHERE ca.editable = true AND ca.activo = true ORDER BY ca.categoria, ca.clave")
    List<ConfiguracionAvanzada> findEditables();

    /**
     * Obtiene todas las categorías distintas.
     * 
     * @return Lista de categorías únicas
     */
    @Query("SELECT DISTINCT ca.categoria FROM ConfiguracionAvanzada ca WHERE ca.activo = true AND ca.categoria IS NOT NULL ORDER BY ca.categoria")
    List<String> findDistinctCategorias();

    /**
     * Obtiene configuraciones por tipo de dato.
     * 
     * @param tipoDato Tipo de dato
     * @return Lista de configuraciones de ese tipo
     */
    List<ConfiguracionAvanzada> findByTipoDatoAndActivoTrue(TipoDato tipoDato);

    /**
     * Verifica si existe una clave de configuración.
     * 
     * @param clave Clave a verificar
     * @return true si existe
     */
    boolean existsByClave(String clave);

    /**
     * Obtiene todas las configuraciones activas.
     * 
     * @return Lista de configuraciones activas
     */
    List<ConfiguracionAvanzada> findByActivoTrue();

    /**
     * Busca configuraciones con filtros y paginación.
     * 
     * @param clave Clave (opcional)
     * @param tipoDato Tipo de dato (opcional)
     * @param editable Si es editable (opcional)
     * @param pageable Configuración de paginación
     * @return Página de configuraciones
     */
    @Query("SELECT ca FROM ConfiguracionAvanzada ca WHERE " +
           "(:clave IS NULL OR ca.clave LIKE %:clave%) AND " +
           "(:tipoDato IS NULL OR ca.tipoDato = :tipoDato) AND " +
           "(:editable IS NULL OR ca.editable = :editable) AND " +
           "ca.activo = true ORDER BY ca.categoria, ca.clave")
    Page<ConfiguracionAvanzada> findByFiltros(
        @Param("clave") String clave,
        @Param("tipoDato") TipoDato tipoDato,
        @Param("editable") Boolean editable,
        Pageable pageable
    );
}
