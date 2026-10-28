package com.josecastrillon.logistics.order.application;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.josecastrillon.logistics.order.domain.Order;
import com.josecastrillon.logistics.order.domain.OrderItem;
import com.josecastrillon.logistics.order.domain.OrderNotFoundException;
import com.josecastrillon.logistics.order.domain.OrderRepository;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orders;
    private final Clock clock;

    public OrderService(OrderRepository orders, Clock clock) {
        this.orders = orders;
        this.clock = clock;
    }

    @Transactional
    public Order place(String customerId, String deliveryAddress, List<OrderItem> items) {
        var order = Order.place(customerId, deliveryAddress, items, clock.instant());
        return orders.save(order);
    }

    public Order get(UUID orderId) {
        return orders.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public List<Order> findByCustomer(String customerId) {
        return orders.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Transactional
    public Order cancel(UUID orderId) {
        var order = get(orderId);
        order.cancel();
        return order;
    }
}
