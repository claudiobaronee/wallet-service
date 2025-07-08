package com.wallet.application.events;

import com.wallet.domain.events.DomainEvent;

/**
 * Interface para publicação de eventos de domínio.
 * Segue o padrão Event-Driven Design para desacoplamento.
 */
public interface DomainEventPublisher {
    
    /**
     * Publica um evento de domínio de forma assíncrona.
     * 
     * @param event O evento de domínio a ser publicado
     */
    void publish(DomainEvent event);
    
    /**
     * Publica um evento de domínio de forma síncrona.
     * 
     * @param event O evento de domínio a ser publicado
     */
    void publishSync(DomainEvent event);
} 