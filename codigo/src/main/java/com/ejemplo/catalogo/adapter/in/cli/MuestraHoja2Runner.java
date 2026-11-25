package com.ejemplo.catalogo.adapter.in.cli;

import com.ejemplo.catalogo.adapter.in.web.controller.OrderController;
import com.ejemplo.catalogo.adapter.in.web.dto.OrderRequest;
import com.ejemplo.catalogo.adapter.in.web.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Muestra de la Hoja 2 (Q3), en dos corridas sobre el mismo pedidos.jsonl.
 * Corrida 1: un pedido de u-1 unidades. Corrida 2 (proceso nuevo): consulta el pedido 1,
 * pide una cantidad que solo se pasa si el stock consumido sobrevivio, y otra que lo deja en 0.
 * Valores de la fila 0: stock 60, umbral u = 10.
 */
@Component
@Order(10)
@Profile("muestra2")
public class MuestraHoja2Runner implements CommandLineRunner {

    private static final long PRODUCTO = 1L;
    private static final int STOCK = 60;
    private static final int U = 10;

    private final OrderController controller;
    private final int corrida;

    public MuestraHoja2Runner(OrderController controller,
                              @Value("${muestra.corrida:1}") int corrida) {
        this.controller = controller;
        this.corrida = corrida;
    }

    @Override
    public void run(String... args) {
        if (corrida == 1) {
            pedir("C-001", U - 1);
        } else {
            imprimir(controller.findById(1L));                // el pedido 1 sobrevivio
            pedir("C-002", STOCK - (U - 1) + 1);              // RECHAZADO: se pasa por 1
            pedir("C-003", STOCK - (U - 1));                  // CONFIRMADO: deja el stock en 0
        }
    }

    private void pedir(String clienteId, int cantidad) {
        imprimir(controller.create(new OrderRequest(clienteId, PRODUCTO, cantidad)).getBody());
    }

    private static void imprimir(OrderResponse o) {
        System.out.println("PEDIDO " + o.id() + " " + o.estado() + " total=" + o.total());
    }
}
