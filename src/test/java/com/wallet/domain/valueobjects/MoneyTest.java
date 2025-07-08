package com.wallet.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create money with valid amount and currency")
    void shouldCreateMoneyWithValidAmountAndCurrency() {
        Money money = new Money(100.50, "BRL");
        
        assertEquals(new BigDecimal("100.50"), money.getAmount());
        assertEquals("BRL", money.getCurrency());
    }

    @Test
    @DisplayName("Should create zero money")
    void shouldCreateZeroMoney() {
        Money zeroBRL = Money.zero("BRL");
        Money zeroUSD = Money.zero("USD");
        
        assertEquals(BigDecimal.ZERO, zeroBRL.getAmount());
        assertEquals("BRL", zeroBRL.getCurrency());
        assertEquals(BigDecimal.ZERO, zeroUSD.getAmount());
        assertEquals("USD", zeroUSD.getCurrency());
    }

    @Test
    @DisplayName("Should add money with same currency")
    void shouldAddMoneyWithSameCurrency() {
        Money money1 = new Money(100.50, "BRL");
        Money money2 = new Money(50.25, "BRL");
        
        Money result = money1.add(money2);
        
        assertEquals(new BigDecimal("150.75"), result.getAmount());
        assertEquals("BRL", result.getCurrency());
    }

    @Test
    @DisplayName("Should subtract money with same currency")
    void shouldSubtractMoneyWithSameCurrency() {
        Money money1 = new Money(100.50, "BRL");
        Money money2 = new Money(30.25, "BRL");
        
        Money result = money1.subtract(money2);
        
        assertEquals(new BigDecimal("70.25"), result.getAmount());
        assertEquals("BRL", result.getCurrency());
    }

    @Test
    @DisplayName("Should throw exception when adding different currencies")
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        Money money1 = new Money(100.50, "BRL");
        Money money2 = new Money(50.25, "USD");
        
        assertThrows(IllegalArgumentException.class, () -> money1.add(money2));
    }

    @Test
    @DisplayName("Should throw exception when subtracting different currencies")
    void shouldThrowExceptionWhenSubtractingDifferentCurrencies() {
        Money money1 = new Money(100.50, "BRL");
        Money money2 = new Money(30.25, "USD");
        
        assertThrows(IllegalArgumentException.class, () -> money1.subtract(money2));
    }

    @Test
    @DisplayName("Should handle negative amounts")
    void shouldHandleNegativeAmounts() {
        Money negativeMoney = new Money(-50.00, "BRL");
        
        assertEquals(new BigDecimal("-50.00"), negativeMoney.getAmount());
        assertEquals("BRL", negativeMoney.getCurrency());
    }

    @Test
    @DisplayName("Should handle very large amounts")
    void shouldHandleVeryLargeAmounts() {
        Money largeAmount = new Money(999999999.99, "BRL");
        
        assertEquals(new BigDecimal("999999999.99"), largeAmount.getAmount());
        assertEquals("BRL", largeAmount.getCurrency());
    }

    @Test
    @DisplayName("Should handle very small amounts")
    void shouldHandleVerySmallAmounts() {
        Money smallAmount = new Money(0.01, "BRL");
        
        assertEquals(new BigDecimal("0.01"), smallAmount.getAmount());
        assertEquals("BRL", smallAmount.getCurrency());
    }

    @ParameterizedTest
    @ValueSource(strings = {"BRL", "USD", "EUR", "GBP", "JPY"})
    @DisplayName("Should accept valid currency codes")
    void shouldAcceptValidCurrencyCodes(String currency) {
        Money money = new Money(100.00, currency);
        
        assertEquals(currency, money.getCurrency());
    }

    @ParameterizedTest
    @CsvSource({
        "100.50, BRL, 50.25, BRL, 150.75",
        "0.00, USD, 100.00, USD, 100.00",
        "999.99, EUR, 0.01, EUR, 1000.00"
    })
    @DisplayName("Should add money correctly with various amounts")
    void shouldAddMoneyCorrectlyWithVariousAmounts(double amount1, String currency1, 
                                                  double amount2, String currency2, 
                                                  double expectedResult) {
        Money money1 = new Money(amount1, currency1);
        Money money2 = new Money(amount2, currency2);
        
        Money result = money1.add(money2);
        
        assertEquals(new BigDecimal(String.valueOf(expectedResult)), result.getAmount());
        assertEquals(currency1, result.getCurrency());
    }

    @Test
    @DisplayName("Should be equal when amount and currency are the same")
    void shouldBeEqualWhenAmountAndCurrencyAreTheSame() {
        Money money1 = new Money(100.50, "BRL");
        Money money2 = new Money(100.50, "BRL");
        
        assertEquals(money1, money2);
        assertEquals(money1.hashCode(), money2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when amounts are different")
    void shouldNotBeEqualWhenAmountsAreDifferent() {
        Money money1 = new Money(100.50, "BRL");
        Money money2 = new Money(100.51, "BRL");
        
        assertNotEquals(money1, money2);
    }

    @Test
    @DisplayName("Should not be equal when currencies are different")
    void shouldNotBeEqualWhenCurrenciesAreDifferent() {
        Money money1 = new Money(100.50, "BRL");
        Money money2 = new Money(100.50, "USD");
        
        assertNotEquals(money1, money2);
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        Money money = new Money(100.50, "BRL");
        String toString = money.toString();
        
        assertTrue(toString.contains("100.50"));
        assertTrue(toString.contains("BRL"));
    }

    @Test
    @DisplayName("Should handle zero addition")
    void shouldHandleZeroAddition() {
        Money money = new Money(100.50, "BRL");
        Money zero = Money.zero("BRL");
        
        Money result = money.add(zero);
        
        assertEquals(money, result);
    }

    @Test
    @DisplayName("Should handle zero subtraction")
    void shouldHandleZeroSubtraction() {
        Money money = new Money(100.50, "BRL");
        Money zero = Money.zero("BRL");
        
        Money result = money.subtract(zero);
        
        assertEquals(money, result);
    }

    @Test
    @DisplayName("Should handle precision correctly")
    void shouldHandlePrecisionCorrectly() {
        Money money1 = new Money(0.1, "BRL");
        Money money2 = new Money(0.2, "BRL");
        
        Money result = money1.add(money2);
        
        assertEquals(new BigDecimal("0.3"), result.getAmount());
    }
} 