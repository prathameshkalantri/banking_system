package com.prathamesh.banking.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Account class.
 * Tests account creation, business rules, transactions, and edge cases.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
class AccountTest {
    
    // Test constants
    private static final String CHECKING_ACCOUNT = "ACC-00000001";
    private static final String SAVINGS_ACCOUNT = "ACC-00000002";
    private static final String CUSTOMER_NAME = "John Doe";
    
    // ========== Account Creation Tests ==========
    
    @Test
    void testCreateCheckingAccount() {
        // Arrange & Act
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("500.00"));
        
        // Assert
        assertEquals(CHECKING_ACCOUNT, account.getAccountNumber());
        assertEquals(AccountType.CHECKING, account.getAccountType());
        assertEquals(CUSTOMER_NAME, account.getCustomerName());
        assertEquals(new BigDecimal("500.00"), account.getBalance());
        assertEquals(0, account.getMonthlyTransactionCount());
        assertEquals(0, account.getMonthlyWithdrawalCount());
        assertTrue(account.getTransactionHistory().isEmpty());
    }
    
    @Test
    void testCreateSavingsAccountWithMinimumBalance() {
        // Arrange & Act
        Account account = new Account(SAVINGS_ACCOUNT, AccountType.SAVINGS, 
                                     CUSTOMER_NAME, new BigDecimal("100.00"));
        
        // Assert
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }
    
    @Test
    void testCreateSavingsAccountBelowMinimumThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Account(SAVINGS_ACCOUNT, AccountType.SAVINGS, 
                       CUSTOMER_NAME, new BigDecimal("99.99"));
        });
        
        assertTrue(exception.getMessage().contains("minimum initial balance"));
    }
    
    @Test
    void testCreateAccountWithNegativeBalanceThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                       CUSTOMER_NAME, new BigDecimal("-100.00"));
        });
    }
    
    @Test
    void testCreateAccountWithNullAccountNumberThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new Account(null, AccountType.CHECKING, 
                       CUSTOMER_NAME, new BigDecimal("500.00"));
        });
    }
    
    @Test
    void testCreateAccountWithNullAccountTypeThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new Account(CHECKING_ACCOUNT, null, 
                       CUSTOMER_NAME, new BigDecimal("500.00"));
        });
    }
    
    @Test
    void testCreateAccountWithEmptyCustomerNameThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                       "   ", new BigDecimal("500.00"));
        });
    }
    
    // ========== Deposit Tests ==========
    
    @Test
    void testDeposit() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("500.00"));
        
        // Act
        BigDecimal newBalance = account.deposit(new BigDecimal("100.00"));
        
        // Assert
        assertEquals(new BigDecimal("600.00"), newBalance);
        assertEquals(new BigDecimal("600.00"), account.getBalance());
        assertEquals(1, account.getMonthlyTransactionCount());
    }
    
    @Test
    void testDepositZeroAmountThrowsException() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("500.00"));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            account.deposit(BigDecimal.ZERO);
        });
    }
    
    @Test
    void testDepositNegativeAmountThrowsException() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("500.00"));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            account.deposit(new BigDecimal("-100.00"));
        });
    }
    
    // ========== Withdrawal Tests ==========
    
    @Test
    void testWithdrawalFromCheckingAccount() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("500.00"));
        
        // Act
        BigDecimal newBalance = account.withdraw(new BigDecimal("100.00"));
        
        // Assert
        assertEquals(new BigDecimal("400.00"), newBalance);
        assertEquals(new BigDecimal("400.00"), account.getBalance());
        assertEquals(1, account.getMonthlyTransactionCount());
        assertEquals(0, account.getMonthlyWithdrawalCount()); // Only for SAVINGS
    }
    
    @Test
    void testWithdrawalFromSavingsAccount() {
        // Arrange
        Account account = new Account(SAVINGS_ACCOUNT, AccountType.SAVINGS, 
                                     CUSTOMER_NAME, new BigDecimal("500.00"));
        
        // Act
        account.withdraw(new BigDecimal("100.00"));
        
        // Assert
        assertEquals(new BigDecimal("400.00"), account.getBalance());
        assertEquals(1, account.getMonthlyTransactionCount());
        assertEquals(1, account.getMonthlyWithdrawalCount());
    }
    
    @Test
    void testCanWithdrawWithSufficientFunds() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("500.00"));
        
        // Act
        String result = account.canWithdraw(new BigDecimal("100.00"));
        
        // Assert
        assertNull(result, "Should allow withdrawal with sufficient funds");
    }
    
    @Test
    void testCannotWithdrawWithInsufficientFunds() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("50.00"));
        
        // Act
        String result = account.canWithdraw(new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Insufficient funds"));
    }
    
    @Test
    void testSavingsAccountMaintainsMinimumBalance() {
        // Arrange
        Account account = new Account(SAVINGS_ACCOUNT, AccountType.SAVINGS, 
                                     CUSTOMER_NAME, new BigDecimal("150.00"));
        
        // Act - Try to withdraw too much
        String result = account.canWithdraw(new BigDecimal("51.00"));
        
        // Assert - Should fail (would leave only $99)
        assertNotNull(result);
        assertTrue(result.contains("minimum balance"));
    }
    
    @Test
    void testSavingsAccountWithdrawalLimit() {
        // Arrange
        Account account = new Account(SAVINGS_ACCOUNT, AccountType.SAVINGS, 
                                     CUSTOMER_NAME, new BigDecimal("1000.00"));
        
        // Act - Make 5 withdrawals
        for (int i = 0; i < 5; i++) {
            assertNull(account.canWithdraw(new BigDecimal("10.00")));
            account.withdraw(new BigDecimal("10.00"));
        }
        
        // Act - Try 6th withdrawal
        String result = account.canWithdraw(new BigDecimal("10.00"));
        
        // Assert - Should fail
        assertNotNull(result);
        assertTrue(result.contains("limited to 5 withdrawals"));
        assertEquals(5, account.getMonthlyWithdrawalCount());
    }
    
    // ========== Fee and Interest Tests ==========
    
    @Test
    void testCheckingAccountNoFeeUnder10Transactions() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("1000.00"));
        
        // Act - Make 10 transactions
        for (int i = 0; i < 10; i++) {
            account.deposit(new BigDecimal("10.00"));
        }
        BigDecimal fee = account.applyMonthlyFee();
        
        // Assert
        assertEquals(BigDecimal.ZERO, fee);
        assertEquals(new BigDecimal("1100.00"), account.getBalance()); // 1000 + (10 * 10)
    }
    
    @Test
    void testCheckingAccountFeeAfter10Transactions() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("1000.00"));
        
        // Act - Make 12 transactions
        for (int i = 0; i < 12; i++) {
            account.deposit(new BigDecimal("10.00"));
        }
        BigDecimal fee = account.applyMonthlyFee();
        
        // Assert - 2 transactions charged at $2.50 each = $5.00
        assertEquals(new BigDecimal("5.00"), fee);
        assertEquals(new BigDecimal("1115.00"), account.getBalance()); // 1000 + 120 - 5
    }
    
    @Test
    void testSavingsAccountNoFeeApplied() {
        // Arrange
        Account account = new Account(SAVINGS_ACCOUNT, AccountType.SAVINGS, 
                                     CUSTOMER_NAME, new BigDecimal("1000.00"));
        
        // Act - Make transactions
        for (int i = 0; i < 20; i++) {
            account.deposit(new BigDecimal("10.00"));
        }
        BigDecimal fee = account.applyMonthlyFee();
        
        // Assert
        assertEquals(BigDecimal.ZERO, fee);
    }
    
    @Test
    void testSavingsAccountInterestApplication() {
        // Arrange
        Account account = new Account(SAVINGS_ACCOUNT, AccountType.SAVINGS, 
                                     CUSTOMER_NAME, new BigDecimal("1000.00"));
        
        // Act
        BigDecimal interest = account.applyMonthlyInterest();
        
        // Assert - 2% of 1000 = 20.00
        assertEquals(new BigDecimal("20.00"), interest);
        assertEquals(new BigDecimal("1020.00"), account.getBalance());
    }
    
    @Test
    void testCheckingAccountNoInterest() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("1000.00"));
        
        // Act
        BigDecimal interest = account.applyMonthlyInterest();
        
        // Assert
        assertEquals(BigDecimal.ZERO, interest);
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }
    
    // ========== Monthly Counter Tests ==========
    
    @Test
    void testResetMonthlyCounters() {
        // Arrange
        Account account = new Account(SAVINGS_ACCOUNT, AccountType.SAVINGS, 
                                     CUSTOMER_NAME, new BigDecimal("1000.00"));
        
        // Act - Make transactions
        account.deposit(new BigDecimal("100.00"));
        account.withdraw(new BigDecimal("50.00"));
        account.resetMonthlyCounters();
        
        // Assert
        assertEquals(0, account.getMonthlyTransactionCount());
        assertEquals(0, account.getMonthlyWithdrawalCount());
    }
    
    // ========== Transaction History Tests ==========
    
    @Test
    void testAddTransaction() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("500.00"));
        
        Transaction transaction = new Transaction.Builder()
                .transactionId("TXN-001")
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("600.00"))
                .build();
        
        // Act
        account.addTransaction(transaction);
        
        // Assert
        assertEquals(1, account.getTransactionHistory().size());
        assertEquals(transaction, account.getTransactionHistory().get(0));
    }
    
    @Test
    void testTransactionHistoryIsUnmodifiable() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("500.00"));
        
        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            account.getTransactionHistory().add(new Transaction.Builder()
                    .transactionId("TXN-001")
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("100.00"))
                    .balanceBefore(new BigDecimal("500.00"))
                    .balanceAfter(new BigDecimal("600.00"))
                    .build());
        });
    }
    
    // ========== Account Closure Tests ==========
    
    @Test
    void testCanCloseAccountWithZeroBalance() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, BigDecimal.ZERO);
        
        // Act & Assert
        assertTrue(account.canClose());
    }
    
    @Test
    void testCannotCloseAccountWithBalance() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("100.00"));
        
        // Act & Assert
        assertFalse(account.canClose());
    }
    
    // ========== Equality Tests ==========
    
    @Test
    void testAccountEquality() {
        // Arrange
        Account account1 = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                      "Customer 1", new BigDecimal("500.00"));
        Account account2 = new Account(CHECKING_ACCOUNT, AccountType.SAVINGS, 
                                      "Customer 2", new BigDecimal("1000.00"));
        
        // Assert - Equality based on account number
        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
    }
    
    @Test
    void testAccountInequality() {
        // Arrange
        Account account1 = new Account("ACC-00000001", AccountType.CHECKING, 
                                      CUSTOMER_NAME, new BigDecimal("500.00"));
        Account account2 = new Account("ACC-00000002", AccountType.CHECKING, 
                                      CUSTOMER_NAME, new BigDecimal("500.00"));
        
        // Assert
        assertNotEquals(account1, account2);
    }
    
    // ========== ToString Test ==========
    
    @Test
    void testToString() {
        // Arrange
        Account account = new Account(CHECKING_ACCOUNT, AccountType.CHECKING, 
                                     CUSTOMER_NAME, new BigDecimal("500.00"));
        
        // Act
        String result = account.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains(CHECKING_ACCOUNT));
        assertTrue(result.contains("CHECKING"));
        assertTrue(result.contains(CUSTOMER_NAME));
        assertTrue(result.contains("500.00"));
    }
}
