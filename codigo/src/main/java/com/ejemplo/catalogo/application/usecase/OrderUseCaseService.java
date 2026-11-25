package com.ejemplo.catalogo.application.usecase;

import com.ejemplo.catalogo.domain.exception.InvalidOrderException;
import com.ejemplo.catalogo.domain.exception.OrderNotFoundException;
import com.ejemplo.catalogo.domain.model.Order;
import com.ejemplo.catalogo.domain.model.OrderStatus;
import com.ejemplo.catalogo.domain.model.Product;
import com.ejemplo.catalogo.domain.port.in.OrderService;
import com.ejemplo.catalogo.domain.port.out.OrderRepository;
import com.ejemplo.catalogo.domain.port.out.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso de pedidos. Aqui viven RN-1 (descuento por volumen), RN-2 (total),
 * RN-3 (stock) y RN-5 (validacion). Depende solo de los puertos de salida.
 * El umbral u y el descuento d vienen de la configuracion (fila 0: 10 y 5).
 */
@Service
@Transactional(readOnly = true)
public class OrderUseCaseService implements OrderService {

    private final OrderRepository orders;
    private final ProductRepository products;
    private final int volumeThreshold;
    private final int discountPercent;

    public OrderUseCaseService(OrderRepository orders,
                               ProductRepository products,
                               @Value("${tienda.umbral-volumen}") int volumeThreshold,
                               @Value("${tienda.descuento}") int discountPercent) {
        this.orders = orders;
        this.products = products;
        this.volumeThreshold = volumeThreshold;
        this.discountPercent = discountPercent;
    }

    @Override
    @Transactional
    public Order register(String clienteId, Long productoId, Integer cantidad) {
        // RN-5: errores de validacion; no se registra nada y no se consume id
        if (clienteId == null || clienteId.isBlank()) {
            throw new InvalidOrderException("El cliente es obligatorio");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new InvalidOrderException("La cantidad debe ser mayor que 0");
        }
        Product product = products.findById(productoId)
                .orElseThrow(() -> new InvalidOrderException(
                        "No existe el producto con id " + productoId));

        // RN-3: el stock disponible se deriva de los pedidos CONFIRMADO
        long available = product.getStock() - orders.sumConfirmedQuantity(productoId);
        if (cantidad > available) {
            return orders.save(new Order(clienteId, productoId, cantidad,
                    OrderStatus.RECHAZADO, 0L));
        }

        long total = calculateTotal(cantidad, product.getPrice().longValueExact());
        return orders.save(new Order(clienteId, productoId, cantidad,
                OrderStatus.CONFIRMADO, total));
    }

    @Override
    public Order findById(Long id) {
        return orders.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public long totalPurchasedBy(String clienteId) {
        return orders.sumConfirmedTotalByClient(clienteId);
    }

    // RN-1: cantidad >= u recibe d %; si no, 0 %
    private int discountFor(int cantidad) {
        return cantidad >= volumeThreshold ? discountPercent : 0;
    }

    // RN-2: total = cantidad x precio x (100 - descuento) / 100, en pesos enteros
    private long calculateTotal(int cantidad, long price) {
        return cantidad * price * (100 - discountFor(cantidad)) / 100;
    }
}
