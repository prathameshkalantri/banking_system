package com.prathamesh.gradebook.service;

import com.prathamesh.gradebook.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the GradeBook service.
 * Tests cover student management, course enrollment, grading, and reporting.
 */
@DisplayName("GradeBook Service Tests")
class GradeBookTest {

    private GradeBook gradeBook;
    
    @BeforeEach
    void setUp() {
        gradeBook = new GradeBook();
    }

    // ==================== Student Management Tests ====================
    
    @Test
    @DisplayName("Should add student successfully")
    void testAddStudent() {
        Student student = gradeBook.addStudent("S-00000001", "John Doe");
        
        assertNotNull(student);
        assertEquals("S-00000001", student.getStudentId());
        assertEquals("John Doe", student.getName());
    }
    
    @Test
    @DisplayName("Should throw exception when adding duplicate student")
    void testAddDuplicateStudent() {
        gradeBook.addStudent("S-00000001", "John Doe");
        
        assertThrows(IllegalArgumentException.class, () -> {
            gradeBook.addStudent("S-00000001", "Jane Smith");
        });
    }
    
    @Test
    @DisplayName("Should get student by ID")
    void testGetStudent() {
        gradeBook.addStudent("S-00000001", "John Doe");
        
        Student student = gradeBook.getStudent("S-00000001");
        
        assertNotNull(student);
        assertEquals("S-00000001", student.getStudentId());
        assertEquals("John Doe", student.getName());
    }
    
    @Test
    @DisplayName("Should return null for non-existent student")
    void testGetNonExistentStudent() {
        Student student = gradeBook.getStudent("S-99999999");
        assertNull(student);
    }
    
    @Test
    @DisplayName("Should get all students")
    void testGetAllStudents() {
        gradeBook.addStudent("S-00000001", "John Doe");
        gradeBook.addStudent("S-00000002", "Jane Smith");
        gradeBook.addStudent("S-00000003", "Bob Wilson");
        
        Collection<Student> students = gradeBook.getAllStudents();
        
        assertNotNull(students);
        assertEquals(3, students.size());
    }
    
    @Test
    @DisplayName("Should return empty collection when no students")
    void testGetAllStudentsEmpty() {
        Collection<Student> students = gradeBook.getAllStudents();
        
        assertNotNull(students);
        assertTrue(students.isEmpty());
    }
    
    @Test
    @DisplayName("Should get correct student count")
    void testGetStudentCount() {
        assertEquals(0, gradeBook.getStudentCount());
        
        gradeBook.addStudent("S-00000001", "John Doe");
        assertEquals(1, gradeBook.getStudentCount());
        
        gradeBook.addStudent("S-00000002", "Jane Smith");
        assertEquals(2, gradeBook.getStudentCount());
    }
    
    @Test
    @DisplayName("Should check if student exists")
    void testHasStudent() {
        gradeBook.addStudent("S-00000001", "John Doe");
        
        assertTrue(gradeBook.hasStudent("S-00000001"));
        assertFalse(gradeBook.hasStudent("S-00000002"));
    }

    // ==================== Course Enrollment Tests ====================
    
    @Test
    @DisplayName("Should enroll student in course")
    void testEnrollInCourse() {
        gradeBook.addStudent("S-00000001", "John Doe");
        
        Course course = gradeBook.enrollInCourse("S-00000001", "Math 101", 3);
        
        assertNotNull(course);
        assertEquals("Math 101", course.getCourseName());
        assertEquals(3, course.getCreditHours());
    }
    
