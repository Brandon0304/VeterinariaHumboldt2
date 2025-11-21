package com.tuorg.veterinaria.gestionusuarios.service;

import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Servicio personalizado para cargar detalles de usuario para Spring Security.
 * 
 * Este servicio implementa UserDetailsService y se utiliza para autenticación
 * con Spring Security y JWT.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repositorio de usuarios.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param usuarioRepository Repositorio de usuarios
     */
    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga un usuario por su nombre de usuario.
     * 
     * @param username Nombre de usuario
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities(getAuthorities(usuario))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }

    /**
     * Obtiene las autoridades (roles) del usuario.
     * 
     * @param usuario Usuario del cual obtener las autoridades
     * @return Colección de autoridades
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Agregar el rol del usuario como autoridad
        if (usuario.getRol() != null && usuario.getRol().getNombreRol() != null) {
            String roleName = usuario.getRol().getNombreRol().toUpperCase();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
        }
        
        return authorities;
    }
}


