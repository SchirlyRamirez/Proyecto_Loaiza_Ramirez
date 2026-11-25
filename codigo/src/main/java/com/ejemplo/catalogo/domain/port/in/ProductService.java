package com.ejemplo.catalogo.domain.port.in;

import com.ejemplo.catalogo.domain.model.Product;
import java.util.List;

/** Puerto de entrada: operaciones de productos, en terminos de dominio. */
public interface ProductService {

    /** Lista todos los productos, o filtra por nombre si q no esta en blanco. */
    List<Product> findAll(String q);

    /** Busca un producto por id o lanza ProductNotFoundException (404). */
    Product findById(Long id);

    /** Crea un producto y lo devuelve con su id. */
    Product create(Product product);

    /** Actualiza un producto existente o lanza ProductNotFoundException (404). */
    Product update(Long id, Product changes);

    /** Borra un producto existente o lanza ProductNotFoundException (404). */
    void delete(Long id);
}
