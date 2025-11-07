package com.tuorg.veterinaria.gestionusuarios.service;

import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.common.util.ValidationUtil;
import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de usuarios.
 * 
 * Este servicio proporciona métodos para crear, actualizar, eliminar
 * y consultar usuarios del sistema.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class UsuarioService {

    /**
     * Repositorio de usuarios.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Codificador de contraseñas.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param usuarioRepository Repositorio de usuarios
     * @param passwordEncoder Codificador de contraseñas
     */
    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un nuevo usuario.
     * 
     * @param usuario Usuario a crear
     * @return Usuario creado
     */
    @Transactional
    public Usuario crear(Usuario usuario) {
        // Validar username
        ValidationUtil.validateUsername(usuario.getUsername());
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new BusinessException("El nombre de usuario ya está en uso");
        }

        // Validar email
        ValidationUtil.validateEmail(usuario.getCorreo());
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new BusinessException("El correo electrónico ya está en uso");
        }

        // Validar y codificar contraseña
        ValidationUtil.validatePassword(usuario.getPasswordHash());
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));

        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    /**
     * Obtiene un usuario por su ID.
     * 
     * @param id ID del usuario
     * @return Usuario encontrado
     */
    @Transactional(readOnly = true)
    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    }

    /**
     * Obtiene todos los usuarios.
     * 
     * @return Lista de usuarios
     */
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Actualiza un usuario existente.
     * 
     * @param id ID del usuario
     * @param usuario Datos actualizados del usuario
     * @return Usuario actualizado
     */
    @Transactional
    public Usuario actualizar(Long id, Usuario usuario) {
        Usuario usuarioExistente = obtener(id);

        // Actualizar campos permitidos
        if (usuario.getNombre() != null) {
            usuarioExistente.setNombre(usuario.getNombre());
        }
        if (usuario.getApellido() != null) {
            usuarioExistente.setApellido(usuario.getApellido());
        }
        if (usuario.getTelefono() != null) {
            ValidationUtil.validatePhone(usuario.getTelefono());
            usuarioExistente.setTelefono(usuario.getTelefono());
        }
        if (usuario.getDireccion() != null) {
            usuarioExistente.setDireccion(usuario.getDireccion());
        }
        if (usuario.getActivo() != null) {
            usuarioExistente.setActivo(usuario.getActivo());
        }

        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Elimina un usuario (soft delete - desactiva).
     * 
     * @param id ID del usuario
     */
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtener(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Actualiza el último acceso del usuario.
     * 
     * @param username Nombre de usuario
     */
    @Transactional
    public void actualizarUltimoAcceso(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
}

