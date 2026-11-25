package com.ejemplo.catalogo.adapter.out.persistence.jsonl;

import static org.assertj.core.api.Assertions.assertThat;

import com.ejemplo.catalogo.domain.model.Order;
import com.ejemplo.catalogo.domain.model.OrderStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Q3: el adaptador de archivo conserva pedidos y stock consumido tras "reiniciar". */
class JsonlOrderRepositoryTest {

    @TempDir
    Path dir;

    @Test
    void survivesARestartAndContinuesTheIdSequence() {
        String file = dir.resolve("pedidos.jsonl").toString();

        JsonlOrderRepository firstRun = new JsonlOrderRepository(file);
        Order a = firstRun.save(new Order("C-001", 1L, 9, OrderStatus.CONFIRMADO, 72_000L));
        Order b = firstRun.save(new Order("C-002", 1L, 52, OrderStatus.RECHAZADO, 0L));

        // "Reinicio": un adaptador nuevo sobre el mismo archivo
        JsonlOrderRepository secondRun = new JsonlOrderRepository(file);
        Order c = secondRun.save(new Order("C-003", 1L, 51, OrderStatus.CONFIRMADO, 387_600L));

        assertThat(a.getId()).isEqualTo(1L);
        assertThat(b.getId()).isEqualTo(2L);
        assertThat(c.getId()).isEqualTo(3L);   // la secuencia continua
        assertThat(secondRun.findById(1L)).isPresent();
        assertThat(secondRun.findById(2L).get().getEstado()).isEqualTo(OrderStatus.RECHAZADO);
    }

    @Test
    void rejectedOrdersDoNotCountAsConsumedStock() {
        JsonlOrderRepository repo = new JsonlOrderRepository(dir.resolve("p.jsonl").toString());
        repo.save(new Order("C-001", 1L, 9, OrderStatus.CONFIRMADO, 72_000L));
        repo.save(new Order("C-002", 1L, 52, OrderStatus.RECHAZADO, 0L));

        assertThat(repo.sumConfirmedQuantity(1L)).isEqualTo(9L);
        assertThat(repo.sumConfirmedTotalByClient("C-001")).isEqualTo(72_000L);
        assertThat(repo.sumConfirmedTotalByClient("C-002")).isZero();
    }

    @Test
    void writesOneJsonObjectPerLine() throws Exception {
        Path file = dir.resolve("lineas.jsonl");
        JsonlOrderRepository repo = new JsonlOrderRepository(file.toString());
        repo.save(new Order("C-001", 1L, 9, OrderStatus.CONFIRMADO, 72_000L));
        repo.save(new Order("C-002", 1L, 52, OrderStatus.RECHAZADO, 0L));

        assertThat(Files.readAllLines(file)).containsExactly(
                "{\"id\":1,\"clienteId\":\"C-001\",\"productoId\":1,\"cantidad\":9,"
                        + "\"estado\":\"CONFIRMADO\",\"total\":72000}",
                "{\"id\":2,\"clienteId\":\"C-002\",\"productoId\":1,\"cantidad\":52,"
                        + "\"estado\":\"RECHAZADO\",\"total\":0}");
    }

    @Test
    void aMissingFileMeansNoOrdersYet() {
        JsonlOrderRepository repo = new JsonlOrderRepository(dir.resolve("nada.jsonl").toString());

        assertThat(repo.findById(1L)).isEmpty();
        assertThat(repo.sumConfirmedQuantity(1L)).isZero();
    }
}
