package com.prathamesh.gradebook.model;

/**
 * Enumeration representing grading categories with their assigned weights.
 * <p>
 * Each category has a fixed weight that must be used in the final grade calculation.
 * The weights of all categories sum to 100% to ensure proper grade computation.
 * </p>
 * 
 * <h3>Category Weight Distribution:</h3>
 * <ul>
 *   <li>HOMEWORK: 20% - Regular assignments and problem sets</li>
 *   <li>QUIZZES: 20% - Short assessments throughout the term</li>
 *   <li>MIDTERM: 25% - Mid-term examination</li>
 *   <li>FINAL_EXAM: 35% - Comprehensive final examination</li>
 * </ul>
 * 
 * <p>
 * The category average is calculated as the arithmetic mean of all assignments
 * within that category. Missing assignments are scored as zero.
 * </p>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public enum Category {
    
    /**
     * Homework assignments category with 20% weight.
     * Includes regular assignments, problem sets, and take-home work.
     */
    HOMEWORK(0.20, "Homework"),
    
    /**
     * Quizzes category with 20% weight.
     * Includes in-class quizzes and short assessments.
     */
    QUIZZES(0.20, "Quizzes"),
    
    /**
     * Midterm examination category with 25% weight.
     * Typically covers material from the first half of the course.
     */
    MIDTERM(0.25, "Midterm Exam"),
    
    /**
     * Final examination category with 35% weight.
     * Comprehensive assessment covering all course material.
     */
    FINAL_EXAM(0.35, "Final Exam");
    
    /** The weight of this category as a decimal (0.0 to 1.0) */
    private final double weight;
    
    /** Human-readable display name for this category */
    private final String displayName;
    
    /**
     * Constructs a Category with the specified weight and display name.
     * 
     * @param weight The weight of this category (0.0 to 1.0)
     * @param displayName The human-readable name of this category
     */
    Category(double weight, String displayName) {
        this.weight = weight;
        this.displayName = displayName;
    }
    
    /**
     * Returns the weight of this category.
     * <p>
     * The weight represents the percentage (as a decimal) that this category
     * contributes to the final course grade. For example, 0.20 means 20%.
     * </p>
     * 
     * @return The weight as a decimal value between 0.0 and 1.0
     */
    public double getWeight() {
        return weight;
    }
    
    /**
     * Returns the weight of this category as a percentage.
     * <p>
     * This is a convenience method that returns the weight as a whole number
     * percentage (e.g., 20 instead of 0.20).
     * </p>
     * 
     * @return The weight as a percentage (0-100)
     */
    public double getWeightPercentage() {
        return weight * 100;
    }
    
    /**
     * Returns the human-readable display name of this category.
     * 
     * @return The display name (e.g., "Homework", "Final Exam")
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Validates that the sum of all category weights equals 100%.
     * <p>
     * This method ensures that the grading system is properly configured
     * and that all weights sum to exactly 1.0 (100%).
     * </p>
     * 
     * @return true if the sum of all weights equals 1.0, false otherwise
     */
    public static boolean validateWeights() {
        double sum = 0.0;
        for (Category category : values()) {
            sum += category.getWeight();
        }
        // Allow for small floating-point rounding errors
        return Math.abs(sum - 1.0) < 0.001;
    }
    
    /**
     * Returns the total weight of all categories as a percentage.
     * <p>
     * This should always return 100% if the system is properly configured.
     * </p>
     * 
     * @return The sum of all category weights as a percentage
     */
    public static double getTotalWeightPercentage() {
        double sum = 0.0;
        for (Category category : values()) {
            sum += category.getWeight();
        }
        return sum * 100;
    }
    
    /**
     * Returns a string representation of this category including its weight.
     * 
     * @return A string in the format "CategoryName (XX%)"
     */
    @Override
    public String toString() {
        return String.format("%s (%.0f%%)", displayName, getWeightPercentage());
    }
}
