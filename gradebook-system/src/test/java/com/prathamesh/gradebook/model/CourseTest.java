package com.prathamesh.gradebook.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Course class.
 * Tests cover course management, assignment handling, and grade calculations.
 */
@DisplayName("Course Model Tests")
class CourseTest {

    private Course course;
    
    @BeforeEach
    void setUp() {
        course = new Course("Calculus I", 4);
    }

    // ==================== Construction Tests ====================
    
    @Test
    @DisplayName("Should create valid course with all parameters")
    void testValidCourseCreation() {
        Course testCourse = new Course("Data Structures", 3);
        
        assertEquals("Data Structures", testCourse.getCourseName());
        assertEquals(3, testCourse.getCreditHours());
        assertTrue(testCourse.getAssignments().isEmpty());
    }
    
    @Test
    @DisplayName("Should create course with minimum credit hours")
    void testMinimumCreditHours() {
        Course testCourse = new Course("Seminar", 1);
        assertEquals(1, testCourse.getCreditHours());
    }
    
    @Test
    @DisplayName("Should create course with maximum typical credit hours")
    void testMaximumCreditHours() {
        Course testCourse = new Course("Research Project", 6);
        assertEquals(6, testCourse.getCreditHours());
    }

    // ==================== Validation Tests ====================
    
