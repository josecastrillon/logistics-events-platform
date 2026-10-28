package com.josecastrillon.logistics.order.domain;

import java.util.UUID;

public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(UUID orderId, OrderStatus status, String action) {
        super("Cannot %s order %s in status %s".formatted(action, orderId, status));
    }
}
