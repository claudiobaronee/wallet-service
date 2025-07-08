package com.wallet.domain.entities;

import com.wallet.domain.enums.TransactionType;
import com.wallet.domain.valueobjects.Money;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TransactionEntity {
    
    private final String id;
    private final String walletId;
    private final TransactionType type;
    private final Money amount;
    private final String description;
    private final LocalDateTime createdAt;
    
    private TransactionEntity(String walletId, TransactionType type, Money amount, String description) {
        this.id = UUID.randomUUID().toString();
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
    
    public static TransactionEntity create(String walletId, TransactionType type, Money amount, String description) {
        return new TransactionEntity(walletId, type, amount, description);
    }

    public String getUserId() {
        return this.walletId;
    }
    public LocalDateTime getTimestamp() {
        return this.createdAt;
    }
    public String getRelatedTransactionId() {
        return null; // Não implementado nesta versão
    }
} 