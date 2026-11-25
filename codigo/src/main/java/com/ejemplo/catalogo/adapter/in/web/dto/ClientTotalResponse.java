package com.ejemplo.catalogo.adapter.in.web.dto;

/** RN-4: total comprado por un cliente (suma de sus pedidos CONFIRMADO). */
public record ClientTotalResponse(String clienteId, long total) {
}
