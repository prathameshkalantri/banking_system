package com.prathamesh.gradebook.exception;

/**
 * Exception thrown when a course is not found for a student.
 * 
 * This exception is thrown when attempting to access a course that
 * a student is not enrolled in. It provides context about both the
 * student and the course.
 * 
 * Common scenarios:
 * - Adding an assignment to a course the student isn't enrolled in
 * - Getting grades for a course the student isn't taking
 * - Requesting category averages for a non-existent course
 * 
 * @author Prathamesh Kalantri
 */
public class CourseNotFoundException extends GradebookException {
    
    private final String studentId;
    private final String courseName;
    
    /**
     * Constructs a new course not found exception.
     * 
     * @param studentId The ID of the student
     * @param courseName The name of the course that was not found
     */
    public CourseNotFoundException(String studentId, String courseName) {
        super(String.format("Student %s is not enrolled in course: %s", 
            studentId, courseName));
        this.studentId = studentId;
        this.courseName = courseName;
    }
    
    /**
     * Constructs a new course not found exception with additional context.
     * 
     * @param studentId The ID of the student
     * @param courseName The name of the course that was not found
     * @param additionalMessage Additional context about the error
     */
    public CourseNotFoundException(String studentId, String courseName, String additionalMessage) {
        super(String.format("Student %s is not enrolled in course: %s. %s", 
            studentId, courseName, additionalMessage));
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
     * Gets the course name that was not found.
     * 
     * @return The course name
     */
    public String getCourseName() {
        return courseName;
    }
}
