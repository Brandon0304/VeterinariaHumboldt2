package com.tuorg.veterinaria.common.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class CitaReprogrammedEvent extends ApplicationEvent {
    private final Long citaId;
    private final LocalDateTime fechaAnterior;
    private final LocalDateTime fechaNueva;
    private final String clienteEmail;
    private final String clienteTelefono;
    private final String clienteNombre;
    private final String pacienteNombre;
    private final String veterinarioNombre;

    public CitaReprogrammedEvent(Object source, Long citaId, LocalDateTime fechaAnterior, 
                                  LocalDateTime fechaNueva, String clienteEmail, 
                                  String clienteTelefono, String clienteNombre, 
                                  String pacienteNombre, String veterinarioNombre) {
        super(source);
        this.citaId = citaId;
        this.fechaAnterior = fechaAnterior;
        this.fechaNueva = fechaNueva;
        this.clienteEmail = clienteEmail;
        this.clienteTelefono = clienteTelefono;
        this.clienteNombre = clienteNombre;
        this.pacienteNombre = pacienteNombre;
        this.veterinarioNombre = veterinarioNombre;
    }
}
