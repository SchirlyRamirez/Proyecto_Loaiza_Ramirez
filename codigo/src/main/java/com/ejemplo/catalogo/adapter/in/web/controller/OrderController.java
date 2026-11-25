package com.ejemplo.catalogo.adapter.in.web.controller;

import com.ejemplo.catalogo.adapter.in.web.dto.ClientTotalResponse;
import com.ejemplo.catalogo.adapter.in.web.dto.OrderRequest;
import com.ejemplo.catalogo.adapter.in.web.dto.OrderResponse;
import com.ejemplo.catalogo.adapter.in.web.mapper.OrderMapper;
import com.ejemplo.catalogo.domain.model.Order;
import com.ejemplo.catalogo.domain.port.in.OrderService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // POST /api/orders -> 201; el pedido puede quedar CONFIRMADO o RECHAZADO (ambos tienen id)
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        Order created = service.register(
                request.clienteId(), request.productoId(), request.cantidad());
        OrderResponse response = OrderMapper.toResponse(created);
        return ResponseEntity.created(URI.create("/api/orders/" + response.id())).body(response);
    }

    // GET /api/orders/1
    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable Long id) {
        return OrderMapper.toResponse(service.findById(id));
    }

    // GET /api/orders/clients/C-001/total
    @GetMapping("/clients/{clienteId}/total")
    public ClientTotalResponse totalByClient(@PathVariable String clienteId) {
        return new ClientTotalResponse(clienteId, service.totalPurchasedBy(clienteId));
    }
}
