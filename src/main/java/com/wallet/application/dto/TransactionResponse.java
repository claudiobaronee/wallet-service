package com.wallet.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String id;
    private String userId;
    private String type;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDateTime timestamp;
    private String relatedTransactionId;
} 