package com.ejemplo.catalogo.adapter.out.persistence.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ejemplo.catalogo.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductPersistenceAdapterTest {

    @Mock
    private ProductJpaRepository jpaRepository;

    @InjectMocks
    private ProductPersistenceAdapter adapter;

    private static ProductJpaEntity entity(long id, String name) {
        ProductJpaEntity e = new ProductJpaEntity(name, "d", new BigDecimal("8000.00"), 60, "papeleria");
        e.setId(id);
        return e;
    }

    @Test
    void findByIdMapsEntityToDomainModel() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity(1L, "Cuaderno")));

        Optional<Product> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Cuaderno");
        assertThat(result.get().getPrice()).isEqualByComparingTo("8000.00");
    }

    @Test
    void findByIdReturnsEmptyWhenMissing() {
        when(jpaRepository.findById(5L)).thenReturn(Optional.empty());

        assertThat(adapter.findById(5L)).isEmpty();
    }

    @Test
    void findAllMapsEveryEntity() {
        when(jpaRepository.findAll()).thenReturn(List.of(entity(1L, "A"), entity(2L, "B")));

        assertThat(adapter.findAll()).extracting(Product::getName).containsExactly("A", "B");
    }

    @Test
    void findByNameDelegatesToSpringData() {
        when(jpaRepository.findByNameContainingIgnoreCase("cu"))
                .thenReturn(List.of(entity(1L, "Cuaderno")));

        assertThat(adapter.findByNameContainingIgnoreCase("cu")).hasSize(1);
    }

    @Test
    void saveMapsBackTheGeneratedId() {
        when(jpaRepository.save(any(ProductJpaEntity.class))).thenAnswer(inv -> {
            ProductJpaEntity e = inv.getArgument(0);
            e.setId(7L);
            return e;
        });
        Product product = new Product("Nuevo", "d", new BigDecimal("1.00"), 3, "hogar");

        Product saved = adapter.save(product);

        assertThat(saved.getId()).isEqualTo(7L);
        verify(jpaRepository).save(any(ProductJpaEntity.class));
    }
}
