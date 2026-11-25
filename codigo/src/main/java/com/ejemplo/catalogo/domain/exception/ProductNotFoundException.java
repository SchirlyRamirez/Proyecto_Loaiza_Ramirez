package com.ejemplo.catalogo.domain.exception;

/** El negocio la lanza cuando un producto no existe; la web la traduce a 404. */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("No existe el producto con id " + id);
    }
}
