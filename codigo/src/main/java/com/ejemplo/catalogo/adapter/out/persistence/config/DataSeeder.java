package com.ejemplo.catalogo.adapter.out.persistence.config;

import com.ejemplo.catalogo.adapter.out.persistence.persistence.ProductJpaEntity;
import com.ejemplo.catalogo.adapter.out.persistence.persistence.ProductJpaRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Datos iniciales de la tienda. Variante fila 0: un unico producto,
 * Cuaderno, productoId 1, precio 8.000 y stock inicial 60.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductJpaRepository repository;

    public DataSeeder(ProductJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.save(new ProductJpaEntity("Cuaderno", "Cuaderno de la tienda",
                new BigDecimal("8000.00"), 60, "papeleria"));
    }
}
