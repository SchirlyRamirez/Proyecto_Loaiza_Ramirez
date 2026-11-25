package com.ejemplo.catalogo.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ejemplo.catalogo.domain.exception.ProductNotFoundException;
import com.ejemplo.catalogo.domain.model.Product;
import com.ejemplo.catalogo.domain.port.out.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductUseCaseService service;

    @Test
    void findAllWithoutQueryListsEverything() {
        Product p = new Product("Cuaderno", "d", new BigDecimal("8000.00"), 60, "papeleria");
        when(repository.findAll()).thenReturn(List.of(p));

        assertThat(service.findAll(null)).containsExactly(p);
        assertThat(service.findAll("  ")).containsExactly(p);
    }

    @Test
    void findAllWithQueryFiltersByName() {
        Product p = new Product("Cuaderno", "d", new BigDecimal("8000.00"), 60, "papeleria");
        when(repository.findByNameContainingIgnoreCase("cuad")).thenReturn(List.of(p));

        assertThat(service.findAll("cuad")).containsExactly(p);
        verify(repository, never()).findAll();
    }

    @Test
    void findByIdThrowsWhenProductDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateCopiesFieldsOntoTheExistingProduct() {
        Product existing = new Product("Viejo", "desc", new BigDecimal("1.00"), 1, "hogar");
        existing.setId(2L);
        Product changes = new Product("Nuevo", "desc nueva", new BigDecimal("9.99"), 5, "electronica");
        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = service.update(2L, changes);

        assertThat(result.getName()).isEqualTo("Nuevo");
        assertThat(result.getPrice()).isEqualByComparingTo("9.99");
        assertThat(result.getId()).isEqualTo(2L);
    }

    @Test
    void deleteThrowsWhenProductDoesNotExist() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(7L))
                .isInstanceOf(ProductNotFoundException.class);
        verify(repository, never()).delete(any(Product.class));
    }
}
