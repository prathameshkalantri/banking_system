package com.prathamesh.gradebook.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a course with graded assignments organized into weighted categories.
 * <p>
 * A Course manages all assignments for a particular class, organized by grading
 * categories (HOMEWORK, QUIZZES, MIDTERM, FINAL_EXAM). Each category has a fixed
 * weight that determines its contribution to the final course grade.
 * </p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Automatic grade calculation using weighted categories</li>
 *   <li>Support for multiple assignments per category</li>
 *   <li>Credit hour tracking for GPA calculation</li>
 *   <li>Exclusion of empty categories from grade calculation</li>
 *   <li>Missing assignment handling (scored as zero)</li>
 * </ul>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public class Course {
    
    /** The name of this course */
    private final String courseName;
    
    /** The number of credit hours for this course (for GPA calculation) */
    private final int creditHours;
    
    /** List of all assignments in this course */
    private final List<Assignment> assignments;
    
    /**
     * Constructs a new Course with the specified name and credit hours.
     * <p>
     * The default category weights are used:
     * HOMEWORK (20%), QUIZZES (20%), MIDTERM (25%), FINAL_EXAM (35%).
     * </p>
     * 
     * @param courseName The name of the course (must not be null or empty)
     * @param creditHours The credit hours (must be between 1 and 6)
     * @throws IllegalArgumentException if validation fails
     */
    public Course(String courseName, int creditHours) {
        // Validate course name
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be null or empty");
        }
        
        // Validate credit hours
        if (creditHours < 1 || creditHours > 6) {
            throw new IllegalArgumentException(
                "Credit hours must be between 1 and 6, got: " + creditHours);
        }
        
        this.courseName = courseName.trim();
        this.creditHours = creditHours;
        this.assignments = new ArrayList<>();
    }
    
    /**
     * Returns the name of this course.
     * 
     * @return The course name
     */
    public String getCourseName() {
        return courseName;
    }
    
    /**
     * Returns the credit hours for this course.
     * 
     * @return The credit hours (1-6)
     */
    public int getCreditHours() {
        return creditHours;
    }
    
    /**
     * Returns an unmodifiable list of all assignments in this course.
     * <p>
     * The returned list cannot be modified. Use {@link #addAssignment(Assignment)}
     * to add new assignments.
     * </p>
     * 
     * @return An unmodifiable list of assignments
     */
    public List<Assignment> getAssignments() {
        return Collections.unmodifiableList(assignments);
    }
    
    /**
     * Adds an assignment to this course.
     * <p>
     * The assignment is added to its corresponding category based on the
     * category specified in the assignment object.
     * </p>
     * 
     * @param assignment The assignment to add (must not be null)
     * @throws IllegalArgumentException if assignment is null
     */
    public void addAssignment(Assignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment cannot be null");
        }
        assignments.add(assignment);
    }
    
    /**
     * Returns all assignments for a specific category.
     * 
     * @param category The category to filter by
     * @return A list of assignments in the specified category (may be empty)
     */
    public List<Assignment> getAssignmentsByCategory(Category category) {
        return assignments.stream()
            .filter(a -> a.getCategory() == category)
            .collect(Collectors.toList());
    }
    
    /**
     * Calculates the average percentage for a specific category.
     * <p>
     * The category average is calculated as the arithmetic mean of all
     * assignment percentages within that category. If the category has no
     * assignments, returns 0.0.
     * </p>
     * 
     * @param category The category to calculate average for
     * @return The category average percentage (0.0 to 100.0)
     */
    public double getCategoryAverage(Category category) {
        List<Assignment> categoryAssignments = getAssignmentsByCategory(category);
        
        if (categoryAssignments.isEmpty()) {
            return 0.0;
        }
        
        double sum = categoryAssignments.stream()
            .mapToDouble(Assignment::getPercentage)
            .sum();
        
        return sum / categoryAssignments.size();
    }
    
    /**
     * Calculates the final course grade as a percentage.
     * <p>
     * The final grade is calculated as the weighted sum of category averages.
     * Only categories with assignments are included in the calculation, and
     * their weights are proportionally adjusted.
     * </p>
     * 
     * <h3>Calculation Algorithm:</h3>
     * <ol>
     *   <li>Calculate average for each category that has assignments</li>
     *   <li>Multiply each category average by its weight</li>
     *   <li>Sum the weighted averages</li>
     *   <li>If not all categories have assignments, normalize by actual weight used</li>
     * </ol>
     * 
     * @return The final course grade as a percentage (0.0 to 100.0)
     */
    public double getFinalPercentage() {
        double weightedSum = 0.0;
        double totalWeightUsed = 0.0;
        
        // Calculate weighted sum for categories with assignments
        for (Category category : Category.values()) {
            List<Assignment> categoryAssignments = getAssignmentsByCategory(category);
            
            // Only include categories that have assignments
            if (!categoryAssignments.isEmpty()) {
                double categoryAvg = getCategoryAverage(category);
                double weight = category.getWeight();
                
                weightedSum += categoryAvg * weight;
                totalWeightUsed += weight;
            }
        }
        
        // If no assignments exist, return 0
        if (totalWeightUsed == 0.0) {
            return 0.0;
        }
        
        // Normalize if not all categories have assignments
        return weightedSum / totalWeightUsed;
    }
    
    /**
     * Calculates and returns the final course grade.
     * <p>
     * This method computes the final percentage and converts it to a Grade object
     * containing both the percentage and corresponding letter grade.
     * </p>
     * 
     * @return A Grade object representing the final course grade
     */
    public Grade getFinalGrade() {
        return new Grade(getFinalPercentage());
    }
    
    /**
     * Returns the number of assignments in this course.
     * 
     * @return The total number of assignments
     */
    public int getAssignmentCount() {
        return assignments.size();
    }
    
    /**
     * Returns the number of assignments in a specific category.
     * 
     * @param category The category to count assignments for
     * @return The number of assignments in the category
     */
    public int getAssignmentCount(Category category) {
        return (int) assignments.stream()
            .filter(a -> a.getCategory() == category)
            .count();
    }
    
    /**
     * Checks if this course has any assignments.
     * 
     * @return true if the course has at least one assignment, false otherwise
     */
    public boolean hasAssignments() {
        return !assignments.isEmpty();
    }
    
    /**
     * Checks if a specific category has any assignments.
     * 
     * @param category The category to check
     * @return true if the category has at least one assignment, false otherwise
     */
    public boolean hasAssignments(Category category) {
        return assignments.stream()
            .anyMatch(a -> a.getCategory() == category);
    }
    
    /**
     * Returns a map of category averages for all categories with assignments.
     * 
     * @return A map of Category to average percentage
     */
    public Map<Category, Double> getCategoryAverages() {
        Map<Category, Double> averages = new HashMap<>();
        
        for (Category category : Category.values()) {
            if (hasAssignments(category)) {
                averages.put(category, getCategoryAverage(category));
            }
        }
        
        return averages;
    }
    
    /**
     * Compares this course to another object for equality.
     * <p>
     * Two courses are equal if they have the same name and credit hours.
     * Assignments are not considered in equality comparison.
     * </p>
     * 
     * @param obj The object to compare with
     * @return true if the courses are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Course course = (Course) obj;
        return creditHours == course.creditHours &&
               courseName.equals(course.courseName);
    }
    
    /**
     * Returns a hash code value for this course.
     * 
     * @return A hash code based on course name and credit hours
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseName, creditHours);
    }
    
    /**
     * Returns a string representation of this course.
     * 
     * @return A formatted string with course name, credit hours, and assignment count
     */
    @Override
    public String toString() {
        return String.format("%s (%d credits, %d assignments)",
            courseName, creditHours, getAssignmentCount());
    }
}
