package com.prathamesh.banking.exception;

import java.math.BigDecimal;

/**
 * Exception thrown when a SAVINGS account withdrawal would violate the minimum balance requirement.
 * 
 * OOP Principles Applied:
 * 1. Single Responsibility - Handles only minimum balance violations
 * 2. Encapsulation - Contains all context needed to understand the violation
 * 
 * Business Rule:
 * - SAVINGS accounts must maintain a minimum balance of $100
 * - Withdrawals that would result in balance < $100 are rejected
 */
public class MinimumBalanceViolationException extends BankingException {
    
    private final String accountNumber;
    private final BigDecimal minimumRequired;
    private final BigDecimal resultingBalance;
    
    /**
     * Constructs a MinimumBalanceViolationException with full context.
     *
     * @param accountNumber the account number attempting the withdrawal
     * @param minimumRequired the minimum balance required for this account type
     * @param resultingBalance the balance that would result from the withdrawal
     */
    public MinimumBalanceViolationException(String accountNumber, BigDecimal minimumRequired, BigDecimal resultingBalance) {
        super(String.format(
            "Minimum balance violation in account %s. Minimum required: $%s, Resulting balance would be: $%s",
            accountNumber, minimumRequired, resultingBalance
        ));
        this.accountNumber = accountNumber;
        this.minimumRequired = minimumRequired;
        this.resultingBalance = resultingBalance;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public BigDecimal getMinimumRequired() {
        return minimumRequired;
    }
    
    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }
}
