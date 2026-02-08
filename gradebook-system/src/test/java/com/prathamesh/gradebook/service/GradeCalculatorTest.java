package com.prathamesh.gradebook.service;

import com.prathamesh.gradebook.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the GradeCalculator service.
 * Tests cover category averages, weighted grade calculations, and edge cases.
 */
@DisplayName("GradeCalculator Service Tests")
class GradeCalculatorTest {

    private GradeCalculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new GradeCalculator();
    }

    // ==================== Category Average Tests ====================
    
    @Test
    @DisplayName("Should calculate category average for single assignment")
    void testCategoryAverageSingleAssignment() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW1", 85.0, 100.0, Category.HOMEWORK));
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        assertEquals(85.0, average, 0.001);
    }
    
    @Test
    @DisplayName("Should calculate category average for multiple assignments")
    void testCategoryAverageMultipleAssignments() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW1", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 85.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW3", 95.0, 100.0, Category.HOMEWORK));
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        assertEquals(90.0, average, 0.001);
    }
    
    @Test
    @DisplayName("Should return 0 for empty assignment list")
    void testCategoryAverageEmpty() {
        Course course = new Course("Test Course", 3);
        // No assignments added
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        assertEquals(0.0, average, 0.001);
    }
    
    @Test
    @DisplayName("Should handle decimal averages correctly")
    void testCategoryAverageWithDecimals() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW1", 87.5, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 92.3, 100.0, Category.HOMEWORK));
        
        double expected = (87.5 + 92.3) / 2.0;
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        assertEquals(expected, average, 0.001);
    }
    
    @Test
    @DisplayName("Should handle non-standard point totals")
    void testCategoryAverageNonStandardPoints() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK));  // 90%
        course.addAssignment(new Assignment("HW2", 38.0, 40.0, Category.HOMEWORK));  // 95%
        course.addAssignment(new Assignment("HW3", 42.0, 50.0, Category.HOMEWORK));  // 84%
        
        double expected = (90.0 + 95.0 + 84.0) / 3.0;
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        assertEquals(expected, average, 0.01);
    }

    // ==================== Weighted Grade Calculation Tests ====================
    
    @Test
    @DisplayName("Should calculate weighted final grade with all categories")
    void testWeightedGradeAllCategories() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 88.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 92.0, 100.0, Category.FINAL_EXAM));
        
        double finalGrade = calculator.calculateFinalPercentage(course);
        
        // Expected: 90*0.20 + 85*0.20 + 88*0.25 + 92*0.35 = 18 + 17 + 22 + 32.2 = 89.2
        assertEquals(89.2, finalGrade, 0.01);
    }
    
    @Test
    @DisplayName("Should normalize grade when missing categories")
    void testWeightedGradeNormalization() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 80.0, 100.0, Category.QUIZZES));
        
        double finalGrade = calculator.calculateFinalPercentage(course);
        
        // With homework (20%) and quizzes (20%) only = 40% total weight
        // Expected: (90*0.20 + 80*0.20) / 0.40 = 34 / 0.40 = 85.0
        assertEquals(85.0, finalGrade, 0.01);
    }
    
    @Test
    @DisplayName("Should return 0 when course has no assignments")
    void testWeightedGradeNoAssignments() {
        Course course = new Course("Empty Course", 3);
        
        double finalGrade = calculator.calculateFinalPercentage(course);
        assertEquals(0.0, finalGrade, 0.001);
    }
    
    @Test
    @DisplayName("Should calculate correctly with single category")
    void testWeightedGradeSingleCategory() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW", 95.0, 100.0, Category.HOMEWORK));
        
        double finalGrade = calculator.calculateFinalPercentage(course);
        
        // Single category should be normalized to 100% weight
        assertEquals(95.0, finalGrade, 0.01);
    }
    
    @Test
    @DisplayName("Should handle perfect scores across all categories")
    void testWeightedGradePerfectScore() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW", 100.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 100.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Mid", 100.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 100.0, 100.0, Category.FINAL_EXAM));
        
        double finalGrade = calculator.calculateFinalPercentage(course);
        assertEquals(100.0, finalGrade, 0.001);
    }

    // ==================== Realistic Grade Calculation Tests ====================
    
    @Test
    @DisplayName("Should calculate realistic course grade with varied performance")
    void testRealisticCourseGrade() {
        Course course = new Course("Calculus", 4);
        
        // Homework: 85, 90, 88 → avg 87.67
        course.addAssignment(new Assignment("HW1", 85.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW3", 88.0, 100.0, Category.HOMEWORK));
        
        // Quizzes: 92, 88 → avg 90.0
        course.addAssignment(new Assignment("Quiz1", 92.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Quiz2", 88.0, 100.0, Category.QUIZZES));
        
        // Midterm: 85
        course.addAssignment(new Assignment("Midterm", 85.0, 100.0, Category.MIDTERM));
        
        // Final: 91
        course.addAssignment(new Assignment("Final", 91.0, 100.0, Category.FINAL_EXAM));
        
        double finalGrade = calculator.calculateFinalPercentage(course);
        
        // Expected: 87.67*0.20 + 90.0*0.20 + 85.0*0.25 + 91.0*0.35 = 88.63
        assertEquals(88.63, finalGrade, 0.01);
    }
    
    @Test
    @DisplayName("Should handle multiple assignments per category correctly")
    void testMultipleAssignmentsPerCategory() {
        Course course = new Course("Test Course", 3);
        
        // 5 homework assignments
        course.addAssignment(new Assignment("HW1", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 85.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW3", 95.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW4", 80.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW5", 100.0, 100.0, Category.HOMEWORK));
        
        // Should average to: (90+85+95+80+100)/5 = 90.0
        double hwAverage = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        assertEquals(90.0, hwAverage, 0.001);
    }

    // ==================== Edge Case Tests ====================
    
    @Test
    @DisplayName("Should handle zero scores")
    void testZeroScores() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW1", 0.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 0.0, 100.0, Category.HOMEWORK));
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        assertEquals(0.0, average, 0.001);
    }
    
    @Test
    @DisplayName("Should handle mix of zero and high scores")
    void testMixedWithZeros() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW1", 0.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 100.0, 100.0, Category.HOMEWORK));
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        assertEquals(50.0, average, 0.001);
    }
    
    @Test
    @DisplayName("Should handle failing grades")
    void testFailingGrades() {
        Course course = new Course("Difficult Course", 3);
        course.addAssignment(new Assignment("HW", 50.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 45.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Mid", 55.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 48.0, 100.0, Category.FINAL_EXAM));
        
        double finalGrade = calculator.calculateFinalPercentage(course);
        
        // Should calculate correctly even with failing grades
        assertTrue(finalGrade < 60.0);
        assertTrue(finalGrade > 0.0);
    }
    
    @Test
    @DisplayName("Should handle boundary grade percentages")
    void testBoundaryPercentages() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 80.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Mid", 70.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 60.0, 100.0, Category.FINAL_EXAM));
        
        double finalGrade = calculator.calculateFinalPercentage(course);
        
        // All boundary grades (A, B, C, D minimum)
        assertTrue(finalGrade >= 60.0);
        assertTrue(finalGrade < 90.0);
    }
    
    @Test
    @DisplayName("Should handle very small point values")
    void testSmallPointValues() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("Quiz1", 0.5, 1.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Quiz2", 0.8, 1.0, Category.QUIZZES));
        
        double average = calculator.calculateCategoryAverage(course, Category.QUIZZES);
        assertEquals(65.0, average, 0.01);
    }
    
    @Test
    @DisplayName("Should handle large point values")
    void testLargePointValues() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("Project", 950.0, 1000.0, Category.HOMEWORK));
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        assertEquals(95.0, average, 0.001);
    }

    // ==================== Null/Validation Tests ====================
    
    @Test
    @DisplayName("Should throw exception for null course")
    void testNullCourse() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateCategoryAverage(null, Category.HOMEWORK);
        });
    }
    
    @Test
    @DisplayName("Should throw exception for null course in calculateFinalPercentage")
    void testNullCourseForFinalPercentage() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateFinalPercentage(null);
        });
    }

    // ==================== Weight Distribution Tests ====================
    
    @Test
    @DisplayName("Should distribute weight correctly with missing midterm")
    void testWeightDistributionNoMidterm() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("HW", 90.0, 100.0, Category.HOMEWORK));     // 20%
        course.addAssignment(new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));   // 20%
        course.addAssignment(new Assignment("Final", 92.0, 100.0, Category.FINAL_EXAM)); // 35%
        
        double finalGrade = calculator.calculateFinalPercentage(course);
        
        // Total weight used: 75%
        // Expected: (90*0.20 + 85*0.20 + 92*0.35) / 0.75 = 67.2 / 0.75 = 89.6
        assertEquals(89.6, finalGrade, 0.01);
    }
    
    @Test
    @DisplayName("Should distribute weight correctly with only exams")
    void testWeightDistributionExamsOnly() {
        Course course = new Course("Test Course", 3);
        course.addAssignment(new Assignment("Mid", 85.0, 100.0, Category.MIDTERM));     // 25%
        course.addAssignment(new Assignment("Final", 90.0, 100.0, Category.FINAL_EXAM)); // 35%
        
        double finalGrade = calculator.calculateFinalPercentage(course);
        
        // Total weight: 60%
        // Expected: (85*0.25 + 90*0.35) / 0.60 = 52.75 / 0.60 = 87.92
        assertEquals(87.92, finalGrade, 0.01);
    }
}
