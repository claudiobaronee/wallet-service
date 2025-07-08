package com.wallet.infrastructure.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuração de circuit breaker simplificada para o Wallet Service.
 */
@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50) // 50% de falhas para abrir o circuit
            .waitDurationInOpenState(Duration.ofSeconds(60)) // 60s em estado aberto
            .slidingWindowSize(10) // Janela de 10 requisições
            .minimumNumberOfCalls(5) // Mínimo de 5 chamadas antes de avaliar
            .permittedNumberOfCallsInHalfOpenState(3) // 3 chamadas em half-open
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .recordExceptions(Exception.class) // Registrar todas as exceções
            .build();

        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(3) // Máximo de 3 tentativas
            .waitDuration(Duration.ofMillis(100)) // Esperar 100ms entre tentativas
            .retryExceptions(Exception.class) // Retry em todas as exceções
            .build();

        return RetryRegistry.of(config);
    }

    @Bean
    public CircuitBreaker walletServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("wallet-service");
    }

    @Bean
    public CircuitBreaker databaseCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("database");
    }

    @Bean
    public Retry walletServiceRetry(RetryRegistry registry) {
        return registry.retry("wallet-service");
    }

    @Bean
    public Retry databaseRetry(RetryRegistry registry) {
        return registry.retry("database");
    }
} 