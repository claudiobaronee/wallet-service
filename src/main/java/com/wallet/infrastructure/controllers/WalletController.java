package com.wallet.infrastructure.controllers;

import com.wallet.domain.entities.Wallet;
import com.wallet.domain.valueobjects.Money;
import com.wallet.adapters.infrastructure.repositories.WalletRepositoryImpl;
import com.wallet.adapters.infrastructure.repositories.BalanceHistoryRepositoryImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@CrossOrigin(origins = "*")
@Tag(name = "Wallet Service", description = "API para gerenciamento de carteiras digitais")
public class WalletController {

    @Autowired
    private WalletRepositoryImpl walletRepository;
    
    @Autowired
    private BalanceHistoryRepositoryImpl balanceHistoryRepository;

    @Operation(summary = "Criar carteira", description = "Cria uma nova carteira para um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carteira criada com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CreateWalletResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createWallet(
            @Parameter(description = "Dados da carteira", required = true,
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CreateWalletRequest.class),
                    examples = @ExampleObject(value = "{\"userId\": \"user123\", \"currency\": \"BRL\"}")))
            @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String currency = request.getOrDefault("currency", "BRL");
            
            if (userId == null || userId.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "userId é obrigatório");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (walletRepository.existsByUserId(userId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Carteira já existe para este usuário");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Wallet wallet = Wallet.create(userId, currency);
            Wallet savedWallet = walletRepository.save(wallet);
            
            // Registrar histórico de saldo inicial
            balanceHistoryRepository.save(savedWallet.createBalanceHistory());
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedWallet.getId());
            response.put("userId", savedWallet.getUserId());
            response.put("balance", savedWallet.getBalance().getAmount());
            response.put("currency", savedWallet.getBalance().getCurrency());
            response.put("status", savedWallet.getStatus().name());
            response.put("message", "Carteira criada com sucesso");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao criar carteira: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(summary = "Consultar carteira", description = "Consulta os detalhes de uma carteira")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carteira encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = WalletResponse.class))),
        @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getWallet(
            @Parameter(description = "ID do usuário", required = true, example = "user123")
            @PathVariable String userId) {
        try {
            return walletRepository.findByUserId(userId)
                    .map(wallet -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("id", wallet.getId());
                        response.put("userId", wallet.getUserId());
                        response.put("balance", wallet.getBalance().getAmount());
                        response.put("currency", wallet.getBalance().getCurrency());
                        response.put("status", wallet.getStatus().name());
                        response.put("createdAt", wallet.getCreatedAt());
                        response.put("updatedAt", wallet.getUpdatedAt());
                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao buscar carteira: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(summary = "Realizar depósito", description = "Realiza um depósito na carteira do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DepositResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou carteira inativa",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    })
    @PostMapping("/{userId}/deposit")
    public ResponseEntity<Map<String, Object>> deposit(
            @Parameter(description = "ID do usuário", required = true, example = "user123")
            @PathVariable String userId,
            @Parameter(description = "Dados do depósito", required = true,
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DepositRequest.class),
                    examples = @ExampleObject(value = "{\"amount\": 100.50, \"currency\": \"BRL\"}")))
            @RequestBody Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = (String) request.getOrDefault("currency", "BRL");
            
            return walletRepository.findByUserId(userId)
                    .map(wallet -> {
                        try {
                            Money money = new Money(amount, currency);
                            wallet.deposit(money);
                            Wallet savedWallet = walletRepository.save(wallet);
                            
                            // Registrar histórico de saldo
                            balanceHistoryRepository.save(savedWallet.createBalanceHistory());
                            
                            Map<String, Object> response = new HashMap<>();
                            response.put("message", "Depósito realizado com sucesso");
                            response.put("amount", amount);
                            response.put("currency", currency);
                            response.put("newBalance", savedWallet.getBalance().getAmount());
                            response.put("transactionId", System.currentTimeMillis()); // ID único para rastreabilidade
                            return ResponseEntity.ok(response);
                        } catch (Exception e) {
                            Map<String, Object> errorResponse = new HashMap<>();
                            errorResponse.put("error", e.getMessage());
                            return ResponseEntity.badRequest().body(errorResponse);
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao realizar depósito: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(summary = "Realizar saque", description = "Realiza um saque da carteira do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saque realizado com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = WithdrawResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos, saldo insuficiente ou carteira inativa",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    })
    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(
            @Parameter(description = "ID do usuário", required = true, example = "user123")
            @PathVariable String userId,
            @Parameter(description = "Dados do saque", required = true,
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = WithdrawRequest.class),
                    examples = @ExampleObject(value = "{\"amount\": 50.25, \"currency\": \"BRL\"}")))
            @RequestBody Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = (String) request.getOrDefault("currency", "BRL");
            
            return walletRepository.findByUserId(userId)
                    .map(wallet -> {
                        try {
                            Money money = new Money(amount, currency);
                            if (!wallet.hasSufficientFunds(money)) {
                                Map<String, Object> errorResponse = new HashMap<>();
                                errorResponse.put("error", "Saldo insuficiente");
                                return ResponseEntity.badRequest().body(errorResponse);
                            }
                            
                            wallet.withdraw(money);
                            Wallet savedWallet = walletRepository.save(wallet);
                            
                            // Registrar histórico de saldo
                            balanceHistoryRepository.save(savedWallet.createBalanceHistory());
                            
                            Map<String, Object> response = new HashMap<>();
                            response.put("message", "Saque realizado com sucesso");
                            response.put("amount", amount);
                            response.put("currency", currency);
                            response.put("newBalance", savedWallet.getBalance().getAmount());
                            response.put("transactionId", System.currentTimeMillis()); // ID único para rastreabilidade
                            return ResponseEntity.ok(response);
                        } catch (Exception e) {
                            Map<String, Object> errorResponse = new HashMap<>();
                            errorResponse.put("error", e.getMessage());
                            return ResponseEntity.badRequest().body(errorResponse);
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao realizar saque: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(summary = "Transferir entre carteiras", description = "Transfere dinheiro entre carteiras de usuários")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = TransferResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos, saldo insuficiente ou carteira inativa",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    })
    @PostMapping("/{userId}/transfer")
    public ResponseEntity<Map<String, Object>> transfer(
            @Parameter(description = "ID do usuário origem", required = true, example = "user123")
            @PathVariable String userId,
            @Parameter(description = "Dados da transferência", required = true,
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TransferRequest.class),
                    examples = @ExampleObject(value = "{\"targetUserId\": \"user456\", \"amount\": 25.00, \"currency\": \"BRL\"}")))
            @RequestBody Map<String, Object> request) {
        try {
            String targetUserId = (String) request.get("targetUserId");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = (String) request.getOrDefault("currency", "BRL");
            
            if (targetUserId == null || targetUserId.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "targetUserId é obrigatório");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (userId.equals(targetUserId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Não é possível transferir para a mesma carteira");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            return walletRepository.findByUserId(userId)
                    .flatMap(sourceWallet -> walletRepository.findByUserId(targetUserId)
                            .map(targetWallet -> {
                                try {
                                    Money money = new Money(amount, currency);
                                    
                                    if (!sourceWallet.hasSufficientFunds(money)) {
                                        Map<String, Object> errorResponse = new HashMap<>();
                                        errorResponse.put("error", "Saldo insuficiente");
                                        return ResponseEntity.badRequest().body(errorResponse);
                                    }
                                    
                                    // Realizar transferência
                                    sourceWallet.transferTo(targetWallet, money);
                                    
                                    // Salvar ambas as carteiras
                                    Wallet savedSourceWallet = walletRepository.save(sourceWallet);
                                    Wallet savedTargetWallet = walletRepository.save(targetWallet);
                                    
                                    // Registrar histórico de saldo para ambas as carteiras
                                    balanceHistoryRepository.save(savedSourceWallet.createBalanceHistory());
                                    balanceHistoryRepository.save(savedTargetWallet.createBalanceHistory());
                                    
                                    Map<String, Object> response = new HashMap<>();
                                    response.put("message", "Transferência realizada com sucesso");
                                    response.put("amount", amount);
                                    response.put("currency", currency);
                                    response.put("sourceUserId", userId);
                                    response.put("targetUserId", targetUserId);
                                    response.put("sourceNewBalance", savedSourceWallet.getBalance().getAmount());
                                    response.put("targetNewBalance", savedTargetWallet.getBalance().getAmount());
                                    response.put("transactionId", System.currentTimeMillis()); // ID único para rastreabilidade
                                    return ResponseEntity.ok(response);
                                } catch (Exception e) {
                                    Map<String, Object> errorResponse = new HashMap<>();
                                    errorResponse.put("error", e.getMessage());
                                    return ResponseEntity.badRequest().body(errorResponse);
                                }
                            }))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao realizar transferência: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(summary = "Consultar histórico de saldo", description = "Consulta o histórico de saldo de uma carteira")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Histórico encontrado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BalanceHistoryResponse.class))),
        @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    })
    @GetMapping("/{userId}/balance-history")
    public ResponseEntity<Map<String, Object>> getBalanceHistory(
            @Parameter(description = "ID do usuário", required = true, example = "user123")
            @PathVariable String userId) {
        try {
            return walletRepository.findByUserId(userId)
                    .map(wallet -> {
                        List<Map<String, Object>> history = balanceHistoryRepository.findByWalletId(wallet.getId())
                                .stream()
                                .map(record -> {
                                    Map<String, Object> historyEntry = new HashMap<>();
                                    historyEntry.put("balance", record.getBalance().getAmount());
                                    historyEntry.put("currency", record.getBalance().getCurrency());
                                    historyEntry.put("recordedAt", record.getRecordedAt());
                                    return historyEntry;
                                })
                                .toList();
                        
                        Map<String, Object> response = new HashMap<>();
                        response.put("userId", userId);
                        response.put("currentBalance", wallet.getBalance().getAmount());
                        response.put("currency", wallet.getBalance().getCurrency());
                        response.put("history", history);
                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao buscar histórico: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(summary = "Health check", description = "Verifica o status do serviço")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço funcionando",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = HealthResponse.class)))
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "wallet-service");
        response.put("version", "1.0.0");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    // Schemas para documentação OpenAPI
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
        public String createdAt;
        
        @Schema(description = "Data de atualização", example = "2024-01-01T10:00:00")
        public String updatedAt;
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
        
        @Schema(description = "Data do registro", example = "2024-01-01T10:00:00")
        public String recordedAt;
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