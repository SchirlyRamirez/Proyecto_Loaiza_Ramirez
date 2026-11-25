package com.ejemplo.catalogo.adapter.out.persistence.jsonl;

import com.ejemplo.catalogo.domain.model.Order;
import com.ejemplo.catalogo.domain.model.OrderStatus;
import com.ejemplo.catalogo.domain.port.out.OrderRepository;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida de pedidos sobre un archivo JSONL (Hoja 2, Q3): un objeto JSON
 * por linea. Reemplaza a OrderPersistenceAdapter (JPA/H2) sin que el caso de uso lo note.
 * El archivo se resuelve respecto al directorio desde el que se corre la aplicacion.
 * Cada operacion lee el archivo completo: simple y suficiente para este ejercicio.
 */
@Component
public class JsonlOrderRepository implements OrderRepository {

    private static final Pattern LINE = Pattern.compile(
            "\\{\"id\":(\\d+),\"clienteId\":\"((?:[^\"\\\\]|\\\\.)*)\","
                    + "\"productoId\":(\\d+),\"cantidad\":(\\d+),"
                    + "\"estado\":\"([A-Z]+)\",\"total\":(\\d+)\\}");

    private final Path file;

    public JsonlOrderRepository(@Value("${tienda.pedidos-archivo:pedidos.jsonl}") String path) {
        this.file = Path.of(path);
    }

    @Override
    public synchronized Order save(Order order) {
        List<Order> all = readAll();
        long nextId = all.stream().mapToLong(Order::getId).max().orElse(0L) + 1;
        order.setId(nextId);
        try {
            Files.writeString(file, toJson(order) + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + file, e);
        }
        return order;
    }

    @Override
    public synchronized Optional<Order> findById(Long id) {
        return readAll().stream().filter(o -> o.getId().equals(id)).findFirst();
    }

    @Override
    public synchronized long sumConfirmedQuantity(Long productoId) {
        return readAll().stream()
                .filter(o -> o.getEstado() == OrderStatus.CONFIRMADO)
                .filter(o -> o.getProductoId().equals(productoId))
                .mapToLong(Order::getCantidad)
                .sum();
    }

    @Override
    public synchronized long sumConfirmedTotalByClient(String clienteId) {
        return readAll().stream()
                .filter(o -> o.getEstado() == OrderStatus.CONFIRMADO)
                .filter(o -> o.getClienteId().equals(clienteId))
                .mapToLong(Order::getTotal)
                .sum();
    }

    private List<Order> readAll() {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try {
            List<Order> result = new ArrayList<>();
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    result.add(fromJson(line));
                }
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + file, e);
        }
    }

    private static String toJson(Order o) {
        return "{\"id\":" + o.getId()
                + ",\"clienteId\":\"" + escape(o.getClienteId()) + "\""
                + ",\"productoId\":" + o.getProductoId()
                + ",\"cantidad\":" + o.getCantidad()
                + ",\"estado\":\"" + o.getEstado().name() + "\""
                + ",\"total\":" + o.getTotal() + "}";
    }

    private static Order fromJson(String line) {
        Matcher m = LINE.matcher(line.trim());
        if (!m.matches()) {
            throw new IllegalStateException("Linea invalida en el archivo de pedidos: " + line);
        }
        Order order = new Order(unescape(m.group(2)), Long.valueOf(m.group(3)),
                Integer.parseInt(m.group(4)), OrderStatus.valueOf(m.group(5)),
                Long.parseLong(m.group(6)));
        order.setId(Long.valueOf(m.group(1)));
        return order;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                sb.append(s.charAt(++i));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
