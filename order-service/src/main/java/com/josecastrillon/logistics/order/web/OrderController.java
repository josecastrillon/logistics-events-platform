package com.josecastrillon.logistics.order.web;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.josecastrillon.logistics.order.application.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> place(@Valid @RequestBody PlaceOrderRequest request) {
        var order = orderService.place(request.customerId(), request.deliveryAddress(), request.toItems());
        return ResponseEntity
                .created(URI.create("/api/orders/" + order.getId()))
                .body(OrderResponse.from(order));
    }

    @GetMapping("/{orderId}")
    public OrderResponse get(@PathVariable UUID orderId) {
        return OrderResponse.from(orderService.get(orderId));
    }

    @GetMapping
    public List<OrderResponse> findByCustomer(@RequestParam String customerId) {
        return orderService.findByCustomer(customerId).stream().map(OrderResponse::from).toList();
    }

    @PostMapping("/{orderId}/cancel")
    public OrderResponse cancel(@PathVariable UUID orderId) {
        return OrderResponse.from(orderService.cancel(orderId));
    }
}
