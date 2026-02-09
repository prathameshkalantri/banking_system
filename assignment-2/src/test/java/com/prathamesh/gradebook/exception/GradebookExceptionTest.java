package com.prathamesh.gradebook.exception;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GradebookExceptionTest {
    
    @Test
    void testExceptionWithMessage() {
        String message = "Test error message";
        GradebookException exception = new GradebookException(message);
        
        assertEquals(message, exception.getMessage());
        assertNotNull(exception.getTimestamp());
        assertTrue(exception.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
    
    @Test
    void testExceptionWithMessageAndCause() {
        String message = "Test error message";
        Throwable cause = new IllegalArgumentException("Root cause");
        GradebookException exception = new GradebookException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertNotNull(exception.getTimestamp());
    }
    
    @Test
    void testTimestampIsSetOnCreation() {
        LocalDateTime before = LocalDateTime.now();
        GradebookException exception = new GradebookException("Test");
        LocalDateTime after = LocalDateTime.now();
        
        assertTrue(exception.getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(exception.getTimestamp().isBefore(after.plusSeconds(1)));
    }
    
    @Test
    void testToStringContainsClassNameAndMessage() {
        GradebookException exception = new GradebookException("Test message");
        String result = exception.toString();
        
        assertTrue(result.contains("GradebookException"));
        assertTrue(result.contains("Test message"));
        assertTrue(result.contains(exception.getTimestamp().toString().substring(0, 10))); // Date part
    }
    
    @Test
    void testExceptionIsUnchecked() {
        // GradebookException should extend RuntimeException (unchecked)
        assertTrue(RuntimeException.class.isAssignableFrom(GradebookException.class));
    }
}
