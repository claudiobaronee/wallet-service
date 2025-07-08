package com.wallet.adapters.infrastructure.repositories;

import com.wallet.application.ports.BalanceHistoryRepository;
import com.wallet.domain.entities.BalanceHistory;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceHistoryRepositoryImpl extends CrudRepository<BalanceHistory, Long>, BalanceHistoryRepository {
    
    @Override
    @Query("SELECT * FROM balance_history WHERE wallet_id = :walletId ORDER BY recorded_at DESC")
    List<BalanceHistory> findByWalletId(@Param("walletId") Long walletId);
    
    @Override
    @Query("SELECT * FROM balance_history WHERE wallet_id = :walletId AND recorded_at BETWEEN :startDate AND :endDate ORDER BY recorded_at DESC")
    List<BalanceHistory> findByWalletIdAndRecordedAtBetween(@Param("walletId") Long walletId,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Override
    @Query("SELECT * FROM balance_history WHERE wallet_id = :walletId ORDER BY recorded_at DESC LIMIT 1")
    Optional<BalanceHistory> findTopByWalletIdOrderByRecordedAtDesc(@Param("walletId") Long walletId);
    
    @Override
    @Query("SELECT * FROM balance_history WHERE wallet_id = :walletId AND recorded_at <= :date ORDER BY recorded_at DESC LIMIT 1")
    Optional<BalanceHistory> findByWalletIdAndRecordedAtBeforeOrderByRecordedAtDesc(@Param("walletId") Long walletId,
                                                                                   @Param("date") LocalDateTime date);
} 