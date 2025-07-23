package org.example.springresilience4j.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CircuitBreakerService {

    @CircuitBreaker(name = "circuitBreakerWithoutFallback")
    public int getResponse(int value) {
        if (value <= 0) {
            log.error("Value must be greater than zero");
            throw new IllegalArgumentException("Value must be greater than zero");
        }
        return value * 2;
    }

    @CircuitBreaker(name = "circuitBreakerWithFallback", fallbackMethod = "fallbackMethod")
    public int getResponseWithFallback(int value) {
        if (value <= 0) {
            log.info("Value must be greater than zero");
            throw new IllegalArgumentException("Value must be greater than zero");
        }
        return value * 2;
    }

    private int fallbackMethod(int value, IllegalArgumentException exception) {
        log.info("Fallback method called for value: {}. Error: {}", value, exception.getMessage());
        return 0;
    }

    private int fallbackMethod(int value, RuntimeException exception)  {
        throw exception;
    }
}


