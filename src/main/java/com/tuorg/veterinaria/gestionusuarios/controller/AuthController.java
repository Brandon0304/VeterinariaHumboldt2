package com.tuorg.veterinaria.gestionusuarios.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionusuarios.dto.ForgotPasswordRequest;
import com.tuorg.veterinaria.gestionusuarios.dto.LoginRequest;
import com.tuorg.veterinaria.gestionusuarios.dto.LoginResponse;
import com.tuorg.veterinaria.gestionusuarios.dto.RegisterRequest;
import com.tuorg.veterinaria.gestionusuarios.dto.ResetPasswordRequest;
import com.tuorg.veterinaria.gestionusuarios.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para autenticación.
 * 
 * Este controlador expone endpoints para login y registro de usuarios.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")  // Sin /api porque el context-path ya lo incluye
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Servicio de autenticación.
     */
    private final AuthService authService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param authService Servicio de autenticación
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint para iniciar sesión.
     * 
     * @param loginRequest Cuerpo de la petición con username y password
     * @return Respuesta con el token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("🔐 Intento de login recibido - Username: {}", loginRequest.getUsername());
        try {
            LoginResponse tokenResponse = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
            logger.info("✅ Login exitoso para usuario: {}", loginRequest.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login exitoso", tokenResponse));
        } catch (Exception e) {
            logger.error("❌ Error en login para usuario {}: {}", loginRequest.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * 
     * @param registerRequest Cuerpo de la petición con datos del usuario
     * @return Respuesta con el usuario creado
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("Usuario registrado exitosamente"));
    }

    /**
     * Endpoint para solicitar recuperación de contraseña.
     * 
     * @param request Solicitud con email o username
     * @return Respuesta con mensaje de confirmación (en desarrollo incluye el token)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        logger.info("🔑 Solicitud de recuperación de contraseña recibida para: {}", request.getEmailOrUsername());
        try {
            String token = authService.forgotPassword(request);
            // En desarrollo, retornamos el token. En producción, solo un mensaje genérico
            return ResponseEntity.ok(ApiResponse.success(
                "Si el usuario existe, se enviará un correo con instrucciones. Token (solo desarrollo): " + token,
                token
            ));
        } catch (Exception e) {
            logger.error("❌ Error en recuperación de contraseña: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Endpoint para restablecer contraseña con token.
     * 
     * @param request Solicitud con token y nueva contraseña
     * @return Respuesta de confirmación
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        logger.info("🔐 Solicitud de restablecimiento de contraseña recibida");
        try {
            authService.resetPassword(request);
            logger.info("✅ Contraseña restablecida exitosamente");
            return ResponseEntity.ok(ApiResponse.success("Contraseña restablecida exitosamente"));
        } catch (Exception e) {
            logger.error("❌ Error al restablecer contraseña: {}", e.getMessage(), e);
            throw e;
        }
    }
}

