package com.prathamesh.gradebook.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for IdGenerator utility class.
 * 
 * Tests cover:
 * 1. Basic ID generation
 * 2. Uniqueness guarantees
 * 3. Format compliance
 * 4. Thread safety (concurrent generation)
 * 5. Counter reset (for testing)
 * 
 * @author Prathamesh Kalantri
 */
class IdGeneratorTest {
    
    @BeforeEach
    void setUp() {
        // Reset counter before each test for predictable IDs
        IdGenerator.resetCounter();
    }
    
    @AfterEach
    void tearDown() {
        // Reset counter after each test to avoid pollution
        IdGenerator.resetCounter();
    }
    
    // ==========================================
    // BASIC GENERATION TESTS
    // ==========================================
    
    @Test
    void testGenerateFirstStudentId() {
        String id = IdGenerator.generateStudentId();
        assertEquals("S-00000001", id);
    }
    
    @Test
    void testGenerateSequentialIds() {
        String id1 = IdGenerator.generateStudentId();
        String id2 = IdGenerator.generateStudentId();
        String id3 = IdGenerator.generateStudentId();
        
        assertEquals("S-00000001", id1);
        assertEquals("S-00000002", id2);
        assertEquals("S-00000003", id3);
    }
    
    @Test
    void testIdFormatCompliance() {
        String id = IdGenerator.generateStudentId();
        
        // Check format matches S-XXXXXXXX pattern
        assertTrue(id.matches("^S-\\d{8}$"), 
            "ID should match format S-XXXXXXXX: " + id);
    }
    
    @Test
    void testIdLength() {
        String id = IdGenerator.generateStudentId();
        assertEquals(10, id.length(), "ID should be 10 characters (S- + 8 digits)");
    }
    
    @Test
    void testIdPrefix() {
        String id = IdGenerator.generateStudentId();
        assertTrue(id.startsWith("S-"), "ID should start with 'S-'");
    }
    
    // ==========================================
    // UNIQUENESS TESTS
    // ==========================================
    
    @Test
    void testUniqueness() {
        String id1 = IdGenerator.generateStudentId();
        String id2 = IdGenerator.generateStudentId();
        
        assertNotEquals(id1, id2, "Each ID should be unique");
    }
    
    @Test
    void testUniquenessAcrossMany() {
        java.util.Set<String> ids = new java.util.HashSet<>();
        
        for (int i = 0; i < 1000; i++) {
            String id = IdGenerator.generateStudentId();
            assertTrue(ids.add(id), "Duplicate ID found: " + id);
        }
        
        assertEquals(1000, ids.size(), "All 1000 IDs should be unique");
    }
    
    // ============================================
    // COUNTER TESTS
    // ==========================================
    
    @Test
    void testGetCurrentCount() {
        assertEquals(0, IdGenerator.getCurrentCount(), "Initial count should be 0");
        
        IdGenerator.generateStudentId();
        assertEquals(1, IdGenerator.getCurrentCount(), "Count should be 1 after first ID");
        
        IdGenerator.generateStudentId();
        IdGenerator.generateStudentId();
        assertEquals(3, IdGenerator.getCurrentCount(), "Count should be 3 after three IDs");
    }
    
    @Test
    void testResetCounter() {
        // Generate some IDs
        IdGenerator.generateStudentId();
        IdGenerator.generateStudentId();
        assertEquals(2, IdGenerator.getCurrentCount());
        
        // Reset
        IdGenerator.resetCounter();
        assertEquals(0, IdGenerator.getCurrentCount(), "Count should be 0 after reset");
        
        // Next ID should be 1 again
        String id = IdGenerator.generateStudentId();
        assertEquals("S-00000001", id, "First ID after reset should be S-00000001");
    }
    
    // ==========================================
    // THREAD SAFETY TESTS
    // ==========================================
    
    @Test
    void testConcurrentGeneration() throws InterruptedException {
        final int THREADS = 10;
        final int IDS_PER_THREAD = 100;
        final java.util.Set<String> allIds = java.util.Collections.synchronizedSet(new java.util.HashSet<>());
        final java.util.List<Thread> threads = new java.util.ArrayList<>();
        
        // Create threads that generate IDs concurrently
        for (int i = 0; i < THREADS; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < IDS_PER_THREAD; j++) {
                    String id = IdGenerator.generateStudentId();
                    allIds.add(id);
                }
            });
            threads.add(thread);
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Verify all IDs are unique (no collisions despite concurrent access)
        int expectedTotal = THREADS * IDS_PER_THREAD;
        assertEquals(expectedTotal, allIds.size(), 
            "All " + expectedTotal + " IDs should be unique despite concurrent generation");
    }
    
    @Test
    void testThreadSafetyWithRaceCondition() throws InterruptedException {
        final int ITERATIONS = 1000;
        final java.util.Set<String> ids = java.util.Collections.synchronizedSet(new java.util.HashSet<>());
        
        // Two threads racing to generate IDs
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                ids.add(IdGenerator.generateStudentId());
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                ids.add(IdGenerator.generateStudentId());
            }
        });
        
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        
        // Should have exactly ITERATIONS * 2 unique IDs (no duplicates from race condition)
        assertEquals(ITERATIONS * 2, ids.size(), 
            "AtomicInteger should prevent duplicate IDs in race conditions");
    }
    
    // ==========================================
    // LARGE SCALE TESTS
    // ==========================================
    
    @Test
    void testLargeNumberGeneration() {
        // Generate many IDs to test format with larger numbers
        for (int i = 0; i < 10000; i++) {
            IdGenerator.generateStudentId();
        }
        
        String id = IdGenerator.generateStudentId();
        assertEquals("S-00010001", id, "ID 10,001 should have correct zero-padding");
    }
    
    @Test
    void testMillionthId() {
        IdGenerator.resetCounter();
        
        // Simulate generating up to ID 1,000,000
        for (int i = 0; i < 999999; i++) {
            IdGenerator.generateStudentId();
        }
        
        String millionthId = IdGenerator.generateStudentId();
        assertEquals("S-01000000", millionthId, "Millionth ID should still have 8 digits");
    }
    
    // ==========================================
    // UTILITY METHOD TESTS
    // ==========================================
    
    @Test
    void testCannotInstantiate() {
        // IdGenerator should throw when trying to instantiate
        try {
            java.lang.reflect.Constructor<IdGenerator> constructor = 
                IdGenerator.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Expected exception was not thrown");
        } catch (java.lang.reflect.InvocationTargetException e) {
            // The UnsupportedOperationException is wrapped in InvocationTargetException
            assertTrue(e.getCause() instanceof UnsupportedOperationException,
                "IdGenerator should throw UnsupportedOperationException");
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }
}
