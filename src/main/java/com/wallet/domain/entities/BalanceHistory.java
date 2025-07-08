package com.wallet.domain.entities;

import com.wallet.domain.valueobjects.Money;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "balance_history")
@EntityListeners(AuditingEntityListener.class)
public class BalanceHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "balance")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money balance;
    
    @CreatedDate
    @Column(name = "recorded_at", nullable = false, updatable = false)
    private LocalDateTime recordedAt;
    
    protected BalanceHistory() {
        // Para JPA
    }
    
    public BalanceHistory(Wallet wallet, Money balance) {
        this.wallet = wallet;
        this.balance = balance;
        this.recordedAt = LocalDateTime.now();
    }
    
    public static BalanceHistory record(Wallet wallet, Money balance) {
        return new BalanceHistory(wallet, balance);
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Wallet getWallet() {
        return wallet;
    }
    
    public Money getBalance() {
        return balance;
    }
    
    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
    
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
} 