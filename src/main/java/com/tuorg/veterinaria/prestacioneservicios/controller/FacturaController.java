package com.tuorg.veterinaria.prestacioneservicios.controller;

import com.tuorg.veterinaria.common.dto.ApiResponse;
import com.tuorg.veterinaria.prestacioneservicios.model.Factura;
import com.tuorg.veterinaria.prestacioneservicios.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
@RequestMapping("/api/facturas")
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
    public ResponseEntity<ApiResponse<Factura>> crear(@RequestBody Factura factura) {
        Factura facturaCreada = facturaService.crear(factura);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Factura creada exitosamente", facturaCreada));
    }

    /**
     * Obtiene una factura por su ID.
     * 
     * @param id ID de la factura
     * @return Respuesta con la factura
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Factura>> obtener(@PathVariable Long id) {
        Factura factura = facturaService.obtener(id);
        return ResponseEntity.ok(ApiResponse.success("Factura obtenida exitosamente", factura));
    }

    /**
     * Obtiene todas las facturas de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return Respuesta con la lista de facturas
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<Factura>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<Factura> facturas = facturaService.obtenerPorCliente(clienteId);
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
    public ResponseEntity<ApiResponse<Factura>> anular(@PathVariable Long id) {
        Factura factura = facturaService.anular(id);
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
    public ResponseEntity<ApiResponse<Factura>> registrarPago(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {
        String formaPago = requestBody.get("formaPago");
        Factura factura = facturaService.registrarPago(id, formaPago);
        return ResponseEntity.ok(ApiResponse.success("Pago registrado exitosamente", factura));
    }
}

