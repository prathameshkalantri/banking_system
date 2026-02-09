package com.prathamesh.gradebook.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique student identifiers.
 * 
 * Design Pattern: Utility Class + Thread-Safe Singleton Counter
 * - Static methods only (no instantiation)
 * - Thread-safe using AtomicInteger  
 * - Generates unique IDs across the application lifecycle
 * 
 * Benefits Over Manual ID Entry:
 * 1. Prevents duplicate IDs (guaranteed uniqueness)
 * 2. Enforces consistent format (S-XXXXXXXX)
 * 3. Thread-safe for concurrent access
 * 4. Better user experience (no manual bookkeeping)
 * 5. Reduces errors (typos, format mistakes)
 * 
 * Thread Safety:
 * - Uses AtomicInteger for thread-safe counter increment
 * - Multiple threads can safely generate IDs concurrently
 * - No locks needed (lock-free algorithm)
 * - Same approach as Assignment-1 IdGenerator
 * 
 * ID Format:
 * - Student: "S-" + 8-digit zero-padded number (e.g., "S-00000001")
 * - Matches validation pattern in Student class
 * 
 * Comparison with Manual IDs:
 * 
 * Manual (Current - Poor Practice):
 *   Student alice = new Student("S-12345678", "Alice");  // ❌ Error-prone
 *   Student bob = new Student("S-12345678", "Bob");      // ❌ Oops! Duplicate
 * 
 * Auto-Generated (Best Practice):
 *   String aliceId = IdGenerator.generateStudentId();    // ✅ "S-00000001"
 *   String bobId = IdGenerator.generateStudentId();      // ✅ "S-00000002" (unique)
 * 
 * @author Prathamesh Kalantri
 */
public final class IdGenerator {
    
    // Thread-safe counter using AtomicInteger (like Assignment-1)
    private static final AtomicInteger studentCounter = new AtomicInteger(0);
    
    // Format constants
    private static final String STUDENT_PREFIX = "S-";
    private static final int STUDENT_ID_LENGTH = 8;
    
    /**
     * Private constructor to prevent instantiation.
     * This is a static utility class.
     */
    private IdGenerator() {
        throw new UnsupportedOperationException("IdGenerator is a utility class and cannot be instantiated");
    }
    
    /**
     * Generates a unique student ID with guaranteed uniqueness.
     * 
     * Format: "S-" + 8-digit zero-padded number
     * Examples: "S-00000001", "S-00000002", "S-00000003", ...
     * 
     * Thread Safety: 
     * - Uses AtomicInteger for safe concurrent access
     * - Multiple threads can call simultaneously without conflicts
     * - No duplicate IDs even under high concurrent load
     * 
     * Why This is Better Than Manual IDs:
     * 1. Automatic uniqueness guarantee
     * 2. Consistent format (no typos)
     * 3. Sequential numbering (easier debugging)
     * 4. Thread-safe (production-ready)
     * 5. User doesn't need to track next ID
     * 
     * @return unique student ID in format S-XXXXXXXX
     */
    public static String generateStudentId() {
        int id = studentCounter.incrementAndGet();
        return STUDENT_PREFIX + String.format("%0" + STUDENT_ID_LENGTH + "d", id);
    }
    
    /**
     * Gets the current counter value without incrementing.
     * Useful for testing and diagnostics.
     * 
     * @return current counter value (number of IDs generated so far)
     */
    public static int getCurrentCount() {
        return studentCounter.get();
    }
    
    /**
     * Resets the counter to zero.
     * 
     * FOR TESTING ONLY - allows deterministic ID generation in tests.
     * Calling this in production can cause ID collisions.
     * 
     * Thread Safety Warning:
     * - This method is NOT safe to call with concurrent ID generation
     * - Only use in test setup/teardown when no other threads are active
     * 
     * @deprecated Only for testing - do not use in production code
     */
    @Deprecated
    public static void resetCounter() {
        studentCounter.set(0);
    }
}
