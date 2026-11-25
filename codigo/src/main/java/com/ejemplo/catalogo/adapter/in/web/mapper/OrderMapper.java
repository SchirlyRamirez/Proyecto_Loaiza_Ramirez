package com.ejemplo.catalogo.adapter.in.web.mapper;

import com.ejemplo.catalogo.adapter.in.web.dto.OrderResponse;
import com.ejemplo.catalogo.domain.model.Order;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getClienteId(),
                order.getProductoId(),
                order.getCantidad(),
                order.getEstado().name(),
                order.getTotal());
    }
}
