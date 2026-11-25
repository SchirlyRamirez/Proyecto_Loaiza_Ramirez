package com.ejemplo.catalogo.domain.port.in;

import com.ejemplo.catalogo.domain.model.Order;

/** Puerto de entrada: casos de uso de pedidos, en terminos de dominio. */
public interface OrderService {

    /**
     * Registra un pedido. Devuelve el pedido CONFIRMADO o RECHAZADO (RN-3) con su id.
     * Lanza InvalidOrderException si la cantidad es <= 0 o el producto no existe (RN-5).
     */
    Order register(String clienteId, Long productoId, Integer cantidad);

    /** RN-4: consulta un pedido por id o lanza OrderNotFoundException. */
    Order findById(Long id);

    /** RN-4: suma de los total de los pedidos CONFIRMADO del cliente. */
    long totalPurchasedBy(String clienteId);
}
