package com.tuorg.veterinaria.gestioninventario.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestioninventario.dto.ProveedorRequest;
import com.tuorg.veterinaria.gestioninventario.dto.ProveedorResponse;
import com.tuorg.veterinaria.gestioninventario.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de proveedores.
 * 
 * Este controlador expone endpoints para crear, consultar, actualizar
 * y eliminar proveedores.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/proveedores")  // Sin /api porque el context-path ya lo incluye
public class ProveedorController {

    /**
     * Servicio de gestión de proveedores.
     */
    private final ProveedorService proveedorService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param proveedorService Servicio de proveedores
     */
    @Autowired
    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    /**
     * Crea un nuevo proveedor.
     * 
     * @param request Datos del proveedor a crear
     * @return Respuesta con el proveedor creado
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProveedorResponse>> crear(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse proveedorCreado = proveedorService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Proveedor creado exitosamente", proveedorCreado));
    }

    /**
     * Obtiene un proveedor por su ID.
     * 
     * @param id ID del proveedor
     * @return Respuesta con el proveedor
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponse>> obtener(@PathVariable Long id) {
        ProveedorResponse proveedor = proveedorService.obtener(id);
        return ResponseEntity.ok(ApiResponse.success("Proveedor obtenido exitosamente", proveedor));
    }

    /**
     * Obtiene todos los proveedores.
     * 
     * @return Respuesta con la lista de proveedores
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProveedorResponse>>> obtenerTodos() {
        List<ProveedorResponse> proveedores = proveedorService.obtenerTodos();
        return ResponseEntity.ok(ApiResponse.success("Proveedores obtenidos exitosamente", proveedores));
    }

    /**
     * Actualiza un proveedor existente.
     * 
     * @param id ID del proveedor
     * @param request Datos actualizados del proveedor
     * @return Respuesta con el proveedor actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse proveedor = proveedorService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success("Proveedor actualizado exitosamente", proveedor));
    }

    /**
     * Elimina un proveedor.
     * 
     * @param id ID del proveedor
     * @return Respuesta de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Proveedor eliminado exitosamente"));
    }
}

