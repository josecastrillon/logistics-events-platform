package com.josecastrillon.logistics.order.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

class OrderTest {

    private static final Instant NOW = Instant.parse("2026-10-27T15:00:00Z");

    @Test
    void placeComputesTotalAndStartsAsCreated() {
        var order = Order.place("customer-1", "Calle 10 #20-30", List.of(
                new OrderItem("SKU-1", 2, new BigDecimal("10.50")),
                new OrderItem("SKU-2", 1, new BigDecimal("4.00"))), NOW);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("25.00");
        assertThat(order.getCreatedAt()).isEqualTo(NOW);
        assertThat(order.getItems()).hasSize(2);
    }

    @Test
    void placeRejectsOrdersWithoutItems() {
        assertThatThrownBy(() -> Order.place("customer-1", "Calle 10", List.of(), NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancelChangesStatus() {
        var order = Order.place("customer-1", "Calle 10", List.of(new OrderItem("SKU-1", 1, BigDecimal.ONE)), NOW);

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelTwiceIsRejected() {
        var order = Order.place("customer-1", "Calle 10", List.of(new OrderItem("SKU-1", 1, BigDecimal.ONE)), NOW);
        order.cancel();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("CANCELLED");
    }

    @Test
    void itemRejectsNonPositiveQuantityOrPrice() {
        assertThatThrownBy(() -> new OrderItem("SKU-1", 0, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new OrderItem("SKU-1", 1, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
