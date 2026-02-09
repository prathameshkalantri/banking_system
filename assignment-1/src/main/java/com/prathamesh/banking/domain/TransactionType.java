package com.prathamesh.banking.domain;

/**
 * Enum representing different types of transactions.
 * 
 * OOP Principle: Type Safety and Extensibility
 * - Using enum provides compile-time type checking
 * - Easy to add new transaction types without breaking existing code
 * - Each type has clear semantic meaning
 */
public enum TransactionType {
    /**
     * Deposit transaction - Adding money to an account
     */
    DEPOSIT,
    
    /**
     * Withdrawal transaction - Removing money from an account
     */
    WITHDRAWAL,
    
    /**
     * Transfer transaction - Moving money from one account to another
     * Note: A transfer creates two transaction records:
     * - TRANSFER (withdrawal) on source account
     * - DEPOSIT on destination account
     */
    TRANSFER,
    
    /**
     * Fee transaction - System-generated charge
     * Applied to CHECKING accounts after free transaction limit
     */
    FEE,
    
    /**
     * Interest transaction - System-generated credit
     * Applied to SAVINGS accounts monthly (2% of balance)
     */
    INTEREST
}
