package org.example.springresilience4j.services;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BulkheadService {

    @Bulkhead(name = "bulkheadService", type = Bulkhead.Type.SEMAPHORE)
    public int getResponse(int value, int wait) throws InterruptedException {
        log.info("Calculating response for value: {}", value);
        Thread.sleep(wait);
        return value * 2;
    }
}
