package com.tuorg.veterinaria.gestionfacturacion.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.gestionfacturacion.dto.FacturaPagoRequest;
import com.tuorg.veterinaria.gestionfacturacion.dto.FacturaRequest;
import com.tuorg.veterinaria.gestionfacturacion.dto.FacturaResponse;
import com.tuorg.veterinaria.gestionfacturacion.service.FacturaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de facturas.
 * 
 * Este controlador expone endpoints para crear, consultar,
 * generar PDFs, anular y registrar pagos de facturas.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/facturas")
public class FacturaController {

    /**
     * Servicio de gestión de facturas.
     */
    private final FacturaService facturaService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param facturaService Servicio de facturas
     */
    @Autowired
    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    /**
     * Crea una nueva factura.
     * 
     * @param factura Factura a crear
     * @return Respuesta con la factura creada
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FacturaResponse>> crear(@RequestBody @Valid FacturaRequest factura) {
        FacturaResponse facturaCreada = facturaService.crear(factura);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Factura creada exitosamente", facturaCreada));
    }

    /**
     * Obtiene todas las facturas.
     * 
     * @return Respuesta con la lista de facturas
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FacturaResponse>>> obtenerTodas() {
        List<FacturaResponse> facturas = facturaService.obtenerTodas();
        return ResponseEntity.ok(ApiResponse.success("Facturas obtenidas exitosamente", facturas));
    }

    /**
     * Obtiene una factura por su ID.
     * 
     * @param id ID de la factura
     * @return Respuesta con la factura
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacturaResponse>> obtener(@PathVariable Long id) {
        FacturaResponse factura = facturaService.obtener(id);
        return ResponseEntity.ok(ApiResponse.success("Factura obtenida exitosamente", factura));
    }

    /**
     * Obtiene todas las facturas de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return Respuesta con la lista de facturas
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<FacturaResponse>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<FacturaResponse> facturas = facturaService.obtenerPorCliente(clienteId);
        return ResponseEntity.ok(ApiResponse.success("Facturas obtenidas exitosamente", facturas));
    }

    /**
     * Genera el PDF de una factura.
     * 
     * @param id ID de la factura
     * @return Respuesta con el PDF en bytes
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generarPDF(@PathVariable Long id) {
        byte[] pdf = facturaService.generarPDF(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=factura_" + id + ".pdf")
                .body(pdf);
    }

    /**
     * Anula una factura.
     * 
     * @param id ID de la factura
     * @return Respuesta con la factura anulada
     */
    @PutMapping("/{id}/anular")
    public ResponseEntity<ApiResponse<FacturaResponse>> anular(@PathVariable Long id) {
        FacturaResponse factura = facturaService.anular(id);
        return ResponseEntity.ok(ApiResponse.success("Factura anulada exitosamente", factura));
    }

    /**
     * Registra el pago de una factura.
     * 
     * @param id ID de la factura
     * @param requestBody Cuerpo con la forma de pago
     * @return Respuesta con la factura actualizada
     */
    @PutMapping("/{id}/pagar")
    public ResponseEntity<ApiResponse<FacturaResponse>> registrarPago(
            @PathVariable Long id,
            @RequestBody @Valid FacturaPagoRequest request) {
        FacturaResponse factura = facturaService.registrarPago(id, request);
        return ResponseEntity.ok(ApiResponse.success("Pago registrado exitosamente", factura));
    }
}

