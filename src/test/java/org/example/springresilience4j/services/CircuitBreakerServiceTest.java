package org.example.springresilience4j.services;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CircuitBreakerServiceTest {
    @Autowired
    private CircuitBreakerService circuitBreakerService;

    @Value("${resilience4j.circuitbreaker.instances.circuitBreakerWithoutFallback.minimumNumberOfCalls}")
    int minimumNumberOfCallsWithoutFallback;

    @Value("${resilience4j.circuitbreaker.instances.circuitBreakerWithFallback.minimumNumberOfCalls}")
    int minimumNumberOfCallsWithFallback;

    @Value(("${resilience4j.circuitbreaker.configs.myBaseConfig.waitDurationInOpenState}"))
    int waitDurationInOpenState;

    @Test
    @SneakyThrows
    void whenCallsGetResponseWithZeroValueThenThrowsRuntimeException() {
        //given
        IntStream
                .range(0, minimumNumberOfCallsWithoutFallback)
                .forEach(i -> assertThrows(IllegalArgumentException.class, () -> circuitBreakerService.getResponse(0)));

        //when/then
        assertThrows(CallNotPermittedException.class, () -> circuitBreakerService.getResponse(0));

        Thread.sleep(waitDurationInOpenState);

        assertThrows(IllegalArgumentException.class, () -> circuitBreakerService.getResponse(0));
    }

    @Test
    void whenCallsGetResponseWithPositiveValueThenReturnsValue() {
        assertEquals(2, circuitBreakerService.getResponse(1));
    }

    @Test
    @SneakyThrows
    void whenCallsGetResponseWithFallbackWithZeroValueThenReturnsTheFallback() {
        //given
        IntStream
                .range(0, minimumNumberOfCallsWithFallback)
                .forEach(i -> assertEquals(0, circuitBreakerService.getResponseWithFallback(0)));

        //when/then
        assertThrows(CallNotPermittedException.class, () -> circuitBreakerService.getResponseWithFallback(0));

        Thread.sleep(waitDurationInOpenState);

        assertEquals(0, circuitBreakerService.getResponseWithFallback(0));
    }

    @Test
    void whenCallsGetResponseWithFallbackWithPositiveValueThenReturnsValue() {
        assertEquals(2, circuitBreakerService.getResponseWithFallback(1));
    }

}