package com.wallet.infrastructure.controllers;

import com.wallet.domain.aggregates.WalletAggregate;
import com.wallet.domain.entities.BalanceHistory;
import com.wallet.domain.entities.Wallet;
import com.wallet.domain.valueobjects.Money;
import com.wallet.infrastructure.dto.WalletDTOs;
import com.wallet.infrastructure.repositories.BalanceHistoryRepositoryImpl;
import com.wallet.infrastructure.repositories.WalletRepositoryImpl;
import com.wallet.infrastructure.services.AuditService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
@Slf4j
public class WalletController {

    @Autowired
    private WalletRepositoryImpl walletRepository;

    @Autowired
    private BalanceHistoryRepositoryImpl balanceHistoryRepository;

    @Autowired
    private AuditService auditService;

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WalletDTOs.CreateWalletResponse> createWallet(
            @Valid @RequestBody WalletDTOs.CreateWalletRequest request) {
        
        String correlationId = auditService.generateCorrelationId();
        log.info("Creating wallet for user: {}", request.getUserId());
        auditService.logOperation("CREATE_WALLET", request.getUserId(), 
                "Creating wallet with currency: " + request.getCurrency(), correlationId);
        
        if (walletRepository.findByUserId(request.getUserId()).isPresent()) {
            auditService.logError("CREATE_WALLET", request.getUserId(), 
                    "Wallet already exists", correlationId);
            throw new RuntimeException("Carteira já existe para este usuário");
        }

        WalletAggregate aggregate = new WalletAggregate();
        Wallet wallet = aggregate.createWallet(request.getUserId(), request.getCurrency());
        Wallet savedWallet = walletRepository.save(wallet);

        WalletDTOs.CreateWalletResponse response = new WalletDTOs.CreateWalletResponse(
            savedWallet.getId(),
            savedWallet.getUserId(),
            savedWallet.getBalance().getAmount(),
            savedWallet.getBalance().getCurrency(),
            savedWallet.getStatus().name(),
            "Carteira criada com sucesso",
            savedWallet.getCreatedAt()
        );
        
        auditService.logOperation("WALLET_CREATED", request.getUserId(), 
                "Wallet created with ID: " + savedWallet.getId(), correlationId);
        log.info("Wallet created successfully: {}", savedWallet.getId());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<WalletDTOs.WalletResponse> getWallet(@PathVariable String userId) {
        
        String correlationId = auditService.generateCorrelationId();
        log.info("Consulting wallet for user: {}", userId);
        auditService.logOperation("CONSULT_WALLET", userId, "Consulting wallet balance", correlationId);
        
        return walletRepository.findByUserId(userId)
                .map(wallet -> {
                    WalletDTOs.WalletResponse response = new WalletDTOs.WalletResponse(
                        wallet.getId(),
                        wallet.getUserId(),
                        wallet.getBalance().getAmount(),
                        wallet.getBalance().getCurrency(),
                        wallet.getStatus().name(),
                        wallet.getCreatedAt(),
                        wallet.getUpdatedAt()
                    );
                    auditService.logOperation("WALLET_CONSULTED", userId, 
                            "Wallet consulted successfully", correlationId);
                    log.info("Wallet consulted successfully: {}", wallet.getId());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/deposit")
    @Transactional
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<WalletDTOs.DepositResponse> deposit(
            @PathVariable String userId,
            @Valid @RequestBody WalletDTOs.DepositRequest request) {
        
        String correlationId = auditService.generateCorrelationId();
        log.info("Processing deposit for user: {}, amount: {} {}", userId, request.getAmount(), request.getCurrency());
        
        return walletRepository.findByUserId(userId)
                .map(wallet -> {
                    try {
                        Money oldBalance = wallet.getBalance();
                        Money money = new Money(request.getAmount(), request.getCurrency());
                        
                        auditService.logTransaction("DEPOSIT", userId, money, 
                                request.getDescription(), correlationId);
                        
                        wallet.deposit(money);
                        Wallet savedWallet = walletRepository.save(wallet);
                        
                        String description = request.getDescription() != null ? 
                            request.getDescription() : 
                            String.format("Depósito de %s %s", request.getAmount(), request.getCurrency());
                        
                        BalanceHistory depositHistory = BalanceHistory.create(savedWallet.getId(), savedWallet.getBalance(), description);
                        balanceHistoryRepository.save(depositHistory);
                        
                        WalletDTOs.DepositResponse response = new WalletDTOs.DepositResponse(
                            "Depósito realizado com sucesso",
                            request.getAmount(),
                            request.getCurrency(),
                            savedWallet.getBalance().getAmount(),
                            correlationId
                        );
                        
                        auditService.logBalanceChange(userId, oldBalance, savedWallet.getBalance(), 
                                "Deposit: " + description, correlationId);
                        
                        log.info("Deposit completed successfully: {}, new balance: {}", 
                                response.getTransactionId(), savedWallet.getBalance().getAmount());
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        auditService.logError("DEPOSIT", userId, e.getMessage(), correlationId);
                        log.error("Error processing deposit for user {}: {}", userId, e.getMessage());
                        throw new RuntimeException("Erro ao realizar depósito: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/withdraw")
    @Transactional
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<WalletDTOs.WithdrawResponse> withdraw(
            @PathVariable String userId,
            @Valid @RequestBody WalletDTOs.WithdrawRequest request) {
        
        String correlationId = auditService.generateCorrelationId();
        log.info("Processing withdrawal for user: {}, amount: {} {}", userId, request.getAmount(), request.getCurrency());
        
        return walletRepository.findByUserId(userId)
                .map(wallet -> {
                    try {
                        Money oldBalance = wallet.getBalance();
                        Money money = new Money(request.getAmount(), request.getCurrency());
                        
                        if (!wallet.hasSufficientFunds(money)) {
                            auditService.logSecurityEvent("INSUFFICIENT_FUNDS", userId, 
                                    "Attempted withdrawal: " + request.getAmount() + " " + request.getCurrency(), correlationId);
                            log.warn("Insufficient funds for withdrawal: user {}, requested: {}, available: {}", 
                                    userId, request.getAmount(), wallet.getBalance().getAmount());
                            throw new RuntimeException("Saldo insuficiente");
                        }
                        
                        auditService.logTransaction("WITHDRAW", userId, money, 
                                request.getDescription(), correlationId);
                        
                        wallet.withdraw(money);
                        Wallet savedWallet = walletRepository.save(wallet);
                        
                        String description = request.getDescription() != null ? 
                            request.getDescription() : 
                            String.format("Saque de %s %s", request.getAmount(), request.getCurrency());
                        
                        BalanceHistory withdrawHistory = BalanceHistory.create(savedWallet.getId(), savedWallet.getBalance(), description);
                        balanceHistoryRepository.save(withdrawHistory);
                        
                        WalletDTOs.WithdrawResponse response = new WalletDTOs.WithdrawResponse(
                            "Saque realizado com sucesso",
                            request.getAmount(),
                            request.getCurrency(),
                            savedWallet.getBalance().getAmount(),
                            correlationId
                        );
                        
                        auditService.logBalanceChange(userId, oldBalance, savedWallet.getBalance(), 
                                "Withdrawal: " + description, correlationId);
                        
                        log.info("Withdrawal completed successfully: {}, new balance: {}", 
                                response.getTransactionId(), savedWallet.getBalance().getAmount());
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        auditService.logError("WITHDRAW", userId, e.getMessage(), correlationId);
                        log.error("Error processing withdrawal for user {}: {}", userId, e.getMessage());
                        throw new RuntimeException("Erro ao realizar saque: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/transfer")
    @Transactional
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<WalletDTOs.TransferResponse> transfer(
            @PathVariable String userId,
            @Valid @RequestBody WalletDTOs.TransferRequest request) {
        
        String correlationId = auditService.generateCorrelationId();
        log.info("Processing transfer from user: {} to user: {}, amount: {} {}", 
                userId, request.getTargetUserId(), request.getAmount(), request.getCurrency());
        
        if (userId.equals(request.getTargetUserId())) {
            auditService.logSecurityEvent("SELF_TRANSFER_ATTEMPT", userId, 
                    "Attempted self-transfer", correlationId);
            log.warn("Attempted self-transfer: user {}", userId);
            throw new RuntimeException("Não é possível transferir para a mesma carteira");
        }
        
        return walletRepository.findByUserId(userId)
                .flatMap(sourceWallet -> walletRepository.findByUserId(request.getTargetUserId())
                        .map(targetWallet -> {
                            try {
                                Money oldSourceBalance = sourceWallet.getBalance();
                                Money oldTargetBalance = targetWallet.getBalance();
                                Money money = new Money(request.getAmount(), request.getCurrency());
                                
                                if (!sourceWallet.hasSufficientFunds(money)) {
                                    auditService.logSecurityEvent("INSUFFICIENT_FUNDS_TRANSFER", userId, 
                                            "Attempted transfer: " + request.getAmount() + " " + request.getCurrency(), correlationId);
                                    log.warn("Insufficient funds for transfer: user {}, requested: {}, available: {}", 
                                            userId, request.getAmount(), sourceWallet.getBalance().getAmount());
                                    throw new RuntimeException("Saldo insuficiente");
                                }
                                
                                auditService.logTransaction("TRANSFER", userId, money, 
                                        "Transfer to " + request.getTargetUserId() + ": " + request.getDescription(), correlationId);
                                
                                sourceWallet.transferTo(targetWallet, money);
                                
                                Wallet savedSourceWallet = walletRepository.save(sourceWallet);
                                Wallet savedTargetWallet = walletRepository.save(targetWallet);
                                String sourceDescription = request.getDescription() != null ? 
                                    request.getDescription() : 
                                    String.format("Transferência enviada de %s %s para %s", request.getAmount(), request.getCurrency(), request.getTargetUserId());
                                
                                BalanceHistory sourceHistory = BalanceHistory.create(savedSourceWallet.getId(), savedSourceWallet.getBalance(), sourceDescription);
                                balanceHistoryRepository.save(sourceHistory);
                                
                                String targetDescription = String.format("Transferência recebida de %s %s de %s", request.getAmount(), request.getCurrency(), userId);
                                BalanceHistory targetHistory = BalanceHistory.create(savedTargetWallet.getId(), savedTargetWallet.getBalance(), targetDescription);
                                balanceHistoryRepository.save(targetHistory);
                                
                                WalletDTOs.TransferResponse response = new WalletDTOs.TransferResponse(
                                    "Transferência realizada com sucesso",
                                    request.getAmount(),
                                    request.getCurrency(),
                                    userId,
                                    request.getTargetUserId(),
                                    savedSourceWallet.getBalance().getAmount(),
                                    savedTargetWallet.getBalance().getAmount(),
                                    correlationId
                                );
                                
                                auditService.logBalanceChange(userId, oldSourceBalance, savedSourceWallet.getBalance(), 
                                        "Transfer sent: " + sourceDescription, correlationId);
                                auditService.logBalanceChange(request.getTargetUserId(), oldTargetBalance, savedTargetWallet.getBalance(), 
                                        "Transfer received: " + targetDescription, correlationId);
                                
                                log.info("Transfer completed successfully: {}, source balance: {}, target balance: {}", 
                                        response.getTransactionId(), savedSourceWallet.getBalance().getAmount(), savedTargetWallet.getBalance().getAmount());
                                return ResponseEntity.ok(response);
                            } catch (Exception e) {
                                auditService.logError("TRANSFER", userId, e.getMessage(), correlationId);
                                log.error("Error processing transfer from user {} to user {}: {}", userId, request.getTargetUserId(), e.getMessage());
                                throw new RuntimeException("Erro ao realizar transferência: " + e.getMessage());
                            }
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/balance-history")
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<WalletDTOs.BalanceHistoryResponse> getBalanceHistory(@PathVariable String userId) {
        
        String correlationId = auditService.generateCorrelationId();
        log.info("Consulting balance history for user: {}", userId);
        auditService.logOperation("CONSULT_BALANCE_HISTORY", userId, "Consulting balance history", correlationId);
        
        return walletRepository.findByUserId(userId)
                .map(wallet -> {
                    List<WalletDTOs.BalanceHistoryEntry> history = balanceHistoryRepository.findByWalletId(wallet.getId())
                            .stream()
                            .map(record -> new WalletDTOs.BalanceHistoryEntry(
                                record.getBalance().getAmount(),
                                record.getBalance().getCurrency(),
                                record.getDescription(),
                                record.getRecordedAt()
                            ))
                            .toList();
                    
                    WalletDTOs.BalanceHistoryResponse response = new WalletDTOs.BalanceHistoryResponse(
                        userId,
                        wallet.getBalance().getAmount(),
                        wallet.getBalance().getCurrency(),
                        history
                    );
                    
                    auditService.logOperation("BALANCE_HISTORY_CONSULTED", userId, 
                            "Balance history consulted: " + history.size() + " records", correlationId);
                    log.info("Balance history consulted successfully: {} records found", history.size());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/health")
    public ResponseEntity<WalletDTOs.HealthResponse> health() {
        WalletDTOs.HealthResponse response = new WalletDTOs.HealthResponse(
            "UP",
            "wallet-service",
            "1.0.0",
            System.currentTimeMillis()
        );
        return ResponseEntity.ok(response);
    }
} 