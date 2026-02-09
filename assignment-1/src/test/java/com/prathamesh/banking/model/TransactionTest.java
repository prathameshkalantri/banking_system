package com.prathamesh.banking.model;

import com.prathamesh.banking.domain.Transaction;
import com.prathamesh.banking.domain.TransactionType;
import com.prathamesh.banking.domain.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Transaction model.
 * Verifies Builder pattern, immutability, and validation.
 */
@DisplayName("Transaction Model Tests")
class TransactionTest {

    private String testAccountNumber;
    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testAccountNumber = "ACC-001";
        testTimestamp = LocalDateTime.now();
    }

    @Test
    @DisplayName("Should create transaction with all required fields")
    void shouldCreateTransactionWithRequiredFields() {
        Transaction transaction = new Transaction.Builder(
                "TXN-001",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                testAccountNumber
        )
                .balanceBefore(BigDecimal.ZERO)
                .balanceAfter(new BigDecimal("100.00"))
                .status(TransactionStatus.SUCCESS)
                .timestamp(testTimestamp)
                .build();

        assertNotNull(transaction);
        assertEquals("TXN-001", transaction.getTransactionId());
        assertEquals(testAccountNumber, transaction.getAccountNumber());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertEquals(new BigDecimal("100.00"), transaction.getBalanceAfter());
        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
        assertEquals(testTimestamp, transaction.getTimestamp());
    }

    @Test
    @DisplayName("Should create transaction with minimal required fields")
    void shouldCreateTransactionWithMinimalFields() {
        Transaction transaction = new Transaction.Builder(
                "TXN-002",
                TransactionType.WITHDRAWAL,
                new BigDecimal("50.00"),
                testAccountNumber
        ).build();

        assertNotNull(transaction);
        assertEquals("TXN-002", transaction.getTransactionId());
        assertNotNull(transaction.getTimestamp());
        assertNull(transaction.getFailureReason());
    }

    @Test
    @DisplayName("Should throw NullPointerException when transactionId is null")
    void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Transaction.Builder(
                    null,
                    TransactionType.DEPOSIT,
                    new BigDecimal("100.00"),
                    testAccountNumber
            ).build();
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException when type is null")
    void shouldThrowExceptionWhenTypeIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Transaction.Builder(
                    "TXN-003",
                    null,
                    new BigDecimal("100.00"),
                    testAccountNumber
            ).build();
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException when amount is null")
    void shouldThrowExceptionWhenAmountIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Transaction.Builder(
                    "TXN-004",
                    TransactionType.DEPOSIT,
                    null,
                    testAccountNumber
            ).build();
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException when accountNumber is null")
    void shouldThrowExceptionWhenAccountNumberIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Transaction.Builder(
                    "TXN-005",
                    TransactionType.DEPOSIT,
                    new BigDecimal("100.00"),
                    null
            ).build();
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException when balanceAfter is null")
    void shouldThrowExceptionWhenBalanceAfterIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Transaction.Builder(
                    "TXN-006",
                    TransactionType.DEPOSIT,
                    new BigDecimal("100.00"),
                    testAccountNumber
            )
                    .balanceAfter(null)
                    .build();
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException when status is null")
    void shouldThrowExceptionWhenStatusIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Transaction.Builder(
                    "TXN-007",
                    TransactionType.DEPOSIT,
                    new BigDecimal("100.00"),
                    testAccountNumber
            )
                    .status(null)
                    .build();
        });
    }

    @Test
    @DisplayName("Should throw NullPointerException when timestamp is null")
    void shouldThrowExceptionWhenTimestampIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new Transaction.Builder(
                    "TXN-008",
                    TransactionType.DEPOSIT,
                    new BigDecimal("100.00"),
                    testAccountNumber
            )
                    .timestamp(null)
                    .build();
        });
    }

    @Test
    @DisplayName("Should implement equals correctly for same object")
    void shouldImplementEqualsForSameObject() {
        Transaction transaction = new Transaction.Builder(
                "TXN-010",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                testAccountNumber
        ).build();

        assertEquals(transaction, transaction, "Transaction should equal itself");
    }

    @Test
    @DisplayName("Should implement equals correctly for equal transactions")
    void shouldImplementEqualsForEqualTransactions() {
        Transaction transaction1 = new Transaction.Builder(
                "TXN-011",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                testAccountNumber
        )
                .balanceBefore(BigDecimal.ZERO)
                .balanceAfter(new BigDecimal("100.00"))
                .status(TransactionStatus.SUCCESS)
                .timestamp(testTimestamp)
                .build();

        Transaction transaction2 = new Transaction.Builder(
                "TXN-011",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                testAccountNumber
        )
                .balanceBefore(BigDecimal.ZERO)
                .balanceAfter(new BigDecimal("100.00"))
                .status(TransactionStatus.SUCCESS)
                .timestamp(testTimestamp)
                .build();

        assertEquals(transaction1, transaction2, "Transactions with same values should be equal");
    }

    @Test
    @DisplayName("Should implement equals correctly for different transactions")
    void shouldImplementEqualsForDifferentTransactions() {
        Transaction transaction1 = new Transaction.Builder(
                "TXN-012",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                testAccountNumber
        ).build();

        Transaction transaction2 = new Transaction.Builder(
                "TXN-013",
                TransactionType.WITHDRAWAL,
                new BigDecimal("50.00"),
                testAccountNumber
        ).build();

        assertNotEquals(transaction1, transaction2, "Transactions with different values should not be equal");
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        Transaction transaction1 = new Transaction.Builder(
                "TXN-014",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                testAccountNumber
        )
                .timestamp(testTimestamp)
                .build();

        Transaction transaction2 = new Transaction.Builder(
                "TXN-014",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                testAccountNumber
        )
                .timestamp(testTimestamp)
                .build();

        assertEquals(transaction1.hashCode(), transaction2.hashCode(),
                "Equal transactions should have equal hash codes");
    }

    @Test
    @DisplayName("Should handle failed transaction status")
    void shouldHandleFailedTransactionStatus() {
        Transaction transaction = new Transaction.Builder(
                "TXN-015",
                TransactionType.WITHDRAWAL,
                new BigDecimal("1000.00"),
                testAccountNumber
        )
                .balanceAfter(BigDecimal.ZERO)
                .status(TransactionStatus.FAILED)
                .failureReason("Insufficient funds")
                .build();

        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
        assertEquals("Insufficient funds", transaction.getFailureReason());
    }

    @Test
    @DisplayName("Should handle transfer transactions")
    void shouldHandleTransferTransactions() {
        Transaction transaction = new Transaction.Builder(
                "TXN-016",
                TransactionType.TRANSFER,
                new BigDecimal("200.00"),
                testAccountNumber
        )
                .balanceBefore(new BigDecimal("1000.00"))
                .balanceAfter(new BigDecimal("800.00"))
                .status(TransactionStatus.SUCCESS)
                .build();

        assertEquals(TransactionType.TRANSFER, transaction.getType());
    }

    @Test
    @DisplayName("Should handle zero amount transactions")
    void shouldHandleZeroAmountTransactions() {
        Transaction transaction = new Transaction.Builder(
                "TXN-017",
                TransactionType.FEE,
                BigDecimal.ZERO,
                testAccountNumber
        )
                .balanceAfter(new BigDecimal("100.00"))
                .build();

        assertEquals(BigDecimal.ZERO, transaction.getAmount());
    }

    @Test
    @DisplayName("Should handle large amount transactions")
    void shouldHandleLargeAmountTransactions() {
        BigDecimal largeAmount = new BigDecimal("999999999.99");
        Transaction transaction = new Transaction.Builder(
                "TXN-018",
                TransactionType.DEPOSIT,
                largeAmount,
                testAccountNumber
        )
                .balanceAfter(largeAmount)
                .build();

        assertEquals(largeAmount, transaction.getAmount());
        assertEquals(largeAmount, transaction.getBalanceAfter());
    }

    @Test
    @DisplayName("Should support fluent builder interface")
    void shouldSupportFluentBuilderInterface() {
        Transaction transaction = new Transaction.Builder(
                "TXN-019",
                TransactionType.INTEREST,
                new BigDecimal("5.00"),
                testAccountNumber
        )
                .balanceBefore(new BigDecimal("100.00"))
                .balanceAfter(new BigDecimal("105.00"))
                .status(TransactionStatus.SUCCESS)
                .timestamp(testTimestamp)
                .build();

        assertNotNull(transaction);
        assertEquals(TransactionType.INTEREST, transaction.getType());
    }

    @Test
    @DisplayName("Should have default timestamp when not specified")
    void shouldHaveDefaultTimestamp() {
        Transaction transaction = new Transaction.Builder(
                "TXN-020",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                testAccountNumber
        ).build();

        assertNotNull(transaction.getTimestamp());
    }

    @Test
    @DisplayName("Should have default status SUCCESS when not specified")
    void shouldHaveDefaultSuccessStatus() {
        Transaction transaction = new Transaction.Builder(
                "TXN-021",
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                testAccountNumber
        ).build();

        assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
    }

    @Test
    @DisplayName("Should handle all transaction types")
    void shouldHandleAllTransactionTypes() {
        for (TransactionType type : TransactionType.values()) {
            Transaction transaction = new Transaction.Builder(
                    "TXN-" + type.name(),
                    type,
                    new BigDecimal("100.00"),
                    testAccountNumber
            ).build();

            assertNotNull(transaction);
            assertEquals(type, transaction.getType());
        }
    }
}
