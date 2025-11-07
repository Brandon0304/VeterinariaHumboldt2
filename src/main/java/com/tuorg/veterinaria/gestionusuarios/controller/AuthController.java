package com.tuorg.veterinaria.gestionusuarios.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionusuarios.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para autenticación.
 * 
 * Este controlador expone endpoints para login y registro de usuarios.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Map<String, String> tokenResponse = authService.login(username, password);
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", tokenResponse));
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * 
     * @param registerRequest Cuerpo de la petición con datos del usuario
     * @return Respuesta con el usuario creado
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String email = registerRequest.get("email");
        String nombre = registerRequest.get("nombre");
        String apellido = registerRequest.get("apellido");

        authService.register(username, password, email, nombre, apellido);
        return ResponseEntity.ok(ApiResponse.success("Usuario registrado exitosamente"));
    }
}

