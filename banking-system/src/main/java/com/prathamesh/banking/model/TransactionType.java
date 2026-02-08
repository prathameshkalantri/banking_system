package com.prathamesh.banking.model;

/**
 * Defines the types of financial transactions supported by the banking system.
 * 
 * <p>All transactions are recorded in the account's transaction history
 * with complete audit information including amounts, timestamps, and status.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public enum TransactionType {
    
    /**
     * Deposit transaction - adds funds to an account.
     * 
     * <p><b>Validation Rules:</b>
     * <ul>
     *   <li>Amount must be positive</li>
     *   <li>No upper limit on deposit amount</li>
     *   <li>Always succeeds if validation passes</li>
     * </ul>
     */
    DEPOSIT,
    
    /**
     * Withdrawal transaction - removes funds from an account.
     * 
     * <p><b>Validation Rules:</b>
     * <ul>
     *   <li>Amount must be positive</li>
     *   <li>Sufficient funds must be available</li>
     *   <li>Must not violate minimum balance (for SAVINGS accounts)</li>
     *   <li>Must not exceed monthly withdrawal limit (for SAVINGS accounts)</li>
     * </ul>
     */
    WITHDRAWAL,
    
    /**
     * Transfer transaction - moves funds between two accounts.
     * 
     * <p><b>Validation Rules:</b>
     * <ul>
     *   <li>Amount must be positive</li>
     *   <li>Source account must have sufficient funds</li>
     *   <li>Source and destination accounts must be different</li>
     *   <li>Both accounts must exist and be active</li>
     *   <li>All withdrawal rules apply to source account</li>
     * </ul>
     */
    TRANSFER
}
