package com.wallet.domain.entities;

import com.wallet.domain.enums.TransactionType;
import com.wallet.domain.valueobjects.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private Long id;
    private Long walletId;
    private TransactionType transactionType;
    private Money amount;
    private String description;
    private Long sourceWalletId;
    private Long targetWalletId;
    private LocalDateTime transactionDate;
    private String status;
    private LocalDateTime createdAt;
    public Transaction(Long walletId, TransactionType transactionType, Money amount, String description) {
        this.walletId = walletId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
        this.status = "COMPLETED";
        this.createdAt = LocalDateTime.now();
    }
    public static Transaction createDeposit(Wallet wallet, Money amount, String description) {
        return new Transaction(wallet.getId(), TransactionType.DEPOSIT, amount, description);
    }
    public static Transaction createWithdraw(Wallet wallet, Money amount, String description) {
        return new Transaction(wallet.getId(), TransactionType.WITHDRAW, amount, description);
    }
    public static Transaction createTransfer(Wallet sourceWallet, Wallet targetWallet, Money amount, String description) {
        Transaction transaction = new Transaction(sourceWallet.getId(), TransactionType.TRANSFER, amount, description);
        transaction.setSourceWalletId(sourceWallet.getId());
        transaction.setTargetWalletId(targetWallet.getId());
        return transaction;
    }
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
} 