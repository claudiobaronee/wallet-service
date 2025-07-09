package com.wallet.infrastructure.services;

import com.wallet.domain.valueobjects.Money;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class AuditService {

    public void logOperation(String operation, String userId, String details, String correlationId) {
        log.info("AUDIT_OPERATION - operation: {}, userId: {}, details: {}, correlationId: {}, timestamp: {}", 
                operation, userId, details, correlationId, LocalDateTime.now());
    }

    public void logTransaction(String transactionType, String userId, Money amount, String description, String correlationId) {
        log.info("AUDIT_TRANSACTION - type: {}, userId: {}, amount: {} {}, description: {}, correlationId: {}, timestamp: {}", 
                transactionType, userId, amount.getAmount(), amount.getCurrency(), description, correlationId, LocalDateTime.now());
    }

    public void logBalanceChange(String userId, Money oldBalance, Money newBalance, String reason, String correlationId) {
        log.info("AUDIT_BALANCE_CHANGE - userId: {}, oldBalance: {} {}, newBalance: {} {}, reason: {}, correlationId: {}, timestamp: {}", 
                userId, oldBalance.getAmount(), oldBalance.getCurrency(), newBalance.getAmount(), newBalance.getCurrency(), reason, correlationId, LocalDateTime.now());
    }

    public void logSecurityEvent(String event, String userId, String details, String correlationId) {
        log.warn("AUDIT_SECURITY - event: {}, userId: {}, details: {}, correlationId: {}, timestamp: {}", 
                event, userId, details, correlationId, LocalDateTime.now());
    }

    public void logError(String operation, String userId, String error, String correlationId) {
        log.error("AUDIT_ERROR - operation: {}, userId: {}, error: {}, correlationId: {}, timestamp: {}", 
                operation, userId, error, correlationId, LocalDateTime.now());
    }

    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
} 