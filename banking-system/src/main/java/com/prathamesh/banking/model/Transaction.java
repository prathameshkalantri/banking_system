package com.prathamesh.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Immutable representation of a banking transaction.
 * 
 * <p>Records complete audit information for every transaction attempt including:
 * <ul>
 *   <li>Unique transaction identifier</li>
 *   <li>Precise timestamp</li>
 *   <li>Transaction type and amount</li>
 *   <li>Account balance before and after</li>
 *   <li>Success/failure status with reason</li>
 * </ul>
 * 
 * <p><b>Design Pattern:</b> This class uses the Builder pattern for flexible
 * construction while maintaining immutability for thread safety and audit integrity.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public final class Transaction {
    
    private final String transactionId;
    private final LocalDateTime timestamp;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceBefore;
    private final BigDecimal balanceAfter;
    private final TransactionStatus status;
    private final String failureReason;
    
    /**
     * Private constructor - use Builder to create instances.
     * 
     * @param builder the builder containing transaction data
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
    }
    
    // Getters
    
    /**
     * Returns the unique transaction identifier.
     * Format: TXN-{timestamp}-{random}
     * 
     * @return transaction ID
     */
    public String getTransactionId() {
        return transactionId;
    }
    
    /**
     * Returns the exact timestamp when the transaction was executed.
     * 
     * @return transaction timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns the type of transaction (DEPOSIT, WITHDRAWAL, or TRANSFER).
     * 
     * @return transaction type
     */
    public TransactionType getType() {
        return type;
    }
    
    /**
     * Returns the transaction amount.
     * Always positive; direction determined by transaction type.
     * 
     * @return transaction amount
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Returns the account balance before this transaction was applied.
     * 
     * @return balance before transaction
     */
    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }
    
    /**
     * Returns the account balance after this transaction was applied.
     * For failed transactions, equals balanceBefore.
     * 
     * @return balance after transaction
     */
    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    /**
     * Returns whether the transaction succeeded or failed.
     * 
     * @return transaction status
     */
    public TransactionStatus getStatus() {
        return status;
    }
    
    /**
     * Returns the reason for transaction failure.
     * Null for successful transactions.
     * 
     * @return failure reason or null
     */
    public String getFailureReason() {
        return failureReason;
    }
    
    /**
     * Checks if this transaction was successful.
     * 
     * @return true if status is SUCCESS, false otherwise
     */
    public boolean isSuccessful() {
        return status == TransactionStatus.SUCCESS;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("Transaction{id='%s', type=%s, amount=$%.2f, status=%s, timestamp=%s}",
                transactionId, type, amount, status, timestamp.format(formatter));
    }
    
    /**
     * Builder class for constructing Transaction instances.
     * 
     * <p>Provides a fluent API for setting transaction properties
     * while ensuring immutability of the final Transaction object.
     */
    public static class Builder {
        private String transactionId;
        private LocalDateTime timestamp;
        private TransactionType type;
        private BigDecimal amount;
        private BigDecimal balanceBefore;
        private BigDecimal balanceAfter;
        private TransactionStatus status;
        private String failureReason;
        
        /**
         * Creates a new Builder instance.
         */
        public Builder() {
            // Set defaults
            this.timestamp = LocalDateTime.now();
            this.status = TransactionStatus.SUCCESS;
        }
        
        /**
         * Sets the transaction ID.
         * 
         * @param transactionId unique identifier
         * @return this builder
         */
        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }
        
        /**
         * Sets the transaction timestamp.
         * 
         * @param timestamp when the transaction occurred
         * @return this builder
         */
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        /**
         * Sets the transaction type.
         * 
         * @param type DEPOSIT, WITHDRAWAL, or TRANSFER
         * @return this builder
         */
        public Builder type(TransactionType type) {
            this.type = type;
            return this;
        }
        
        /**
         * Sets the transaction amount.
         * 
         * @param amount transaction amount (must be positive)
         * @return this builder
         */
        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        
        /**
         * Sets the balance before the transaction.
         * 
         * @param balanceBefore account balance before transaction
         * @return this builder
         */
        public Builder balanceBefore(BigDecimal balanceBefore) {
            this.balanceBefore = balanceBefore;
            return this;
        }
        
        /**
         * Sets the balance after the transaction.
         * 
         * @param balanceAfter account balance after transaction
         * @return this builder
         */
        public Builder balanceAfter(BigDecimal balanceAfter) {
            this.balanceAfter = balanceAfter;
            return this;
        }
        
        /**
         * Sets the transaction status.
         * 
         * @param status SUCCESS or FAILED
         * @return this builder
         */
        public Builder status(TransactionStatus status) {
            this.status = status;
            return this;
        }
        
        /**
         * Sets the failure reason for failed transactions.
         * 
         * @param failureReason explanation of why transaction failed
         * @return this builder
         */
        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }
        
        /**
         * Builds and returns an immutable Transaction instance.
         * 
         * @return new Transaction instance
         * @throws IllegalStateException if required fields are missing
         */
        public Transaction build() {
            // Validate required fields
            Objects.requireNonNull(transactionId, "Transaction ID is required");
            Objects.requireNonNull(timestamp, "Timestamp is required");
            Objects.requireNonNull(type, "Transaction type is required");
            Objects.requireNonNull(amount, "Amount is required");
            Objects.requireNonNull(balanceBefore, "Balance before is required");
            Objects.requireNonNull(balanceAfter, "Balance after is required");
            Objects.requireNonNull(status, "Status is required");
            
            // Validate amount is positive
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Transaction amount must be positive");
            }
            
            // Validate failure reason is provided for failed transactions
            if (status == TransactionStatus.FAILED && 
                (failureReason == null || failureReason.trim().isEmpty())) {
                throw new IllegalStateException("Failure reason is required for failed transactions");
            }
            
            return new Transaction(this);
        }
    }
}
