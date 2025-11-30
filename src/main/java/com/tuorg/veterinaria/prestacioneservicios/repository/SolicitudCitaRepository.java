package com.tuorg.veterinaria.prestacioneservicios.repository;

import com.tuorg.veterinaria.prestacioneservicios.model.SolicitudCita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad SolicitudCita.
 */
@Repository
public interface SolicitudCitaRepository extends JpaRepository<SolicitudCita, Long> {

    /**
     * Busca solicitudes de cita por cliente.
     */
    @Query("SELECT s FROM SolicitudCita s WHERE s.cliente.idPersona = :clienteId")
    List<SolicitudCita> findByClienteId(@Param("clienteId") Long clienteId);

    /**
     * Busca solicitudes de cita por cliente con paginación.
     */
    @Query("SELECT s FROM SolicitudCita s WHERE s.cliente.idPersona = :clienteId")
    Page<SolicitudCita> findByClienteId(@Param("clienteId") Long clienteId, Pageable pageable);

    /**
     * Busca solicitudes de cita por paciente.
     */
    @Query("SELECT s FROM SolicitudCita s WHERE s.paciente.idPaciente = :pacienteId")
    List<SolicitudCita> findByPacienteId(@Param("pacienteId") Long pacienteId);

    /**
     * Busca solicitudes por estado.
     */
    List<SolicitudCita> findByEstado(String estado);

    /**
     * Busca solicitudes pendientes con paginación.
     */
    Page<SolicitudCita> findByEstado(String estado, Pageable pageable);

    /**
     * Cuenta solicitudes pendientes.
     */
    long countByEstado(String estado);
}
