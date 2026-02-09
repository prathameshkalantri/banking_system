package com.prathamesh.gradebook.exception;

/**
 * Exception thrown when credit hours are outside the valid range.
 * 
 * This exception is thrown when attempting to create a course with
 * credit hours that are not in the valid range of 1-6.
 * 
 * Business rule: Credit hours must be between 1 and 6 (inclusive)
 * 
 * Common scenarios:
 * - Creating a course with 0 credit hours
 * - Creating a course with more than 6 credit hours
 * - Creating a course with negative credit hours
 * 
 * @author Prathamesh Kalantri
 */
public class InvalidCreditHoursException extends GradebookException {
    
    private final int creditHours;
    private final int minAllowed;
    private final int maxAllowed;
    
    /**
     * Constructs a new invalid credit hours exception.
     * 
     * @param creditHours The invalid credit hours value
     */
    public InvalidCreditHoursException(int creditHours) {
        super(String.format("Invalid credit hours: %d. Credit hours must be between 1 and 6.", 
            creditHours));
        this.creditHours = creditHours;
        this.minAllowed = 1;
        this.maxAllowed = 6;
    }
    
    /**
     * Constructs a new invalid credit hours exception with custom range.
     * 
     * @param creditHours The invalid credit hours value
     * @param minAllowed The minimum allowed credit hours
     * @param maxAllowed The maximum allowed credit hours
     */
    public InvalidCreditHoursException(int creditHours, int minAllowed, int maxAllowed) {
        super(String.format("Invalid credit hours: %d. Credit hours must be between %d and %d.", 
            creditHours, minAllowed, maxAllowed));
        this.creditHours = creditHours;
        this.minAllowed = minAllowed;
        this.maxAllowed = maxAllowed;
    }
    
    /**
     * Gets the invalid credit hours value that was provided.
     * 
     * @return The credit hours value
     */
    public int getCreditHours() {
        return creditHours;
    }
    
    /**
     * Gets the minimum allowed credit hours.
     * 
     * @return The minimum credit hours
     */
    public int getMinAllowed() {
        return minAllowed;
    }
    
    /**
     * Gets the maximum allowed credit hours.
     * 
     * @return The maximum credit hours
     */
    public int getMaxAllowed() {
        return maxAllowed;
    }
}
