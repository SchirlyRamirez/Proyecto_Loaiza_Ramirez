package com.ejemplo.catalogo.adapter.out.persistence.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio de Spring Data. Interno al adaptador de persistencia. */
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    List<ProductJpaEntity> findByNameContainingIgnoreCase(String name);
}
