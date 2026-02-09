package com.prathamesh.banking.model;

import com.prathamesh.banking.domain.Account;
import com.prathamesh.banking.domain.AccountType;
import com.prathamesh.banking.domain.Transaction;
import com.prathamesh.banking.domain.TransactionType;
import com.prathamesh.banking.domain.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Account entity.
 * Verifies creation, validation, operations, and state management.
 */
@DisplayName("Account Model Tests")
class AccountTest {

    private static final String ACCOUNT_NUMBER = "ACC-001";
    private static final String CUSTOMER_NAME = "John Doe";
    private static final BigDecimal INITIAL_DEPOSIT = new BigDecimal("1000.00");

    @Test
    @DisplayName("Should create CHECKING account successfully")
    void shouldCreateCheckingAccountSuccessfully() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);

        assertNotNull(account);
        assertEquals(ACCOUNT_NUMBER, account.getAccountNumber());
        assertEquals(CUSTOMER_NAME, account.getCustomerName());
        assertEquals(AccountType.CHECKING, account.getAccountType());
        assertEquals(INITIAL_DEPOSIT, account.getBalance());
        assertEquals(0, account.getMonthlyTransactionCount());
        assertEquals(0, account.getMonthlyWithdrawalCount());
        assertTrue(account.getTransactionHistory().isEmpty());
    }

    @Test
    @DisplayName("Should create SAVINGS account successfully")
    void shouldCreateSavingsAccountSuccessfully() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.SAVINGS, INITIAL_DEPOSIT);

        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals(INITIAL_DEPOSIT, account.getBalance());
    }

    @Test
    @DisplayName("Should create account with zero initial deposit")
    void shouldCreateAccountWithZeroInitialDeposit() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, new BigDecimal("0.00"));

        assertEquals(new BigDecimal("0.00"), account.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when account number is null")
    void shouldThrowExceptionWhenAccountNumberIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Account(null, CUSTOMER_NAME, AccountType.CHECKING, INITIAL_DEPOSIT);
        });
    }

    @Test
    @DisplayName("Should throw exception when account number is blank")
    void shouldThrowExceptionWhenAccountNumberIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Account("  ", CUSTOMER_NAME, AccountType.CHECKING, INITIAL_DEPOSIT);
        });
    }

    @Test
    @DisplayName("Should throw exception when customer name is null")
    void shouldThrowExceptionWhenCustomerNameIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Account(ACCOUNT_NUMBER, null, AccountType.CHECKING, INITIAL_DEPOSIT);
        });
    }

    @Test
    @DisplayName("Should throw exception when customer name is blank")
    void shouldThrowExceptionWhenCustomerNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Account(ACCOUNT_NUMBER, "   ", AccountType.CHECKING, INITIAL_DEPOSIT);
        });
    }

    @Test
    @DisplayName("Should throw exception when account type is null")
    void shouldThrowExceptionWhenAccountTypeIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, null, INITIAL_DEPOSIT);
        });
    }

    @Test
    @DisplayName("Should throw exception when initial deposit is null")
    void shouldThrowExceptionWhenInitialDepositIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, AccountType.CHECKING, null);
        });
    }

    @Test
    @DisplayName("Should throw exception when initial deposit is negative")
    void shouldThrowExceptionWhenInitialDepositIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, AccountType.CHECKING, 
                    new BigDecimal("-100.00"));
        });
    }

    @Test
    @DisplayName("Should deposit successfully and update balance")
    void shouldDepositSuccessfully() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        BigDecimal depositAmount = new BigDecimal("500.00");
        Transaction transaction = account.deposit(depositAmount, "TXN-001");

        assertEquals(new BigDecimal("1500.00"), account.getBalance());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(depositAmount, transaction.getAmount());
        assertEquals(1, account.getTransactionHistory().size());
        assertEquals(1, account.getMonthlyTransactionCount());
    }

    @Test
    @DisplayName("Should throw exception when deposit amount is null")
    void shouldThrowExceptionWhenDepositAmountIsNull() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        assertThrows(IllegalArgumentException.class, () -> {
            account.deposit(null, "TXN-001");
        });
    }

    @Test
    @DisplayName("Should throw exception when deposit amount is zero")
    void shouldThrowExceptionWhenDepositAmountIsZero() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        assertThrows(IllegalArgumentException.class, () -> {
            account.deposit(BigDecimal.ZERO, "TXN-001");
        });
    }

    @Test
    @DisplayName("Should throw exception when deposit amount is negative")
    void shouldThrowExceptionWhenDepositAmountIsNegative() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        assertThrows(IllegalArgumentException.class, () -> {
            account.deposit(new BigDecimal("-50.00"), "TXN-001");
        });
    }

    @Test
    @DisplayName("Should withdraw successfully and update balance")
    void shouldWithdrawSuccessfully() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        BigDecimal withdrawAmount = new BigDecimal("300.00");
        Transaction transaction = account.withdraw(withdrawAmount, "TXN-001");

        assertEquals(new BigDecimal("700.00"), account.getBalance());
        assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(withdrawAmount, transaction.getAmount());
        assertEquals(1, account.getTransactionHistory().size());
        assertEquals(1, account.getMonthlyTransactionCount());
        assertEquals(1, account.getMonthlyWithdrawalCount());
    }

    @Test
    @DisplayName("Should throw exception when withdraw amount exceeds balance")
    void shouldThrowExceptionWhenWithdrawExceedsBalance() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        assertThrows(IllegalArgumentException.class, () -> {
            account.withdraw(new BigDecimal("2000.00"), "TXN-001");
        });
    }

    @Test
    @DisplayName("Should throw exception when withdraw amount is null")
    void shouldThrowExceptionWhenWithdrawAmountIsNull() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        assertThrows(IllegalArgumentException.class, () -> {
            account.withdraw(null, "TXN-001");
        });
    }

    @Test
    @DisplayName("Should throw exception when withdraw amount is zero")
    void shouldThrowExceptionWhenWithdrawAmountIsZero() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        assertThrows(IllegalArgumentException.class, () -> {
            account.withdraw(BigDecimal.ZERO, "TXN-001");
        });
    }

    @Test
    @DisplayName("Should transfer out successfully")
    void shouldTransferOutSuccessfully() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        BigDecimal transferAmount = new BigDecimal("200.00");
        Transaction transaction = account.transferOut(transferAmount, "TXN-001");

        assertEquals(new BigDecimal("800.00"), account.getBalance());
        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(1, account.getMonthlyTransactionCount());
        // Transfer does NOT increment withdrawal count
        assertEquals(0, account.getMonthlyWithdrawalCount());
    }

    @Test
    @DisplayName("Should throw exception when transfer exceeds balance")
    void shouldThrowExceptionWhenTransferExceedsBalance() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        assertThrows(IllegalArgumentException.class, () -> {
            account.transferOut(new BigDecimal("2000.00"), "TXN-001");
        });
    }

    @Test
    @DisplayName("Should apply fee successfully")
    void shouldApplyFeeSuccessfully() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        BigDecimal feeAmount = new BigDecimal("2.50");
        Transaction transaction = account.applyFee(feeAmount, "TXN-001");

        assertEquals(new BigDecimal("997.50"), account.getBalance());
        assertEquals(TransactionType.FEE, transaction.getType());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(feeAmount, transaction.getAmount());
        // Fee does NOT increment transaction count
        assertEquals(0, account.getMonthlyTransactionCount());
    }

    @Test
    @DisplayName("Should apply interest successfully")
    void shouldApplyInterestSuccessfully() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.SAVINGS, INITIAL_DEPOSIT);
        
        BigDecimal interestAmount = new BigDecimal("20.00");
        Transaction transaction = account.applyInterest(interestAmount, "TXN-001");

        assertEquals(new BigDecimal("1020.00"), account.getBalance());
        assertEquals(TransactionType.INTEREST, transaction.getType());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        // Interest does NOT increment transaction count
        assertEquals(0, account.getMonthlyTransactionCount());
    }

    @Test
    @DisplayName("Should record failed transaction")
    void shouldRecordFailedTransaction() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        BigDecimal attemptedAmount = new BigDecimal("2000.00");
        BigDecimal balanceBefore = account.getBalance();
        
        Transaction transaction = account.recordFailedTransaction(
                TransactionType.WITHDRAWAL, attemptedAmount, 
                "Insufficient funds", "TXN-001");

        assertEquals(balanceBefore, account.getBalance());
        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
        assertEquals("Insufficient funds", transaction.getFailureReason());
        assertEquals(1, account.getTransactionHistory().size());
    }

    @Test
    @DisplayName("Should reset monthly counters")
    void shouldResetMonthlyCounters() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        // Perform some transactions
        account.deposit(new BigDecimal("100.00"), "TXN-001");
        account.withdraw(new BigDecimal("50.00"), "TXN-002");
        
        assertEquals(2, account.getMonthlyTransactionCount());
        assertEquals(1, account.getMonthlyWithdrawalCount());
        
        // Reset counters
        account.resetMonthlyCounters();
        
        assertEquals(0, account.getMonthlyTransactionCount());
        assertEquals(0, account.getMonthlyWithdrawalCount());
    }

    @Test
    @DisplayName("Should indicate account can be closed when balance is zero")
    void shouldIndicateAccountCanBeClosedWhenBalanceIsZero() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, BigDecimal.ZERO);
        
        assertTrue(account.canBeClosed());
    }

    @Test
    @DisplayName("Should indicate account cannot be closed when balance is positive")
    void shouldIndicateAccountCannotBeClosedWhenBalanceIsPositive() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        assertFalse(account.canBeClosed());
    }

    @Test
    @DisplayName("Should increment transaction count")
    void shouldIncrementTransactionCount() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        assertEquals(0, account.getMonthlyTransactionCount());
        account.incrementTransactionCount();
        assertEquals(1, account.getMonthlyTransactionCount());
        account.incrementTransactionCount();
        assertEquals(2, account.getMonthlyTransactionCount());
    }

    @Test
    @DisplayName("Should increment withdrawal count")
    void shouldIncrementWithdrawalCount() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.SAVINGS, INITIAL_DEPOSIT);
        
        assertEquals(0, account.getMonthlyWithdrawalCount());
        account.incrementWithdrawalCount();
        assertEquals(1, account.getMonthlyWithdrawalCount());
    }

    @Test
    @DisplayName("Should implement equals based on account number")
    void shouldImplementEqualsBasedOnAccountNumber() {
        Account account1 = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        Account account2 = new Account(ACCOUNT_NUMBER, "Jane Doe", 
                AccountType.SAVINGS, new BigDecimal("5000.00"));

        assertEquals(account1, account2, "Accounts with same number should be equal");
    }

    @Test
    @DisplayName("Should not be equal when account numbers differ")
    void shouldNotBeEqualWhenAccountNumbersDiffer() {
        Account account1 = new Account("ACC-001", CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        Account account2 = new Account("ACC-002", CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);

        assertNotEquals(account1, account2, "Accounts with different numbers should not be equal");
    }

    @Test
    @DisplayName("Should implement hashCode consistently with equals")
    void shouldImplementHashCodeConsistently() {
        Account account1 = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        Account account2 = new Account(ACCOUNT_NUMBER, "Jane Doe", 
                AccountType.SAVINGS, new BigDecimal("5000.00"));

        assertEquals(account1.hashCode(), account2.hashCode(), 
                "Equal accounts should have equal hash codes");
    }

    @Test
    @DisplayName("Should have readable toString")
    void shouldHaveReadableToString() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        String toString = account.toString();
        assertTrue(toString.contains(ACCOUNT_NUMBER));
        assertTrue(toString.contains(CUSTOMER_NAME));
        assertTrue(toString.contains("CHECKING"));
    }

    @Test
    @DisplayName("Should return immutable transaction history")
    void shouldReturnImmutableTransactionHistory() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        account.deposit(new BigDecimal("100.00"), "TXN-001");
        
        assertThrows(UnsupportedOperationException.class, () -> {
            account.getTransactionHistory().clear();
        }, "Transaction history should be immutable");
    }

    @Test
    @DisplayName("Should handle multiple transactions correctly")
    void shouldHandleMultipleTransactionsCorrectly() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        account.deposit(new BigDecimal("200.00"), "TXN-001");
        account.withdraw(new BigDecimal("150.00"), "TXN-002");
        account.deposit(new BigDecimal("300.00"), "TXN-003");
        
        assertEquals(new BigDecimal("1350.00"), account.getBalance());
        assertEquals(3, account.getTransactionHistory().size());
        assertEquals(3, account.getMonthlyTransactionCount());
        assertEquals(1, account.getMonthlyWithdrawalCount());
    }

    @Test
    @DisplayName("Should throw exception for amount with more than 2 decimal places")
    void shouldThrowExceptionForInvalidDecimalScale() {
        Account account = new Account(ACCOUNT_NUMBER, CUSTOMER_NAME, 
                AccountType.CHECKING, INITIAL_DEPOSIT);
        
        assertThrows(IllegalArgumentException.class, () -> {
            account.deposit(new BigDecimal("100.123"), "TXN-001");
        }, "Should reject amounts with more than 2 decimal places");
    }
}
