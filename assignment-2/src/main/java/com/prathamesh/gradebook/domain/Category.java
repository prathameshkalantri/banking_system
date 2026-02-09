package com.prathamesh.gradebook.domain;

import java.util.EnumMap;
import java.util.Map;

/**
 * Enum representing grading categories with their default weights.
 * 
 * Each category has a default weight that contributes to the final course grade.
 * The weights must sum to 100% for a valid grading scheme.
 * 
 * This enum demonstrates:
 * - Enum with associated data (weights)
 * - Encapsulation of category business rules
 * - Type-safe category representation
 * 
 * @author Prathamesh Kalantri
 */
public enum Category {
    /**
     * Homework assignments - typically regular practice work
     * Default weight: 20%
     */
    HOMEWORK(20.0),
    
    /**
     * Quizzes - short assessments
     * Default weight: 20%
     */
    QUIZZES(20.0),
    
    /**
     * Midterm exam - mid-semester comprehensive exam
     * Default weight: 25%
     */
    MIDTERM(25.0),
    
    /**
     * Final exam - end-of-semester comprehensive exam
     * Default weight: 35%
     */
    FINAL_EXAM(35.0);
    
    private final double defaultWeight;
    
    /**
     * Constructor for Category enum.
     * 
     * @param defaultWeight The default weight percentage for this category
     */
    Category(double defaultWeight) {
        this.defaultWeight = defaultWeight;
    }
    
    /**
     * Gets the default weight for this category.
     * 
     * @return The default weight as a percentage (0-100)
     */
    public double getDefaultWeight() {
        return defaultWeight;
    }
    
    /**
     * Creates a map of default weights for all categories.
     * This is useful when initializing a course with standard weights.
     * 
     * @return Map of Category to default weight percentage
     */
    public static Map<Category, Double> getDefaultWeights() {
        Map<Category, Double> weights = new EnumMap<>(Category.class);
        for (Category category : Category.values()) {
            weights.put(category, category.getDefaultWeight());
        }
        return weights;
    }
    
    /**
     * Validates that a map of category weights sums to 100%.
     * Allows for small floating-point rounding errors (within 0.01).
     * 
     * @param weights Map of categories to their weights
     * @return true if weights sum to 100% (within tolerance), false otherwise
     */
    public static boolean validateWeights(Map<Category, Double> weights) {
        if (weights == null || weights.isEmpty()) {
            return false;
        }
        
        double sum = weights.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        
        // Allow for small floating-point rounding errors
        return Math.abs(sum - 100.0) < 0.01;
    }
    
    /**
     * Calculates the sum of all weights in the map.
     * 
     * @param weights Map of categories to their weights
     * @return Sum of all weights
     */
    public static double sumWeights(Map<Category, Double> weights) {
        if (weights == null || weights.isEmpty()) {
            return 0.0;
        }
        
        return weights.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
