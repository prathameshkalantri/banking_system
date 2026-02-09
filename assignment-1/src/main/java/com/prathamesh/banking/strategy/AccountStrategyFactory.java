package com.prathamesh.banking.strategy;

import com.prathamesh.banking.domain.AccountType;
import java.util.EnumMap;
import java.util.Map;

/**
 * Factory for creating AccountStrategy instances using Singleton pattern.
 * 
 * Design Pattern: Factory + Singleton
 * - Factory Method: getStrategy(AccountType) returns appropriate strategy
 * - Singleton: Pre-creates and caches strategy instances (stateless, reusable)
 * 
 * OOP Principles:
 * 1. Encapsulation - Hides strategy creation logic from clients
 * 2. Single Responsibility - Only responsible for strategy creation/lookup
 * 3. Open/Closed - New account types can be added without modifying clients
 * 
 * Performance:
 * - O(1) lookup using EnumMap
 * - Strategies created once at initialization (lazy in static block)
 * - Thread-safe: All strategies are stateless and immutable
 * 
 * Usage:
 * <pre>
 * AccountStrategy strategy = AccountStrategyFactory.getStrategy(AccountType.CHECKING);
 * strategy.validateWithdrawal(account, amount);
 * </pre>
 */
public class AccountStrategyFactory {
    
    // Strategy cache using EnumMap for O(1) lookup
    private static final Map<AccountType, AccountStrategy> STRATEGIES;
    
    // Static initialization block - creates strategies once
    static {
        STRATEGIES = new EnumMap<>(AccountType.class);
        STRATEGIES.put(AccountType.CHECKING, new CheckingAccountStrategy());
        STRATEGIES.put(AccountType.SAVINGS, new SavingsAccountStrategy());
    }
    
    /**
     * Private constructor to prevent instantiation.
     * This is a static utility class.
     * 
     * OOP Principle: Factory class should not be instantiated
     */
    private AccountStrategyFactory() {
        throw new UnsupportedOperationException("Factory class cannot be instantiated");
    }
    
    /**
     * Returns the appropriate strategy for the given account type.
     * 
     * Design Pattern: Factory Method
     * - Returns pre-created singleton instance
     * - O(1) lookup using EnumMap
     * - Thread-safe (strategies are stateless)
     * 
     * @param accountType the type of account (CHECKING or SAVINGS)
     * @return the strategy for this account type
     * @throws IllegalArgumentException if accountType is null or unknown
     */
    public static AccountStrategy getStrategy(AccountType accountType) {
        if (accountType == null) {
            throw new IllegalArgumentException("AccountType cannot be null");
        }
        
        AccountStrategy strategy = STRATEGIES.get(accountType);
        
        if (strategy == null) {
            // Should never happen if all enum values are mapped
            throw new IllegalArgumentException(
                "No strategy found for AccountType: " + accountType
            );
        }
        
        return strategy;
    }
    
    /**
     * Returns the number of registered strategies.
     * Package-private for testing.
     * 
     * @return count of strategies
     */
    static int getStrategyCount() {
        return STRATEGIES.size();
    }
}
