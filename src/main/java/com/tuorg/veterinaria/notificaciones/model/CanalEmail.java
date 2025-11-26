package com.tuorg.veterinaria.notificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Implementación concreta de CanalEnvio para envío por email.
 * 
 * Esta clase extiende CanalEnvio e implementa la estrategia de envío
 * por correo electrónico (Strategy pattern).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Entity
@Table(name = "canales_email", schema = "public")
@PrimaryKeyJoinColumn(name = "id_canal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanalEmail extends CanalEnvio {

    /**
     * Servidor SMTP para el envío de emails.
     */
    @Column(name = "smtp_server", length = 150)
    private String smtpServer;

    /**
     * Dirección de correo del remitente.
     */
    @Column(name = "from_address", length = 150)
    private String fromAddress;

    /**
     * JavaMailSender inyectado por Spring para envío de emails.
     * @Transient para que JPA no intente persistirlo en la BD.
     */
    @Transient
    private JavaMailSender mailSender;

    /**
     * Implementación del método enviar para email.
     * 
     * @param notificacion Notificación a enviar
     * @return true si el envío fue exitoso, false en caso contrario
     */
    @Override
    public boolean enviar(Notificacion notificacion) {
        // Validar que el JavaMailSender esté configurado
        if (mailSender == null) {
            System.err.println("❌ JavaMailSender no está configurado. Verifica la configuración de Spring Mail.");
            return false;
        }

        try {
            // Crear mensaje de email simple
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(extraerEmailDestinatario(notificacion));
            message.setSubject(construirAsunto(notificacion));
            message.setText(notificacion.getMensaje());

            // Enviar el email
            mailSender.send(message);
            
            System.out.println("✅ Email enviado exitosamente a través de " + smtpServer);
            System.out.println("   → Para: " + extraerEmailDestinatario(notificacion));
            System.out.println("   → Asunto: " + construirAsunto(notificacion));
            
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extrae el email del destinatario desde los datos de la notificación.
     * Lee el campo "destinatario" del JSON almacenado en notificacion.datos
     */
    private String extraerEmailDestinatario(Notificacion notificacion) {
        try {
            // Parsear el JSON de datos para obtener el destinatario
            String datosJson = notificacion.getDatos();
            if (datosJson != null && !datosJson.isEmpty()) {
                // Buscar el campo "destinatario" en el JSON
                // Formato esperado: {"destinatario":"email@ejemplo.com", ...}
                if (datosJson.contains("\"destinatario\"")) {
                    int inicioEmail = datosJson.indexOf("\"destinatario\":\"") + 16;
                    int finEmail = datosJson.indexOf("\"", inicioEmail);
                    if (finEmail > inicioEmail) {
                        String emailDestinatario = datosJson.substring(inicioEmail, finEmail);
                        System.out.println("📧 Destinatario extraído: " + emailDestinatario);
                        return emailDestinatario;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al extraer destinatario, usando from_address: " + e.getMessage());
        }
        
        // Fallback: Si no se puede extraer el destinatario, usar from_address
        System.out.println("⚠️ No se pudo extraer destinatario, usando from_address: " + fromAddress);
        return fromAddress;
    }

    /**
     * Construye el asunto del email basado en el tipo de notificación.
     */
    private String construirAsunto(Notificacion notificacion) {
        String tipoFormateado = notificacion.getTipo()
            .replace("_", " ")
            .toLowerCase();
        
        // Capitalizar primera letra de cada palabra
        String[] palabras = tipoFormateado.split(" ");
        StringBuilder asunto = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                asunto.append(Character.toUpperCase(palabra.charAt(0)))
                      .append(palabra.substring(1))
                      .append(" ");
            }
        }
        
        return "🐾 Clínica Veterinaria - " + asunto.toString().trim();
    }
}

