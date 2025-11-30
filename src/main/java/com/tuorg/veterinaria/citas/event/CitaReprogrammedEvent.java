package com.tuorg.veterinaria.citas.event;

import org.springframework.context.ApplicationEvent;

/**
 * Evento disparado cuando se reprograma una cita existente.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
public class CitaReprogrammedEvent extends ApplicationEvent {
    
    private final Long idCita;
    private final String telefonoCliente;
    private final String nombrePaciente;
    private final String fechaAnterior;
    private final String fechaNueva;

    public CitaReprogrammedEvent(Object source, Long idCita, String telefonoCliente, String nombrePaciente, 
                                  String fechaAnterior, String fechaNueva) {
        super(source);
        this.idCita = idCita;
        this.telefonoCliente = telefonoCliente;
        this.nombrePaciente = nombrePaciente;
        this.fechaAnterior = fechaAnterior;
        this.fechaNueva = fechaNueva;
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

    public String getFechaAnterior() {
        return fechaAnterior;
    }

    public String getFechaNueva() {
        return fechaNueva;
    }
}
