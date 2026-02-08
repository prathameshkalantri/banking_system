package com.prathamesh.banking.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AccountType enum.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
class AccountTypeTest {
    
    @Test
    void testEnumValues() {
        // Verify all expected account types exist
        AccountType[] types = AccountType.values();
        assertEquals(2, types.length, "Should have exactly 2 account types");
        
        // Verify each value is accessible
        assertNotNull(AccountType.CHECKING);
        assertNotNull(AccountType.SAVINGS);
    }
    
    @Test
    void testValueOf() {
        // Test valueOf with valid strings
        assertEquals(AccountType.CHECKING, AccountType.valueOf("CHECKING"));
        assertEquals(AccountType.SAVINGS, AccountType.valueOf("SAVINGS"));
    }
    
    @Test
    void testValueOfInvalidThrowsException() {
        // Test valueOf with invalid string
        assertThrows(IllegalArgumentException.class, () -> {
            AccountType.valueOf("INVALID");
        });
    }
}
