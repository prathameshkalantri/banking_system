package com.prathamesh.gradebook.calculator;

import com.prathamesh.gradebook.domain.Course;
import com.prathamesh.gradebook.domain.Student;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Calculator for computing Grade Point Average (GPA) across multiple courses.
 * 
 * Implements credit-weighted GPA calculation:
 * GPA = Σ(grade_points × credit_hours) / Σ(credit_hours)
 * 
 * Where grade_points are determined by letter grade:
 * - A = 4.0
 * - B = 3.0
 * - C = 2.0
 * - D = 1.0
 * - F = 0.0
 * 
 * Example:
 * - Course 1: B (3.0) × 4 credits = 12.0 quality points
 * - Course 2: A (4.0) × 3 credits = 12.0 quality points
 * - Course 3: C (2.0) × 3 credits = 6.0 quality points
 * Total: 30.0 quality points / 10 credits = 3.0 GPA
 * 
 * @author Prathamesh Kalantri
 */
public class GPACalculator {
    
    private final GradeCalculator gradeCalculator;
    
    /**
     * Creates a GPA calculator with a grade calculator.
     */
    public GPACalculator() {
        this.gradeCalculator = new GradeCalculator();
    }
    
    /**
     * Creates a GPA calculator with a specific grade calculator.
     * Useful for testing or custom grade calculation logic.
     * 
     * @param gradeCalculator The grade calculator to use
     */
    public GPACalculator(GradeCalculator gradeCalculator) {
        if (gradeCalculator == null) {
            throw new IllegalArgumentException("Grade calculator cannot be null");
        }
        this.gradeCalculator = gradeCalculator;
    }
    
    /**
     * Calculates GPA for a student across all enrolled courses.
     * 
     * @param student The student
     * @return The credit-weighted GPA (0.0-4.0), or 0.0 if no courses
     * @throws IllegalArgumentException if student is null
     */
    public double calculateGPA(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        Collection<Course> courses = student.getAllCourses();
        
        if (courses.isEmpty()) {
            return 0.0;
        }
        
        return calculateGPA(courses);
    }
    
    /**
     * Calculates GPA for a collection of courses.
     * 
     * @param courses The courses
     * @return The credit-weighted GPA (0.0-4.0), or 0.0 if no courses
     * @throws IllegalArgumentException if courses is null
     */
    public double calculateGPA(Collection<Course> courses) {
        if (courses == null) {
            throw new IllegalArgumentException("Courses cannot be null");
        }
        
        if (courses.isEmpty()) {
            return 0.0;
        }
        
        List<CourseGrade> courseGrades = new ArrayList<>();
        for (Course course : courses) {
            CourseGrade grade = gradeCalculator.calculateCourseGrade(course);
            courseGrades.add(grade);
        }
        
        return calculateGPAFromGrades(courseGrades);
    }
    
    /**
     * Calculates GPA from a list of course grades.
     * 
     * @param courseGrades The course grades
     * @return The credit-weighted GPA (0.0-4.0), or 0.0 if no grades
     * @throws IllegalArgumentException if courseGrades is null
     */
    public double calculateGPAFromGrades(List<CourseGrade> courseGrades) {
        if (courseGrades == null) {
            throw new IllegalArgumentException("Course grades cannot be null");
        }
        
        if (courseGrades.isEmpty()) {
            return 0.0;
        }
        
        double totalQualityPoints = 0.0;
        int totalCreditHours = 0;
        
        for (CourseGrade courseGrade : courseGrades) {
            int creditHours = courseGrade.getCreditHours();
            double gradePoints = courseGrade.getGpaValue();
            
            totalQualityPoints += (gradePoints * creditHours);
            totalCreditHours += creditHours;
        }
        
        // Avoid division by zero (shouldn't happen with valid courses)
        if (totalCreditHours == 0) {
            return 0.0;
        }
        
        return totalQualityPoints / totalCreditHours;
    }
    
    /**
     * Calculates GPA and returns all course grades.
     * Useful for transcript generation.
     * 
     * @param student The student
     * @return List of course grades for all enrolled courses
     * @throws IllegalArgumentException if student is null
     */
    public List<CourseGrade> calculateAllCourseGrades(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        Collection<Course> courses = student.getAllCourses();
        List<CourseGrade> courseGrades = new ArrayList<>();
        
        for (Course course : courses) {
            CourseGrade grade = gradeCalculator.calculateCourseGrade(course);
            courseGrades.add(grade);
        }
        
        return courseGrades;
    }
    
    /**
     * Checks if a student qualifies for Honor Roll (GPA >= 3.5).
     * 
     * @param student The student
     * @return true if GPA is 3.5 or higher
     * @throws IllegalArgumentException if student is null
     */
    public boolean isHonorRoll(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        double gpa = calculateGPA(student);
        return gpa >= 3.5;
    }
    
    /**
     * Gets the total credit hours for which a student has enrolled.
     * 
     * @param student The student
     * @return Total credit hours
     * @throws IllegalArgumentException if student is null
     */
    public int getTotalCreditHours(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        return student.getTotalCreditHours();
    }
}
