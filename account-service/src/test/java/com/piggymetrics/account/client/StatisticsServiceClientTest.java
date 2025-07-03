package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.Account;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticsServiceClientTest {

    @Autowired
    private StatisticsServiceClient statisticsServiceClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    public void shouldTriggerCircuitBreakerOnFailure() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("statisticsService");
        
        // Initial state should be CLOSED
        assert(circuitBreaker.getState() == CircuitBreaker.State.CLOSED);

        Account account = new Account();
        account.setName("test");

        // Simulate multiple failures
        for (int i = 0; i < 10; i++) {
            try {
                statisticsServiceClient.updateStatistics("test", account);
            } catch (Exception e) {
                // Expected
            }
        }

        // After multiple failures, circuit should be OPEN
        assert(circuitBreaker.getState() == CircuitBreaker.State.OPEN);
    }
}