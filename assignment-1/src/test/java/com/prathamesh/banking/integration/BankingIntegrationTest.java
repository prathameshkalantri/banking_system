package com.prathamesh.banking.integration;

import com.prathamesh.banking.domain.Account;
import com.prathamesh.banking.domain.AccountType;
import com.prathamesh.banking.domain.Transaction;
import com.prathamesh.banking.domain.TransactionStatus;
import com.prathamesh.banking.domain.TransactionType;
import com.prathamesh.banking.exception.*;
import com.prathamesh.banking.service.Bank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Banking System.
 * Tests end-to-end scenarios across multiple components.
 */
@DisplayName("Banking System Integration Tests")
class BankingIntegrationTest {

    private Bank bank;

    @BeforeEach
    void setUp() {
        bank = new Bank(); // Create new instance for each test
    }

    @Test
    @DisplayName("Should complete full user journey: open account, deposit, withdraw, transfer")
    void shouldCompleteFullUserJourney() {
        // Open two accounts
        Account checking = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("1000.00"));
        Account savings = bank.openAccount("Jane Smith", AccountType.SAVINGS, new BigDecimal("500.00"));

        // Deposit to checking
        bank.deposit(checking.getAccountNumber(), new BigDecimal("500.00"));
        assertEquals(new BigDecimal("1500.00"), checking.getBalance());

        // Withdraw from checking
        bank.withdraw(checking.getAccountNumber(), new BigDecimal("200.00"));
        assertEquals(new BigDecimal("1300.00"), checking.getBalance());

        // Transfer from checking to savings
        bank.transfer(checking.getAccountNumber(), savings.getAccountNumber(), new BigDecimal("300.00"));
        assertEquals(new BigDecimal("1000.00"), checking.getBalance());
        assertEquals(new BigDecimal("800.00"), savings.getBalance());

