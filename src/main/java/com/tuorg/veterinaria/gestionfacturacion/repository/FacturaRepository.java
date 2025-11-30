package com.tuorg.veterinaria.gestionfacturacion.repository;

import com.tuorg.veterinaria.gestionfacturacion.model.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Factura.
 * 
 * Proporciona métodos de acceso a datos para facturas
 * utilizando Spring Data JPA con soporte de paginación.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    /**
     * Busca una factura por su número único.
     * 
     * @param numero Número de la factura
     * @return Optional con la factura si existe
     */
    Optional<Factura> findByNumero(String numero);

    /**
     * Busca facturas por cliente.
     * 
     * @param clienteId ID del cliente
     * @return Lista de facturas del cliente
     */
    @Query("SELECT f FROM Factura f WHERE f.cliente.idPersona = :clienteId")
    List<Factura> findByClienteId(@Param("clienteId") Long clienteId);

    /**
     * Busca facturas por cliente con paginación.
     * 
     * @param clienteId ID del cliente
     * @param pageable Información de paginación
     * @return Página de facturas del cliente
     */
    @Query("SELECT f FROM Factura f WHERE f.cliente.idPersona = :clienteId")
    Page<Factura> findByClienteId(@Param("clienteId") Long clienteId, Pageable pageable);

    /**
     * Busca facturas por estado.
     * 
     * @param estado Estado de la factura
     * @return Lista de facturas con el estado especificado
     */
    List<Factura> findByEstado(String estado);

    /**
     * Busca facturas por estado con paginación.
     * 
     * @param estado Estado de la factura
     * @param pageable Información de paginación
     * @return Página de facturas con el estado especificado
     */
    Page<Factura> findByEstado(String estado, Pageable pageable);
}

