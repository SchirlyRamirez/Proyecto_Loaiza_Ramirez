package com.ejemplo.catalogo.domain.exception;

/**
 * RN-5: error de validacion (cantidad <= 0 o producto inexistente).
 * No es un pedido RECHAZADO: no se registra y no consume id.
 */
public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }
}
