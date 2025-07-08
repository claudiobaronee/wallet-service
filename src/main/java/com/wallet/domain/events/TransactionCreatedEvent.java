package com.wallet.domain.events;

import com.wallet.domain.enums.TransactionType;
import com.wallet.domain.valueobjects.Money;
import java.time.LocalDateTime;

/**
 * Evento de domínio emitido quando uma transação é criada
 */
public class TransactionCreatedEvent implements DomainEvent {
    
    private final TransactionType transactionType;
    private final String userId;
    private final Money amount;
    private final String description;
    private final LocalDateTime occurredAt;
    
    public TransactionCreatedEvent(TransactionType transactionType, String userId, Money amount, String description, LocalDateTime occurredAt) {
        this.transactionType = transactionType;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.occurredAt = occurredAt;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public Money getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public String getEventType() {
        return "TransactionCreated";
    }
} 