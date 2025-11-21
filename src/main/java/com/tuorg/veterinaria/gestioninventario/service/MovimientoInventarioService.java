package com.tuorg.veterinaria.gestioninventario.service;

import com.tuorg.veterinaria.common.constants.AppConstants;
import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.gestioninventario.dto.MovimientoEntradaRequest;
import com.tuorg.veterinaria.gestioninventario.dto.MovimientoInventarioResponse;
import com.tuorg.veterinaria.gestioninventario.dto.MovimientoSalidaRequest;
import com.tuorg.veterinaria.gestioninventario.model.MovimientoInventario;
import com.tuorg.veterinaria.gestioninventario.model.Producto;
import com.tuorg.veterinaria.gestioninventario.model.Proveedor;
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
    public MovimientoInventarioResponse registrarEntrada(MovimientoEntradaRequest request) {
        if (request.getCantidad() <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que cero");
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductoId()));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(AppConstants.TIPO_MOVIMIENTO_ENTRADA);
        movimiento.setCantidad(request.getCantidad());
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setReferencia(request.getReferencia());

        if (request.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", request.getProveedorId()));
            movimiento.setProveedor(proveedor);
        }

        if (request.getUsuarioId() != null) {
            movimiento.setUsuario(usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.getUsuarioId())));
        }

        MovimientoInventario guardado = movimientoInventarioRepository.save(movimiento);
        Producto actualizado = productoService.actualizarStock(request.getProductoId(), request.getCantidad());
        return mapToResponse(guardado, actualizado.getStock());
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
    public MovimientoInventarioResponse registrarSalida(MovimientoSalidaRequest request) {
        if (request.getCantidad() <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que cero");
        }

        if (!productoService.verificarDisponibilidad(request.getProductoId(), request.getCantidad())) {
            Producto producto = productoService.obtenerEntidad(request.getProductoId());
            throw new BusinessException("Stock insuficiente. Stock disponible: " + producto.getStock());
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductoId()));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(AppConstants.TIPO_MOVIMIENTO_SALIDA);
        movimiento.setCantidad(request.getCantidad());
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setReferencia(request.getReferencia());

        if (request.getUsuarioId() != null) {
            movimiento.setUsuario(usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.getUsuarioId())));
        }

        MovimientoInventario guardado = movimientoInventarioRepository.save(movimiento);
        Producto actualizado = productoService.actualizarStock(request.getProductoId(), -request.getCantidad());
        return mapToResponse(guardado, actualizado.getStock());
    }

    /**
     * Obtiene todos los movimientos de un producto.
     * 
     * @param productoId ID del producto
     * @return Lista de movimientos del producto
     */
    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> obtenerPorProducto(Long productoId) {
        return movimientoInventarioRepository.findByProductoId(productoId)
                .stream()
                .map(mov -> mapToResponse(mov, null))
                .toList();
    }

    /**
     * Obtiene movimientos en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de movimientos en el rango especificado
     */
    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> obtenerPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoInventarioRepository.findByFechaBetween(fechaInicio, fechaFin)
                .stream()
                .map(mov -> mapToResponse(mov, null))
                .toList();
    }

    private MovimientoInventarioResponse mapToResponse(MovimientoInventario movimiento, Integer stockResultante) {
        MovimientoInventarioResponse.ProductoSummary productoSummary = new MovimientoInventarioResponse.ProductoSummary(
                movimiento.getProducto().getIdProducto(),
                movimiento.getProducto().getSku(),
                movimiento.getProducto().getNombre()
        );

        MovimientoInventarioResponse.ProveedorSummary proveedorSummary = null;
        if (movimiento.getProveedor() != null) {
            proveedorSummary = new MovimientoInventarioResponse.ProveedorSummary(
                    movimiento.getProveedor().getIdProveedor(),
                    movimiento.getProveedor().getNombre()
            );
        }

        MovimientoInventarioResponse.UsuarioSummary usuarioSummary = null;
        if (movimiento.getUsuario() != null) {
            Usuario usuario = movimiento.getUsuario();
            usuarioSummary = new MovimientoInventarioResponse.UsuarioSummary(
                    usuario.getIdUsuario(),
                    usuario.getUsername(),
                    usuario.getNombre(),
                    usuario.getApellido()
            );
        }

        return new MovimientoInventarioResponse(
                movimiento.getIdMovimiento(),
                movimiento.getTipoMovimiento(),
                movimiento.getCantidad(),
                movimiento.getFecha(),
                movimiento.getReferencia(),
                productoSummary,
                proveedorSummary,
                usuarioSummary,
                stockResultante
        );
    }
}


