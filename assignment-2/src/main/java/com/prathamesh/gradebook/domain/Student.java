package com.prathamesh.gradebook.domain;

import com.prathamesh.gradebook.exception.CourseNotFoundException;
import com.prathamesh.gradebook.exception.DuplicateEnrollmentException;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents a student in the gradebook system. This is the Composite pattern root node
 * that contains courses (branch nodes), which in turn contain assignments (leaf nodes).
 * 
 * A student has:
 * - A unique student ID (format: S-XXXXXXXX where X is a digit)
 * - A name
 * - A collection of enrolled courses
 * 
 * Design Patterns:
 * - Composite Pattern: Student is the root node containing Course branch nodes
 * - Identity-based equality: Students are equal if they have the same ID
 * 
 * Immutability Notes:
 * - Student ID and name are final and immutable after construction
 * - Courses map is mutable (can enroll in new courses) but defensively copied on access
 * - Individual Course objects are partially mutable (can add assignments)
 * 
 * The Composite hierarchy:
 * Student (root) → Course (branch) → Assignment (leaf)
 * 
 * @author Prathamesh Kalantri
 */
public class Student {
    
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^S-\\d{8}$");
    
    private final String studentId;
    private final String name;
    private final Map<String, Course> courses;
    
    /**
     * Creates a new student with the specified ID and name.
     * 
     * @param studentId The unique student ID (format: S-XXXXXXXX, where X is a digit)
     * @param name The student's name (cannot be null or blank)
     * @throws IllegalArgumentException if studentId is invalid or name is null/blank
     */
    public Student(String studentId, String name) {
        // Validate student ID
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Student ID cannot be null or blank");
        }
        
        if (!STUDENT_ID_PATTERN.matcher(studentId.trim()).matches()) {
            throw new IllegalArgumentException(
                "Student ID must follow format 'S-XXXXXXXX' where X is a digit. Got: " + studentId);
        }
        
        // Validate name
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Student name cannot be null or blank");
        }
        
        this.studentId = studentId.trim();
        this.name = name.trim();
        this.courses = new HashMap<>();
    }
    
    /**
     * Gets the student's unique ID.
     * 
     * @return The student ID (format: S-XXXXXXXX)
     */
    public String getStudentId() {
        return studentId;
    }
    
    /**
     * Gets the student's name.
     * 
     * @return The student name (never null or blank)
     */
    public String getName() {
        return name;
    }
    
    /**
     * Enrolls the student in a course.
     * 
     * @param course The course to enroll in (cannot be null)
     * @throws IllegalArgumentException if course is null
     * @throws DuplicateEnrollmentException if already enrolled in a course with the same name
     */
    public void enrollInCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        String courseName = course.getCourseName();
        
        if (courses.containsKey(courseName)) {
            throw new DuplicateEnrollmentException(studentId, courseName);
        }
        
        courses.put(courseName, course);
    }
    
    /**
     * Gets a specific course by name.
     * 
     * @param courseName The name of the course to retrieve
     * @return The course object
     * @throws IllegalArgumentException if courseName is null or blank
     * @throws CourseNotFoundException if the student is not enrolled in the course
     */
    public Course getCourse(String courseName) {
        if (courseName == null || courseName.isBlank()) {
            throw new IllegalArgumentException("Course name cannot be null or blank");
        }
        
        Course course = courses.get(courseName.trim());
        
        if (course == null) {
            throw new CourseNotFoundException(studentId, courseName.trim());
        }
        
        return course;
    }
    
    /**
     * Gets all courses the student is enrolled in.
     * 
     * @return Defensive copy of the courses collection (never null, may be empty)
     */
    public Collection<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }
    
    /**
     * Gets all course names the student is enrolled in.
     * 
     * @return Defensive copy of course names set (never null, may be empty)
     */
    public Set<String> getCourseNames() {
        return new HashSet<>(courses.keySet());
    }
    
    /**
     * Checks if the student is enrolled in a specific course.
     * 
     * @param courseName The name of the course to check
     * @return true if enrolled in the course
     * @throws IllegalArgumentException if courseName is null or blank
     */
    public boolean isEnrolledIn(String courseName) {
        if (courseName == null || courseName.isBlank()) {
            throw new IllegalArgumentException("Course name cannot be null or blank");
        }
        
        return courses.containsKey(courseName.trim());
    }
    
    /**
     * Gets the number of courses the student is enrolled in.
     * 
     * @return The number of enrolled courses
     */
    public int getCourseCount() {
        return courses.size();
    }
    
    /**
     * Checks if the student has no course enrollments.
     * 
     * @return true if not enrolled in any courses
     */
    public boolean hasNoCourses() {
        return courses.isEmpty();
    }
    
    /**
     * Gets the total number of credit hours across all enrolled courses.
     * 
     * @return The sum of credit hours from all courses
     */
    public int getTotalCreditHours() {
        return courses.values().stream()
            .mapToInt(Course::getCreditHours)
            .sum();
    }
    
    /**
     * Gets the total number of assignments across all courses.
     * 
     * @return The total count of assignments
     */
    public int getTotalAssignmentCount() {
        return courses.values().stream()
            .mapToInt(Course::getAssignmentCount)
            .sum();
    }
    
    /**
     * Gets all assignments across all courses.
     * 
     * @return List of all assignments (never null, may be empty)
     */
    public List<Assignment> getAllAssignments() {
        List<Assignment> allAssignments = new ArrayList<>();
        
        for (Course course : courses.values()) {
            allAssignments.addAll(course.getAssignments());
        }
        
        return allAssignments;
    }
    
    /**
     * Returns a string representation of the student.
     * 
     * @return String representation including ID, name, and course count
     */
    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s', courses=%d, totalCredits=%d}",
            studentId, name, courses.size(), getTotalCreditHours());
    }
    
    /**
     * Checks if two students are equal based on student ID only.
     * Two students with the same ID are considered the same person,
     * regardless of name or course enrollments.
     * 
     * @param o The object to compare with
     * @return true if students have the same ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }
    
    /**
     * Hash code based on student ID only.
     * 
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
}
