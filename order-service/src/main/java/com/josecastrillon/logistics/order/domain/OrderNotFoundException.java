package com.josecastrillon.logistics.order.domain;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(UUID orderId) {
        super("Order %s not found".formatted(orderId));
    }
}
