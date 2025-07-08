package com.wallet.application.ports;

import com.wallet.domain.entities.Wallet;
import java.util.List;
import java.util.Optional;

public interface WalletRepository {
    
    Wallet save(Wallet wallet);
    
    Optional<Wallet> findById(Long id);
    
    Optional<Wallet> findByUserId(String userId);
    
    List<Wallet> findAll();
    
    void delete(Wallet wallet);
    
    boolean existsByUserId(String userId);
    
    List<Wallet> findByStatus(String status);
} 