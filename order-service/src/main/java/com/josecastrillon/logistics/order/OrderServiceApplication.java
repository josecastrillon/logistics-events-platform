package com.josecastrillon.logistics.order;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    /** Injected instead of calling Instant.now() directly, so time is controllable in tests. */
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
