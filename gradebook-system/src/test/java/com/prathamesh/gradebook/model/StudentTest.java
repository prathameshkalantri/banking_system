package com.prathamesh.gradebook.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Student class.
 * Tests cover student creation, course enrollment, GPA calculation, and academic standing.
 */
@DisplayName("Student Model Tests")
class StudentTest {

    private Student student;
    private Course mathCourse;
    private Course csCourse;
    
    @BeforeEach
    void setUp() {
        student = new Student("S-10001001", "John Doe");
        mathCourse = new Course("Calculus I", 4);
        csCourse = new Course("Data Structures", 3);
    }

    // ==================== Construction Tests ====================
    
    @Test
    @DisplayName("Should create valid student with ID and name")
    void testValidStudentCreation() {
        Student testStudent = new Student("S-12345678", "Jane Smith");
        
        assertEquals("S-12345678", testStudent.getStudentId());
        assertEquals("Jane Smith", testStudent.getName());
        assertTrue(testStudent.getCourses().isEmpty());
        assertEquals(0.0, testStudent.calculateGPA(), 0.001);
    }
    
    @Test
    @DisplayName("Should create student with different valid ID format")
    void testValidIdFormats() {
        Student s1 = new Student("S-00000001", "Student One");
        Student s2 = new Student("S-99999999", "Student Two");
        
        assertEquals("S-00000001", s1.getStudentId());
        assertEquals("S-99999999", s2.getStudentId());
    }

    // ==================== Validation Tests ====================
    
