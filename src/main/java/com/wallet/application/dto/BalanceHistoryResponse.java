package com.wallet.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceHistoryResponse {
    private String userId;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime date;
    private LocalDateTime lastUpdated;
} 