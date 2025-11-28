package com.tuorg.veterinaria.gestionusuarios.repository;

import com.tuorg.veterinaria.gestionusuarios.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}


