package org.example.springresilience4j.services;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BulkheadServiceTest {

    @MockitoSpyBean
    private BulkheadService bulkheadService;

    @Value("${resilience4j.bulkhead.configs.myBaseBulkheadConfig.maxConcurrentCalls}")
    private int maxConcurrentCalls;

    @Value("${resilience4j.bulkhead.configs.myBaseBulkheadConfig.maxWaitDuration}")
    private int maxWaitDuration;

    @Test
    @SneakyThrows
    void whenReachesTheMaxOfCallsItThrowsException() {
        //given
        val value = 1;
        val waitTime = 5000;
        IntStream
                .rangeClosed(1, maxConcurrentCalls)
                .forEach(i -> new Thread(() -> {
                    try {
                        bulkheadService.getResponse(value, waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start());

        //when/then
        assertThrows(BulkheadFullException.class, () -> bulkheadService.getResponse(value, waitTime));
    }

    @Test
    @SneakyThrows
    void whenReachesTheMaxOfCallsButWithinIntervalItWaits() {
        //given
        val value = 1;
        val waitTime = maxWaitDuration - 1000;
        IntStream
                .rangeClosed(1, maxConcurrentCalls)
                .forEach(i -> new Thread(() -> {
                    try {
                        bulkheadService.getResponse(value, waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start());

        //when/then
        assertEquals(2, bulkheadService.getResponse(value, waitTime));
    }
}