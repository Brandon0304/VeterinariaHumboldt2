package com.tuorg.veterinaria.gestionusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad que representa un cliente del sistema.
 * 
 * Esta clase extiende de Usuario y agrega información específica
 * de los clientes que son dueños de mascotas.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "clientes", schema = "public")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Usuario {

    /**
     * Fecha y hora de registro del cliente en el sistema.
     * Incluye información de zona horaria.
     */
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    /**
     * Número de documento de identidad del cliente.
     */
    @Column(name = "documento_identidad", length = 50)
    private String documentoIdentidad;
}

