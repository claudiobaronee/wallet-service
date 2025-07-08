package com.wallet.domain.events;

import java.time.LocalDateTime;

/**
 * Evento de domínio emitido quando uma carteira é suspensa
 */
public class WalletSuspendedEvent implements DomainEvent {
    
    private final String userId;
    private final LocalDateTime occurredAt;
    
    public WalletSuspendedEvent(String userId, LocalDateTime occurredAt) {
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
        return "WalletSuspended";
    }
} 