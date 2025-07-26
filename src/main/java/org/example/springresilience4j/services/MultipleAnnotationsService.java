package org.example.springresilience4j.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MultipleAnnotationsService {

    @TimeLimiter(name = "multipleAnnotationsService")
    @Retry(name = "multipleAnnotationsService")
    @CircuitBreaker(name = "multipleAnnotationsService")
    public CompletableFuture<Void> doSomething(int value, int wait) {
        return CompletableFuture.runAsync(() -> executeAction(value, wait));
    }

    private static void executeAction(int value, int wait) {
        try {
            log.info("Calculating response for value: {}", value);
            Thread.sleep(wait);
            log.info("Completed operation for value: {}", value);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
