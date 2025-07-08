package com.wallet.infrastructure.controllers;

import com.wallet.application.dto.*;
import com.wallet.application.usecases.*;
import com.wallet.domain.valueobjects.Money;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Validated
@Tag(name = "Wallets", description = "Wallet operations")
public class WalletController {
    
    private final CreateWalletUseCase createWalletUseCase;
    private final GetWalletUseCase getWalletUseCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final TransferUseCase transferUseCase;
    private final GetBalanceHistoryUseCase getBalanceHistoryUseCase;
    private final GetTransactionsUseCase getTransactionsUseCase;
    
    @PostMapping
    @Operation(summary = "Create new wallet")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Wallet created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "409", description = "Wallet already exists")
    })
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        log.info("Creating wallet: {}", request);
        WalletResponse response = createWalletUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get wallet")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Wallet found"),
        @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<WalletResponse> getWallet(
            @Parameter(description = "User ID")
            @PathVariable @Pattern(regexp = "^[a-zA-Z0-9_-]+$") String userId) {
        log.info("Getting wallet for user: {}", userId);
        WalletResponse response = getWalletUseCase.execute(userId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{userId}/deposit")
    @Operation(summary = "Deposit funds")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deposit successful"),
        @ApiResponse(responseCode = "400", description = "Invalid data or currency mismatch"),
        @ApiResponse(responseCode = "404", description = "Wallet not found"),
        @ApiResponse(responseCode = "422", description = "Wallet not active")
    })
    public ResponseEntity<WalletResponse> depositMoney(
            @Parameter(description = "User ID")
            @PathVariable @Pattern(regexp = "^[a-zA-Z0-9_-]+$") String userId,
            @Valid @RequestBody TransactionRequest request) {
        log.info("Deposit request: user={}, amount={} {}", userId, request.getAmount(), request.getCurrency());
        WalletResponse response = depositUseCase.execute(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{userId}/withdraw")
    @Operation(summary = "Withdraw funds")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Withdrawal successful"),
        @ApiResponse(responseCode = "400", description = "Invalid data or currency mismatch"),
        @ApiResponse(responseCode = "404", description = "Wallet not found"),
        @ApiResponse(responseCode = "422", description = "Insufficient funds or wallet not active")
    })
    public ResponseEntity<WalletResponse> withdrawMoney(
            @Parameter(description = "User ID")
            @PathVariable @Pattern(regexp = "^[a-zA-Z0-9_-]+$") String userId,
            @Valid @RequestBody TransactionRequest request) {
        log.info("Withdrawal request: user={}, amount={} {}", userId, request.getAmount(), request.getCurrency());
        WalletResponse response = withdrawUseCase.execute(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{userId}/transfer")
    @Operation(summary = "Transfer funds")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transfer successful"),
        @ApiResponse(responseCode = "400", description = "Invalid data or currency mismatch"),
        @ApiResponse(responseCode = "404", description = "Source or target wallet not found"),
        @ApiResponse(responseCode = "422", description = "Insufficient funds or wallet not active")
    })
    public ResponseEntity<WalletResponse> transferMoney(
            @Parameter(description = "Source user ID")
            @PathVariable @Pattern(regexp = "^[a-zA-Z0-9_-]+$") String userId,
            @Valid @RequestBody TransactionRequest request) {
        log.info("Transfer request: from={}, to={}, amount={} {}", 
                userId, request.getTargetUserId(), request.getAmount(), request.getCurrency());
        WalletResponse response = transferUseCase.execute(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}/balance/history")
    @Operation(summary = "Get balance history")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Balance history retrieved"),
        @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<BalanceHistoryResponse> getBalanceHistory(
            @Parameter(description = "User ID")
            @PathVariable @Pattern(regexp = "^[a-zA-Z0-9_-]+$") String userId,
            @Parameter(description = "Date for balance query")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.info("Balance history request: user={}, date={}", userId, date);
        Money balance = getBalanceHistoryUseCase.execute(userId, date);
        BalanceHistoryResponse response = new BalanceHistoryResponse(userId, balance.getAmount(), balance.getCurrency(), date, LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}/transactions")
    @Operation(summary = "List transactions")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaction list"),
        @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<TransactionListResponse> getTransactions(
            @Parameter(description = "User ID")
            @PathVariable @Pattern(regexp = "^[a-zA-Z0-9_-]+$") String userId,
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Transaction type filter")
            @RequestParam(required = false) String type) {
        log.info("Transaction list request: user={}, page={}, size={}, type={}", userId, page, size, type);
        Pageable pageable = PageRequest.of(page, size);
        TransactionListResponse response = getTransactionsUseCase.execute(userId, pageable, type);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{userId}/cache")
    @Operation(summary = "Clear wallet cache")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cache cleared successfully"),
        @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    public ResponseEntity<Void> clearCache(
            @Parameter(description = "User ID")
            @PathVariable @Pattern(regexp = "^[a-zA-Z0-9_-]+$") String userId) {
        log.info("Clearing cache for user: {}", userId);
        getWalletUseCase.evictCache(userId);
        return ResponseEntity.ok().build();
    }
} 