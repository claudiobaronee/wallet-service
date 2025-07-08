package com.wallet.domain.events;

import java.time.LocalDateTime;

/**
 * Evento de domínio emitido quando uma carteira é criada
 */
public class WalletCreatedEvent implements DomainEvent {
    
    private final String userId;
    private final String currency;
    private final LocalDateTime occurredAt;
    
    public WalletCreatedEvent(String userId, String currency, LocalDateTime occurredAt) {
        this.userId = userId;
        this.currency = currency;
        this.occurredAt = occurredAt;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public String getEventType() {
        return "WalletCreated";
    }
} 