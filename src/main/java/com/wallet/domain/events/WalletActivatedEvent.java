package com.wallet.domain.events;

import java.time.LocalDateTime;

/**
 * Evento de domínio emitido quando uma carteira é ativada
 */
public class WalletActivatedEvent implements DomainEvent {
    
    private final String userId;
    private final LocalDateTime occurredAt;
    
    public WalletActivatedEvent(String userId, LocalDateTime occurredAt) {
        this.userId = userId;
        this.occurredAt = occurredAt;
    }
    
    public String getUserId() {
        return userId;
    }
    
    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public String getEventType() {
        return "WalletActivated";
    }
} 