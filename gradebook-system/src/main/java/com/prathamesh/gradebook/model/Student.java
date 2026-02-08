package com.prathamesh.gradebook.model;

import java.util.*;

/**
 * Represents a student enrolled in multiple courses.
 * <p>
 * A Student manages enrollment in courses and provides access to
 * course information and grades. Each student has a unique ID and name,
 * and can be enrolled in multiple courses simultaneously.
 * </p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Unique student identification</li>
 *   <li>Multiple course enrollment</li>
 *   <li>GPA calculation across all enrolled courses</li>
 *   <li>Course grade tracking</li>
 * </ul>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public class Student {
    
    /** The unique identifier for this student */
    private final String studentId;
    
    /** The student's full name */
    private final String name;
    
    /** Map of course name to Course objects */
    private final Map<String, Course> courses;
    
    /**
     * Constructs a new Student with the specified ID and name.
     * 
     * @param studentId The unique student ID (must not be null or empty)
     * @param name The student's name (must not be null or empty)
     * @throws IllegalArgumentException if validation fails
     */
    public Student(String studentId, String name) {
        // Validate student ID
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }
        
        // Validate student ID format (optional: S-XXXXXXXX pattern)
        if (!studentId.matches("S-\\d{8}")) {
            throw new IllegalArgumentException(
                "Student ID must follow format S-XXXXXXXX, got: " + studentId);
        }
        
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be null or empty");
        }
        
        this.studentId = studentId.trim();
        this.name = name.trim();
        this.courses = new LinkedHashMap<>(); // Maintain insertion order
    }
    
    /**
     * Returns the student's unique ID.
     * 
     * @return The student ID (format: S-XXXXXXXX)
     */
    public String getStudentId() {
        return studentId;
    }
    
    /**
     * Returns the student's name.
     * 
     * @return The student name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Enrolls the student in a new course.
     * <p>
     * If the student is already enrolled in a course with the same name,
     * this method throws an exception. Use {@link #isEnrolledIn(String)}
     * to check enrollment status before calling this method.
     * </p>
     * 
     * @param course The course to enroll in (must not be null)
     * @throws IllegalArgumentException if course is null or student already enrolled
     */
    public void enrollInCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        String courseName = course.getCourseName();
        
        if (courses.containsKey(courseName)) {
            throw new IllegalArgumentException(
                "Student is already enrolled in course: " + courseName);
        }
        
        courses.put(courseName, course);
    }
    
    /**
     * Returns an unmodifiable collection of all courses this student is enrolled in.
     * 
     * @return An unmodifiable collection of courses
     */
    public Collection<Course> getCourses() {
        return Collections.unmodifiableCollection(courses.values());
    }
    
    /**
     * Returns a specific course by name.
     * 
     * @param courseName The name of the course to retrieve
     * @return The course, or null if not found
     */
    public Course getCourse(String courseName) {
        return courses.get(courseName);
    }
    
    /**
     * Checks if the student is enrolled in a specific course.
     * 
     * @param courseName The name of the course to check
     * @return true if enrolled, false otherwise
     */
    public boolean isEnrolledIn(String courseName) {
        return courses.containsKey(courseName);
    }
    
    /**
     * Returns the number of courses this student is enrolled in.
     * 
     * @return The course count
     */
    public int getCourseCount() {
        return courses.size();
    }
    
    /**
     * Calculates the cumulative GPA across all courses.
     * <p>
     * The GPA is calculated as a weighted average based on credit hours:
     * GPA = Σ(Grade Points × Credit Hours) / Σ(Credit Hours)
     * </p>
     * 
     * <p>
     * Only courses with assignments are included in the GPA calculation.
     * If no courses have assignments, returns 0.0.
     * </p>
     * 
     * @return The cumulative GPA on a 4.0 scale
     */
    public double calculateGPA() {
        double totalGradePoints = 0.0;
        int totalCreditHours = 0;
        
        for (Course course : courses.values()) {
            // Only include courses with assignments
            if (course.hasAssignments()) {
                Grade grade = course.getFinalGrade();
                double gpaPoints = grade.getGpaPoints();
                int creditHours = course.getCreditHours();
                
                totalGradePoints += gpaPoints * creditHours;
                totalCreditHours += creditHours;
            }
        }
        
        // Avoid division by zero
        if (totalCreditHours == 0) {
            return 0.0;
        }
        
        return totalGradePoints / totalCreditHours;
    }
    
    /**
     * Returns the total number of credit hours for all enrolled courses.
     * 
     * @return The total credit hours
     */
    public int getTotalCreditHours() {
        return courses.values().stream()
            .mapToInt(Course::getCreditHours)
            .sum();
    }
    
    /**
     * Returns the total number of credit hours for courses with assignments.
     * 
     * @return The credit hours for graded courses
     */
    public int getGradedCreditHours() {
        return courses.values().stream()
            .filter(Course::hasAssignments)
            .mapToInt(Course::getCreditHours)
            .sum();
    }
    
    /**
     * Checks if the student qualifies for the Dean's List.
     * <p>
     * Dean's List qualification typically requires:
     * - GPA >= 3.5
     * - At least 12 credit hours with grades
     * </p>
     * 
     * @return true if qualified for Dean's List, false otherwise
     */
    public boolean isDeansListQualified() {
        return calculateGPA() >= 3.5 && getGradedCreditHours() >= 12;
    }
    
    /**
     * Checks if the student is on academic probation.
     * <p>
     * Academic probation typically occurs when GPA falls below 2.0.
     * </p>
     * 
     * @return true if GPA < 2.0, false otherwise
     */
    public boolean isOnProbation() {
        double gpa = calculateGPA();
        return gpa > 0 && gpa < 2.0;
    }
    
    /**
     * Returns a map of course names to grades.
     * 
     * @return A map of course name to Grade for all courses with assignments
     */
    public Map<String, Grade> getCourseGrades() {
        Map<String, Grade> grades = new LinkedHashMap<>();
        
        for (Course course : courses.values()) {
            if (course.hasAssignments()) {
                grades.put(course.getCourseName(), course.getFinalGrade());
            }
        }
        
        return grades;
    }
    
    /**
     * Compares this student to another object for equality.
     * <p>
     * Two students are equal if they have the same student ID.
     * </p>
     * 
     * @param obj The object to compare with
     * @return true if the students are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Student student = (Student) obj;
        return studentId.equals(student.studentId);
    }
    
    /**
     * Returns a hash code value for this student.
     * 
     * @return A hash code based on the student ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
    
    /**
     * Returns a string representation of this student.
     * 
     * @return A formatted string with student ID, name, and course count
     */
    @Override
    public String toString() {
        return String.format("%s - %s (%d courses, GPA: %.2f)",
            studentId, name, getCourseCount(), calculateGPA());
    }
}
