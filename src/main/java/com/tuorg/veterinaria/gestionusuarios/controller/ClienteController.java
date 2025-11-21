package com.tuorg.veterinaria.gestionusuarios.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionusuarios.dto.ClienteRequest;
import com.tuorg.veterinaria.gestionusuarios.dto.ClienteResponse;
import com.tuorg.veterinaria.gestionusuarios.dto.ClienteUpdateRequest;
import com.tuorg.veterinaria.gestionusuarios.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponse>> crear(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse cliente = clienteService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cliente creado exitosamente", cliente));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> listar() {
        List<ClienteResponse> clientes = clienteService.listar();
        return ResponseEntity.ok(ApiResponse.success("Clientes obtenidos exitosamente", clientes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> obtener(@PathVariable Long id) {
        ClienteResponse cliente = clienteService.obtener(id);
        return ResponseEntity.ok(ApiResponse.success("Cliente obtenido exitosamente", cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateRequest request) {
        ClienteResponse cliente = clienteService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cliente actualizado exitosamente", cliente));
    }
}


