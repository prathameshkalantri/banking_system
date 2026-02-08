package com.prathamesh.banking.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Transaction class.
 * Tests the Builder pattern, validation, immutability, and all business logic.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
class TransactionTest {
    
    @Test
    void testBuildSuccessfulTransaction() {
        // Arrange
        String txnId = "TXN-001";
        LocalDateTime timestamp = LocalDateTime.now();
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal balanceBefore = new BigDecimal("500.00");
        BigDecimal balanceAfter = new BigDecimal("600.00");
        
        // Act
        Transaction transaction = new Transaction.Builder()
                .transactionId(txnId)
                .timestamp(timestamp)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .status(TransactionStatus.SUCCESS)
                .build();
        
        // Assert
        assertNotNull(transaction);
        assertEquals(txnId, transaction.getTransactionId());
        assertEquals(timestamp, transaction.getTimestamp());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(balanceBefore, transaction.getBalanceBefore());
        assertEquals(balanceAfter, transaction.getBalanceAfter());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        assertNull(transaction.getFailureReason());
        assertTrue(transaction.isSuccessful());
    }
    
    @Test
    void testBuildFailedTransaction() {
        // Arrange
        String failureReason = "Insufficient funds";
        
        // Act
        Transaction transaction = new Transaction.Builder()
                .transactionId("TXN-002")
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("50.00"))
                .balanceAfter(new BigDecimal("50.00"))
                .status(TransactionStatus.FAILED)
                .failureReason(failureReason)
                .build();
        
        // Assert
        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
        assertEquals(failureReason, transaction.getFailureReason());
        assertFalse(transaction.isSuccessful());
    }
    
    @Test
    void testBuildWithoutTransactionIdThrowsException() {
        // Act & Assert
        Transaction.Builder builder = new Transaction.Builder()
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("600.00"));
        
        assertThrows(NullPointerException.class, builder::build);
    }
    
    @Test
    void testBuildWithoutTypeThrowsException() {
        // Act & Assert
        Transaction.Builder builder = new Transaction.Builder()
                .transactionId("TXN-003")
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("600.00"));
        
        assertThrows(NullPointerException.class, builder::build);
    }
    
    @Test
    void testBuildWithoutAmountThrowsException() {
        // Act & Assert
        Transaction.Builder builder = new Transaction.Builder()
                .transactionId("TXN-004")
                .type(TransactionType.DEPOSIT)
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("600.00"));
        
        assertThrows(NullPointerException.class, builder::build);
    }
    
    @Test
    void testBuildWithZeroAmountThrowsException() {
        // Act & Assert
        Transaction.Builder builder = new Transaction.Builder()
                .transactionId("TXN-005")
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.ZERO)
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("500.00"));
        
        assertThrows(IllegalStateException.class, builder::build);
    }
    
    @Test
    void testBuildWithNegativeAmountThrowsException() {
        // Act & Assert
        Transaction.Builder builder = new Transaction.Builder()
                .transactionId("TXN-006")
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("-100.00"))
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("400.00"));
        
        assertThrows(IllegalStateException.class, builder::build);
    }
    
    @Test
    void testBuildFailedTransactionWithoutFailureReasonThrowsException() {
        // Act & Assert
        Transaction.Builder builder = new Transaction.Builder()
                .transactionId("TXN-007")
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("50.00"))
                .balanceAfter(new BigDecimal("50.00"))
                .status(TransactionStatus.FAILED);
        
        assertThrows(IllegalStateException.class, builder::build);
    }
    
    @Test
    void testBuildFailedTransactionWithEmptyFailureReasonThrowsException() {
        // Act & Assert
        Transaction.Builder builder = new Transaction.Builder()
                .transactionId("TXN-008")
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("50.00"))
                .balanceAfter(new BigDecimal("50.00"))
                .status(TransactionStatus.FAILED)
                .failureReason("   ");
        
        assertThrows(IllegalStateException.class, builder::build);
    }
    
    @Test
    void testTransactionEquality() {
        // Arrange - Two transactions with same ID
        Transaction txn1 = new Transaction.Builder()
                .transactionId("TXN-009")
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("600.00"))
                .build();
        
        Transaction txn2 = new Transaction.Builder()
                .transactionId("TXN-009")
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("50.00"))
                .balanceBefore(new BigDecimal("600.00"))
                .balanceAfter(new BigDecimal("550.00"))
                .build();
        
        // Assert - Equality based on transaction ID
        assertEquals(txn1, txn2);
        assertEquals(txn1.hashCode(), txn2.hashCode());
    }
    
    @Test
    void testTransactionInequality() {
        // Arrange - Two transactions with different IDs
        Transaction txn1 = new Transaction.Builder()
                .transactionId("TXN-010")
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("600.00"))
                .build();
        
        Transaction txn2 = new Transaction.Builder()
                .transactionId("TXN-011")
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("600.00"))
                .build();
        
        // Assert
        assertNotEquals(txn1, txn2);
    }
    
    @Test
    void testToString() {
        // Arrange
        Transaction transaction = new Transaction.Builder()
                .transactionId("TXN-012")
                .type(TransactionType.TRANSFER)
                .amount(new BigDecimal("250.00"))
                .balanceBefore(new BigDecimal("1000.00"))
                .balanceAfter(new BigDecimal("750.00"))
                .build();
        
        // Act
        String result = transaction.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("TXN-012"));
        assertTrue(result.contains("TRANSFER"));
        assertTrue(result.contains("250.00"));
    }
    
    @Test
    void testDefaultTimestampIsSet() {
        // Arrange & Act
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        Transaction transaction = new Transaction.Builder()
                .transactionId("TXN-013")
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("600.00"))
                .build();
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        
        // Assert - Timestamp should be between before and after
        assertTrue(transaction.getTimestamp().isAfter(before));
        assertTrue(transaction.getTimestamp().isBefore(after));
    }
    
    @Test
    void testImmutability() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        Transaction transaction = new Transaction.Builder()
                .transactionId("TXN-014")
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .balanceBefore(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("600.00"))
                .build();
        
        // Act - Try to modify the original amount
        amount = amount.add(new BigDecimal("50.00"));
        
        // Assert - Transaction amount should not change
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
    }
}
