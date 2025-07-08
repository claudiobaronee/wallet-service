package com.wallet.adapters.infrastructure.repositories;

import com.wallet.application.ports.BalanceHistoryRepository;
import com.wallet.domain.entities.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaBalanceHistoryRepository extends JpaRepository<BalanceHistory, Long>, BalanceHistoryRepository {
    
    @Override
    @Query("SELECT bh FROM BalanceHistory bh WHERE bh.wallet.id = :walletId ORDER BY bh.recordedAt DESC")
    List<BalanceHistory> findByWalletId(@Param("walletId") Long walletId);
    
    @Override
    @Query("SELECT bh FROM BalanceHistory bh WHERE bh.wallet.id = :walletId AND bh.recordedAt BETWEEN :startDate AND :endDate ORDER BY bh.recordedAt DESC")
    List<BalanceHistory> findByWalletIdAndRecordedAtBetween(@Param("walletId") Long walletId,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Override
    @Query("SELECT bh FROM BalanceHistory bh WHERE bh.wallet.id = :walletId ORDER BY bh.recordedAt DESC")
    Optional<BalanceHistory> findTopByWalletIdOrderByRecordedAtDesc(@Param("walletId") Long walletId);
    
    @Override
    @Query("SELECT bh FROM BalanceHistory bh WHERE bh.wallet.id = :walletId AND bh.recordedAt <= :date ORDER BY bh.recordedAt DESC")
    Optional<BalanceHistory> findByWalletIdAndRecordedAtBeforeOrderByRecordedAtDesc(@Param("walletId") Long walletId,
                                                                                   @Param("date") LocalDateTime date);
} 