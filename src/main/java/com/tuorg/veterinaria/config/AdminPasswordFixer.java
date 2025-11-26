package com.tuorg.veterinaria.config;

import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Inicializador que fuerza la actualización del password del admin.
 */
@Component
@Order(1)
public class AdminPasswordFixer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminPasswordFixer.class);
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Admin123!";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminPasswordFixer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("🔧 Ejecutando corrección de password para usuario admin...");
        
        Optional<Usuario> adminOpt = usuarioRepository.findByUsername(ADMIN_USERNAME);
        
        if (adminOpt.isPresent()) {
            Usuario admin = adminOpt.get();
            
            // Generar nuevo hash
            String nuevoHash = passwordEncoder.encode(ADMIN_PASSWORD);
            
            logger.info("🔐 Actualizando password del admin...");
            logger.info("   Username: {}", ADMIN_USERNAME);
            logger.info("   Password: {}", ADMIN_PASSWORD);
            logger.info("   Nuevo hash: {}", nuevoHash);
            
            admin.setPasswordHash(nuevoHash);
            admin.setActivo(true);
            
            usuarioRepository.save(admin);
            usuarioRepository.flush();
            
            logger.info("✅ Password del admin actualizado correctamente!");
            logger.info("   Puedes iniciar sesión con:");
            logger.info("   - Username: {}", ADMIN_USERNAME);
            logger.info("   - Password: {}", ADMIN_PASSWORD);
            
        } else {
            logger.error("❌ Usuario admin no encontrado en la base de datos!");
        }
    }
}
