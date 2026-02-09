package com.prathamesh.gradebook.exception;

import java.time.LocalDateTime;

/**
 * Base exception for all gradebook-related business errors.
 * 
 * This exception extends RuntimeException (unchecked exception) to avoid
 * cluttering client code with try-catch blocks for business rule violations.
 * 
 * All gradebook exceptions inherit from this base class, providing:
 * - Timestamp of when the error occurred
 * - Consistent error handling across the application
 * - Rich error messages with context
 * 
 * Design Pattern: Exception Hierarchy with common base
 * 
 * @author Prathamesh Kalantri
 */
public class GradebookException extends RuntimeException {
    
    private final LocalDateTime timestamp;
    
    /**
     * Constructs a new gradebook exception with the specified message.
     * Automatically captures the timestamp when the exception is created.
     * 
     * @param message The detail message explaining the error
     */
    public GradebookException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructs a new gradebook exception with the specified message and cause.
     * Automatically captures the timestamp when the exception is created.
     * 
     * @param message The detail message explaining the error
     * @param cause The underlying cause of this exception
     */
    public GradebookException(String message, Throwable cause) {
        super(message, cause);
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Gets the timestamp when this exception was created.
     * 
     * @return The timestamp of the error
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns a detailed string representation including timestamp.
     * 
     * @return Formatted error message with timestamp
     */
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", 
            timestamp, getClass().getSimpleName(), getMessage());
    }
}
