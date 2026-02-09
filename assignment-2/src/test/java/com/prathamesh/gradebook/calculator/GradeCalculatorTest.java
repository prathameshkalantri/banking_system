package com.prathamesh.gradebook.calculator;

import com.prathamesh.gradebook.domain.Assignment;
import com.prathamesh.gradebook.domain.Category;
import com.prathamesh.gradebook.domain.Course;
import com.prathamesh.gradebook.domain.LetterGrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for GradeCalculator.
 * 
 * Test Coverage:
 * - Category average calculation (single, multiple, empty)
 * - Weight adjustment when categories missing assignments
 * - Course grade calculation with various scenarios
 * - Letter grade conversion integration
 * - Edge cases (no assignments, all perfect, all zero)
 * - Null parameter validation
 * 
 * @author Prathamesh Kalantri
 */
class GradeCalculatorTest {
    
    private GradeCalculator calculator;
    private Course course;
    
    @BeforeEach
    void setUp() {
        calculator = new GradeCalculator();
        course = new Course("Data Structures", 3);
    }
    
    // ========== Constructor Tests ==========
    
    @Test
    void testCreateCalculator() {
        assertNotNull(calculator);
    }
    
    // ========== Category Average Tests ==========
    
    @Test
    void testCalculateCategoryAverageWithOneAssignment() {
        course.addAssignment(new Assignment("HW1", 85, 100, Category.HOMEWORK));
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        
        assertEquals(85.0, average, 0.01);
    }
    
    @Test
    void testCalculateCategoryAverageWithMultipleAssignments() {
        course.addAssignment(new Assignment("HW1", 80, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 90, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW3", 85, 100, Category.HOMEWORK));
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        
        // (80 + 90 + 85) / (100 + 100 + 100) = 255/300 = 85%
        assertEquals(85.0, average, 0.01);
    }
    
    @Test
    void testCalculateCategoryAverageWithDifferentPointValues() {
        course.addAssignment(new Assignment("HW1", 40, 50, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 80, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW3", 30, 50, Category.HOMEWORK));
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        
        // (40 + 80 + 30) / (50 + 100 + 50) = 150/200 = 75%
        assertEquals(75.0, average, 0.01);
    }
    
    @Test
    void testCalculateCategoryAverageWithEmptyCategory() {
        // No assignments added
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        
        assertEquals(0.0, average, 0.01);
    }
    
    @Test
    void testCalculateCategoryAverageWithPerfectScores() {
        course.addAssignment(new Assignment("HW1", 100, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 50, 50, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW3", 75, 75, Category.HOMEWORK));
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        
        assertEquals(100.0, average, 0.01);
    }
    
    @Test
    void testCalculateCategoryAverageWithZeroScores() {
        course.addAssignment(new Assignment("HW1", 0, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 0, 100, Category.HOMEWORK));
        
        double average = calculator.calculateCategoryAverage(course, Category.HOMEWORK);
        
        assertEquals(0.0, average, 0.01);
    }
    
    @Test
    void testCalculateCategoryAverageNullCourseThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateCategoryAverage(null, Category.HOMEWORK);
        });
        
        assertEquals("Course cannot be null", exception.getMessage());
    }
    
    @Test
    void testCalculateCategoryAverageNullCategoryThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateCategoryAverage(course, null);
        });
        
        assertEquals("Category cannot be null", exception.getMessage());
    }
    
    // ========== Category Averages Map Tests ==========
    
