package com.tuorg.veterinaria.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Punto de entrada para manejar errores de autenticación.
 * 
 * Esta clase se ejecuta cuando un usuario no autenticado intenta acceder
 * a un recurso protegido o cuando el token JWT es inválido.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    /**
     * Maneja el error de autenticación.
     * 
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param authException Excepción de autenticación
     * @throws IOException Si hay error de I/O
     * @throws ServletException Si hay error en el servlet
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Error de autenticación en {}: {}", request.getRequestURI(), authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado: " + authException.getMessage());
    }
}

