package com.ejemplo.catalogo.adapter.in.cli;

import com.ejemplo.catalogo.adapter.in.web.controller.OrderController;
import com.ejemplo.catalogo.adapter.in.web.dto.OrderRequest;
import com.ejemplo.catalogo.adapter.in.web.dto.OrderResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Muestra de la Hoja 1. Recorre la arquitectura por su entrada real: llama al
 * OrderController (DTO -> puerto de entrada -> caso de uso -> puerto de salida -> adaptador).
 * Valores de la fila 0: stock 60, umbral u = 10.
 */
@Component
@Order(10)
@Profile("muestra1")
public class MuestraHoja1Runner implements CommandLineRunner {

    private static final long PRODUCTO = 1L;
    private static final int STOCK = 60;
    private static final int U = 10;

    private final OrderController controller;

    public MuestraHoja1Runner(OrderController controller) {
        this.controller = controller;
    }

    @Override
    public void run(String... args) {
        pedir("C-001", U - 1);               // sin descuento: justo por debajo del umbral
        pedir("C-002", U);                   // con descuento: el umbral mismo cuenta (>=)
        pedir("C-001", STOCK - 2 * U + 2);   // RECHAZADO: se pasa por 1 unidad
        pedir("C-003", STOCK - 2 * U + 1);   // CONFIRMADO: deja el stock en 0
    }

    private void pedir(String clienteId, int cantidad) {
        OrderResponse o = controller.create(new OrderRequest(clienteId, PRODUCTO, cantidad)).getBody();
        System.out.println("PEDIDO " + o.id() + " " + o.estado() + " total=" + o.total());
    }
}
