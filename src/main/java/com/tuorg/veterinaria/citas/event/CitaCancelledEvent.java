package com.tuorg.veterinaria.citas.event;

import org.springframework.context.ApplicationEvent;

/**
 * Evento disparado cuando se cancela una cita.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public class CitaCancelledEvent extends ApplicationEvent {
    
    private final Long idCita;
    private final String telefonoCliente;
    private final String nombrePaciente;
    private final String motivoCancelacion;

    public CitaCancelledEvent(Object source, Long idCita, String telefonoCliente, String nombrePaciente, 
                              String motivoCancelacion) {
        super(source);
        this.idCita = idCita;
        this.telefonoCliente = telefonoCliente;
        this.nombrePaciente = nombrePaciente;
        this.motivoCancelacion = motivoCancelacion;
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

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }
}
