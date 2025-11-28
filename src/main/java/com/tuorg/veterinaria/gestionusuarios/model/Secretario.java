package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un secretario del sistema.
 * 
 * Esta clase extiende de Usuario y agrega información específica
 * de los secretarios que gestionan citas e inventario.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "secretarios", schema = "public")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Secretario extends Usuario {

    /**
     * Número de extensión telefónica del secretario.
     */
    @Column(name = "extension", length = 20)
    private String extension;
}

