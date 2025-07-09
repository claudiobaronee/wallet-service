package com.wallet.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class WalletDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWalletRequest {
        @NotBlank(message = "userId é obrigatório")
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "userId deve conter apenas letras, números, hífen e underscore")
        @Size(min = 3, max = 50, message = "userId deve ter entre 3 e 50 caracteres")
        private String userId;
        
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency deve ser um código de moeda válido (3 letras maiúsculas)")
        private String currency = "BRL";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWalletResponse {
        private Long id;
        private String userId;
        private BigDecimal balance;
        private String currency;
        private String status;
        private String message;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletResponse {
        private Long id;
        private String userId;
        private BigDecimal balance;
        private String currency;
        private String status;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepositRequest {
        @NotNull(message = "amount é obrigatório")
        @DecimalMin(value = "0.01", message = "amount deve ser maior que zero")
        @Digits(integer = 15, fraction = 2, message = "amount deve ter no máximo 15 dígitos inteiros e 2 decimais")
        private BigDecimal amount;
        
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency deve ser um código de moeda válido")
        private String currency = "BRL";
        
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepositResponse {
        private String message;
        private BigDecimal amount;
        private String currency;
        private BigDecimal newBalance;
        private String transactionId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithdrawRequest {
        @NotNull(message = "amount é obrigatório")
        @DecimalMin(value = "0.01", message = "amount deve ser maior que zero")
        @Digits(integer = 15, fraction = 2, message = "amount deve ter no máximo 15 dígitos inteiros e 2 decimais")
        private BigDecimal amount;
        
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency deve ser um código de moeda válido")
        private String currency = "BRL";
        
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithdrawResponse {
        private String message;
        private BigDecimal amount;
        private String currency;
        private BigDecimal newBalance;
        private String transactionId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRequest {
        @NotBlank(message = "targetUserId é obrigatório")
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "targetUserId deve conter apenas letras, números, hífen e underscore")
        @Size(min = 3, max = 50, message = "targetUserId deve ter entre 3 e 50 caracteres")
        private String targetUserId;
        
        @NotNull(message = "amount é obrigatório")
        @DecimalMin(value = "0.01", message = "amount deve ser maior que zero")
        @Digits(integer = 15, fraction = 2, message = "amount deve ter no máximo 15 dígitos inteiros e 2 decimais")
        private BigDecimal amount;
        
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency deve ser um código de moeda válido")
        private String currency = "BRL";
        
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferResponse {
        private String message;
        private BigDecimal amount;
        private String currency;
        private String sourceUserId;
        private String targetUserId;
        private BigDecimal sourceNewBalance;
        private BigDecimal targetNewBalance;
        private String transactionId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BalanceHistoryResponse {
        private String userId;
        private BigDecimal currentBalance;
        private String currency;
        private List<BalanceHistoryEntry> history;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BalanceHistoryEntry {
        private BigDecimal balance;
        private String currency;
        private String description;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime recordedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthResponse {
        private String status;
        private String service;
        private String version;
        private Long timestamp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime timestamp;
    }
} 