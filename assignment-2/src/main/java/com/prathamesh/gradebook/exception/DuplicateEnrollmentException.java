package com.prathamesh.gradebook.exception;

/**
 * Exception thrown when attempting to enroll a student in a course
 * they are already enrolled in.
 * 
 * This exception prevents duplicate enrollments and maintains data
 * integrity in the gradebook system.
 * 
 * Business rule: A student can only be enrolled in a course once
 * 
 * Common scenarios:
 * - Attempting to enroll a student twice in the same course
 * - Re-enrolling a student without first unenrolling them
 * 
 * @author Prathamesh Kalantri
 */
public class DuplicateEnrollmentException extends GradebookException {
    
    private final String studentId;
    private final String courseName;
    
    /**
     * Constructs a new duplicate enrollment exception.
     * 
     * @param studentId The ID of the student
     * @param courseName The name of the course
     */
    public DuplicateEnrollmentException(String studentId, String courseName) {
        super(String.format("Student %s is already enrolled in course: %s", 
            studentId, courseName));
        this.studentId = studentId;
        this.courseName = courseName;
    }
    
    /**
     * Gets the student ID.
     * 
     * @return The student ID
     */
    public String getStudentId() {
        return studentId;
    }
    
    /**
     * Gets the course name.
     * 
     * @return The course name
     */
    public String getCourseName() {
        return courseName;
    }
}
