package com.prathamesh.gradebook.exception;

import com.prathamesh.gradebook.domain.Category;
import java.util.Map;

/**
 * Exception thrown when category weights are invalid.
 * 
 * This exception is thrown when category weights do not sum to 100%,
 * which is required for proper grade calculation. It provides context
 * about the actual sum of weights.
 * 
 * Common scenarios:
 * - Creating a course with weights that don't sum to 100%
 * - Attempting to use custom weights that are misconfigured
 * 
 * Business rule: Category weights must sum to exactly 100% (with 0.01% tolerance)
 * 
 * @author Prathamesh Kalantri
 */
public class InvalidWeightException extends GradebookException {
    
    private final double actualSum;
    private final double expectedSum;
    
    /**
     * Constructs a new invalid weight exception.
     * 
     * @param actualSum The actual sum of weights provided
     */
    public InvalidWeightException(double actualSum) {
        super(String.format("Invalid category weights: weights must sum to 100%%, got %.2f%%", 
            actualSum));
        this.actualSum = actualSum;
        this.expectedSum = 100.0;
    }
    
    /**
     * Constructs a new invalid weight exception with weight details.
     * 
     * @param weights The map of category weights that failed validation
     * @param actualSum The actual sum of weights
     */
    public InvalidWeightException(Map<Category, Double> weights, double actualSum) {
        super(String.format("Invalid category weights: weights must sum to 100%%, got %.2f%%. " +
            "Provided weights: %s", actualSum, formatWeights(weights)));
        this.actualSum = actualSum;
        this.expectedSum = 100.0;
    }
    
    /**
     * Gets the actual sum of weights that was provided.
     * 
     * @return The actual weight sum
     */
    public double getActualSum() {
        return actualSum;
    }
    
    /**
     * Gets the expected sum of weights (always 100%).
     * 
     * @return The expected weight sum
     */
    public double getExpectedSum() {
        return expectedSum;
    }
    
    /**
     * Formats the weights map for error message.
     */
    private static String formatWeights(Map<Category, Double> weights) {
        if (weights == null || weights.isEmpty()) {
            return "null or empty";
        }
        StringBuilder sb = new StringBuilder("{");
        weights.forEach((category, weight) -> 
            sb.append(category).append("=").append(String.format("%.1f%%", weight)).append(", "));
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2); // Remove trailing comma
        }
        sb.append("}");
        return sb.toString();
    }
}
