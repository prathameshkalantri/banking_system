package com.prathamesh.banking.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IdGenerator utility class.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
class IdGeneratorTest {
    
    @AfterEach
    void resetCounters() {
        IdGenerator.resetCounters();
    }
    
    @Test
    void testCannotInstantiate() {
        // Act & Assert - Reflection wraps the exception in InvocationTargetException
        java.lang.reflect.InvocationTargetException exception = assertThrows(
            java.lang.reflect.InvocationTargetException.class, () -> {
            java.lang.reflect.Constructor<IdGenerator> constructor = 
                IdGenerator.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
        
        // Verify the cause is UnsupportedOperationException
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertTrue(exception.getCause().getMessage().contains("Utility class"));
    }
    
    @Test
    void testGenerateAccountNumber() {
        // Act
        String accountNumber = IdGenerator.generateAccountNumber();
        
        // Assert
        assertNotNull(accountNumber);
        assertTrue(accountNumber.startsWith("ACC-"));
        assertEquals(12, accountNumber.length()); // ACC- + 8 digits
    }
    
    @Test
    void testAccountNumberFormat() {
        // Act
        String accountNumber = IdGenerator.generateAccountNumber();
        
        // Assert
        assertTrue(accountNumber.matches("ACC-\\d{8}"));
    }
    
    @Test
    void testAccountNumbersAreSequential() {
        // Act
        String acc1 = IdGenerator.generateAccountNumber();
        String acc2 = IdGenerator.generateAccountNumber();
        String acc3 = IdGenerator.generateAccountNumber();
        
        // Assert
        assertEquals("ACC-00000001", acc1);
        assertEquals("ACC-00000002", acc2);
        assertEquals("ACC-00000003", acc3);
    }
    
    @Test
    void testAccountNumbersAreUnique() {
        // Arrange & Act
        Set<String> accountNumbers = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            accountNumbers.add(IdGenerator.generateAccountNumber());
        }
        
        // Assert
        assertEquals(100, accountNumbers.size(), "All account numbers should be unique");
    }
    
    @Test
    void testGenerateTransactionId() {
        // Act
        String transactionId = IdGenerator.generateTransactionId();
        
        // Assert
        assertNotNull(transactionId);
        assertTrue(transactionId.startsWith("TXN-"));
    }
    
    @Test
    void testTransactionIdFormat() {
        // Act
        String transactionId = IdGenerator.generateTransactionId();
        
        // Assert - Format: TXN-{14-digit-timestamp}-{6-char-uuid}
        String[] parts = transactionId.split("-");
        assertEquals(3, parts.length);
        assertEquals("TXN", parts[0]);
        assertEquals(14, parts[1].length()); // yyyyMMddHHmmss
        assertEquals(6, parts[2].length()); // UUID fragment
    }
    
    @Test
    void testTransactionIdsAreUnique() {
        // Arrange & Act
        Set<String> transactionIds = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            transactionIds.add(IdGenerator.generateTransactionId());
        }
        
        // Assert
        assertEquals(100, transactionIds.size(), "All transaction IDs should be unique");
    }
    
    @Test
    void testResetCounters() {
        // Arrange - Generate some IDs
        IdGenerator.generateAccountNumber();
        IdGenerator.generateAccountNumber();
        
        // Act
        IdGenerator.resetCounters();
        String accountNumber = IdGenerator.generateAccountNumber();
        
        // Assert - Should start from 1 again
        assertEquals("ACC-00000001", accountNumber);
    }
}
