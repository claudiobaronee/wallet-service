package com.wallet.domain.entities;

import com.wallet.domain.enums.WalletStatus;
import com.wallet.domain.valueobjects.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = Wallet.create("user123", "BRL");
    }

    @Test
    void shouldCreateWalletWithZeroBalance() {
        assertEquals("user123", wallet.getUserId());
        assertEquals(BigDecimal.ZERO, wallet.getBalance().getAmount());
        assertEquals("BRL", wallet.getBalance().getCurrency());
        assertEquals(WalletStatus.ACTIVE, wallet.getStatus());
        assertTrue(wallet.isActive());
    }

    @Test
    void shouldDepositMoneySuccessfully() {
        Money depositAmount = new Money(new BigDecimal("100.50"), "BRL");
        Money oldBalance = wallet.getBalance();

        wallet.deposit(depositAmount);

        assertEquals(new BigDecimal("100.50"), wallet.getBalance().getAmount());
        assertTrue(wallet.getBalance().getAmount().compareTo(oldBalance.getAmount()) > 0);
    }

    @Test
    void shouldWithdrawMoneySuccessfully() {
        // Primeiro deposita
        wallet.deposit(new Money(new BigDecimal("100.00"), "BRL"));
        
        // Depois saca
        Money withdrawAmount = new Money(new BigDecimal("30.00"), "BRL");
        Money oldBalance = wallet.getBalance();

        wallet.withdraw(withdrawAmount);

        assertEquals(new BigDecimal("70.00"), wallet.getBalance().getAmount());
        assertTrue(wallet.getBalance().getAmount().compareTo(oldBalance.getAmount()) < 0);
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingMoreThanBalance() {
        wallet.deposit(new Money(new BigDecimal("50.00"), "BRL"));
        
        Money withdrawAmount = new Money(new BigDecimal("100.00"), "BRL");

        assertThrows(IllegalStateException.class, () -> {
            wallet.withdraw(withdrawAmount);
        });
    }

    @Test
    void shouldThrowExceptionWhenDepositingDifferentCurrency() {
        Money depositAmount = new Money(new BigDecimal("100.00"), "USD");

        assertThrows(IllegalArgumentException.class, () -> {
            wallet.deposit(depositAmount);
        });
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingFromInactiveWallet() {
        wallet.suspend();
        
        Money withdrawAmount = new Money(new BigDecimal("10.00"), "BRL");

        assertThrows(IllegalStateException.class, () -> {
            wallet.withdraw(withdrawAmount);
        });
    }

    @Test
    void shouldTransferMoneyBetweenWallets() {
        Wallet sourceWallet = Wallet.create("user1", "BRL");
        Wallet targetWallet = Wallet.create("user2", "BRL");
        
        sourceWallet.deposit(new Money(new BigDecimal("100.00"), "BRL"));
        Money transferAmount = new Money(new BigDecimal("50.00"), "BRL");

        sourceWallet.transferTo(targetWallet, transferAmount);

        assertEquals(new BigDecimal("50.00"), sourceWallet.getBalance().getAmount());
        assertEquals(new BigDecimal("50.00"), targetWallet.getBalance().getAmount());
    }

    @Test
    void shouldThrowExceptionWhenTransferringInsufficientFunds() {
        Wallet sourceWallet = Wallet.create("user1", "BRL");
        Wallet targetWallet = Wallet.create("user2", "BRL");
        
        sourceWallet.deposit(new Money(new BigDecimal("30.00"), "BRL"));
        Money transferAmount = new Money(new BigDecimal("50.00"), "BRL");

        assertThrows(IllegalStateException.class, () -> {
            sourceWallet.transferTo(targetWallet, transferAmount);
        });
    }

    @Test
    void shouldCheckSufficientFundsCorrectly() {
        wallet.deposit(new Money(new BigDecimal("100.00"), "BRL"));
        
        assertTrue(wallet.hasSufficientFunds(new Money(new BigDecimal("50.00"), "BRL")));
        assertTrue(wallet.hasSufficientFunds(new Money(new BigDecimal("100.00"), "BRL")));
        assertFalse(wallet.hasSufficientFunds(new Money(new BigDecimal("150.00"), "BRL")));
    }

    @Test
    void shouldChangeWalletStatus() {
        assertTrue(wallet.isActive());
        
        wallet.suspend();
        assertEquals(WalletStatus.SUSPENDED, wallet.getStatus());
        assertFalse(wallet.isActive());
        
        wallet.activate();
        assertEquals(WalletStatus.ACTIVE, wallet.getStatus());
        assertTrue(wallet.isActive());
        
        wallet.close();
        assertEquals(WalletStatus.CLOSED, wallet.getStatus());
        assertFalse(wallet.isActive());
    }
} 