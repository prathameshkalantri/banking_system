package com.prathamesh.banking.strategy;

import com.prathamesh.banking.domain.Account;
import java.math.BigDecimal;

/**
 * Strategy interface for account-specific business rules.
 * 
 * OOP Principles Applied:
 * 1. Abstraction - Defines contract without implementation details
 * 2. Polymorphism - Different account types implement this interface differently
 * 3. Dependency Inversion - High-level code depends on this abstraction, not concrete classes
 * 
 * Design Pattern: Strategy Pattern
 * Why Strategy?
 * - Separates business rules (strategy) from account state (Account class)
 * - Each account type has different rules (CHECKING vs SAVINGS)
 * - Easy to add new account types without modifying existing code (Open/Closed Principle)
 * - Single Responsibility - Account manages state, Strategy enforces rules
 * 
 * Benefits:
 * - Testability: Can test strategies independently
 * - Flexibility: Can change strategy at runtime if needed
 * - Maintainability: Business rules centralized in one place per account type
 * - Extensibility: New account types = new strategy class, no changes to Account
 */
public interface AccountStrategy {
    
    /**
     * Validates whether a withdrawal can be performed.
     * 
     * This method enforces account-specific business rules BEFORE withdrawal executes:
     * - CHECKING: Only checks sufficient funds (no other restrictions)
     * - SAVINGS: Checks sufficient funds, minimum balance $100, max 5 withdrawals/month
     * 
     * OOP Principle: Strategy encapsulates varying behavior
     * Account class calls this before executing withdrawal
     *
     * @param account the account attempting withdrawal
     * @param amount the amount to withdraw
     * @throws com.prathamesh.banking.exception.InsufficientFundsException if insufficient funds
     * @throws com.prathamesh.banking.exception.MinimumBalanceViolationException if would violate minimum balance
     * @throws com.prathamesh.banking.exception.WithdrawalLimitExceededException if exceeds withdrawal limit
     */
    void validateWithdrawal(Account account, BigDecimal amount);
    
    /**
     * Applies monthly adjustments to the account.
     * 
     * This method is called at the end of each month to:
     * - CHECKING: Calculate and apply transaction fees (if > 10 transactions)
     * - SAVINGS: Calculate and apply interest (2% of balance)
     * 
     * After applying adjustments, resets monthly counters.
     * 
     * OOP Principle: Each strategy knows its own monthly processing rules
     *
     * @param account the account to apply adjustments to
     */
    void applyMonthlyAdjustments(Account account);
    
    /**
     * Returns a human-readable description of business rules for this account type.
     * 
     * Useful for:
     * - Documentation
     * - User interfaces showing account rules
     * - Testing/debugging
     *
     * @return description of business rules
     */
    String getBusinessRulesDescription();
}
