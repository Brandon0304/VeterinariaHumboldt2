package com.tuorg.veterinaria.gestionusuarios.service;

import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.config.security.JwtTokenProvider;
import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de autenticación.
 *
 * Este servicio maneja la autenticación de usuarios, generación de tokens JWT
 * y registro de nuevos usuarios.
 *
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class AuthService {

    /**
     * Repositorio de usuarios.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Gestor de autenticación de Spring Security.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Proveedor de tokens JWT.
     */
    private final JwtTokenProvider tokenProvider;

    /**
     * Codificador de contraseñas.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Servicio de detalles de usuario.
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioRepository Repositorio de usuarios
     * @param authenticationManager Gestor de autenticación
     * @param tokenProvider Proveedor de tokens JWT
     * @param passwordEncoder Codificador de contraseñas
     * @param userDetailsService Servicio de detalles de usuario
     */
    @Autowired
    public AuthService(UsuarioRepository usuarioRepository,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       PasswordEncoder passwordEncoder,
                       CustomUserDetailsService userDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Autentica un usuario y genera un token JWT.
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Map con el token JWT y tipo de token
     */
    @Transactional
    public Map<String, String> login(String username, String password) {
        // Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Obtener detalles del usuario autenticado
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Generar token JWT
        String token = tokenProvider.generateToken(userDetails);

        // Actualizar último acceso
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Retornar token
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        return response;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param email Correo electrónico
     * @param nombre Nombre de la persona
     * @param apellido Apellido de la persona
     * @return Usuario creado
     */
    @Transactional
    public Usuario register(String username, String password, String email, String nombre, String apellido) {
        // Verificar que el username no exista
        if (usuarioRepository.existsByUsername(username)) {
            throw new BusinessException("El nombre de usuario ya está en uso");
        }

        // Verificar que el email no exista
        if (usuarioRepository.existsByCorreo(email)) {
            throw new BusinessException("El correo electrónico ya está en uso");
        }

        // Crear nuevo usuario (simplificado, se puede extender según necesidades)
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setCorreo(email);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setActivo(true);

        // TODO: Asignar rol por defecto (CLIENTE) - requiere obtener el rol de la BD

        return usuarioRepository.save(usuario);
    }
}
