package com.wallet.domain.entities;

import com.wallet.domain.enums.WalletStatus;
import com.wallet.domain.valueobjects.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Wallet Entity Tests")
class WalletEntityTest {

    private WalletEntity wallet;
    private WalletEntity targetWallet;

    @BeforeEach
    void setUp() {
        wallet = WalletEntity.create("user123", "BRL");
        targetWallet = WalletEntity.create("user456", "BRL");
    }

    @Test
    @DisplayName("Should create wallet with correct initial values")
    void shouldCreateWalletWithCorrectInitialValues() {
        assertEquals("user123", wallet.getUserId());
        assertEquals(Money.zero("BRL"), wallet.getBalance());
        assertEquals(WalletStatus.ACTIVE, wallet.getStatus());
        assertNotNull(wallet.getTransactions());
        assertTrue(wallet.getTransactions().isEmpty());
    }

    @Test
    @DisplayName("Should deposit money successfully")
    void shouldDepositMoneySuccessfully() {
        Money depositAmount = new Money(100.50, "BRL");
        
        wallet.deposit(depositAmount);
        
        assertEquals(depositAmount, wallet.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when depositing with different currency")
    void shouldThrowExceptionWhenDepositingWithDifferentCurrency() {
        Money depositAmount = new Money(100.50, "USD");
        
        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(depositAmount));
    }

    @Test
    @DisplayName("Should throw exception when depositing to suspended wallet")
    void shouldThrowExceptionWhenDepositingToSuspendedWallet() {
        wallet.suspend();
        Money depositAmount = new Money(100.50, "BRL");
        
        assertThrows(IllegalStateException.class, () -> wallet.deposit(depositAmount));
    }

    @Test
    @DisplayName("Should withdraw money successfully")
    void shouldWithdrawMoneySuccessfully() {
        Money depositAmount = new Money(100.50, "BRL");
        Money withdrawAmount = new Money(30.25, "BRL");
        
        wallet.deposit(depositAmount);
        wallet.withdraw(withdrawAmount);
        
        assertEquals(new Money(70.25, "BRL"), wallet.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when withdrawing with different currency")
    void shouldThrowExceptionWhenWithdrawingWithDifferentCurrency() {
        Money depositAmount = new Money(100.50, "BRL");
        Money withdrawAmount = new Money(30.25, "USD");
        
        wallet.deposit(depositAmount);
        
        assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(withdrawAmount));
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from suspended wallet")
    void shouldThrowExceptionWhenWithdrawingFromSuspendedWallet() {
        Money depositAmount = new Money(100.50, "BRL");
        Money withdrawAmount = new Money(30.25, "BRL");
        
        wallet.deposit(depositAmount);
        wallet.suspend();
        
        assertThrows(IllegalStateException.class, () -> wallet.withdraw(withdrawAmount));
    }

    @Test
    @DisplayName("Should transfer money successfully")
    void shouldTransferMoneySuccessfully() {
        Money depositAmount = new Money(100.50, "BRL");
        Money transferAmount = new Money(30.25, "BRL");
        
        wallet.deposit(depositAmount);
        wallet.transferTo(targetWallet, transferAmount);
        
        assertEquals(new Money(70.25, "BRL"), wallet.getBalance());
        assertEquals(transferAmount, targetWallet.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when transferring with different currency")
    void shouldThrowExceptionWhenTransferringWithDifferentCurrency() {
        Money depositAmount = new Money(100.50, "BRL");
        Money transferAmount = new Money(30.25, "USD");
        
        wallet.deposit(depositAmount);
        
        assertThrows(IllegalArgumentException.class, () -> wallet.transferTo(targetWallet, transferAmount));
    }

    @Test
    @DisplayName("Should throw exception when transferring from suspended wallet")
    void shouldThrowExceptionWhenTransferringFromSuspendedWallet() {
        Money depositAmount = new Money(100.50, "BRL");
        Money transferAmount = new Money(30.25, "BRL");
        
        wallet.deposit(depositAmount);
        wallet.suspend();
        
        assertThrows(IllegalStateException.class, () -> wallet.transferTo(targetWallet, transferAmount));
    }

    @Test
    @DisplayName("Should throw exception when transferring to suspended wallet")
    void shouldThrowExceptionWhenTransferringToSuspendedWallet() {
        Money depositAmount = new Money(100.50, "BRL");
        Money transferAmount = new Money(30.25, "BRL");
        
        wallet.deposit(depositAmount);
        targetWallet.suspend();
        
        assertThrows(IllegalStateException.class, () -> wallet.transferTo(targetWallet, transferAmount));
    }

    @Test
    @DisplayName("Should suspend wallet successfully")
    void shouldSuspendWalletSuccessfully() {
        wallet.suspend();
        
        assertEquals(WalletStatus.SUSPENDED, wallet.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when suspending closed wallet")
    void shouldThrowExceptionWhenSuspendingClosedWallet() {
        wallet.close();
        
        assertThrows(IllegalStateException.class, () -> wallet.suspend());
    }

    @Test
    @DisplayName("Should activate wallet successfully")
    void shouldActivateWalletSuccessfully() {
        wallet.suspend();
        wallet.activate();
        
        assertEquals(WalletStatus.ACTIVE, wallet.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when activating closed wallet")
    void shouldThrowExceptionWhenActivatingClosedWallet() {
        wallet.close();
        
        assertThrows(IllegalStateException.class, () -> wallet.activate());
    }

    @Test
    @DisplayName("Should close wallet with zero balance")
    void shouldCloseWalletWithZeroBalance() {
        wallet.close();
        
        assertEquals(WalletStatus.CLOSED, wallet.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when closing wallet with positive balance")
    void shouldThrowExceptionWhenClosingWalletWithPositiveBalance() {
        Money depositAmount = new Money(100.50, "BRL");
        wallet.deposit(depositAmount);
        
        assertThrows(IllegalStateException.class, () -> wallet.close());
    }

    @Test
    @DisplayName("Should handle multiple deposits")
    void shouldHandleMultipleDeposits() {
        Money deposit1 = new Money(100.50, "BRL");
        Money deposit2 = new Money(50.25, "BRL");
        
        wallet.deposit(deposit1);
        wallet.deposit(deposit2);
        
        assertEquals(new Money(150.75, "BRL"), wallet.getBalance());
    }

    @Test
    @DisplayName("Should handle multiple withdrawals")
    void shouldHandleMultipleWithdrawals() {
        Money depositAmount = new Money(100.50, "BRL");
        Money withdraw1 = new Money(30.25, "BRL");
        Money withdraw2 = new Money(20.00, "BRL");
        
        wallet.deposit(depositAmount);
        wallet.withdraw(withdraw1);
        wallet.withdraw(withdraw2);
        
        assertEquals(new Money(50.25, "BRL"), wallet.getBalance());
    }

    @Test
    @DisplayName("Should handle complex transfer scenario")
    void shouldHandleComplexTransferScenario() {
        Money depositAmount = new Money(1000.00, "BRL");
        Money transfer1 = new Money(300.00, "BRL");
        Money transfer2 = new Money(200.00, "BRL");
        
        wallet.deposit(depositAmount);
        wallet.transferTo(targetWallet, transfer1);
        wallet.transferTo(targetWallet, transfer2);
        
        assertEquals(new Money(500.00, "BRL"), wallet.getBalance());
        assertEquals(new Money(500.00, "BRL"), targetWallet.getBalance());
    }

    @Test
    @DisplayName("Should return defensive copy of transactions list")
    void shouldReturnDefensiveCopyOfTransactionsList() {
        var transactions = wallet.getTransactions();
        transactions.add(null); // Try to modify the returned list
        
        assertTrue(wallet.getTransactions().isEmpty()); // Original list should be unchanged
    }

    @Test
    @DisplayName("Should handle edge case with very small amounts")
    void shouldHandleEdgeCaseWithVerySmallAmounts() {
        Money smallAmount = new Money(0.01, "BRL");
        
        wallet.deposit(smallAmount);
        wallet.withdraw(smallAmount);
        
        assertEquals(Money.zero("BRL"), wallet.getBalance());
    }

    @Test
    @DisplayName("Should handle edge case with very large amounts")
    void shouldHandleEdgeCaseWithVeryLargeAmounts() {
        Money largeAmount = new Money(999999999.99, "BRL");
        
        wallet.deposit(largeAmount);
        wallet.withdraw(largeAmount);
        
        assertEquals(Money.zero("BRL"), wallet.getBalance());
    }
} 