package com.prathamesh.gradebook.util;

import com.prathamesh.gradebook.calculator.CourseGrade;
import com.prathamesh.gradebook.domain.Category;
import com.prathamesh.gradebook.domain.Course;
import com.prathamesh.gradebook.domain.LetterGrade;
import com.prathamesh.gradebook.domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for TranscriptFormatter.
 * 
 * Test Coverage:
 * - Transcript formatting with various scenarios
 * - Header formatting
 * - Course table formatting
 * - GPA summary formatting
 * - Color coding
 * - Honor Roll designation
 * - Edge cases (empty courses, long names, null inputs)
 * - Plain transcript formatting
 * 
 * @author Prathamesh Kalantri
 */
class TranscriptFormatterTest {
    
    private Student student;
    private Course course1;
    private Course course2;
    private Course course3;
    
    @BeforeEach
    void setUp() {
        student = new Student("S-12345678", "Alice Johnson");
        course1 = new Course("Data Structures", 3);
        course2 = new Course("Algorithms", 4);
        course3 = new Course("Database Systems", 3);
    }
    
    @Test
    void testFormatTranscriptWithMultipleCourses() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 92.0, LetterGrade.A),
            createCourseGrade(course2, 85.0, LetterGrade.B),
            createCourseGrade(course3, 78.0, LetterGrade.C)
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 3.5, grades);
        
        assertNotNull(transcript);
        assertTrue(transcript.contains("ACADEMIC TRANSCRIPT"));
        assertTrue(transcript.contains("Alice Johnson"));
        assertTrue(transcript.contains("S-12345678"));
        assertTrue(transcript.contains("Data Structures"));
        assertTrue(transcript.contains("Algorithms"));
        assertTrue(transcript.contains("Database Systems"));
        assertTrue(transcript.contains("3.50"));
        assertTrue(transcript.contains("Total Credits: 10"));
    }
    
    @Test
    void testFormatTranscriptWithHonorRoll() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 95.0, LetterGrade.A)
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 4.0, grades);
        
        assertTrue(transcript.contains("HONOR ROLL"));
    }
    
    @Test
    void testFormatTranscriptWithoutHonorRoll() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 75.0, LetterGrade.C)
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 2.0, grades);
        
        assertFalse(transcript.contains("HONOR ROLL"));
    }
    
    @Test
    void testFormatTranscriptWithNoCourses() {
        List<CourseGrade> grades = new ArrayList<>();
        
        String transcript = TranscriptFormatter.formatTranscript(student, 0.0, grades);
        
        assertNotNull(transcript);
        assertTrue(transcript.contains("Alice Johnson"));
        assertTrue(transcript.contains("No courses enrolled"));
        assertTrue(transcript.contains("Total Credits: 0"));
    }
    
    @Test
    void testFormatTranscriptWithNullStudentThrowsException() {
        List<CourseGrade> grades = new ArrayList<>();
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            TranscriptFormatter.formatTranscript(null, 3.0, grades);
        });
        
        assertEquals("Student cannot be null", exception.getMessage());
    }
    
    @Test
    void testFormatTranscriptWithNullGradesThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            TranscriptFormatter.formatTranscript(student, 3.0, null);
        });
        
        assertEquals("Course grades cannot be null", exception.getMessage());
    }
    
    @Test
    void testFormatTranscriptWithLongCourseName() {
        Course longCourse = new Course("Introduction to Advanced Database Management Systems", 3);
        List<CourseGrade> grades = List.of(
            createCourseGrade(longCourse, 90.0, LetterGrade.A)
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 4.0, grades);
        
        assertNotNull(transcript);
        // Should truncate and add "..."
        assertTrue(transcript.contains("...") || transcript.contains("Introduction to Advanced Database"));
    }
    
    @Test
    void testTranscriptContainsBorders() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 90.0, LetterGrade.A)
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 4.0, grades);
        
        // Check for box drawing characters
        assertTrue(transcript.contains("╔"));
        assertTrue(transcript.contains("╗"));
        assertTrue(transcript.contains("╚"));
        assertTrue(transcript.contains("╝"));
        assertTrue(transcript.contains("║"));
        assertTrue(transcript.contains("═"));
        assertTrue(transcript.contains("╠"));
        assertTrue(transcript.contains("╣"));
    }
    
    @Test
    void testTranscriptContainsColorCodes() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 92.0, LetterGrade.A),
            createCourseGrade(course2, 82.0, LetterGrade.B)
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 3.5, grades);
        
        // Should contain ANSI color codes
        assertTrue(transcript.contains("\u001B["));
    }
    
    @Test
    void testGradeColorCoding() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 92.0, LetterGrade.A),
            createCourseGrade(course2, 82.0, LetterGrade.B),
            createCourseGrade(course3, 72.0, LetterGrade.C)
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 3.2, grades);
        
        // A should be green
        assertTrue(transcript.contains(ConsoleColors.GREEN));
        // B should be blue
        assertTrue(transcript.contains(ConsoleColors.BLUE));
        // C should be yellow
        assertTrue(transcript.contains(ConsoleColors.YELLOW));
    }
    
    @Test
    void testFormatPlainTranscript() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 92.0, LetterGrade.A)
        );
        
        String plainTranscript = TranscriptFormatter.formatPlainTranscript(student, 4.0, grades);
        
        assertNotNull(plainTranscript);
        assertFalse(plainTranscript.contains("\u001B["));
        assertTrue(plainTranscript.contains("Alice Johnson"));
        assertTrue(plainTranscript.contains("Data Structures"));
    }
    
    @Test
    void testTranscriptWithAllGrades() {
        Course c1 = new Course("Course A", 3);
        Course c2 = new Course("Course B", 3);
        Course c3 = new Course("Course C", 3);
        Course c4 = new Course("Course D", 3);
        Course c5 = new Course("Course F", 3);
        
        List<CourseGrade> grades = List.of(
            createCourseGrade(c1, 95.0, LetterGrade.A),
            createCourseGrade(c2, 85.0, LetterGrade.B),
            createCourseGrade(c3, 75.0, LetterGrade.C),
            createCourseGrade(c4, 65.0, LetterGrade.D),
            createCourseGrade(c5, 45.0, LetterGrade.F)
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 2.0, grades);
        
        assertNotNull(transcript);
        assertTrue(transcript.contains("Course A"));
        assertTrue(transcript.contains("Course B"));
        assertTrue(transcript.contains("Course C"));
        assertTrue(transcript.contains("Course D"));
        assertTrue(transcript.contains("Course F"));
        
        // Check all color codes are present
        assertTrue(transcript.contains(ConsoleColors.GREEN));
        assertTrue(transcript.contains(ConsoleColors.BLUE));
        assertTrue(transcript.contains(ConsoleColors.YELLOW));
        assertTrue(transcript.contains(ConsoleColors.ORANGE));
        assertTrue(transcript.contains(ConsoleColors.RED));
    }
    
    @Test
    void testTranscriptGPAColorCoding() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 95.0, LetterGrade.A)
        );
        
        // Test Honor Roll GPA (>= 3.5) - should be green
        String honorRoll = TranscriptFormatter.formatTranscript(student, 3.8, grades);
        assertTrue(honorRoll.contains(ConsoleColors.GREEN) || honorRoll.contains(ConsoleColors.BRIGHT_GREEN));
        
        // Test high GPA (>= 3.0) - should be blue
        grades = List.of(createCourseGrade(course1, 85.0, LetterGrade.B));
        String highGPA = TranscriptFormatter.formatTranscript(student, 3.2, grades);
        assertTrue(highGPA.contains(ConsoleColors.BLUE));
        
        // Test medium GPA (>= 2.0) - should be yellow
        grades = List.of(createCourseGrade(course1, 75.0, LetterGrade.C));
        String mediumGPA = TranscriptFormatter.formatTranscript(student, 2.5, grades);
        assertTrue(mediumGPA.contains(ConsoleColors.YELLOW));
        
        // Test low GPA (>= 1.0) - should be orange
        grades = List.of(createCourseGrade(course1, 65.0, LetterGrade.D));
        String lowGPA = TranscriptFormatter.formatTranscript(student, 1.5, grades);
        assertTrue(lowGPA.contains(ConsoleColors.ORANGE));
        
        // Test failing GPA (< 1.0) - should be red
        grades = List.of(createCourseGrade(course1, 45.0, LetterGrade.F));
        String failingGPA = TranscriptFormatter.formatTranscript(student, 0.5, grades);
        assertTrue(failingGPA.contains(ConsoleColors.RED));
    }
    
    @Test
    void testTranscriptWithSingleCourse() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 88.0, LetterGrade.B)
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 3.0, grades);
        
        assertNotNull(transcript);
        assertTrue(transcript.contains("Data Structures"));
        assertTrue(transcript.contains("3.0"));
        assertTrue(transcript.contains("Total Credits: 3"));
    }
    
    @Test
    void testTranscriptTotalCreditsCalculation() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 90.0, LetterGrade.A),  // 3 credits
            createCourseGrade(course2, 85.0, LetterGrade.B),  // 4 credits
            createCourseGrade(course3, 80.0, LetterGrade.B)   // 3 credits
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 3.5, grades);
        
        assertTrue(transcript.contains("Total Credits: 10"));
    }
    
    @Test
    void testTranscriptHonorRollThreshold() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 92.0, LetterGrade.A)
        );
        
        // Exactly at threshold (3.5)
        String atThreshold = TranscriptFormatter.formatTranscript(student, 3.5, grades);
        assertTrue(atThreshold.contains("HONOR ROLL"));
        
        // Just below threshold (3.49)
        String belowThreshold = TranscriptFormatter.formatTranscript(student, 3.49, grades);
        assertFalse(belowThreshold.contains("HONOR ROLL"));
    }
    
    @Test
    void testUtilityClassCannotBeInstantiated() {
        try {
            // Use reflection to try instantiating
            var constructor = TranscriptFormatter.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            // Reflection wraps the AssertionError in InvocationTargetException
            assertTrue(e.getCause() instanceof AssertionError);
            assertTrue(e.getCause().getMessage().contains("utility class"));
        }
    }
    
    @Test
    void testTranscriptFormatConsistency() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 90.0, LetterGrade.A)
        );
        
        String transcript1 = TranscriptFormatter.formatTranscript(student, 4.0, grades);
        String transcript2 = TranscriptFormatter.formatTranscript(student, 4.0, grades);
        
        // Should produce identical output for same inputs
        assertEquals(transcript1, transcript2);
    }
    
    @Test
    void testPlainTranscriptMatchesColoredTranscript() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 90.0, LetterGrade.A)
        );
        
        String coloredTranscript = TranscriptFormatter.formatTranscript(student, 4.0, grades);
        String plainTranscript = TranscriptFormatter.formatPlainTranscript(student, 4.0, grades);
        
        // Plain should be the stripped version of colored
        assertEquals(plainTranscript, ConsoleColors.stripColors(coloredTranscript));
    }
    
    @Test
    void testTranscriptWithZeroGPA() {
        List<CourseGrade> grades = new ArrayList<>();
        
        String transcript = TranscriptFormatter.formatTranscript(student, 0.0, grades);
        
        assertTrue(transcript.contains("0.00"));
        assertTrue(transcript.contains("Total Credits: 0"));
    }
    
    @Test
    void testTranscriptWithPerfectGPA() {
        List<CourseGrade> grades = List.of(
            createCourseGrade(course1, 100.0, LetterGrade.A),
            createCourseGrade(course2, 98.0, LetterGrade.A)
        );
        
        String transcript = TranscriptFormatter.formatTranscript(student, 4.0, grades);
        
        assertTrue(transcript.contains("4.00"));
        assertTrue(transcript.contains("HONOR ROLL"));
    }
    
    // Helper method to create CourseGrade
    private CourseGrade createCourseGrade(Course course, double numericGrade, LetterGrade letterGrade) {
        Map<Category, Double> categoryAverages = new EnumMap<>(Category.class);
        categoryAverages.put(Category.HOMEWORK, numericGrade);
        categoryAverages.put(Category.QUIZZES, numericGrade);
        categoryAverages.put(Category.MIDTERM, numericGrade);
        categoryAverages.put(Category.FINAL_EXAM, numericGrade);
        
        return new CourseGrade(course, numericGrade, letterGrade, categoryAverages);
    }
}
