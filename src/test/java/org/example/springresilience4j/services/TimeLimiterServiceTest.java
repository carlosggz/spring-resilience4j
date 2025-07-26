package org.example.springresilience4j.services;

import lombok.SneakyThrows;
import lombok.val;
import org.example.springresilience4j.utils.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeLimiterServiceTest extends IntegrationTest {
    @Autowired
    private TimeLimiterService timeLimiterService;

    @Value("${resilience4j.timelimiter.configs.myBaseTimeLimiterConfig.timeoutDuration}")
    private int timeoutDuration;

    @Test
    @SneakyThrows
    void whenOperationCompletesWithinTimeLimitThenSuccess() {
        //when
        val result = timeLimiterService.getResult(1, timeoutDuration - 200);

        //then
        assertEquals(2, result.get(timeoutDuration, java.util.concurrent.TimeUnit.MILLISECONDS));
    }

    @Test
    void whenOperationExceedsTimeLimitThenThrowsException() {
        //when
        val result = timeLimiterService.getResult(1, timeoutDuration + 200);

        //then
        assertThrows(
                TimeoutException.class,
                () -> result.get(timeoutDuration, java.util.concurrent.TimeUnit.MILLISECONDS));
    }
}