    @Test
    void testCalculateCategoryAveragesWithAllCategories() {
        course.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 85, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 80, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 88, 100, Category.FINAL_EXAM));
        
        Map<Category, Double> averages = calculator.calculateCategoryAverages(course);
        
        assertEquals(4, averages.size());
        assertEquals(90.0, averages.get(Category.HOMEWORK), 0.01);
        assertEquals(85.0, averages.get(Category.QUIZZES), 0.01);
        assertEquals(80.0, averages.get(Category.MIDTERM), 0.01);
        assertEquals(88.0, averages.get(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testCalculateCategoryAveragesWithPartialCategories() {
        course.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Final", 88, 100, Category.FINAL_EXAM));
        
        Map<Category, Double> averages = calculator.calculateCategoryAverages(course);
        
        assertEquals(4, averages.size());
        assertEquals(90.0, averages.get(Category.HOMEWORK), 0.01);
        assertEquals(0.0, averages.get(Category.QUIZZES), 0.01);
        assertEquals(0.0, averages.get(Category.MIDTERM), 0.01);
        assertEquals(88.0, averages.get(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testCalculateCategoryAveragesWithNoAssignments() {
        Map<Category, Double> averages = calculator.calculateCategoryAverages(course);
        
        assertEquals(4, averages.size());
        assertEquals(0.0, averages.get(Category.HOMEWORK), 0.01);
        assertEquals(0.0, averages.get(Category.QUIZZES), 0.01);
        assertEquals(0.0, averages.get(Category.MIDTERM), 0.01);
        assertEquals(0.0, averages.get(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testCalculateCategoryAveragesNullCourseThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateCategoryAverages(null);
        });
        
        assertEquals("Course cannot be null", exception.getMessage());
    }
    
    // ========== Course Grade Tests - All Categories ==========
    
    @Test
    void testCalculateCourseGradeWithAllCategoriesStandardWeights() {
        // Default weights: HOMEWORK(20%), QUIZZES(20%), MIDTERM(25%), FINAL(35%)
        course.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 85, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 80, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 88, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        // (90*0.20) + (85*0.20) + (80*0.25) + (88*0.35) = 18 + 17 + 20 + 30.8 = 85.8
        assertEquals(85.8, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
        assertEquals("Data Structures", grade.getCourseName());
    }
    
    @Test
    void testCalculateCourseGradeResultsInGradeA() {
        course.addAssignment(new Assignment("HW1", 95, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 92, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 90, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 93, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        // (95*0.20) + (92*0.20) + (90*0.25) + (93*0.35) = 19 + 18.4 + 22.5 + 32.55 = 92.45
        assertEquals(92.45, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.A, grade.getLetterGrade());
    }
    
    @Test
    void testCalculateCourseGradeResultsInGradeF() {
        course.addAssignment(new Assignment("HW1", 50, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 45, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 40, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 48, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        // (50*0.20) + (45*0.20) + (40*0.25) + (48*0.35) = 10 + 9 + 10 + 16.8 = 45.8
        assertEquals(45.8, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.F, grade.getLetterGrade());
    }
    
    // ========== Course Grade Tests - Missing Categories ==========
    
    @Test
    void testCalculateCourseGradeWithMissingOneCategory() {
        // Missing QUIZZES (20% weight)
        // Weights should adjust: HOMEWORK=25%, MIDTERM=31.25%, FINAL=43.75%
        course.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Midterm", 80, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 85, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        // Adjusted weights: 20/(20+25+35)=0.25, 25/80=0.3125, 35/80=0.4375
        // (90*0.25) + (80*0.3125) + (85*0.4375) = 22.5 + 25 + 37.1875 = 84.6875
        assertEquals(84.6875, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }
    
    @Test
    void testCalculateCourseGradeWithMissingTwoCategories() {
        // Missing QUIZZES and MIDTERM (45% weight)
        // Weights should adjust: HOMEWORK=36.36%, FINAL=63.64%
        course.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Final", 85, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        // Adjusted weights: 20/(20+35)=0.363636, 35/55=0.636364
        // (90*0.363636) + (85*0.636364) = 32.727 + 54.091 = 86.818
        assertEquals(86.818, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }
    
    @Test
    void testCalculateCourseGradeWithOnlyOneCategory() {
        // Only FINAL_EXAM (should get 100% weight)
        course.addAssignment(new Assignment("Final", 92, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        assertEquals(92.0, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.A, grade.getLetterGrade());
    }
    
    @Test
    void testCalculateCourseGradeWithNoAssignments() {
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        assertEquals(0.0, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.F, grade.getLetterGrade());
    }
    
    // ========== Course Grade Tests - Custom Weights ==========
    
    @Test
    void testCalculateCourseGradeWithCustomWeights() {
        Map<Category, Double> customWeights = new EnumMap<>(Category.class);
        customWeights.put(Category.HOMEWORK, 10.0);
        customWeights.put(Category.QUIZZES, 10.0);
        customWeights.put(Category.MIDTERM, 30.0);
        customWeights.put(Category.FINAL_EXAM, 50.0);
        Course customCourse = new Course("Special Course", 3, customWeights);
        
        customCourse.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        customCourse.addAssignment(new Assignment("Quiz1", 85, 100, Category.QUIZZES));
        customCourse.addAssignment(new Assignment("Midterm", 80, 100, Category.MIDTERM));
        customCourse.addAssignment(new Assignment("Final", 88, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(customCourse);
        
        // (90*0.10) + (85*0.10) + (80*0.30) + (88*0.50) = 9 + 8.5 + 24 + 44 = 85.5
        assertEquals(85.5, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }
    
    @Test
    void testCalculateCourseGradeWithCustomWeightsAndMissingCategory() {
        Map<Category, Double> customWeights = new EnumMap<>(Category.class);
        customWeights.put(Category.HOMEWORK, 30.0);
        customWeights.put(Category.QUIZZES, 0.0);
        customWeights.put(Category.MIDTERM, 30.0);
        customWeights.put(Category.FINAL_EXAM, 40.0);
        Course customCourse = new Course("Special Course", 3, customWeights);
        
        // Missing MIDTERM (30% weight)
        customCourse.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        customCourse.addAssignment(new Assignment("Final", 85, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(customCourse);
        
        // Adjusted weights: 30/70=0.4286, 40/70=0.5714
        // (90*0.4286) + (85*0.5714) = 38.574 + 48.569 = 87.143
        assertEquals(87.143, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }
    
    // ========== Multiple Assignments Per Category Tests ==========
    
    @Test
    void testCalculateCourseGradeWithMultipleAssignmentsPerCategory() {
        // Add multiple assignments to each category
        course.addAssignment(new Assignment("HW1", 80, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 90, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW3", 100, 100, Category.HOMEWORK));
        
        course.addAssignment(new Assignment("Quiz1", 85, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Quiz2", 95, 100, Category.QUIZZES));
        
        course.addAssignment(new Assignment("Midterm", 80, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 88, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        // HOMEWORK avg: (80+90+100)/(100+100+100) = 270/300 = 90%
        // QUIZZES avg: (85+95)/(100+100) = 180/200 = 90%
        // MIDTERM avg: 80/100 = 80%
        // FINAL avg: 88/100 = 88%
        // (90*0.20) + (90*0.20) + (80*0.25) + (88*0.35) = 18 + 18 + 20 + 30.8 = 86.8
        assertEquals(86.8, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }
    
    // ========== Edge Cases ==========
    
    @Test
    void testCalculateCourseGradeWithPerfectScores() {
        course.addAssignment(new Assignment("HW1", 100, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 100, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 100, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 100, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        assertEquals(100.0, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.A, grade.getLetterGrade());
    }
    
    @Test
    void testCalculateCourseGradeWithAllZeroScores() {
        course.addAssignment(new Assignment("HW1", 0, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 0, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 0, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 0, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        assertEquals(0.0, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.F, grade.getLetterGrade());
    }
    
    @Test
    void testCalculateCourseGradeNullCourseThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateCourseGrade(null);
        });
        
        assertEquals("Course cannot be null", exception.getMessage());
    }
    
    // ========== Boundary Grade Tests ==========
    
    @Test
    void testCalculateCourseGradeAtABoundary() {
        // Exactly 90.0 should be A
        course.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 90, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 90, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 90, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        assertEquals(90.0, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.A, grade.getLetterGrade());
    }
    
    @Test
    void testCalculateCourseGradeJustBelowABoundary() {
        // 89.99 should be B
        course.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 90, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 90, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 89, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        // (90*0.20) + (90*0.20) + (90*0.25) + (89*0.35) = 18 + 18 + 22.5 + 31.15 = 89.65
        assertEquals(89.65, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }
    
    @Test
    void testCalculateCourseGradeAtDFBoundary() {
        // Exactly 60.0 should be D
        course.addAssignment(new Assignment("HW1", 60, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 60, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 60, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 60, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        assertEquals(60.0, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.D, grade.getLetterGrade());
    }
    
    // ========== CourseGrade Object Verification ==========
    
    @Test
    void testCourseGradeContainsCategoryAverages() {
        course.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 85, 100, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 80, 100, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 88, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        assertEquals(90.0, grade.getCategoryAverage(Category.HOMEWORK), 0.01);
        assertEquals(85.0, grade.getCategoryAverage(Category.QUIZZES), 0.01);
        assertEquals(80.0, grade.getCategoryAverage(Category.MIDTERM), 0.01);
        assertEquals(88.0, grade.getCategoryAverage(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testCourseGradeContainsCourse() {
        course.addAssignment(new Assignment("Final", 85, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = calculator.calculateCourseGrade(course);
        
        assertSame(course, grade.getCourse());
    }
}
