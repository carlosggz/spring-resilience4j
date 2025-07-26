package org.example.springresilience4j.services;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class TimeLimiterService {

    @TimeLimiter(name = "timeLimiterService")
    public CompletableFuture<Integer> getResult(int value, int wait) {
        return CompletableFuture.supplyAsync(() -> getOperationResult(value, wait));
    }

    private static int getOperationResult(int value, int wait) {
        try {
            log.info("Calculating response for value: {}", value);
            Thread.sleep(wait);
            log.info("Completed operation for value: {}", value);
            return value * 2;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
