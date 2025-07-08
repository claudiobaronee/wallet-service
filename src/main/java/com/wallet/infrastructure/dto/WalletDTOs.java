package com.wallet.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTOs para a API de Wallet
 */
public class WalletDTOs {

    @Schema(description = "Request para criar carteira")
    public static class CreateWalletRequest {
        @Schema(description = "ID do usuário", example = "user123", required = true)
        public String userId;
        
        @Schema(description = "Moeda da carteira", example = "BRL", defaultValue = "BRL")
        public String currency;
    }

    @Schema(description = "Response de carteira criada")
    public static class CreateWalletResponse {
        @Schema(description = "ID da carteira", example = "1")
        public Long id;
        
        @Schema(description = "ID do usuário", example = "user123")
        public String userId;
        
        @Schema(description = "Saldo atual", example = "0.00")
        public BigDecimal balance;
        
        @Schema(description = "Moeda", example = "BRL")
        public String currency;
        
        @Schema(description = "Status da carteira", example = "ACTIVE")
        public String status;
        
        @Schema(description = "Mensagem de confirmação", example = "Carteira criada com sucesso")
        public String message;
    }

    @Schema(description = "Request para depósito")
    public static class DepositRequest {
        @Schema(description = "Valor do depósito", example = "100.50", required = true)
        public BigDecimal amount;
        
        @Schema(description = "Moeda", example = "BRL", defaultValue = "BRL")
        public String currency;
    }

    @Schema(description = "Response de depósito")
    public static class DepositResponse {
        @Schema(description = "Mensagem de confirmação", example = "Depósito realizado com sucesso")
        public String message;
        
        @Schema(description = "Valor depositado", example = "100.50")
        public BigDecimal amount;
        
        @Schema(description = "Moeda", example = "BRL")
        public String currency;
        
        @Schema(description = "Novo saldo", example = "100.50")
        public BigDecimal newBalance;
        
        @Schema(description = "ID da transação", example = "1704067200000")
        public Long transactionId;
    }

    @Schema(description = "Request para saque")
    public static class WithdrawRequest {
        @Schema(description = "Valor do saque", example = "50.25", required = true)
        public BigDecimal amount;
        
        @Schema(description = "Moeda", example = "BRL", defaultValue = "BRL")
        public String currency;
    }

    @Schema(description = "Response de saque")
    public static class WithdrawResponse {
        @Schema(description = "Mensagem de confirmação", example = "Saque realizado com sucesso")
        public String message;
        
        @Schema(description = "Valor sacado", example = "50.25")
        public BigDecimal amount;
        
        @Schema(description = "Moeda", example = "BRL")
        public String currency;
        
        @Schema(description = "Novo saldo", example = "50.25")
        public BigDecimal newBalance;
        
        @Schema(description = "ID da transação", example = "1704067200000")
        public Long transactionId;
    }

    @Schema(description = "Request para transferência")
    public static class TransferRequest {
        @Schema(description = "ID do usuário destino", example = "user456", required = true)
        public String targetUserId;
        
        @Schema(description = "Valor da transferência", example = "25.00", required = true)
        public BigDecimal amount;
        
        @Schema(description = "Moeda", example = "BRL", defaultValue = "BRL")
        public String currency;
    }

    @Schema(description = "Response de transferência")
    public static class TransferResponse {
        @Schema(description = "Mensagem de confirmação", example = "Transferência realizada com sucesso")
        public String message;
        
        @Schema(description = "Valor transferido", example = "25.00")
        public BigDecimal amount;
        
        @Schema(description = "Moeda", example = "BRL")
        public String currency;
        
        @Schema(description = "ID do usuário origem", example = "user123")
        public String sourceUserId;
        
        @Schema(description = "ID do usuário destino", example = "user456")
        public String targetUserId;
        
        @Schema(description = "Novo saldo da carteira origem", example = "75.00")
        public BigDecimal sourceNewBalance;
        
        @Schema(description = "Novo saldo da carteira destino", example = "25.00")
        public BigDecimal targetNewBalance;
        
        @Schema(description = "ID da transação", example = "1704067200000")
        public Long transactionId;
    }

    @Schema(description = "Response de carteira")
    public static class WalletResponse {
        @Schema(description = "ID da carteira", example = "1")
        public Long id;
        
        @Schema(description = "ID do usuário", example = "user123")
        public String userId;
        
        @Schema(description = "Saldo atual", example = "100.50")
        public BigDecimal balance;
        
        @Schema(description = "Moeda", example = "BRL")
        public String currency;
        
        @Schema(description = "Status da carteira", example = "ACTIVE")
        public String status;
        
        @Schema(description = "Data de criação", example = "2024-01-01T10:00:00")
        public LocalDateTime createdAt;
        
        @Schema(description = "Data de atualização", example = "2024-01-01T10:00:00")
        public LocalDateTime updatedAt;
    }

    @Schema(description = "Response de histórico de saldo")
    public static class BalanceHistoryResponse {
        @Schema(description = "ID do usuário", example = "user123")
        public String userId;
        
        @Schema(description = "Saldo atual", example = "100.50")
        public BigDecimal currentBalance;
        
        @Schema(description = "Moeda", example = "BRL")
        public String currency;
        
        @Schema(description = "Histórico de saldos")
        public List<BalanceHistoryEntry> history;
    }

    @Schema(description = "Entrada do histórico de saldo")
    public static class BalanceHistoryEntry {
        @Schema(description = "Saldo", example = "100.50")
        public BigDecimal balance;
        
        @Schema(description = "Moeda", example = "BRL")
        public String currency;
        
        @Schema(description = "Descrição da operação", example = "Depósito de 100.50 BRL")
        public String description;
        
        @Schema(description = "Data do registro", example = "2024-01-01T10:00:00")
        public LocalDateTime recordedAt;
    }

    @Schema(description = "Response de health check")
    public static class HealthResponse {
        @Schema(description = "Status do serviço", example = "UP")
        public String status;
        
        @Schema(description = "Nome do serviço", example = "wallet-service")
        public String service;
        
        @Schema(description = "Versão", example = "1.0.0")
        public String version;
        
        @Schema(description = "Timestamp", example = "1704067200000")
        public Long timestamp;
    }

    @Schema(description = "Response de erro")
    public static class ErrorResponse {
        @Schema(description = "Mensagem de erro", example = "Saldo insuficiente")
        public String error;
    }
} 