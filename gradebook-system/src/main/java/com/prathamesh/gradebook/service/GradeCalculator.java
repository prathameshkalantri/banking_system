package com.prathamesh.gradebook.service;

import com.prathamesh.gradebook.model.*;

import java.util.List;
import java.util.Map;

/**
 * Service class for calculating grades from assignments and courses.
 * <p>
 * This calculator handles the complex logic of computing category averages,
 * weighted final grades, and converting percentages to letter grades.
 * All calculation methods are stateless and thread-safe.
 * </p>
 * 
 * <h3>Calculation Features:</h3>
 * <ul>
 *   <li>Category average computation</li>
 *   <li>Weighted grade calculation with proper normalization</li>
 *   <li>Empty category handling (exclusion from calculation)</li>
 *   <li>Missing assignment treatment (scored as zero)</li>
 * </ul>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public class GradeCalculator {
    
    /**
     * Constructs a new GradeCalculator.
     * <p>
     * This class is stateless and can be reused for multiple calculations.
     * </p>
     */
    public GradeCalculator() {
        // No initialization needed - stateless calculator
    }
    
    /**
     * Calculates the average percentage for a specific category in a course.
     * <p>
     * The category average is computed as the arithmetic mean of all
     * assignment percentages within that category. If no assignments exist
     * in the category, returns 0.0.
     * </p>
     * 
     * <h3>Algorithm:</h3>
     * <ol>
     *   <li>Filter assignments by category</li>
     *   <li>Calculate percentage for each assignment</li>
     *   <li>Compute arithmetic mean</li>
     * </ol>
     * 
     * @param course The course containing the assignments
     * @param category The category to calculate average for
     * @return The category average percentage (0.0 to 100.0)
     * @throws IllegalArgumentException if course or category is null
     */
    public double calculateCategoryAverage(Course course, Category category) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        List<Assignment> categoryAssignments = course.getAssignmentsByCategory(category);
        
        // Return 0 if no assignments in this category
        if (categoryAssignments.isEmpty()) {
            return 0.0;
        }
        
        // Calculate sum of all assignment percentages
        double sum = categoryAssignments.stream()
            .mapToDouble(Assignment::getPercentage)
            .sum();
        
        // Return arithmetic mean
        return sum / categoryAssignments.size();
    }
    
    /**
     * Calculates the final course grade using weighted category averages.
     * <p>
     * The final grade is computed as a weighted sum of category averages.
     * Categories without assignments are excluded from the calculation, and
     * remaining weights are proportionally adjusted.
     * </p>
     * 
     * <h3>Algorithm:</h3>
     * <ol>
     *   <li>For each category with assignments:</li>
     *   <li>  - Calculate category average</li>
     *   <li>  - Multiply by category weight</li>
     *   <li>  - Add to weighted sum</li>
     *   <li>  - Track total weight used</li>
     *   <li>Normalize by total weight used</li>
     *   <li>Multiply by 100 to get percentage</li>
     * </ol>
     * 
     * <h3>Example:</h3>
     * <pre>
     * Homework (20%): 85% average
     * Quizzes (20%): No assignments (excluded)
     * Midterm (25%): 90% average
     * Final (35%): 88% average
     * 
     * Weighted Sum = (85 * 0.20) + (90 * 0.25) + (88 * 0.35) = 70.3
     * Total Weight = 0.20 + 0.25 + 0.35 = 0.80
     * Final Grade = (70.3 / 0.80) * 100 = 87.875%
     * </pre>
     * 
     * @param course The course to calculate grade for
     * @return The final course percentage (0.0 to 100.0)
     * @throws IllegalArgumentException if course is null
     */
    public double calculateFinalPercentage(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        double weightedSum = 0.0;
        double totalWeightUsed = 0.0;
        
        // Process each category
        for (Category category : Category.values()) {
            // Check if this category has assignments
            if (course.hasAssignments(category)) {
                double categoryAvg = calculateCategoryAverage(course, category);
                double weight = category.getWeight();
                
                weightedSum += categoryAvg * weight;
                totalWeightUsed += weight;
            }
        }
        
        // If no assignments at all, return 0
        if (totalWeightUsed == 0.0) {
            return 0.0;
        }
        
        // Normalize by actual weight used (already returns percentage 0-100)
        return weightedSum / totalWeightUsed;
    }
    
    /**
     * Calculates and returns the final course grade as a Grade object.
     * <p>
     * This is a convenience method that combines percentage calculation
     * with letter grade conversion.
     * </p>
     * 
     * @param course The course to calculate grade for
     * @return A Grade object with percentage and letter grade
     * @throws IllegalArgumentException if course is null
     */
    public Grade calculateFinalGrade(Course course) {
        double percentage = calculateFinalPercentage(course);
        return new Grade(percentage);
    }
    
    /**
     * Returns a map of all category averages for a course.
     * <p>
     * Only includes categories that have at least one assignment.
     * This is useful for generating detailed grade reports.
     * </p>
     * 
     * @param course The course to analyze
     * @return A map of Category to average percentage
     * @throws IllegalArgumentException if course is null
     */
    public Map<Category, Double> calculateAllCategoryAverages(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        return course.getCategoryAverages();
    }
    
    /**
     * Calculates the contribution of each category to the final grade.
     * <p>
     * Returns a map showing how many percentage points each category
     * contributes to the final grade (after weight adjustment).
     * </p>
     * 
     * @param course The course to analyze
     * @return A map of Category to weighted contribution percentage
     * @throws IllegalArgumentException if course is null
     */
    public Map<Category, Double> calculateCategoryContributions(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        Map<Category, Double> contributions = new java.util.LinkedHashMap<>();
        double totalWeightUsed = 0.0;
        
        // Calculate total weight used
        for (Category category : Category.values()) {
            if (course.hasAssignments(category)) {
                totalWeightUsed += category.getWeight();
            }
        }
        
        // Calculate contribution for each category
        for (Category category : Category.values()) {
            if (course.hasAssignments(category)) {
                double categoryAvg = calculateCategoryAverage(course, category);
                double weight = category.getWeight();
                double normalizedWeight = weight / totalWeightUsed;
                double contribution = categoryAvg * normalizedWeight;
                
                contributions.put(category, contribution);
            }
        }
        
        return contributions;
    }
    
    /**
     * Validates that a course has the minimum required assignments.
     * <p>
     * This method can be used to check if a course is ready for grading.
     * Returns true if the course has at least one assignment in any category.
     * </p>
     * 
     * @param course The course to validate
     * @return true if the course has assignments, false otherwise
     * @throws IllegalArgumentException if course is null
     */
    public boolean hasMinimumRequiredAssignments(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        return course.hasAssignments();
    }
    
    /**
     * Calculates statistics for a category's assignments.
     * <p>
     * Returns an array with [average, min, max, count] for the category.
     * If the category has no assignments, returns [0, 0, 0, 0].
     * </p>
     * 
     * @param course The course containing the assignments
     * @param category The category to analyze
     * @return Array of [average, min, max, count]
     * @throws IllegalArgumentException if course or category is null
     */
    public double[] calculateCategoryStatistics(Course course, Category category) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        List<Assignment> assignments = course.getAssignmentsByCategory(category);
        
        if (assignments.isEmpty()) {
            return new double[]{0.0, 0.0, 0.0, 0.0};
        }
        
        double sum = 0.0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (Assignment assignment : assignments) {
            double percentage = assignment.getPercentage();
            sum += percentage;
            min = Math.min(min, percentage);
            max = Math.max(max, percentage);
        }
        
        double average = sum / assignments.size();
        double count = assignments.size();
        
        return new double[]{average, min, max, count};
    }
}
