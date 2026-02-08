package com.prathamesh.banking.service;

import com.prathamesh.banking.model.Account;
import com.prathamesh.banking.model.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for TransactionValidator.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
class TransactionValidatorTest {
    
    private TransactionValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new TransactionValidator();
    }
    
    // ========== Deposit Validation Tests ==========
    
    @Test
    void testValidateDeposit_ValidAmount() {
        // Act
        String result = validator.validateDeposit(new BigDecimal("100.00"));
        
        // Assert
        assertNull(result, "Valid deposit should pass validation");
    }
    
    @Test
    void testValidateDeposit_NullAmount() {
        // Act
        String result = validator.validateDeposit(null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("cannot be null"));
    }
    
    @Test
    void testValidateDeposit_ZeroAmount() {
        // Act
        String result = validator.validateDeposit(BigDecimal.ZERO);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("must be positive"));
    }
    
    @Test
    void testValidateDeposit_NegativeAmount() {
        // Act
        String result = validator.validateDeposit(new BigDecimal("-100.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("must be positive"));
    }
    
    // ========== Withdrawal Validation Tests ==========
    
    @Test
    void testValidateWithdrawal_ValidAmount() {
        // Arrange
        Account account = new Account("ACC-001", AccountType.CHECKING, 
                                     "John Doe", new BigDecimal("500.00"));
        
        // Act
        String result = validator.validateWithdrawal(account, new BigDecimal("100.00"));
        
        // Assert
        assertNull(result, "Valid withdrawal should pass validation");
    }
    
    @Test
    void testValidateWithdrawal_NullAccount() {
        // Act
        String result = validator.validateWithdrawal(null, new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Account cannot be null"));
    }
    
    @Test
    void testValidateWithdrawal_NullAmount() {
        // Arrange
        Account account = new Account("ACC-001", AccountType.CHECKING, 
                                     "John Doe", new BigDecimal("500.00"));
        
        // Act
        String result = validator.validateWithdrawal(account, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("cannot be null"));
    }
    
    @Test
    void testValidateWithdrawal_InsufficientFunds() {
        // Arrange
        Account account = new Account("ACC-001", AccountType.CHECKING, 
                                     "John Doe", new BigDecimal("50.00"));
        
        // Act
        String result = validator.validateWithdrawal(account, new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Insufficient funds"));
    }
    
    @Test
    void testValidateWithdrawal_SavingsMinimumBalance() {
        // Arrange - $150 balance, try to withdraw $51 (would leave $99)
        Account account = new Account("ACC-001", AccountType.SAVINGS, 
                                     "John Doe", new BigDecimal("150.00"));
        
        // Act
        String result = validator.validateWithdrawal(account, new BigDecimal("51.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("minimum balance"));
    }
    
    @Test
    void testValidateWithdrawal_SavingsWithdrawalLimit() {
        // Arrange
        Account account = new Account("ACC-001", AccountType.SAVINGS, 
                                     "John Doe", new BigDecimal("1000.00"));
        
        // Make 5 withdrawals
        for (int i = 0; i < 5; i++) {
            account.withdraw(new BigDecimal("10.00"));
        }
        
        // Act - Try 6th withdrawal
        String result = validator.validateWithdrawal(account, new BigDecimal("10.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("limited to 5 withdrawals"));
    }
    
    // ========== Transfer Validation Tests ==========
    
    @Test
    void testValidateTransfer_ValidTransfer() {
        // Arrange
        Account fromAccount = new Account("ACC-001", AccountType.CHECKING, 
                                         "John Doe", new BigDecimal("500.00"));
        Account toAccount = new Account("ACC-002", AccountType.CHECKING, 
                                       "Jane Smith", new BigDecimal("200.00"));
        
        // Act
        String result = validator.validateTransfer(fromAccount, toAccount, new BigDecimal("100.00"));
        
        // Assert
        assertNull(result, "Valid transfer should pass validation");
    }
    
    @Test
    void testValidateTransfer_NullFromAccount() {
        // Arrange
        Account toAccount = new Account("ACC-002", AccountType.CHECKING, 
                                       "Jane Smith", new BigDecimal("200.00"));
        
        // Act
        String result = validator.validateTransfer(null, toAccount, new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Source account cannot be null"));
    }
    
    @Test
    void testValidateTransfer_NullToAccount() {
        // Arrange
        Account fromAccount = new Account("ACC-001", AccountType.CHECKING, 
                                         "John Doe", new BigDecimal("500.00"));
        
        // Act
        String result = validator.validateTransfer(fromAccount, null, new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Destination account cannot be null"));
    }
    
    @Test
    void testValidateTransfer_SameAccount() {
        // Arrange
        Account account = new Account("ACC-001", AccountType.CHECKING, 
                                     "John Doe", new BigDecimal("500.00"));
        
        // Act
        String result = validator.validateTransfer(account, account, new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Cannot transfer to the same account"));
    }
    
    @Test
    void testValidateTransfer_NullAmount() {
        // Arrange
        Account fromAccount = new Account("ACC-001", AccountType.CHECKING, 
                                         "John Doe", new BigDecimal("500.00"));
        Account toAccount = new Account("ACC-002", AccountType.CHECKING, 
                                       "Jane Smith", new BigDecimal("200.00"));
        
        // Act
        String result = validator.validateTransfer(fromAccount, toAccount, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("cannot be null"));
    }
    
    @Test
    void testValidateTransfer_ZeroAmount() {
        // Arrange
        Account fromAccount = new Account("ACC-001", AccountType.CHECKING, 
                                         "John Doe", new BigDecimal("500.00"));
        Account toAccount = new Account("ACC-002", AccountType.CHECKING, 
                                       "Jane Smith", new BigDecimal("200.00"));
        
        // Act
        String result = validator.validateTransfer(fromAccount, toAccount, BigDecimal.ZERO);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("must be positive"));
    }
    
    @Test
    void testValidateTransfer_InsufficientFunds() {
        // Arrange
        Account fromAccount = new Account("ACC-001", AccountType.CHECKING, 
                                         "John Doe", new BigDecimal("50.00"));
        Account toAccount = new Account("ACC-002", AccountType.CHECKING, 
                                       "Jane Smith", new BigDecimal("200.00"));
        
        // Act
        String result = validator.validateTransfer(fromAccount, toAccount, new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Insufficient funds"));
    }
    
    // ========== Account Closure Validation Tests ==========
    
    @Test
    void testValidateAccountClosure_ZeroBalance() {
        // Arrange
        Account account = new Account("ACC-001", AccountType.CHECKING, 
                                     "John Doe", BigDecimal.ZERO);
        
        // Act
        String result = validator.validateAccountClosure(account);
        
        // Assert
        assertNull(result, "Account with zero balance should be closeable");
    }
    
    @Test
    void testValidateAccountClosure_NonZeroBalance() {
        // Arrange
        Account account = new Account("ACC-001", AccountType.CHECKING, 
                                     "John Doe", new BigDecimal("100.00"));
        
        // Act
        String result = validator.validateAccountClosure(account);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("cannot be closed"));
        assertTrue(result.contains("100.00"));
    }
    
    @Test
    void testValidateAccountClosure_NullAccount() {
        // Act
        String result = validator.validateAccountClosure(null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Account cannot be null"));
    }
    
    // ========== Initial Deposit Validation Tests ==========
    
    @Test
    void testValidateInitialDeposit_CheckingAccount_ZeroDeposit() {
        // Act
        String result = validator.validateInitialDeposit(AccountType.CHECKING, BigDecimal.ZERO);
        
        // Assert
        assertNull(result, "CHECKING account allows zero initial deposit");
    }
    
    @Test
    void testValidateInitialDeposit_CheckingAccount_ValidDeposit() {
        // Act
        String result = validator.validateInitialDeposit(AccountType.CHECKING, new BigDecimal("50.00"));
        
        // Assert
        assertNull(result);
    }
    
    @Test
    void testValidateInitialDeposit_SavingsAccount_ValidDeposit() {
        // Act
        String result = validator.validateInitialDeposit(AccountType.SAVINGS, new BigDecimal("100.00"));
        
        // Assert
        assertNull(result);
    }
    
    @Test
    void testValidateInitialDeposit_SavingsAccount_BelowMinimum() {
        // Act
        String result = validator.validateInitialDeposit(AccountType.SAVINGS, new BigDecimal("99.99"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("minimum initial deposit"));
        assertTrue(result.contains("100.00"));
    }
    
    @Test
    void testValidateInitialDeposit_NegativeAmount() {
        // Act
        String result = validator.validateInitialDeposit(AccountType.CHECKING, new BigDecimal("-100.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("cannot be negative"));
    }
    
    @Test
    void testValidateInitialDeposit_NullAccountType() {
        // Act
        String result = validator.validateInitialDeposit(null, new BigDecimal("100.00"));
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Account type cannot be null"));
    }
    
    @Test
    void testValidateInitialDeposit_NullAmount() {
        // Act
        String result = validator.validateInitialDeposit(AccountType.CHECKING, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Initial deposit cannot be null"));
    }
    
    // ========== Customer Name Validation Tests ==========
    
    @Test
    void testValidateCustomerName_ValidName() {
        // Act
        String result = validator.validateCustomerName("John Doe");
        
        // Assert
        assertNull(result);
    }
    
    @Test
    void testValidateCustomerName_NullName() {
        // Act
        String result = validator.validateCustomerName(null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("cannot be null"));
    }
    
    @Test
    void testValidateCustomerName_EmptyName() {
        // Act
        String result = validator.validateCustomerName("   ");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("cannot be empty"));
    }
    
    @Test
    void testValidateCustomerName_TooShort() {
        // Act
        String result = validator.validateCustomerName("A");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("at least 2 characters"));
    }
}
