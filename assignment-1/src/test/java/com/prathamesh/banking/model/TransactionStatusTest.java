package com.prathamesh.banking.model;

import com.prathamesh.banking.domain.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TransactionStatus enum.
 * Verifies transaction status values and operations.
 */
@DisplayName("TransactionStatus Enum Tests")
class TransactionStatusTest {

    @Test
    @DisplayName("Should contain exactly two transaction statuses")
    void shouldContainExactlyTwoStatuses() {
        TransactionStatus[] statuses = TransactionStatus.values();
        assertEquals(2, statuses.length, "Should have exactly 2 transaction statuses");
    }

    @Test
    @DisplayName("Should contain SUCCESS status")
    void shouldContainSuccessStatus() {
        TransactionStatus status = TransactionStatus.valueOf("SUCCESS");
        assertNotNull(status, "SUCCESS status should exist");
        assertEquals(TransactionStatus.SUCCESS, status);
    }

    @Test
    @DisplayName("Should contain FAILED status")
    void shouldContainFailedStatus() {
        TransactionStatus status = TransactionStatus.valueOf("FAILED");
        assertNotNull(status, "FAILED status should exist");
        assertEquals(TransactionStatus.FAILED, status);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid valueOf")
    void shouldThrowExceptionForInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionStatus.valueOf("PENDING");
        }, "Should throw exception for invalid status");
    }

    @Test
    @DisplayName("Should have consistent toString values")
    void shouldHaveConsistentToStringValues() {
        assertEquals("SUCCESS", TransactionStatus.SUCCESS.toString());
        assertEquals("FAILED", TransactionStatus.FAILED.toString());
    }

    @Test
    @DisplayName("Should maintain enum singleton pattern")
    void shouldMaintainEnumSingletonPattern() {
        TransactionStatus success1 = TransactionStatus.SUCCESS;
        TransactionStatus success2 = TransactionStatus.valueOf("SUCCESS");
        assertSame(success1, success2, "Enum values should be singletons");
    }

    @Test
    @DisplayName("Should have different ordinal values")
    void shouldHaveDifferentOrdinalValues() {
        assertNotEquals(TransactionStatus.SUCCESS.ordinal(), 
                TransactionStatus.FAILED.ordinal(),
                "Different statuses should have different ordinals");
    }

    @Test
    @DisplayName("Should work in conditional logic")
    void shouldWorkInConditionalLogic() {
        TransactionStatus status = TransactionStatus.SUCCESS;
        boolean isSuccessful = (status == TransactionStatus.SUCCESS);
        assertTrue(isSuccessful, "Should correctly identify SUCCESS status");
        
        status = TransactionStatus.FAILED;
        isSuccessful = (status == TransactionStatus.SUCCESS);
        assertFalse(isSuccessful, "Should correctly identify FAILED status");
    }

    @Test
    @DisplayName("Should work in switch statements")
    void shouldWorkInSwitchStatements() {
        String message = getStatusMessage(TransactionStatus.SUCCESS);
        assertEquals("Transaction completed successfully", message);
        
        message = getStatusMessage(TransactionStatus.FAILED);
        assertEquals("Transaction failed", message);
    }

    private String getStatusMessage(TransactionStatus status) {
        switch (status) {
            case SUCCESS:
                return "Transaction completed successfully";
            case FAILED:
                return "Transaction failed";
            default:
                return "Unknown status";
        }
    }

    @Test
    @DisplayName("Should iterate through all values")
    void shouldIterateThroughAllValues() {
        int count = 0;
        for (TransactionStatus status : TransactionStatus.values()) {
            assertNotNull(status, "Each status should be non-null");
            count++;
        }
        assertEquals(2, count, "Should iterate through all 2 statuses");
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        assertNotEquals(null, TransactionStatus.SUCCESS);
        assertNotEquals(null, TransactionStatus.FAILED);
    }

    @Test
    @DisplayName("Should maintain inequality between different statuses")
    void shouldMaintainInequality() {
        assertNotEquals(TransactionStatus.SUCCESS, TransactionStatus.FAILED);
        assertNotEquals(TransactionStatus.FAILED, TransactionStatus.SUCCESS);
    }
}
