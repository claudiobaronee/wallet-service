package com.wallet.domain.events;

import java.time.LocalDateTime;

/**
 * Interface base para todos os eventos de domínio
 */
public interface DomainEvent {
    
    /**
     * Obtém o timestamp do evento
     */
    LocalDateTime getOccurredAt();
    
    /**
     * Obtém o tipo do evento
     */
    String getEventType();
} 