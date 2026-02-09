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
 * Represents the calculated grade for a course, including the numeric grade,
 * letter grade, and category breakdowns.
 * 
 * This is a Value Object that encapsulates the result of grade calculations.
 * 
 * Design Patterns:
 * - Value Object: Immutable result of calculation
 * - Encapsulation: Hides calculation complexity
 * 
 * @author Prathamesh Kalantri
 */
public final class CourseGrade {
    
    private final Course course;
    private final double numericGrade;
    private final LetterGrade letterGrade;
    private final Map<Category, Double> categoryAverages;
    
    /**
     * Creates a course grade result.
     * 
     * @param course The course being graded
     * @param numericGrade The final numeric grade (0-100)
     * @param letterGrade The letter grade
     * @param categoryAverages The average for each category
     */
    public CourseGrade(Course course, double numericGrade, LetterGrade letterGrade,
                      Map<Category, Double> categoryAverages) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (letterGrade == null) {
            throw new IllegalArgumentException("Letter grade cannot be null");
        }
        if (categoryAverages == null) {
            throw new IllegalArgumentException("Category averages cannot be null");
        }
        if (numericGrade < 0 || numericGrade > 100) {
            throw new IllegalArgumentException("Numeric grade must be between 0 and 100");
        }
        
        this.course = course;
        this.numericGrade = numericGrade;
        this.letterGrade = letterGrade;
        this.categoryAverages = new EnumMap<>(categoryAverages);
    }
    
    /**
     * Gets the course.
     * 
     * @return The course
     */
    public Course getCourse() {
        return course;
    }
    
    /**
     * Gets the course name.
     * 
     * @return The course name
     */
    public String getCourseName() {
        return course.getCourseName();
    }
    
    /**
     * Gets the numeric grade (0-100).
     * 
     * @return The numeric grade
     */
    public double getNumericGrade() {
        return numericGrade;
    }
    
    /**
     * Gets the letter grade.
     * 
     * @return The letter grade
     */
    public LetterGrade getLetterGrade() {
        return letterGrade;
    }
    
    /**
     * Gets the GPA value for this grade.
     * 
     * @return The GPA value (0.0-4.0)
     */
    public double getGpaValue() {
        return letterGrade.getGpaValue();
    }
    
    /**
     * Gets the average for a specific category.
     * 
     * @param category The category
     * @return The category average, or 0.0 if no assignments in category
     */
    public double getCategoryAverage(Category category) {
        return categoryAverages.getOrDefault(category, 0.0);
    }
    
    /**
     * Gets all category averages.
     * 
     * @return Defensive copy of category averages map
     */
    public Map<Category, Double> getCategoryAverages() {
        return new EnumMap<>(categoryAverages);
    }
    
    /**
     * Gets the credit hours for this course.
     * 
     * @return The credit hours
     */
    public int getCreditHours() {
        return course.getCreditHours();
    }
    
    /**
     * Checks if the course has a passing grade (D or better).
     * 
     * @return true if letter grade is D, C, B, or A
     */
    public boolean isPassing() {
        return letterGrade != LetterGrade.F;
    }
    
    /**
     * Returns a formatted string representation of the grade.
     * Example: "Data Structures: 85.50% (B, 3.0 GPA)"
     * 
     * @return Formatted grade string
     */
    @Override
    public String toString() {
        return String.format("%s: %.2f%% (%s, %.1f GPA)",
            course.getCourseName(), numericGrade, letterGrade, letterGrade.getGpaValue());
    }
}
