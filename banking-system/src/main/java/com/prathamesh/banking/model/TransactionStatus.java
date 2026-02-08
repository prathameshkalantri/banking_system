package com.prathamesh.banking.model;

/**
 * Represents the outcome status of a banking transaction.
 * 
 * <p>Each transaction attempt results in either SUCCESS or FAILED status.
 * Failed transactions include a failure reason to provide clear feedback
 * to users and support audit requirements.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public enum TransactionStatus {
    
    /**
     * Transaction completed successfully.
     * Indicates that all validation passed and the account balance was updated.
     */
    SUCCESS,
    
    /**
     * Transaction failed validation or execution.
     * Accompanied by a failure reason explaining why the transaction could not complete.
     */
    FAILED
}
