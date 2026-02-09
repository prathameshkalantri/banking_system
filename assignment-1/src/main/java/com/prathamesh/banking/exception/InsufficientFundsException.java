package com.prathamesh.banking.exception;

import java.math.BigDecimal;

/**
 * Exception thrown when a withdrawal or transfer cannot be completed due to insufficient funds.
 * 
 * OOP Principles Applied:
 * 1. Encapsulation - Exception contains all relevant context data
 * 2. Immutability - All fields are final and set at construction
 * 3. Rich Domain Exception - Carries business context, not just a message
 * 
 * Business Rule:
 * - Withdrawals and transfers require sufficient balance
 * - Exception provides detailed information for error reporting
 */
public class InsufficientFundsException extends BankingException {
    
    private final String accountNumber;
    private final BigDecimal requestedAmount;
    private final BigDecimal availableBalance;
    
    /**
     * Constructs an InsufficientFundsException with full context.
     *
     * @param accountNumber the account number attempting the operation
     * @param requestedAmount the amount requested to withdraw/transfer
     * @param availableBalance the current available balance
     */
    public InsufficientFundsException(String accountNumber, BigDecimal requestedAmount, BigDecimal availableBalance) {
        super(String.format(
            "Insufficient funds in account %s. Requested: $%s, Available: $%s",
            accountNumber, requestedAmount, availableBalance
        ));
        this.accountNumber = accountNumber;
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }
    
    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
}
