package com.tuorg.veterinaria.gestioninventario.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestioninventario.model.MovimientoInventario;
import com.tuorg.veterinaria.gestioninventario.service.MovimientoInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de movimientos de inventario.
 * 
 * Este controlador expone endpoints para registrar entradas y salidas
 * de inventario (implementando el patrón Command).
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/movimientos-inventario")
public class MovimientoInventarioController {

    /**
     * Servicio de gestión de movimientos de inventario.
     */
    private final MovimientoInventarioService movimientoInventarioService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param movimientoInventarioService Servicio de movimientos
     */
    @Autowired
    public MovimientoInventarioController(MovimientoInventarioService movimientoInventarioService) {
        this.movimientoInventarioService = movimientoInventarioService;
    }

    /**
     * Registra una entrada de inventario.
     * 
     * @param requestBody Cuerpo de la petición con los datos del movimiento
     * @return Respuesta con el movimiento creado
     */
    @PostMapping("/entrada")
    public ResponseEntity<ApiResponse<MovimientoInventario>> registrarEntrada(
            @RequestBody Map<String, Object> requestBody) {
        Long productoId = Long.valueOf(requestBody.get("productoId").toString());
        Integer cantidad = Integer.valueOf(requestBody.get("cantidad").toString());
        Long proveedorId = requestBody.get("proveedorId") != null ?
                Long.valueOf(requestBody.get("proveedorId").toString()) : null;
        String referencia = (String) requestBody.get("referencia");
        Long usuarioId = requestBody.get("usuarioId") != null ?
                Long.valueOf(requestBody.get("usuarioId").toString()) : null;

        MovimientoInventario movimiento = movimientoInventarioService.registrarEntrada(
                productoId, proveedorId, cantidad, referencia, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Entrada de inventario registrada exitosamente", movimiento));
    }

    /**
     * Registra una salida de inventario.
     * 
     * @param requestBody Cuerpo de la petición con los datos del movimiento
     * @return Respuesta con el movimiento creado
     */
    @PostMapping("/salida")
    public ResponseEntity<ApiResponse<MovimientoInventario>> registrarSalida(
            @RequestBody Map<String, Object> requestBody) {
        Long productoId = Long.valueOf(requestBody.get("productoId").toString());
        Integer cantidad = Integer.valueOf(requestBody.get("cantidad").toString());
        String referencia = (String) requestBody.get("referencia");
        Long usuarioId = requestBody.get("usuarioId") != null ?
                Long.valueOf(requestBody.get("usuarioId").toString()) : null;

        MovimientoInventario movimiento = movimientoInventarioService.registrarSalida(
                productoId, cantidad, referencia, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salida de inventario registrada exitosamente", movimiento));
    }

    /**
     * Obtiene todos los movimientos de un producto.
     * 
     * @param productoId ID del producto
     * @return Respuesta con la lista de movimientos
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponse<List<MovimientoInventario>>> obtenerPorProducto(
            @PathVariable Long productoId) {
        List<MovimientoInventario> movimientos = movimientoInventarioService.obtenerPorProducto(productoId);
        return ResponseEntity.ok(ApiResponse.success("Movimientos obtenidos exitosamente", movimientos));
    }

    /**
     * Obtiene movimientos en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio (formato: yyyy-MM-ddTHH:mm:ss)
     * @param fechaFin Fecha de fin (formato: yyyy-MM-ddTHH:mm:ss)
     * @return Respuesta con la lista de movimientos
     */
    @GetMapping("/rango-fechas")
    public ResponseEntity<ApiResponse<List<MovimientoInventario>>> obtenerPorRangoFechas(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);
        List<MovimientoInventario> movimientos = movimientoInventarioService.obtenerPorRangoFechas(inicio, fin);
        return ResponseEntity.ok(ApiResponse.success("Movimientos obtenidos exitosamente", movimientos));
    }
}

