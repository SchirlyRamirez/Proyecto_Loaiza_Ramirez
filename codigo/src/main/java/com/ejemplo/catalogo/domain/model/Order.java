package com.ejemplo.catalogo.domain.model;

/** Pedido de una linea: un producto y una cantidad. Total en pesos enteros. */
public class Order {

    private Long id;
    private final String clienteId;
    private final Long productoId;
    private final int cantidad;
    private final OrderStatus estado;
    private final long total;

    public Order(String clienteId, Long productoId, int cantidad,
                 OrderStatus estado, long total) {
        this.clienteId = clienteId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.estado = estado;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    // El id consecutivo lo asigna la persistencia al guardar
    public void setId(Long id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public OrderStatus getEstado() {
        return estado;
    }

    public long getTotal() {
        return total;
    }
}
