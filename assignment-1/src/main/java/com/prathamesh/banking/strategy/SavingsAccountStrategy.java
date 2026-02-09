package com.prathamesh.banking.strategy;

import com.prathamesh.banking.domain.Account;
import com.prathamesh.banking.exception.InsufficientFundsException;
import com.prathamesh.banking.exception.MinimumBalanceViolationException;
import com.prathamesh.banking.exception.WithdrawalLimitExceededException;
import com.prathamesh.banking.util.IdGenerator;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Strategy for SAVINGS account business rules.
 * 
 * Business Rules:
 * - $100.00 minimum balance required
 * - 2% monthly interest on entire balance
 * - Maximum 5 withdrawals per month
 * - No transaction fees
 * - Transfers do NOT count toward withdrawal limit
 * 
 * OOP Principles:
 * 1. Encapsulation - All SAVINGS-specific logic in one place
 * 2. Single Responsibility - Only handles SAVINGS account rules
 * 3. Stateless - No instance variables, can be shared (Singleton)
 * 
 * Design Pattern: Strategy + Singleton
 * - Implements Strategy interface
 * - Stateless so can be reused (one instance for all SAVINGS accounts)
 */
public class SavingsAccountStrategy implements AccountStrategy {
    
    // Business rule constants
    private static final BigDecimal MINIMUM_BALANCE = new BigDecimal("100.00");
    private static final BigDecimal MONTHLY_INTEREST_RATE = new BigDecimal("0.02"); // 2%
    private static final int MAX_WITHDRAWALS_PER_MONTH = 5;
    
    /**
     * Validates withdrawal for SAVINGS account.
     * 
     * SAVINGS Rules (all must be satisfied):
     * 1. Sufficient funds available
     * 2. Balance after withdrawal >= $100 minimum
     * 3. Monthly withdrawal count < 5
     * 
     * OOP Principle: Multiple validations enforcing business constraints
     * Edge Cases: Validates null parameters, negative amounts
     *
     * @param account the SAVINGS account
     * @param amount amount to withdraw
     * @throws IllegalArgumentException if account or amount is null, or amount is negative
     * @throws InsufficientFundsException if insufficient funds
     * @throws MinimumBalanceViolationException if withdrawal violates minimum balance
     * @throws WithdrawalLimitExceededException if monthly withdrawal limit reached
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
        
        // Check 1: Sufficient funds
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                account.getAccountNumber(),
                amount,
                account.getBalance()
            );
        }
        
        // Check 2: Minimum balance after withdrawal
        BigDecimal balanceAfterWithdrawal = account.getBalance().subtract(amount);
        if (balanceAfterWithdrawal.compareTo(MINIMUM_BALANCE) < 0) {
            throw new MinimumBalanceViolationException(
                account.getAccountNumber(),
                MINIMUM_BALANCE,
                balanceAfterWithdrawal
            );
        }
        
        // Check 3: Withdrawal limit not exceeded
        // NOTE: Transfers do NOT count toward withdrawal limit
        // Only direct withdrawals increment monthlyWithdrawalCount
        if (account.getMonthlyWithdrawalCount() >= MAX_WITHDRAWALS_PER_MONTH) {
            throw new WithdrawalLimitExceededException(
                account.getAccountNumber(),
                account.getMonthlyWithdrawalCount(),
                MAX_WITHDRAWALS_PER_MONTH
            );
        }
    }
    
    /**
     * Applies monthly adjustments for SAVINGS account.
     * 
     * Process:
     * 1. Calculate interest: balance × 2%
     * 2. Apply interest to account
     * 3. Reset monthly counters
     * 
     * Business Rule:
     * - 2% monthly interest on ENTIRE balance
     * - Interest rounds to 2 decimal places (HALF_UP)
     * - No fees for SAVINGS
     * 
     * OOP Principle: Strategy knows how to calculate and apply its own interest
     * Edge Cases: Validates null account, handles zero balance
     *
     * @param account the SAVINGS account
     * @throws IllegalArgumentException if account is null
     */
    @Override
    public void applyMonthlyAdjustments(Account account) {
        // Edge case: Null validation
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        
        // Calculate interest: balance × 2%
        BigDecimal interest = account.getBalance()
            .multiply(MONTHLY_INTEREST_RATE)
            .setScale(2, RoundingMode.HALF_UP);
        
        // Apply interest to account (only if interest > 0)
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            String transactionId = IdGenerator.generateTransactionId();
            account.applyInterest(interest, transactionId);
        }
        
        // Reset counters for next month
        account.resetMonthlyCounters();
    }
    
    /**
     * Returns description of SAVINGS account business rules.
     * 
     * @return human-readable rule description
     */
    @Override
    public String getBusinessRulesDescription() {
        return String.format(
            "SAVINGS Account Rules:\n" +
            "  - $%s minimum balance required\n" +
            "  - %.0f%% monthly interest on entire balance\n" +
            "  - Maximum %d withdrawals per month\n" +
            "  - Transfers do NOT count toward withdrawal limit\n" +
            "  - No transaction fees",
            MINIMUM_BALANCE,
            MONTHLY_INTEREST_RATE.multiply(new BigDecimal("100")),
            MAX_WITHDRAWALS_PER_MONTH
        );
    }
    
    /**
     * Returns the minimum balance requirement.
     * Package-private for testing.
     *
     * @return minimum balance
     */
    static BigDecimal getMinimumBalance() {
        return MINIMUM_BALANCE;
    }
    
    /**
     * Returns the monthly interest rate.
     * Package-private for testing.
     *
     * @return interest rate (e.g., 0.02 for 2%)
     */
    static BigDecimal getMonthlyInterestRate() {
        return MONTHLY_INTEREST_RATE;
    }
    
    /**
     * Returns the maximum withdrawals per month.
     * Package-private for testing.
     *
     * @return max withdrawal count
     */
    static int getMaxWithdrawalsPerMonth() {
        return MAX_WITHDRAWALS_PER_MONTH;
    }
}
