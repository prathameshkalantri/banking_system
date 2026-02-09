package com.prathamesh.gradebook.domain;

import com.prathamesh.gradebook.exception.InvalidCategoryException;
import com.prathamesh.gradebook.exception.InvalidCreditHoursException;
import com.prathamesh.gradebook.exception.InvalidWeightException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a course in the gradebook system. This is a Composite pattern branch node
 * that contains assignments (leaf nodes) and aggregates their grades by category.
 * 
 * A course has:
 * - A name
 * - Credit hours (1-6, used for GPA calculation)
 * - Category weights (must sum to 100%)
 * - A list of assignments organized by category
 * 
 * Design Patterns:
 * - Composite Pattern: Course is a branch node containing Assignment leaf nodes
 * - Builder Pattern: For optional custom category weights
 * 
 * Immutability Notes:
 * - Course name and credit hours are final and immutable after construction
 * - Category weights are final and immutable
 * - Assignments list is mutable (can add assignments) but defensively copied on access
 * - Individual Assignment objects are immutable (from Phase 3)
 * 
 * @author Prathamesh Kalantri
 */
public class Course {
    
    private final String courseName;
    private final int creditHours;
    private final Map<Category, Double> categoryWeights;
    private final List<Assignment> assignments;
    
    /**
     * Creates a course with default category weights.
     * 
     * @param courseName The name of the course (cannot be null or blank)
     * @param creditHours The credit hours for this course (must be 1-6)
     * @throws IllegalArgumentException if courseName is null or blank
     * @throws InvalidCreditHoursException if creditHours is not between 1 and 6
     */
    public Course(String courseName, int creditHours) {
        this(courseName, creditHours, Category.getDefaultWeights());
    }
    
    /**
     * Creates a course with custom category weights.
     * 
     * @param courseName The name of the course (cannot be null or blank)
     * @param creditHours The credit hours for this course (must be 1-6)
     * @param categoryWeights The custom weights for each category (must sum to 100%)
     * @throws IllegalArgumentException if courseName is null or blank
     * @throws InvalidCreditHoursException if creditHours is not between 1 and 6
     * @throws InvalidWeightException if categoryWeights don't sum to 100%
     * @throws IllegalArgumentException if categoryWeights is null
     */
    public Course(String courseName, int creditHours, Map<Category, Double> categoryWeights) {
        // Validate course name
        if (courseName == null || courseName.isBlank()) {
            throw new IllegalArgumentException("Course name cannot be null or blank");
        }
        
        // Validate credit hours
        if (creditHours < 1 || creditHours > 6) {
            throw new InvalidCreditHoursException(creditHours);
        }
        
        // Validate category weights
        if (categoryWeights == null) {
            throw new IllegalArgumentException("Category weights cannot be null");
        }
        
        // Validate weights sum to 100%
        if (!Category.validateWeights(categoryWeights)) {
            throw new InvalidWeightException(categoryWeights, Category.sumWeights(categoryWeights));
        }
        
        this.courseName = courseName.trim();
        this.creditHours = creditHours;
        // Create defensive immutable copy of weights
        this.categoryWeights = new EnumMap<>(categoryWeights);
        this.assignments = new ArrayList<>();
    }
    
    /**
     * Gets the course name.
     * 
     * @return The course name (never null or blank)
     */
    public String getCourseName() {
        return courseName;
    }
    
    /**
     * Gets the credit hours for this course.
     * 
     * @return The credit hours (always between 1 and 6)
     */
    public int getCreditHours() {
        return creditHours;
    }
    
    /**
     * Gets the category weights for this course.
     * 
     * @return Defensive copy of category weights map (never null)
     */
    public Map<Category, Double> getCategoryWeights() {
        return new EnumMap<>(categoryWeights);
    }
    
    /**
     * Gets the weight for a specific category.
     * 
     * @param category The category to get weight for
     * @return The weight percentage for the category
     * @throws InvalidCategoryException if category is not configured for this course
     */
    public double getCategoryWeight(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        Double weight = categoryWeights.get(category);
        if (weight == null) {
            throw new InvalidCategoryException(category, courseName, 
                "Category weight not configured for this course");
        }
        
        return weight;
    }
    
    /**
     * Gets all assignments for this course.
     * 
     * @return Defensive copy of assignments list (never null, may be empty)
     */
    public List<Assignment> getAssignments() {
        return new ArrayList<>(assignments);
    }
    
