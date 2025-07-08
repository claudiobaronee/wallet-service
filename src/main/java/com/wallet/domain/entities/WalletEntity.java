package com.wallet.domain.entities;

import com.wallet.domain.enums.WalletStatus;
import com.wallet.domain.enums.TransactionType;
import com.wallet.domain.valueobjects.Money;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class WalletEntity {
    
    private final String id;
    private final String userId;
    private Money balance;
    private WalletStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<TransactionEntity> transactions;
    
    private WalletEntity(String userId, String currency) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.balance = Money.zero(currency);
        this.status = WalletStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }
    
    public static WalletEntity create(String userId, String currency) {
        return new WalletEntity(userId, currency);
    }
    
    public void deposit(Money amount) {
        validateWalletStatus();
        validateCurrency(amount);
        
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
        
        TransactionEntity transaction = TransactionEntity.create(
            this.id, 
            TransactionType.DEPOSIT, 
            amount, 
            "Deposit"
        );
        this.transactions.add(transaction);
    }
    
    public void withdraw(Money amount) {
        validateWalletStatus();
        validateCurrency(amount);
        validateSufficientFunds(amount);
        
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
        
        TransactionEntity transaction = TransactionEntity.create(
            this.id, 
            TransactionType.WITHDRAW, 
            amount, 
            "Withdrawal"
        );
        this.transactions.add(transaction);
    }
    
    public void transferTo(WalletEntity targetWallet, Money amount) {
        validateWalletStatus();
        targetWallet.validateWalletStatus();
        validateCurrency(amount);
        validateSufficientFunds(amount);
        
        this.balance = this.balance.subtract(amount);
        targetWallet.balance = targetWallet.balance.add(amount);
        
        this.updatedAt = LocalDateTime.now();
        targetWallet.updatedAt = LocalDateTime.now();
        
        TransactionEntity sourceTransaction = TransactionEntity.create(
            this.id, 
            TransactionType.TRANSFER, 
            amount, 
            "Transfer to " + targetWallet.getUserId()
        );
        this.transactions.add(sourceTransaction);
        
        TransactionEntity targetTransaction = TransactionEntity.create(
            targetWallet.getId(), 
            TransactionType.TRANSFER, 
            amount, 
            "Transfer from " + this.userId
        );
        targetWallet.transactions.add(targetTransaction);
    }
    
    public void suspend() {
        if (this.status == WalletStatus.CLOSED) {
            throw new IllegalStateException("Cannot suspend a closed wallet");
        }
        this.status = WalletStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void activate() {
        if (this.status == WalletStatus.CLOSED) {
            throw new IllegalStateException("Cannot activate a closed wallet");
        }
        this.status = WalletStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void close() {
        if (!this.balance.isZero()) {
            throw new IllegalStateException("Cannot close wallet with non-zero balance");
        }
        this.status = WalletStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }
    
    private void validateWalletStatus() {
        if (this.status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Wallet is not active. Current status: " + this.status);
        }
    }
    
    private void validateCurrency(Money amount) {
        if (!this.balance.getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch. Expected: " + this.balance.getCurrency() + ", Got: " + amount.getCurrency());
        }
    }
    
    private void validateSufficientFunds(Money amount) {
        if (this.balance.subtract(amount).isNegative()) {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }
    
    public List<TransactionEntity> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
} 