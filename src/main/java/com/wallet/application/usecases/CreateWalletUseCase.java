package com.wallet.application.usecases;

import com.wallet.application.dto.CreateWalletRequest;
import com.wallet.application.dto.WalletResponse;
import com.wallet.application.events.DomainEventPublisher;
import com.wallet.application.ports.WalletRepository;
import com.wallet.domain.aggregates.WalletAggregate;
import com.wallet.domain.entities.Wallet;
import com.wallet.domain.valueobjects.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateWalletUseCase {
    
    private final WalletRepository walletRepository;
    private final DomainEventPublisher eventPublisher;
    
    @Transactional
    @CacheEvict(value = "wallets", key = "#request.userId")
    public WalletResponse execute(CreateWalletRequest request) {
        log.info("Criando carteira para usuário: {} com moeda: {}", request.getUserId(), request.getCurrency());
        
        // Verificar se já existe uma carteira para o usuário
        if (walletRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("Wallet already exists for user: " + request.getUserId());
        }
        
        // Criar agregado de carteira
        WalletAggregate walletAggregate = WalletAggregate.create(request.getUserId(), request.getCurrency());
        
        // Salvar a carteira
        Wallet savedWallet = walletRepository.save(walletAggregate.getWallet());
        
        // Publicar eventos de domínio de forma assíncrona
        walletAggregate.getDomainEvents().forEach(event -> {
            eventPublisher.publish(event);
        });
        
        log.info("Carteira criada com sucesso. ID: {}", savedWallet.getId());
        
        // Retornar a resposta
        return WalletResponse.from(savedWallet);
    }
} 