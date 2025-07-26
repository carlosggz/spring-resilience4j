package org.example.springresilience4j.services;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.example.springresilience4j.utils.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CircuitBreakerServiceTest extends IntegrationTest {
    @Autowired
    private CircuitBreakerService circuitBreakerService;

    @Value("${resilience4j.circuitbreaker.instances.circuitBreakerWithoutFallback.minimumNumberOfCalls}")
    int minimumNumberOfCallsWithoutFallback;

    @Value("${resilience4j.circuitbreaker.instances.circuitBreakerWithFallback.minimumNumberOfCalls}")
    int minimumNumberOfCallsWithFallback;

    @Value(("${resilience4j.circuitbreaker.configs.myBaseConfig.waitDurationInOpenState}"))
    int waitDurationInOpenState;

    @Test
    void whenCallsGetResponseWithZeroValueThenThrowsRuntimeException() {
        //given
        IntStream
                .range(0, minimumNumberOfCallsWithoutFallback)
                .forEach(i -> assertThrows(IllegalArgumentException.class, () -> circuitBreakerService.getResponse(0)));

        //when/then
        assertThrows(CallNotPermittedException.class, () -> circuitBreakerService.getResponse(0));

        waitFor(waitDurationInOpenState);

        assertThrows(IllegalArgumentException.class, () -> circuitBreakerService.getResponse(0));
    }

    @Test
    void whenCallsGetResponseWithPositiveValueThenReturnsValue() {
        assertEquals(2, circuitBreakerService.getResponse(1));
    }

    @Test
    void whenCallsGetResponseWithFallbackWithZeroValueThenReturnsTheFallback() {
        //given
        IntStream
                .range(0, minimumNumberOfCallsWithFallback)
                .forEach(i -> assertEquals(0, circuitBreakerService.getResponseWithFallback(0)));

        //when/then
        assertThrows(CallNotPermittedException.class, () -> circuitBreakerService.getResponseWithFallback(0));

        waitFor(waitDurationInOpenState);

        assertEquals(0, circuitBreakerService.getResponseWithFallback(0));
    }

    @Test
    void whenCallsGetResponseWithFallbackWithPositiveValueThenReturnsValue() {
        assertEquals(2, circuitBreakerService.getResponseWithFallback(1));
    }

}