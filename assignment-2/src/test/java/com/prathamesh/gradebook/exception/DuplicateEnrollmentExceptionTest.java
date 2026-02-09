package com.prathamesh.gradebook.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateEnrollmentExceptionTest {
    
    @Test
    void testExceptionWithStudentAndCourse() {
        String studentId = "S-12345678";
        String courseName = "Data Structures";
        DuplicateEnrollmentException exception = new DuplicateEnrollmentException(studentId, courseName);
        
        assertEquals(studentId, exception.getStudentId());
        assertEquals(courseName, exception.getCourseName());
        assertTrue(exception.getMessage().contains(studentId));
        assertTrue(exception.getMessage().contains(courseName));
        assertTrue(exception.getMessage().contains("already enrolled"));
    }
    
    @Test
    void testDifferentStudentsSameCourse() {
        String student1 = "S-11111111";
        String student2 = "S-22222222";
        String courseName = "Algorithms";
        
        DuplicateEnrollmentException exception1 = new DuplicateEnrollmentException(student1, courseName);
        DuplicateEnrollmentException exception2 = new DuplicateEnrollmentException(student2, courseName);
        
        assertEquals(student1, exception1.getStudentId());
        assertEquals(student2, exception2.getStudentId());
        assertEquals(courseName, exception1.getCourseName());
        assertEquals(courseName, exception2.getCourseName());
    }
    
    @Test
    void testSameStudentDifferentCourses() {
        String studentId = "S-33333333";
        String course1 = "Database Systems";
        String course2 = "Operating Systems";
        
        DuplicateEnrollmentException exception1 = new DuplicateEnrollmentException(studentId, course1);
        DuplicateEnrollmentException exception2 = new DuplicateEnrollmentException(studentId, course2);
        
        assertEquals(studentId, exception1.getStudentId());
        assertEquals(studentId, exception2.getStudentId());
        assertEquals(course1, exception1.getCourseName());
        assertEquals(course2, exception2.getCourseName());
    }
    
    @Test
    void testInheritsFromGradebookException() {
        DuplicateEnrollmentException exception = new DuplicateEnrollmentException("S-12345678", "CS101");
        assertTrue(exception instanceof GradebookException);
        assertNotNull(exception.getTimestamp());
    }
    
    @Test
    void testMessageClarity() {
        String studentId = "S-99999999";
        String courseName = "Software Engineering";
        DuplicateEnrollmentException exception = new DuplicateEnrollmentException(studentId, courseName);
        String message = exception.getMessage();
        
        assertTrue(message.contains("already"));
        assertTrue(message.contains("enrolled"));
        assertTrue(message.toLowerCase().contains(studentId.toLowerCase()));
        assertTrue(message.contains(courseName));
    }
}
