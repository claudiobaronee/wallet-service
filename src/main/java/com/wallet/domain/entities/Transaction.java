package com.wallet.domain.entities;

import com.wallet.domain.enums.TransactionType;
import com.wallet.domain.valueobjects.Money;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money amount;
    
    @Column(name = "description")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_wallet_id")
    private Wallet sourceWallet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_wallet_id")
    private Wallet targetWallet;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    protected Transaction() {
        // Para JPA
    }
    
    public Transaction(Wallet wallet, TransactionType transactionType, Money amount, String description) {
        this.wallet = wallet;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
        this.status = "COMPLETED";
    }
    
    public static Transaction createDeposit(Wallet wallet, Money amount, String description) {
        return new Transaction(wallet, TransactionType.DEPOSIT, amount, description);
    }
    
    public static Transaction createWithdraw(Wallet wallet, Money amount, String description) {
        return new Transaction(wallet, TransactionType.WITHDRAW, amount, description);
    }
    
    public static Transaction createTransfer(Wallet sourceWallet, Wallet targetWallet, Money amount, String description) {
        Transaction transaction = new Transaction(sourceWallet, TransactionType.TRANSFER, amount, description);
        transaction.targetWallet = targetWallet;
        return transaction;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Wallet getWallet() {
        return wallet;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public Money getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Wallet getSourceWallet() {
        return sourceWallet;
    }
    
    public Wallet getTargetWallet() {
        return targetWallet;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
    
    public void setSourceWallet(Wallet sourceWallet) {
        this.sourceWallet = sourceWallet;
    }
    
    public void setTargetWallet(Wallet targetWallet) {
        this.targetWallet = targetWallet;
    }
} 