package com.prathamesh.banking.model;

import com.prathamesh.banking.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TransactionType enum.
 * Verifies all transaction type values and operations.
 */
@DisplayName("TransactionType Enum Tests")
class TransactionTypeTest {

    @Test
    @DisplayName("Should contain exactly five transaction types")
    void shouldContainExactlyFiveTransactionTypes() {
        TransactionType[] types = TransactionType.values();
        assertEquals(5, types.length, "Should have exactly 5 transaction types");
    }

    @Test
    @DisplayName("Should contain DEPOSIT transaction type")
    void shouldContainDepositType() {
        TransactionType type = TransactionType.valueOf("DEPOSIT");
        assertNotNull(type, "DEPOSIT should exist");
        assertEquals(TransactionType.DEPOSIT, type);
    }

    @Test
    @DisplayName("Should contain WITHDRAWAL transaction type")
    void shouldContainWithdrawalType() {
        TransactionType type = TransactionType.valueOf("WITHDRAWAL");
        assertNotNull(type, "WITHDRAWAL should exist");
        assertEquals(TransactionType.WITHDRAWAL, type);
    }

    @Test
    @DisplayName("Should contain TRANSFER transaction type")
    void shouldContainTransferType() {
        TransactionType type = TransactionType.valueOf("TRANSFER");
        assertNotNull(type, "TRANSFER should exist");
        assertEquals(TransactionType.TRANSFER, type);
    }

    @Test
    @DisplayName("Should contain FEE transaction type")
    void shouldContainFeeType() {
        TransactionType type = TransactionType.valueOf("FEE");
        assertNotNull(type, "FEE should exist");
        assertEquals(TransactionType.FEE, type);
    }

    @Test
    @DisplayName("Should contain INTEREST transaction type")
    void shouldContainInterestType() {
        TransactionType type = TransactionType.valueOf("INTEREST");
        assertNotNull(type, "INTEREST should exist");
        assertEquals(TransactionType.INTEREST, type);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid valueOf")
    void shouldThrowExceptionForInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionType.valueOf("INVALID_TYPE");
        }, "Should throw exception for invalid transaction type");
    }

    @Test
    @DisplayName("Should have unique ordinal values")
    void shouldHaveUniqueOrdinalValues() {
        TransactionType[] types = TransactionType.values();
        for (int i = 0; i < types.length; i++) {
            for (int j = i + 1; j < types.length; j++) {
                assertNotEquals(types[i].ordinal(), types[j].ordinal(),
                        "Each transaction type should have unique ordinal");
            }
        }
    }

    @Test
    @DisplayName("Should have consistent toString values")
    void shouldHaveConsistentToStringValues() {
        assertEquals("DEPOSIT", TransactionType.DEPOSIT.toString());
        assertEquals("WITHDRAWAL", TransactionType.WITHDRAWAL.toString());
        assertEquals("TRANSFER", TransactionType.TRANSFER.toString());
        assertEquals("FEE", TransactionType.FEE.toString());
        assertEquals("INTEREST", TransactionType.INTEREST.toString());
    }

    @Test
    @DisplayName("Should maintain enum singleton pattern")
    void shouldMaintainEnumSingletonPattern() {
        TransactionType deposit1 = TransactionType.DEPOSIT;
        TransactionType deposit2 = TransactionType.valueOf("DEPOSIT");
        assertSame(deposit1, deposit2, "Enum values should be singletons");
    }

    @Test
    @DisplayName("Should work in switch statements")
    void shouldWorkInSwitchStatements() {
        String result = getTransactionDescription(TransactionType.DEPOSIT);
        assertEquals("Credit", result);
        
        result = getTransactionDescription(TransactionType.WITHDRAWAL);
        assertEquals("Debit", result);
        
        result = getTransactionDescription(TransactionType.FEE);
        assertEquals("Debit", result);
    }

    private String getTransactionDescription(TransactionType type) {
        switch (type) {
            case DEPOSIT:
            case INTEREST:
                return "Credit";
            case WITHDRAWAL:
            case TRANSFER:
            case FEE:
                return "Debit";
            default:
                return "Unknown";
        }
    }

    @Test
    @DisplayName("Should iterate through all values")
    void shouldIterateThroughAllValues() {
        int count = 0;
        for (TransactionType type : TransactionType.values()) {
            assertNotNull(type, "Each transaction type should be non-null");
            count++;
        }
        assertEquals(5, count, "Should iterate through all 5 transaction types");
    }
}
