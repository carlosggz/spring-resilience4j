package org.example.springresilience4j.services;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RateLimiterServiceTest {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Value("${resilience4j.ratelimiter.configs.myBaseRateLimiterConfig.limitForPeriod}")
    private int limitForPeriod;

    @Value("${resilience4j.ratelimiter.configs.myBaseRateLimiterConfig.limitRefreshPeriod}")
    private int limitRefreshPeriod;

    @Test
    void whenReachesLimitForPeriodThenThrowsException() {
        //given
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
        assertThrows(RequestNotPermitted.class, () -> {
            Thread.sleep(100);
            rateLimiterService.doSomething(limitForPeriod+1, 2*limitRefreshPeriod);
        });
    }

    @Test
    void whenReachesLimitForPeriodItWaits() {
        //given
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
        assertDoesNotThrow(() -> {
            Thread.sleep(limitRefreshPeriod-100);
            rateLimiterService.doSomething(limitForPeriod+1, limitRefreshPeriod/2);
        });
    }
}