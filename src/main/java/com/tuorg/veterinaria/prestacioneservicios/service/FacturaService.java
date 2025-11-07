package com.tuorg.veterinaria.prestacioneservicios.service;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.gestionusuarios.model.Cliente;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import com.tuorg.veterinaria.prestacioneservicios.model.Factura;
import com.tuorg.veterinaria.prestacioneservicios.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para la gestión de facturas.
 * 
 * Este servicio implementa el patrón Factory/Builder para crear facturas
 * y proporciona métodos para generar PDFs y anular facturas.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class FacturaService {

    /**
     * Repositorio de facturas.
     */
    private final FacturaRepository facturaRepository;

    /**
     * Repositorio de usuarios (para obtener clientes).
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param facturaRepository Repositorio de facturas
     * @param usuarioRepository Repositorio de usuarios
     */
    @Autowired
    public FacturaService(FacturaRepository facturaRepository,
                         UsuarioRepository usuarioRepository) {
        this.facturaRepository = facturaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crea una nueva factura (Factory pattern).
     * 
     * Genera automáticamente el número de factura único.
     * 
     * @param factura Factura a crear
     * @return Factura creada
     */
    @Transactional
    public Factura crear(Factura factura) {
        // Validar que el cliente exista
        Cliente cliente = (Cliente) usuarioRepository.findById(factura.getCliente().getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", 
                        factura.getCliente().getIdUsuario()));

        // Generar número de factura único
        String numeroFactura = generarNumeroFactura();
        while (facturaRepository.findByNumero(numeroFactura).isPresent()) {
            numeroFactura = generarNumeroFactura();
        }
        factura.setNumero(numeroFactura);

        // Validar total
        if (factura.getTotal() == null || factura.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El total de la factura debe ser mayor o igual a cero");
        }

        factura.setFechaEmision(LocalDateTime.now());
        factura.setEstado(AppConstants.ESTADO_FACTURA_PENDIENTE);

        return facturaRepository.save(factura);
    }

    /**
     * Genera un número de factura único.
     * 
     * Formato: FACT-YYYYMMDD-XXXX
     * 
     * @return Número de factura generado
     */
    private String generarNumeroFactura() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String secuencia = String.format("%04d", (int) (Math.random() * 10000));
        return "FACT-" + fecha + "-" + secuencia;
    }

    /**
     * Obtiene una factura por su ID.
     * 
     * @param id ID de la factura
     * @return Factura encontrada
     */
    @Transactional(readOnly = true)
    public Factura obtener(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
    }

    /**
     * Obtiene todas las facturas de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return Lista de facturas del cliente
     */
    @Transactional(readOnly = true)
    public List<Factura> obtenerPorCliente(Long clienteId) {
        return facturaRepository.findByClienteId(clienteId);
    }

    /**
     * Genera el PDF de una factura (simplificado).
     * 
     * En una implementación completa, esto generaría un PDF real con iText o JasperReports.
     * 
     * @param facturaId ID de la factura
     * @return Array de bytes representando el PDF
     */
    @Transactional(readOnly = true)
    public byte[] generarPDF(Long facturaId) {
        Factura factura = obtener(facturaId);
        // TODO: Implementar generación real de PDF con iText o JasperReports
        // Por ahora retornamos un array vacío
        return new byte[0];
    }

    /**
     * Anula una factura.
     * 
     * @param facturaId ID de la factura
     * @return Factura anulada
     */
    @Transactional
    public Factura anular(Long facturaId) {
        Factura factura = obtener(facturaId);

        if (AppConstants.ESTADO_FACTURA_ANULADA.equals(factura.getEstado())) {
            throw new BusinessException("La factura ya está anulada");
        }

        if (AppConstants.ESTADO_FACTURA_PAGADA.equals(factura.getEstado())) {
            throw new BusinessException("No se puede anular una factura ya pagada");
        }

        factura.setEstado(AppConstants.ESTADO_FACTURA_ANULADA);
        return facturaRepository.save(factura);
    }

    /**
     * Registra el pago de una factura.
     * 
     * @param facturaId ID de la factura
     * @param formaPago Forma de pago utilizada
     * @return Factura actualizada
     */
    @Transactional
    public Factura registrarPago(Long facturaId, String formaPago) {
        Factura factura = obtener(facturaId);

        if (!AppConstants.ESTADO_FACTURA_PENDIENTE.equals(factura.getEstado())) {
            throw new BusinessException("Solo se pueden pagar facturas en estado PENDIENTE");
        }

        factura.setEstado(AppConstants.ESTADO_FACTURA_PAGADA);
        factura.setFormaPago(formaPago);
        return facturaRepository.save(factura);
    }
}

