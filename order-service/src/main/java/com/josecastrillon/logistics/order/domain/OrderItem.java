package com.josecastrillon.logistics.order.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Immutable order line, stored as a value object (no identity of its own). */
@Embeddable
public record OrderItem(
        @Column(nullable = false) String sku,
        @Column(nullable = false) int quantity,
        @Column(nullable = false) BigDecimal unitPrice) {

    public OrderItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice == null || unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
    }

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
