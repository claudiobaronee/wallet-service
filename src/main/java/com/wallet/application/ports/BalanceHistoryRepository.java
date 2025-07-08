package com.wallet.application.ports;

import com.wallet.domain.entities.BalanceHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para acesso a dados do histórico de saldos
 */
public interface BalanceHistoryRepository {
    
    List<BalanceHistory> findByWalletId(Long walletId);
    
    List<BalanceHistory> findByWalletIdAndRecordedAtBetween(Long walletId, LocalDateTime startDate, LocalDateTime endDate);
    
    Optional<BalanceHistory> findTopByWalletIdOrderByRecordedAtDesc(Long walletId);
    
    Optional<BalanceHistory> findByWalletIdAndRecordedAtBeforeOrderByRecordedAtDesc(Long walletId, LocalDateTime date);
} 