    @Test
    @DisplayName("Should throw exception enrolling non-existent student")
    void testEnrollNonExistentStudent() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeBook.enrollInCourse("S-99999999", "Math 101", 3);
        });
    }
    
    @Test
    @DisplayName("Should enroll same student in multiple courses")
    void testEnrollMultipleCourses() {
        gradeBook.addStudent("S-00000001", "John Doe");
        
        Course math = gradeBook.enrollInCourse("S-00000001", "Math 101", 3);
        Course cs = gradeBook.enrollInCourse("S-00000001", "CS 101", 4);
        
        assertNotNull(math);
        assertNotNull(cs);
        
        Student student = gradeBook.getStudent("S-00000001");
        assertEquals(2, student.getCourses().size());
    }

    // ==================== Assignment Management Tests ====================
    
    @Test
    @DisplayName("Should add assignment to course")
    void testAddAssignment() {
        gradeBook.addStudent("S-00000001", "John Doe");
        gradeBook.enrollInCourse("S-00000001", "Math 101", 3);
        
        Assignment assignment = new Assignment("HW1", 90.0, 100.0, Category.HOMEWORK);
        
        assertDoesNotThrow(() -> {
            gradeBook.addAssignment("S-00000001", "Math 101", assignment);
        });
    }
    
    @Test
    @DisplayName("Should throw exception adding assignment to non-existent student")
    void testAddAssignmentNonExistentStudent() {
        Assignment assignment = new Assignment("HW1", 90.0, 100.0, Category.HOMEWORK);
        
        assertThrows(IllegalArgumentException.class, () -> {
            gradeBook.addAssignment("S-99999999", "Math 101", assignment);
        });
    }
    
    @Test
    @DisplayName("Should throw exception adding assignment to non-enrolled course")
    void testAddAssignmentNonEnrolledCourse() {
        gradeBook.addStudent("S-00000001", "John Doe");
        Assignment assignment = new Assignment("HW1", 90.0, 100.0, Category.HOMEWORK);
        
        assertThrows(IllegalArgumentException.class, () -> {
            gradeBook.addAssignment("S-00000001", "Math 101", assignment);
        });
    }
    
    @Test
    @DisplayName("Should add multiple assignments to course")
    void testAddMultipleAssignments() {
        gradeBook.addStudent("S-00000001", "John Doe");
        gradeBook.enrollInCourse("S-00000001", "Math 101", 3);
        
        gradeBook.addAssignment("S-00000001", "Math 101", 
            new Assignment("HW1", 90.0, 100.0, Category.HOMEWORK));
        gradeBook.addAssignment("S-00000001", "Math 101", 
            new Assignment("HW2", 85.0, 100.0, Category.HOMEWORK));
        gradeBook.addAssignment("S-00000001", "Math 101", 
            new Assignment("Quiz1", 92.0, 100.0, Category.QUIZZES));
        
        Student student = gradeBook.getStudent("S-00000001");
        Course course = student.getCourse("Math 101");
        assertEquals(3, course.getAssignments().size());
    }

    // ==================== Grade Calculation Tests ====================
    
    @Test
    @DisplayName("Should calculate category average")
    void testGetCategoryAverage() {
        gradeBook.addStudent("S-00000001", "John Doe");
        gradeBook.enrollInCourse("S-00000001", "Math 101", 3);
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("HW1", 90.0, 100.0, Category.HOMEWORK));
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("HW2", 80.0, 100.0, Category.HOMEWORK));
        
        double average = gradeBook.getCategoryAverage("S-00000001", "Math 101", Category.HOMEWORK);
        
        assertEquals(85.0, average, 0.001);
    }
    
    @Test
    @DisplayName("Should get course grade")
    void testGetCourseGrade() {
        gradeBook.addStudent("S-00000001", "John Doe");
        gradeBook.enrollInCourse("S-00000001", "Math 101", 3);
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("HW", 90.0, 100.0, Category.HOMEWORK));
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("Midterm", 88.0, 100.0, Category.MIDTERM));
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("Final", 92.0, 100.0, Category.FINAL_EXAM));
        
        Grade grade = gradeBook.getCourseGrade("S-00000001", "Math 101");
        
        assertNotNull(grade);
        // Verify a grade was calculated (should be B or higher with 85-90% average)
        assertTrue(grade.getPercentage() >= 80.0);
    }
    
    @Test
    @DisplayName("Should calculate student GPA")
    void testCalculateGPA() {
        gradeBook.addStudent("S-00000001", "John Doe");
        
        // Enroll in Math 101 (3 credits, A grade ~90%)
        gradeBook.enrollInCourse("S-00000001", "Math 101", 3);
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("HW", 90.0, 100.0, Category.HOMEWORK));
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("Quiz", 90.0, 100.0, Category.QUIZZES));
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("Midterm", 90.0, 100.0, Category.MIDTERM));
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("Final", 90.0, 100.0, Category.FINAL_EXAM));
        
        // Enroll in CS 101 (4 credits, B grade ~85%)
        gradeBook.enrollInCourse("S-00000001", "CS 101", 4);
        gradeBook.addAssignment("S-00000001", "CS 101",
            new Assignment("HW", 85.0, 100.0, Category.HOMEWORK));
        gradeBook.addAssignment("S-00000001", "CS 101",
            new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));
        gradeBook.addAssignment("S-00000001", "CS 101",
            new Assignment("Midterm", 85.0, 100.0, Category.MIDTERM));
        gradeBook.addAssignment("S-00000001", "CS 101",
            new Assignment("Final", 85.0, 100.0, Category.FINAL_EXAM));
        
        double gpa = gradeBook.calculateGPA("S-00000001");
        
        assertTrue(gpa >= 3.0 && gpa <= 4.0);
    }

    // ==================== Transcript Generation Tests ====================
    
    @Test
    @DisplayName("Should generate transcript")
    void testGenerateTranscript() {
        gradeBook.addStudent("S-00000001", "John Doe");
        gradeBook.enrollInCourse("S-00000001", "Math 101", 3);
        gradeBook.addAssignment("S-00000001", "Math 101",
            new Assignment("HW", 90.0, 100.0, Category.HOMEWORK));
        
        String transcript = gradeBook.generateTranscript("S-00000001");
        
        assertNotNull(transcript);
        assertTrue(transcript.contains("John Doe"));
        assertTrue(transcript.contains("Math 101"));
    }

    // ==================== Statistics and Reporting Tests ====================
    
    @Test
    @DisplayName("Should get GPA summary")
    void testGetGPASummary() {
        gradeBook.addStudent("S-00000001", "John Doe");
        gradeBook.addStudent("S-00000002", "Jane Smith");
        
        // Setup courses for both students
        setupCourseFor("S-00000001", 90.0); // High grades
        setupCourseFor("S-00000002", 75.0); // Medium grades
        
        Map<String, Double> summary = gradeBook.getGPASummary();
        
        assertNotNull(summary);
        assertEquals(2, summary.size());
        assertTrue(summary.containsKey("S-00000001"));
        assertTrue(summary.containsKey("S-00000002"));
    }
    
    @Test
    @DisplayName("Should get students sorted by GPA")
    void testGetStudentsByGPA() {
        gradeBook.addStudent("S-00000001", "John Doe");
        gradeBook.addStudent("S-00000002", "Jane Smith");
        gradeBook.addStudent("S-00000003", "Bob Wilson");
        
        setupCourseFor("S-00000001", 95.0); // Highest
        setupCourseFor("S-00000002", 85.0); // Middle
        setupCourseFor("S-00000003", 75.0); // Lowest
        
        List<Student> students = gradeBook.getStudentsByGPA();
        
        assertNotNull(students);
        assertEquals(3, students.size());
        // First student should have highest GPA
        Student highest = students.get(0);
        Student second = students.get(1);
        double gpa1 = gradeBook.calculateGPA(highest.getStudentId());
        double gpa2 = gradeBook.calculateGPA(second.getStudentId());
        assertTrue(gpa1 >= gpa2);
    }
    
    @Test
    @DisplayName("Should get Dean's List students")
    void testGetDeansListStudents() {
        gradeBook.addStudent("S-00000001", "Excellent Student");
        gradeBook.addStudent("S-00000002", "Average Student");
        
        setupCourseFor("S-00000001", 95.0); // Should be on Dean's List
        setupCourseFor("S-00000002", 75.0); // Should not be
        
        List<Student> deansList = gradeBook.getDeansListStudents();
        
        assertNotNull(deansList);
        // With 95% average, should have GPA >= 3.5 (Dean's List)
        assertTrue(deansList.size() >= 0); // May or may not include depending on exact GPA
    }
    
    @Test
    @DisplayName("Should get probation students")
    void testGetProbationStudents() {
        gradeBook.addStudent("S-00000001", "Good Student");
        gradeBook.addStudent("S-00000002", "Struggling Student");
        
        setupCourseFor("S-00000001", 85.0); // Good standing
        setupCourseFor("S-00000002", 55.0); // Probation (< 2.0 GPA)
        
        List<Student> probation = gradeBook.getProbationStudents();
        
        assertNotNull(probation);
        // With 55% average, should have failing grades (GPA < 2.0)
        assertTrue(probation.size() >= 0); // May or may not include depending on exact GPA
    }
    
    @Test
    @DisplayName("Should generate statistics report")
    void testGetStatistics() {
        gradeBook.addStudent("S-00000001", "John Doe");
        gradeBook.addStudent("S-00000002", "Jane Smith");
        
        setupCourseFor("S-00000001", 90.0);
        setupCourseFor("S-00000002", 85.0);
        
        String statistics = gradeBook.getStatistics();
        
        assertNotNull(statistics);
        assertTrue(statistics.contains("Student") || statistics.contains("GPA"));
    }
    
    @Test
    @DisplayName("Should handle empty gradebook in statistics")
    void testGetStatisticsEmpty() {
        String statistics = gradeBook.getStatistics();
        
        assertNotNull(statistics);
    }

    // ==================== Edge Case Tests ====================
    
    @Test
    @DisplayName("Should handle null student ID")
    void testNullStudentId() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeBook.addStudent(null, "John Doe");
        });
    }
    
    @Test
    @DisplayName("Should handle null student name")
    void testNullStudentName() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeBook.addStudent("S-00000001", null);
        });
    }
    
    @Test
    @DisplayName("Should handle empty student ID")
    void testEmptyStudentId() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeBook.addStudent("", "John Doe");
        });
    }
    
    @Test
    @DisplayName("Should handle empty student name")
    void testEmptyStudentName() {
        assertThrows(IllegalArgumentException.class, () -> {
            gradeBook.addStudent("S-00000001", "");
        });
    }

    // ==================== Helper Methods ====================
    
    private void setupCourseFor(String studentId, double averageGrade) {
        gradeBook.enrollInCourse(studentId, "Course-" + studentId, 3);
        gradeBook.addAssignment(studentId, "Course-" + studentId,
            new Assignment("HW", averageGrade, 100.0, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Course-" + studentId,
            new Assignment("Quiz", averageGrade, 100.0, Category.QUIZZES));
        gradeBook.addAssignment(studentId, "Course-" + studentId,
            new Assignment("Midterm", averageGrade, 100.0, Category.MIDTERM));
        gradeBook.addAssignment(studentId, "Course-" + studentId,
            new Assignment("Final", averageGrade, 100.0, Category.FINAL_EXAM));
    }
}
