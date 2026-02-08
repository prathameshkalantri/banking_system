package com.prathamesh.gradebook.service;

import com.prathamesh.gradebook.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the TranscriptGenerator service.
 * Tests cover transcript generation, grade reports, and course breakdowns.
 */
@DisplayName("TranscriptGenerator Service Tests")
class TranscriptGeneratorTest {

    private TranscriptGenerator generator;
    private GPACalculator gpaCalculator;
    private GradeCalculator gradeCalculator;
    
    @BeforeEach
    void setUp() {
        generator = new TranscriptGenerator();
        gpaCalculator = new GPACalculator();
        gradeCalculator = new GradeCalculator();
    }

    // ==================== Transcript Generation Tests ====================
    
    @Test
    @DisplayName("Should generate transcript for student with single course")
    void testTranscriptSingleCourse() {
        Student student = new Student("S-00000001", "John Doe");
        Course course = new Course("Math 101", 3);
        course.addAssignment(new Assignment("HW", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 88.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 92.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        String transcript = generator.generateTranscript(student);
        
        assertNotNull(transcript);
        assertTrue(transcript.contains("John Doe"));
        assertTrue(transcript.contains("S-00000001"));
        assertTrue(transcript.contains("Math 101"));
        assertTrue(transcript.contains("GPA"));
    }
    
    @Test
    @DisplayName("Should generate transcript for student with multiple courses")
    void testTranscriptMultipleCourses() {
        Student student = new Student("S-00000002", "Jane Smith");
        
        Course math = new Course("Math 101", 3);
        math.addAssignment(new Assignment("HW", 95.0, 100.0, Category.HOMEWORK));
        math.addAssignment(new Assignment("Quiz", 90.0, 100.0, Category.QUIZZES));
        math.addAssignment(new Assignment("Midterm", 92.0, 100.0, Category.MIDTERM));
        math.addAssignment(new Assignment("Final", 94.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(math);
        
        Course cs = new Course("CS 101", 4);
        cs.addAssignment(new Assignment("HW", 88.0, 100.0, Category.HOMEWORK));
        cs.addAssignment(new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));
        cs.addAssignment(new Assignment("Midterm", 90.0, 100.0, Category.MIDTERM));
        cs.addAssignment(new Assignment("Final", 87.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(cs);
        
        String transcript = generator.generateTranscript(student);
        
        assertNotNull(transcript);
        assertTrue(transcript.contains("Jane Smith"));
        assertTrue(transcript.contains("Math 101"));
        assertTrue(transcript.contains("CS 101"));
    }
    
    @Test
    @DisplayName("Should show GPA classification in transcript")
    void testTranscriptShowsGPAClassification() {
        Student student = new Student("S-00000003", "Alice Johnson");
        Course course = new Course("Physics 101", 3);
        course.addAssignment(new Assignment("HW", 95.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 92.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 93.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 96.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        String transcript = generator.generateTranscript(student);
        
        assertNotNull(transcript);
        // High GPA should show honors classification
        assertTrue(transcript.contains("Cum Laude") || 
                   transcript.contains("Magna Cum Laude") || 
                   transcript.contains("Summa Cum Laude"));
    }
    
    @Test
    @DisplayName("Should handle student with no courses")
    void testTranscriptNoCourses() {
        Student student = new Student("S-00000004", "Bob Williams");
        
        String transcript = generator.generateTranscript(student);
        
        assertNotNull(transcript);
        assertTrue(transcript.contains("Bob Williams"));
        assertTrue(transcript.contains("S-00000004"));
    }
    
    @Test
    @DisplayName("Should throw exception for null student in generateTranscript")
    void testTranscriptNullStudent() {
        assertThrows(IllegalArgumentException.class, () -> {
            generator.generateTranscript(null);
        });
    }

    // ==================== Grade Report Tests ====================
    
    @Test
    @DisplayName("Should generate grade report with all courses")
    void testGradeReportAllCourses() {
        Student student = new Student("S-00000005", "Charlie Brown");
        
        Course math = new Course("Math 101", 3);
        math.addAssignment(new Assignment("HW", 85.0, 100.0, Category.HOMEWORK));
        math.addAssignment(new Assignment("Quiz", 80.0, 100.0, Category.QUIZZES));
        math.addAssignment(new Assignment("Midterm", 82.0, 100.0, Category.MIDTERM));
        math.addAssignment(new Assignment("Final", 88.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(math);
        
        String report = generator.generateGradeReport(student);
        
        assertNotNull(report);
        assertTrue(report.contains("Charlie Brown"));
        assertTrue(report.contains("Math 101"));
        assertTrue(report.contains("Grade"));
    }
    
    @Test
    @DisplayName("Should show letter grades in grade report")
    void testGradeReportShowsLetterGrades() {
        Student student = new Student("S-00000006", "Diana Prince");
        
        Course course = new Course("History 101", 3);
        course.addAssignment(new Assignment("HW", 92.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 88.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 90.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 91.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        String report = generator.generateGradeReport(student);
        
        assertNotNull(report);
        // Should contain A grade (90+ average)
        assertTrue(report.contains("A") || report.contains("B"));
    }
    
    @Test
    @DisplayName("Should handle student with no courses in grade report")
    void testGradeReportNoCourses() {
        Student student = new Student("S-00000007", "Eve Adams");
        
        String report = generator.generateGradeReport(student);
        
        assertNotNull(report);
        assertTrue(report.contains("Eve Adams"));
    }
    
    @Test
    @DisplayName("Should throw exception for null student in generateGradeReport")
    void testGradeReportNullStudent() {
        assertThrows(IllegalArgumentException.class, () -> {
            generator.generateGradeReport(null);
        });
    }

    // ==================== Course Breakdown Tests ====================
    
    @Test
    @DisplayName("Should generate detailed course breakdown")
    void testCourseBreakdownDetailed() {
        Course course = new Course("Chemistry 101", 4);
        course.addAssignment(new Assignment("HW1", 90.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 85.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz1", 92.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 88.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 91.0, 100.0, Category.FINAL_EXAM));
        
        String breakdown = generator.generateCourseBreakdown(course);
        
        assertNotNull(breakdown);
        assertTrue(breakdown.contains("Chemistry 101"));
        // Should contain at least one category name
        assertTrue(breakdown.contains("HOMEWORK") || 
                   breakdown.contains("QUIZZES") || 
                   breakdown.contains("MIDTERM") || 
                   breakdown.contains("FINAL_EXAM") ||
                   breakdown.contains("Homework") ||
                   breakdown.contains("Final"));
    }
    
    @Test
    @DisplayName("Should show category averages in breakdown")
    void testCourseBreakdownCategoryAverages() {
        Course course = new Course("Biology 101", 3);
        course.addAssignment(new Assignment("HW", 80.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 90.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 88.0, 100.0, Category.FINAL_EXAM));
        
        String breakdown = generator.generateCourseBreakdown(course);
        
        assertNotNull(breakdown);
        // Should contain percentage values
        assertTrue(breakdown.contains("%") || breakdown.contains("Average"));
    }
    
    @Test
    @DisplayName("Should show final grade in breakdown")
    void testCourseBreakdownFinalGrade() {
        Course course = new Course("English 101", 3);
        course.addAssignment(new Assignment("HW", 92.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 88.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 90.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 93.0, 100.0, Category.FINAL_EXAM));
        
        String breakdown = generator.generateCourseBreakdown(course);
        
        assertNotNull(breakdown);
        assertTrue(breakdown.contains("Final") || breakdown.contains("Grade"));
    }
    
    @Test
    @DisplayName("Should handle course with missing categories")
    void testCourseBreakdownMissingCategories() {
        Course course = new Course("Art 101", 2);
        course.addAssignment(new Assignment("HW", 95.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Final", 90.0, 100.0, Category.FINAL_EXAM));
        // Missing QUIZZES and MIDTERM
        
        String breakdown = generator.generateCourseBreakdown(course);
        
        assertNotNull(breakdown);
        assertTrue(breakdown.contains("Art 101"));
    }
    
    @Test
    @DisplayName("Should throw exception for null course")
    void testCourseBreakdownNullCourse() {
        assertThrows(IllegalArgumentException.class, () -> {
            generator.generateCourseBreakdown(null);
        });
    }

    // ==================== Integration Tests ====================
    
    @Test
    @DisplayName("Should generate consistent data across all report types")
    void testConsistencyAcrossReports() {
        Student student = new Student("S-00000008", "Frank Castle");
        Course course = new Course("Law 101", 3);
        course.addAssignment(new Assignment("HW", 87.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 85.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 88.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 90.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        String transcript = generator.generateTranscript(student);
        String gradeReport = generator.generateGradeReport(student);
        String courseBreakdown = generator.generateCourseBreakdown(course);
        
        assertNotNull(transcript);
        assertNotNull(gradeReport);
        assertNotNull(courseBreakdown);
        
        // All should reference the same course
        assertTrue(transcript.contains("Law 101"));
        assertTrue(gradeReport.contains("Law 101"));
        assertTrue(courseBreakdown.contains("Law 101"));
    }
    
    @Test
    @DisplayName("Should format transcript properly with headers and sections")
    void testTranscriptFormatting() {
        Student student = new Student("S-00000009", "Grace Hopper");
        Course course = new Course("CS 201", 4);
        course.addAssignment(new Assignment("HW", 95.0, 100.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz", 92.0, 100.0, Category.QUIZZES));
        course.addAssignment(new Assignment("Midterm", 94.0, 100.0, Category.MIDTERM));
        course.addAssignment(new Assignment("Final", 96.0, 100.0, Category.FINAL_EXAM));
        student.enrollInCourse(course);
        
        String transcript = generator.generateTranscript(student);
        
        assertNotNull(transcript);
        assertFalse(transcript.isEmpty());
        // Should have some structure (multiple lines)
        assertTrue(transcript.split("\n").length > 5);
    }
}
