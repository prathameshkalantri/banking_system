package com.prathamesh.banking.strategy;

import com.prathamesh.banking.domain.Account;
import com.prathamesh.banking.exception.InsufficientFundsException;
import com.prathamesh.banking.util.IdGenerator;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Strategy for CHECKING account business rules.
 * 
 * Business Rules:
 * - No minimum balance requirement
 * - First 10 transactions per month are free
 * - $2.50 fee for each transaction after 10
 * - No interest earned
 * - No withdrawal limits
 * 
 * OOP Principles:
 * 1. Encapsulation - All CHECKING-specific logic in one place
 * 2. Single Responsibility - Only handles CHECKING account rules
 * 3. Stateless - No instance variables, can be shared (Singleton)
 * 
 * Design Pattern: Strategy + Singleton
 * - Implements Strategy interface
 * - Stateless so can be reused (one instance for all CHECKING accounts)
 */
public class CheckingAccountStrategy implements AccountStrategy {
    
    // Business rule constants
    private static final BigDecimal TRANSACTION_FEE = new BigDecimal("2.50");
    private static final int FREE_TRANSACTIONS_PER_MONTH = 10;
    
    /**
     * Validates withdrawal for CHECKING account.
     * 
     * CHECKING Rules:
     * - Only requirement: Sufficient funds
     * - No minimum balance
     * - No withdrawal limits
     * 
     * OOP Principle: Simple validation - CHECKING has fewest restrictions
     * Edge Cases: Validates null parameters, negative amounts
     *
     * @param account the CHECKING account
     * @param amount amount to withdraw
     * @throws IllegalArgumentException if account or amount is null, or amount is negative
     * @throws InsufficientFundsException if insufficient funds
     */
    @Override
    public void validateWithdrawal(Account account, BigDecimal amount) {
        // Edge case: Null validation
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        
        // Edge case: Negative amount validation
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Withdrawal amount cannot be negative: " + amount);
        }
        
        // Edge case: Zero amount is technically valid but pointless - let it through
        
        // CHECKING only requirement: sufficient funds
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                account.getAccountNumber(),
                amount,
                account.getBalance()
            );
        }
        
        // No minimum balance check - CHECKING can go to zero
        // No withdrawal limit check - unlimited withdrawals
    }
    
    /**
     * Applies monthly adjustments for CHECKING account.
     * 
     * Process:
     * 1. Calculate fee: (transactionCount - 10) × $2.50
     * 2. Apply fee if transactionCount > 10
     * 3. Reset monthly counters
     * 
     * Business Rule:
     * - First 10 transactions free
     * - $2.50 per transaction after 10
     * - Fee charged even if results in negative balance
     * 
     * OOP Principle: Strategy knows how to calculate and apply its own fees
     * Edge Cases: Validates null account parameter
     *
     * @param account the CHECKING account
     * @throws IllegalArgumentException if account is null
     */
    @Override
    public void applyMonthlyAdjustments(Account account) {
        // Edge case: Null validation
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        
        int transactionCount = account.getMonthlyTransactionCount();
        
        // Calculate fee: only charge for transactions beyond free limit
        if (transactionCount > FREE_TRANSACTIONS_PER_MONTH) {
            int chargeableTransactions = transactionCount - FREE_TRANSACTIONS_PER_MONTH;
            BigDecimal totalFee = TRANSACTION_FEE
                .multiply(new BigDecimal(chargeableTransactions))
                .setScale(2, RoundingMode.HALF_UP);
            
            // Apply fee to account (fee is charged even if balance goes negative)
            String transactionId = IdGenerator.generateTransactionId();
            account.applyFee(totalFee, transactionId);
        }
        
        // Reset counters for next month
        account.resetMonthlyCounters();
    }
    
    /**
     * Returns description of CHECKING account business rules.
     * 
     * @return human-readable rule description
     */
    @Override
    public String getBusinessRulesDescription() {
        return String.format(
            "CHECKING Account Rules:\n" +
            "  - No minimum balance requirement\n" +
            "  - First %d transactions per month are free\n" +
            "  - $%s fee for each transaction after %d\n" +
            "  - No interest earned\n" +
            "  - Unlimited withdrawals",
            FREE_TRANSACTIONS_PER_MONTH,
            TRANSACTION_FEE,
            FREE_TRANSACTIONS_PER_MONTH
        );
    }
    
    /**
     * Returns the transaction fee amount.
     * Package-private for testing.
     *
     * @return transaction fee
     */
    static BigDecimal getTransactionFee() {
        return TRANSACTION_FEE;
    }
    
    /**
     * Returns the number of free transactions per month.
     * Package-private for testing.
     *
     * @return free transaction count
     */
    static int getFreeTransactionsPerMonth() {
        return FREE_TRANSACTIONS_PER_MONTH;
    }
}
