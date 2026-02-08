package com.prathamesh.banking.service;

import com.prathamesh.banking.model.Account;
import com.prathamesh.banking.model.AccountType;
import com.prathamesh.banking.model.Transaction;
import com.prathamesh.banking.model.TransactionStatus;
import com.prathamesh.banking.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Bank service.
 * Tests all core banking operations including account management,
 * transactions, monthly processing, and statement generation.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
class BankTest {
    
    private Bank bank;
    
    @BeforeEach
    void setUp() {
        bank = new Bank();
    }
    
    // ========== Account Opening Tests ==========
    
    @Test
    void testOpenAccount_CheckingAccount_ZeroDeposit() {
        // Act
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        
        // Assert
        assertNotNull(account);
        assertEquals(AccountType.CHECKING, account.getAccountType());
        assertEquals("John Doe", account.getCustomerName());
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));
        assertTrue(account.getAccountNumber().startsWith("ACC-"));
    }
    
    @Test
    void testOpenAccount_CheckingAccount_WithInitialDeposit() {
        // Act
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("1000.00"));
        
        // Assert
        assertNotNull(account);
        assertEquals(0, account.getBalance().compareTo(new BigDecimal("1000.00")));
    }
    
    @Test
    void testOpenAccount_SavingsAccount_ValidDeposit() {
        // Act
        Account account = bank.openAccount("Jane Smith", AccountType.SAVINGS, new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(account);
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals("Jane Smith", account.getCustomerName());
        assertEquals(0, account.getBalance().compareTo(new BigDecimal("100.00")));
    }
    
    @Test
    void testOpenAccount_SavingsAccount_BelowMinimum() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.openAccount("Jane Smith", AccountType.SAVINGS, new BigDecimal("99.99"));
        });
        assertTrue(exception.getMessage().contains("minimum initial deposit"));
    }
    
    @Test
    void testOpenAccount_NullAccountType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.openAccount("John Doe", null, BigDecimal.ZERO);
        });
        assertTrue(exception.getMessage().contains("Account type cannot be null"));
    }
    
    @Test
    void testOpenAccount_InvalidCustomerName() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.openAccount("A", AccountType.CHECKING, BigDecimal.ZERO);
        });
        assertTrue(exception.getMessage().contains("at least 2 characters"));
    }
    
    @Test
    void testOpenAccount_UniqueAccountNumbers() {
        // Act
        Account account1 = bank.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        Account account2 = bank.openAccount("Jane Smith", AccountType.CHECKING, BigDecimal.ZERO);
        
        // Assert
        assertNotEquals(account1.getAccountNumber(), account2.getAccountNumber());
    }
    
    // ========== Account Retrieval Tests ==========
    
    @Test
    void testGetAccount_ExistingAccount() {
        // Arrange
        Account created = bank.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        
        // Act
        Account retrieved = bank.getAccount(created.getAccountNumber());
        
        // Assert
        assertNotNull(retrieved);
        assertEquals(created.getAccountNumber(), retrieved.getAccountNumber());
    }
    
    @Test
    void testGetAccount_NonExistentAccount() {
        // Act
        Account retrieved = bank.getAccount("ACC-999999");
        
        // Assert
        assertNull(retrieved);
    }
    
    // ========== Account Closure Tests ==========
    
    @Test
    void testCloseAccount_ZeroBalance() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        
        // Act
        boolean result = bank.closeAccount(account.getAccountNumber());
        
        // Assert
        assertTrue(result);
        assertNull(bank.getAccount(account.getAccountNumber()));
    }
    
    @Test
    void testCloseAccount_NonZeroBalance() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("100.00"));
        
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bank.closeAccount(account.getAccountNumber());
        });
        assertTrue(exception.getMessage().contains("cannot be closed"));
    }
    
    @Test
    void testCloseAccount_NonExistentAccount() {
        // Act
        boolean result = bank.closeAccount("ACC-999999");
        
        // Assert
        assertFalse(result);
    }
    
    // ========== Deposit Tests ==========
    
    @Test
    void testDeposit_ValidDeposit() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        
        // Act
        Transaction transaction = bank.deposit(account.getAccountNumber(), new BigDecimal("500.00"));
        
        // Assert
        assertNotNull(transaction);
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(0, transaction.getAmount().compareTo(new BigDecimal("500.00")));
        
        // Verify balance updated
        Account updated = bank.getAccount(account.getAccountNumber());
        assertEquals(0, updated.getBalance().compareTo(new BigDecimal("500.00")));
    }
    
    @Test
    void testDeposit_NonExistentAccount() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.deposit("ACC-999999", new BigDecimal("500.00"));
        });
        assertTrue(exception.getMessage().contains("Account not found"));
    }
    
    @Test
    void testDeposit_ZeroAmount() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        
        // Act & Assert - Zero amount validation fails at Transaction Builder level
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bank.deposit(account.getAccountNumber(), BigDecimal.ZERO);
        });
        assertTrue(exception.getMessage().contains("Transaction amount must be positive"));
    }
    
    // ========== Withdrawal Tests ==========
    
    @Test
    void testWithdraw_ValidWithdrawal() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("500.00"));
        
        // Act
        Transaction transaction = bank.withdraw(account.getAccountNumber(), new BigDecimal("200.00"));
        
        // Assert
        assertNotNull(transaction);
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
        assertEquals(0, transaction.getAmount().compareTo(new BigDecimal("200.00")));
        
        // Verify balance updated
        Account updated = bank.getAccount(account.getAccountNumber());
        assertEquals(0, updated.getBalance().compareTo(new BigDecimal("300.00")));
    }
    
    @Test
    void testWithdraw_InsufficientFunds() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("50.00"));
        
        // Act
        Transaction transaction = bank.withdraw(account.getAccountNumber(), new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(transaction);
        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
        assertTrue(transaction.getFailureReason().contains("Insufficient funds"));
        
        // Verify balance unchanged
        Account updated = bank.getAccount(account.getAccountNumber());
        assertEquals(0, updated.getBalance().compareTo(new BigDecimal("50.00")));
    }
    
    @Test
    void testWithdraw_SavingsAccount_WithinLimit() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.SAVINGS, new BigDecimal("500.00"));
        
        // Act - Make 5 withdrawals (at the limit)
        for (int i = 0; i < 5; i++) {
            Transaction tx = bank.withdraw(account.getAccountNumber(), new BigDecimal("10.00"));
            assertEquals(TransactionStatus.SUCCESS, tx.getStatus());
        }
        
        // Assert
        Account updated = bank.getAccount(account.getAccountNumber());
        assertEquals(0, updated.getBalance().compareTo(new BigDecimal("450.00")));
        assertEquals(5, updated.getMonthlyWithdrawalCount());
    }
    
    @Test
    void testWithdraw_SavingsAccount_ExceedsLimit() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.SAVINGS, new BigDecimal("500.00"));
        
        // Make 5 successful withdrawals
        for (int i = 0; i < 5; i++) {
            bank.withdraw(account.getAccountNumber(), new BigDecimal("10.00"));
        }
        
        // Act - Try 6th withdrawal
        Transaction failedTx = bank.withdraw(account.getAccountNumber(), new BigDecimal("10.00"));
        
        // Assert
        assertEquals(TransactionStatus.FAILED, failedTx.getStatus());
        assertTrue(failedTx.getFailureReason().contains("limited to 5 withdrawals"));
        
        // Verify balance unchanged from 6th attempt
        Account updated = bank.getAccount(account.getAccountNumber());
        assertEquals(0, updated.getBalance().compareTo(new BigDecimal("450.00")));
    }
    
    // ========== Transfer Tests ==========
    
    @Test
    void testTransfer_ValidTransfer() {
        // Arrange
        Account from = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("500.00"));
        Account to = bank.openAccount("Jane Smith", AccountType.CHECKING, new BigDecimal("200.00"));
        
        // Act
        Transaction[] transactions = bank.transfer(from.getAccountNumber(), 
                                                  to.getAccountNumber(), 
                                                  new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(transactions);
        assertEquals(2, transactions.length);
        assertEquals(TransactionStatus.SUCCESS, transactions[0].getStatus());
        assertEquals(TransactionType.TRANSFER, transactions[0].getType());
        assertEquals(0, transactions[0].getAmount().compareTo(new BigDecimal("100.00")));
        assertEquals(TransactionStatus.SUCCESS, transactions[1].getStatus());
        
        // Verify balances updated
        Account fromUpdated = bank.getAccount(from.getAccountNumber());
        Account toUpdated = bank.getAccount(to.getAccountNumber());
        assertEquals(0, fromUpdated.getBalance().compareTo(new BigDecimal("400.00")));
        assertEquals(0, toUpdated.getBalance().compareTo(new BigDecimal("300.00")));
    }
    
    @Test
    void testTransfer_SameAccount() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("500.00"));
        
        // Act
        Transaction[] transactions = bank.transfer(account.getAccountNumber(), 
                                                  account.getAccountNumber(), 
                                                  new BigDecimal("100.00"));
        
        // Assert - Should return failed transaction, not throw exception
        assertNotNull(transactions);
        assertEquals(TransactionStatus.FAILED, transactions[0].getStatus());
        assertTrue(transactions[0].getFailureReason().contains("Cannot transfer to the same account"));
    }
    
    @Test
    void testTransfer_InsufficientFunds() {
        // Arrange
        Account from = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("50.00"));
        Account to = bank.openAccount("Jane Smith", AccountType.CHECKING, new BigDecimal("200.00"));
        
        // Act
        Transaction[] transactions = bank.transfer(from.getAccountNumber(), 
                                                  to.getAccountNumber(), 
                                                  new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(transactions);
        assertEquals(TransactionStatus.FAILED, transactions[0].getStatus());
        assertTrue(transactions[0].getFailureReason().contains("Insufficient funds"));
        
        // Verify balances unchanged
        Account fromUpdated = bank.getAccount(from.getAccountNumber());
        Account toUpdated = bank.getAccount(to.getAccountNumber());
        assertEquals(0, fromUpdated.getBalance().compareTo(new BigDecimal("50.00")));
        assertEquals(0, toUpdated.getBalance().compareTo(new BigDecimal("200.00")));
    }
    
    @Test
    void testTransfer_FromNonExistentAccount() {
        // Arrange
        Account to = bank.openAccount("Jane Smith", AccountType.CHECKING, new BigDecimal("200.00"));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.transfer("ACC-999999", to.getAccountNumber(), new BigDecimal("100.00"));
        });
        assertTrue(exception.getMessage().contains("Account not found"));
    }
    
    @Test
    void testTransfer_ToNonExistentAccount() {
        // Arrange
        Account from = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("500.00"));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.transfer(from.getAccountNumber(), "ACC-999999", new BigDecimal("100.00"));
        });
        assertTrue(exception.getMessage().contains("Account not found"));
    }
    
    // ========== Transaction History Tests ==========
    
    @Test
    void testGetTransactionHistory_AllTransactions() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("500.00"));
        bank.deposit(account.getAccountNumber(), new BigDecimal("100.00"));
        bank.withdraw(account.getAccountNumber(), new BigDecimal("50.00"));
        
        // Act
        List<Transaction> history = bank.getTransactionHistory(account.getAccountNumber());
        
        // Assert - Should have 3: initial deposit (500), deposit (100), withdrawal (50)
        assertEquals(3, history.size());
        assertEquals(TransactionType.DEPOSIT, history.get(0).getType());
        assertEquals(TransactionType.DEPOSIT, history.get(1).getType());
        assertEquals(TransactionType.WITHDRAWAL, history.get(2).getType());
    }
    
    @Test
    void testGetTransactionHistory_WithDateFilter() {
        // Arrange
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("500.00"));
        bank.deposit(account.getAccountNumber(), new BigDecimal("100.00"));
        
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        // Act
        List<Transaction> history = bank.getTransactionHistory(account.getAccountNumber(), today, tomorrow);
        
        // Assert - Should have 2: initial deposit (500) + deposit (100)
        assertEquals(2, history.size());
    }
    
    @Test
    void testGetTransactionHistory_NonExistentAccount() {
        // Act & Assert - Should throw exception, not return empty list
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.getTransactionHistory("ACC-999999");
        });
        assertTrue(exception.getMessage().contains("Account not found"));
    }
    
    // ========== Monthly Interest Tests ==========
    
    @Test
    void testApplyMonthlyInterest_SavingsAccounts() {
        // Arrange
        Account savings1 = bank.openAccount("John Doe", AccountType.SAVINGS, new BigDecimal("1000.00"));
        Account savings2 = bank.openAccount("Jane Smith", AccountType.SAVINGS, new BigDecimal("2000.00"));
        Account checking = bank.openAccount("Bob Johnson", AccountType.CHECKING, new BigDecimal("1000.00"));
        
        // Act
        bank.applyMonthlyInterest();
        
        // Assert
        Account s1Updated = bank.getAccount(savings1.getAccountNumber());
        Account s2Updated = bank.getAccount(savings2.getAccountNumber());
        Account checkingUpdated = bank.getAccount(checking.getAccountNumber());
        
        // Savings accounts should have interest: 1000 * 0.02 = 20, total = 1020
        assertEquals(0, s1Updated.getBalance().compareTo(new BigDecimal("1020.00")));
        assertEquals(0, s2Updated.getBalance().compareTo(new BigDecimal("2040.00")));
        
        // Checking account should remain unchanged
        assertEquals(0, checkingUpdated.getBalance().compareTo(new BigDecimal("1000.00")));
    }
    
    // ========== Monthly Fees and Reset Tests ==========
    
    @Test
    void testApplyMonthlyFeesAndResetCounters_CheckingAccount() {
        // Arrange
        Account checking = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("1000.00"));
        
        // Make 12 transactions (10 free + 2 with fees)
        for (int i = 0; i < 12; i++) {
            bank.deposit(checking.getAccountNumber(), new BigDecimal("10.00"));
        }
        
        // Act
        bank.applyMonthlyFeesAndResetCounters();
        
        // Assert
        Account updated = bank.getAccount(checking.getAccountNumber());
        // Balance: 1000 + (12 * 10) = 1120, then fees: 2 * 2.50 = 5.00 deducted
        // Total: 1120 - 5 = 1115
        assertEquals(0, updated.getBalance().compareTo(new BigDecimal("1115.00")));
        assertEquals(0, updated.getMonthlyTransactionCount());
    }
    
    @Test
    void testApplyMonthlyFeesAndResetCounters_SavingsAccount() {
        // Arrange
        Account savings = bank.openAccount("Jane Smith", AccountType.SAVINGS, new BigDecimal("1000.00"));
        
        // Make 3 withdrawals
        for (int i = 0; i < 3; i++) {
            bank.withdraw(savings.getAccountNumber(), new BigDecimal("10.00"));
        }
        
        // Act
        bank.applyMonthlyFeesAndResetCounters();
        
        // Assert
        Account updated = bank.getAccount(savings.getAccountNumber());
        // No fees for savings accounts, just counter reset
        assertEquals(0, updated.getBalance().compareTo(new BigDecimal("970.00"))); // 1000 - 30
        assertEquals(0, updated.getMonthlyWithdrawalCount());
    }
    
    // ========== Monthly Statement Tests ==========
    
    @Test
    void testGenerateMonthlyStatement() {
        // Arrange - Start with zero balance to avoid initial deposit transaction
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        bank.deposit(account.getAccountNumber(), new BigDecimal("1000.00"));
        bank.deposit(account.getAccountNumber(), new BigDecimal("500.00"));
        bank.withdraw(account.getAccountNumber(), new BigDecimal("200.00"));
        
        // Act
        String statement = bank.generateMonthlyStatement(account.getAccountNumber());
        
        // Assert
        assertNotNull(statement);
        assertFalse(statement.isEmpty());
        assertTrue(statement.contains("MONTHLY"));
        assertTrue(statement.contains("STATEMENT"));
        assertTrue(statement.contains("John Doe"));
        assertTrue(statement.contains(account.getAccountNumber()));
        
        // Verify final balance
        Account updatedAccount = bank.getAccount(account.getAccountNumber());
        assertEquals(0, updatedAccount.getBalance().compareTo(new BigDecimal("1300.00")));
    }
    
    @Test
    void testGenerateMonthlyStatement_NonExistentAccount() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bank.generateMonthlyStatement("ACC-999999");
        });
        assertTrue(exception.getMessage().contains("Account not found"));
    }
    
    // ========== All Accounts Test ==========
    
    @Test
    void testGetAllAccounts() {
        // Arrange
        Account account1 = bank.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        Account account2 = bank.openAccount("Jane Smith", AccountType.SAVINGS, new BigDecimal("100.00"));
        Account account3 = bank.openAccount("Bob Johnson", AccountType.CHECKING, new BigDecimal("500.00"));
        
        // Act
        Collection<Account> allAccounts = bank.getAllAccounts();
        
        // Assert
        assertEquals(3, allAccounts.size());
        assertTrue(allAccounts.stream().anyMatch(a -> a.getAccountNumber().equals(account1.getAccountNumber())));
        assertTrue(allAccounts.stream().anyMatch(a -> a.getAccountNumber().equals(account2.getAccountNumber())));
        assertTrue(allAccounts.stream().anyMatch(a -> a.getAccountNumber().equals(account3.getAccountNumber())));
    }
}
