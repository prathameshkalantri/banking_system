package com.prathamesh.banking.domain;

/**
 * Enum representing the status of a transaction.
 * 
 * OOP Principle: Clear State Representation
 * - Transactions can either succeed or fail
 * - Failed transactions include a reason for failure
 * - All transaction attempts are recorded for audit trail
 */
public enum TransactionStatus {
    /**
     * Transaction completed successfully
     * Balance was modified as expected
     */
    SUCCESS,
    
    /**
     * Transaction failed due to validation or business rule violation
     * Balance was NOT modified
     * Failure reason is stored in Transaction object
     */
    FAILED
}
