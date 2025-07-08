package com.wallet.adapters.infrastructure.repositories;

import com.wallet.application.ports.WalletRepository;
import com.wallet.domain.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaWalletRepository extends JpaRepository<Wallet, Long>, WalletRepository {
    
    @Override
    @Query("SELECT w FROM Wallet w WHERE w.userId = :userId")
    Optional<Wallet> findByUserId(@Param("userId") String userId);
    
    @Override
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wallet w WHERE w.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);
    
    @Override
    @Query("SELECT w FROM Wallet w WHERE w.status = :status")
    List<Wallet> findByStatus(@Param("status") String status);
} 