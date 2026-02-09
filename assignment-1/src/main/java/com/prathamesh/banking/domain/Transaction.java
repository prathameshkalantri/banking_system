package com.prathamesh.banking.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable Transaction class representing a bank transaction.
 * 
 * OOP Principles Applied:
 * 1. Encapsulation - All fields are private final, accessed via getters only
 * 2. Immutability - No setters, all fields final, thread-safe
 * 3. Value Object Pattern - Identified by its values, not identity
 * 
 * Design Patterns:
 * 1. Builder Pattern - Provides fluent API for constructing complex objects
 * 2. Immutable Object Pattern - Once created, state cannot change
 * 
 * Why Immutable?
 * - Thread-safe without synchronization
 * - Can be safely shared across multiple contexts
 * - Represents historical fact that shouldn't change
 * - Simplifies reasoning about transaction history
 */
public final class Transaction {
    
    // All fields are final - immutability guaranteed
    private final String transactionId;
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceBefore;
    private final BigDecimal balanceAfter;
    private final TransactionStatus status;
    private final String failureReason;
    private final String accountNumber;
    
    /**
     * Private constructor - only Builder can create instances.
     * OOP Principle: Controlled object creation through Builder
     *
     * @param builder the builder containing all field values
     */
    private Transaction(Builder builder) {
        this.transactionId = builder.transactionId;
        this.timestamp = builder.timestamp;
        this.type = builder.type;
        this.amount = builder.amount;
        this.balanceBefore = builder.balanceBefore;
        this.balanceAfter = builder.balanceAfter;
        this.status = builder.status;
        this.failureReason = builder.failureReason;
        this.accountNumber = builder.accountNumber;
    }
    
    // Getters only - no setters (immutability)
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }
    
    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    /**
     * Builder class for constructing Transaction instances.
     * 
     * Design Pattern: Builder Pattern
     * Why Builder?
     * - Transaction has 9 fields, constructor would be complex
     * - Some fields are optional (e.g., failureReason for successful transactions)
     * - Provides clear, readable construction syntax
     * - Allows validation before object creation
     * - Fluent API improves code readability
     * 
     * Example Usage:
     * Transaction tx = new Transaction.Builder("TXN-123", TransactionType.DEPOSIT, 
     *                                            new BigDecimal("100.00"), "ACC-001")
     *     .balanceBefore(new BigDecimal("500.00"))
     *     .balanceAfter(new BigDecimal("600.00"))
     *     .status(TransactionStatus.SUCCESS)
     *     .build();
     */
    public static class Builder {
        // Required fields
        private final String transactionId;
        private final TransactionType type;
        private final BigDecimal amount;
        private final String accountNumber;
        
        // Optional fields with defaults
        private LocalDateTime timestamp = LocalDateTime.now();
        private BigDecimal balanceBefore = BigDecimal.ZERO;
        private BigDecimal balanceAfter = BigDecimal.ZERO;
        private TransactionStatus status = TransactionStatus.SUCCESS;
        private String failureReason = null;
        
        /**
         * Constructs a Builder with required fields.
         * OOP Principle: Fail-fast - Required fields must be provided at construction
         *
         * @param transactionId unique transaction identifier
         * @param type transaction type (DEPOSIT, WITHDRAWAL, etc.)
         * @param amount transaction amount
         * @param accountNumber account number this transaction belongs to
         * @throws NullPointerException if any required field is null
         */
        public Builder(String transactionId, TransactionType type, BigDecimal amount, String accountNumber) {
            this.transactionId = Objects.requireNonNull(transactionId, "Transaction ID cannot be null");
            this.type = Objects.requireNonNull(type, "Transaction type cannot be null");
            this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
            this.accountNumber = Objects.requireNonNull(accountNumber, "Account number cannot be null");
        }
        
        /**
         * Sets the timestamp (optional, defaults to now).
         * Fluent API: Returns this for method chaining
         *
         * @param timestamp when the transaction occurred
         * @return this builder
         * @throws NullPointerException if timestamp is null
         */
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
            return this;
        }
        
        /**
         * Sets the balance before transaction (optional, defaults to ZERO).
         *
         * @param balanceBefore account balance before transaction
         * @return this builder
         * @throws NullPointerException if balanceBefore is null
         */
        public Builder balanceBefore(BigDecimal balanceBefore) {
            this.balanceBefore = Objects.requireNonNull(balanceBefore, "Balance before cannot be null");
            return this;
        }
        
        /**
         * Sets the balance after transaction (optional, defaults to ZERO).
         *
         * @param balanceAfter account balance after transaction
         * @return this builder
         * @throws NullPointerException if balanceAfter is null
         */
        public Builder balanceAfter(BigDecimal balanceAfter) {
            this.balanceAfter = Objects.requireNonNull(balanceAfter, "Balance after cannot be null");
            return this;
        }
        
        /**
         * Sets the transaction status (optional, defaults to SUCCESS).
         *
         * @param status SUCCESS or FAILED
         * @return this builder
         * @throws NullPointerException if status is null
         */
        public Builder status(TransactionStatus status) {
            this.status = Objects.requireNonNull(status, "Status cannot be null");
            return this;
        }
        
        /**
         * Sets the failure reason (optional, only for failed transactions).
         *
         * @param failureReason explanation of why transaction failed
         * @return this builder
         */
        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }
        
        /**
         * Builds the immutable Transaction instance.
         * OOP Principle: Once built, the transaction cannot be modified
         *
         * @return new immutable Transaction instance
         */
        public Transaction build() {
            return new Transaction(this);
        }
    }
    
    /**
     * Checks if this transaction was successful.
     * Convenience method for readability
     *
     * @return true if status is SUCCESS, false otherwise
     */
    public boolean isSuccessful() {
        return status == TransactionStatus.SUCCESS;
    }
    
    /**
     * Checks if this transaction failed.
     * Convenience method for readability
     *
     * @return true if status is FAILED, false otherwise
     */
    public boolean isFailed() {
        return status == TransactionStatus.FAILED;
    }
    
    /**
     * Value Object equality - based on transaction ID.
     * Two transactions are equal if they have the same transaction ID
     *
     * @param o object to compare
     * @return true if same transaction ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }
    
    /**
     * Hash code based on transaction ID for consistent hashing.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
    
    /**
     * String representation for debugging and logging.
     * OOP Principle: Good toString() aids debugging
     *
     * @return readable string representation
     */
    @Override
    public String toString() {
        return String.format(
            "Transaction{id='%s', timestamp=%s, type=%s, amount=$%s, status=%s, account='%s', balanceBefore=$%s, balanceAfter=$%s%s}",
            transactionId,
            timestamp,
            type,
            amount,
            status,
            accountNumber,
            balanceBefore,
            balanceAfter,
            failureReason != null ? ", failureReason='" + failureReason + "'" : ""
        );
    }
}
