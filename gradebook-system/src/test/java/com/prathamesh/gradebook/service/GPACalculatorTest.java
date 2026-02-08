package com.prathamesh.gradebook.service;

import com.prathamesh.gradebook.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the GPACalculator service.
 * Tests cover GPA calculations, classifications, and academic standing.
 */
@DisplayName("GPACalculator Service Tests")
class GPACalculatorTest {

    private GPACalculator calculator;
    private Student student;
    
    @BeforeEach
    void setUp() {
        calculator = new GPACalculator();
        student = new Student("S-10001001", "Test Student");
    }

    // ==================== Basic GPA Calculation Tests ====================
    
    @Test
    @DisplayName("Should return 0.0 GPA for student with no courses")
    void testGpaNoCourses() {
        double gpa = calculator.calculateGPA(student);
        assertEquals(0.0, gpa, 0.001);
    }
    
    @Test
    @DisplayName("Should return 0.0 GPA for courses with no assignments")
    void testGpaNoAssignments() {
        Course math = new Course("Math", 3);
        Course english = new Course("English", 3);
        
        student.enrollInCourse(math);
        student.enrollInCourse(english);
        
        double gpa = calculator.calculateGPA(student);
        assertEquals(0.0, gpa, 0.001);
    }
    
    @Test
    @DisplayName("Should calculate GPA for single course")
    void testGpaSingleCourse() {
        Course math = new Course("Math", 3);
        math.addAssignment(new Assignment("HW", 92.0, 100.0, Category.HOMEWORK));
        math.addAssignment(new Assignment("Quiz", 92.0, 100.0, Category.QUIZZES));
        math.addAssignment(new Assignment("Mid", 92.0, 100.0, Category.MIDTERM));
        math.addAssignment(new Assignment("Final", 92.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(math);
        
        double gpa = calculator.calculateGPA(student);
        assertEquals(4.0, gpa, 0.1);  // 92% = A = 4.0
    }
    
    @Test
    @DisplayName("Should calculate credit-weighted GPA for multiple courses")
    void testGpaMultipleCourses() {
        // Math (4 credits): 92% → A (4.0)
        Course math = new Course("Math", 4);
        math.addAssignment(new Assignment("HW", 92.0, 100.0, Category.HOMEWORK));
        math.addAssignment(new Assignment("Quiz", 92.0, 100.0, Category.QUIZZES));
        math.addAssignment(new Assignment("Mid", 92.0, 100.0, Category.MIDTERM));
        math.addAssignment(new Assignment("Final", 92.0, 100.0, Category.FINAL_EXAM));
        
        // English (3 credits): 85% → B (3.0)
        Course english = new Course("English", 3);
        english.addAssignment(new Assignment("HW", 85.0, 100.0, Category.HOMEWORK));
        english.addAssignment(new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));
        english.addAssignment(new Assignment("Mid", 85.0, 100.0, Category.MIDTERM));
        english.addAssignment(new Assignment("Final", 85.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(math);
        student.enrollInCourse(english);
        
        double gpa = calculator.calculateGPA(student);
        
        // Expected: (4.0*4 + 3.0*3) / 7 = 25/7 = 3.571
        assertEquals(3.571, gpa, 0.01);
    }
    
    // ==================== GPA Calculation with Varied Grades ====================
    
    @Test
    @DisplayName("Should calculate GPA with four different letter grades")
    void testGpaFourDifferentGrades() {
        // Math (3 credits): 95% → A (4.0)
        Course math = new Course("Math", 3);
        math.addAssignment(new Assignment("HW", 95.0, 100.0, Category.HOMEWORK));
        math.addAssignment(new Assignment("Final", 95.0, 100.0, Category.FINAL_EXAM));
        
        // English (3 credits): 85% → B (3.0)
        Course english = new Course("English", 3);
        english.addAssignment(new Assignment("HW", 85.0, 100.0, Category.HOMEWORK));
        english.addAssignment(new Assignment("Final", 85.0, 100.0, Category.FINAL_EXAM));
        
        // History (3 credits): 75% → C (2.0)
        Course history = new Course("History", 3);
        history.addAssignment(new Assignment("HW", 75.0, 100.0, Category.HOMEWORK));
        history.addAssignment(new Assignment("Final", 75.0, 100.0, Category.FINAL_EXAM));
        
        // Science (3 credits): 65% → D (1.0)
        Course science = new Course("Science", 3);
        science.addAssignment(new Assignment("HW", 65.0, 100.0, Category.HOMEWORK));
        science.addAssignment(new Assignment("Final", 65.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(math);
        student.enrollInCourse(english);
        student.enrollInCourse(history);
        student.enrollInCourse(science);
        
        double gpa = calculator.calculateGPA(student);
        
        // Expected: (4.0 + 3.0 + 2.0 + 1.0) / 4 = 2.5
        assertEquals(2.5, gpa, 0.01);
    }
    
    @Test
    @DisplayName("Should calculate GPA with all A grades")
    void testGpaAllAs() {
        Course course1 = new Course("Course1", 3);
        course1.addAssignment(new Assignment("Test", 95.0, 100.0, Category.FINAL_EXAM));
        
        Course course2 = new Course("Course2", 4);
        course2.addAssignment(new Assignment("Test", 92.0, 100.0, Category.FINAL_EXAM));
        
        Course course3 = new Course("Course3", 3);
        course3.addAssignment(new Assignment("Test", 98.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        student.enrollInCourse(course3);
        
        double gpa = calculator.calculateGPA(student);
        assertEquals(4.0, gpa, 0.01);
    }
    
    @Test
    @DisplayName("Should calculate GPA with mixed weighted courses")
    void testGpaMixedWeightedCourses() {
        // 1-credit lab: A (4.0)
        Course lab = new Course("Lab", 1);
        lab.addAssignment(new Assignment("Report", 95.0, 100.0, Category.HOMEWORK));
        
        // 4-credit major course: B (3.0)
        Course major = new Course("Major", 4);
        major.addAssignment(new Assignment("Test", 85.0, 100.0, Category.FINAL_EXAM));
        
        // 3-credit elective: C (2.0)
        Course elective = new Course("Elective", 3);
        elective.addAssignment(new Assignment("Test", 75.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(lab);
        student.enrollInCourse(major);
        student.enrollInCourse(elective);
        
        double gpa = calculator.calculateGPA(student);
        
        // Expected: (4.0*1 + 3.0*4 + 2.0*3) / 8 = 22/8 = 2.75
        assertEquals(2.75, gpa, 0.01);
    }
    
    @Test
    @DisplayName("Should calculate GPA for specific course collection")
    void testGpaForCourseCollection() {
        Course math = new Course("Math", 3);
        math.addAssignment(new Assignment("Test", 92.0, 100.0, Category.FINAL_EXAM));
        
        Course english = new Course("English", 3);
        english.addAssignment(new Assignment("Test", 85.0, 100.0, Category.FINAL_EXAM));
        
        List<Course> courses = new ArrayList<>();
        courses.add(math);
        courses.add(english);
        
        double gpa = calculator.calculateGPA(courses);
        
        // Expected: (4.0*3 + 3.0*3) / 6 = 21/6 = 3.5
        assertEquals(3.5, gpa, 0.01);
    }
    
    @Test
    @DisplayName("Should calculate GPA with failing grades")
    void testGpaWithFailingGrades() {
        // Pass one, fail one
        Course passed = new Course("Passed", 3);
        passed.addAssignment(new Assignment("Test", 85.0, 100.0, Category.FINAL_EXAM));
        
        Course failed = new Course("Failed", 3);
        failed.addAssignment(new Assignment("Test", 50.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(passed);
        student.enrollInCourse(failed);
        
        double gpa = calculator.calculateGPA(student);
        
        // Expected: (3.0*3 + 0.0*3) / 6 = 9/6 = 1.5
        assertEquals(1.5, gpa, 0.01);
    }

    // ==================== GPA Classification Tests ====================
    
    @Test
    @DisplayName("Should classify Summa Cum Laude (GPA >= 3.75)")
    void testClassifySummaCumLaude() {
        assertEquals("Summa Cum Laude", calculator.getGPAClassification(3.75));
        assertEquals("Summa Cum Laude", calculator.getGPAClassification(3.9));
        assertEquals("Summa Cum Laude", calculator.getGPAClassification(4.0));
    }
    
    @Test
    @DisplayName("Should classify Magna Cum Laude (GPA 3.5-3.74)")
    void testClassifyMagnaCumLaude() {
        assertEquals("Magna Cum Laude", calculator.getGPAClassification(3.5));
        assertEquals("Magna Cum Laude", calculator.getGPAClassification(3.6));
        assertEquals("Magna Cum Laude", calculator.getGPAClassification(3.74));
    }
    
    @Test
    @DisplayName("Should classify Cum Laude (GPA 3.25-3.49)")
    void testClassifyCumLaude() {
        assertEquals("Cum Laude", calculator.getGPAClassification(3.25));
        assertEquals("Cum Laude", calculator.getGPAClassification(3.35));
        assertEquals("Cum Laude", calculator.getGPAClassification(3.49));
    }
    
    @Test
    @DisplayName("Should classify Dean's List (GPA 3.0-3.24)")
    void testClassifyDeansList() {
        assertEquals("Dean's List", calculator.getGPAClassification(3.0));
        assertEquals("Dean's List", calculator.getGPAClassification(3.1));
        assertEquals("Dean's List", calculator.getGPAClassification(3.24));
    }
    
    @Test
    @DisplayName("Should classify Good Standing (GPA 2.0-2.99)")
    void testClassifyGoodStanding() {
        assertEquals("Good Standing", calculator.getGPAClassification(2.0));
        assertEquals("Good Standing", calculator.getGPAClassification(2.5));
        assertEquals("Good Standing", calculator.getGPAClassification(2.99));
    }
    
    @Test
    @DisplayName("Should classify Academic Probation (GPA > 0 and < 2.0)")
    void testClassifyAcademicProbation() {
        assertEquals("Academic Probation", calculator.getGPAClassification(0.5));
        assertEquals("Academic Probation", calculator.getGPAClassification(1.5));
        assertEquals("Academic Probation", calculator.getGPAClassification(1.99));
    }
    
    @Test
    @DisplayName("Should classify No Grades for zero GPA")
    void testClassifyNoGrades() {
        assertEquals("No Grades", calculator.getGPAClassification(0.0));
    }

    // ==================== Honors and Probation Tests ====================
    
    @Test
    @DisplayName("Should qualify for honors with GPA >= 3.25")
    void testQualifiesForHonors() {
        Course course = new Course("Course", 3);
        course.addAssignment(new Assignment("Test", 92.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        assertTrue(calculator.qualifiesForHonors(student));
    }
    
    @Test
    @DisplayName("Should not qualify for honors with GPA < 3.25")
    void testDoesNotQualifyForHonors() {
        Course course = new Course("Course", 3);
        course.addAssignment(new Assignment("Test", 82.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        assertFalse(calculator.qualifiesForHonors(student));
    }
    
    @Test
    @DisplayName("Should be on probation with GPA < 2.0")
    void testIsOnProbation() {
        Course course = new Course("Course", 3);
        course.addAssignment(new Assignment("Test", 65.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        assertTrue(calculator.isOnProbation(student));
    }
    
    @Test
    @DisplayName("Should not be on probation with GPA >= 2.0")
    void testNotOnProbation() {
        Course course = new Course("Course", 3);
        course.addAssignment(new Assignment("Test", 75.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        assertFalse(calculator.isOnProbation(student));
    }

    // ==================== Format GPA Tests ====================
    
    @Test
    @DisplayName("Should format GPA to two decimal places")
    void testFormatGpa() {
        assertEquals("3.57", calculator.formatGPA(3.571428));
        assertEquals("4.00", calculator.formatGPA(4.0));
        assertEquals("2.50", calculator.formatGPA(2.5));
    }
    
    @Test
    @DisplayName("Should format GPA with rounding")
    void testFormatGpaRounding() {
        assertEquals("3.57", calculator.formatGPA(3.5749));
        assertEquals("3.58", calculator.formatGPA(3.575));
        assertEquals("0.00", calculator.formatGPA(0.0));
    }

    // ==================== Total Grade Points Tests ====================
    
    @Test
    @DisplayName("Should calculate total grade points for student")
    void testTotalGradePoints() {
        // Math (4 credits): A (4.0) → 16 points
        Course math = new Course("Math", 4);
        math.addAssignment(new Assignment("Test", 95.0, 100.0, Category.FINAL_EXAM));
        
        // English (3 credits): B (3.0) → 9 points
        Course english = new Course("English", 3);
        english.addAssignment(new Assignment("Test", 85.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(math);
        student.enrollInCourse(english);
        
        double totalPoints = calculator.calculateTotalGradePoints(student);
        
        // Expected: 4.0*4 + 3.0*3 = 16 + 9 = 25
        assertEquals(25.0, totalPoints, 0.01);
    }
    
    @Test
    @DisplayName("Should return zero grade points for student with no graded courses")
    void testZeroGradePoints() {
        double totalPoints = calculator.calculateTotalGradePoints(student);
        assertEquals(0.0, totalPoints, 0.01);
    }

    // ==================== Edge Cases ====================
    
    @Test
    @DisplayName("Should handle perfect GPA (4.0)")
    void testPerfectGpa() {
        Course course = new Course("Perfect", 3);
        course.addAssignment(new Assignment("Test", 100.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        double gpa = calculator.calculateGPA(student);
        assertEquals(4.0, gpa, 0.01);
    }
    
    @Test
    @DisplayName("Should handle zero GPA from failed courses")
    void testZeroGpa() {
        Course course = new Course("Failed", 3);
        course.addAssignment(new Assignment("Test", 50.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        double gpa = calculator.calculateGPA(student);
        assertEquals(0.0, gpa, 0.01);
    }
    
    @Test
    @DisplayName("Should handle many courses correctly")
    void testManyCourses() {
        for (int i = 1; i <= 8; i++) {
            Course course = new Course("Course" + i, 3);
            course.addAssignment(new Assignment("Test", 85.0, 100.0, Category.FINAL_EXAM));
            student.enrollInCourse(course);
        }
        
        double gpa = calculator.calculateGPA(student);
        assertEquals(3.0, gpa, 0.01);
    }

    // ==================== Null Validation Tests ====================
    
    @Test
    @DisplayName("Should throw exception for null student in calculateGPA")
    void testNullStudentCalculateGpa() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateGPA((Student) null);
        });
    }
    
    @Test
    @DisplayName("Should throw exception for null course collection")
    void testNullCourseCollection() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateGPA((Collection<Course>) null);
        });
    }
}
