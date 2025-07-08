package com.wallet.application.usecases;

import com.wallet.application.dto.WalletResponse;
import com.wallet.application.ports.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetWalletUseCase {
    
    private final WalletRepository walletRepository;
    
    @Cacheable(value = "wallets", key = "#userId")
    public WalletResponse execute(String userId) {
        log.info("Buscando carteira para usuário: {} (cache miss)", userId);
        return walletRepository.findByUserId(userId)
                .map(WalletResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user: " + userId));
    }
    
    @CacheEvict(value = "wallets", key = "#userId")
    public void evictCache(String userId) {
        log.info("Evicting cache for user: {}", userId);
    }
} 