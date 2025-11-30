package com.tuorg.veterinaria.gestionusuarios.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionusuarios.dto.UsuarioRequest;
import com.tuorg.veterinaria.gestionusuarios.dto.UsuarioResponse;
import com.tuorg.veterinaria.gestionusuarios.dto.UsuarioUpdateRequest;
import com.tuorg.veterinaria.gestionusuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de usuarios.
 * 
 * Este controlador expone endpoints para crear, consultar, actualizar
 * y eliminar usuarios del sistema.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    /**
     * Servicio de gestión de usuarios.
     */
    private final UsuarioService usuarioService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param usuarioService Servicio de usuarios
     */
    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Crea un nuevo usuario.
     * 
     * @param usuario Usuario a crear
     * @return Respuesta con el usuario creado
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> crear(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse usuarioCreado = usuarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario creado exitosamente", usuarioCreado));
    }

    /**
     * Obtiene un usuario por su ID.
     * 
     * @param id ID del usuario
     * @return Respuesta con el usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtener(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.obtener(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario obtenido exitosamente", usuario));
    }

    /**
     * Obtiene todos los usuarios.
     * 
     * @return Respuesta con la lista de usuarios
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> obtenerTodos() {
        List<UsuarioResponse> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(ApiResponse.success("Usuarios obtenidos exitosamente", usuarios));
    }

    /**
     * Actualiza un usuario existente.
     * 
     * @param id ID del usuario
     * @param usuario Datos actualizados del usuario
     * @return Respuesta con el usuario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        UsuarioResponse usuarioActualizado = usuarioService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success("Usuario actualizado exitosamente", usuarioActualizado));
    }

    /**
     * Elimina un usuario (desactiva).
     * 
     * @param id ID del usuario
     * @return Respuesta de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario eliminado exitosamente"));
    }

    /**
     * Obtiene todos los veterinarios activos.
     * 
     * @return Respuesta con la lista de veterinarios activos
     */
    @GetMapping("/veterinarios")
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> obtenerVeterinarios() {
        List<UsuarioResponse> veterinarios = usuarioService.obtenerVeterinariosActivos();
        return ResponseEntity.ok(ApiResponse.success("Veterinarios obtenidos exitosamente", veterinarios));
    }
}

