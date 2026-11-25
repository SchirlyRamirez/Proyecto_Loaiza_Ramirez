package com.ejemplo.catalogo.domain.port.out;

import com.ejemplo.catalogo.domain.model.Order;
import java.util.Optional;

/**
 * Puerto de salida: lo que la aplicacion necesita de la persistencia de pedidos.
 * Todo lo que cambie de lugar (base de datos, archivo) se enchufa aqui.
 */
public interface OrderRepository {

    /** Guarda el pedido, le asigna el siguiente id consecutivo y lo devuelve. */
    Order save(Order order);

    Optional<Order> findById(Long id);

    /** Suma de cantidades de los pedidos CONFIRMADO de ese producto (stock consumido). */
    long sumConfirmedQuantity(Long productoId);

    /** Suma de los total de los pedidos CONFIRMADO de ese cliente. */
    long sumConfirmedTotalByClient(String clienteId);
}
