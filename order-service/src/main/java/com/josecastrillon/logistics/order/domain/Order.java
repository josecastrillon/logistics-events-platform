package com.josecastrillon.logistics.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Aggregate root for a customer order. State changes go through domain methods
 * ({@link #place}, {@link #cancel}) so the business rules live in one place.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItem> items = new ArrayList<>();

    /** Optimistic locking: concurrent updates to the same order fail instead of overwriting each other. */
    @Version
    private long version;

    protected Order() {
        // required by JPA
    }

    public static Order place(String customerId, String deliveryAddress, List<OrderItem> items, Instant now) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("An order needs at least one item");
        }
        var order = new Order();
        order.id = UUID.randomUUID();
        order.customerId = customerId;
        order.deliveryAddress = deliveryAddress;
        order.status = OrderStatus.CREATED;
        order.items = new ArrayList<>(items);
        order.totalAmount = items.stream()
                .map(OrderItem::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.createdAt = now;
        return order;
    }

    public void cancel() {
        if (status != OrderStatus.CREATED) {
            throw new InvalidOrderStateException(id, status, "cancel");
        }
        status = OrderStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }
}
