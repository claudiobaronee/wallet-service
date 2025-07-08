package com.wallet.application.usecases;

import com.wallet.application.dto.TransactionRequest;
import com.wallet.application.dto.WalletResponse;
import com.wallet.application.ports.TransactionRepository;
import com.wallet.application.ports.WalletRepository;
import com.wallet.domain.entities.Transaction;
import com.wallet.domain.entities.Wallet;
import com.wallet.domain.valueobjects.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferUseCase {
    
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    
    public TransferUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }
    
    @Transactional
    public WalletResponse execute(String sourceUserId, TransactionRequest request) {
        // Validar se o targetUserId foi fornecido
        if (request.getTargetUserId() == null || request.getTargetUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("Target user ID is required for transfers");
        }
        
        // Verificar se não está tentando transferir para si mesmo
        if (sourceUserId.equals(request.getTargetUserId())) {
            throw new IllegalArgumentException("Cannot transfer to the same wallet");
        }
        
        // Buscar a carteira de origem
        Wallet sourceWallet = walletRepository.findByUserId(sourceUserId)
                .orElseThrow(() -> new IllegalArgumentException("Source wallet not found for user: " + sourceUserId));
        
        // Buscar a carteira de destino
        Wallet targetWallet = walletRepository.findByUserId(request.getTargetUserId())
                .orElseThrow(() -> new IllegalArgumentException("Target wallet not found for user: " + request.getTargetUserId()));
        
        // Criar o valor monetário
        Money amount = Money.of(request.getAmount(), request.getCurrency());
        
        // Verificar se as moedas são compatíveis
        if (!sourceWallet.getBalance().getCurrency().equals(amount.getCurrency()) ||
            !targetWallet.getBalance().getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch between wallets");
        }
        
        // Verificar se há saldo suficiente na carteira de origem
        if (!sourceWallet.hasSufficientFunds(amount)) {
            throw new IllegalArgumentException("Insufficient funds in source wallet. Available: " + 
                    sourceWallet.getBalance() + ", Requested: " + amount);
        }
        
        // Realizar a transferência
        sourceWallet.transferTo(targetWallet, amount);
        
        // Criar e salvar a transação de saída
        Transaction sourceTransaction = Transaction.createTransfer(sourceWallet, targetWallet, amount, 
                "Transfer to " + request.getTargetUserId() + ": " + request.getDescription());
        transactionRepository.save(sourceTransaction);
        
        // Criar e salvar a transação de entrada
        Transaction targetTransaction = Transaction.createDeposit(targetWallet, amount, 
                "Transfer from " + sourceUserId + ": " + request.getDescription());
        transactionRepository.save(targetTransaction);
        
        // Salvar ambas as carteiras atualizadas
        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);
        
        return WalletResponse.from(sourceWallet);
    }
} 