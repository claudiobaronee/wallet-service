package com.wallet.application.ports;

import com.wallet.domain.entities.Transaction;
import com.wallet.domain.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Porta de saída para acesso a dados de transações
 */
public interface TransactionRepository {
    
    List<Transaction> findByWalletId(Long walletId);
    
    List<Transaction> findByWalletIdAndTransactionType(Long walletId, TransactionType transactionType);
    
    List<Transaction> findByWalletIdAndTransactionDateBetween(Long walletId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findBySourceWalletIdOrTargetWalletId(Long sourceWalletId, Long targetWalletId);
    
    List<Transaction> findByWalletUserId(String userId);
    
    List<Transaction> findByWalletUserIdAndTransactionType(String userId, TransactionType type);
} 