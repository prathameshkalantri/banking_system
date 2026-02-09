package com.prathamesh.banking.exception;

/**
 * Exception thrown when a SAVINGS account exceeds its monthly withdrawal limit.
 * 
 * OOP Principles Applied:
 * 1. Clear Business Rule Enforcement - Exception name clearly states the violation
 * 2. Context Preservation - Contains all data needed to explain the error
 * 
 * Business Rule:
 * - SAVINGS accounts are limited to 5 withdrawals per month
 * - Withdrawal attempts beyond this limit are rejected
 * - Counter resets at the beginning of each month
 */
public class WithdrawalLimitExceededException extends BankingException {
    
    private final String accountNumber;
    private final int currentWithdrawalCount;
    private final int maxAllowed;
    
    /**
     * Constructs a WithdrawalLimitExceededException with full context.
     *
     * @param accountNumber the account number attempting the withdrawal
     * @param currentWithdrawalCount the current number of withdrawals this month
     * @param maxAllowed the maximum number of withdrawals allowed per month
     */
    public WithdrawalLimitExceededException(String accountNumber, int currentWithdrawalCount, int maxAllowed) {
        super(String.format(
            "Withdrawal limit exceeded for account %s. Current withdrawals: %d, Maximum allowed: %d per month",
            accountNumber, currentWithdrawalCount, maxAllowed
        ));
        this.accountNumber = accountNumber;
        this.currentWithdrawalCount = currentWithdrawalCount;
        this.maxAllowed = maxAllowed;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public int getCurrentWithdrawalCount() {
        return currentWithdrawalCount;
    }
    
    public int getMaxAllowed() {
        return maxAllowed;
    }
}
