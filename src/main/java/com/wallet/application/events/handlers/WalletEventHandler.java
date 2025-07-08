package com.wallet.application.events.handlers;

import com.wallet.domain.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Handler para processar eventos de carteira de forma assíncrona.
 * Implementa Event-Driven Design para auditoria e integração.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventHandler {
    
    /**
     * Processa evento de carteira criada.
     * Pode ser usado para notificações, auditoria, etc.
     */
    @EventListener
    @Async("domainEventExecutor")
    public void handleWalletCreated(WalletCreatedEvent event) {
        log.info("Processando evento: Carteira criada para usuário {}", event.getUserId());
        try {
            Thread.sleep(100);
            log.info("Evento de carteira criada processado com sucesso: {}", event.getUserId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Processamento do evento interrompido", e);
        }
    }
    
    /**
     * Processa evento de depósito realizado.
     */
    @EventListener
    @Async("domainEventExecutor")
    public void handleMoneyDeposited(MoneyDepositedEvent event) {
        log.info("Processando evento: Depósito de {} {} na carteira {}", 
                event.getAmount().getAmount(), event.getAmount().getCurrency(), event.getUserId());
    }
    
    /**
     * Processa evento de saque realizado.
     */
    @EventListener
    @Async("domainEventExecutor")
    public void handleMoneyWithdrawn(MoneyWithdrawnEvent event) {
        log.info("Processando evento: Saque de {} {} da carteira {}", 
                event.getAmount().getAmount(), event.getAmount().getCurrency(), event.getUserId());
    }
    
    /**
     * Processa evento de transferência realizada.
     */
    @EventListener
    @Async("domainEventExecutor")
    public void handleMoneyTransferred(MoneyTransferredEvent event) {
        log.info("Processando evento: Transferência de {} {} da carteira {} para {}", 
                event.getAmount().getAmount(), event.getAmount().getCurrency(), event.getSourceUserId(), event.getTargetUserId());
    }
    
    /**
     * Processa evento de carteira suspensa.
     */
    @EventListener
    @Async("domainEventExecutor")
    public void handleWalletSuspended(WalletSuspendedEvent event) {
        log.warn("Processando evento: Carteira suspensa para usuário {}", event.getUserId());
    }
    
    /**
     * Processa evento de carteira ativada.
     */
    @EventListener
    @Async("domainEventExecutor")
    public void handleWalletActivated(WalletActivatedEvent event) {
        log.info("Processando evento: Carteira ativada para usuário {}", event.getUserId());
    }
    
    /**
     * Processa evento de carteira fechada.
     */
    @EventListener
    @Async("domainEventExecutor")
    public void handleWalletClosed(WalletClosedEvent event) {
        log.info("Processando evento: Carteira fechada para usuário {}", event.getUserId());
    }
    
    /**
     * Processa evento de transação criada.
     */
    @EventListener
    @Async("domainEventExecutor")
    public void handleTransactionCreated(TransactionCreatedEvent event) {
        log.info("Processando evento: Transação {} criada na carteira do usuário {}", 
                event.getTransactionType(), event.getUserId());
    }
} 