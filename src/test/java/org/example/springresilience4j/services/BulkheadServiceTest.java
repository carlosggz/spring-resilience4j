package org.example.springresilience4j.services;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import lombok.SneakyThrows;
import lombok.val;
import org.example.springresilience4j.utils.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BulkheadServiceTest extends IntegrationTest {

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
        val waitTime = 5000;
        waitFor(maxWaitDuration);
        IntStream
                .rangeClosed(1, maxConcurrentCalls)
                .forEach(i -> new Thread(() -> {
                    try {
                        bulkheadService.getResponse(i, waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start());

        //when/then
        waitFor(100);
        assertThrows(BulkheadFullException.class, () -> bulkheadService.getResponse(maxConcurrentCalls+1, waitTime));
    }

    @Test
    @SneakyThrows
    void whenReachesTheMaxOfCallsButWithinIntervalItWaits() {
        //given
        val waitTime = maxWaitDuration - 1000;
        waitFor(maxWaitDuration);
        IntStream
                .rangeClosed(1, maxConcurrentCalls)
                .forEach(i -> new Thread(() -> {
                    try {
                        bulkheadService.getResponse(i, waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start());

        //when/then
        waitFor(100);
        assertEquals(2*(maxConcurrentCalls+1), bulkheadService.getResponse(maxConcurrentCalls+1, waitTime));
    }
}