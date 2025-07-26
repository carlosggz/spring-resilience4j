package org.example.springresilience4j.services;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.SneakyThrows;
import org.example.springresilience4j.utils.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RateLimiterServiceTest extends IntegrationTest {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Value("${resilience4j.ratelimiter.configs.myBaseRateLimiterConfig.limitForPeriod}")
    private int limitForPeriod;

    @Value("${resilience4j.ratelimiter.configs.myBaseRateLimiterConfig.limitRefreshPeriod}")
    private int limitRefreshPeriod;

    @Test
    @SneakyThrows
    void whenReachesLimitForPeriodThenThrowsException() {
        //given
        waitFor(limitRefreshPeriod);
        IntStream
                .rangeClosed(1, limitForPeriod)
                .forEach(i -> new Thread(() -> {
                    try {
                        rateLimiterService.doSomething(i, 2*limitRefreshPeriod);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start());

        //when/then
        waitFor(100);
        assertThrows(RequestNotPermitted.class, () -> rateLimiterService.doSomething(limitForPeriod+1, 2*limitRefreshPeriod));
    }

    @Test
    @SneakyThrows
    void whenReachesLimitForPeriodItWaits() {
        //given
        waitFor(limitRefreshPeriod);
        IntStream
                .rangeClosed(1, limitForPeriod)
                .forEach(i -> new Thread(() -> {
                    try {
                        rateLimiterService.doSomething(i, limitRefreshPeriod/2);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start());

        //when/then
        waitFor(limitRefreshPeriod-100);
        assertDoesNotThrow(() -> rateLimiterService.doSomething(limitForPeriod+1, limitRefreshPeriod/2));
    }
}