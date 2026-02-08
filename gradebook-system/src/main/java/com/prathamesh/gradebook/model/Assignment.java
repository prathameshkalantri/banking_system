package com.prathamesh.gradebook.model;

import java.util.Objects;

/**
 * Immutable value object representing a graded assignment.
 * <p>
 * An Assignment represents a single graded item within a course, belonging to
 * a specific grading category (HOMEWORK, QUIZZES, MIDTERM, or FINAL_EXAM).
 * Each assignment tracks the points earned by a student and the maximum points possible.
 * </p>
 * 
 * <h3>Invariants:</h3>
 * <ul>
 *   <li>Name cannot be null or empty</li>
 *   <li>Points earned must be non-negative</li>
 *   <li>Points possible must be positive</li>
 *   <li>Points earned cannot exceed points possible</li>
 *   <li>Category cannot be null</li>
 * </ul>
 * 
 * <h3>Immutability:</h3>
 * <p>
 * This class is immutable - once created, its state cannot be changed.
 * This ensures thread-safety and prevents accidental modification.
 * </p>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public final class Assignment {
    
    /** The name of this assignment */
    private final String name;
    
    /** The points earned by the student on this assignment */
    private final double pointsEarned;
    
    /** The maximum points possible for this assignment */
    private final double pointsPossible;
    
    /** The grading category to which this assignment belongs */
    private final Category category;
    
    /**
     * Constructs a new Assignment with the specified parameters.
     * <p>
     * This constructor performs comprehensive validation to ensure all
     * business rules are satisfied.
     * </p>
     * 
     * @param name The name of the assignment (must not be null or empty)
     * @param pointsEarned The points earned (must be non-negative and <= pointsPossible)
     * @param pointsPossible The maximum points possible (must be positive)
     * @param category The grading category (must not be null)
     * @throws IllegalArgumentException if any validation rule is violated
     */
    public Assignment(String name, double pointsEarned, double pointsPossible, Category category) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Assignment name cannot be null or empty");
        }
        
        // Validate points possible
        if (pointsPossible <= 0) {
            throw new IllegalArgumentException(
                "Points possible must be positive, got: " + pointsPossible);
        }
        
        // Validate points earned
        if (pointsEarned < 0) {
            throw new IllegalArgumentException(
                "Points earned cannot be negative, got: " + pointsEarned);
        }
        
        if (pointsEarned > pointsPossible) {
            throw new IllegalArgumentException(
                String.format("Points earned (%.2f) cannot exceed points possible (%.2f)", 
                    pointsEarned, pointsPossible));
        }
        
        // Validate category
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        this.name = name.trim();
        this.pointsEarned = pointsEarned;
        this.pointsPossible = pointsPossible;
        this.category = category;
    }
    
    /**
     * Returns the name of this assignment.
     * 
     * @return The assignment name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the points earned on this assignment.
     * 
     * @return The points earned (non-negative)
     */
    public double getPointsEarned() {
        return pointsEarned;
    }
    
    /**
     * Returns the maximum points possible for this assignment.
     * 
     * @return The points possible (positive)
     */
    public double getPointsPossible() {
        return pointsPossible;
    }
    
    /**
     * Returns the grading category of this assignment.
     * 
     * @return The category (HOMEWORK, QUIZZES, MIDTERM, or FINAL_EXAM)
     */
    public Category getCategory() {
        return category;
    }
    
    /**
     * Calculates and returns the percentage score for this assignment.
     * <p>
     * The percentage is calculated as (pointsEarned / pointsPossible) * 100.
     * For example, earning 85 out of 100 points yields 85.0%.
     * </p>
     * 
     * @return The percentage score (0.0 to 100.0)
     */
    public double getPercentage() {
        return (pointsEarned / pointsPossible) * 100.0;
    }
    
    /**
     * Checks if this assignment was completed successfully.
     * <p>
     * An assignment is considered complete if any points were earned.
     * Missing assignments (0 points earned) are not complete.
     * </p>
     * 
     * @return true if pointsEarned > 0, false otherwise
     */
    public boolean isComplete() {
        return pointsEarned > 0;
    }
    
    /**
     * Checks if this is a perfect score assignment.
     * <p>
     * An assignment has a perfect score if points earned equals points possible.
     * </p>
     * 
     * @return true if the student earned all possible points, false otherwise
     */
    public boolean isPerfectScore() {
        return Math.abs(pointsEarned - pointsPossible) < 0.001;
    }
    
    /**
     * Compares this assignment to another object for equality.
     * <p>
     * Two assignments are considered equal if they have the same name,
     * points earned, points possible, and category.
     * </p>
     * 
     * @param obj The object to compare with
     * @return true if the assignments are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Assignment that = (Assignment) obj;
        return Double.compare(that.pointsEarned, pointsEarned) == 0 &&
               Double.compare(that.pointsPossible, pointsPossible) == 0 &&
               name.equals(that.name) &&
               category == that.category;
    }
    
    /**
     * Returns a hash code value for this assignment.
     * 
     * @return A hash code based on all fields
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, pointsEarned, pointsPossible, category);
    }
    
    /**
     * Returns a string representation of this assignment.
     * <p>
     * Format: "AssignmentName [Category]: XX.X/XX.X points (XX.X%)"
     * </p>
     * 
     * @return A formatted string representation
     */
    @Override
    public String toString() {
        return String.format("%s [%s]: %.1f/%.1f points (%.1f%%)",
            name, category.name(), pointsEarned, pointsPossible, getPercentage());
    }
}
