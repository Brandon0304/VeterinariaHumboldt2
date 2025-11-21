package com.tuorg.veterinaria.gestionusuarios.service;

import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.config.security.JwtTokenProvider;
import com.tuorg.veterinaria.gestionusuarios.dto.ForgotPasswordRequest;
import com.tuorg.veterinaria.gestionusuarios.dto.RegisterRequest;
import com.tuorg.veterinaria.gestionusuarios.dto.ResetPasswordRequest;
import com.tuorg.veterinaria.gestionusuarios.model.Rol;
import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.RolRepository;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import com.tuorg.veterinaria.gestionusuarios.dto.LoginResponse;
import com.tuorg.veterinaria.common.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

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
                      CustomUserDetailsService userDetailsService,
                      RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.rolRepository = rolRepository;
    }

    /**
     * Autentica un usuario y genera un token JWT.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Map con el token JWT y tipo de token
     */
    @Transactional
    public LoginResponse login(String username, String password) {
        try {
            // Verificar que el usuario existe antes de intentar autenticar
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado: " + username));
            
            if (!usuario.getActivo()) {
                throw new BusinessException("Usuario inactivo: " + username);
            }
            
            // Autenticar usuario (si falla, lanza AuthenticationException)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            // Obtener detalles del usuario autenticado
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Generar token JWT
            String token = tokenProvider.generateToken(userDetails);

            // Actualizar último acceso
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);

            LoginResponse.UsuarioLoginResponse usuarioResponse = new LoginResponse.UsuarioLoginResponse(
                    usuario.getIdUsuario(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getCorreo(),
                    usuario.getRol().getNombreRol()
            );

            return new LoginResponse(token, "Bearer", usuarioResponse);
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new BusinessException("Credenciales inválidas: " + e.getMessage());
        }
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
    public Usuario register(RegisterRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();

        // Verificar que el username no exista
        if (usuarioRepository.existsByUsername(username)) {
            throw new BusinessException("El nombre de usuario ya está en uso");
        }

        // Verificar que el email no exista
        if (usuarioRepository.existsByCorreo(email)) {
            throw new BusinessException("El correo electrónico ya está en uso");
        }

        // Validar y codificar contraseña
        ValidationUtil.validatePassword(request.getPassword());

        // Determinar el rol: usar el especificado en el request o CLIENTE por defecto
        String nombreRol = (request.getRol() != null && !request.getRol().trim().isEmpty()) 
                ? request.getRol().trim().toUpperCase() 
                : "CLIENTE";

        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new BusinessException("El rol '" + nombreRol + "' no está configurado en el sistema"));

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setCorreo(email);
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setActivo(true);
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    /**
     * Genera un token de recuperación de contraseña para un usuario.
     * 
     * @param request Solicitud con email o username
     * @return Token generado (para desarrollo, en producción se enviaría por email)
     */
    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        String emailOrUsername = request.getEmailOrUsername().trim();
        
        // Buscar usuario por email o username
        Usuario usuario = usuarioRepository.findByCorreo(emailOrUsername)
                .orElseGet(() -> usuarioRepository.findByUsername(emailOrUsername)
                        .orElse(null));

        if (usuario == null) {
            // Por seguridad, no revelamos si el usuario existe o no
            throw new BusinessException("Si el usuario existe, se enviará un correo con instrucciones para restablecer la contraseña");
        }

        if (!usuario.getActivo()) {
            throw new BusinessException("La cuenta está inactiva. Contacte al administrador.");
        }

        // Generar token único
        String token = generateResetToken();
        
        // Establecer token y expiración (1 hora desde ahora)
        usuario.setPasswordResetToken(token);
        usuario.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        // En producción, aquí se enviaría un email con el token
        // Por ahora, retornamos el token para desarrollo
        logger.info("🔑 Token de recuperación generado para usuario: {} - Token: {}", usuario.getUsername(), token);
        
        return token;
    }

    /**
     * Restablece la contraseña usando un token válido.
     * 
     * @param request Solicitud con token y nueva contraseña
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.getToken().trim();
        String newPassword = request.getNewPassword();

        // Buscar usuario por token
        Usuario usuario = usuarioRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new BusinessException("Token inválido o expirado"));

        // Verificar que el token no haya expirado
        if (usuario.getPasswordResetTokenExpiry() == null || 
            usuario.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            // Limpiar token expirado
            usuario.setPasswordResetToken(null);
            usuario.setPasswordResetTokenExpiry(null);
            usuarioRepository.save(usuario);
            throw new BusinessException("Token inválido o expirado");
        }

        // Validar nueva contraseña
        ValidationUtil.validatePassword(newPassword);

        // Actualizar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        
        // Limpiar token después de usarlo
        usuario.setPasswordResetToken(null);
        usuario.setPasswordResetTokenExpiry(null);
        
        usuarioRepository.save(usuario);
        
        logger.info("✅ Contraseña restablecida exitosamente para usuario: {}", usuario.getUsername());
    }

    /**
     * Genera un token único y seguro para recuperación de contraseña.
     * 
     * @return Token en formato Base64
     */
    private String generateResetToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}


