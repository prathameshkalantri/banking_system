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
 * Comprehensive tests for CourseGrade value object.
 * 
 * Test Coverage:
 * - Constructor validation (null checks, range checks)
 * - Getter methods for all fields
 * - Category average retrieval
 * - Defensive copying of category averages
 * - Passing grade logic
 * - String representation
 * - Edge cases (boundary grades, empty categories)
 * 
 * @author Prathamesh Kalantri
 */
class CourseGradeTest {
    
    private Course course;
    private Map<Category, Double> categoryAverages;
    
    @BeforeEach
    void setUp() {
        // Create a standard course
        course = new Course("Data Structures", 3);
        
        // Create sample category averages
        categoryAverages = new EnumMap<>(Category.class);
        categoryAverages.put(Category.HOMEWORK, 85.0);
        categoryAverages.put(Category.QUIZZES, 90.0);
        categoryAverages.put(Category.MIDTERM, 80.0);
        categoryAverages.put(Category.FINAL_EXAM, 88.0);
    }
    
    // ========== Constructor Tests ==========
    
    @Test
    void testCreateValidCourseGrade() {
        CourseGrade grade = new CourseGrade(course, 86.5, LetterGrade.B, categoryAverages);
        
        assertNotNull(grade);
        assertEquals(course, grade.getCourse());
        assertEquals(86.5, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
        assertEquals(3.0, grade.getGpaValue(), 0.01);
    }
    
    @Test
    void testCreateWithMinimumGrade() {
        CourseGrade grade = new CourseGrade(course, 0.0, LetterGrade.F, categoryAverages);
        
        assertEquals(0.0, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.F, grade.getLetterGrade());
        assertFalse(grade.isPassing());
    }
    
    @Test
    void testCreateWithMaximumGrade() {
        CourseGrade grade = new CourseGrade(course, 100.0, LetterGrade.A, categoryAverages);
        
        assertEquals(100.0, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.A, grade.getLetterGrade());
        assertTrue(grade.isPassing());
    }
    
    @Test
    void testCreateWithBoundaryGrades() {
        // Test D- boundary (60.0 - minimum passing)
        CourseGrade gradeD = new CourseGrade(course, 60.0, LetterGrade.D, categoryAverages);
        assertEquals(60.0, gradeD.getNumericGrade(), 0.01);
        assertTrue(gradeD.isPassing());
        
        // Test F+ boundary (59.99 - maximum failing)
        CourseGrade gradeF = new CourseGrade(course, 59.99, LetterGrade.F, categoryAverages);
        assertEquals(59.99, gradeF.getNumericGrade(), 0.01);
        assertFalse(gradeF.isPassing());
    }
    
    @Test
    void testNullCourseThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CourseGrade(null, 85.0, LetterGrade.B, categoryAverages);
        });
        
        assertEquals("Course cannot be null", exception.getMessage());
    }
    
    @Test
    void testNullLetterGradeThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CourseGrade(course, 85.0, null, categoryAverages);
        });
        
        assertEquals("Letter grade cannot be null", exception.getMessage());
    }
    
    @Test
    void testNullCategoryAveragesThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CourseGrade(course, 85.0, LetterGrade.B, null);
        });
        
        assertEquals("Category averages cannot be null", exception.getMessage());
    }
    
    @Test
    void testNegativeNumericGradeThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CourseGrade(course, -0.01, LetterGrade.F, categoryAverages);
        });
        
        assertEquals("Numeric grade must be between 0 and 100", exception.getMessage());
    }
    
    @Test
    void testNumericGradeAbove100ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CourseGrade(course, 100.01, LetterGrade.A, categoryAverages);
        });
        
        assertEquals("Numeric grade must be between 0 and 100", exception.getMessage());
    }
    
    // ========== Getter Tests ==========
    
    @Test
    void testGetCourseName() {
        CourseGrade grade = new CourseGrade(course, 85.0, LetterGrade.B, categoryAverages);
        
        assertEquals("Data Structures", grade.getCourseName());
    }
    
    @Test
    void testGetCreditHours() {
        CourseGrade grade = new CourseGrade(course, 85.0, LetterGrade.B, categoryAverages);
        
        assertEquals(3, grade.getCreditHours());
    }
    
    @Test
    void testGetGpaValue() {
        CourseGrade gradeA = new CourseGrade(course, 92.0, LetterGrade.A, categoryAverages);
        assertEquals(4.0, gradeA.getGpaValue(), 0.01);
        
        CourseGrade gradeB = new CourseGrade(course, 85.0, LetterGrade.B, categoryAverages);
        assertEquals(3.0, gradeB.getGpaValue(), 0.01);
        
        CourseGrade gradeC = new CourseGrade(course, 75.0, LetterGrade.C, categoryAverages);
        assertEquals(2.0, gradeC.getGpaValue(), 0.01);
        
        CourseGrade gradeD = new CourseGrade(course, 65.0, LetterGrade.D, categoryAverages);
        assertEquals(1.0, gradeD.getGpaValue(), 0.01);
        
        CourseGrade gradeF = new CourseGrade(course, 55.0, LetterGrade.F, categoryAverages);
        assertEquals(0.0, gradeF.getGpaValue(), 0.01);
    }
    
    // ========== Category Average Tests ==========
    
    @Test
    void testGetCategoryAverage() {
        CourseGrade grade = new CourseGrade(course, 86.0, LetterGrade.B, categoryAverages);
        
        assertEquals(85.0, grade.getCategoryAverage(Category.HOMEWORK), 0.01);
        assertEquals(90.0, grade.getCategoryAverage(Category.QUIZZES), 0.01);
        assertEquals(80.0, grade.getCategoryAverage(Category.MIDTERM), 0.01);
        assertEquals(88.0, grade.getCategoryAverage(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testGetCategoryAverageForMissingCategory() {
        Map<Category, Double> partialAverages = new EnumMap<>(Category.class);
        partialAverages.put(Category.HOMEWORK, 85.0);
        partialAverages.put(Category.MIDTERM, 80.0);
        
        CourseGrade grade = new CourseGrade(course, 82.5, LetterGrade.B, partialAverages);
        
        // Missing categories should return 0.0
        assertEquals(0.0, grade.getCategoryAverage(Category.QUIZZES), 0.01);
        assertEquals(0.0, grade.getCategoryAverage(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testGetCategoryAveragesReturnsDefensiveCopy() {
        CourseGrade grade = new CourseGrade(course, 86.0, LetterGrade.B, categoryAverages);
        
        Map<Category, Double> retrieved = grade.getCategoryAverages();
        retrieved.put(Category.HOMEWORK, 100.0);
        
        // Original should be unchanged
        assertEquals(85.0, grade.getCategoryAverage(Category.HOMEWORK), 0.01);
    }
    
    @Test
    void testConstructorMakeDefensiveCopyOfAverages() {
        CourseGrade grade = new CourseGrade(course, 86.0, LetterGrade.B, categoryAverages);
        
        // Modify original map
        categoryAverages.put(Category.HOMEWORK, 100.0);
        
        // CourseGrade should have original value
        assertEquals(85.0, grade.getCategoryAverage(Category.HOMEWORK), 0.01);
    }
    
    // ========== Passing Grade Tests ==========
    
    @Test
    void testIsPassingWithGradeA() {
        CourseGrade grade = new CourseGrade(course, 95.0, LetterGrade.A, categoryAverages);
        assertTrue(grade.isPassing());
    }
    
    @Test
    void testIsPassingWithGradeB() {
        CourseGrade grade = new CourseGrade(course, 85.0, LetterGrade.B, categoryAverages);
        assertTrue(grade.isPassing());
    }
    
    @Test
    void testIsPassingWithGradeC() {
        CourseGrade grade = new CourseGrade(course, 75.0, LetterGrade.C, categoryAverages);
        assertTrue(grade.isPassing());
    }
    
    @Test
    void testIsPassingWithGradeD() {
        CourseGrade grade = new CourseGrade(course, 65.0, LetterGrade.D, categoryAverages);
        assertTrue(grade.isPassing());
    }
    
    @Test
    void testIsNotPassingWithGradeF() {
        CourseGrade grade = new CourseGrade(course, 55.0, LetterGrade.F, categoryAverages);
        assertFalse(grade.isPassing());
    }
    
    // ========== String Representation Tests ==========
    
    @Test
    void testToString() {
        CourseGrade grade = new CourseGrade(course, 85.5, LetterGrade.B, categoryAverages);
        
        String expected = "Data Structures: 85.50% (B, 3.0 GPA)";
        assertEquals(expected, grade.toString());
    }
    
    @Test
    void testToStringWithGradeA() {
        CourseGrade grade = new CourseGrade(course, 95.75, LetterGrade.A, categoryAverages);
        
        String expected = "Data Structures: 95.75% (A, 4.0 GPA)";
        assertEquals(expected, grade.toString());
    }
    
    @Test
    void testToStringWithGradeF() {
        CourseGrade grade = new CourseGrade(course, 45.0, LetterGrade.F, categoryAverages);
        
        String expected = "Data Structures: 45.00% (F, 0.0 GPA)";
        assertEquals(expected, grade.toString());
    }
    
    @Test
    void testToStringWithDifferentCourse() {
        Course mathCourse = new Course("Calculus I", 4);
        
        CourseGrade grade = new CourseGrade(mathCourse, 88.25, LetterGrade.B, categoryAverages);
        
        String expected = "Calculus I: 88.25% (B, 3.0 GPA)";
        assertEquals(expected, grade.toString());
    }
    
    // ========== Integration Tests ==========
    
    @Test
    void testCourseGradeWithEmptyCategoryAverages() {
        Map<Category, Double> emptyAverages = new EnumMap<>(Category.class);
        
        CourseGrade grade = new CourseGrade(course, 0.0, LetterGrade.F, emptyAverages);
        
        assertEquals(0.0, grade.getCategoryAverage(Category.HOMEWORK), 0.01);
        assertEquals(0.0, grade.getCategoryAverage(Category.QUIZZES), 0.01);
        assertEquals(0.0, grade.getCategoryAverage(Category.MIDTERM), 0.01);
        assertEquals(0.0, grade.getCategoryAverage(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testCourseGradeWithOnlyOneCategory() {
        Map<Category, Double> singleCategory = new EnumMap<>(Category.class);
        singleCategory.put(Category.FINAL_EXAM, 92.5);
        
        CourseGrade grade = new CourseGrade(course, 92.5, LetterGrade.A, singleCategory);
        
        assertEquals(92.5, grade.getCategoryAverage(Category.FINAL_EXAM), 0.01);
        assertEquals(0.0, grade.getCategoryAverage(Category.HOMEWORK), 0.01);
    }
    
    @Test
    void testCourseGradeWithAllCategories() {
        CourseGrade grade = new CourseGrade(course, 86.0, LetterGrade.B, categoryAverages);
        
        Map<Category, Double> allAverages = grade.getCategoryAverages();
        assertEquals(4, allAverages.size());
        assertTrue(allAverages.containsKey(Category.HOMEWORK));
        assertTrue(allAverages.containsKey(Category.QUIZZES));
        assertTrue(allAverages.containsKey(Category.MIDTERM));
        assertTrue(allAverages.containsKey(Category.FINAL_EXAM));
    }
}
