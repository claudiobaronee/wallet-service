package com.wallet.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Métricas simplificadas para o Wallet Service.
 * Implementa monitoramento essencial de performance e negócio.
 */
@Slf4j
@Component
public class WalletMetrics {

    private final MeterRegistry meterRegistry;

    // Contadores principais
    private final Counter walletCreatedCounter;
    private final Counter walletDepositCounter;
    private final Counter walletWithdrawCounter;
    private final Counter walletTransferCounter;
    private final Counter transactionCreatedCounter;
    private final Counter failedTransactionCounter;

    // Timers principais
    private final Timer walletCreationTimer;
    private final Timer depositTimer;
    private final Timer withdrawTimer;
    private final Timer transferTimer;

    public WalletMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Inicializar contadores
        this.walletCreatedCounter = Counter.builder("wallet.created")
            .description("Number of wallets created")
            .register(meterRegistry);

        this.walletDepositCounter = Counter.builder("wallet.deposit")
            .description("Number of deposits")
            .register(meterRegistry);

        this.walletWithdrawCounter = Counter.builder("wallet.withdraw")
            .description("Number of withdrawals")
            .register(meterRegistry);

        this.walletTransferCounter = Counter.builder("wallet.transfer")
            .description("Number of transfers")
            .register(meterRegistry);

        this.transactionCreatedCounter = Counter.builder("transaction.created")
            .description("Number of transactions created")
            .register(meterRegistry);

        this.failedTransactionCounter = Counter.builder("transaction.failed")
            .description("Number of failed transactions")
            .register(meterRegistry);

        // Inicializar timers
        this.walletCreationTimer = Timer.builder("wallet.creation.time")
            .description("Time to create a wallet")
            .register(meterRegistry);

        this.depositTimer = Timer.builder("wallet.deposit.time")
            .description("Time to process a deposit")
            .register(meterRegistry);

        this.withdrawTimer = Timer.builder("wallet.withdraw.time")
            .description("Time to process a withdrawal")
            .register(meterRegistry);

        this.transferTimer = Timer.builder("wallet.transfer.time")
            .description("Time to process a transfer")
            .register(meterRegistry);
    }

    // Métodos para incrementar contadores
    public void incrementWalletCreated() {
        walletCreatedCounter.increment();
        log.debug("Wallet created counter incremented");
    }

    public void incrementWalletDeposit() {
        walletDepositCounter.increment();
        log.debug("Wallet deposit counter incremented");
    }

    public void incrementWalletWithdraw() {
        walletWithdrawCounter.increment();
        log.debug("Wallet withdraw counter incremented");
    }

    public void incrementWalletTransfer() {
        walletTransferCounter.increment();
        log.debug("Wallet transfer counter incremented");
    }

    public void incrementTransactionCreated() {
        transactionCreatedCounter.increment();
        log.debug("Transaction created counter incremented");
    }

    public void incrementFailedTransaction() {
        failedTransactionCounter.increment();
        log.warn("Failed transaction counter incremented");
    }

    // Métodos para timers
    public Timer.Sample startWalletCreationTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopWalletCreationTimer(Timer.Sample sample) {
        sample.stop(walletCreationTimer);
        log.debug("Wallet creation timer stopped");
    }

    public Timer.Sample startDepositTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopDepositTimer(Timer.Sample sample) {
        sample.stop(depositTimer);
        log.debug("Deposit timer stopped");
    }

    public Timer.Sample startWithdrawTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopWithdrawTimer(Timer.Sample sample) {
        sample.stop(withdrawTimer);
        log.debug("Withdraw timer stopped");
    }

    public Timer.Sample startTransferTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTransferTimer(Timer.Sample sample) {
        sample.stop(transferTimer);
        log.debug("Transfer timer stopped");
    }

    // Métodos para métricas customizadas
    public void recordTransactionValue(String currency, double amount) {
        meterRegistry.counter("transaction.value", "currency", currency).increment(amount);
        log.debug("Transaction value recorded: {} {}", amount, currency);
    }

    public void recordError(String errorType, String errorCode) {
        meterRegistry.counter("error", "type", errorType, "code", errorCode).increment();
        log.warn("Error recorded: {} - {}", errorType, errorCode);
    }
} 