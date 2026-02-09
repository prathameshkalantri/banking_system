package com.prathamesh.gradebook.exception;

import com.prathamesh.gradebook.domain.Category;

/**
 * Exception thrown when an invalid category is specified.
 * 
 * While this is less likely with enum-based categories, this exception
 * can be thrown when:
 * - A course doesn't have a specific category configured
 * - Category weights are missing for a required category
 * - An operation is attempted on a category not applicable to the course
 * 
 * @author Prathamesh Kalantri
 */
public class InvalidCategoryException extends GradebookException {
    
    private final String categoryName;
    private final String courseName;
    
    /**
     * Constructs a new invalid category exception.
     * 
     * @param categoryName The name of the invalid category
     * @param courseName The name of the course
     */
    public InvalidCategoryException(String categoryName, String courseName) {
        super(String.format("Invalid category '%s' for course: %s", 
            categoryName, courseName));
        this.categoryName = categoryName;
        this.courseName = courseName;
    }
    
    /**
     * Constructs a new invalid category exception with Category enum.
     * 
     * @param category The invalid category
     * @param courseName The name of the course
     * @param reason The reason why the category is invalid
     */
    public InvalidCategoryException(Category category, String courseName, String reason) {
        super(String.format("Invalid category %s for course '%s': %s", 
            category, courseName, reason));
        this.categoryName = category.name();
        this.courseName = courseName;
    }
    
    /**
     * Gets the category name that was invalid.
     * 
     * @return The category name
     */
    public String getCategoryName() {
        return categoryName;
    }
    
    /**
     * Gets the course name.
     * 
     * @return The course name
     */
    public String getCourseName() {
        return courseName;
    }
}
