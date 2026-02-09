package com.prathamesh.banking.exception;

/**
 * Base exception for all banking-related exceptions.
 * 
 * OOP Principles Applied:
 * 1. Inheritance - All banking exceptions extend this base class
 * 2. Polymorphism - Can catch this base exception or specific subtypes
 * 3. Abstraction - Provides common exception behavior for all banking errors
 * 
 * Design Decision:
 * - Extends RuntimeException (unchecked) so clients aren't forced to catch
 * - Business logic exceptions shouldn't require explicit handling everywhere
 * - Allows exceptions to propagate to appropriate handling layers
 */
public class BankingException extends RuntimeException {
    
    /**
     * Constructs a new banking exception with the specified detail message.
     *
     * @param message the detail message explaining the exception
     */
    public BankingException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new banking exception with the specified detail message and cause.
     *
     * @param message the detail message explaining the exception
     * @param cause the cause of this exception (can be null)
     */
    public BankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
