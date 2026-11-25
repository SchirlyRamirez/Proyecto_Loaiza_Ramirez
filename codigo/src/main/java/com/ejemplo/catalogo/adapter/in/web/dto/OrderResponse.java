package com.ejemplo.catalogo.adapter.in.web.dto;

public record OrderResponse(
        Long id,
        String clienteId,
        Long productoId,
        int cantidad,
        String estado,
        long total
) {
}
