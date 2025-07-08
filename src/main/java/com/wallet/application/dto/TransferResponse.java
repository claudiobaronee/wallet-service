package com.wallet.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private String sourceUserId;
    private String targetUserId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDateTime timestamp;
    private String transactionId;
} 