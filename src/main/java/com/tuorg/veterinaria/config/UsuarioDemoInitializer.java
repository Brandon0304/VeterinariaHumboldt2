package com.tuorg.veterinaria.config;

import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Inicializador que asegura que el usuario demo tenga un hash compatible con Spring Security.
 * Se ejecuta al iniciar la aplicación.
 */
@Component
public class UsuarioDemoInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioDemoInitializer.class);
    private static final String VET_DEMO_USERNAME = "vet_demo";
    private static final String VET_DEMO_PASSWORD = "Vet1234!";
    private static final String SECRETARIO_DEMO_USERNAME = "secretario_demo";
    private static final String SECRETARIO_DEMO_PASSWORD = "Secret1234!";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Admin123!";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioDemoInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        actualizarHashUsuario(VET_DEMO_USERNAME, VET_DEMO_PASSWORD);
        actualizarHashUsuario(SECRETARIO_DEMO_USERNAME, SECRETARIO_DEMO_PASSWORD);
        actualizarHashUsuario(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    private void actualizarHashUsuario(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String hashActual = usuario.getPasswordHash();
            
            // Verificar si el hash es compatible con Spring Security BCrypt
            // Los hashes de Spring Security BCrypt siempre empiezan con $2a$ o $2b$ y tienen 60 caracteres
            if (hashActual == null || (!hashActual.startsWith("$2a$") && !hashActual.startsWith("$2b$")) || hashActual.length() != 60) {
                logger.info("🔄 Actualizando hash de contraseña para usuario demo '{}' a formato compatible con Spring Security", username);
                String nuevoHash = passwordEncoder.encode(password);
                usuario.setPasswordHash(nuevoHash);
                usuarioRepository.save(usuario);
                logger.info("✅ Hash actualizado correctamente para usuario '{}'", username);
            } else {
                logger.debug("✓ Hash del usuario '{}' ya es compatible con Spring Security", username);
            }
        } else {
            logger.debug("Usuario demo '{}' no encontrado. Se puede crear usando el script SQL.", username);
        }
    }
}

