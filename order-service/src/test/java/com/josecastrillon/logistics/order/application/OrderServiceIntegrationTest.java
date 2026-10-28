package com.josecastrillon.logistics.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.josecastrillon.logistics.order.domain.OrderItem;
import com.josecastrillon.logistics.order.domain.OrderNotFoundException;
import com.josecastrillon.logistics.order.domain.OrderStatus;

/**
 * Runs the full Spring context against an in-memory H2 database (PostgreSQL mode), with the real
 * Flyway migrations. Replaced by Testcontainers + PostgreSQL in a later milestone.
 */
@SpringBootTest
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Test
    void placedOrderCanBeReadBackAndCancelled() {
        var placed = orderService.place("customer-42", "Carrera 5 #1-10",
                List.of(new OrderItem("SKU-9", 3, new BigDecimal("2.50"))));

        var loaded = orderService.get(placed.getId());
        assertThat(loaded.getTotalAmount()).isEqualByComparingTo("7.50");
        assertThat(loaded.getItems()).containsExactly(new OrderItem("SKU-9", 3, new BigDecimal("2.50")));
        assertThat(orderService.findByCustomer("customer-42")).hasSize(1);

        orderService.cancel(placed.getId());

        assertThat(orderService.get(placed.getId()).getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void getUnknownOrderFails() {
        assertThatThrownBy(() -> orderService.get(UUID.randomUUID()))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
