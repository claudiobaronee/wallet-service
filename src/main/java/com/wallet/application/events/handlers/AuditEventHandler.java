package com.wallet.application.events.handlers;

import com.wallet.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handler especializado em auditoria de eventos de domínio.
 * Garante rastreabilidade completa de todas as operações.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventHandler {
    
    /**
     * Processa todos os eventos de domínio para auditoria.
     * Este handler é executado para TODOS os eventos publicados.
     */
    @EventListener
    @Async("auditEventExecutor")
    public void auditAllEvents(DomainEvent event) {
        String auditId = UUID.randomUUID().toString();
        LocalDateTime auditTimestamp = LocalDateTime.now();
        
        log.info("AUDIT [{}] - Evento: {} | Timestamp: {} | Dados: {}", 
                auditId, 
                event.getClass().getSimpleName(), 
                auditTimestamp, 
                event);
        
        // Aqui você pode implementar:
        // - Persistência em banco de auditoria
        // - Envio para sistemas de log centralizados
        // - Integração com ferramentas de compliance
        // - Análise de padrões de comportamento
        
        try {
            // Simular persistência de auditoria
            persistAuditRecord(auditId, event, auditTimestamp);
            log.debug("AUDIT [{}] - Registro de auditoria persistido com sucesso", auditId);
        } catch (Exception e) {
            log.error("AUDIT [{}] - Erro ao persistir registro de auditoria", auditId, e);
            // Em produção, você pode implementar retry logic ou dead letter queue
        }
    }
    
    /**
     * Persiste registro de auditoria.
     * Em produção, isso seria uma integração com banco de auditoria.
     */
    private void persistAuditRecord(String auditId, DomainEvent event, LocalDateTime timestamp) {
        // Simulação de persistência
        // Em produção, você implementaria:
        // - JPA Repository para AuditRecord
        // - Integração com Elasticsearch
        // - Envio para sistemas de log como Splunk, ELK Stack
        
        log.debug("Persistindo auditoria: ID={}, Evento={}, Timestamp={}", 
                auditId, event.getClass().getSimpleName(), timestamp);
    }
} 