package com.tuorg.veterinaria.common.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class CitaCancelledEvent extends ApplicationEvent {
    private final Long citaId;
    private final LocalDateTime fechaHora;
    private final String clienteEmail;
    private final String clienteTelefono;
    private final String clienteNombre;
    private final String pacienteNombre;
    private final String motivoCancelacion;

    public CitaCancelledEvent(Object source, Long citaId, LocalDateTime fechaHora, 
                               String clienteEmail, String clienteTelefono,
                               String clienteNombre, String pacienteNombre, 
                               String motivoCancelacion) {
        super(source);
        this.citaId = citaId;
        this.fechaHora = fechaHora;
        this.clienteEmail = clienteEmail;
        this.clienteTelefono = clienteTelefono;
        this.clienteNombre = clienteNombre;
        this.pacienteNombre = pacienteNombre;
        this.motivoCancelacion = motivoCancelacion;
    }
}
