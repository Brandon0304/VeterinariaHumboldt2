package com.tuorg.veterinaria.citas.event;

import org.springframework.context.ApplicationEvent;

/**
 * Evento disparado cuando se crea una nueva cita.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public class CitaCreatedEvent extends ApplicationEvent {
    
    private final Long idCita;
    private final String telefonoCliente;
    private final String nombrePaciente;
    private final String fechaHora;

    public CitaCreatedEvent(Object source, Long idCita, String telefonoCliente, String nombrePaciente, String fechaHora) {
        super(source);
        this.idCita = idCita;
        this.telefonoCliente = telefonoCliente;
        this.nombrePaciente = nombrePaciente;
        this.fechaHora = fechaHora;
    }

    public Long getIdCita() {
        return idCita;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public String getFechaHora() {
        return fechaHora;
    }
}
