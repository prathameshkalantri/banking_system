package com.prathamesh.gradebook.calculator;

import com.prathamesh.gradebook.domain.Assignment;
import com.prathamesh.gradebook.domain.Category;
import com.prathamesh.gradebook.domain.Course;
import com.prathamesh.gradebook.domain.LetterGrade;
import com.prathamesh.gradebook.domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for GPACalculator.
 * 
 * Test Coverage:
 * - GPA calculation for student with multiple courses
 * - GPA calculation for collection of courses
 * - GPA calculation from course grades
 * - Credit-weighted GPA logic verification
 * - Honor roll determination
 * - Edge cases (no courses, single course, all same grade)
 * - Null parameter validation
 * 
 * @author Prathamesh Kalantri
 */
class GPACalculatorTest {
    
    private GPACalculator gpaCalculator;
    private GradeCalculator gradeCalculator;
    private Student student;
    
    @BeforeEach
    void setUp() {
        gradeCalculator = new GradeCalculator();
        gpaCalculator = new GPACalculator(gradeCalculator);
        student = new Student("S-12345678", "John Doe");
    }
    
    // ========== Constructor Tests ==========
    
    @Test
    void testCreateGPACalculatorWithDefaultConstructor() {
        GPACalculator calc = new GPACalculator();
        assertNotNull(calc);
    }
    
    @Test
    void testCreateGPACalculatorWithGradeCalculator() {
        GPACalculator calc = new GPACalculator(gradeCalculator);
        assertNotNull(calc);
    }
    
