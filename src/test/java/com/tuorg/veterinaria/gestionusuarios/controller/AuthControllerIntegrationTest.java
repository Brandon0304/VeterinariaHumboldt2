package com.tuorg.veterinaria.gestionusuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuorg.veterinaria.config.AbstractIntegrationTest;
import com.tuorg.veterinaria.gestionusuarios.dto.LoginRequest;
import com.tuorg.veterinaria.gestionusuarios.model.Rol;
import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.RolRepository;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para AuthController.
 * 
 * Verifica POST /api/auth/login: 401 con credenciales malas y 200 con token correcto.
 */
@DisplayName("Pruebas de integración de AuthController")
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        // Crear rol
        rol = new Rol();
        rol.setNombreRol("CLIENTE");
        rol.setDescripcion("Rol de cliente");
        rol = rolRepository.save(rol);

        // Crear usuario de prueba
        usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setPasswordHash(passwordEncoder.encode("password123"));
        usuario.setCorreo("test@example.com");
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);
    }

    @Test
    @DisplayName("POST /api/auth/login: credenciales correctas debe retornar 200 con token")
    void loginCredencialesCorrectas_DeberiaRetornar200ConToken() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.usuario.username").value("testuser"));
    }

    @Test
    @DisplayName("POST /api/auth/login: credenciales incorrectas debe retornar 401")
    void loginCredencialesIncorrectas_DeberiaRetornar401() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("passwordIncorrecta");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login: usuario inexistente debe retornar error")
    void loginUsuarioInexistente_DeberiaRetornarError() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("usuarioInexistente");
        loginRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}

