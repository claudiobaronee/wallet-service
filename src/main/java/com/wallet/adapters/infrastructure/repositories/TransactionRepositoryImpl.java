package com.wallet.adapters.infrastructure.repositories;

import com.wallet.application.ports.TransactionRepository;
import com.wallet.domain.entities.Transaction;
import com.wallet.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepositoryImpl extends CrudRepository<Transaction, Long>, com.wallet.application.ports.TransactionRepository {
    
    @Override
    @Query("SELECT * FROM transactions WHERE wallet_id = :walletId")
    List<Transaction> findByWalletId(@Param("walletId") Long walletId);
    
    @Override
    @Query("SELECT * FROM transactions WHERE wallet_id = :walletId AND transaction_type = :transactionType")
    List<Transaction> findByWalletIdAndTransactionType(@Param("walletId") Long walletId, 
                                                      @Param("transactionType") TransactionType transactionType);
    
    @Override
    @Query("SELECT * FROM transactions WHERE wallet_id = :walletId AND transaction_date BETWEEN :startDate AND :endDate")
    List<Transaction> findByWalletIdAndTransactionDateBetween(@Param("walletId") Long walletId,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);
    
    @Override
    @Query("SELECT * FROM transactions WHERE source_wallet_id = :sourceWalletId OR target_wallet_id = :targetWalletId")
    List<Transaction> findBySourceWalletIdOrTargetWalletId(@Param("sourceWalletId") Long sourceWalletId,
                                                          @Param("targetWalletId") Long targetWalletId);
    
    @Override
    @Query("SELECT t.* FROM transactions t JOIN wallets w ON t.wallet_id = w.id WHERE w.user_id = :userId")
    List<Transaction> findByWalletUserId(@Param("userId") String userId);
    
    @Override
    @Query("SELECT t.* FROM transactions t JOIN wallets w ON t.wallet_id = w.id WHERE w.user_id = :userId AND t.transaction_type = :type")
    List<Transaction> findByWalletUserIdAndTransactionType(@Param("userId") String userId, 
                                                          @Param("type") TransactionType type);
} 