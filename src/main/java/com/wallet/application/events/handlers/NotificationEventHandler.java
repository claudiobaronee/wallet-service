package com.wallet.application.events.handlers;

import com.wallet.domain.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Handler para envio de notificações baseadas em eventos de domínio.
 * Implementa notificações push, email, SMS, etc.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
    
    /**
     * Envia notificação de boas-vindas quando carteira é criada.
     */
    @EventListener
    @Async("notificationExecutor")
    public void sendWelcomeNotification(WalletCreatedEvent event) {
        log.info("Enviando notificação de boas-vindas para usuário: {}", event.getUserId());
        sendEmail(event.getUserId(), "Bem-vindo!", "Sua carteira foi criada com sucesso!");
        sendPushNotification(event.getUserId(), "Carteira criada", "Sua carteira digital está pronta!");
        log.info("Notificação de boas-vindas enviada com sucesso para: {}", event.getUserId());
    }
    
    /**
     * Envia notificação de depósito realizado.
     */
    @EventListener
    @Async("notificationExecutor")
    public void sendDepositNotification(MoneyDepositedEvent event) {
        log.info("Enviando notificação de depósito para carteira: {}", event.getUserId());
        String message = String.format("Depósito de %.2f %s realizado com sucesso!", 
                event.getAmount().getAmount(), event.getAmount().getCurrency());
        sendEmail(event.getUserId(), "Depósito realizado", message);
        sendPushNotification(event.getUserId(), "Depósito confirmado", message);
        log.info("Notificação de depósito enviada para carteira: {}", event.getUserId());
    }
    
    /**
     * Envia notificação de saque realizado.
     */
    @EventListener
    @Async("notificationExecutor")
    public void sendWithdrawNotification(MoneyWithdrawnEvent event) {
        log.info("Enviando notificação de saque para carteira: {}", event.getUserId());
        String message = String.format("Saque de %.2f %s realizado com sucesso!", 
                event.getAmount().getAmount(), event.getAmount().getCurrency());
        sendEmail(event.getUserId(), "Saque realizado", message);
        sendPushNotification(event.getUserId(), "Saque confirmado", message);
        log.info("Notificação de saque enviada para carteira: {}", event.getUserId());
    }
    
    /**
     * Envia notificação de transferência para ambas as carteiras.
     */
    @EventListener
    @Async("notificationExecutor")
    public void sendTransferNotifications(MoneyTransferredEvent event) {
        log.info("Enviando notificações de transferência entre carteiras: {} -> {}", 
                event.getSourceUserId(), event.getTargetUserId());
        String sourceMessage = String.format("Transferência de %.2f %s enviada com sucesso!", 
                event.getAmount().getAmount(), event.getAmount().getCurrency());
        String targetMessage = String.format("Você recebeu %.2f %s na sua carteira!", 
                event.getAmount().getAmount(), event.getAmount().getCurrency());
        sendEmail(event.getSourceUserId(), "Transferência enviada", sourceMessage);
        sendPushNotification(event.getSourceUserId(), "Transferência enviada", sourceMessage);
        sendEmail(event.getTargetUserId(), "Transferência recebida", targetMessage);
        sendPushNotification(event.getTargetUserId(), "Transferência recebida", targetMessage);
        log.info("Notificações de transferência enviadas com sucesso");
    }
    
    /**
     * Envia notificação de carteira suspensa.
     */
    @EventListener
    @Async("notificationExecutor")
    public void sendSuspensionNotification(WalletSuspendedEvent event) {
        log.warn("Enviando notificação de suspensão para usuário: {}", event.getUserId());
        sendEmail(event.getUserId(), "Carteira suspensa", 
                "Sua carteira foi temporariamente suspensa. Entre em contato com o suporte.");
        sendPushNotification(event.getUserId(), "Carteira suspensa", 
                "Sua carteira foi suspensa. Verifique seu email.");
        log.info("Notificação de suspensão enviada para usuário: {}", event.getUserId());
    }
    
    /**
     * Envia notificação de carteira ativada.
     */
    @EventListener
    @Async("notificationExecutor")
    public void sendActivationNotification(WalletActivatedEvent event) {
        log.info("Enviando notificação de ativação para usuário: {}", event.getUserId());
        sendEmail(event.getUserId(), "Carteira ativada", 
                "Sua carteira foi reativada e está funcionando normalmente.");
        sendPushNotification(event.getUserId(), "Carteira ativada", 
                "Sua carteira foi reativada!");
        log.info("Notificação de ativação enviada para usuário: {}", event.getUserId());
    }
    
    /**
     * Envia notificação de carteira fechada.
     */
    @EventListener
    @Async("notificationExecutor")
    public void sendClosureNotification(WalletClosedEvent event) {
        log.info("Enviando notificação de fechamento para usuário: {}", event.getUserId());
        sendEmail(event.getUserId(), "Carteira fechada", 
                "Sua carteira foi fechada conforme solicitado. Obrigado por usar nossos serviços.");
        sendPushNotification(event.getUserId(), "Carteira fechada", 
                "Sua carteira foi fechada.");
        log.info("Notificação de fechamento enviada para usuário: {}", event.getUserId());
    }
    
    // Métodos de integração com serviços de notificação
    
    private void sendEmail(String userId, String subject, String message) {
        // Implementar integração com serviço de email
        log.debug("Enviando email para {}: {} - {}", userId, subject, message);
    }
    
    private void sendPushNotification(String userId, String title, String message) {
        // Implementar integração com serviço de push notification
        log.debug("Enviando push para {}: {} - {}", userId, title, message);
    }
} 