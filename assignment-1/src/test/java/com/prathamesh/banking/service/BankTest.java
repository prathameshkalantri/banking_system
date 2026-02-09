package com.prathamesh.banking.service;

import com.prathamesh.banking.domain.*;
import com.prathamesh.banking.exception.*;
import com.prathamesh.banking.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Bank service layer.
 * Tests all required operations and edge cases.
 */
@DisplayName("Bank Service Tests")
class BankTest {
    
    private Bank bank;
    
    @BeforeEach
    void setUp() {
        bank = new Bank();
        IdGenerator.resetCounters();
    }
    
    // ==================== OPEN ACCOUNT TESTS ====================
    
    @Test
    @DisplayName("Open CHECKING account with valid initial deposit")
    void testOpenCheckingAccount() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("500.00"));
        
        assertNotNull(account);
        assertEquals("John Doe", account.getCustomerName());
        assertEquals(AccountType.CHECKING, account.getAccountType());
        assertEquals(new BigDecimal("500.00"), account.getBalance());
        assertTrue(account.getAccountNumber().startsWith("ACC-"));
    }
    
    @Test
    @DisplayName("Open SAVINGS account with valid initial deposit >= $100")
    void testOpenSavingsAccount() {
        Account account = bank.openAccount("Jane Smith", AccountType.SAVINGS, new BigDecimal("1000.00"));
        
        assertNotNull(account);
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
    }
    
    @Test
    @DisplayName("Open SAVINGS account with insufficient initial deposit")
    void testOpenSavingsAccountInsufficientDeposit() {
        assertThrows(MinimumBalanceViolationException.class,
            () -> bank.openAccount("Bob Jones", AccountType.SAVINGS, new BigDecimal("50.00")),
            "SAVINGS requires $100 minimum");
    }
    
    @Test
    @DisplayName("Open account with null customer name")
    void testOpenAccountNullName() {
        assertThrows(IllegalArgumentException.class,
            () -> bank.openAccount(null, AccountType.CHECKING, new BigDecimal("100.00")));
    }
    
    @Test
    @DisplayName("Open account with empty customer name")
    void testOpenAccountEmptyName() {
        assertThrows(IllegalArgumentException.class,
            () -> bank.openAccount("   ", AccountType.CHECKING, new BigDecimal("100.00")));
    }
    
    @Test
    @DisplayName("Open account with null account type")
    void testOpenAccountNullType() {
        assertThrows(IllegalArgumentException.class,
            () -> bank.openAccount("John Doe", null, new BigDecimal("100.00")));
    }
    
    @Test
    @DisplayName("Open account with negative initial deposit")
    void testOpenAccountNegativeDeposit() {
        assertThrows(IllegalArgumentException.class,
            () -> bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("-100.00")));
    }
    
    // ==================== CLOSE ACCOUNT TESTS ====================
    
    @Test
    @DisplayName("Close account with zero balance")
    void testCloseAccountZeroBalance() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        
        assertDoesNotThrow(() -> bank.closeAccount(account.getAccountNumber()));
        assertFalse(bank.accountExists(account.getAccountNumber()));
    }
    
    @Test
    @DisplayName("Close account with non-zero balance")
    void testCloseAccountNonZeroBalance() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("100.00"));
        
        assertThrows(InvalidOperationException.class,
            () -> bank.closeAccount(account.getAccountNumber()),
            "Cannot close account with non-zero balance");
    }
    
    @Test
    @DisplayName("Close non-existent account")
    void testCloseNonExistentAccount() {
        assertThrows(AccountNotFoundException.class,
            () -> bank.closeAccount("ACC-99999999"));
    }
    
    // ==================== DEPOSIT TESTS ====================
    
    @Test
    @DisplayName("Deposit valid amount into existing account")
    void testDeposit() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("100.00"));
        
        Transaction tx = bank.deposit(account.getAccountNumber(), new BigDecimal("50.00"));
        
        assertNotNull(tx);
        assertEquals(TransactionType.DEPOSIT, tx.getType());
        assertEquals(TransactionStatus.SUCCESS, tx.getStatus());
        assertEquals(new BigDecimal("150.00"), account.getBalance());
    }
    
    @Test
    @DisplayName("Deposit into non-existent account")
    void testDepositNonExistentAccount() {
        assertThrows(AccountNotFoundException.class,
            () -> bank.deposit("ACC-99999999", new BigDecimal("100.00")));
    }
    
    @Test
    @DisplayName("Deposit negative amount")
    void testDepositNegativeAmount() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("100.00"));
        
        assertThrows(IllegalArgumentException.class,
            () -> bank.deposit(account.getAccountNumber(), new BigDecimal("-50.00")));
    }
    
    @Test
    @DisplayName("Deposit zero amount")
    void testDepositZeroAmount() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("100.00"));
        
        assertThrows(IllegalArgumentException.class,
            () -> bank.deposit(account.getAccountNumber(), BigDecimal.ZERO));
    }
    
    // ==================== WITHDRAW TESTS ====================
    
    @Test
    @DisplayName("Withdraw valid amount from CHECKING account")
    void testWithdrawChecking() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("500.00"));
        
        Transaction tx = bank.withdraw(account.getAccountNumber(), new BigDecimal("200.00"));
        
        assertNotNull(tx);
        assertEquals(TransactionType.WITHDRAWAL, tx.getType());
        assertEquals(TransactionStatus.SUCCESS, tx.getStatus());
        assertEquals(new BigDecimal("300.00"), account.getBalance());
    }
    
    @Test
    @DisplayName("Withdraw from SAVINGS account - success")
    void testWithdrawSavingsSuccess() {
        Account account = bank.openAccount("Jane Smith", AccountType.SAVINGS, new BigDecimal("500.00"));
        
        Transaction tx = bank.withdraw(account.getAccountNumber(), new BigDecimal("100.00"));
        
        assertTrue(tx.isSuccessful());
        assertEquals(new BigDecimal("400.00"), account.getBalance());
    }
    
    @Test
    @DisplayName("Withdraw from SAVINGS violating minimum balance - records failed transaction")
    void testWithdrawSavingsMinimumBalance() {
        Account account = bank.openAccount("Jane Smith", AccountType.SAVINGS, new BigDecimal("200.00"));
        
        Transaction tx = bank.withdraw(account.getAccountNumber(), new BigDecimal("150.00"));
        
        // Should create FAILED transaction
        assertTrue(tx.isFailed());
        assertEquals(TransactionStatus.FAILED, tx.getStatus());
        assertNotNull(tx.getFailureReason());
        
        // Balance should remain unchanged
        assertEquals(new BigDecimal("200.00"), account.getBalance());
    }
    
    @Test
    @DisplayName("Withdraw exceeding SAVINGS withdrawal limit - records failed transaction")
    void testWithdrawSavingsLimit() {
        Account account = bank.openAccount("Bob Jones", AccountType.SAVINGS, new BigDecimal("1000.00"));
        
        // Perform 5 successful withdrawals
        for (int i = 0; i < 5; i++) {
            Transaction tx = bank.withdraw(account.getAccountNumber(), new BigDecimal("50.00"));
            assertTrue(tx.isSuccessful(), "Withdrawal " + (i + 1) + " should succeed");
        }
        
        // 6th withdrawal should fail
        Transaction failedTx = bank.withdraw(account.getAccountNumber(), new BigDecimal("50.00"));
        assertTrue(failedTx.isFailed());
        assertTrue(failedTx.getFailureReason().contains("limit"));
    }
    
    @Test
    @DisplayName("Withdraw with insufficient funds - records failed transaction")
    void testWithdrawInsufficientFunds() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("50.00"));
        
        Transaction tx = bank.withdraw(account.getAccountNumber(), new BigDecimal("100.00"));
        
        assertTrue(tx.isFailed());
        assertEquals(new BigDecimal("50.00"), account.getBalance()); // Unchanged
    }
    
    // ==================== TRANSFER TESTS (ATOMIC) ====================
    
    @Test
    @DisplayName("Transfer valid amount between accounts - success")
    void testTransferSuccess() {
        Account fromAccount = bank.openAccount("Alice", AccountType.CHECKING, new BigDecimal("500.00"));
        Account toAccount = bank.openAccount("Bob", AccountType.CHECKING, new BigDecimal("100.00"));
        
        Transaction tx = bank.transfer(
            fromAccount.getAccountNumber(),
            toAccount.getAccountNumber(),
            new BigDecimal("200.00")
        );
        
        assertTrue(tx.isSuccessful());
        assertEquals(TransactionType.TRANSFER, tx.getType());
        assertEquals(new BigDecimal("300.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("300.00"), toAccount.getBalance());
    }
    
    @Test
    @DisplayName("Transfer to same account - should fail")
    void testTransferSameAccount() {
        Account account = bank.openAccount("Alice", AccountType.CHECKING, new BigDecimal("500.00"));
        
        assertThrows(IllegalArgumentException.class,
            () -> bank.transfer(account.getAccountNumber(), account.getAccountNumber(), new BigDecimal("100.00")));
    }
    
    @Test
    @DisplayName("Transfer with insufficient funds - records failed transaction")
    void testTransferInsufficientFunds() {
        Account fromAccount = bank.openAccount("Alice", AccountType.CHECKING, new BigDecimal("50.00"));
        Account toAccount = bank.openAccount("Bob", AccountType.CHECKING, new BigDecimal("100.00"));
        
        Transaction tx = bank.transfer(
            fromAccount.getAccountNumber(),
            toAccount.getAccountNumber(),
            new BigDecimal("100.00")
        );
        
        assertTrue(tx.isFailed());
        // Balances should remain unchanged
        assertEquals(new BigDecimal("50.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("100.00"), toAccount.getBalance());
    }
    
    @Test
    @DisplayName("Transfer from SAVINGS violating minimum balance - records failed transaction")
    void testTransferSavingsMinimumBalance() {
        Account fromAccount = bank.openAccount("Alice", AccountType.SAVINGS, new BigDecimal("200.00"));
        Account toAccount = bank.openAccount("Bob", AccountType.CHECKING, new BigDecimal("100.00"));
        
        Transaction tx = bank.transfer(
            fromAccount.getAccountNumber(),
            toAccount.getAccountNumber(),
            new BigDecimal("150.00")
        );
        
        assertTrue(tx.isFailed());
        assertEquals(new BigDecimal("200.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("100.00"), toAccount.getBalance());
    }
    
    @Test
    @DisplayName("Transfer does NOT count toward SAVINGS withdrawal limit")
    void testTransferNotCountedAsWithdrawal() {
        Account fromAccount = bank.openAccount("Alice", AccountType.SAVINGS, new BigDecimal("1000.00"));
        Account toAccount = bank.openAccount("Bob", AccountType.CHECKING, new BigDecimal("100.00"));
        
        // Perform 6 transfers (more than withdrawal limit)
        for (int i = 0; i < 6; i++) {
            Transaction tx = bank.transfer(
                fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                new BigDecimal("50.00")
            );
            assertTrue(tx.isSuccessful(), "Transfer " + (i + 1) + " should succeed");
        }
        
        // Verify withdrawal counter was NOT incremented
        assertEquals(0, fromAccount.getMonthlyWithdrawalCount(), "Transfers should not count as withdrawals");
        
        // But transaction counter WAS incremented
        assertEquals(6, fromAccount.getMonthlyTransactionCount(), "Transfers should count as transactions");
    }
    
    @Test
    @DisplayName("Transfer from non-existent account")
    void testTransferFromNonExistent() {
        Account toAccount = bank.openAccount("Bob", AccountType.CHECKING, new BigDecimal("100.00"));
        
        assertThrows(AccountNotFoundException.class,
            () -> bank.transfer("ACC-99999999", toAccount.getAccountNumber(), new BigDecimal("100.00")));
    }
    
    @Test
    @DisplayName("Transfer to non-existent account")
    void testTransferToNonExistent() {
        Account fromAccount = bank.openAccount("Alice", AccountType.CHECKING, new BigDecimal("500.00"));
        
        assertThrows(AccountNotFoundException.class,
            () -> bank.transfer(fromAccount.getAccountNumber(), "ACC-99999999", new BigDecimal("100.00")));
    }
    
    // ==================== TRANSACTION HISTORY TESTS ====================
    
    @Test
    @DisplayName("Get transaction history for account")
    void testGetTransactionHistory() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("100.00"));
        
        bank.deposit(account.getAccountNumber(), new BigDecimal("50.00"));
        bank.withdraw(account.getAccountNumber(), new BigDecimal("30.00"));
        
        List<Transaction> history = bank.getTransactionHistory(account.getAccountNumber());
        
        assertEquals(2, history.size());
        assertEquals(TransactionType.DEPOSIT, history.get(0).getType());
        assertEquals(TransactionType.WITHDRAWAL, history.get(1).getType());
    }
    
    @Test
    @DisplayName("Get filtered transaction history by type")
    void testGetFilteredTransactionHistory() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("500.00"));
        
        bank.deposit(account.getAccountNumber(), new BigDecimal("100.00"));
        bank.withdraw(account.getAccountNumber(), new BigDecimal("50.00"));
        bank.deposit(account.getAccountNumber(), new BigDecimal("200.00"));
        
        List<Transaction> deposits = bank.getTransactionHistory(
            account.getAccountNumber(), null, null, TransactionType.DEPOSIT
        );
        
        assertEquals(2, deposits.size());
        assertTrue(deposits.stream().allMatch(tx -> tx.getType() == TransactionType.DEPOSIT));
    }
    
    @Test
    @DisplayName("Get failed transactions only")
    void testGetFailedTransactions() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("50.00"));
        
        bank.deposit(account.getAccountNumber(), new BigDecimal("50.00")); // Success
        bank.withdraw(account.getAccountNumber(), new BigDecimal("200.00")); // Fails - insufficient funds
        bank.deposit(account.getAccountNumber(), new BigDecimal("100.00")); // Success
        
        List<Transaction> failed = bank.getFailedTransactions(account.getAccountNumber());
        
        assertEquals(1, failed.size());
        assertTrue(failed.get(0).isFailed());
    }
    
    // ==================== MONTHLY ADJUSTMENTS TESTS ====================
    
    @Test
    @DisplayName("Apply monthly interest to all accounts")
    void testApplyMonthlyInterest() {
        Account checking = bank.openAccount("Alice", AccountType.CHECKING, new BigDecimal("1000.00"));
        Account savings = bank.openAccount("Bob", AccountType.SAVINGS, new BigDecimal("1000.00"));
        
        // Perform 15 transactions on CHECKING (10 free + 5 @ $2.50 = $12.50 fee)
        for (int i = 0; i < 15; i++) {
            bank.deposit(checking.getAccountNumber(), new BigDecimal("10.00"));
        }
        
        bank.applyMonthlyInterest();
        
        // CHECKING: Should have fee applied
        BigDecimal expectedChecking = new BigDecimal("1150.00").subtract(new BigDecimal("12.50"));
        assertEquals(expectedChecking, checking.getBalance());
        
        // SAVINGS: Should have 2% interest applied
        BigDecimal expectedSavings = new BigDecimal("1000.00").add(new BigDecimal("20.00"));
        assertEquals(expectedSavings, savings.getBalance());
        
        // Counters should be reset
        assertEquals(0, checking.getMonthlyTransactionCount());
        assertEquals(0, savings.getMonthlyTransactionCount());
    }
    
    // ==================== MONTHLY STATEMENT TESTS ====================
    
    @Test
    @DisplayName("Generate monthly statement for account")
    void testGenerateMonthlyStatement() {
        Account account = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("500.00"));
        bank.deposit(account.getAccountNumber(), new BigDecimal("100.00"));
        
        String statement = bank.generateMonthlyStatement(account.getAccountNumber());
        
        assertNotNull(statement);
        assertTrue(statement.contains("John Doe"));
        assertTrue(statement.contains("CHECKING"));
        assertTrue(statement.contains("$600.00")); // Balance after deposit
        assertTrue(statement.contains("DEPOSIT"));
    }
    
    // ==================== ADDITIONAL METHODS TESTS ====================
    
    @Test
    @DisplayName("Get all accounts")
    void testGetAllAccounts() {
        bank.openAccount("Alice", AccountType.CHECKING, new BigDecimal("100.00"));
        bank.openAccount("Bob", AccountType.SAVINGS, new BigDecimal("200.00"));
        bank.openAccount("Charlie", AccountType.CHECKING, new BigDecimal("300.00"));
        
        Collection<Account> accounts = bank.getAllAccounts();
        
        assertEquals(3, accounts.size());
    }
    
    @Test
    @DisplayName("Get accounts by type")
    void testGetAccountsByType() {
        bank.openAccount("Alice", AccountType.CHECKING, new BigDecimal("100.00"));
        bank.openAccount("Bob", AccountType.SAVINGS, new BigDecimal("200.00"));
        bank.openAccount("Charlie", AccountType.CHECKING, new BigDecimal("300.00"));
        
        List<Account> checkingAccounts = bank.getAccountsByType(AccountType.CHECKING);
        List<Account> savingsAccounts = bank.getAccountsByType(AccountType.SAVINGS);
        
        assertEquals(2, checkingAccounts.size());
        assertEquals(1, savingsAccounts.size());
    }
    
    @Test
    @DisplayName("Get account count")
    void testGetAccountCount() {
        assertEquals(0, bank.getAccountCount());
        
        bank.openAccount("Alice", AccountType.CHECKING, new BigDecimal("100.00"));
        assertEquals(1, bank.getAccountCount());
        
        bank.openAccount("Bob", AccountType.SAVINGS, new BigDecimal("200.00"));
        assertEquals(2, bank.getAccountCount());
    }
    
    @Test
    @DisplayName("Check account exists")
    void testAccountExists() {
        Account account = bank.openAccount("Alice", AccountType.CHECKING, new BigDecimal("100.00"));
        
        assertTrue(bank.accountExists(account.getAccountNumber()));
        assertFalse(bank.accountExists("ACC-99999999"));
        assertFalse(bank.accountExists(null));
    }
    
    @Test
    @DisplayName("Get account by account number")
    void testGetAccount() {
        Account created = bank.openAccount("Alice", AccountType.CHECKING, new BigDecimal("100.00"));
        
        Account retrieved = bank.getAccount(created.getAccountNumber());
        
        assertSame(created, retrieved);
    }
}
