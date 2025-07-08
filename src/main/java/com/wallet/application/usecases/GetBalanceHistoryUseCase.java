package com.wallet.application.usecases;

import com.wallet.application.ports.BalanceHistoryRepository;
import com.wallet.application.ports.WalletRepository;
import com.wallet.domain.entities.BalanceHistory;
import com.wallet.domain.entities.Wallet;
import com.wallet.domain.valueobjects.Money;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GetBalanceHistoryUseCase {
    
    private final WalletRepository walletRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;
    
    public GetBalanceHistoryUseCase(WalletRepository walletRepository, BalanceHistoryRepository balanceHistoryRepository) {
        this.walletRepository = walletRepository;
        this.balanceHistoryRepository = balanceHistoryRepository;
    }
    
    public Money execute(String userId, LocalDateTime date) {
        // Buscar a carteira
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user: " + userId));
        
        // Se a data for no futuro, retornar o saldo atual
        if (date.isAfter(LocalDateTime.now())) {
            return wallet.getBalance();
        }
        
        // Buscar o saldo histórico mais próximo da data especificada
        Optional<BalanceHistory> balanceHistory = balanceHistoryRepository
                .findByWalletIdAndRecordedAtBeforeOrderByRecordedAtDesc(wallet.getId(), date);
        
        // Se não encontrar histórico, retornar o saldo atual
        return balanceHistory.map(BalanceHistory::getBalance).orElse(wallet.getBalance());
    }
} 