    @Test
    @DisplayName("Should throw exception when course name is null")
    void testNullCourseName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Course(null, 3);
        });
        
        assertTrue(exception.getMessage().contains("name"));
    }
    
    @Test
    @DisplayName("Should throw exception when course name is empty")
    void testEmptyCourseName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Course("", 3);
        });
        
        assertTrue(exception.getMessage().contains("name"));
    }
    
    @Test
    @DisplayName("Should throw exception when credit hours is zero")
    void testZeroCreditHours() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Course("Test Course", 0);
        });
        
        assertTrue(exception.getMessage().contains("between 1 and 6"));
    }
    
    @Test
    @DisplayName("Should throw exception when credit hours is negative")
    void testNegativeCreditHours() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Course("Test Course", -3);
        });
        
        assertTrue(exception.getMessage().contains("between 1 and 6"));
    }

    // ==================== Assignment Management Tests ====================
    
    @Test
    @DisplayName("Should add assignment successfully")
    void testAddAssignment() {
        Assignment assignment = new Assignment("Quiz 1", 85.0, 100.0, Category.QUIZZES);
        course.addAssignment(assignment);
        
        assertEquals(1, course.getAssignments().size());
        assertTrue(course.getAssignments().contains(assignment));
    }
    
    @Test
    @DisplayName("Should add multiple assignments")
    void testAddMultipleAssignments() {
        Assignment hw1 = new Assignment("HW 1", 90.0, 100.0, Category.HOMEWORK);
        Assignment hw2 = new Assignment("HW 2", 85.0, 100.0, Category.HOMEWORK);
        Assignment quiz = new Assignment("Quiz", 88.0, 100.0, Category.QUIZZES);
        
        course.addAssignment(hw1);
        course.addAssignment(hw2);
        course.addAssignment(quiz);
        
        assertEquals(3, course.getAssignments().size());
    }
    
    @Test
    @DisplayName("Should throw exception when adding null assignment")
    void testAddNullAssignment() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addAssignment(null);
        });
        
        assertTrue(exception.getMessage().contains("null"));
    }
    
    @Test
    @DisplayName("Should return unmodifiable list of assignments")
    void testAssignmentsListUnmodifiable() {
        Assignment assignment = new Assignment("Quiz 1", 85.0, 100.0, Category.QUIZZES);
        course.addAssignment(assignment);
        
        List<Assignment> assignments = course.getAssignments();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            assignments.add(new Assignment("New", 90.0, 100.0, Category.HOMEWORK));
        });
    }

    // ==================== Category Filter Tests ====================
    
    @Test
    @DisplayName("Should get assignments by category")
    void testGetAssignmentsByCategory() {
        Assignment hw1 = new Assignment("HW 1", 90.0, 100.0, Category.HOMEWORK);
        Assignment hw2 = new Assignment("HW 2", 85.0, 100.0, Category.HOMEWORK);
        Assignment quiz = new Assignment("Quiz", 88.0, 100.0, Category.QUIZZES);
        
        course.addAssignment(hw1);
        course.addAssignment(hw2);
        course.addAssignment(quiz);
        
        List<Assignment> homework = course.getAssignmentsByCategory(Category.HOMEWORK);
        assertEquals(2, homework.size());
        assertTrue(homework.contains(hw1));
        assertTrue(homework.contains(hw2));
        
        List<Assignment> quizzes = course.getAssignmentsByCategory(Category.QUIZZES);
        assertEquals(1, quizzes.size());
        assertTrue(quizzes.contains(quiz));
    }
    
    @Test
    @DisplayName("Should return empty list for category with no assignments")
    void testGetAssignmentsByEmptyCategory() {
        List<Assignment> midterms = course.getAssignmentsByCategory(Category.MIDTERM);
        assertNotNull(midterms);
        assertTrue(midterms.isEmpty());
    }

    // ==================== Category Average Tests ====================
    
    @Test
    @DisplayName("Should calculate category average correctly")
    void testGetCategoryAverage() {
        Assignment hw1 = new Assignment("HW 1", 90.0, 100.0, Category.HOMEWORK);
        Assignment hw2 = new Assignment("HW 2", 80.0, 100.0, Category.HOMEWORK);
        
        course.addAssignment(hw1);
        course.addAssignment(hw2);
        
        double average = course.getCategoryAverage(Category.HOMEWORK);
        assertEquals(85.0, average, 0.001);
    }
    
    @Test
    @DisplayName("Should return 0 for category average when no assignments")
    void testCategoryAverageEmpty() {
        double average = course.getCategoryAverage(Category.HOMEWORK);
        assertEquals(0.0, average, 0.001);
    }
    
    @Test
    @DisplayName("Should calculate average for single assignment")
    void testCategoryAverageSingle() {
        Assignment quiz = new Assignment("Quiz 1", 87.5, 100.0, Category.QUIZZES);
        course.addAssignment(quiz);
        
        double average = course.getCategoryAverage(Category.QUIZZES);
        assertEquals(87.5, average, 0.001);
    }
    
    @Test
    @DisplayName("Should calculate average with decimal values")
    void testCategoryAverageWithDecimals() {
        Assignment hw1 = new Assignment("HW 1", 87.5, 100.0, Category.HOMEWORK);
        Assignment hw2 = new Assignment("HW 2", 92.3, 100.0, Category.HOMEWORK);
        Assignment hw3 = new Assignment("HW 3", 88.7, 100.0, Category.HOMEWORK);
        
        course.addAssignment(hw1);
        course.addAssignment(hw2);
        course.addAssignment(hw3);
        
        double expected = (87.5 + 92.3 + 88.7) / 3.0;
        double average = course.getCategoryAverage(Category.HOMEWORK);
        assertEquals(expected, average, 0.001);
    }

    // ==================== Final Percentage Tests ====================
    
    @Test
    @DisplayName("Should calculate final percentage with all categories")
    void testFinalPercentageAllCategories() {
        // Add assignments for all categories
        course.addAssignment(new Assignment("HW Avg", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz Avg", 85.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 88.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 92.0, 100.0, Category.FINAL_EXAM));
        
        double finalPercentage = course.getFinalPercentage();
        
        // Expected: 90*0.20 + 85*0.20 + 88*0.25 + 92*0.35 = 18 + 17 + 22 + 32.2 = 89.2
        assertEquals(89.2, finalPercentage, 0.01);
    }
    
    @Test
    @DisplayName("Should return 0 when no assignments")
    void testFinalPercentageNoAssignments() {
        double finalPercentage = course.getFinalPercentage();
        assertEquals(0.0, finalPercentage, 0.001);
    }
    
    @Test
    @DisplayName("Should normalize when missing categories")
    void testFinalPercentageNormalization() {
        // Only homework and quizzes (40% total)
        course.addAssignment(new Assignment("HW", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 80.0, 100.0, Category.QUIZZES));
        
        double finalPercentage = course.getFinalPercentage();
        
        // Expected: (90*0.20 + 80*0.20) / 0.40 = 34 / 0.40 = 85.0
        assertEquals(85.0, finalPercentage, 0.01);
    }
    
    @Test
    @DisplayName("Should handle single category with weight normalization")
    void testFinalPercentageSingleCategory() {
        course.addAssignment(new Assignment("HW", 95.0, 100.0, Category.HOMEWORK));
        
        double finalPercentage = course.getFinalPercentage();
        
        // Should be normalized to 95% (the category average)
        assertEquals(95.0, finalPercentage, 0.01);
    }

    // ==================== Final Grade Tests ====================
    
    @Test
    @DisplayName("Should calculate final grade with correct letter")
    void testFinalGrade() {
        course.addAssignment(new Assignment("HW", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 88.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 92.0, 100.0, Category.FINAL_EXAM));
        
        Grade finalGrade = course.getFinalGrade();
        
        assertNotNull(finalGrade);
        assertEquals(89.2, finalGrade.getPercentage(), 0.01);
        assertEquals(LetterGrade.B, finalGrade.getLetterGrade());
        assertEquals(3.0, finalGrade.getGpaPoints(), 0.001);
    }
    
    @Test
    @DisplayName("Should return grade with 0 when no assignments")
    void testFinalGradeNoAssignments() {
        Grade finalGrade = course.getFinalGrade();
        
        assertNotNull(finalGrade);
        assertEquals(0.0, finalGrade.getPercentage(), 0.001);
        assertEquals(LetterGrade.F, finalGrade.getLetterGrade());
    }

    // ==================== Complex Calculation Tests ====================
    
    @Test
    @DisplayName("Should handle multiple assignments per category")
    void testMultipleAssignmentsPerCategory() {
        // Add multiple homework assignments
        course.addAssignment(new Assignment("HW 1", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW 2", 85.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW 3", 95.0, 100.0, Category.HOMEWORK));
        
        // Average: (90 + 85 + 95) / 3 = 90.0
        assertEquals(90.0, course.getCategoryAverage(Category.HOMEWORK), 0.001);
    }
    
    @Test
    @DisplayName("Should calculate realistic course grade")
    void testRealisticCourseGrade() {
        // Homework: 85, 90, 88 → avg 87.67
        course.addAssignment(new Assignment("HW 1", 85.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW 2", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW 3", 88.0, 100.0, Category.HOMEWORK));
        
        // Quizzes: 92, 88 → avg 90.0
        course.addAssignment(new Assignment("Quiz 1", 92.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Quiz 2", 88.0, 100.0, Category.QUIZZES));
        
        // Midterm: 85
        course.addAssignment(new Assignment("Midterm", 85.0, 100.0, Category.MIDTERM));
        
        // Final: 91
        course.addAssignment(new Assignment("Final", 91.0, 100.0, Category.FINAL_EXAM));
        
        double finalPercentage = course.getFinalPercentage();
        
        // Expected: 87.67*0.20 + 90.0*0.20 + 85.0*0.25 + 91.0*0.35
        //         = 17.53 + 18.0 + 21.25 + 31.85 = 88.63
        assertEquals(88.63, finalPercentage, 0.01);
        
        Grade finalGrade = course.getFinalGrade();
        assertEquals(LetterGrade.B, finalGrade.getLetterGrade());
    }

    // ==================== Edge Case Tests ====================
    
    @Test
    @DisplayName("Should handle perfect score in all categories")
    void testPerfectScores() {
        course.addAssignment(new Assignment("HW", 100.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 100.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 100.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 100.0, 100.0, Category.FINAL_EXAM));
        
        assertEquals(100.0, course.getFinalPercentage(), 0.001);
        assertEquals(LetterGrade.A, course.getFinalGrade().getLetterGrade());
    }
    
    @Test
    @DisplayName("Should handle failing scores")
    void testFailingScores() {
        course.addAssignment(new Assignment("HW", 50.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 45.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 55.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 48.0, 100.0, Category.FINAL_EXAM));
        
        double finalPercentage = course.getFinalPercentage();
        assertTrue(finalPercentage < 60.0);
        assertEquals(LetterGrade.F, course.getFinalGrade().getLetterGrade());
    }
    
    @Test
    @DisplayName("Should handle non-standard point totals")
    void testNonStandardPointTotals() {
        course.addAssignment(new Assignment("HW", 45.0, 50.0, Category.HOMEWORK)); // 90%
        course.addAssignment(new Assignment("Quiz", 42.0, 50.0, Category.QUIZZES)); // 84%
        course.addAssignment(new Assignment("Midterm", 88.0, 100.0, Category.MIDTERM)); // 88%
        course.addAssignment(new Assignment("Final", 184.0, 200.0, Category.FINAL_EXAM)); // 92%
        
        double finalPercentage = course.getFinalPercentage();
        
        // Expected: 90*0.20 + 84*0.20 + 88*0.25 + 92*0.35 = 89.0
        assertEquals(89.0, finalPercentage, 0.01);
    }

    // ==================== toString Tests ====================
    
    @Test
    @DisplayName("Should have meaningful toString representation")
    void testToString() {
        String toString = course.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Calculus I"));
    }

    // ==================== Immutability Tests ====================
    
    @Test
    @DisplayName("Should have consistent getter returns")
    void testConsistentGetters() {
        assertEquals("Calculus I", course.getCourseName());
        assertEquals("Calculus I", course.getCourseName());
        
        assertEquals(4, course.getCreditHours());
        assertEquals(4, course.getCreditHours());
    }

    // ==================== Weight Normalization Tests ====================
    
    @Test
    @DisplayName("Should normalize with two categories present")
    void testTwoCategoryNormalization() {
        // Midterm (25%) and Final (35%) = 60% total
        course.addAssignment(new Assignment("Midterm", 80.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 90.0, 100.0, Category.FINAL_EXAM));
        
        double expected = (80.0 * 0.25 + 90.0 * 0.35) / 0.60;
        assertEquals(expected, course.getFinalPercentage(), 0.01);
    }
    
    @Test
    @DisplayName("Should normalize with three categories present")
    void testThreeCategoryNormalization() {
        // Homework (20%), Quizzes (20%), Final (35%) = 75% total
        course.addAssignment(new Assignment("HW", 85.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 90.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Final", 88.0, 100.0, Category.FINAL_EXAM));
        
        double expected = (85.0 * 0.20 + 90.0 * 0.20 + 88.0 * 0.35) / 0.75;
        assertEquals(expected, course.getFinalPercentage(), 0.01);
    }
}
