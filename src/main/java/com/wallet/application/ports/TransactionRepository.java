package com.wallet.application.ports;

import com.wallet.domain.entities.Transaction;
import com.wallet.domain.enums.TransactionType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.wallet.domain.entities.TransactionEntity;

public interface TransactionRepository {
    
    Transaction save(Transaction transaction);
    
    List<Transaction> findByWalletId(Long walletId);
    
    List<Transaction> findByWalletIdAndTransactionType(Long walletId, TransactionType transactionType);
    
    List<Transaction> findByWalletIdAndTransactionDateBetween(Long walletId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findBySourceWalletIdOrTargetWalletId(Long sourceWalletId, Long targetWalletId);

    Page<Transaction> findByWalletUserId(String userId, Pageable pageable);
    Page<Transaction> findByWalletUserIdAndTransactionType(String userId, TransactionType type, Pageable pageable);
} 