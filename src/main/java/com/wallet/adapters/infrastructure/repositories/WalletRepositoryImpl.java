package com.wallet.adapters.infrastructure.repositories;

import com.wallet.domain.entities.Wallet;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepositoryImpl extends CrudRepository<Wallet, Long> {
    
    @Query("SELECT * FROM wallets WHERE user_id = :userId")
    Optional<Wallet> findByUserId(@Param("userId") String userId);
    
    @Query("SELECT COUNT(*) > 0 FROM wallets WHERE user_id = :userId")
    boolean existsByUserId(@Param("userId") String userId);
    
    @Query("SELECT * FROM wallets WHERE user_id = :userId")
    Optional<Wallet> findWalletByUserId(@Param("userId") String userId);
} 