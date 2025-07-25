package org.example.springresilience4j.services;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RateLimiterService {

    @RateLimiter(name = "rateLimiterService")
    public void doSomething(int value, int wait) throws InterruptedException {
        log.info("Calculating response for value: {}", value);
        Thread.sleep(wait);
        log.info("Completed operation for value: {}", value);
    }
}
