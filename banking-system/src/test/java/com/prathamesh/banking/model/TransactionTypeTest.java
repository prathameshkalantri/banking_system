package com.prathamesh.banking.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TransactionType enum.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
class TransactionTypeTest {
    
    @Test
    void testEnumValues() {
        // Verify all expected transaction types exist
        TransactionType[] types = TransactionType.values();
        assertEquals(3, types.length, "Should have exactly 3 transaction types");
        
        // Verify each value is accessible
        assertNotNull(TransactionType.DEPOSIT);
        assertNotNull(TransactionType.WITHDRAWAL);
        assertNotNull(TransactionType.TRANSFER);
    }
    
    @Test
    void testValueOf() {
        // Test valueOf with valid strings
        assertEquals(TransactionType.DEPOSIT, TransactionType.valueOf("DEPOSIT"));
        assertEquals(TransactionType.WITHDRAWAL, TransactionType.valueOf("WITHDRAWAL"));
        assertEquals(TransactionType.TRANSFER, TransactionType.valueOf("TRANSFER"));
    }
    
    @Test
    void testValueOfInvalidThrowsException() {
        // Test valueOf with invalid string
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionType.valueOf("INVALID");
        });
    }
}
