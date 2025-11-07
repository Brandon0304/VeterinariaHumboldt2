package com.tuorg.veterinaria.prestacioneservicios.service;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.gestioninventario.service.MovimientoInventarioService;
import com.tuorg.veterinaria.prestacioneservicios.model.Cita;
import com.tuorg.veterinaria.prestacioneservicios.model.Factura;
import com.tuorg.veterinaria.prestacioneservicios.model.Servicio;
import com.tuorg.veterinaria.prestacioneservicios.model.ServicioPrestado;
import com.tuorg.veterinaria.prestacioneservicios.repository.CitaRepository;
import com.tuorg.veterinaria.prestacioneservicios.repository.ServicioPrestadoRepository;
import com.tuorg.veterinaria.prestacioneservicios.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de servicios prestados.
 * 
 * Este servicio implementa el patrón Factory/Builder para crear servicios prestados
 * y maneja la transaccionalidad compleja: crear servicio prestado, actualizar stock,
 * generar factura y marcar cita como realizada.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class ServicioPrestadoService {

    /**
     * Repositorio de servicios prestados.
     */
    private final ServicioPrestadoRepository servicioPrestadoRepository;

    /**
     * Repositorio de citas.
     */
    private final CitaRepository citaRepository;

    /**
     * Repositorio de servicios.
     */
    private final ServicioRepository servicioRepository;

    /**
     * Servicio de movimientos de inventario.
     */
    private final MovimientoInventarioService movimientoInventarioService;

    /**
     * Servicio de facturas.
     */
    private final FacturaService facturaService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param servicioPrestadoRepository Repositorio de servicios prestados
     * @param citaRepository Repositorio de citas
     * @param servicioRepository Repositorio de servicios
     * @param movimientoInventarioService Servicio de movimientos de inventario
     * @param facturaService Servicio de facturas
     */
    @Autowired
    public ServicioPrestadoService(
            ServicioPrestadoRepository servicioPrestadoRepository,
            CitaRepository citaRepository,
            ServicioRepository servicioRepository,
            MovimientoInventarioService movimientoInventarioService,
            FacturaService facturaService) {
        this.servicioPrestadoRepository = servicioPrestadoRepository;
        this.citaRepository = citaRepository;
        this.servicioRepository = servicioRepository;
        this.movimientoInventarioService = movimientoInventarioService;
        this.facturaService = facturaService;
    }

    /**
     * Registra la ejecución de un servicio (transaccional complejo).
     * 
     * Esta operación es transaccional y realiza múltiples acciones:
     * 1. Crea el servicio prestado
     * 2. Actualiza el stock del inventario (consume insumos)
     * 3. Genera la factura
     * 4. Marca la cita como REALIZADA
     * 
     * Si alguna de estas acciones falla, se revierte toda la transacción.
     * 
     * @param servicioPrestado Servicio prestado a registrar
     * @return ServicioPrestado creado
     */
    @Transactional
    public ServicioPrestado registrarEjecucion(ServicioPrestado servicioPrestado) {
        // Validar que la cita exista y esté en estado PROGRAMADA
        Cita cita = citaRepository.findById(servicioPrestado.getCita().getIdCita())
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", 
                        servicioPrestado.getCita().getIdCita()));

        if (!AppConstants.ESTADO_CITA_PROGRAMADA.equals(cita.getEstado())) {
            throw new BusinessException("Solo se pueden registrar servicios para citas en estado PROGRAMADA");
        }

        // Validar que el servicio exista
        Servicio servicio = servicioRepository.findById(servicioPrestado.getServicio().getIdServicio())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", 
                        servicioPrestado.getServicio().getIdServicio()));

        // Establecer fecha de ejecución si no está establecida
        if (servicioPrestado.getFechaEjecucion() == null) {
            servicioPrestado.setFechaEjecucion(LocalDateTime.now());
        }

        // Calcular costo total si no está establecido
        if (servicioPrestado.getCostoTotal() == null) {
            servicioPrestado.setCostoTotal(servicio.getPrecioBase());
        }

        // Guardar servicio prestado
        ServicioPrestado servicioPrestadoGuardado = servicioPrestadoRepository.save(servicioPrestado);

        // TODO: Consumir insumos del inventario según servicioPrestado.getInsumosConsumidos()
        // Esto debe mapearse a MovimientoInventario usando movimientoInventarioService.registrarSalida()

        // Generar factura
        Factura factura = new Factura();
        factura.setCliente(cita.getPaciente().getCliente());
        factura.setTotal(servicioPrestado.getCostoTotal());
        factura.setContenido("{\"servicioPrestadoId\": " + servicioPrestadoGuardado.getIdPrestado() + "}");
        facturaService.crear(factura);

        // Marcar cita como realizada
        cita.setEstado(AppConstants.ESTADO_CITA_REALIZADA);
        citaRepository.save(cita);

        return servicioPrestadoGuardado;
    }

    /**
     * Genera un resumen del servicio prestado.
     * 
     * @param servicioPrestadoId ID del servicio prestado
     * @return Resumen en formato String
     */
    @Transactional(readOnly = true)
    public String generarResumen(Long servicioPrestadoId) {
        ServicioPrestado servicioPrestado = servicioPrestadoRepository.findById(servicioPrestadoId)
                .orElseThrow(() -> new ResourceNotFoundException("ServicioPrestado", "id", servicioPrestadoId));

        StringBuilder resumen = new StringBuilder();
        resumen.append("Resumen de Servicio Prestado\n");
        resumen.append("Servicio: ").append(servicioPrestado.getServicio().getNombre()).append("\n");
        resumen.append("Fecha: ").append(servicioPrestado.getFechaEjecucion()).append("\n");
        resumen.append("Costo Total: $").append(servicioPrestado.getCostoTotal()).append("\n");
        if (servicioPrestado.getObservaciones() != null) {
            resumen.append("Observaciones: ").append(servicioPrestado.getObservaciones()).append("\n");
        }

        return resumen.toString();
    }

    /**
     * Obtiene todos los servicios prestados de una cita.
     * 
     * @param citaId ID de la cita
     * @return Lista de servicios prestados
     */
    @Transactional(readOnly = true)
    public List<ServicioPrestado> obtenerPorCita(Long citaId) {
        return servicioPrestadoRepository.findByCitaId(citaId);
    }
}

