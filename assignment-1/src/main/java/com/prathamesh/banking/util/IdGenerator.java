package com.prathamesh.banking.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for generating unique identifiers.
 * 
 * Design Pattern: Utility Class + Thread-Safe Singleton Counter
 * - Static methods only (no instantiation)
 * - Thread-safe using AtomicLong
 * - Generates unique IDs across the application lifecycle
 * 
 * OOP Principles:
 * 1. Encapsulation - Hides ID generation logic
 * 2. Single Responsibility - Only responsible for ID generation
 * 
 * Thread Safety:
 * - Uses AtomicLong for thread-safe counter increment
 * - Multiple threads can safely generate IDs concurrently
 * - No locks needed (lock-free algorithm)
 * 
 * ID Format:
 * - Account: "ACC-" + 8-digit zero-padded number (e.g., "ACC-00000001")
 * - Transaction: "TXN-" + 12-digit zero-padded number (e.g., "TXN-000000000001")
 */
public class IdGenerator {
    
    // Thread-safe counters using AtomicLong
    private static final AtomicLong accountCounter = new AtomicLong(0);
    private static final AtomicLong transactionCounter = new AtomicLong(0);
    
    // Format constants
    private static final String ACCOUNT_PREFIX = "ACC-";
    private static final String TRANSACTION_PREFIX = "TXN-";
    private static final int ACCOUNT_ID_LENGTH = 8;
    private static final int TRANSACTION_ID_LENGTH = 12;
    
    /**
     * Private constructor to prevent instantiation.
     * This is a static utility class.
     */
    private IdGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Generates a unique account ID.
     * 
     * Format: "ACC-" + 8-digit zero-padded number
     * Example: "ACC-00000001", "ACC-00000002", ...
     * 
     * Thread Safety: Uses AtomicLong for safe concurrent access
     * 
     * @return unique account ID
     */
    public static String generateAccountId() {
        long id = accountCounter.incrementAndGet();
        return ACCOUNT_PREFIX + String.format("%0" + ACCOUNT_ID_LENGTH + "d", id);
    }
    
    /**
     * Generates a unique transaction ID.
     * 
     * Format: "TXN-" + 12-digit zero-padded number
     * Example: "TXN-000000000001", "TXN-000000000002", ...
     * 
     * Thread Safety: Uses AtomicLong for safe concurrent access
     * 
     * @return unique transaction ID
     */
    public static String generateTransactionId() {
        long id = transactionCounter.incrementAndGet();
        return TRANSACTION_PREFIX + String.format("%0" + TRANSACTION_ID_LENGTH + "d", id);
    }
    
    /**
     * Resets all counters to zero.
     * FOR TESTING ONLY - allows deterministic ID generation in tests.
     * 
     * WARNING: Not safe to call in production code with concurrent access.
     */
    public static void resetCounters() {
        accountCounter.set(0);
        transactionCounter.set(0);
    }
    
    /**
     * Returns the current account counter value.
     * Package-private for testing.
     * 
     * @return current account counter
     */
    static long getAccountCounterValue() {
        return accountCounter.get();
    }
    
    /**
     * Returns the current transaction counter value.
     * Package-private for testing.
     * 
     * @return current transaction counter
     */
    static long getTransactionCounterValue() {
        return transactionCounter.get();
    }
}
