package com.prathamesh.gradebook.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseNotFoundExceptionTest {
    
    @Test
    void testExceptionWithStudentAndCourse() {
        String studentId = "S-12345678";
        String courseName = "Data Structures";
        CourseNotFoundException exception = new CourseNotFoundException(studentId, courseName);
        
        assertEquals(studentId, exception.getStudentId());
        assertEquals(courseName, exception.getCourseName());
        assertTrue(exception.getMessage().contains(studentId));
        assertTrue(exception.getMessage().contains(courseName));
        assertTrue(exception.getMessage().contains("not enrolled"));
    }
    
    @Test
    void testExceptionWithAdditionalMessage() {
        String studentId = "S-87654321";
        String courseName = "Algorithms";
        String additional = "Student must enroll before adding assignments.";
        CourseNotFoundException exception = new CourseNotFoundException(studentId, courseName, additional);
        
        assertEquals(studentId, exception.getStudentId());
        assertEquals(courseName, exception.getCourseName());
        assertTrue(exception.getMessage().contains(studentId));
        assertTrue(exception.getMessage().contains(courseName));
        assertTrue(exception.getMessage().contains(additional));
    }
    
    @Test
    void testInheritsFromGradebookException() {
        CourseNotFoundException exception = new CourseNotFoundException("S-12345678", "CS101");
        assertTrue(exception instanceof GradebookException);
        assertNotNull(exception.getTimestamp());
    }
    
    @Test
    void testMultipleCourseNames() {
        String studentId = "S-11111111";
        String course1 = "Database Systems";
        String course2 = "Operating Systems";
        
        CourseNotFoundException exception1 = new CourseNotFoundException(studentId, course1);
        CourseNotFoundException exception2 = new CourseNotFoundException(studentId, course2);
        
        assertTrue(exception1.getMessage().contains(course1));
        assertFalse(exception1.getMessage().contains(course2));
        assertTrue(exception2.getMessage().contains(course2));
        assertFalse(exception2.getMessage().contains(course1));
    }
}
