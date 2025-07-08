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
public class WithdrawUseCase {
    
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    
    public WithdrawUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }
    
    @Transactional
    public WalletResponse execute(String userId, TransactionRequest request) {
        // Buscar a carteira
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user: " + userId));
        
        // Criar o valor monetário
        Money amount = Money.of(request.getAmount(), request.getCurrency());
        
        // Verificar se a moeda é compatível
        if (!wallet.getBalance().getCurrency().equals(amount.getCurrency())) {
            throw new IllegalArgumentException("Currency mismatch. Wallet currency: " + 
                    wallet.getBalance().getCurrency() + ", Transaction currency: " + amount.getCurrency());
        }
        
        // Verificar se há saldo suficiente
        if (!wallet.hasSufficientFunds(amount)) {
            throw new IllegalArgumentException("Insufficient funds. Available: " + 
                    wallet.getBalance() + ", Requested: " + amount);
        }
        
        // Realizar o saque
        wallet.withdraw(amount);
        
        // Criar e salvar a transação
        Transaction transaction = Transaction.createWithdraw(wallet, amount, request.getDescription());
        transactionRepository.save(transaction);
        
        // Salvar a carteira atualizada
        Wallet updatedWallet = walletRepository.save(wallet);
        
        return WalletResponse.from(updatedWallet);
    }
} 