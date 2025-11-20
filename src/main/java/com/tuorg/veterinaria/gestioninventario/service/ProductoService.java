package com.tuorg.veterinaria.gestioninventario.service;

import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.common.util.ValidationUtil;
import com.tuorg.veterinaria.gestioninventario.dto.ProductoRequest;
import com.tuorg.veterinaria.gestioninventario.dto.ProductoResponse;
import com.tuorg.veterinaria.gestioninventario.dto.ProductoUpdateRequest;
import com.tuorg.veterinaria.gestioninventario.model.Producto;
import com.tuorg.veterinaria.gestioninventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de productos del inventario.
 * 
 * Este servicio proporciona métodos para crear, actualizar, eliminar
 * y consultar productos, así como gestionar el stock.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0.0
 */
@Service
public class ProductoService {

    /**
     * Repositorio de productos.
     */
    private final ProductoRepository productoRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param productoRepository Repositorio de productos
     */
    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Crea un nuevo producto.
     * 
     * @param producto Producto a crear
     * @return Producto creado
     */
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsBySku(request.getSku())) {
            throw new BusinessException("El SKU ya está en uso");
        }

        ValidationUtil.validateNonNegativeNumber(
                request.getPrecioUnitario().doubleValue(), "precio_unitario");

        if (request.getStock() != null && request.getStock() < 0) {
            throw new BusinessException("El stock inicial no puede ser negativo");
        }

        Producto producto = new Producto();
        producto.setSku(request.getSku());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setTipo(request.getTipo());
        producto.setPrecioUnitario(request.getPrecioUnitario());
        producto.setUm(request.getUm());
        producto.setStock(request.getStock() != null ? request.getStock() : 0);
        producto.setMetadatos(request.getMetadatos());

        Producto guardado = productoRepository.save(producto);
        return mapToResponse(guardado);
    }

    /**
     * Obtiene un producto por su ID.
     * 
     * @param id ID del producto
     * @return Producto encontrado
     */
    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        Producto producto = obtenerEntidad(id);
        return mapToResponse(producto);
    }

    /**
     * Obtiene todos los productos.
     * 
     * @return Lista de productos
     */
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerTodos() {
        return productoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un producto existente.
     * 
     * @param id ID del producto
     * @param request Datos actualizados del producto
     * @return Producto actualizado
     */
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoUpdateRequest request) {
        Producto producto = obtenerEntidad(id);

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            producto.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            producto.setDescripcion(request.getDescripcion());
        }
        if (request.getTipo() != null) {
            producto.setTipo(request.getTipo());
        }
        if (request.getPrecioUnitario() != null) {
            ValidationUtil.validateNonNegativeNumber(
                    request.getPrecioUnitario().doubleValue(), "precio_unitario");
            producto.setPrecioUnitario(request.getPrecioUnitario());
        }
        if (request.getUm() != null) {
            producto.setUm(request.getUm());
        }
        if (request.getStock() != null) {
            if (request.getStock() < 0) {
                throw new BusinessException("El stock no puede ser negativo");
            }
            producto.setStock(request.getStock());
        }
        if (request.getMetadatos() != null) {
            producto.setMetadatos(request.getMetadatos());
        }

        Producto actualizado = productoRepository.save(producto);
        return mapToResponse(actualizado);
    }

    /**
     * Actualiza el stock de un producto.
     * 
     * @param productoId ID del producto
     * @param delta Cantidad a agregar (positiva) o restar (negativa)
     * @return Producto actualizado
     */
    @Transactional
    public Producto actualizarStock(Long productoId, Integer delta) {
        Producto producto = obtenerEntidad(productoId);
        int nuevoStock = producto.getStock() + delta;

        if (nuevoStock < 0) {
            throw new BusinessException("No hay suficiente stock disponible. Stock actual: " + producto.getStock());
        }

        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    /**
     * Verifica la disponibilidad de stock para una cantidad solicitada.
     * 
     * @param productoId ID del producto
     * @param cantidad Cantidad solicitada
     * @return true si hay stock suficiente, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(Long productoId, Integer cantidad) {
        Producto producto = obtenerEntidad(productoId);
        return producto.getStock() >= cantidad;
    }

    /**
     * Obtiene productos con stock bajo.
     * 
     * @param nivelStock Nivel mínimo de stock para considerar bajo
     * @return Lista de productos con stock bajo
     */
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosConStockBajo(Integer nivelStock) {
        return productoRepository.findProductosConStockBajo(nivelStock)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Producto obtenerEntidad(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }

    private ProductoResponse mapToResponse(Producto producto) {
        Map<String, Object> metadatos = producto.getMetadatos();
        return new ProductoResponse(
                producto.getIdProducto(),
                producto.getSku(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getTipo(),
                producto.getStock(),
                producto.getPrecioUnitario(),
                producto.getUm(),
                metadatos
        );
    }
}

