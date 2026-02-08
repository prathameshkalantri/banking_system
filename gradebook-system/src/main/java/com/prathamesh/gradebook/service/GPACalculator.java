package com.prathamesh.gradebook.service;

import com.prathamesh.gradebook.model.*;

import java.util.Collection;

/**
 * Service class for calculating cumulative GPA across multiple courses.
 * <p>
 * This calculator implements credit-hour weighted GPA calculation following
 * standard academic practices. Only courses with assignments are included
 * in the GPA calculation.
 * </p>
 * 
 * <h3>GPA Scale (4.0 System):</h3>
 * <ul>
 *   <li>A (90-100%) = 4.0 points</li>
 *   <li>B (80-89%) = 3.0 points</li>
 *   <li>C (70-79%) = 2.0 points</li>
 *   <li>D (60-69%) = 1.0 points</li>
 *   <li>F (below 60%) = 0.0 points</li>
 * </ul>
 * 
 * <h3>Calculation Formula:</h3>
 * <pre>
 * GPA = Σ(Grade Points × Credit Hours) / Σ(Credit Hours)
 * </pre>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public class GPACalculator {
    
    /**
     * Constructs a new GPACalculator.
     * <p>
     * This class is stateless and can be reused for multiple calculations.
     * </p>
     */
    public GPACalculator() {
        // No initialization needed - stateless calculator
    }
    
    /**
     * Calculates the cumulative GPA for a student.
     * <p>
     * The GPA is calculated as a credit-hour weighted average of all courses
     * with assignments. Courses without assignments are excluded from the calculation.
     * </p>
     * 
     * <h3>Algorithm:</h3>
     * <ol>
     *   <li>For each course with assignments:</li>
     *   <li>  - Calculate final grade percentage</li>
     *   <li>  - Convert to letter grade</li>
     *   <li>  - Get GPA points for letter grade</li>
     *   <li>  - Multiply by credit hours</li>
     *   <li>  - Add to total grade points</li>
     *   <li>  - Add credit hours to total</li>
     *   <li>Divide total grade points by total credit hours</li>
     * </ol>
     * 
     * <h3>Example:</h3>
     * <pre>
     * Course 1: 92% (A = 4.0), 3 credits → 4.0 × 3 = 12.0 grade points
     * Course 2: 85% (B = 3.0), 4 credits → 3.0 × 4 = 12.0 grade points
     * Course 3: 78% (C = 2.0), 3 credits → 2.0 × 3 = 6.0 grade points
     * 
     * Total Grade Points = 12.0 + 12.0 + 6.0 = 30.0
     * Total Credit Hours = 3 + 4 + 3 = 10
     * 
     * GPA = 30.0 / 10 = 3.0
     * </pre>
     * 
     * @param student The student to calculate GPA for
     * @return The cumulative GPA on a 4.0 scale (0.0 to 4.0)
     * @throws IllegalArgumentException if student is null
     */
    public double calculateGPA(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        Collection<Course> courses = student.getCourses();
        
        double totalGradePoints = 0.0;
        int totalCreditHours = 0;
        
        for (Course course : courses) {
            // Only include courses with assignments
            if (course.hasAssignments()) {
                // Calculate final grade for this course
                Grade grade = course.getFinalGrade();
                double gpaPoints = grade.getGpaPoints();
                int creditHours = course.getCreditHours();
                
                // Add weighted grade points
                totalGradePoints += gpaPoints * creditHours;
                totalCreditHours += creditHours;
            }
        }
        
        // Avoid division by zero
        if (totalCreditHours == 0) {
            return 0.0;
        }
        
        // Calculate and return weighted GPA
        return totalGradePoints / totalCreditHours;
    }
    
    /**
     * Calculates GPA for a specific collection of courses.
     * <p>
     * This method allows calculating GPA for a subset of courses,
     * useful for semester GPA or specific term calculations.
     * </p>
     * 
     * @param courses The collection of courses to include
     * @return The GPA for the specified courses (0.0 to 4.0)
     * @throws IllegalArgumentException if courses is null
     */
    public double calculateGPA(Collection<Course> courses) {
        if (courses == null) {
            throw new IllegalArgumentException("Courses collection cannot be null");
        }
        
        double totalGradePoints = 0.0;
        int totalCreditHours = 0;
        
        for (Course course : courses) {
            if (course.hasAssignments()) {
                Grade grade = course.getFinalGrade();
                double gpaPoints = grade.getGpaPoints();
                int creditHours = course.getCreditHours();
                
                totalGradePoints += gpaPoints * creditHours;
                totalCreditHours += creditHours;
            }
        }
        
        if (totalCreditHours == 0) {
            return 0.0;
        }
        
        return totalGradePoints / totalCreditHours;
    }
    
    /**
     * Formats a GPA value to two decimal places.
     * 
     * @param gpa The GPA value to format
     * @return A formatted string (e.g., "3.45")
     */
    public String formatGPA(double gpa) {
        return String.format("%.2f", gpa);
    }
    
    /**
     * Returns the GPA classification for a given GPA value.
     * <p>
     * Classifications:
     * - 3.75-4.0: Summa Cum Laude
     * - 3.5-3.74: Magna Cum Laude
     * - 3.25-3.49: Cum Laude
     * - 3.0-3.24: Dean's List
     * - 2.0-2.99: Good Standing
     * - Below 2.0: Academic Probation
     * </p>
     * 
     * @param gpa The GPA to classify
     * @return A string classification
     */
    public String getGPAClassification(double gpa) {
        if (gpa >= 3.75) {
            return "Summa Cum Laude";
        } else if (gpa >= 3.5) {
            return "Magna Cum Laude";
        } else if (gpa >= 3.25) {
            return "Cum Laude";
        } else if (gpa >= 3.0) {
            return "Dean's List";
        } else if (gpa >= 2.0) {
            return "Good Standing";
        } else if (gpa > 0) {
            return "Academic Probation";
        } else {
            return "No Grades";
        }
    }
    
    /**
     * Calculates the total grade points earned by a student.
     * <p>
     * Grade points = Σ(GPA Points × Credit Hours) for all graded courses.
     * </p>
     * 
     * @param student The student to calculate for
     * @return The total grade points earned
     * @throws IllegalArgumentException if student is null
     */
    public double calculateTotalGradePoints(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        double totalGradePoints = 0.0;
        
        for (Course course : student.getCourses()) {
            if (course.hasAssignments()) {
                Grade grade = course.getFinalGrade();
                double gpaPoints = grade.getGpaPoints();
                int creditHours = course.getCreditHours();
                
                totalGradePoints += gpaPoints * creditHours;
            }
        }
        
        return totalGradePoints;
    }
    
    /**
     * Checks if a student's GPA qualifies for honors.
     * <p>
     * Honors qualification requires GPA >= 3.25.
     * </p>
     * 
     * @param student The student to check
     * @return true if GPA >= 3.25, false otherwise
     */
    public boolean qualifiesForHonors(Student student) {
        double gpa = calculateGPA(student);
        return gpa >= 3.25;
    }
    
    /**
     * Checks if a student is on academic probation.
     * <p>
     * Probation occurs when GPA < 2.0 (and student has grades).
     * </p>
     * 
     * @param student The student to check
     * @return true if GPA < 2.0, false otherwise
     */
    public boolean isOnProbation(Student student) {
        double gpa = calculateGPA(student);
        return gpa > 0 && gpa < 2.0;
    }
}
