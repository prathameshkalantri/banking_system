package com.prathamesh.gradebook.domain;

/**
 * Enum representing letter grades with their percentage ranges and GPA values.
 * 
 * This enum encapsulates the grading scale and provides methods to convert
 * between percentage scores and letter grades.
 * 
 * Grading Scale (as per assignment requirements):
 * - A: 90-100 (GPA: 4.0)
 * - B: 80-89  (GPA: 3.0)
 * - C: 70-79  (GPA: 2.0)
 * - D: 60-69  (GPA: 1.0)
 * - F: below 60 (GPA: 0.0)
 * 
 * This enum demonstrates:
 * - Enum with multiple associated values
 * - Business logic encapsulation (percentage to letter conversion)
 * - Type-safe grade representation
 * - Correct handling of boundary conditions
 * 
 * @author Prathamesh Kalantri
 */
public enum LetterGrade {
    /**
     * Grade A - Excellent performance
     * Range: [90.0, 100.0] (90 &lt;= grade &lt;= 100)
     * GPA Value: 4.0
     */
    A(90.0, 4.0),
    
    /**
     * Grade B - Good performance
     * Range: [80.0, 90.0) (80 &lt;= grade &lt; 90)
     * GPA Value: 3.0
     */
    B(80.0, 3.0),
    
    /**
     * Grade C - Satisfactory performance
     * Range: [70.0, 80.0) (70 &lt;= grade &lt; 80)
     * GPA Value: 2.0
     */
    C(70.0, 2.0),
    
    /**
     * Grade D - Passing performance
     * Range: [60.0, 70.0) (60 &lt;= grade &lt; 70)
     * GPA Value: 1.0
     */
    D(60.0, 1.0),
    
    /**
     * Grade F - Failing performance
     * Range: [0.0, 60.0) (grade &lt; 60)
     * GPA Value: 0.0
     */
    F(0.0, 0.0);
    
    private final double minThreshold;
    private final double gpaValue;
    
    /**
     * Constructor for LetterGrade enum.
     * 
     * @param minThreshold Minimum percentage threshold for this grade (inclusive)
     * @param gpaValue GPA points associated with this grade
     */
    LetterGrade(double minThreshold, double gpaValue) {
        this.minThreshold = minThreshold;
        this.gpaValue = gpaValue;
    }
    
    /**
     * Gets the minimum threshold percentage for this letter grade.
     * 
     * @return Minimum percentage threshold (inclusive)
     */
    public double getMinThreshold() {
        return minThreshold;
    }
    
    /**
     * Gets the GPA value for this letter grade.
     * 
     * @return GPA value (0.0 - 4.0 scale)
     */
    public double getGpaValue() {
        return gpaValue;
    }
    
    /**
     * Converts a percentage score to a letter grade.
     * 
     * Logic:
     * - Iterates from highest grade (A) to lowest (F)
     * - Returns first grade where percentage >= threshold
     * - This ensures no gaps and correct boundary handling
     * 
     * Edge cases handled:
     * - Exactly 90.0 → A (not B)
     * - Exactly 80.0 → B (not C)
     * - 89.999... → B (not A)
     * - Below 60.0 → F
     * - Exactly 100.0 → A
     * - Exactly 0.0 → F
     * 
     * @param percentage The percentage score to convert (0-100)
     * @return The corresponding LetterGrade
     * @throws IllegalArgumentException if percentage is outside valid range (0-100)
     */
    public static LetterGrade fromPercentage(double percentage) {
        // Validate input range
        if (percentage < 0.0 || percentage > 100.0) {
            throw new IllegalArgumentException(
                String.format("Percentage must be between 0 and 100, got: %.2f", percentage)
            );
        }
        
        // Iterate from highest grade to lowest
        // Return first grade where percentage meets threshold
        // This naturally handles all boundaries correctly:
        // - 90.0+ returns A
        // - 80.0-89.9... returns B
        // - 70.0-79.9... returns C
        // - 60.0-69.9... returns D
        // - <60.0 returns F
        for (LetterGrade grade : LetterGrade.values()) {
            if (percentage >= grade.minThreshold) {
                return grade;
            }
        }
        
        // Should never reach here due to F having threshold of 0.0
        // But added for completeness
        return F;
    }
    
    /**
     * Returns a formatted string representation of this grade.
     * Includes the letter and GPA value.
     * 
     * @return Formatted string (e.g., "A (GPA: 4.0)")
     */
    public String getDetailedDescription() {
        return String.format("%s (GPA: %.1f)", name(), gpaValue);
    }
    
    /**
     * Returns a description of the grading scale.
     * Useful for documentation and user information.
     * 
     * @return Multi-line string describing all grades and their ranges
     */
    public static String getGradingScaleDescription() {
        return "Grading Scale:\n" +
               "A: 90-100 (GPA: 4.0)\n" +
               "B: 80-89  (GPA: 3.0)\n" +
               "C: 70-79  (GPA: 2.0)\n" +
               "D: 60-69  (GPA: 1.0)\n" +
               "F: <60    (GPA: 0.0)";
    }
}
