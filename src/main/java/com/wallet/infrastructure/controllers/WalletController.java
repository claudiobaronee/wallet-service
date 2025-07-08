package com.wallet.infrastructure.controllers;

import com.wallet.domain.entities.Wallet;
import com.wallet.domain.entities.BalanceHistory;
import com.wallet.domain.valueobjects.Money;
import com.wallet.adapters.infrastructure.repositories.WalletRepositoryImpl;
import com.wallet.adapters.infrastructure.repositories.BalanceHistoryRepositoryImpl;
import com.wallet.infrastructure.dto.WalletDTOs;
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
import java.time.LocalDateTime;
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
                schema = @Schema(implementation = WalletDTOs.CreateWalletResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = WalletDTOs.ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createWallet(
            @RequestBody @Schema(implementation = WalletDTOs.CreateWalletRequest.class) Map<String, String> request) {
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
            BalanceHistory initialHistory = BalanceHistory.create(savedWallet.getId(), savedWallet.getBalance(), "Criação da carteira");
            balanceHistoryRepository.save(initialHistory);
            
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
                schema = @Schema(implementation = WalletDTOs.WalletResponse.class))),
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
                schema = @Schema(implementation = WalletDTOs.DepositResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou carteira inativa",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = WalletDTOs.ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    })
    @PostMapping("/{userId}/deposit")
    public ResponseEntity<Map<String, Object>> deposit(
            @Parameter(description = "ID do usuário", required = true, example = "user123")
            @PathVariable String userId,
            @Parameter(description = "Dados do depósito", required = true,
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = WalletDTOs.DepositRequest.class),
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
                            BalanceHistory depositHistory = BalanceHistory.create(savedWallet.getId(), savedWallet.getBalance(), 
                                String.format("Depósito de %s %s", amount, currency));
                            balanceHistoryRepository.save(depositHistory);
                            
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
                schema = @Schema(implementation = WalletDTOs.WithdrawResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos, saldo insuficiente ou carteira inativa",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = WalletDTOs.ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    })
    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(
            @Parameter(description = "ID do usuário", required = true, example = "user123")
            @PathVariable String userId,
            @Parameter(description = "Dados do saque", required = true,
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = WalletDTOs.WithdrawRequest.class),
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
                            BalanceHistory withdrawHistory = BalanceHistory.create(savedWallet.getId(), savedWallet.getBalance(), 
                                String.format("Saque de %s %s", amount, currency));
                            balanceHistoryRepository.save(withdrawHistory);
                            
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
                schema = @Schema(implementation = WalletDTOs.TransferResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos, saldo insuficiente ou carteira inativa",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = WalletDTOs.ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    })
    @PostMapping("/{userId}/transfer")
    public ResponseEntity<Map<String, Object>> transfer(
            @Parameter(description = "ID do usuário origem", required = true, example = "user123")
            @PathVariable String userId,
            @Parameter(description = "Dados da transferência", required = true,
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = WalletDTOs.TransferRequest.class),
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
                                    BalanceHistory sourceHistory = BalanceHistory.create(savedSourceWallet.getId(), savedSourceWallet.getBalance(), 
                                        String.format("Transferência enviada de %s %s para %s", amount, currency, targetUserId));
                                    balanceHistoryRepository.save(sourceHistory);
                                    
                                    BalanceHistory targetHistory = BalanceHistory.create(savedTargetWallet.getId(), savedTargetWallet.getBalance(), 
                                        String.format("Transferência recebida de %s %s de %s", amount, currency, userId));
                                    balanceHistoryRepository.save(targetHistory);
                                    
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
                schema = @Schema(implementation = WalletDTOs.BalanceHistoryResponse.class))),
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
                                    historyEntry.put("description", record.getDescription());
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
                schema = @Schema(implementation = WalletDTOs.HealthResponse.class)))
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
} 