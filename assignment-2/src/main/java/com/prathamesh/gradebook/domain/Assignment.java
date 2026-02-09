package com.prathamesh.gradebook.domain;

import com.prathamesh.gradebook.exception.InvalidGradeException;

import java.util.Objects;

/**
 * Represents an immutable assignment with a grade in a specific category.
 * 
 * This is a Value Object in the domain model - two assignments with the same
 * values are considered equal, even if they are different object instances.
 * 
 * Immutability guarantees:
 * - All fields are final
 * - No setters provided
 * - No mutable objects stored or returned
 * 
 * Validation rules:
 * - Name cannot be null or blank
 * - Points earned must be >= 0
 * - Points possible must be > 0
 * - Points earned cannot exceed points possible
 * - Category cannot be null
 * 
 * Design Pattern: Value Object (immutable domain object)
 * 
 * @author Prathamesh Kalantri
 */
public final class Assignment {
    
    private final String name;
    private final double pointsEarned;
    private final double pointsPossible;
    private final Category category;
    
    /**
     * Creates a new immutable assignment.
     * 
     * @param name The name of the assignment (cannot be null or blank)
     * @param pointsEarned The points earned on this assignment (must be >= 0)
     * @param pointsPossible The total points possible (must be > 0)
     * @param category The category this assignment belongs to (cannot be null)
     * @throws IllegalArgumentException if name is null or blank, or category is null
     * @throws InvalidGradeException if grade values are invalid
     */
    public Assignment(String name, double pointsEarned, double pointsPossible, Category category) {
        // Validate name
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Assignment name cannot be null or blank");
        }
        
        // Validate category
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        // Validate points possible
        if (pointsPossible <= 0) {
            throw new InvalidGradeException(pointsEarned, pointsPossible, 
                "Points possible must be greater than zero");
        }
        
        // Validate points earned is non-negative
        if (pointsEarned < 0) {
            throw new InvalidGradeException(pointsEarned, pointsPossible, 
                "Points earned cannot be negative");
        }
        
        // Validate points earned does not exceed points possible
        if (pointsEarned > pointsPossible) {
            throw new InvalidGradeException(pointsEarned, pointsPossible, 
                "Points earned cannot exceed points possible");
        }
        
        this.name = name.trim();
        this.pointsEarned = pointsEarned;
        this.pointsPossible = pointsPossible;
        this.category = category;
    }
    
    /**
     * Gets the assignment name.
     * 
     * @return The assignment name (never null or blank)
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the points earned on this assignment.
     * 
     * @return The points earned (always >= 0 and <= pointsPossible)
     */
    public double getPointsEarned() {
        return pointsEarned;
    }
    
    /**
     * Gets the total points possible for this assignment.
     * 
     * @return The points possible (always > 0)
     */
    public double getPointsPossible() {
        return pointsPossible;
    }
    
    /**
     * Gets the category this assignment belongs to.
     * 
     * @return The category (never null)
     */
    public Category getCategory() {
        return category;
    }
    
    /**
     * Calculates the percentage grade for this assignment.
     * 
     * @return The percentage (0.0 to 100.0)
     */
    public double getPercentage() {
        return (pointsEarned / pointsPossible) * 100.0;
    }
    
    /**
     * Checks if this assignment has a perfect score.
     * 
     * @return true if points earned equals points possible
     */
    public boolean isPerfectScore() {
        return Math.abs(pointsEarned - pointsPossible) < 0.01;
    }
    
    /**
     * Gets a formatted string representation of the grade.
     * Example: "45.00/50.00 (90.00%)"
     * 
     * @return Formatted grade string
     */
    public String getFormattedGrade() {
        return String.format("%.2f/%.2f (%.2f%%)", 
            pointsEarned, pointsPossible, getPercentage());
    }
    
    /**
     * Value objects are equal if all their fields are equal.
     * 
     * @param o The object to compare with
     * @return true if all fields are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Double.compare(that.pointsEarned, pointsEarned) == 0 &&
               Double.compare(that.pointsPossible, pointsPossible) == 0 &&
               Objects.equals(name, that.name) &&
               category == that.category;
    }
    
    /**
     * Hash code based on all fields for proper use in collections.
     * 
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, pointsEarned, pointsPossible, category);
    }
    
    /**
     * Returns a string representation of the assignment.
     * Example: "Assignment{name='Homework 1', grade=45.00/50.00 (90.00%), category=HOMEWORK}"
     * 
     * @return String representation
     */
    @Override
    public String toString() {
        return String.format("Assignment{name='%s', grade=%s, category=%s}",
            name, getFormattedGrade(), category);
    }
}
