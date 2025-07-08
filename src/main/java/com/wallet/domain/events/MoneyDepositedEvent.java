package com.wallet.domain.events;

import com.wallet.domain.valueobjects.Money;
import java.time.LocalDateTime;

/**
 * Evento de domínio emitido quando dinheiro é depositado
 */
public class MoneyDepositedEvent implements DomainEvent {
    
    private final String userId;
    private final Money amount;
    private final Money oldBalance;
    private final Money newBalance;
    private final LocalDateTime occurredAt;
    
    public MoneyDepositedEvent(String userId, Money amount, Money oldBalance, Money newBalance, LocalDateTime occurredAt) {
        this.userId = userId;
        this.amount = amount;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.occurredAt = occurredAt;
    }
    
    public String getUserId() {
        return userId;
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
        return "MoneyDeposited";
    }
} 