package com.tuorg.veterinaria.config;

import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Componente que actualiza las contraseñas de los usuarios de demostración al iniciar la aplicación.
 * Se ejecuta después del AdminPasswordFixer (Order = 2).
 */
@Slf4j
@Component
@Order(2)
public class DemoUsersPasswordFixer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DemoUsersPasswordFixer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🔧 Actualizando contraseñas de usuarios de demostración...");

        // Actualizar cliente_demo
        actualizarPassword("cliente_demo", "Cliente123!");
        
        // Actualizar secretario_demo2
        actualizarPassword("secretario_demo2", "Secretario123!");
        
        log.info("✅ Contraseñas de usuarios demo actualizadas correctamente!");
    }

    private void actualizarPassword(String username, String plainPassword) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String hashedPassword = passwordEncoder.encode(plainPassword);
            usuario.setPasswordHash(hashedPassword);
            usuarioRepository.save(usuario);
            
            log.info("   ✓ Password actualizado para: {}", username);
            log.info("      Username: {}", username);
            log.info("      Password: {}", plainPassword);
        } else {
            log.warn("   ⚠️ Usuario {} no encontrado, omitiendo actualización", username);
        }
    }
}