    @Test
    void testCreateGPACalculatorWithNullGradeCalculatorThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new GPACalculator(null);
        });
        
        assertEquals("Grade calculator cannot be null", exception.getMessage());
    }
    
    // ========== GPA Calculation for Student Tests ==========
    
    @Test
    void testCalculateGPAWithSingleCourse() {
        Course course = createCourseWithGrade("Data Structures", 3, 85.0); // B = 3.0
        student.enrollInCourse(course);
        
        double gpa = gpaCalculator.calculateGPA(student);
        
        assertEquals(3.0, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAWithMultipleCoursesSameCredits() {
        // All 3 credit courses
        Course course1 = createCourseWithGrade("Data Structures", 3, 85.0); // B = 3.0
        Course course2 = createCourseWithGrade("Algorithms", 3, 95.0);       // A = 4.0
        Course course3 = createCourseWithGrade("Databases", 3, 75.0);        // C = 2.0
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        student.enrollInCourse(course3);
        
        double gpa = gpaCalculator.calculateGPA(student);
        
        // (3.0*3 + 4.0*3 + 2.0*3) / (3+3+3) = 27/9 = 3.0
        assertEquals(3.0, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAWithMultipleCoursesVariedCredits() {
        Course course1 = createCourseWithGrade("Data Structures", 4, 85.0); // B = 3.0, 4 credits
        Course course2 = createCourseWithGrade("Algorithms", 3, 95.0);       // A = 4.0, 3 credits
        Course course3 = createCourseWithGrade("Seminar", 1, 75.0);          // C = 2.0, 1 credit
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        student.enrollInCourse(course3);
        
        double gpa = gpaCalculator.calculateGPA(student);
        
        // (3.0*4 + 4.0*3 + 2.0*1) / (4+3+1) = (12 + 12 + 2) / 8 = 26/8 = 3.25
        assertEquals(3.25, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAWithAllAPerfect() {
        Course course1 = createCourseWithGrade("Course1", 3, 95.0); // A = 4.0
        Course course2 = createCourseWithGrade("Course2", 4, 92.0); // A = 4.0
        Course course3 = createCourseWithGrade("Course3", 3, 98.0); // A = 4.0
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        student.enrollInCourse(course3);
        
        double gpa = gpaCalculator.calculateGPA(student);
        
        assertEquals(4.0, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAWithAllFFailing() {
        Course course1 = createCourseWithGrade("Course1", 3, 45.0); // F = 0.0
        Course course2 = createCourseWithGrade("Course2", 4, 50.0); // F = 0.0
        Course course3 = createCourseWithGrade("Course3", 3, 55.0); // F = 0.0
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        student.enrollInCourse(course3);
        
        double gpa = gpaCalculator.calculateGPA(student);
        
        assertEquals(0.0, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAWithNoCourses() {
        double gpa = gpaCalculator.calculateGPA(student);
        
        assertEquals(0.0, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAWithNullStudentThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gpaCalculator.calculateGPA((Student) null);
        });
        
        assertEquals("Student cannot be null", exception.getMessage());
    }
    
    // ========== GPA Calculation for Course Collection Tests ==========
    
    @Test
    void testCalculateGPAFromCourseCollection() {
        List<Course> courses = Arrays.asList(
            createCourseWithGrade("Course1", 3, 85.0), // B = 3.0
            createCourseWithGrade("Course2", 4, 95.0), // A = 4.0
            createCourseWithGrade("Course3", 3, 75.0)  // C = 2.0
        );
        
        double gpa = gpaCalculator.calculateGPA(courses);
        
        // (3.0*3 + 4.0*4 + 2.0*3) / (3+4+3) = (9 + 16 + 6) / 10 = 31/10 = 3.1
        assertEquals(3.1, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAFromEmptyCourseCollection() {
        List<Course> courses = new ArrayList<>();
        
        double gpa = gpaCalculator.calculateGPA(courses);
        
        assertEquals(0.0, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAFromNullCourseCollectionThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gpaCalculator.calculateGPA((List<Course>) null);
        });
        
        assertEquals("Courses cannot be null", exception.getMessage());
    }
    
    // ========== GPA Calculation from CourseGrade List Tests ==========
    
    @Test
    void testCalculateGPAFromGrades() {
        Course course1 = createCourseWithGrade("Course1", 3, 85.0);
        Course course2 = createCourseWithGrade("Course2", 4, 95.0);
        
        CourseGrade grade1 = gradeCalculator.calculateCourseGrade(course1);
        CourseGrade grade2 = gradeCalculator.calculateCourseGrade(course2);
        
        List<CourseGrade> grades = Arrays.asList(grade1, grade2);
        
        double gpa = gpaCalculator.calculateGPAFromGrades(grades);
        
        // (3.0*3 + 4.0*4) / (3+4) = (9 + 16) / 7 = 25/7 = 3.571
        assertEquals(3.571, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAFromEmptyGradesList() {
        List<CourseGrade> grades = new ArrayList<>();
        
        double gpa = gpaCalculator.calculateGPAFromGrades(grades);
        
        assertEquals(0.0, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAFromNullGradesListThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gpaCalculator.calculateGPAFromGrades(null);
        });
        
        assertEquals("Course grades cannot be null", exception.getMessage());
    }
    
    // ========== Calculate All Course Grades Tests ==========
    
    @Test
    void testCalculateAllCourseGrades() {
        Course course1 = createCourseWithGrade("Data Structures", 3, 85.0);
        Course course2 = createCourseWithGrade("Algorithms", 4, 95.0);
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        
        List<CourseGrade> grades = gpaCalculator.calculateAllCourseGrades(student);
        
        assertEquals(2, grades.size());
        
        // Verify grades contain correct courses (order may vary)
        List<String> courseNames = Arrays.asList(
            grades.get(0).getCourseName(),
            grades.get(1).getCourseName()
        );
        assertTrue(courseNames.contains("Data Structures"));
        assertTrue(courseNames.contains("Algorithms"));
    }
    
    @Test
    void testCalculateAllCourseGradesWithNoCourses() {
        List<CourseGrade> grades = gpaCalculator.calculateAllCourseGrades(student);
        
        assertTrue(grades.isEmpty());
    }
    
    @Test
    void testCalculateAllCourseGradesWithNullStudentThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gpaCalculator.calculateAllCourseGrades(null);
        });
        
        assertEquals("Student cannot be null", exception.getMessage());
    }
    
    // ========== Honor Roll Tests ==========
    
    @Test
    void testIsHonorRollWithGPA_3_5() {
        // GPA exactly 3.5
        Course course1 = createCourseWithGrade("Course1", 3, 85.0); // B = 3.0
        Course course2 = createCourseWithGrade("Course2", 3, 95.0); // A = 4.0
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        
        // (3.0*3 + 4.0*3) / 6 = 21/6 = 3.5
        assertTrue(gpaCalculator.isHonorRoll(student));
    }
    
    @Test
    void testIsHonorRollWithGPAAbove_3_5() {
        Course course1 = createCourseWithGrade("Course1", 3, 95.0); // A = 4.0
        Course course2 = createCourseWithGrade("Course2", 3, 92.0); // A = 4.0
        Course course3 = createCourseWithGrade("Course3", 3, 85.0); // B = 3.0
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        student.enrollInCourse(course3);
        
        // (4.0*3 + 4.0*3 + 3.0*3) / 9 = 33/9 = 3.667
        assertTrue(gpaCalculator.isHonorRoll(student));
    }
    
    @Test
    void testIsNotHonorRollWithGPABelow_3_5() {
        Course course1 = createCourseWithGrade("Course1", 3, 85.0); // B = 3.0
        Course course2 = createCourseWithGrade("Course2", 3, 85.0); // B = 3.0
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        
        // (3.0*3 + 3.0*3) / 6 = 18/6 = 3.0
        assertFalse(gpaCalculator.isHonorRoll(student));
    }
    
    @Test
    void testIsNotHonorRollWithNoCourses() {
        assertFalse(gpaCalculator.isHonorRoll(student));
    }
    
    @Test
    void testIsHonorRollWithNullStudentThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gpaCalculator.isHonorRoll(null);
        });
        
        assertEquals("Student cannot be null", exception.getMessage());
    }
    
    // ========== Total Credit Hours Tests ==========
    
    @Test
    void testGetTotalCreditHours() {
        Course course1 = createCourseWithGrade("Course1", 3, 85.0);
        Course course2 = createCourseWithGrade("Course2", 4, 95.0);
        Course course3 = createCourseWithGrade("Course3", 2, 75.0);
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        student.enrollInCourse(course3);
        
        int totalCredits = gpaCalculator.getTotalCreditHours(student);
        
        assertEquals(9, totalCredits);
    }
    
    @Test
    void testGetTotalCreditHoursWithNoCourses() {
        int totalCredits = gpaCalculator.getTotalCreditHours(student);
        
        assertEquals(0, totalCredits);
    }
    
    @Test
    void testGetTotalCreditHoursWithNullStudentThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gpaCalculator.getTotalCreditHours(null);
        });
        
        assertEquals("Student cannot be null", exception.getMessage());
    }
    
    // ========== Complex Scenario Tests ==========
    
    @Test
    void testCalculateGPAWithMixedLetterGrades() {
        Course courseA = createCourseWithGrade("Course A", 3, 95.0); // A = 4.0
        Course courseB = createCourseWithGrade("Course B", 3, 85.0); // B = 3.0
        Course courseC = createCourseWithGrade("Course C", 3, 75.0); // C = 2.0
        Course courseD = createCourseWithGrade("Course D", 3, 65.0); // D = 1.0
        Course courseF = createCourseWithGrade("Course F", 3, 55.0); // F = 0.0
        
        student.enrollInCourse(courseA);
        student.enrollInCourse(courseB);
        student.enrollInCourse(courseC);
        student.enrollInCourse(courseD);
        student.enrollInCourse(courseF);
        
        double gpa = gpaCalculator.calculateGPA(student);
        
        // (4.0*3 + 3.0*3 + 2.0*3 + 1.0*3 + 0.0*3) / 15 = 30/15 = 2.0
        assertEquals(2.0, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAWithVaryingCreditHoursAndGrades() {
        Course course1 = createCourseWithGrade("CS101", 4, 92.0);  // A = 4.0, 4 credits
        Course course2 = createCourseWithGrade("MATH201", 5, 88.0); // B = 3.0, 5 credits
        Course course3 = createCourseWithGrade("ENGL101", 3, 78.0); // C = 2.0, 3 credits
        Course course4 = createCourseWithGrade("PE101", 1, 95.0);  // A = 4.0, 1 credit
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        student.enrollInCourse(course3);
        student.enrollInCourse(course4);
        
        double gpa = gpaCalculator.calculateGPA(student);
        
        // (4.0*4 + 3.0*5 + 2.0*3 + 4.0*1) / (4+5+3+1) = (16 + 15 + 6 + 4) / 13 = 41/13 = 3.154
        assertEquals(3.154, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPABoundaryCase_3_499() {
        // Create scenario where GPA is 3.499 (not honor roll)
        Course course1 = createCourseWithGrade("Course1", 1, 85.0); // B = 3.0, 1 credit
        Course course2 = createCourseWithGrade("Course2", 1, 95.0); // A = 4.0, 1 credit
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        
        double gpa = gpaCalculator.calculateGPA(student);
        
        // (3.0*1 + 4.0*1) / 2 = 7/2 = 3.5
        assertEquals(3.5, gpa, 0.01);
        assertTrue(gpaCalculator.isHonorRoll(student));
    }
    
    @Test
    void testCalculateAllCourseGradesMatchesGPACalculation() {
        Course course1 = createCourseWithGrade("Course1", 3, 85.0);
        Course course2 = createCourseWithGrade("Course2", 4, 95.0);
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        
        // Calculate GPA using student method
        double gpaFromStudent = gpaCalculator.calculateGPA(student);
        
        // Calculate GPA using courseGrades method
        List<CourseGrade> courseGrades = gpaCalculator.calculateAllCourseGrades(student);
        double gpaFromGrades = gpaCalculator.calculateGPAFromGrades(courseGrades);
        
        assertEquals(gpaFromStudent, gpaFromGrades, 0.01);
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Creates a course with a single assignment that results in the specified grade.
     * 
     * @param name Course name
     * @param credits Credit hours
     * @param targetGrade Target numeric grade (0-100)
     * @return Course with assignment
     */
    private Course createCourseWithGrade(String name, int credits, double targetGrade) {
        Course course = new Course(name, credits);
        
        // Add a single assignment with the target grade to FINAL_EXAM (which will get 100% weight)
        int points = (int) targetGrade;
        course.addAssignment(new Assignment("Final", points, 100, Category.FINAL_EXAM));
        
        return course;
    }
}
