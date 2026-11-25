package com.ejemplo.catalogo.domain.port.out;

import com.ejemplo.catalogo.domain.model.Product;
import java.util.List;
import java.util.Optional;

/** Puerto de salida: lo que la aplicacion necesita de la persistencia de productos. */
public interface ProductRepository {

    List<Product> findAll();

    List<Product> findByNameContainingIgnoreCase(String name);

    Optional<Product> findById(Long id);

    /** Inserta o actualiza y devuelve el estado guardado. */
    Product save(Product product);

    void delete(Product product);
}
