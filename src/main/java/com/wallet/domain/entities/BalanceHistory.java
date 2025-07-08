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
    
    private LocalDateTime recordedAt;
    
    public BalanceHistory(Long walletId, Money balance) {
        this.walletId = walletId;
        this.balanceAmount = balance.getAmount();
        this.balanceCurrency = balance.getCurrency();
        this.recordedAt = LocalDateTime.now();
    }
    
    public static BalanceHistory create(Long walletId, Money balance) {
        return new BalanceHistory(walletId, balance);
    }
    
    public Money getBalance() {
        return new Money(balanceAmount, balanceCurrency);
    }
    
    public void setBalance(Money balance) {
        this.balanceAmount = balance.getAmount();
        this.balanceCurrency = balance.getCurrency();
    }
} 