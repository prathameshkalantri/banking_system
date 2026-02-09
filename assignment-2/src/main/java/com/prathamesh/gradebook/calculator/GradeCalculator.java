package com.prathamesh.gradebook.calculator;

import com.prathamesh.gradebook.domain.Assignment;
import com.prathamesh.gradebook.domain.Category;
import com.prathamesh.gradebook.domain.Course;
import com.prathamesh.gradebook.domain.LetterGrade;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculator for computing course grades based on category-weighted averages.
 * 
 * Implements the grading logic:
 * 1. Calculate average for each category
 * 2. Apply category weights
 * 3. Adjust weights for missing categories (redistribute proportionally)
 * 4. Sum weighted averages to get final grade
 * 
 * Key Feature: Weight Adjustment
 * If a course has 40% of weight in categories with no assignments,
 * that 40% is redistributed proportionally among categories that DO have assignments.
 * 
 * Example:
 * - HOMEWORK (20%) has assignments → average 90%
 * - QUIZZES (20%) has NO assignments → weight redistributed
 * - MIDTERM (25%) has assignments → average 80%
 * - FINAL (35%) has assignments → average 85%
 * 
 * Adjusted weights: HOMEWORK=25%, MIDTERM=31.25%, FINAL=43.75% (sum=100%)
 * Final grade: (90*0.25) + (80*0.3125) + (85*0.4375) = 84.69%
 * 
 * @author Prathamesh Kalantri
 */
public class GradeCalculator {
    
    /**
     * Calculates the grade for a course.
     * 
     * @param course The course to calculate grade for
     * @return CourseGrade object with numeric grade, letter grade, and category averages
     * @throws IllegalArgumentException if course is null
     */
    public CourseGrade calculateCourseGrade(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        // Calculate average for each category
        Map<Category, Double> categoryAverages = calculateCategoryAverages(course);
        
        // Get categories that have assignments
        Set<Category> categoriesWithAssignments = course.getCategoriesWithAssignments();
        
        // If no assignments at all, return 0
        if (categoriesWithAssignments.isEmpty()) {
            return new CourseGrade(course, 0.0, LetterGrade.F, categoryAverages);
        }
        
        // Calculate adjusted weights for categories with assignments
        Map<Category, Double> adjustedWeights = calculateAdjustedWeights(course, categoriesWithAssignments);
        
        // Calculate weighted average
        double finalGrade = calculateWeightedAverage(categoryAverages, adjustedWeights);
        
        // Convert to letter grade
        LetterGrade letterGrade = LetterGrade.fromPercentage(finalGrade);
        
        return new CourseGrade(course, finalGrade, letterGrade, categoryAverages);
    }
    
    /**
     * Calculates the average percentage for each category.
     * 
     * @param course The course
     * @return Map of category to average percentage (0-100)
     */
    public Map<Category, Double> calculateCategoryAverages(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        Map<Category, Double> averages = new EnumMap<>(Category.class);
        
        // Get all configured categories for this course
        Map<Category, Double> categoryWeights = course.getCategoryWeights();
        
        for (Category category : categoryWeights.keySet()) {
            double average = calculateCategoryAverage(course, category);
            averages.put(category, average);
        }
        
        return averages;
    }
    
    /**
     * Calculates the average for a specific category.
     * 
     * @param course The course
     * @param category The category
     * @return Average percentage (0-100), or 0.0 if no assignments in category
     * @throws IllegalArgumentException if course or category is null
     */
    public double calculateCategoryAverage(Course course, Category category) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        List<Assignment> assignments = course.getAssignmentsByCategory(category);
        
        if (assignments.isEmpty()) {
            return 0.0;
        }
        
        double totalEarned = 0.0;
        double totalPossible = 0.0;
        
        for (Assignment assignment : assignments) {
            totalEarned += assignment.getPointsEarned();
            totalPossible += assignment.getPointsPossible();
        }
        
        // Avoid division by zero (should not happen with valid assignments)
        if (totalPossible == 0) {
            return 0.0;
        }
        
        return (totalEarned / totalPossible) * 100.0;
    }
    
    /**
     * Calculates adjusted weights for categories with assignments.
     * Redistributes weight from empty categories proportionally.
     * 
     * Algorithm:
     * 1. Sum weights of categories WITH assignments
     * 2. Calculate scale factor = 100 / sumOfActiveWeights
     * 3. Multiply each active category weight by scale factor
     * 
     * @param course The course
     * @param categoriesWithAssignments Categories that have assignments
     * @return Map of adjusted weights that sum to 100
     */
    private Map<Category, Double> calculateAdjustedWeights(Course course,
                                                           Set<Category> categoriesWithAssignments) {
        Map<Category, Double> originalWeights = course.getCategoryWeights();
        Map<Category, Double> adjustedWeights = new EnumMap<>(Category.class);
        
        // Calculate sum of weights for categories with assignments
        double activeWeightSum = 0.0;
        for (Category category : categoriesWithAssignments) {
            activeWeightSum += originalWeights.get(category);
        }
        
        // If all categories have assignments, no adjustment needed
        if (Math.abs(activeWeightSum - 100.0) < 0.01) {
            return new EnumMap<>(originalWeights);
        }
        
        // Calculate scale factor for redistribution
        double scaleFactor = 100.0 / activeWeightSum;
        
        // Apply scale factor to adjust weights
        for (Category category : categoriesWithAssignments) {
            double originalWeight = originalWeights.get(category);
            double adjustedWeight = originalWeight * scaleFactor;
            adjustedWeights.put(category, adjustedWeight);
        }
        
        return adjustedWeights;
    }
    
    /**
     * Calculates weighted average using category averages and weights.
     * 
     * @param categoryAverages Map of category to average percentage
     * @param weights Map of category to weight percentage
     * @return Weighted average (0-100)
     */
    private double calculateWeightedAverage(Map<Category, Double> categoryAverages,
                                           Map<Category, Double> weights) {
        double weightedSum = 0.0;
        
        for (Map.Entry<Category, Double> entry : weights.entrySet()) {
            Category category = entry.getKey();
            double weight = entry.getValue();
            double average = categoryAverages.get(category);
            
            weightedSum += (average * weight / 100.0);
        }
        
        return weightedSum;
    }
}