        // Verify transaction histories
        assertTrue(checking.getTransactionHistory().size() > 0);
        assertTrue(savings.getTransactionHistory().size() > 0);
    }

    @Test
    @DisplayName("Should enforce SAVINGS account rules throughout lifecycle")
    void shouldEnforceSavingsAccountRules() {
        Account savings = bank.openAccount("Test User", AccountType.SAVINGS, new BigDecimal("1000.00"));

        // Rule 1: Can make 5 withdrawals
        for (int i = 0; i < 5; i++) {
            bank.withdraw(savings.getAccountNumber(), new BigDecimal("10.00"));
        }
        assertEquals(new BigDecimal("950.00"), savings.getBalance());

        // Rule 2: 6th withdrawal should fail
        Transaction failedTx = bank.withdraw(savings.getAccountNumber(), new BigDecimal("10.00"));
        assertEquals(TransactionStatus.FAILED, failedTx.getStatus());
        assertTrue(failedTx.getFailureReason().toLowerCase().contains("withdrawal") &&
                   failedTx.getFailureReason().toLowerCase().contains("limit"));

        // Rule 3: Cannot violate minimum balance
        Transaction minBalanceFail = bank.withdraw(savings.getAccountNumber(), new BigDecimal("900.00"));
        assertEquals(TransactionStatus.FAILED, minBalanceFail.getStatus());
        assertTrue(minBalanceFail.getFailureReason().toLowerCase().contains("minimum"));

        // Rule 4: Interest should be applied monthly
        bank.applyMonthlyInterest();
        // 2% of $950 = $19.00
        assertEquals(new BigDecimal("969.00"), savings.getBalance());

        // Rule 5: After monthly reset, can withdraw again
        assertEquals(0, savings.getMonthlyWithdrawalCount());
        Transaction successTx = bank.withdraw(savings.getAccountNumber(), new BigDecimal("10.00"));
        assertEquals(TransactionStatus.SUCCESS, successTx.getStatus());
    }

    @Test
    @DisplayName("Should enforce CHECKING account rules throughout lifecycle")
    void shouldEnforceCheckingAccountRules() {
        Account checking = bank.openAccount("Test User", AccountType.CHECKING, new BigDecimal("1000.00"));

        // Make 12 transactions (beyond 10 free limit)
        for (int i = 0; i < 12; i++) {
            bank.deposit(checking.getAccountNumber(), new BigDecimal("10.00"));
        }
        assertEquals(new BigDecimal("1120.00"), checking.getBalance());

        // Apply monthly adjustments - should charge fee for 2 extra transactions
        bank.applyMonthlyInterest(); // This applies all adjustments
        // Fee: 2 × $2.50 = $5.00
        assertEquals(new BigDecimal("1115.00"), checking.getBalance());

        // No minimum balance - can withdraw to zero
        assertDoesNotThrow(() -> {
            bank.withdraw(checking.getAccountNumber(), new BigDecimal("1115.00"));
        });
        assertEquals(0, checking.getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should handle complex transfer scenarios with atomicity")
    void shouldHandleComplexTransferScenarios() {
        Account source = bank.openAccount("Source", AccountType.CHECKING, new BigDecimal("1000.00"));
        Account dest = bank.openAccount("Destination", AccountType.SAVINGS, new BigDecimal("200.00"));

        // Successful transfer
        bank.transfer(source.getAccountNumber(), dest.getAccountNumber(), new BigDecimal("500.00"));
        assertEquals(new BigDecimal("500.00"), source.getBalance());
        assertEquals(new BigDecimal("700.00"), dest.getBalance());

        // Failed transfer - insufficient funds (should be atomic)
        BigDecimal sourceBefore = source.getBalance();
        BigDecimal destBefore = dest.getBalance();
        
        Transaction failedTransfer = bank.transfer(source.getAccountNumber(), dest.getAccountNumber(), new BigDecimal("1000.00"));
        assertEquals(TransactionStatus.FAILED, failedTransfer.getStatus());
        assertTrue(failedTransfer.getFailureReason().contains("Insufficient") || 
                   failedTransfer.getFailureReason().contains("insufficient"));

        // Balances should be unchanged
        assertEquals(sourceBefore, source.getBalance());
        assertEquals(destBefore, dest.getBalance());
    }

    @Test
    @DisplayName("Should handle monthly processing for multiple accounts")
    void shouldHandleMonthlyProcessingForMultipleAccounts() {
        // Create various accounts
        Account checking1 = bank.openAccount("User1", AccountType.CHECKING, new BigDecimal("1000.00"));
        Account checking2 = bank.openAccount("User2", AccountType.CHECKING, new BigDecimal("500.00"));
        Account savings1 = bank.openAccount("User3", AccountType.SAVINGS, new BigDecimal("2000.00"));
        Account savings2 = bank.openAccount("User4", AccountType.SAVINGS, new BigDecimal("1000.00"));

        // Make transactions on checking accounts
        for (int i = 0; i < 15; i++) {
            checking1.deposit(new BigDecimal("1.00"), "TXN-" + i);
        }

        // Apply monthly adjustments
        bank.applyMonthlyInterest();

        // Checking1: 15 txn → fee of 5 × $2.50 = $12.50, balance: $1015 - $12.50 = $1002.50
        assertEquals(new BigDecimal("1002.50"), checking1.getBalance());

        // Checking2: 0 txn → no fee
        assertEquals(new BigDecimal("500.00"), checking2.getBalance());

        // Savings1: 2% interest → $2000 × 0.02 = $40
        assertEquals(new BigDecimal("2040.00"), savings1.getBalance());

        // Savings2: 2% interest → $1000 × 0.02 = $20
        assertEquals(new BigDecimal("1020.00"), savings2.getBalance());
    }

    @Test
    @DisplayName("Should prevent duplicate account numbers")
    void shouldPreventDuplicateAccountNumbers() {
        Account acc1 = bank.openAccount("User1", AccountType.CHECKING, new BigDecimal("1000.00"));
        
        // All account numbers should be unique
        Account acc2 = bank.openAccount("User2", AccountType.SAVINGS, new BigDecimal("500.00"));
        
        assertNotEquals(acc1.getAccountNumber(), acc2.getAccountNumber());
    }

    @Test
    @DisplayName("Should maintain transaction history integrity")
    void shouldMaintainTransactionHistoryIntegrity() {
        Account account = bank.openAccount("Test User", AccountType.CHECKING, new BigDecimal("1000.00"));

        // Perform various operations
        bank.deposit(account.getAccountNumber(), new BigDecimal("100.00"));
        bank.withdraw(account.getAccountNumber(), new BigDecimal("50.00"));
        
        try {
            bank.withdraw(account.getAccountNumber(), new BigDecimal("2000.00"));
        } catch (InsufficientFundsException e) {
            // Expected
        }

        List<Transaction> history = account.getTransactionHistory();
        
        // Should have 3 transactions: 1 deposit, 1 successful withdrawal, 1 failed withdrawal
        assertEquals(3, history.size());
        
        // Verify types
        assertEquals(TransactionType.DEPOSIT, history.get(0).getType());
        assertEquals(TransactionType.WITHDRAWAL, history.get(1).getType());
        assertEquals(TransactionType.WITHDRAWAL, history.get(2).getType());
        
        // Verify statuses
        assertEquals(TransactionStatus.SUCCESS, history.get(0).getStatus());
        assertEquals(TransactionStatus.SUCCESS, history.get(1).getStatus());
        assertEquals(TransactionStatus.FAILED, history.get(2).getStatus());
    }

    @Test
    @DisplayName("Should handle account filtering correctly")
    void shouldHandleAccountFilteringCorrectly() {
        // Create mix of accounts
        bank.openAccount("John", AccountType.CHECKING, new BigDecimal("1000.00"));
        bank.openAccount("Jane", AccountType.SAVINGS, new BigDecimal("2000.00"));
        bank.openAccount("Bob", AccountType.CHECKING, new BigDecimal("500.00"));
        bank.openAccount("Alice", AccountType.SAVINGS, new BigDecimal("1500.00"));

        List<Account> checkingAccounts = bank.getAccountsByType(AccountType.CHECKING);
        List<Account> savingsAccounts = bank.getAccountsByType(AccountType.SAVINGS);

        assertEquals(2, checkingAccounts.size());
        assertEquals(2, savingsAccounts.size());

        // Verify all CHECKING accounts are actually CHECKING
        for (Account acc : checkingAccounts) {
            assertEquals(AccountType.CHECKING, acc.getAccountType());
        }

        // Verify all SAVINGS accounts are actually SAVINGS
        for (Account acc : savingsAccounts) {
            assertEquals(AccountType.SAVINGS, acc.getAccountType());
        }
    }

    @Test
    @DisplayName("Should throw exception for operations on non-existent account")
    void shouldThrowExceptionForNonExistentAccount() {
        assertThrows(AccountNotFoundException.class, () -> {
            bank.deposit("ACC-999", new BigDecimal("100.00"));
        });

        assertThrows(AccountNotFoundException.class, () -> {
            bank.withdraw("ACC-999", new BigDecimal("100.00"));
        });

        assertThrows(AccountNotFoundException.class, () -> {
            bank.getAccount("ACC-999");
        });
    }

    @Test
    @DisplayName("Should handle edge case of minimum SAVINGS balance")
    void shouldHandleEdgeCaseOfMinimumSavingsBalance() {
        Account savings = bank.openAccount("Test", AccountType.SAVINGS, new BigDecimal("100.00"));

        // Cannot withdraw anything - would violate minimum
        Transaction failedTx = bank.withdraw(savings.getAccountNumber(), new BigDecimal("1.00"));
        assertEquals(TransactionStatus.FAILED, failedTx.getStatus());
        assertTrue(failedTx.getFailureReason().toLowerCase().contains("minimum"));

        // But can deposit
        Transaction depositTx = bank.deposit(savings.getAccountNumber(), new BigDecimal("50.00"));
        assertEquals(TransactionStatus.SUCCESS, depositTx.getStatus());

        // Now can withdraw (keeping minimum)
        Transaction withdrawTx = bank.withdraw(savings.getAccountNumber(), new BigDecimal("50.00"));
        assertEquals(TransactionStatus.SUCCESS, withdrawTx.getStatus());
    }

    @Test
    @DisplayName("Should generate valid account and transaction IDs")
    void shouldGenerateValidIds() {
        Account acc1 = bank.openAccount("User1", AccountType.CHECKING, new BigDecimal("100.00"));
        Account acc2 = bank.openAccount("User2", AccountType.SAVINGS, new BigDecimal("200.00"));

        // Account IDs should follow pattern ACC-XXXXXXXX (8 digits)
        assertTrue(acc1.getAccountNumber().matches("ACC-\\d{8}"));
        assertTrue(acc2.getAccountNumber().matches("ACC-\\d{8}"));

        // Perform deposits and check transaction IDs
        bank.deposit(acc1.getAccountNumber(), new BigDecimal("50.00"));
        Transaction txn = acc1.getTransactionHistory().get(0);
        
        // Transaction IDs should follow pattern TXN-XXXXXXXXXXXX (12 digits)
        assertTrue(txn.getTransactionId().matches("TXN-\\d{12}"));
    }

    @Test
    @DisplayName("Should handle transfers between same account type")
    void shouldHandleTransfersBetweenSameAccountType() {
        Account checking1 = bank.openAccount("User1", AccountType.CHECKING, new BigDecimal("1000.00"));
        Account checking2 = bank.openAccount("User2", AccountType.CHECKING, new BigDecimal("500.00"));

        bank.transfer(checking1.getAccountNumber(), checking2.getAccountNumber(), new BigDecimal("300.00"));

        assertEquals(new BigDecimal("700.00"), checking1.getBalance());
        assertEquals(new BigDecimal("800.00"), checking2.getBalance());
    }
}
