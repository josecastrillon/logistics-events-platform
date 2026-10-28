package com.josecastrillon.logistics.order.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.josecastrillon.logistics.order.application.OrderService;
import com.josecastrillon.logistics.order.domain.InvalidOrderStateException;
import com.josecastrillon.logistics.order.domain.Order;
import com.josecastrillon.logistics.order.domain.OrderItem;
import com.josecastrillon.logistics.order.domain.OrderNotFoundException;
import com.josecastrillon.logistics.order.domain.OrderStatus;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    private static final String VALID_REQUEST = """
            {
              "customerId": "customer-1",
              "deliveryAddress": "Calle 10 #20-30",
              "items": [ { "sku": "SKU-1", "quantity": 2, "unitPrice": 10.50 } ]
            }
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private OrderService orderService;

    private static Order sampleOrder() {
        return Order.place("customer-1", "Calle 10 #20-30",
                List.of(new OrderItem("SKU-1", 2, new BigDecimal("10.50"))),
                Instant.parse("2026-10-27T15:00:00Z"));
    }

    @Test
    void placeReturns201WithLocation() throws Exception {
        var order = sampleOrder();
        given(orderService.place(anyString(), anyString(), anyList())).willReturn(order);

        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/orders/" + order.getId()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(21.00))
                .andExpect(jsonPath("$.items[0].sku").value("SKU-1"));
    }

    @Test
    void placeRejectsInvalidRequest() throws Exception {
        var invalid = """
                { "customerId": "", "deliveryAddress": "Calle 10", "items": [] }
                """;

        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).place(any(), any(), any());
    }

    @Test
    void getReturns404WhenOrderDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        given(orderService.get(id)).willThrow(new OrderNotFoundException(id));

        mvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Order %s not found".formatted(id)));
    }

    @Test
    void getReturnsOrder() throws Exception {
        var order = sampleOrder();
        given(orderService.get(order.getId())).willReturn(order);

        mvc.perform(get("/api/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()));
    }

    @Test
    void findByCustomerReturnsList() throws Exception {
        given(orderService.findByCustomer("customer-1")).willReturn(List.of(sampleOrder()));

        mvc.perform(get("/api/orders").param("customerId", "customer-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void cancelReturns409WhenOrderCannotBeCancelled() throws Exception {
        var id = UUID.randomUUID();
        given(orderService.cancel(id)).willThrow(new InvalidOrderStateException(id, OrderStatus.CANCELLED, "cancel"));

        mvc.perform(post("/api/orders/{id}/cancel", id))
                .andExpect(status().isConflict());
    }
}
