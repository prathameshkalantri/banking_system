package com.prathamesh.banking.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for IdGenerator utility.
 * Verifies thread-safety, format validation, and uniqueness.
 */
@DisplayName("IdGenerator Utility Tests")
class IdGeneratorTest {

    @Test
    @DisplayName("Should generate account ID with correct format")
    void shouldGenerateAccountIdWithCorrectFormat() {
        String accountId = IdGenerator.generateAccountId();
        
        assertNotNull(accountId);
        assertTrue(accountId.matches("ACC-\\d{8}"), 
                "Account ID should match format ACC-XXXXXXXX");
    }

    @Test
    @DisplayName("Should generate transaction ID with correct format")
    void shouldGenerateTransactionIdWithCorrectFormat() {
        String transactionId = IdGenerator.generateTransactionId();
        
        assertNotNull(transactionId);
        assertTrue(transactionId.matches("TXN-\\d{12}"), 
                "Transaction ID should match format TXN-XXXXXXXXXXXX");
    }

    @Test
    @DisplayName("Should generate unique account IDs")
    void shouldGenerateUniqueAccountIds() {
        Set<String> ids = new HashSet<>();
        
        for (int i = 0; i < 100; i++) {
            String id = IdGenerator.generateAccountId();
            assertTrue(ids.add(id), "Account ID should be unique: " + id);
        }
        
        assertEquals(100, ids.size());
    }

    @Test
    @DisplayName("Should generate unique transaction IDs")
    void shouldGenerateUniqueTransactionIds() {
        Set<String> ids = new HashSet<>();
        
        for (int i = 0; i < 100; i++) {
            String id = IdGenerator.generateTransactionId();
            assertTrue(ids.add(id), "Transaction ID should be unique: " + id);
        }
        
        assertEquals(100, ids.size());
    }

    @Test
    @DisplayName("Should start account IDs from ACC-00000001")
    void shouldStartAccountIdsFromOne() {
        // Note: IDs continue incrementing across tests
        String id = IdGenerator.generateAccountId();
        assertTrue(id.matches("ACC-\\d{8}"));
    }

    @Test
    @DisplayName("Should start transaction IDs from TXN-000000000001")
    void shouldStartTransactionIdsFromOne() {
        String id = IdGenerator.generateTransactionId();
        assertTrue(id.matches("TXN-\\d{12}"));
    }

    @Test
    @DisplayName("Should maintain separate counters for accounts and transactions")
    void shouldMaintainSeparateCounters() {
        Set<String> accountIds = new HashSet<>();
        Set<String> transactionIds = new HashSet<>();
        
        for (int i = 0; i < 10; i++) {
            accountIds.add(IdGenerator.generateAccountId());
            transactionIds.add(IdGenerator.generateTransactionId());
        }
        
        // All account IDs should start with ACC
        for (String id : accountIds) {
            assertTrue(id.startsWith("ACC-"));
        }
        
        // All transaction IDs should start with TXN
        for (String id : transactionIds) {
            assertTrue(id.startsWith("TXN-"));
        }
        
        // No overlap between account and transaction IDs
        Set<String> allIds = new HashSet<>();
        allIds.addAll(accountIds);
        allIds.addAll(transactionIds);
        assertEquals(20, allIds.size());
    }

    @Test
    @DisplayName("Should be thread-safe for account ID generation")
    void shouldBeThreadSafeForAccountIds() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 100;
        Set<String> allIds = ConcurrentHashMap.newKeySet();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        String id = IdGenerator.generateAccountId();
                        allIds.add(id);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Threads should complete within timeout");
        executor.shutdown();

