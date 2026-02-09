package com.prathamesh.banking.model;

import com.prathamesh.banking.domain.AccountType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AccountType enum.
 * Verifies enum values, valueOf, and basic enum operations.
 */
@DisplayName("AccountType Enum Tests")
class AccountTypeTest {

    @Test
    @DisplayName("Should contain exactly two account types")
    void shouldContainExactlyTwoAccountTypes() {
        AccountType[] types = AccountType.values();
        assertEquals(2, types.length, "Should have exactly 2 account types");
    }

    @Test
    @DisplayName("Should contain CHECKING account type")
    void shouldContainCheckingAccountType() {
        AccountType[] types = AccountType.values();
        boolean hasChecking = false;
        for (AccountType type : types) {
            if (type == AccountType.CHECKING) {
                hasChecking = true;
                break;
            }
        }
        assertTrue(hasChecking, "Should contain CHECKING account type");
    }

    @Test
    @DisplayName("Should contain SAVINGS account type")
    void shouldContainSavingsAccountType() {
        AccountType[] types = AccountType.values();
        boolean hasSavings = false;
        for (AccountType type : types) {
            if (type == AccountType.SAVINGS) {
                hasSavings = true;
                break;
            }
        }
        assertTrue(hasSavings, "Should contain SAVINGS account type");
    }

    @Test
    @DisplayName("Should get CHECKING by valueOf")
    void shouldGetCheckingByValueOf() {
        AccountType type = AccountType.valueOf("CHECKING");
        assertNotNull(type, "CHECKING should not be null");
        assertEquals(AccountType.CHECKING, type, "Should get CHECKING account type");
    }

    @Test
    @DisplayName("Should get SAVINGS by valueOf")
    void shouldGetSavingsByValueOf() {
        AccountType type = AccountType.valueOf("SAVINGS");
        assertNotNull(type, "SAVINGS should not be null");
        assertEquals(AccountType.SAVINGS, type, "Should get SAVINGS account type");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid valueOf")
    void shouldThrowExceptionForInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            AccountType.valueOf("INVALID");
        }, "Should throw IllegalArgumentException for invalid account type");
    }

    @Test
    @DisplayName("Should have consistent toString")
    void shouldHaveConsistentToString() {
        assertEquals("CHECKING", AccountType.CHECKING.toString(),
                "CHECKING toString should match name");
        assertEquals("SAVINGS", AccountType.SAVINGS.toString(),
                "SAVINGS toString should match name");
    }

    @Test
    @DisplayName("Should maintain enum equality")
    void shouldMaintainEnumEquality() {
        AccountType checking1 = AccountType.CHECKING;
        AccountType checking2 = AccountType.valueOf("CHECKING");
        assertSame(checking1, checking2, "Same enum values should be identical");
    }

    @Test
    @DisplayName("Should maintain enum inequality")
    void shouldMaintainEnumInequality() {
        AccountType checking = AccountType.CHECKING;
        AccountType savings = AccountType.SAVINGS;
        assertNotEquals(checking, savings, "Different enum values should not be equal");
    }

    @Test
    @DisplayName("Should have consistent ordinal values")
    void shouldHaveConsistentOrdinalValues() {
        assertTrue(AccountType.CHECKING.ordinal() >= 0, "CHECKING ordinal should be non-negative");
        assertTrue(AccountType.SAVINGS.ordinal() >= 0, "SAVINGS ordinal should be non-negative");
        assertNotEquals(AccountType.CHECKING.ordinal(), AccountType.SAVINGS.ordinal(),
                "Different enums should have different ordinals");
    }
}
