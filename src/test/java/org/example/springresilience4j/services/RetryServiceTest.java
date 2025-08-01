package org.example.springresilience4j.services;

import org.example.springresilience4j.utils.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RetryServiceTest extends IntegrationTest {

    @MockitoSpyBean
    private RetryService retryService;

    @Value("${resilience4j.retry.configs.myBaseRetryConfig.maxAttempts}")
    int maxAttempts;

    @Test
    void whenCallsGetResponseWithZeroValueThenThrowsRuntimeException() {

        assertThrows(IllegalArgumentException.class, () -> retryService.getResponse(0));

        verify(retryService, times(maxAttempts)).getResponse(0);
    }

    @Test
    void whenCallsGetResponseWithPositiveValueThenReturnsValue() {

        assertEquals(2, retryService.getResponse(1));

        verify(retryService, times(1)).getResponse(1);
    }

    @Test
    void whenCallsGetResponseWithFallbackWithZeroValueThenThrowsRuntimeException() {

        assertEquals(0, retryService.getResponseWithFallback(0));

        verify(retryService, times(maxAttempts)).getResponseWithFallback(0);
    }

    @Test
    void whenCallsGetResponseWithFallbackWithPositiveValueThenReturnsValue() {

        assertEquals(2, retryService.getResponse(1));

        verify(retryService, times(1)).getResponse(1);
    }
}