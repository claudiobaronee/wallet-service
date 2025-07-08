package com.wallet.domain.entities;

import com.wallet.domain.enums.WalletStatus;
import com.wallet.domain.valueobjects.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    
    @Id
    private Long id;
    
    private String userId;
    
    @Column("balance_amount")
    private BigDecimal balanceAmount;
    
    @Column("balance_currency")
    private String balanceCurrency;
    
    private WalletStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Wallet(String userId, Money balance) {
        this.userId = userId;
        this.balanceAmount = balance.getAmount();
        this.balanceCurrency = balance.getCurrency();
        this.status = WalletStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public static Wallet create(String userId, String currency) {
        return new Wallet(userId, Money.zero(currency));
    }
    
    public Money getBalance() {
        return new Money(balanceAmount, balanceCurrency);
    }
    
    public void setBalance(Money balance) {
        this.balanceAmount = balance.getAmount();
        this.balanceCurrency = balance.getCurrency();
    }
    
    public void deposit(Money amount) {
        if (status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Wallet is not active");
        }
        if (!this.balanceCurrency.equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        this.balanceAmount = this.balanceAmount.add(amount.getAmount());
        this.updatedAt = LocalDateTime.now();
    }
    
    public void withdraw(Money amount) {
        if (status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Wallet is not active");
        }
        if (!this.balanceCurrency.equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        this.balanceAmount = this.balanceAmount.subtract(amount.getAmount());
        this.updatedAt = LocalDateTime.now();
    }
    
    public void transferTo(Wallet targetWallet, Money amount) {
        if (status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Source wallet is not active");
        }
        if (targetWallet.status != WalletStatus.ACTIVE) {
            throw new IllegalStateException("Target wallet is not active");
        }
        if (!this.balanceCurrency.equals(amount.getCurrency()) || 
            !targetWallet.balanceCurrency.equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        
        this.withdraw(amount);
        targetWallet.deposit(amount);
    }
    
    public boolean hasSufficientFunds(Money amount) {
        return this.balanceAmount.compareTo(amount.getAmount()) >= 0;
    }
    
    public boolean isActive() {
        return status == WalletStatus.ACTIVE;
    }
    
    public void activate() {
        this.status = WalletStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void suspend() {
        this.status = WalletStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void close() {
        this.status = WalletStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Cria um registro de histórico de saldo para rastreabilidade
     * 
     * @return BalanceHistory com o saldo atual
     */
    public BalanceHistory createBalanceHistory() {
        return new BalanceHistory(this.id, this.getBalance());
    }
} 