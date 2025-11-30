package com.tuorg.veterinaria.notificaciones.config;

import com.tuorg.veterinaria.notificaciones.model.CanalEmail;
import com.tuorg.veterinaria.notificaciones.repository.CanalEnvioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Configuración para inyectar JavaMailSender en los canales de email existentes.
 * 
 * Esta clase se encarga de configurar automáticamente el JavaMailSender
 * en todos los canales de email que estén registrados en la base de datos.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class EmailConfig {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CanalEnvioRepository canalEnvioRepository;

    /**
     * Después de inicializar el contexto de Spring, 
     * inyecta el JavaMailSender en todos los canales de email.
     */
    @PostConstruct
    public void configurarCanalesEmail() {
        try {
            List<?> canales = canalEnvioRepository.findAll();
            
            long canalesConfigurados = canales.stream()
                .filter(CanalEmail.class::isInstance)
                .map(CanalEmail.class::cast)
                .peek(canalEmail -> canalEmail.setMailSender(mailSender))
                .count();
            
            log.info("JavaMailSender configurado en {} canales de email", canalesConfigurados);
                    
        } catch (Exception e) {
            log.warn("No se pudieron configurar los canales de email: {}. Esto es normal si la base de datos aún no tiene registros de canales.", e.getMessage());
        }
    }
}
