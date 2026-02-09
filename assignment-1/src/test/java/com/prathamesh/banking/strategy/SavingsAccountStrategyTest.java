package com.prathamesh.banking.strategy;

import com.prathamesh.banking.domain.Account;
import com.prathamesh.banking.domain.AccountType;
import com.prathamesh.banking.exception.InsufficientFundsException;
import com.prathamesh.banking.exception.MinimumBalanceViolationException;
import com.prathamesh.banking.exception.WithdrawalLimitExceededException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SavingsAccountStrategy.
 * Verifies interest calculations, minimum balance, withdrawal limits.
 */
@DisplayName("SavingsAccountStrategy Tests")
class SavingsAccountStrategyTest {

    private SavingsAccountStrategy strategy;
    private Account savingsAccount;

    @BeforeEach
    void setUp() {
        strategy = new SavingsAccountStrategy();
        savingsAccount = new Account("ACC-001", "John Doe", 
                AccountType.SAVINGS, new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Should validate withdrawal successfully with sufficient funds and balance")
    void shouldValidateWithdrawalWithSufficientFunds() {
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(savingsAccount, new BigDecimal("500.00"));
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
            strategy.validateWithdrawal(savingsAccount, null);
        });
    }

    @Test
    @DisplayName("Should throw exception when amount is negative")
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.validateWithdrawal(savingsAccount, new BigDecimal("-100.00"));
        });
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException when insufficient funds")
    void shouldThrowInsufficientFundsException() {
        assertThrows(InsufficientFundsException.class, () -> {
            strategy.validateWithdrawal(savingsAccount, new BigDecimal("2000.00"));
        });
    }

    @Test
    @DisplayName("Should throw MinimumBalanceViolationException when violating minimum")
    void shouldThrowMinimumBalanceViolationException() {
        // Account has $1000, trying to withdraw $950 would leave $50 (below $100 minimum)
        assertThrows(MinimumBalanceViolationException.class, () -> {
            strategy.validateWithdrawal(savingsAccount, new BigDecimal("950.00"));
        });
    }

    @Test
    @DisplayName("Should allow withdrawal keeping exactly minimum balance")
    void shouldAllowWithdrawalKeepingExactlyMinimumBalance() {
        // Account has $1000, withdraw $900 leaves exactly $100
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(savingsAccount, new BigDecimal("900.00"));
        });
    }

    @Test
    @DisplayName("Should allow withdrawal keeping balance above minimum")
    void shouldAllowWithdrawalKeepingBalanceAboveMinimum() {
        // Account has $1000, withdraw $850 leaves $150 (above $100)
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(savingsAccount, new BigDecimal("850.00"));
        });
    }

    @Test
    @DisplayName("Should throw WithdrawalLimitExceededException when limit reached")
    void shouldThrowWithdrawalLimitExceededException() {
        // Perform 5 withdrawals
        for (int i = 0; i < 5; i++) {
            savingsAccount.withdraw(new BigDecimal("10.00"), "TXN-" + i);
        }

        // 6th withdrawal should fail
        assertThrows(WithdrawalLimitExceededException.class, () -> {
            strategy.validateWithdrawal(savingsAccount, new BigDecimal("10.00"));
        });
    }

    @Test
    @DisplayName("Should allow 5 withdrawals per month")
    void shouldAllow5WithdrawalsPerMonth() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 5; i++) {
                strategy.validateWithdrawal(savingsAccount, new BigDecimal("10.00"));
                savingsAccount.withdraw(new BigDecimal("10.00"), "TXN-" + i);
            }
        });

        assertEquals(5, savingsAccount.getMonthlyWithdrawalCount());
    }

    @Test
    @DisplayName("Should apply 2% monthly interest")
    void shouldApply2PercentInterest() {
        BigDecimal balanceBefore = savingsAccount.getBalance(); // $1000.00
        strategy.applyMonthlyAdjustments(savingsAccount);
        BigDecimal balanceAfter = savingsAccount.getBalance();

        // 2% of $1000 = $20.00
        BigDecimal expectedInterest = new BigDecimal("20.00");
        assertEquals(balanceBefore.add(expectedInterest), balanceAfter);
        assertEquals(new BigDecimal("1020.00"), balanceAfter);
    }

    @Test
    @DisplayName("Should calculate interest correctly for various balances")
    void shouldCalculateInterestCorrectly() {
        // Test with $500 balance
        Account acc = new Account("ACC-002", "Jane Doe", 
                AccountType.SAVINGS, new BigDecimal("500.00"));
        
        strategy.applyMonthlyAdjustments(acc);
        
        // 2% of $500 = $10.00
        assertEquals(new BigDecimal("510.00"), acc.getBalance());
    }

    @Test
    @DisplayName("Should calculate interest with proper rounding")
    void shouldCalculateInterestWithProperRounding() {
        // Test with odd amount
        Account acc = new Account("ACC-003", "Test", 
                AccountType.SAVINGS, new BigDecimal("123.45"));
        
        strategy.applyMonthlyAdjustments(acc);
        
        // 2% of $123.45 = $2.469 → rounds to $2.47
        assertEquals(new BigDecimal("125.92"), acc.getBalance());
    }

    @Test
    @DisplayName("Should reset monthly counters after adjustment")
    void shouldResetMonthlyCounters() {
        // Perform transactions
        savingsAccount.deposit(new BigDecimal("100.00"), "TXN-001");
        savingsAccount.withdraw(new BigDecimal("50.00"), "TXN-002");

        assertEquals(2, savingsAccount.getMonthlyTransactionCount());
        assertEquals(1, savingsAccount.getMonthlyWithdrawalCount());
        
        strategy.applyMonthlyAdjustments(savingsAccount);
        
        assertEquals(0, savingsAccount.getMonthlyTransactionCount());
        assertEquals(0, savingsAccount.getMonthlyWithdrawalCount());
    }

    @Test
    @DisplayName("Should throw exception when applying adjustments to null account")
    void shouldThrowExceptionWhenApplyingAdjustmentsToNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.applyMonthlyAdjustments(null);
        });
    }

    @Test
    @DisplayName("Should not apply interest on zero balance")
    void shouldNotApplyInterestOnZeroBalance() {
        Account acc = new Account("ACC-004", "Test", 
                AccountType.SAVINGS, BigDecimal.ZERO);
        
        strategy.applyMonthlyAdjustments(acc);
        
        assertEquals(0, acc.getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should provide business rules description")
    void shouldProvideBusinessRulesDescription() {
        String description = strategy.getBusinessRulesDescription();
        
        assertNotNull(description);
        assertTrue(description.contains("SAVINGS"));
        assertTrue(description.contains("100"));
        assertTrue(description.contains("2%"));
        assertTrue(description.contains("5"));
    }

    @Test
    @DisplayName("Should have correct minimum balance constant")
    void shouldHaveCorrectMinimumBalance() {
        assertEquals(new BigDecimal("100.00"), SavingsAccountStrategy.getMinimumBalance());
    }

    @Test
    @DisplayName("Should have correct interest rate constant")
    void shouldHaveCorrectInterestRate() {
        assertEquals(new BigDecimal("0.02"), SavingsAccountStrategy.getMonthlyInterestRate());
    }

    @Test
    @DisplayName("Should have correct withdrawal limit constant")
    void shouldHaveCorrectWithdrawalLimit() {
        assertEquals(5, SavingsAccountStrategy.getMaxWithdrawalsPerMonth());
    }

    @Test
    @DisplayName("Should enforce all three rules simultaneously")
    void shouldEnforceAllThreeRulesSimultaneously() {
        // Balance: $1000
        // Rule 1: Sufficient funds - OK
        // Rule 2: Minimum balance - would leave $50 (below $100) - FAIL
        // Rule 3: Withdrawal limit - 0 withdrawals so far - OK
        
        assertThrows(MinimumBalanceViolationException.class, () -> {
            strategy.validateWithdrawal(savingsAccount, new BigDecimal("950.00"));
        });
    }

    @Test
    @DisplayName("Should fail on insufficient funds before checking minimum balance")
    void shouldFailOnInsufficientFundsBeforeMinimumBalance() {
        // Trying to withdraw more than balance
        assertThrows(InsufficientFundsException.class, () -> {
            strategy.validateWithdrawal(savingsAccount, new BigDecimal("1500.00"));
        });
    }

    @Test
    @DisplayName("Should apply interest on large balance")
    void shouldApplyInterestOnLargeBalance() {
        Account richAccount = new Account("ACC-005", "Rich Person", 
                AccountType.SAVINGS, new BigDecimal("100000.00"));
        
        strategy.applyMonthlyAdjustments(richAccount);
        
        // 2% of $100,000 = $2,000
        assertEquals(new BigDecimal("102000.00"), richAccount.getBalance());
    }

    @Test
    @DisplayName("Should handle edge case of exactly 5 withdrawals")
    void shouldHandleEdgeCaseOfExactly5Withdrawals() {
        // Make 4 withdrawals
        for (int i = 0; i < 4; i++) {
            savingsAccount.withdraw(new BigDecimal("10.00"), "TXN-" + i);
        }

        // 5th should succeed
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(savingsAccount, new BigDecimal("10.00"));
        });
    }

    @Test
    @DisplayName("Should handle minimum balance edge case")
    void shouldHandleMinimumBalanceEdgeCase() {
        Account minAccount = new Account("ACC-006", "Min Balance", 
                AccountType.SAVINGS, new BigDecimal("100.00"));
        
        // Cannot withdraw anything, would violate minimum
        assertThrows(MinimumBalanceViolationException.class, () -> {
            strategy.validateWithdrawal(minAccount, new BigDecimal("0.01"));
        });
    }

    @Test
    @DisplayName("Should allow zero amount withdrawal without violating rules")
    void shouldAllowZeroAmountWithdrawal() {
        assertDoesNotThrow(() -> {
            strategy.validateWithdrawal(savingsAccount, BigDecimal.ZERO);
        });
    }

    @Test
    @DisplayName("Should calculate compound interest effect over multiple months")
    void shouldCalculateCompoundInterestEffect() {
        BigDecimal initialBalance = new BigDecimal("1000.00");
        Account acc = new Account("ACC-007", "Compound Test", 
                AccountType.SAVINGS, initialBalance);
        
        // Month 1: $1000 + $20 = $1020
        strategy.applyMonthlyAdjustments(acc);
        assertEquals(new BigDecimal("1020.00"), acc.getBalance());
        
        // Month 2: $1020 + $20.40 = $1040.40
        strategy.applyMonthlyAdjustments(acc);
        assertEquals(new BigDecimal("1040.40"), acc.getBalance());
        
        // Month 3: $1040.40 + $20.81 = $1061.21
        strategy.applyMonthlyAdjustments(acc);
        assertEquals(new BigDecimal("1061.21"), acc.getBalance());
    }
}
