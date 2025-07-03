package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.Account;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "statistics-service", fallback = StatisticsServiceClientFallback.class)
public interface StatisticsServiceClient {

    @RequestMapping(method = RequestMethod.PUT, value = "/statistics/{accountName}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @CircuitBreaker(name = "statisticsService", fallbackMethod = "updateStatisticsFallback")
    @Retry(name = "statisticsService")
    void updateStatistics(@PathVariable("accountName") String accountName, Account account);

    default void updateStatisticsFallback(String accountName, Account account, Exception ex) {
        // Fallback method when circuit breaker is triggered
        System.out.println("Fallback: Failed to update statistics for account: " + accountName);
    }
}