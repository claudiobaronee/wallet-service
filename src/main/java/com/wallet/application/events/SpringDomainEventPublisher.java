package com.wallet.application.events;

import com.wallet.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Implementação do publisher de eventos de domínio usando Spring Events.
 * Permite publicação síncrona e assíncrona de eventos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    @Async("domainEventExecutor")
    public void publish(DomainEvent event) {
        try {
            log.info("Publicando evento de domínio assíncrono: {}", event.getClass().getSimpleName());
            eventPublisher.publishEvent(event);
            log.debug("Evento publicado com sucesso: {}", event);
        } catch (Exception e) {
            log.error("Erro ao publicar evento de domínio: {}", event, e);
            throw new RuntimeException("Falha na publicação do evento", e);
        }
    }
    
    @Override
    public void publishSync(DomainEvent event) {
        try {
            log.info("Publicando evento de domínio síncrono: {}", event.getClass().getSimpleName());
            eventPublisher.publishEvent(event);
            log.debug("Evento síncrono publicado com sucesso: {}", event);
        } catch (Exception e) {
            log.error("Erro ao publicar evento de domínio síncrono: {}", event, e);
            throw new RuntimeException("Falha na publicação do evento síncrono", e);
        }
    }
} 