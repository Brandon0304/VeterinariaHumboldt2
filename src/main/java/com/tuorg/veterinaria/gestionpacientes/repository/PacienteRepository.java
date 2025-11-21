package com.tuorg.veterinaria.gestionpacientes.repository;

import com.tuorg.veterinaria.gestionpacientes.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Paciente.
 * 
 * Proporciona métodos de acceso a datos para pacientes
 * utilizando Spring Data JPA.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * Busca pacientes por cliente (dueño).
     * 
     * @param clienteId ID del cliente
     * @return Lista de pacientes del cliente
     */
    @Query("SELECT p FROM Paciente p WHERE p.cliente.idPersona = :clienteId")
    List<Paciente> findByClienteId(@Param("clienteId") Long clienteId);

    /**
     * Busca pacientes por especie.
     * 
     * @param especie Especie del paciente (perro, gato)
     * @return Lista de pacientes de la especie especificada
     */
    List<Paciente> findByEspecie(String especie);

    /**
     * Busca pacientes por nombre (búsqueda parcial, case-insensitive).
     * 
     * @param nombre Nombre o parte del nombre del paciente
     * @return Lista de pacientes que coinciden con el nombre
     */
    @Query("SELECT p FROM Paciente p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Paciente> buscarPorNombre(@Param("nombre") String nombre);
}

