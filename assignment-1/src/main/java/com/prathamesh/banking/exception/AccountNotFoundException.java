package com.prathamesh.banking.exception;

/**
 * Exception thrown when attempting to access an account that doesn't exist.
 * 
 * OOP Principles Applied:
 * 1. Fail Fast - Immediately indicate when account lookup fails
 * 2. Clear Error Communication - Indicates exactly which account was not found
 * 
 * Usage:
 * - Thrown by Bank service when account number doesn't exist in repository
 * - Helps identify typos or invalid account references
 */
public class AccountNotFoundException extends BankingException {
    
    private final String accountNumber;
    
    /**
     * Constructs an AccountNotFoundException for the specified account number.
     *
     * @param accountNumber the account number that was not found
     */
    public AccountNotFoundException(String accountNumber) {
        super(String.format("Account not found: %s", accountNumber));
        this.accountNumber = accountNumber;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
}