    @Test
    @DisplayName("Should throw exception when student ID is null")
    void testNullStudentId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Student(null, "John Doe");
        });
        
        assertTrue(exception.getMessage().contains("ID") ||
                  exception.getMessage().contains("null"));
    }
    
    @Test
    @DisplayName("Should throw exception when student ID is invalid format")
    void testInvalidStudentIdFormat() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Student("12345678", "John Doe");
        });
        
        assertTrue(exception.getMessage().contains("format") ||
                  exception.getMessage().contains("S-"));
    }
    
    @Test
    @DisplayName("Should throw exception when student ID has wrong length")
    void testInvalidStudentIdLength() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Student("S-123", "John Doe");
        });
        
        assertTrue(exception.getMessage().contains("format") ||
                  exception.getMessage().contains("digit"));
    }
    
    @Test
    @DisplayName("Should throw exception when name is null")
    void testNullName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Student("S-10001001", null);
        });
        
        assertTrue(exception.getMessage().contains("name") ||
                  exception.getMessage().contains("null"));
    }
    
    @Test
    @DisplayName("Should throw exception when name is empty")
    void testEmptyName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Student("S-10001001", "");
        });
        
        assertTrue(exception.getMessage().contains("name") ||
                  exception.getMessage().contains("empty"));
    }

    // ==================== Course Enrollment Tests ====================
    
    @Test
    @DisplayName("Should enroll in course successfully")
    void testEnrollInCourse() {
        student.enrollInCourse(mathCourse);
        
        assertEquals(1, student.getCourses().size());
        assertTrue(student.getCourses().contains(mathCourse));
        assertEquals(4, student.getTotalCreditHours());
    }
    
    @Test
    @DisplayName("Should enroll in multiple courses")
    void testEnrollInMultipleCourses() {
        student.enrollInCourse(mathCourse);
        student.enrollInCourse(csCourse);
        
        assertEquals(2, student.getCourses().size());
        assertEquals(7, student.getTotalCreditHours());
    }
    
    @Test
    @DisplayName("Should throw exception when enrolling in null course")
    void testEnrollInNullCourse() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            student.enrollInCourse(null);
        });
        
        assertTrue(exception.getMessage().contains("null") ||
                  exception.getMessage().contains("Course"));
    }
    
    @Test
    @DisplayName("Should throw exception when enrolling in duplicate course")
    void testEnrollInDuplicateCourse() {
        student.enrollInCourse(mathCourse);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            student.enrollInCourse(mathCourse);
        });
        
        assertTrue(exception.getMessage().contains("already enrolled") ||
                  exception.getMessage().contains("duplicate"));
    }
    
    @Test
    @DisplayName("Should return unmodifiable courses collection")
    void testCoursesCollectionUnmodifiable() {
        student.enrollInCourse(mathCourse);
        
        Collection<Course> courses = student.getCourses();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            courses.add(new Course("Physics", 3));
        });
    }

    // ==================== GPA Calculation Tests ====================
    
    @Test
    @DisplayName("Should return 0.0 GPA when no courses enrolled")
    void testGpaWithNoCourses() {
        assertEquals(0.0, student.calculateGPA(), 0.001);
    }
    
    @Test
    @DisplayName("Should return 0.0 GPA when courses have no assignments")
    void testGpaWithNoAssignments() {
        student.enrollInCourse(mathCourse);
        student.enrollInCourse(csCourse);
        
        assertEquals(0.0, student.calculateGPA(), 0.001);
    }
    
    @Test
    @DisplayName("Should calculate GPA correctly for single course")
    void testGpaSingleCourse() {
        // Add assignments to get 85% (B = 3.0 GPA)
        mathCourse.addAssignment(new Assignment("HW1", 85.0, 100.0, Category.HOMEWORK));
        mathCourse.addAssignment(new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));
        mathCourse.addAssignment(new Assignment("Midterm", 85.0, 100.0, Category.MIDTERM));
        mathCourse.addAssignment(new Assignment("Final", 85.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(mathCourse);
        
        double gpa = student.calculateGPA();
        assertEquals(3.0, gpa, 0.1); // B grade should give 3.0 GPA
    }
    
    @Test
    @DisplayName("Should calculate credit-hour weighted GPA for multiple courses")
    void testGpaMultipleCourses() {
        // Math (4 credits): 92% average → A (4.0)
        mathCourse.addAssignment(new Assignment("HW", 92.0, 100.0, Category.HOMEWORK));
        mathCourse.addAssignment(new Assignment("Quiz", 92.0, 100.0, Category.QUIZZES));
        mathCourse.addAssignment(new Assignment("Mid", 92.0, 100.0, Category.MIDTERM));
        mathCourse.addAssignment(new Assignment("Final", 92.0, 100.0, Category.FINAL_EXAM));
        
        // CS (3 credits): 82% average → B (3.0)
        csCourse.addAssignment(new Assignment("HW", 82.0, 100.0, Category.HOMEWORK));
        csCourse.addAssignment(new Assignment("Quiz", 82.0, 100.0, Category.QUIZZES));
        csCourse.addAssignment(new Assignment("Mid", 82.0, 100.0, Category.MIDTERM));
        csCourse.addAssignment(new Assignment("Final", 82.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(mathCourse);
        student.enrollInCourse(csCourse);
        
        // Expected: (4.0 * 4 + 3.0 * 3) / 7 = 25 / 7 = 3.571
        double gpa = student.calculateGPA();
        assertEquals(3.571, gpa, 0.01);
    }

    // ==================== Academic Standing Tests ====================
    
    @Test
    @DisplayName("Should not qualify for Dean's List with no courses")
    void testDeansListNoCourses() {
        assertFalse(student.isDeansListQualified());
    }
    
    @Test
    @DisplayName("Should not qualify for Dean's List with insufficient credits")
    void testDeansListInsufficientCredits() {
        // Only 3 credits (need >= 12)
        csCourse.addAssignment(new Assignment("HW", 95.0, 100.0, Category.HOMEWORK));
        csCourse.addAssignment(new Assignment("Quiz", 95.0, 100.0, Category.QUIZZES));
        csCourse.addAssignment(new Assignment("Mid", 95.0, 100.0, Category.MIDTERM));
        csCourse.addAssignment(new Assignment("Final", 95.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(csCourse);
        
        assertFalse(student.isDeansListQualified());
    }
    
    @Test
    @DisplayName("Should not qualify for Dean's List with low GPA")
    void testDeansListLowGpa() {
        // Create 4 courses for 13 credits, but with low grades
        Course c1 = new Course("Course 1", 3);
        Course c2 = new Course("Course 2", 4);
        Course c3 = new Course("Course 3", 3);
        Course c4 = new Course("Course 4", 3);
        
        // All courses get 70% (C = 2.0 GPA)
        for (Course course : new Course[]{c1, c2, c3, c4}) {
            course.addAssignment(new Assignment("HW", 70.0, 100.0, Category.HOMEWORK));
            course.addAssignment(new Assignment("Quiz", 70.0, 100.0, Category.QUIZZES));
            course.addAssignment(new Assignment("Mid", 70.0, 100.0, Category.MIDTERM));
            course.addAssignment(new Assignment("Final", 70.0, 100.0, Category.FINAL_EXAM));
            student.enrollInCourse(course);
        }
        
        assertFalse(student.isDeansListQualified());
    }
    
    @Test
    @DisplayName("Should qualify for Dean's List with GPA >= 3.5 and >= 12 credits")
    void testDeansListQualified() {
        // Create 4 courses for 13 credits with high grades
        Course c1 = new Course("Course 1", 3);
        Course c2 = new Course("Course 2", 4);
        Course c3 = new Course("Course 3", 3);
        Course c4 = new Course("Course 4", 3);
        
        // All courses get 92% (A = 4.0 GPA, but we'll use consistent high score)
        for (Course course : new Course[]{c1, c2, c3, c4}) {
            course.addAssignment(new Assignment("HW", 92.0, 100.0, Category.HOMEWORK));
            course.addAssignment(new Assignment("Quiz", 92.0, 100.0, Category.QUIZZES));
            course.addAssignment(new Assignment("Mid", 92.0, 100.0, Category.MIDTERM));
            course.addAssignment(new Assignment("Final", 92.0, 100.0, Category.FINAL_EXAM));
            student.enrollInCourse(course);
        }
        
        assertTrue(student.isDeansListQualified());
    }
    
    @Test
    @DisplayName("Should not be on probation with no courses")
    void testProbationNoCourses() {
        assertFalse(student.isOnProbation());
    }
    
    @Test
    @DisplayName("Should be on probation with GPA < 2.0")
    void testProbationLowGpa() {
        // Add course with D grades (65% = D = 1.0 GPA, which is > 0 but < 2.0)
        mathCourse.addAssignment(new Assignment("HW", 65.0, 100.0, Category.HOMEWORK));
        mathCourse.addAssignment(new Assignment("Quiz", 65.0, 100.0, Category.QUIZZES));
        mathCourse.addAssignment(new Assignment("Mid", 65.0, 100.0, Category.MIDTERM));
        mathCourse.addAssignment(new Assignment("Final", 65.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(mathCourse);
        
        assertTrue(student.isOnProbation());
    }
    
    @Test
    @DisplayName("Should not be on probation with GPA >= 2.0")
    void testNotOnProbation() {
        // Add course with passing grades (75% = C = 2.0 GPA)
        mathCourse.addAssignment(new Assignment("HW", 75.0, 100.0, Category.HOMEWORK));
        mathCourse.addAssignment(new Assignment("Quiz", 75.0, 100.0, Category.QUIZZES));
        mathCourse.addAssignment(new Assignment("Mid", 75.0, 100.0, Category.MIDTERM));
        mathCourse.addAssignment(new Assignment("Final", 75.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(mathCourse);
        
        assertFalse(student.isOnProbation());
    }

    // ==================== Credit Hours Tests ====================
    
    @Test
    @DisplayName("Should return 0 total credit hours with no courses")
    void testCreditHoursNoCourses() {
        assertEquals(0, student.getTotalCreditHours());
    }
    
    @Test
    @DisplayName("Should calculate total credit hours correctly")
    void testTotalCreditHours() {
        student.enrollInCourse(mathCourse); // 4 credits
        student.enrollInCourse(csCourse);   // 3 credits
        
        assertEquals(7, student.getTotalCreditHours());
    }
    
    @Test
    @DisplayName("Should calculate graded credit hours same as total when all have assignments")
    void testGradedCreditHoursWithAssignments() {
        mathCourse.addAssignment(new Assignment("HW", 85.0, 100.0, Category.HOMEWORK));
        csCourse.addAssignment(new Assignment("HW", 85.0, 100.0, Category.HOMEWORK));
        
        student.enrollInCourse(mathCourse);
        student.enrollInCourse(csCourse);
        
        assertEquals(7, student.getGradedCreditHours());
    }
    
    @Test
    @DisplayName("Should return 0 graded credit hours when no courses have assignments")
    void testGradedCreditHoursNoAssignments() {
        student.enrollInCourse(mathCourse);
        student.enrollInCourse(csCourse);
        
        assertEquals(0, student.getGradedCreditHours());
    }

    // ==================== toString Tests ====================
    
    @Test
    @DisplayName("Should have meaningful toString representation")
    void testToString() {
        String toString = student.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("S-10001001") && toString.contains("John Doe"));
    }

    // ==================== Edge Cases ====================
    
    @Test
    @DisplayName("Should handle student with many courses")
    void testManyCourses() {
        for (int i = 1; i <= 6; i++) {
            Course course = new Course("Course " + i, 3);
            course.addAssignment(new Assignment("HW", 85.0, 100.0, Category.HOMEWORK));
            student.enrollInCourse(course);
        }
        
        assertEquals(6, student.getCourses().size());
        assertEquals(18, student.getTotalCreditHours());
    }
    
    @Test
    @DisplayName("Should handle mixed performance across courses")
    void testMixedPerformance() {
        Course excellentCourse = new Course("Easy Course", 3);
        excellentCourse.addAssignment(new Assignment("HW", 95.0, 100.0, Category.HOMEWORK));
        excellentCourse.addAssignment(new Assignment("Quiz", 95.0, 100.0, Category.QUIZZES));
        excellentCourse.addAssignment(new Assignment("Mid", 95.0, 100.0, Category.MIDTERM));
        excellentCourse.addAssignment(new Assignment("Final", 95.0, 100.0, Category.FINAL_EXAM));
        
        Course poorCourse = new Course("Hard Course", 3);
        poorCourse.addAssignment(new Assignment("HW", 65.0, 100.0, Category.HOMEWORK));
        poorCourse.addAssignment(new Assignment("Quiz", 65.0, 100.0, Category.QUIZZES));
        poorCourse.addAssignment(new Assignment("Mid", 65.0, 100.0, Category.MIDTERM));
        poorCourse.addAssignment(new Assignment("Final", 65.0, 100.0, Category.FINAL_EXAM));
        
        student.enrollInCourse(excellentCourse);
        student.enrollInCourse(poorCourse);
        
        double gpa = student.calculateGPA();
        // Expected: (4.0 * 3 + 1.0 * 3) / 6 = 15 / 6 = 2.5
        assertEquals(2.5, gpa, 0.1);
    }
}