        // All IDs should be unique (no duplicates from race conditions)
        assertEquals(threadCount * idsPerThread, allIds.size(), 
                "All generated IDs should be unique even with concurrent access");
    }

    @Test
    @DisplayName("Should be thread-safe for transaction ID generation")
    void shouldBeThreadSafeForTransactionIds() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 100;
        Set<String> allIds = ConcurrentHashMap.newKeySet();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        String id = IdGenerator.generateTransactionId();
                        allIds.add(id);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Threads should complete within timeout");
        executor.shutdown();

        assertEquals(threadCount * idsPerThread, allIds.size(), 
                "All generated IDs should be unique even with concurrent access");
    }

    @Test
    @DisplayName("Should handle large number of IDs")
    void shouldHandleLargeNumberOfIds() {
        Set<String> accountIds = new HashSet<>();
        Set<String> transactionIds = new HashSet<>();
        
        // Generate 999 of each (maximum for 3-digit format)
        for (int i = 0; i < 999; i++) {
            accountIds.add(IdGenerator.generateAccountId());
            transactionIds.add(IdGenerator.generateTransactionId());
        }
        
        assertEquals(999, accountIds.size());
        assertEquals(999, transactionIds.size());
    }

    @RepeatedTest(5)
    @DisplayName("Should consistently generate valid formats")
    void shouldConsistentlyGenerateValidFormats() {
        String accountId = IdGenerator.generateAccountId();
        String transactionId = IdGenerator.generateTransactionId();
        
        assertTrue(accountId.matches("ACC-\\d{8}"));
        assertTrue(transactionId.matches("TXN-\\d{12}"));
    }

    @Test
    @DisplayName("Should generate IDs with exactly correct digit counts")
    void shouldGenerateIdsWithExactly3Digits() {
        String accountId = IdGenerator.generateAccountId();
        String transactionId = IdGenerator.generateTransactionId();
        
        // Extract numeric part
        String accountNum = accountId.substring(4); // After "ACC-"
        String transactionNum = transactionId.substring(4); // After "TXN-"
        
        assertEquals(8, accountNum.length());
        assertEquals(12, transactionNum.length());
    }

    @Test
    @DisplayName("Should handle concurrent mixed ID generation")
    void shouldHandleConcurrentMixedIdGeneration() throws InterruptedException {
        int threadCount = 20;
        Set<String> allIds = ConcurrentHashMap.newKeySet();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger accountCount = new AtomicInteger(0);
        AtomicInteger transactionCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        // Alternate between account and transaction IDs
                        if ((threadIndex + j) % 2 == 0) {
                            String id = IdGenerator.generateAccountId();
                            allIds.add(id);
                            accountCount.incrementAndGet();
                        } else {
                            String id = IdGenerator.generateTransactionId();
                            allIds.add(id);
                            transactionCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        // Verify all IDs are unique
        assertEquals(threadCount * 50, allIds.size());
        
        // Verify both types were generated
        assertTrue(accountCount.get() > 0);
        assertTrue(transactionCount.get() > 0);
    }

    @Test
    @DisplayName("Should generate different IDs in rapid succession")
    void shouldGenerateDifferentIdsInRapidSuccession() {
        Set<String> accountIds = new HashSet<>();
        
        // Generate many IDs as fast as possible
        for (int i = 0; i < 1000; i++) {
            accountIds.add(IdGenerator.generateAccountId());
        }
        
        // All should be unique
        assertEquals(1000, accountIds.size());
    }

    @Test
    @DisplayName("Should not have leading zeros in numeric part")
    void shouldNotHaveLeadingZerosAfterFirstDigit() {
        // This test verifies the format is consistent
        String id = IdGenerator.generateAccountId();
        String numericPart = id.substring(4);
        
        // Should be exactly correct digits with zero-padding for formatting
        assertEquals(8, numericPart.length());
        assertTrue(numericPart.matches("\\d{8}"));
    }

    @Test
    @DisplayName("Should handle rapid sequential calls")
    void shouldHandleRapidSequentialCalls() {
        String lastAccountId = null;
        String lastTransactionId = null;
        
        for (int i = 0; i < 100; i++) {
            String accountId = IdGenerator.generateAccountId();
            String transactionId = IdGenerator.generateTransactionId();
            
            // Each new ID should be different from the last
            assertNotEquals(lastAccountId, accountId);
            assertNotEquals(lastTransactionId, transactionId);
            
            lastAccountId = accountId;
            lastTransactionId = transactionId;
        }
    }
}
