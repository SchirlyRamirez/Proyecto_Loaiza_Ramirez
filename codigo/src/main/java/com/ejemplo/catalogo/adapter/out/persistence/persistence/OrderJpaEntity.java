package com.ejemplo.catalogo.adapter.out.persistence.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Fila de la tabla orders ("order" es palabra reservada en SQL). */
@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String clienteId;

    @Column(nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(nullable = false)
    private long total;

    protected OrderJpaEntity() {
        // JPA necesita un constructor vacio
    }

    public OrderJpaEntity(String clienteId, Long productoId, int cantidad,
                          String estado, long total) {
        this.clienteId = clienteId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.estado = estado;
        this.total = total;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClienteId() { return clienteId; }
    public Long getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
    public String getEstado() { return estado; }
    public long getTotal() { return total; }
}
