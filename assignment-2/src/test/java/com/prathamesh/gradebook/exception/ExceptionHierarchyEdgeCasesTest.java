package com.prathamesh.gradebook.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive edge case tests for the entire exception hierarchy.
 * Tests scenarios that might be missed in individual exception tests.
 */
class ExceptionHierarchyEdgeCasesTest {
    
    @Test
    void testAllExceptionsExtendGradebookException() {
        // Verify inheritance hierarchy is correct
        assertTrue(GradebookException.class.isAssignableFrom(StudentNotFoundException.class));
        assertTrue(GradebookException.class.isAssignableFrom(CourseNotFoundException.class));
        assertTrue(GradebookException.class.isAssignableFrom(InvalidWeightException.class));
        assertTrue(GradebookException.class.isAssignableFrom(InvalidGradeException.class));
        assertTrue(GradebookException.class.isAssignableFrom(InvalidCreditHoursException.class));
        assertTrue(GradebookException.class.isAssignableFrom(DuplicateEnrollmentException.class));
        assertTrue(GradebookException.class.isAssignableFrom(InvalidCategoryException.class));
    }
    
    @Test
    void testAllExceptionsAreUnchecked() {
        // Verify all are RuntimeExceptions (unchecked)
        assertTrue(RuntimeException.class.isAssignableFrom(GradebookException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(StudentNotFoundException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(CourseNotFoundException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(InvalidWeightException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(InvalidGradeException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(InvalidCreditHoursException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(DuplicateEnrollmentException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(InvalidCategoryException.class));
    }
    
    @Test
    void testExceptionWithNullMessage() {
        // GradebookException should handle null message gracefully
        GradebookException exception = new GradebookException(null);
        assertNull(exception.getMessage());
        assertNotNull(exception.getTimestamp());
    }
    
    @Test
    void testExceptionWithEmptyMessage() {
        // GradebookException should handle empty message
        GradebookException exception = new GradebookException("");
        assertEquals("", exception.getMessage());
        assertNotNull(exception.getTimestamp());
    }
    
    @Test
    void testExceptionWithVeryLongMessage() {
        // Test with very long message (1000 characters)
        String longMessage = "a".repeat(1000);
        GradebookException exception = new GradebookException(longMessage);
        assertEquals(1000, exception.getMessage().length());
        assertNotNull(exception.getTimestamp());
    }
    
    @Test
    void testExceptionWithSpecialCharacters() {
        // Test with special characters in message
        String message = "Error: \n\t\"Special\" chars & symbols <>\u00A9\u2122";
        GradebookException exception = new GradebookException(message);
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testExceptionChaining() {
        // Test exception chaining works properly
        IllegalArgumentException rootCause = new IllegalArgumentException("Root cause");
        InvalidGradeException middleException = new InvalidGradeException("Middle exception");
        middleException.initCause(rootCause);
        
        GradebookException topException = new GradebookException("Top exception", middleException);
        
        assertEquals(middleException, topException.getCause());
        assertEquals(rootCause, topException.getCause().getCause());
    }
    
    @Test
    void testStudentNotFoundWithNullId() {
        // Edge case: null student ID
        StudentNotFoundException exception = new StudentNotFoundException(null);
        assertNull(exception.getStudentId());
        assertTrue(exception.getMessage().contains("null"));
    }
    
    @Test
    void testStudentNotFoundWithEmptyId() {
        // Edge case: empty student ID
        StudentNotFoundException exception = new StudentNotFoundException("");
        assertEquals("", exception.getStudentId());
    }
    
    @Test
    void testCourseNotFoundWithNullValues() {
        // Edge case: null student ID and course name
        CourseNotFoundException exception = new CourseNotFoundException(null, null);
        assertNull(exception.getStudentId());
        assertNull(exception.getCourseName());
        assertTrue(exception.getMessage().contains("null"));
    }
    
    @Test
    void testInvalidWeightWithExtremeValues() {
        // Edge case: extreme weight values
        InvalidWeightException exception1 = new InvalidWeightException(0.0);
        assertEquals(0.0, exception1.getActualSum(), 0.001);
        
        InvalidWeightException exception2 = new InvalidWeightException(999999.99);
        assertEquals(999999.99, exception2.getActualSum(), 0.01);
    }
    
    @Test
    void testInvalidWeightWithNegativeSum() {
        // Edge case: negative weight sum
        InvalidWeightException exception = new InvalidWeightException(-50.0);
        assertEquals(-50.0, exception.getActualSum(), 0.001);
        assertTrue(exception.getMessage().contains("-50.00"));
    }
    
    @Test
    void testInvalidGradeWithNullValues() {
        // Edge case: when nullable fields are null
        InvalidGradeException exception = new InvalidGradeException("Test error");
        assertNull(exception.getPointsEarned());
        assertNull(exception.getPointsPossible());
    }
    
    @Test
    void testInvalidGradeWithExtremeValues() {
        // Edge case: very large grade values
        InvalidGradeException exception = new InvalidGradeException(
            999999.99, 1000000.00, "Extreme values");
        assertEquals(999999.99, exception.getPointsEarned(), 0.01);
        assertEquals(1000000.00, exception.getPointsPossible(), 0.01);
    }
    
    @Test
    void testInvalidCreditHoursWithExtremeNegative() {
        // Edge case: very negative credit hours
        InvalidCreditHoursException exception = new InvalidCreditHoursException(-999);
        assertEquals(-999, exception.getCreditHours());
        assertTrue(exception.getMessage().contains("-999"));
    }
    
    @Test
    void testDuplicateEnrollmentWithIdenticalValues() {
        // Edge case: same student and course
        String studentId = "S-12345678";
        String courseName = "Data Structures";
        
        DuplicateEnrollmentException exception = new DuplicateEnrollmentException(studentId, courseName);
        assertEquals(studentId, exception.getStudentId());
        assertEquals(courseName, exception.getCourseName());
    }
    
    @Test
    void testInvalidCategoryWithEmptyStrings() {
        // Edge case: empty strings
        InvalidCategoryException exception = new InvalidCategoryException("", "");
        assertEquals("", exception.getCategoryName());
        assertEquals("", exception.getCourseName());
    }
    
    @Test
    void testTimestampImmutability() {
        // Verify timestamps are captured and don't change
        GradebookException exception = new GradebookException("Test");
        var timestamp1 = exception.getTimestamp();
        
        // Wait a tiny bit
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        
        var timestamp2 = exception.getTimestamp();
        assertEquals(timestamp1, timestamp2); // Should be same object
    }
    
    @Test
    void testToStringDoesNotThrowException() {
        // Verify toString() never throws exception even with null values
        assertDoesNotThrow(() -> new StudentNotFoundException(null).toString());
        assertDoesNotThrow(() -> new CourseNotFoundException(null, null).toString());
        assertDoesNotThrow(() -> new InvalidWeightException(0.0).toString());
        assertDoesNotThrow(() -> new InvalidGradeException(null).toString());
        assertDoesNotThrow(() -> new InvalidCreditHoursException(0).toString());
        assertDoesNotThrow(() -> new DuplicateEnrollmentException(null, null).toString());
        assertDoesNotThrow(() -> new InvalidCategoryException(null, null).toString());
    }
}