    /**
     * Gets assignments filtered by a specific category.
     * 
     * @param category The category to filter by
     * @return List of assignments in the specified category (never null, may be empty)
     * @throws IllegalArgumentException if category is null
     */
    public List<Assignment> getAssignmentsByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        return assignments.stream()
            .filter(a -> a.getCategory() == category)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets the number of assignments in this course.
     * 
     * @return The total number of assignments
     */
    public int getAssignmentCount() {
        return assignments.size();
    }
    
    /**
     * Gets the number of assignments in a specific category.
     * 
     * @param category The category to count
     * @return The number of assignments in the category
     * @throws IllegalArgumentException if category is null
     */
    public int getAssignmentCountByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        return (int) assignments.stream()
            .filter(a -> a.getCategory() == category)
            .count();
    }
    
    /**
     * Adds an assignment to this course.
     * 
     * @param assignment The assignment to add (cannot be null)
     * @throws IllegalArgumentException if assignment is null
     * @throws InvalidCategoryException if assignment's category is not configured for this course
     */
    public void addAssignment(Assignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment cannot be null");
        }
        
        // Verify the assignment's category is configured for this course
        if (!categoryWeights.containsKey(assignment.getCategory())) {
            throw new InvalidCategoryException(assignment.getCategory(), courseName,
                "Cannot add assignment with category not configured for this course");
        }
        
        assignments.add(assignment);
    }
    
    /**
     * Checks if this course has any assignments.
     * 
     * @return true if there are no assignments
     */
    public boolean isEmpty() {
        return assignments.isEmpty();
    }
    
    /**
     * Checks if this course has assignments in a specific category.
     * 
     * @param category The category to check
     * @return true if there are assignments in the category
     * @throws IllegalArgumentException if category is null
     */
    public boolean hasAssignmentsInCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        return assignments.stream().anyMatch(a -> a.getCategory() == category);
    }
    
    /**
     * Gets all categories that have at least one assignment.
     * 
     * @return Set of categories with assignments (never null, may be empty)
     */
    public Set<Category> getCategoriesWithAssignments() {
        return assignments.stream()
            .map(Assignment::getCategory)
            .collect(Collectors.toSet());
    }
    
    /**
     * Returns a string representation of the course.
     * 
     * @return String representation including name, credit hours, and assignment count
     */
    @Override
    public String toString() {
        return String.format("Course{name='%s', creditHours=%d, assignments=%d}",
            courseName, creditHours, assignments.size());
    }
    
    /**
     * Checks if two courses are equal based on course name and credit hours.
     * Note: Does not compare assignments or weights (courses are identified by name/credits)
     * 
     * @param o The object to compare with
     * @return true if courses have same name and credit hours
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return creditHours == course.creditHours &&
               Objects.equals(courseName, course.courseName);
    }
    
    /**
     * Hash code based on course name and credit hours.
     * 
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseName, creditHours);
    }
    
    /**
     * Builder class for creating courses with custom configurations.
     * Provides a fluent API for optional custom category weights.
     */
    public static class Builder {
        private final String courseName;
        private final int creditHours;
        private Map<Category, Double> categoryWeights;
        
        /**
         * Creates a new course builder.
         * 
         * @param courseName The course name
         * @param creditHours The credit hours (1-6)
         */
        public Builder(String courseName, int creditHours) {
            this.courseName = courseName;
            this.creditHours = creditHours;
            this.categoryWeights = Category.getDefaultWeights();
        }
        
        /**
         * Sets custom category weights.
         * Creates a defensive copy to prevent external modification.
         * 
         * @param weights The custom weights map
         * @return This builder for chaining
         */
        public Builder withCategoryWeights(Map<Category, Double> weights) {
            this.categoryWeights = new EnumMap<>(weights);
            return this;
        }
        
        /**
         * Sets a specific category weight.
         * 
         * @param category The category
         * @param weight The weight percentage
         * @return This builder for chaining
         */
        public Builder withCategoryWeight(Category category, double weight) {
            if (categoryWeights == Category.getDefaultWeights()) {
                // Create a new mutable copy if still using defaults
                categoryWeights = new EnumMap<>(categoryWeights);
            }
            categoryWeights.put(category, weight);
            return this;
        }
        
        /**
         * Builds the course with the configured settings.
         * 
         * @return A new Course instance
         * @throws IllegalArgumentException if validation fails
         * @throws InvalidCreditHoursException if credit hours are invalid
         * @throws InvalidWeightException if weights don't sum to 100%
         */
        public Course build() {
            return new Course(courseName, creditHours, categoryWeights);
        }
    }
}
