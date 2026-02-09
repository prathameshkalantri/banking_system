package com.prathamesh.banking.strategy;

import com.prathamesh.banking.domain.Account;
import com.prathamesh.banking.domain.AccountType;
import com.prathamesh.banking.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CheckingAccountStrategy.
 * Verifies fee calculations, validations, and business rules.
 */
@DisplayName("CheckingAccountStrategy Tests")
class CheckingAccountStrategyTest {

    private CheckingAccountStrategy strategy;
    private Account checkingAccount;

    @BeforeEach
    void setUp() {
        strategy = new CheckingAccountStrategy();
        checkingAccount = new Account("ACC-001", "John Doe", 
                AccountType.CHECKING, new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Should validate withdrawal successfully with sufficient funds")
    void shouldValidateWithdrawalWithSufficientFunds() {
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(checkingAccount, new BigDecimal("500.00"));
        });
    }

    @Test
    @DisplayName("Should throw exception when account is null")
    void shouldThrowExceptionWhenAccountIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.validateWithdrawal(null, new BigDecimal("100.00"));
        });
    }

    @Test
    @DisplayName("Should throw exception when amount is null")
    void shouldThrowExceptionWhenAmountIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.validateWithdrawal(checkingAccount, null);
        });
    }

    @Test
    @DisplayName("Should throw exception when amount is negative")
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.validateWithdrawal(checkingAccount, new BigDecimal("-100.00"));
        });
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException when insufficient funds")
    void shouldThrowInsufficientFundsException() {
        assertThrows(InsufficientFundsException.class, () -> {
            strategy.validateWithdrawal(checkingAccount, new BigDecimal("2000.00"));
        });
    }

    @Test
    @DisplayName("Should allow withdrawal to zero balance")
    void shouldAllowWithdrawalToZeroBalance() {
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(checkingAccount, new BigDecimal("1000.00"));
        });
    }

    @Test
    @DisplayName("Should allow zero amount withdrawal")
    void shouldAllowZeroAmountWithdrawal() {
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(checkingAccount, BigDecimal.ZERO);
        });
    }

    @Test
    @DisplayName("Should allow withdrawal when balance equals amount")
    void shouldAllowWithdrawalWhenBalanceEqualsAmount() {
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(checkingAccount, checkingAccount.getBalance());
        });
    }

    @Test
    @DisplayName("Should apply no fee when transactions are below limit")
    void shouldApplyNoFeeWhenBelowLimit() {
        // Perform 5 transactions (below 10)
        for (int i = 0; i < 5; i++) {
            checkingAccount.deposit(new BigDecimal("10.00"), "TXN-" + i);
        }

        BigDecimal balanceBefore = checkingAccount.getBalance();
        strategy.applyMonthlyAdjustments(checkingAccount);
        BigDecimal balanceAfter = checkingAccount.getBalance();

        assertEquals(balanceBefore, balanceAfter, "No fee should be applied");
        assertEquals(0, checkingAccount.getMonthlyTransactionCount());
    }

    @Test
    @DisplayName("Should apply no fee when transactions equal 10")
    void shouldApplyNoFeeWhenExactly10Transactions() {
        // Perform exactly 10 transactions
        for (int i = 0; i < 10; i++) {
            checkingAccount.deposit(new BigDecimal("10.00"), "TXN-" + i);
        }

        BigDecimal balanceBefore = checkingAccount.getBalance();
        strategy.applyMonthlyAdjustments(checkingAccount);
        BigDecimal balanceAfter = checkingAccount.getBalance();

        assertEquals(balanceBefore, balanceAfter, "No fee should be applied for exactly 10");
        assertEquals(0, checkingAccount.getMonthlyTransactionCount());
    }

    @Test
    @DisplayName("Should apply $2.50 fee for 11 transactions")
    void shouldApplyFeeFor11Transactions() {
        // Perform 11 transactions
        for (int i = 0; i < 11; i++) {
            checkingAccount.deposit(new BigDecimal("10.00"), "TXN-" + i);
        }

        BigDecimal balanceBefore = checkingAccount.getBalance();
        strategy.applyMonthlyAdjustments(checkingAccount);
        BigDecimal balanceAfter = checkingAccount.getBalance();

        BigDecimal expectedFee = new BigDecimal("2.50");
        assertEquals(balanceBefore.subtract(expectedFee), balanceAfter, 
                "Should deduct $2.50 for 1 excess transaction");
    }

    @Test
    @DisplayName("Should apply $5.00 fee for 12 transactions")
    void shouldApplyFeeFor12Transactions() {
        // Perform 12 transactions
        for (int i = 0; i < 12; i++) {
            checkingAccount.deposit(new BigDecimal("10.00"), "TXN-" + i);
        }

        strategy.applyMonthlyAdjustments(checkingAccount);

        // Fee = (12 - 10) × $2.50 = $5.00
        // Started with $1000, added 12 × $10 = $120, total $1120, minus $5.00 fee = $1115.00
        assertEquals(new BigDecimal("1115.00"), checkingAccount.getBalance());
    }

    @Test
    @DisplayName("Should apply $25.00 fee for 20 transactions")
    void shouldApplyFeeFor20Transactions() {
        // Perform 20 transactions
        for (int i = 0; i < 20; i++) {
            checkingAccount.deposit(new BigDecimal("10.00"), "TXN-" + i);
        }

        strategy.applyMonthlyAdjustments(checkingAccount);

        // Fee = (20 - 10) × $2.50 = $25.00
        // Originally had $1200 after deposits, minus $25.00 fee = $1175.00
        assertEquals(new BigDecimal("1175.00"), checkingAccount.getBalance());
    }

    @Test
    @DisplayName("Should reset monthly counters after adjustment")
    void shouldResetMonthlyCounters() {
        // Perform transactions
        for (int i = 0; i < 15; i++) {
            checkingAccount.deposit(new BigDecimal("10.00"), "TXN-" + i);
        }

        assertEquals(15, checkingAccount.getMonthlyTransactionCount());
        
        strategy.applyMonthlyAdjustments(checkingAccount);
        
        assertEquals(0, checkingAccount.getMonthlyTransactionCount());
        assertEquals(0, checkingAccount.getMonthlyWithdrawalCount());
    }

    @Test
    @DisplayName("Should throw exception when applying adjustments to null account")
    void shouldThrowExceptionWhenApplyingAdjustmentsToNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.applyMonthlyAdjustments(null);
        });
    }

    @Test
    @DisplayName("Should apply fee even if balance becomes negative")
    void shouldApplyFeeEvenIfBalanceBecomesNegative() {
        // Create account with low balance
        Account lowBalanceAccount = new Account("ACC-002", "Jane Doe", 
                AccountType.CHECKING, new BigDecimal("5.00"));
        
        // Perform 15 transactions
        for (int i = 0; i < 15; i++) {
            lowBalanceAccount.incrementTransactionCount();
        }

        strategy.applyMonthlyAdjustments(lowBalanceAccount);

        // Fee = (15 - 10) × $2.50 = $12.50
        // Balance: $5.00 - $12.50 = -$7.50 (negative)
        assertEquals(new BigDecimal("-7.50"), lowBalanceAccount.getBalance());
    }

    @Test
    @DisplayName("Should provide business rules description")
    void shouldProvideBusinessRulesDescription() {
        String description = strategy.getBusinessRulesDescription();
        
        assertNotNull(description);
        assertTrue(description.contains("CHECKING"));
        assertTrue(description.contains("10"));
        assertTrue(description.contains("2.50"));
        assertTrue(description.contains("No minimum balance"));
    }

    @Test
    @DisplayName("Should have correct transaction fee constant")
    void shouldHaveCorrectTransactionFee() {
        assertEquals(new BigDecimal("2.50"), CheckingAccountStrategy.getTransactionFee());
    }

    @Test
    @DisplayName("Should have correct free transaction limit")
    void shouldHaveCorrectFreeTransactionLimit() {
        assertEquals(10, CheckingAccountStrategy.getFreeTransactionsPerMonth());
    }

    @Test
    @DisplayName("Should handle large number of transactions")
    void shouldHandleLargeNumberOfTransactions() {
        // Perform 100 transactions
        for (int i = 0; i < 100; i++) {
            checkingAccount.deposit(new BigDecimal("1.00"), "TXN-" + i);
        }

        strategy.applyMonthlyAdjustments(checkingAccount);

        // Fee = (100 - 10) × $2.50 = $225.00
        // Originally had $1100 after deposits, minus $225.00 fee = $875.00
        assertEquals(new BigDecimal("875.00"), checkingAccount.getBalance());
    }

    @Test
    @DisplayName("Should allow multiple successive withdrawals")
    void shouldAllowMultipleSuccessiveWithdrawals() {
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(checkingAccount, new BigDecimal("200.00"));
            checkingAccount.withdraw(new BigDecimal("200.00"), "TXN-001");
            
            strategy.validateWithdrawal(checkingAccount, new BigDecimal("300.00"));
            checkingAccount.withdraw(new BigDecimal("300.00"), "TXN-002");
            
            strategy.validateWithdrawal(checkingAccount, new BigDecimal("400.00"));
            checkingAccount.withdraw(new BigDecimal("400.00"), "TXN-003");
        });

        assertEquals(new BigDecimal("100.00"), checkingAccount.getBalance());
    }

    @Test
    @DisplayName("Should not have minimum balance requirement")
    void shouldNotHaveMinimumBalanceRequirement() {
        Account acc = new Account("ACC-003", "Test", 
                AccountType.CHECKING, new BigDecimal("150.00"));
        
        // Should allow withdrawal to zero (no minimum balance)
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(acc, new BigDecimal("150.00"));
        });
    }
}
