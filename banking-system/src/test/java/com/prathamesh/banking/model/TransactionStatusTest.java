package com.prathamesh.banking.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TransactionStatus enum.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
class TransactionStatusTest {
    
    @Test
    void testEnumValues() {
        // Verify all expected values exist
        TransactionStatus[] statuses = TransactionStatus.values();
        assertEquals(2, statuses.length, "Should have exactly 2 status values");
        
        // Verify each value is accessible
        assertNotNull(TransactionStatus.SUCCESS);
        assertNotNull(TransactionStatus.FAILED);
    }
    
    @Test
    void testValueOf() {
        // Test valueOf with valid strings
        assertEquals(TransactionStatus.SUCCESS, TransactionStatus.valueOf("SUCCESS"));
        assertEquals(TransactionStatus.FAILED, TransactionStatus.valueOf("FAILED"));
    }
    
    @Test
    void testValueOfInvalidThrowsException() {
        // Test valueOf with invalid string
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionStatus.valueOf("INVALID");
        });
    }
}
