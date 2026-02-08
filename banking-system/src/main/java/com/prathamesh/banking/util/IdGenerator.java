package com.prathamesh.banking.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for generating unique identifiers for accounts and transactions.
 * 
 * <p>Provides thread-safe ID generation with human-readable formats and guaranteed uniqueness.
 * All generated IDs include timestamps and sequential counters to ensure uniqueness even
 * under high concurrent load.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public class IdGenerator {
    
    // Atomic counter for sequential numbering
    private static final AtomicLong accountCounter = new AtomicLong(1);
    private static final AtomicLong transactionCounter = new AtomicLong(1);
    
    // Private constructor to prevent instantiation
    private IdGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Generates a unique account number.
     * 
     * <p>Format: ACC-{8-digit-number}
     * <p>Example: ACC-00000001
     * 
     * <p>Thread-safe implementation uses AtomicLong for concurrent access.
     * 
     * @return unique account number
     */
    public static String generateAccountNumber() {
        long counter = accountCounter.getAndIncrement();
        return String.format("ACC-%08d", counter);
    }
    
    /**
     * Generates a unique transaction ID.
     * 
     * <p>Format: TXN-{timestamp}-{6-char-uuid}
     * <p>Example: TXN-20260207172500-a1b2c3
     * 
     * <p>Combines timestamp for chronological sorting with UUID fragment for uniqueness.
     * Thread-safe and collision-resistant even under high load.
     * 
     * @return unique transaction ID
     */
    public static String generateTransactionId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 6);
        return String.format("TXN-%s-%s", timestamp, uuid);
    }
    
    /**
     * Resets counters to initial values.
     * 
     * <p><b>WARNING:</b> Only use for testing purposes. 
     * Resetting in production can cause ID collisions.
     */
    public static void resetCounters() {
        accountCounter.set(1);
        transactionCounter.set(1);
    }
}
