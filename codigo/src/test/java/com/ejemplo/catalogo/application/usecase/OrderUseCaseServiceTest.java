package com.ejemplo.catalogo.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ejemplo.catalogo.domain.exception.InvalidOrderException;
import com.ejemplo.catalogo.domain.exception.OrderNotFoundException;
import com.ejemplo.catalogo.domain.model.Order;
import com.ejemplo.catalogo.domain.model.OrderStatus;
import com.ejemplo.catalogo.domain.model.Product;
import com.ejemplo.catalogo.domain.port.out.OrderRepository;
import com.ejemplo.catalogo.domain.port.out.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas de la regla de negocio (RN-1 a RN-5) sobre los PUERTOS, sin base de datos
 * ni archivo. Fila 0: Cuaderno a 8.000, stock 60, umbral u = 10, descuento d = 5 %.
 */
@ExtendWith(MockitoExtension.class)
class OrderUseCaseServiceTest {

    @Mock
    private OrderRepository orders;

    @Mock
    private ProductRepository products;

    private OrderUseCaseService service;

    @BeforeEach
    void setUp() {
        service = new OrderUseCaseService(orders, products, 10, 5);
    }

    /** El producto existe y ya hay {@code alreadySold} unidades en pedidos CONFIRMADO. */
    private void givenCuaderno(long alreadySold) {
        Product cuaderno = new Product("Cuaderno", "d", new BigDecimal("8000.00"), 60, "papeleria");
        cuaderno.setId(1L);
        when(products.findById(1L)).thenReturn(Optional.of(cuaderno));
        when(orders.sumConfirmedQuantity(1L)).thenReturn(alreadySold);
        when(orders.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---- RN-1 y RN-2: descuento por volumen y total ----

    @Test
    void justBelowThresholdHasNoDiscount() {
        givenCuaderno(0);

        Order order = service.register("C-001", 1L, 9);

        assertThat(order.getEstado()).isEqualTo(OrderStatus.CONFIRMADO);
        assertThat(order.getTotal()).isEqualTo(72_000L);   // 9 x 8000
    }

    @Test
    void atThresholdTheDiscountApplies() {
        givenCuaderno(0);

        Order order = service.register("C-002", 1L, 10);

        assertThat(order.getEstado()).isEqualTo(OrderStatus.CONFIRMADO);
        assertThat(order.getTotal()).isEqualTo(76_000L);   // 10 x 8000 x 95 / 100
    }

    // ---- RN-3: stock ----

    @Test
    void anOrderThatExceedsStockIsRejectedWithZeroTotal() {
        givenCuaderno(19);   // quedan 41

        Order order = service.register("C-001", 1L, 42);

        assertThat(order.getEstado()).isEqualTo(OrderStatus.RECHAZADO);
        assertThat(order.getTotal()).isZero();
    }

    @Test
    void anOrderThatLeavesStockAtExactlyZeroIsConfirmed() {
        givenCuaderno(19);   // quedan 41

        Order order = service.register("C-003", 1L, 41);

        assertThat(order.getEstado()).isEqualTo(OrderStatus.CONFIRMADO);
        assertThat(order.getTotal()).isEqualTo(311_600L);  // 41 x 8000 x 95 / 100
    }

    // ---- RN-5: validacion ----

    @Test
    void zeroQuantityIsAValidationErrorAndNothingIsSaved() {
        assertThatThrownBy(() -> service.register("C-001", 1L, 0))
                .isInstanceOf(InvalidOrderException.class);
        verify(orders, never()).save(any(Order.class));
    }

    @Test
    void unknownProductIsAValidationErrorAndNothingIsSaved() {
        when(products.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.register("C-001", 99L, 1))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessageContaining("99");
        verify(orders, never()).save(any(Order.class));
    }

    // ---- RN-4: consultas ----

    @Test
    void findByIdThrowsWhenTheOrderDoesNotExist() {
        when(orders.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(5L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void totalPurchasedByDelegatesToTheConfirmedSum() {
        when(orders.sumConfirmedTotalByClient("C-001")).thenReturn(72_000L);

        assertThat(service.totalPurchasedBy("C-001")).isEqualTo(72_000L);
    }
}
