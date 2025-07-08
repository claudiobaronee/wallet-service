package com.wallet.domain.entities;

import com.wallet.domain.enums.WalletStatus;
import com.wallet.domain.valueobjects.Money;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
@EntityListeners(AuditingEntityListener.class)
public class Wallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "balance")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money balance;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WalletStatus status;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();
    
    protected Wallet() {
        // Para JPA
    }
    
    public Wallet(String userId, Money initialBalance) {
        this.userId = userId;
        this.balance = initialBalance;
        this.status = WalletStatus.ACTIVE;
    }
    
    public static Wallet create(String userId, String currency) {
        Money initialBalance = Money.zero(currency);
        return new Wallet(userId, initialBalance);
    }
    
    public void deposit(Money amount) {
        if (status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Wallet is not active");
        }
        if (!balance.getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        this.balance = this.balance.add(amount);
    }
    
    public void withdraw(Money amount) {
        if (status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Wallet is not active");
        }
        if (!balance.getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        this.balance = this.balance.subtract(amount);
    }
    
    public void transferTo(Wallet targetWallet, Money amount) {
        if (status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Source wallet is not active");
        }
        if (targetWallet.status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Target wallet is not active");
        }
        if (!balance.getCurrency().equals(amount.getCurrency()) || 
            !targetWallet.balance.getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        
        this.withdraw(amount);
        targetWallet.deposit(amount);
    }
    
    public void suspend() {
        if (status == WalletStatus.CLOSED) {
            throw new IllegalStateException("Cannot suspend a closed wallet");
        }
        this.status = WalletStatus.SUSPENDED;
    }
    
    public void activate() {
        if (status == WalletStatus.CLOSED) {
            throw new IllegalStateException("Cannot activate a closed wallet");
        }
        this.status = WalletStatus.ACTIVE;
    }
    
    public void close() {
        if (balance.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Cannot close wallet with positive balance");
        }
        this.status = WalletStatus.CLOSED;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public Money getBalance() {
        return balance;
    }
    
    public WalletStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    public boolean isActive() {
        return status == WalletStatus.ACTIVE;
    }
    
    public boolean hasSufficientFunds(Money amount) {
        return balance.isGreaterThan(amount) || balance.equals(amount);
    }
} 