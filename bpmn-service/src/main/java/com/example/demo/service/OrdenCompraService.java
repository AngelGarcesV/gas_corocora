package com.example.demo.service;

import com.example.demo.model.OrdenCompra;
import com.example.demo.repository.OrdenCompraRepository;
import com.example.demo.util.IdGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Servicio para gestionar órdenes de compra
 * Incluye lógica para creación, actualización y auditoría
 */
@Service
public class OrdenCompraService {

    private static final Logger logger = LoggerFactory.getLogger(OrdenCompraService.class);

    @Autowired
    private OrdenCompraRepository repository;

    public OrdenCompra crearOrdenDesdeNecesidad(
            Integer cantidadEstimada,
            String fechaRequerido,
            String justificacionNecesidad,
            String observaciones,
            String necesidadDetectada) {

        logger.info("Creando nueva orden de compra con cantidad: {} kg", cantidadEstimada);

        OrdenCompra orden = new OrdenCompra();
        String ordenId = IdGeneratorUtil.generarOrdenId();
        orden.setOrdenId(ordenId);
        orden.setCantidadGas(cantidadEstimada);
        orden.setJustificacionNecesidad(justificacionNecesidad);
        orden.setObservaciones(observaciones);
        orden.setEstado("NECESIDAD_EVALUADA");

        // Establecer fechas
        orden.setFechaCreacion(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        orden.setFechaActualizacion(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        OrdenCompra guardada = repository.save(orden);
        logger.info("Orden creada exitosamente: {}", ordenId);
        
        return guardada;
    }

    public void actualizarEstado(String ordenId, String nuevoEstado) {
        logger.info("Actualizando estado de orden {} a {}", ordenId, nuevoEstado);
        
        OrdenCompra orden = repository.findByOrdenId(ordenId);
        if (orden != null) {
            orden.setEstado(nuevoEstado.toUpperCase());
            orden.setFechaActualizacion(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            repository.save(orden);
            logger.info("Estado de orden {} actualizado", ordenId);
        } else {
            logger.error("Orden no encontrada: {}", ordenId);
        }
    }

    public void actualizarConOferta(String ordenId, String proveedor, Integer cantidad,
                                    Double costo, Integer tiempoEntrega) {
        logger.info("Actualizando orden {} con oferta del proveedor: {}", ordenId, proveedor);
        
        OrdenCompra orden = repository.findByOrdenId(ordenId);
        if (orden != null) {
            orden.setProveedor(proveedor);
            orden.setCantidadGas(cantidad);
            orden.setCostoTotal(costo);
            orden.setTiempoEntregaDias(tiempoEntrega);
            orden.setEstado("OFERTA_SELECCIONADA");
            orden.setFechaActualizacion(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            repository.save(orden);
            logger.info("Orden {} actualizada con oferta", ordenId);
        } else {
            logger.error("Orden no encontrada: {}", ordenId);
        }
    }

    /**
     * Actualiza una orden completa
     */
    public OrdenCompra actualizarOrden(OrdenCompra orden) {
        logger.info("Actualizando orden: {}", orden.getOrdenId());
        orden.setFechaActualizacion(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        OrdenCompra guardada = repository.save(orden);
        logger.info("Orden actualizada correctamente");
        return guardada;
    }

    /**
     * Obtiene una orden por ID
     */
    public OrdenCompra obtenerOrdenPorId(String ordenId) {
        logger.debug("Buscando orden: {}", ordenId);
        return repository.findByOrdenId(ordenId);
    }

    /**
     * Obtiene todas las órdenes
     */
    public List<OrdenCompra> obtenerTodasLasOrdenes() {
        logger.debug("Obteniendo todas las órdenes");
        return repository.findAll();
    }

    /**
     * Obtiene órdenes por estado
     */
    public List<OrdenCompra> obtenerOrdenesPorEstado(String estado) {
        logger.debug("Buscando órdenes con estado: {}", estado);
        // Buscar manualmente si el método no existe en el repository
        List<OrdenCompra> todas = repository.findAll();
        return todas.stream()
                .filter(o -> o.getEstado() != null && o.getEstado().equals(estado))
                .collect(java.util.stream.Collectors.toList());
    }
}