package com.josecastrillon.logistics.order.web;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.josecastrillon.logistics.order.domain.OrderItem;

public record PlaceOrderRequest(
        @NotBlank @Size(max = 64) String customerId,
        @NotBlank @Size(max = 255) String deliveryAddress,
        @NotEmpty @Valid List<Item> items) {

    public record Item(
            @NotBlank @Size(max = 64) String sku,
            @Positive int quantity,
            @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal unitPrice) {
    }

    List<OrderItem> toItems() {
        return items.stream()
                .map(item -> new OrderItem(item.sku(), item.quantity(), item.unitPrice()))
                .toList();
    }
}
