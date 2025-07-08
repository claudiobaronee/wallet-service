package com.wallet.domain.entities;

import com.wallet.domain.valueobjects.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("balance_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceHistory {
    @Id
    private Long id;
    private Long walletId;
    @Column("balance_amount")
    private BigDecimal balanceAmount;
    @Column("balance_currency")
    private String balanceCurrency;
    private String description;
    private LocalDateTime recordedAt;
    public BalanceHistory(Long walletId, Money balance) {
        this.walletId = walletId;
        this.balanceAmount = balance.getAmount();
        this.balanceCurrency = balance.getCurrency();
        this.recordedAt = LocalDateTime.now();
    }
    public BalanceHistory(Long walletId, Money balance, String description) {
        this.walletId = walletId;
        this.balanceAmount = balance.getAmount();
        this.balanceCurrency = balance.getCurrency();
        this.description = description;
        this.recordedAt = LocalDateTime.now();
    }
    public static BalanceHistory create(Long walletId, Money balance) {
        return new BalanceHistory(walletId, balance);
    }
    public static BalanceHistory create(Long walletId, Money balance, String description) {
        return new BalanceHistory(walletId, balance, description);
    }
    public Money getBalance() {
        return new Money(balanceAmount, balanceCurrency);
    }
    public void setBalance(Money balance) {
        this.balanceAmount = balance.getAmount();
        this.balanceCurrency = balance.getCurrency();
    }
    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
} 