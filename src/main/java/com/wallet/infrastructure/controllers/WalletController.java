package com.wallet.infrastructure.controllers;

import com.wallet.domain.entities.Wallet;
import com.wallet.domain.entities.BalanceHistory;
import com.wallet.domain.valueobjects.Money;
import com.wallet.adapters.infrastructure.repositories.WalletRepositoryImpl;
import com.wallet.adapters.infrastructure.repositories.BalanceHistoryRepositoryImpl;
import com.wallet.infrastructure.dto.WalletDTOs;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletRepositoryImpl walletRepository;
    
    @Autowired
    private BalanceHistoryRepositoryImpl balanceHistoryRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<WalletDTOs.CreateWalletResponse> createWallet(
            @Valid @RequestBody WalletDTOs.CreateWalletRequest request) {
        
        if (walletRepository.existsByUserId(request.getUserId())) {
            return ResponseEntity.status(409).build();
        }
        
        Wallet wallet = Wallet.create(request.getUserId(), request.getCurrency());
        Wallet savedWallet = walletRepository.save(wallet);
        
        // Registrar histórico de saldo inicial
        BalanceHistory initialHistory = BalanceHistory.create(savedWallet.getId(), savedWallet.getBalance(), "Criação da carteira");
        balanceHistoryRepository.save(initialHistory);
        
        WalletDTOs.CreateWalletResponse response = new WalletDTOs.CreateWalletResponse(
            savedWallet.getId(),
            savedWallet.getUserId(),
            savedWallet.getBalance().getAmount(),
            savedWallet.getBalance().getCurrency(),
            savedWallet.getStatus().name(),
            "Carteira criada com sucesso",
            savedWallet.getCreatedAt()
        );
        
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletDTOs.WalletResponse> getWallet(@PathVariable String userId) {
        
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
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/deposit")
    @Transactional
    public ResponseEntity<WalletDTOs.DepositResponse> deposit(
            @PathVariable String userId,
            @Valid @RequestBody WalletDTOs.DepositRequest request) {
        
        return walletRepository.findByUserId(userId)
                .map(wallet -> {
                    try {
                        Money money = new Money(request.getAmount(), request.getCurrency());
                        wallet.deposit(money);
                        Wallet savedWallet = walletRepository.save(wallet);
                        
                        // Registrar histórico de saldo
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
                            UUID.randomUUID().toString()
                        );
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao realizar depósito: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/withdraw")
    @Transactional
    public ResponseEntity<WalletDTOs.WithdrawResponse> withdraw(
            @PathVariable String userId,
            @Valid @RequestBody WalletDTOs.WithdrawRequest request) {
        
        return walletRepository.findByUserId(userId)
                .map(wallet -> {
                    try {
                        Money money = new Money(request.getAmount(), request.getCurrency());
                        if (!wallet.hasSufficientFunds(money)) {
                            throw new RuntimeException("Saldo insuficiente");
                        }
                        
                        wallet.withdraw(money);
                        Wallet savedWallet = walletRepository.save(wallet);
                        
                        // Registrar histórico de saldo
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
                            UUID.randomUUID().toString()
                        );
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao realizar saque: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/transfer")
    @Transactional
    public ResponseEntity<WalletDTOs.TransferResponse> transfer(
            @PathVariable String userId,
            @Valid @RequestBody WalletDTOs.TransferRequest request) {
        
        if (userId.equals(request.getTargetUserId())) {
            throw new RuntimeException("Não é possível transferir para a mesma carteira");
        }
        
        return walletRepository.findByUserId(userId)
                .flatMap(sourceWallet -> walletRepository.findByUserId(request.getTargetUserId())
                        .map(targetWallet -> {
                            try {
                                Money money = new Money(request.getAmount(), request.getCurrency());
                                
                                if (!sourceWallet.hasSufficientFunds(money)) {
                                    throw new RuntimeException("Saldo insuficiente");
                                }
                                
                                // Realizar transferência
                                sourceWallet.transferTo(targetWallet, money);
                                
                                // Salvar ambas as carteiras
                                Wallet savedSourceWallet = walletRepository.save(sourceWallet);
                                Wallet savedTargetWallet = walletRepository.save(targetWallet);
                                
                                // Registrar histórico de saldo para ambas as carteiras
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
                                    UUID.randomUUID().toString()
                                );
                                return ResponseEntity.ok(response);
                            } catch (Exception e) {
                                throw new RuntimeException("Erro ao realizar transferência: " + e.getMessage());
                            }
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/balance-history")
    public ResponseEntity<WalletDTOs.BalanceHistoryResponse> getBalanceHistory(@PathVariable String userId) {
        
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