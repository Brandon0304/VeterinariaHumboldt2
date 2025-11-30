package com.tuorg.veterinaria.common.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final JavaMailSender mailSender;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String FROM_EMAIL = "noreply@veterinaria.com";

    @Async("emailTaskExecutor")
    @EventListener
    public void handleCitaCreatedEvent(CitaCreatedEvent event) {
        try {
            log.info("Procesando evento de cita creada: citaId={}", event.getCitaId());
            
            String asunto = "Confirmación de Cita - Veterinaria";
            String mensaje = String.format(
                "Estimado/a %s,\n\n" +
                "Su cita ha sido agendada exitosamente:\n\n" +
                "Paciente: %s\n" +
                "Veterinario: %s\n" +
                "Fecha y Hora: %s\n\n" +
                "Por favor, llegue 10 minutos antes de su cita.\n\n" +
                "Saludos cordiales,\n" +
                "Veterinaria Humboldt",
                event.getClienteNombre(),
                event.getPacienteNombre(),
                event.getVeterinarioNombre(),
                event.getFechaHora().format(DATE_FORMATTER)
            );
            
            enviarEmail(event.getClienteEmail(), asunto, mensaje);
            log.info("Email de confirmación enviado para citaId={}", event.getCitaId());
            
        } catch (Exception e) {
            log.error("Error al procesar evento de cita creada: citaId={}", event.getCitaId(), e);
            // No relanzamos la excepción para que no afecte la transacción principal
        }
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void handleCitaReprogrammedEvent(CitaReprogrammedEvent event) {
        try {
            log.info("Procesando evento de cita reprogramada: citaId={}", event.getCitaId());
            
            String asunto = "Cita Reprogramada - Veterinaria";
            String mensaje = String.format(
                "Estimado/a %s,\n\n" +
                "Su cita ha sido reprogramada:\n\n" +
                "Paciente: %s\n" +
                "Veterinario: %s\n" +
                "Fecha Anterior: %s\n" +
                "Nueva Fecha: %s\n\n" +
                "Por favor, confirme su asistencia.\n\n" +
                "Saludos cordiales,\n" +
                "Veterinaria Humboldt",
                event.getClienteNombre(),
                event.getPacienteNombre(),
                event.getVeterinarioNombre(),
                event.getFechaAnterior().format(DATE_FORMATTER),
                event.getFechaNueva().format(DATE_FORMATTER)
            );
            
            enviarEmail(event.getClienteEmail(), asunto, mensaje);
            log.info("Email de reprogramación enviado para citaId={}", event.getCitaId());
            
        } catch (Exception e) {
            log.error("Error al procesar evento de cita reprogramada: citaId={}", event.getCitaId(), e);
        }
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void handleCitaCancelledEvent(CitaCancelledEvent event) {
        try {
            log.info("Procesando evento de cita cancelada: citaId={}", event.getCitaId());
            
            String asunto = "Cita Cancelada - Veterinaria";
            String mensaje = String.format(
                "Estimado/a %s,\n\n" +
                "Su cita ha sido cancelada:\n\n" +
                "Paciente: %s\n" +
                "Fecha y Hora: %s\n" +
                "Motivo: %s\n\n" +
                "Si desea agendar una nueva cita, por favor contáctenos.\n\n" +
                "Saludos cordiales,\n" +
                "Veterinaria Humboldt",
                event.getClienteNombre(),
                event.getPacienteNombre(),
                event.getFechaHora().format(DATE_FORMATTER),
                event.getMotivoCancelacion() != null ? event.getMotivoCancelacion() : "No especificado"
            );
            
            enviarEmail(event.getClienteEmail(), asunto, mensaje);
            log.info("Email de cancelación enviado para citaId={}", event.getCitaId());
            
        } catch (Exception e) {
            log.error("Error al procesar evento de cita cancelada: citaId={}", event.getCitaId(), e);
        }
    }

    /**
     * Método helper para enviar emails usando JavaMailSender.
     */
    private void enviarEmail(String destinatario, String asunto, String mensaje) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(FROM_EMAIL);
            mailMessage.setTo(destinatario);
            mailMessage.setSubject(asunto);
            mailMessage.setText(mensaje);
            
            mailSender.send(mailMessage);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", destinatario, e.getMessage());
            // No relanzar excepción para no afectar el flujo asíncrono
        }
    }
}
