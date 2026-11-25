package com.ejemplo.catalogo.adapter.out.persistence.persistence;

import com.ejemplo.catalogo.domain.model.Order;
import com.ejemplo.catalogo.domain.model.OrderStatus;
import com.ejemplo.catalogo.domain.port.out.OrderRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Adaptador de salida de pedidos sobre JPA/H2 (Hoja 1). */
@Component
@Transactional(readOnly = true)
public class OrderPersistenceAdapter implements OrderRepository {

    private final OrderJpaRepository repository;

    public OrderPersistenceAdapter(OrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        OrderJpaEntity saved = repository.save(toEntity(order));
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return repository.findById(id).map(OrderPersistenceAdapter::toDomain);
    }

    @Override
    public long sumConfirmedQuantity(Long productoId) {
        return repository.sumConfirmedQuantity(productoId);
    }

    @Override
    public long sumConfirmedTotalByClient(String clienteId) {
        return repository.sumConfirmedTotalByClient(clienteId);
    }

    private static Order toDomain(OrderJpaEntity e) {
        Order order = new Order(e.getClienteId(), e.getProductoId(), e.getCantidad(),
                OrderStatus.valueOf(e.getEstado()), e.getTotal());
        order.setId(e.getId());
        return order;
    }

    private static OrderJpaEntity toEntity(Order o) {
        OrderJpaEntity entity = new OrderJpaEntity(o.getClienteId(), o.getProductoId(),
                o.getCantidad(), o.getEstado().name(), o.getTotal());
        entity.setId(o.getId());
        return entity;
    }
}
