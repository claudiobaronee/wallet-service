package com.wallet.adapters.infrastructure.repositories;

import com.wallet.application.ports.TransactionRepository;
import com.wallet.domain.entities.Transaction;
import com.wallet.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaTransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepository {
    
    @Override
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId")
    List<Transaction> findByWalletId(@Param("walletId") Long walletId);
    
    @Override
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId AND t.transactionType = :transactionType")
    List<Transaction> findByWalletIdAndTransactionType(@Param("walletId") Long walletId, 
                                                      @Param("transactionType") TransactionType transactionType);
    
    @Override
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByWalletIdAndTransactionDateBetween(@Param("walletId") Long walletId,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);
    
    @Override
    @Query("SELECT t FROM Transaction t WHERE t.sourceWallet.id = :sourceWalletId OR t.targetWallet.id = :targetWalletId")
    List<Transaction> findBySourceWalletIdOrTargetWalletId(@Param("sourceWalletId") Long sourceWalletId,
                                                          @Param("targetWalletId") Long targetWalletId);
    
    @Override
    @Query("SELECT t FROM Transaction t WHERE t.wallet.userId = :userId")
    Page<Transaction> findByWalletUserId(@Param("userId") String userId, Pageable pageable);
    
    @Override
    @Query("SELECT t FROM Transaction t WHERE t.wallet.userId = :userId AND t.transactionType = :type")
    Page<Transaction> findByWalletUserIdAndTransactionType(@Param("userId") String userId, 
                                                          @Param("type") TransactionType type, 
                                                          Pageable pageable);
} 