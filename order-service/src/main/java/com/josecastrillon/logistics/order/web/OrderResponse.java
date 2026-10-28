package com.josecastrillon.logistics.order.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.josecastrillon.logistics.order.domain.Order;
import com.josecastrillon.logistics.order.domain.OrderItem;
import com.josecastrillon.logistics.order.domain.OrderStatus;

public record OrderResponse(
        UUID id,
        String customerId,
        String deliveryAddress,
        OrderStatus status,
        List<Item> items,
        BigDecimal totalAmount,
        Instant createdAt) {

    public record Item(String sku, int quantity, BigDecimal unitPrice) {

        static Item from(OrderItem item) {
            return new Item(item.sku(), item.quantity(), item.unitPrice());
        }
    }

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getDeliveryAddress(),
                order.getStatus(),
                order.getItems().stream().map(Item::from).toList(),
                order.getTotalAmount(),
                order.getCreatedAt());
    }
}
