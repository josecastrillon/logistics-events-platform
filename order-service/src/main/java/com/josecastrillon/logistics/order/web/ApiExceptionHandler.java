package com.josecastrillon.logistics.order.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.josecastrillon.logistics.order.domain.InvalidOrderStateException;
import com.josecastrillon.logistics.order.domain.OrderNotFoundException;

/** Maps domain exceptions to RFC 9457 problem details. Bean validation errors use Spring's built-in handling. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail handleNotFound(OrderNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    ProblemDetail handleInvalidState(InvalidOrderStateException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }
}
