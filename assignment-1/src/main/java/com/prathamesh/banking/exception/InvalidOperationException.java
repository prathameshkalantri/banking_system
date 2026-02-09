package com.prathamesh.banking.exception;

/**
 * Exception thrown for invalid banking operations that don't fit other specific exceptions.
 * 
 * OOP Principles Applied:
 * 1. Catch-all for validation errors - Handles various invalid operation scenarios
 * 2. Flexible messaging - Can describe any invalid operation with appropriate context
 * 
 * Usage Examples:
 * - Attempting to transfer to the same account
 * - Attempting to close an account with non-zero balance
 * - Invalid amount format (negative, too many decimals)
 * - Invalid account number format
 */
public class InvalidOperationException extends BankingException {
    
    /**
     * Constructs an InvalidOperationException with a descriptive message.
     *
     * @param message description of why the operation is invalid
     */
    public InvalidOperationException(String message) {
        super(message);
    }
    
    /**
     * Constructs an InvalidOperationException with a message and cause.
     *
     * @param message description of why the operation is invalid
     * @param cause the underlying cause of the invalid operation
     */
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
