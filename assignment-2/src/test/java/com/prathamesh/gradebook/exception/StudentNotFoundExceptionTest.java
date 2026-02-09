package com.prathamesh.gradebook.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentNotFoundExceptionTest {
    
    @Test
    void testExceptionWithStudentId() {
        String studentId = "S-12345678";
        StudentNotFoundException exception = new StudentNotFoundException(studentId);
        
        assertEquals(studentId, exception.getStudentId());
        assertTrue(exception.getMessage().contains(studentId));
        assertTrue(exception.getMessage().contains("Student not found"));
    }
    
    @Test
    void testExceptionWithAdditionalMessage() {
        String studentId = "S-87654321";
        String additional = "Please check the student ID format.";
        StudentNotFoundException exception = new StudentNotFoundException(studentId, additional);
        
        assertEquals(studentId, exception.getStudentId());
        assertTrue(exception.getMessage().contains(studentId));
        assertTrue(exception.getMessage().contains(additional));
    }
    
    @Test
    void testInheritsFromGradebookException() {
        StudentNotFoundException exception = new StudentNotFoundException("S-12345678");
        assertTrue(exception instanceof GradebookException);
        assertNotNull(exception.getTimestamp());
    }
    
    @Test
    void testToStringFormat() {
        String studentId = "S-99999999";
        StudentNotFoundException exception = new StudentNotFoundException(studentId);
        String result = exception.toString();
        
        assertTrue(result.contains("StudentNotFoundException"));
        assertTrue(result.contains(studentId));
    }
}
