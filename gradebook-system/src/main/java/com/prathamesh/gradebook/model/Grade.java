package com.prathamesh.gradebook.model;

import java.util.Objects;

/**
 * Immutable value object representing a course grade.
 * <p>
 * A Grade combines a numerical percentage with its corresponding letter grade
 * and GPA points. This class ensures consistency between the percentage and
 * letter grade by automatically converting the percentage to the appropriate
 * letter grade.
 * </p>
 * 
 * <h3>Grade Components:</h3>
 * <ul>
 *   <li>Percentage: Numerical grade from 0.0 to 100.0+</li>
 *   <li>Letter Grade: Corresponding letter (A, B, C, D, or F)</li>
 *   <li>GPA Points: 4.0 scale value (4.0, 3.0, 2.0, 1.0, or 0.0)</li>
 * </ul>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public final class Grade {
    
    /** The numerical grade as a percentage */
    private final double percentage;
    
    /** The letter grade corresponding to the percentage */
    private final LetterGrade letterGrade;
    
    /**
     * Constructs a Grade from a percentage.
     * <p>
     * The letter grade is automatically determined from the percentage
     * using the standard grading scale.
     * </p>
     * 
     * @param percentage The numerical grade as a percentage (0.0 to 100.0+)
     * @throws IllegalArgumentException if percentage is negative
     */
    public Grade(double percentage) {
        if (percentage < 0.0) {
            throw new IllegalArgumentException("Percentage cannot be negative: " + percentage);
        }
        
        this.percentage = percentage;
        this.letterGrade = LetterGrade.fromPercentage(percentage);
    }
    
    /**
     * Constructs a Grade from a percentage and validates it matches the expected letter grade.
     * <p>
     * This constructor is useful when you want to ensure that the calculated grade
     * matches expectations.
     * </p>
     * 
     * @param percentage The numerical grade as a percentage
     * @param expectedLetterGrade The expected letter grade
     * @throws IllegalArgumentException if percentage is negative or doesn't match expected grade
     */
    public Grade(double percentage, LetterGrade expectedLetterGrade) {
        this(percentage);
        
        if (this.letterGrade != expectedLetterGrade) {
            throw new IllegalArgumentException(
                String.format("Percentage %.2f%% maps to %s, not %s",
                    percentage, this.letterGrade, expectedLetterGrade));
        }
    }
    
    /**
     * Returns the numerical percentage for this grade.
     * 
     * @return The percentage (0.0 to 100.0+)
     */
    public double getPercentage() {
        return percentage;
    }
    
    /**
     * Returns the letter grade.
     * 
     * @return The letter grade (A, B, C, D, or F)
     */
    public LetterGrade getLetterGrade() {
        return letterGrade;
    }
    
    /**
     * Returns the GPA points for this grade on a 4.0 scale.
     * 
     * @return The GPA points (4.0, 3.0, 2.0, 1.0, or 0.0)
     */
    public double getGpaPoints() {
        return letterGrade.getGpaPoints();
    }
    
    /**
     * Checks if this is a passing grade.
     * 
     * @return true if the grade is D or better (60% or higher), false otherwise
     */
    public boolean isPassing() {
        return letterGrade.isPassing();
    }
    
    /**
     * Checks if this grade qualifies for the Dean's List.
     * 
     * @return true if the grade is A or B, false otherwise
     */
    public boolean isDeansListQualified() {
        return letterGrade.isDeansListQualified();
    }
    
    /**
     * Checks if this grade indicates at-risk status.
     * 
     * @return true if the grade is D or F, false otherwise
     */
    public boolean isAtRisk() {
        return letterGrade.isAtRisk();
    }
    
    /**
     * Returns the grade description from the letter grade.
     * 
     * @return A description such as "Excellent", "Good", "Satisfactory", etc.
     */
    public String getDescription() {
        return letterGrade.getDescription();
    }
    
    /**
     * Compares this grade to another object for equality.
     * <p>
     * Two grades are equal if they have the same percentage.
     * Since letter grades are derived from percentages, equal percentages
     * will always have equal letter grades.
     * </p>
     * 
     * @param obj The object to compare with
     * @return true if the grades are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Grade grade = (Grade) obj;
        return Double.compare(grade.percentage, percentage) == 0;
    }
    
    /**
     * Returns a hash code value for this grade.
     * 
     * @return A hash code based on the percentage
     */
    @Override
    public int hashCode() {
        return Objects.hash(percentage);
    }
    
    /**
     * Returns a string representation of this grade.
     * <p>
     * Format: "XX.XX% (Letter Grade)"
     * </p>
     * 
     * @return A formatted string representation
     */
    @Override
    public String toString() {
        return String.format("%.2f%% (%s)", percentage, letterGrade.name());
    }
    
    /**
     * Returns a detailed string representation including GPA.
     * <p>
     * Format: "XX.XX% (Letter Grade) - X.X GPA - Description"
     * </p>
     * 
     * @return A detailed formatted string
     */
    public String toDetailedString() {
        return String.format("%.2f%% (%s) - %.1f GPA - %s",
            percentage, letterGrade.name(), getGpaPoints(), getDescription());
    }
}
