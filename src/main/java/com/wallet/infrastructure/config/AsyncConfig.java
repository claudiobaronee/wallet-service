package com.wallet.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuração para execução assíncrona de eventos de domínio.
 * Implementa Event-Driven Design com processamento paralelo.
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * Executor para eventos de domínio gerais.
     * Processa eventos como criação, depósito, saque, etc.
     */
    @Bean(name = "domainEventExecutor")
    public Executor domainEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("DomainEvent-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("Evento de domínio rejeitado - fila cheia. Evento: {}", r);
        });
        executor.initialize();
        
        log.info("Executor de eventos de domínio configurado: core={}, max={}, queue={}", 
                5, 20, 100);
        
        return executor;
    }
    
    /**
     * Executor para auditoria de eventos.
     * Processa todos os eventos para auditoria e compliance.
     */
    @Bean(name = "auditEventExecutor")
    public Executor auditEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("AuditEvent-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.error("Evento de auditoria rejeitado - fila cheia. Evento: {}", r);
            // Em produção, você pode implementar dead letter queue
        });
        executor.initialize();
        
        log.info("Executor de auditoria configurado: core={}, max={}, queue={}", 
                2, 10, 200);
        
        return executor;
    }
    
    /**
     * Executor para notificações.
     * Processa envio de emails, push notifications, etc.
     */
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Notification-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("Notificação rejeitada - fila cheia. Notificação: {}", r);
        });
        executor.initialize();
        
        log.info("Executor de notificações configurado: core={}, max={}, queue={}", 
                3, 15, 50);
        
        return executor;
    }
    
    /**
     * Executor para operações de alta prioridade.
     * Processa operações críticas como suspensão de carteira.
     */
    @Bean(name = "highPriorityExecutor")
    public Executor highPriorityExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("HighPriority-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.error("Operação de alta prioridade rejeitada - fila cheia. Operação: {}", r);
        });
        executor.initialize();
        
        log.info("Executor de alta prioridade configurado: core={}, max={}, queue={}", 
                2, 5, 20);
        
        return executor;
    }
} 