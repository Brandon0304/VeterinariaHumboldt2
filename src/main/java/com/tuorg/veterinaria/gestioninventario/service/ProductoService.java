package com.tuorg.veterinaria.gestioninventario.service;

import com.tuorg.veterinaria.common.exception.BusinessException;
import com.tuorg.veterinaria.common.exception.ResourceNotFoundException;
import com.tuorg.veterinaria.common.util.ValidationUtil;
import com.tuorg.veterinaria.gestioninventario.model.Producto;
import com.tuorg.veterinaria.gestioninventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Producto crear(Producto producto) {
        // Validar SKU único
        if (productoRepository.existsBySku(producto.getSku())) {
            throw new BusinessException("El SKU ya está en uso");
        }

        // Validar precio
        ValidationUtil.validateNonNegativeNumber(
                producto.getPrecioUnitario().doubleValue(), "precio_unitario");

        // Inicializar stock si es null
        if (producto.getStock() == null) {
            producto.setStock(0);
        }

        return productoRepository.save(producto);
    }

    /**
     * Obtiene un producto por su ID.
     * 
     * @param id ID del producto
     * @return Producto encontrado
     */
    @Transactional(readOnly = true)
    public Producto obtener(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }

    /**
     * Obtiene todos los productos.
     * 
     * @return Lista de productos
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
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
        Producto producto = obtener(productoId);
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
        Producto producto = obtener(productoId);
        return producto.getStock() >= cantidad;
    }

    /**
     * Obtiene productos con stock bajo.
     * 
     * @param nivelStock Nivel mínimo de stock para considerar bajo
     * @return Lista de productos con stock bajo
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosConStockBajo(Integer nivelStock) {
        return productoRepository.findProductosConStockBajo(nivelStock);
    }
}

