package com.prathamesh.banking.service;

import com.prathamesh.banking.model.Account;
import com.prathamesh.banking.model.AccountType;

import java.math.BigDecimal;

/**
 * Validates banking transactions before execution.
 * 
 * <p>Centralizes all transaction validation logic to ensure consistency
 * and business rule enforcement across the banking system. Each validation
 * method returns null if valid, or an error message explaining the failure.
 * 
 * <p><b>Validation Categories:</b>
 * <ul>
 *   <li>Amount validation (positive, non-zero)</li>
 *   <li>Account state validation (exists, sufficient funds)</li>
 *   <li>Business rule validation (minimum balance, withdrawal limits)</li>
 *   <li>Transfer validation (different accounts, both valid)</li>
 * </ul>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public class TransactionValidator {
    
    /**
     * Validates a deposit amount.
     * 
     * @param amount the amount to deposit
     * @return null if valid, error message if invalid
     */
    public String validateDeposit(BigDecimal amount) {
        if (amount == null) {
            return "Deposit amount cannot be null";
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Deposit amount must be positive";
        }
        
        return null; // Valid
    }
    
    /**
     * Validates a withdrawal from an account.
     * 
     * <p>Checks:
     * <ul>
     *   <li>Amount is positive</li>
     *   <li>Sufficient balance available</li>
     *   <li>SAVINGS: Maintains minimum balance of $100</li>
     *   <li>SAVINGS: Does not exceed 5 withdrawals per month</li>
     * </ul>
     * 
     * @param account the account to withdraw from
     * @param amount the amount to withdraw
     * @return null if valid, error message if invalid
     */
    public String validateWithdrawal(Account account, BigDecimal amount) {
        if (account == null) {
            return "Account cannot be null";
        }
        
        if (amount == null) {
            return "Withdrawal amount cannot be null";
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Withdrawal amount must be positive";
        }
        
        // Use Account's built-in validation
        return account.canWithdraw(amount);
    }
    
    /**
     * Validates a transfer between two accounts.
     * 
     * <p>Checks:
     * <ul>
     *   <li>Both accounts exist and are not null</li>
     *   <li>Accounts are different (cannot transfer to self)</li>
     *   <li>Amount is positive</li>
     *   <li>Source account has sufficient funds</li>
     *   <li>All withdrawal rules apply to source account</li>
     * </ul>
     * 
     * @param fromAccount source account
     * @param toAccount destination account
     * @param amount amount to transfer
     * @return null if valid, error message if invalid
     */
    public String validateTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        if (fromAccount == null) {
            return "Source account cannot be null";
        }
        
        if (toAccount == null) {
            return "Destination account cannot be null";
        }
        
        if (fromAccount.equals(toAccount)) {
            return "Cannot transfer to the same account";
        }
        
        if (amount == null) {
            return "Transfer amount cannot be null";
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Transfer amount must be positive";
        }
        
        // Validate withdrawal from source account
        return fromAccount.canWithdraw(amount);
    }
    
    /**
     * Validates that an account can be closed.
     * 
     * <p>Account can only be closed if balance is exactly zero.
     * 
     * @param account the account to close
     * @return null if valid, error message if invalid
     */
    public String validateAccountClosure(Account account) {
        if (account == null) {
            return "Account cannot be null";
        }
        
        if (!account.canClose()) {
            return String.format("Account cannot be closed with non-zero balance: $%.2f", 
                               account.getBalance());
        }
        
        return null; // Valid
    }
    
    /**
     * Validates initial deposit for account opening.
     * 
     * <p>Rules:
     * <ul>
     *   <li>CHECKING: Any non-negative amount allowed</li>
     *   <li>SAVINGS: Minimum $100.00 required</li>
     * </ul>
     * 
     * @param accountType type of account being opened
     * @param initialDeposit initial deposit amount
     * @return null if valid, error message if invalid
     */
    public String validateInitialDeposit(AccountType accountType, BigDecimal initialDeposit) {
        if (accountType == null) {
            return "Account type cannot be null";
        }
        
        if (initialDeposit == null) {
            return "Initial deposit cannot be null";
        }
        
        if (initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            return "Initial deposit cannot be negative";
        }
        
        // SAVINGS accounts require minimum balance
        if (accountType == AccountType.SAVINGS) {
            BigDecimal minimumBalance = new BigDecimal("100.00");
            if (initialDeposit.compareTo(minimumBalance) < 0) {
                return String.format("SAVINGS account requires minimum initial deposit of $%.2f", 
                                   minimumBalance);
            }
        }
        
        return null; // Valid
    }
    
    /**
     * Validates customer name.
     * 
     * @param name customer name
     * @return null if valid, error message if invalid
     */
    public String validateCustomerName(String name) {
        if (name == null) {
            return "Customer name cannot be null";
        }
        
        if (name.trim().isEmpty()) {
            return "Customer name cannot be empty";
        }
        
        if (name.trim().length() < 2) {
            return "Customer name must be at least 2 characters";
        }
        
        return null; // Valid
    }
}
