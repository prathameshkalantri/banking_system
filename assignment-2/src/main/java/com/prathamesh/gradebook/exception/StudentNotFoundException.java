package com.prathamesh.gradebook.exception;

/**
 * Exception thrown when a student is not found in the gradebook.
 * 
 * This exception is thrown when attempting to access a student that
 * does not exist in the system. It provides context about which
 * student ID was requested.
 * 
 * Common scenarios:
 * - Trying to enroll a non-existent student in a course
 * - Attempting to add an assignment for a non-existent student
 * - Requesting grades for a student that doesn't exist
 * 
 * @author Prathamesh Kalantri
 */
public class StudentNotFoundException extends GradebookException {
    
    private final String studentId;
    
    /**
     * Constructs a new student not found exception.
     * 
     * @param studentId The ID of the student that was not found
     */
    public StudentNotFoundException(String studentId) {
        super(String.format("Student not found: %s", studentId));
        this.studentId = studentId;
    }
    
    /**
     * Constructs a new student not found exception with additional context.
     * 
     * @param studentId The ID of the student that was not found
     * @param additionalMessage Additional context about the error
     */
    public StudentNotFoundException(String studentId, String additionalMessage) {
        super(String.format("Student not found: %s. %s", studentId, additionalMessage));
        this.studentId = studentId;
    }
    
    /**
     * Gets the student ID that was not found.
     * 
     * @return The student ID
     */
    public String getStudentId() {
        return studentId;
    }
}
