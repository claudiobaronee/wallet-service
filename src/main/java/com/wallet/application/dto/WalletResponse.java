package com.wallet.application.dto;

import com.wallet.domain.entities.Wallet;
import com.wallet.domain.valueobjects.Money;
import java.time.LocalDateTime;

public class WalletResponse {
    
    private Long id;
    private String userId;
    private Money balance;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public WalletResponse() {
        // Para serialização JSON
    }
    
    public WalletResponse(Wallet wallet) {
        this.id = wallet.getId();
        this.userId = wallet.getUserId();
        this.balance = wallet.getBalance();
        this.status = wallet.getStatus().name();
        this.createdAt = wallet.getCreatedAt();
        this.updatedAt = wallet.getUpdatedAt();
    }
    
    public static WalletResponse from(Wallet wallet) {
        return new WalletResponse(wallet);
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Money getBalance() {
        return balance;
    }
    
    public void setBalance(Money balance) {
        this.balance = balance;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 