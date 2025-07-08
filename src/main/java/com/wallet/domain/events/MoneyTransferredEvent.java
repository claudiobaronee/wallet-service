package com.wallet.domain.events;

import com.wallet.domain.valueobjects.Money;
import java.time.LocalDateTime;

/**
 * Evento de domínio emitido quando dinheiro é transferido
 */
public class MoneyTransferredEvent implements DomainEvent {
    
    private final String sourceUserId;
    private final String targetUserId;
    private final Money amount;
    private final Money oldBalance;
    private final Money newBalance;
    private final LocalDateTime occurredAt;
    
    public MoneyTransferredEvent(String sourceUserId, String targetUserId, Money amount, Money oldBalance, Money newBalance, LocalDateTime occurredAt) {
        this.sourceUserId = sourceUserId;
        this.targetUserId = targetUserId;
        this.amount = amount;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.occurredAt = occurredAt;
    }
    
    public String getSourceUserId() {
        return sourceUserId;
    }
    
    public String getTargetUserId() {
        return targetUserId;
    }
    
    public Money getAmount() {
        return amount;
    }
    
    public Money getOldBalance() {
        return oldBalance;
    }
    
    public Money getNewBalance() {
        return newBalance;
    }
    
    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public String getEventType() {
        return "MoneyTransferred";
    }
} 