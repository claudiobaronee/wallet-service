package com.wallet.domain.aggregates;

import com.wallet.domain.entities.Transaction;
import com.wallet.domain.entities.Wallet;
import com.wallet.domain.enums.TransactionType;
import com.wallet.domain.events.*;
import com.wallet.domain.valueobjects.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agregado Wallet - Raiz do agregado que contém Wallet e suas transações
 * Responsável por garantir a consistência do domínio
 */
public class WalletAggregate {
    
    private Wallet wallet;
    private List<Transaction> transactions;
    private List<DomainEvent> domainEvents;
    
    public WalletAggregate(Wallet wallet) {
        this.wallet = wallet;
        this.transactions = new ArrayList<>();
        this.domainEvents = new ArrayList<>();
    }
    
    public static WalletAggregate create(String userId, String currency) {
        Wallet wallet = Wallet.create(userId, currency);
        WalletAggregate aggregate = new WalletAggregate(wallet);
        
        aggregate.addDomainEvent(new WalletCreatedEvent(userId, currency, LocalDateTime.now()));
        
        return aggregate;
    }
    
    public void deposit(Money amount, String description) {
        if (!wallet.isActive()) {
            throw new IllegalStateException("Wallet is not active");
        }
        
        if (!wallet.getBalance().getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        
        Money oldBalance = wallet.getBalance();
        wallet.deposit(amount);
        
        Transaction transaction = Transaction.createDeposit(wallet, amount, description);
        transactions.add(transaction);
        addDomainEvent(new MoneyDepositedEvent(
            wallet.getUserId(), 
            amount, 
            oldBalance, 
            wallet.getBalance(), 
            LocalDateTime.now()
        ));
        
        addDomainEvent(new TransactionCreatedEvent(
            transaction.getTransactionType(),
            wallet.getUserId(),
            amount,
            description,
            LocalDateTime.now()
        ));
    }
    
    public void withdraw(Money amount, String description) {
        if (!wallet.isActive()) {
            throw new IllegalStateException("Wallet is not active");
        }
        
        if (!wallet.getBalance().getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        
        if (!wallet.hasSufficientFunds(amount)) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        Money oldBalance = wallet.getBalance();
        wallet.withdraw(amount);
        
        Transaction transaction = Transaction.createWithdraw(wallet, amount, description);
        transactions.add(transaction);
        addDomainEvent(new MoneyWithdrawnEvent(
            wallet.getUserId(), 
            amount, 
            oldBalance, 
            wallet.getBalance(), 
            LocalDateTime.now()
        ));
        
        addDomainEvent(new TransactionCreatedEvent(
            transaction.getTransactionType(),
            wallet.getUserId(),
            amount,
            description,
            LocalDateTime.now()
        ));
    }
    
    public void transferTo(WalletAggregate targetAggregate, Money amount, String description) {
        if (!wallet.isActive()) {
            throw new IllegalStateException("Source wallet is not active");
        }
        
        if (!targetAggregate.getWallet().isActive()) {
            throw new IllegalStateException("Target wallet is not active");
        }
        
        if (!wallet.getBalance().getCurrency().equals(amount.getCurrency()) ||
            !targetAggregate.getWallet().getBalance().getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch between wallets");
        }
        
        if (!wallet.hasSufficientFunds(amount)) {
            throw new IllegalArgumentException("Insufficient funds in source wallet");
        }
        
        Money sourceOldBalance = wallet.getBalance();
        Money targetOldBalance = targetAggregate.getWallet().getBalance();
        
        wallet.transferTo(targetAggregate.getWallet(), amount);
        
        Transaction sourceTransaction = Transaction.createTransfer(
            wallet, 
            targetAggregate.getWallet(), 
            amount, 
            "Transfer to " + targetAggregate.getWallet().getUserId() + ": " + description
        );
        transactions.add(sourceTransaction);
        
        Transaction targetTransaction = Transaction.createDeposit(
            targetAggregate.getWallet(), 
            amount, 
            "Transfer from " + wallet.getUserId() + ": " + description
        );
        targetAggregate.transactions.add(targetTransaction);
        addDomainEvent(new MoneyTransferredEvent(
            wallet.getUserId(),
            targetAggregate.getWallet().getUserId(),
            amount,
            sourceOldBalance,
            wallet.getBalance(),
            LocalDateTime.now()
        ));
        
        targetAggregate.addDomainEvent(new MoneyTransferredEvent(
            wallet.getUserId(),
            targetAggregate.getWallet().getUserId(),
            amount,
            targetOldBalance,
            targetAggregate.getWallet().getBalance(),
            LocalDateTime.now()
        ));
    }
    
    public void suspend() {
        if (wallet.getStatus().name().equals("CLOSED")) {
            throw new IllegalStateException("Cannot suspend a closed wallet");
        }
        
        wallet.suspend();
        addDomainEvent(new WalletSuspendedEvent(wallet.getUserId(), LocalDateTime.now()));
    }
    
    public void activate() {
        if (wallet.getStatus().name().equals("CLOSED")) {
            throw new IllegalStateException("Cannot activate a closed wallet");
        }
        
        wallet.activate();
        addDomainEvent(new WalletActivatedEvent(wallet.getUserId(), LocalDateTime.now()));
    }
    
    public void close() {
        if (wallet.getBalance().getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Cannot close wallet with positive balance");
        }
        
        wallet.close();
        addDomainEvent(new WalletClosedEvent(wallet.getUserId(), LocalDateTime.now()));
    }
    
    private void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
    
    public Wallet getWallet() {
        return wallet;
    }
    
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
    
    public String getUserId() {
        return wallet.getUserId();
    }
    
    public Money getBalance() {
        return wallet.getBalance();
    }
    
    public boolean isActive() {
        return wallet.isActive();
    }
} 