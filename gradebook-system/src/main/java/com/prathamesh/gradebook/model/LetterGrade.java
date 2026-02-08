package com.prathamesh.gradebook.model;

/**
 * Enumeration representing letter grades with their corresponding grade ranges and GPA points.
 * <p>
 * This enum defines the standard grading scale used to convert numerical grades
 * (percentages) into letter grades and GPA values. Each letter grade has:
 * </p>
 * <ul>
 *   <li>A minimum percentage threshold</li>
 *   <li>A maximum percentage threshold</li>
 *   <li>Corresponding GPA points (on a 4.0 scale)</li>
 * </ul>
 * 
 * <h3>Grading Scale:</h3>
 * <table border="1">
 *   <tr>
 *     <th>Letter Grade</th>
 *     <th>Percentage Range</th>
 *     <th>GPA Points</th>
 *   </tr>
 *   <tr><td>A</td><td>90-100%</td><td>4.0</td></tr>
 *   <tr><td>B</td><td>80-89%</td><td>3.0</td></tr>
 *   <tr><td>C</td><td>70-79%</td><td>2.0</td></tr>
 *   <tr><td>D</td><td>60-69%</td><td>1.0</td></tr>
 *   <tr><td>F</td><td>Below 60%</td><td>0.0</td></tr>
 * </table>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public enum LetterGrade {
    
    /**
     * Grade A: 90-100%, worth 4.0 GPA points.
     * Represents excellent performance.
     */
    A(90.0, 100.0, 4.0, "Excellent"),
    
    /**
     * Grade B: 80-89%, worth 3.0 GPA points.
     * Represents good performance.
     */
    B(80.0, 89.999, 3.0, "Good"),
    
    /**
     * Grade C: 70-79%, worth 2.0 GPA points.
     * Represents satisfactory performance.
     */
    C(70.0, 79.999, 2.0, "Satisfactory"),
    
    /**
     * Grade D: 60-69%, worth 1.0 GPA points.
     * Represents passing but poor performance.
     */
    D(60.0, 69.999, 1.0, "Poor"),
    
    /**
     * Grade F: Below 60%, worth 0.0 GPA points.
     * Represents failing performance.
     */
    F(0.0, 59.999, 0.0, "Failing");
    
    /** The minimum percentage required for this grade (inclusive) */
    private final double minPercentage;
    
    /** The maximum percentage for this grade (inclusive) */
    private final double maxPercentage;
    
    /** The GPA points awarded for this grade (on a 4.0 scale) */
    private final double gpaPoints;
    
    /** Human-readable description of this grade level */
    private final String description;
    
    /**
     * Constructs a LetterGrade with the specified parameters.
     * 
     * @param minPercentage The minimum percentage for this grade (inclusive)
     * @param maxPercentage The maximum percentage for this grade (inclusive)
     * @param gpaPoints The GPA points for this grade
     * @param description A human-readable description of the grade level
     */
    LetterGrade(double minPercentage, double maxPercentage, double gpaPoints, String description) {
        this.minPercentage = minPercentage;
        this.maxPercentage = maxPercentage;
        this.gpaPoints = gpaPoints;
        this.description = description;
    }
    
    /**
     * Returns the minimum percentage required for this grade.
     * 
     * @return The minimum percentage (0.0 to 100.0)
     */
    public double getMinPercentage() {
        return minPercentage;
    }
    
    /**
     * Returns the maximum percentage for this grade.
     * 
     * @return The maximum percentage (0.0 to 100.0)
     */
    public double getMaxPercentage() {
        return maxPercentage;
    }
    
    /**
     * Returns the GPA points for this grade.
     * 
     * @return The GPA points (0.0 to 4.0 scale)
     */
    public double getGpaPoints() {
        return gpaPoints;
    }
    
    /**
     * Returns the description of this grade level.
     * 
     * @return A description such as "Excellent", "Good", etc.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Converts a numerical percentage to a letter grade.
     * <p>
     * This method determines which letter grade corresponds to the given
     * percentage by checking if the percentage falls within each grade's range.
     * </p>
     * 
     * @param percentage The numerical grade as a percentage (0.0 to 100.0+)
     * @return The corresponding LetterGrade
     * @throws IllegalArgumentException if the percentage is negative
     */
    public static LetterGrade fromPercentage(double percentage) {
        if (percentage < 0.0) {
            throw new IllegalArgumentException("Percentage cannot be negative: " + percentage);
        }
        
        // Handle percentages over 100 as grade A
        if (percentage > 100.0) {
            percentage = 100.0;
        }
        
        // Find the letter grade that matches this percentage
        for (LetterGrade grade : values()) {
            if (percentage >= grade.getMinPercentage() && percentage <= grade.getMaxPercentage()) {
                return grade;
            }
        }
        
        // Should never reach here if ranges are properly defined
        return F;
    }
    
    /**
     * Checks if this grade represents a passing grade.
     * <p>
     * A passing grade is defined as D or better (60% or higher).
     * </p>
     * 
     * @return true if this is a passing grade (D or better), false otherwise
     */
    public boolean isPassing() {
        return this != F;
    }
    
    /**
     * Checks if this grade qualifies for the Dean's List.
     * <p>
     * Dean's List qualification typically requires a grade of A or B.
     * </p>
     * 
     * @return true if this grade is A or B, false otherwise
     */
    public boolean isDeansListQualified() {
        return this == A || this == B;
    }
    
    /**
     * Checks if this grade is at risk (D or F).
     * <p>
     * At-risk grades indicate students who may need academic support.
     * </p>
     * 
     * @return true if this grade is D or F, false otherwise
     */
    public boolean isAtRisk() {
        return this == D || this == F;
    }
    
    /**
     * Returns a string representation of this grade with its range.
     * 
     * @return A string in the format "A (90-100%)"
     */
    @Override
    public String toString() {
        if (this == F) {
            return String.format("%s (Below %.0f%%)", name(), D.getMinPercentage());
        }
        return String.format("%s (%.0f-%.0f%%)", name(), minPercentage, Math.min(maxPercentage, 100.0));
    }
}
