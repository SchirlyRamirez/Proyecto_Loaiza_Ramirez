package com.ejemplo.catalogo.domain.exception;

/** Se consulto un pedido por un id que no existe; la web la traduce a 404. */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("No existe el pedido con id " + id);
    }
}
