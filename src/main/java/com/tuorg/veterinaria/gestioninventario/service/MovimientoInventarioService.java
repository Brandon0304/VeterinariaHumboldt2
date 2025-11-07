package com.tuorg.veterinaria.gestioninventario.service;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.gestioninventario.model.MovimientoInventario;
import com.tuorg.veterinaria.gestioninventario.model.Producto;
import com.tuorg.veterinaria.gestioninventario.repository.MovimientoInventarioRepository;
import com.tuorg.veterinaria.gestioninventario.repository.ProductoRepository;
import com.tuorg.veterinaria.gestioninventario.repository.ProveedorRepository;
import com.tuorg.veterinaria.gestionusuarios.model.Usuario;
import com.tuorg.veterinaria.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de movimientos de inventario.
 * 
 * Este servicio implementa el patrón Command para registrar
 * movimientos de inventario de forma transaccional.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class MovimientoInventarioService {

    /**
     * Repositorio de movimientos de inventario.
     */
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    /**
     * Repositorio de productos.
     */
    private final ProductoRepository productoRepository;

    /**
     * Repositorio de proveedores.
     */
    private final ProveedorRepository proveedorRepository;

    /**
     * Repositorio de usuarios.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Servicio de productos.
     */
    private final ProductoService productoService;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param movimientoInventarioRepository Repositorio de movimientos
     * @param productoRepository Repositorio de productos
     * @param proveedorRepository Repositorio de proveedores
     * @param usuarioRepository Repositorio de usuarios
     * @param productoService Servicio de productos
     */
    @Autowired
    public MovimientoInventarioService(
            MovimientoInventarioRepository movimientoInventarioRepository,
            ProductoRepository productoRepository,
            ProveedorRepository proveedorRepository,
            UsuarioRepository usuarioRepository,
            ProductoService productoService) {
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoService = productoService;
    }

    /**
     * Registra una entrada de inventario (patrón Command).
     * 
     * Esta operación es transaccional: registra el movimiento y
     * actualiza el stock del producto.
     * 
     * @param productoId ID del producto
     * @param proveedorId ID del proveedor (opcional)
     * @param cantidad Cantidad de entrada
     * @param referencia Referencia del movimiento
     * @param usuarioId ID del usuario que realiza el movimiento
     * @return MovimientoInventario creado
     */
    @Transactional
    public MovimientoInventario registrarEntrada(Long productoId, Long proveedorId,
                                                  Integer cantidad, String referencia, Long usuarioId) {
        // Validar cantidad
        if (cantidad <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que cero");
        }

        // Obtener producto
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));

        // Obtener proveedor si se especificó
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(AppConstants.TIPO_MOVIMIENTO_ENTRADA);
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setReferencia(referencia);

        if (proveedorId != null) {
            movimiento.setProveedor(proveedorRepository.findById(proveedorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", proveedorId)));
        }

        if (usuarioId != null) {
            movimiento.setUsuario(usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId)));
        }

        // Guardar movimiento
        MovimientoInventario movimientoGuardado = movimientoInventarioRepository.save(movimiento);

        // Actualizar stock (incrementar)
        productoService.actualizarStock(productoId, cantidad);

        return movimientoGuardado;
    }

    /**
     * Registra una salida de inventario (patrón Command).
     * 
     * Esta operación es transaccional: verifica stock disponible,
     * registra el movimiento y actualiza el stock del producto.
     * 
     * @param productoId ID del producto
     * @param cantidad Cantidad de salida
     * @param referencia Referencia del movimiento
     * @param usuarioId ID del usuario que realiza el movimiento
     * @return MovimientoInventario creado
     */
    @Transactional
    public MovimientoInventario registrarSalida(Long productoId, Integer cantidad,
                                                 String referencia, Long usuarioId) {
        // Validar cantidad
        if (cantidad <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que cero");
        }

        // Verificar disponibilidad de stock
        if (!productoService.verificarDisponibilidad(productoId, cantidad)) {
            Producto producto = productoService.obtener(productoId);
            throw new BusinessException("Stock insuficiente. Stock disponible: " + producto.getStock());
        }

        // Obtener producto
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));

        // Crear movimiento
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(AppConstants.TIPO_MOVIMIENTO_SALIDA);
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setReferencia(referencia);

        if (usuarioId != null) {
            movimiento.setUsuario(usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId)));
        }

        // Guardar movimiento
        MovimientoInventario movimientoGuardado = movimientoInventarioRepository.save(movimiento);

        // Actualizar stock (decrementar)
        productoService.actualizarStock(productoId, -cantidad);

        return movimientoGuardado;
    }

    /**
     * Obtiene todos los movimientos de un producto.
     * 
     * @param productoId ID del producto
     * @return Lista de movimientos del producto
     */
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerPorProducto(Long productoId) {
        return movimientoInventarioRepository.findByProductoId(productoId);
    }

    /**
     * Obtiene movimientos en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de movimientos en el rango especificado
     */
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoInventarioRepository.findByFechaBetween(fechaInicio, fechaFin);
    }
}

