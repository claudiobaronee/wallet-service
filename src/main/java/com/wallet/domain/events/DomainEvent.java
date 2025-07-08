package com.wallet.domain.events;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime getOccurredAt();
    String getEventType();
} 