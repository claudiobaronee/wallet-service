package com.wallet.application.usecases;

import com.wallet.application.dto.TransactionListResponse;
import com.wallet.application.dto.TransactionResponse;
import com.wallet.domain.entities.Transaction;
import com.wallet.domain.enums.TransactionType;
import com.wallet.application.ports.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetTransactionsUseCase {
    
    private final TransactionRepository transactionRepository;
    
    public TransactionListResponse execute(String userId, Pageable pageable, String type) {
        log.info("Getting transactions for user: {}, type: {}", userId, type);
        
        Page<Transaction> transactions;
        if (type != null && !type.isEmpty()) {
            TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
            transactions = transactionRepository.findByWalletUserIdAndTransactionType(userId, transactionType, pageable);
        } else {
            transactions = transactionRepository.findByWalletUserId(userId, pageable);
        }
        
        var transactionResponses = transactions.getContent().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        
        return new TransactionListResponse(
            transactionResponses,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            transactions.getTotalElements(),
            transactions.getTotalPages()
        );
    }
    
    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId().toString(),
            transaction.getWallet().getUserId(),
            transaction.getTransactionType().name(),
            transaction.getAmount().getAmount(),
            transaction.getAmount().getCurrency(),
            transaction.getDescription(),
            transaction.getTransactionDate(),
            null // relatedTransactionId não implementado nesta versão
        );
    }
} 