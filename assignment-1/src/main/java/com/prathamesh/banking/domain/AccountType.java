package com.prathamesh.banking.domain;

/**
 * Enum representing the types of bank accounts.
 * 
 * OOP Principle: Type Safety
 * - Using enum instead of String prevents invalid account types
 * - Compile-time validation ensures only valid types exist
 * 
 * Business Rules:
 * - CHECKING: No minimum balance, transaction fees after 10 free transactions
 * - SAVINGS: $100 minimum balance, 2% monthly interest, max 5 withdrawals/month
 */
public enum AccountType {
    /**
     * Checking account type
     * - No minimum balance requirement
     * - First 10 transactions per month are free
     * - $2.50 fee for each transaction after 10
     * - No interest earned
     */
    CHECKING,
    
    /**
     * Savings account type
     * - $100 minimum balance requirement
     * - 2% monthly interest on entire balance
     * - Maximum 5 withdrawals per month
     * - No transaction fees
     */
    SAVINGS
}
