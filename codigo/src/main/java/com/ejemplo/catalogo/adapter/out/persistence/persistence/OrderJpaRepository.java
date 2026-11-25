package com.ejemplo.catalogo.adapter.out.persistence.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repositorio de Spring Data de pedidos. Interno al adaptador de persistencia. */
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    @Query("select coalesce(sum(o.cantidad), 0L) from OrderJpaEntity o "
            + "where o.productoId = :productoId and o.estado = 'CONFIRMADO'")
    long sumConfirmedQuantity(@Param("productoId") Long productoId);

    @Query("select coalesce(sum(o.total), 0L) from OrderJpaEntity o "
            + "where o.clienteId = :clienteId and o.estado = 'CONFIRMADO'")
    long sumConfirmedTotalByClient(@Param("clienteId") String clienteId);
}
