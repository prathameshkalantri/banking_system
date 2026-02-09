package com.prathamesh.gradebook.exception;

/**
 * Exception thrown when assignment grade values are invalid.
 * 
 * This exception is thrown when:
 * - Points earned is negative
 * - Points possible is zero or negative
 * - Points earned exceeds points possible
 * 
 * These validations ensure data integrity and prevent impossible
 * grade scenarios.
 * 
 * @author Prathamesh Kalantri
 */
public class InvalidGradeException extends GradebookException {
    
    private final Double pointsEarned;
    private final Double pointsPossible;
    
    /**
     * Constructs a new invalid grade exception with a custom message.
     * 
     * @param message The detail message explaining the validation failure
     */
    public InvalidGradeException(String message) {
        super(message);
        this.pointsEarned = null;
        this.pointsPossible = null;
    }
    
    /**
     * Constructs a new invalid grade exception with grade details.
     * 
     * @param pointsEarned The invalid points earned value
     * @param pointsPossible The points possible value
     * @param reason The reason why the grade is invalid
     */
    public InvalidGradeException(double pointsEarned, double pointsPossible, String reason) {
        super(String.format("Invalid grade: points earned = %.2f, points possible = %.2f. %s", 
            pointsEarned, pointsPossible, reason));
        this.pointsEarned = pointsEarned;
        this.pointsPossible = pointsPossible;
    }
    
    /**
     * Gets the points earned value that caused the error.
     * 
     * @return The points earned, or null if not applicable
     */
    public Double getPointsEarned() {
        return pointsEarned;
    }
    
    /**
     * Gets the points possible value.
     * 
     * @return The points possible, or null if not applicable
     */
    public Double getPointsPossible() {
        return pointsPossible;
    }
}
