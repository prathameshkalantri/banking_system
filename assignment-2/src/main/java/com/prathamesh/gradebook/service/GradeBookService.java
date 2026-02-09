package com.prathamesh.gradebook.service;

import com.prathamesh.gradebook.calculator.CourseGrade;
import com.prathamesh.gradebook.calculator.GPACalculator;
import com.prathamesh.gradebook.calculator.GradeCalculator;
import com.prathamesh.gradebook.domain.Assignment;
import com.prathamesh.gradebook.domain.Category;
import com.prathamesh.gradebook.domain.Course;
import com.prathamesh.gradebook.domain.Student;
import com.prathamesh.gradebook.exception.CourseNotFoundException;
import com.prathamesh.gradebook.exception.DuplicateEnrollmentException;
import com.prathamesh.gradebook.exception.StudentNotFoundException;
import com.prathamesh.gradebook.util.IdGenerator;

import java.util.*;

/**
 * Service layer for the GradeBook system. Provides high-level operations
 * for managing students, courses, assignments, and grade calculations.
 * 
 * This class orchestrates domain objects (Student, Course, Assignment) and
 * calculation engines (GradeCalculator, GPACalculator) to provide complete
 * gradebook functionality.
 * 
 * Design Patterns:
 * - Facade: Simplifies complex subsystem interactions
 * - Service Layer: Separates business logic from presentation
 * 
 * Responsibilities:
 * - Student management (register, retrieve, list)
 * - Course enrollment
 * - Assignment management
 * - Grade calculations
 * - Academic standing queries
 * 
 * @author Prathamesh Kalantri
 */
public class GradeBookService {
    
    private final Map<String, Student> students;
    private final GradeCalculator gradeCalculator;
    private final GPACalculator gpaCalculator;
    
    /**
     * Creates a new GradeBook service with empty student registry.
     */
    public GradeBookService() {
        this.students = new HashMap<>();
        this.gradeCalculator = new GradeCalculator();
        this.gpaCalculator = new GPACalculator(gradeCalculator);
    }
    
    /**
     * Creates a GradeBook service with custom calculators (for testing).
     * 
     * @param gradeCalculator The grade calculator to use
     * @param gpaCalculator The GPA calculator to use
     */
    public GradeBookService(GradeCalculator gradeCalculator, GPACalculator gpaCalculator) {
        if (gradeCalculator == null) {
            throw new IllegalArgumentException("Grade calculator cannot be null");
        }
        if (gpaCalculator == null) {
            throw new IllegalArgumentException("GPA calculator cannot be null");
        }
        
        this.students = new HashMap<>();
        this.gradeCalculator = gradeCalculator;
        this.gpaCalculator = gpaCalculator;
    }
    
    // ========== Student Management ==========
    
    /**
     * Registers a new student with auto-generated unique ID.
     * 
     * This is the RECOMMENDED method for adding students as it:
     * - Automatically generates unique student IDs (thread-safe)
     * - Prevents duplicate ID errors
     * - Enforces consistent ID format (S-XXXXXXXX)
     * - Provides better user experience (no manual ID management)
     * 
     * Example:
     * <pre>
     * String aliceId = gradeBook.registerStudent("Alice Johnson");
     * // Returns: "S-00000001" (auto-generated, guaranteed unique)
     * </pre>
     * 
     * @param name The student's name (cannot be null or blank)
     * @return The auto-generated student ID
     * @throws IllegalArgumentException if name is null or blank
     */
    public String registerStudent(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Student name cannot be null or blank");
        }
        
        String studentId = IdGenerator.generateStudentId();
        Student student = new Student(studentId, name);
        students.put(studentId, student);
        
        return studentId;
    }
    
    /**
     * Registers a student with a manually specified ID.
     * 
     * This method is provided for:
     * - Testing with specific IDs
     * - Data migration from external systems
     * - Advanced use cases requiring custom IDs
     * 
     * CAUTION: Manual IDs are error-prone. Prefer registerStudent(name) for normal use.
     * 
     * @param student The student to register
     * @throws IllegalArgumentException if student is null or ID already exists
     */
    public void registerStudentWithId(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        String studentId = student.getStudentId();
        if (students.containsKey(studentId)) {
            throw new IllegalArgumentException(
                "Student with ID " + studentId + " is already registered"
            );
        }
        
        students.put(studentId, student);
    }
    
    /**
     * Retrieves a student by ID.
     * 
     * @param studentId The student ID
     * @return The student
     * @throws IllegalArgumentException if studentId is null or blank
     * @throws StudentNotFoundException if student doesn't exist
     */
    public Student getStudent(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Student ID cannot be null or blank");
        }
        
        Student student = students.get(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        
        return student;
    }
    
    /**
     * Gets all registered students.
     * 
     * @return Unmodifiable list of all students
     */
    public List<Student> getAllStudents() {
        return List.copyOf(students.values());
    }
    
    /**
     * Gets the total number of registered students.
     * 
     * @return The student count
     */
    public int getStudentCount() {
        return students.size();
    }
    
    /**
     * Checks if a student is registered.
     * 
     * @param studentId The student ID
     * @return true if student exists
     */
    public boolean isStudentRegistered(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            return false;
        }
        return students.containsKey(studentId);
    }
    
    // ========== Course Enrollment ==========
    
    /**
     * Enrolls a student in a course.
     * 
     * @param studentId The student ID
     * @param course The course to enroll in
     * @throws StudentNotFoundException if student doesn't exist
     * @throws DuplicateEnrollmentException if already enrolled in course
     * @throws IllegalArgumentException if course is null
     */
    public void enrollStudentInCourse(String studentId, Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        Student student = getStudent(studentId);
        student.enrollInCourse(course);
    }
    
    /**
     * Gets all courses for a student.
     * 
     * @param studentId The student ID
     * @return List of courses (defensive copy)
     * @throws StudentNotFoundException if student doesn't exist
     */
    public List<Course> getStudentCourses(String studentId) {
        Student student = getStudent(studentId);
        return new ArrayList<>(student.getAllCourses());
    }
    
    /**
     * Gets a specific course for a student.
     * 
     * @param studentId The student ID
     * @param courseName The course name
     * @return The course
     * @throws StudentNotFoundException if student doesn't exist
     * @throws CourseNotFoundException if course not found for student
     */
    public Course getStudentCourse(String studentId, String courseName) {
        Student student = getStudent(studentId);
        return student.getCourse(courseName);
    }
    
    /**
     * Gets the total credit hours for a student.
     * 
     * @param studentId The student ID
     * @return Total credit hours
     * @throws StudentNotFoundException if student doesn't exist
     */
    public int getStudentCreditHours(String studentId) {
        Student student = getStudent(studentId);
        return student.getTotalCreditHours();
    }
    
    // ========== Assignment Management ==========
    
    /**
     * Adds an assignment to a student's course.
     * 
     * @param studentId The student ID
     * @param courseName The course name
     * @param assignment The assignment to add
     * @throws StudentNotFoundException if student doesn't exist
     * @throws CourseNotFoundException if course not found
     * @throws IllegalArgumentException if assignment is null
     */
    public void addAssignment(String studentId, String courseName, Assignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment cannot be null");
        }
        
        Course course = getStudentCourse(studentId, courseName);
        course.addAssignment(assignment);
    }
    
    /**
     * Gets all assignments for a student's course.
     * 
     * @param studentId The student ID
     * @param courseName The course name
     * @return List of assignments (defensive copy)
     * @throws StudentNotFoundException if student doesn't exist
     * @throws CourseNotFoundException if course not found
     */
    public List<Assignment> getCourseAssignments(String studentId, String courseName) {
        Course course = getStudentCourse(studentId, courseName);
        return course.getAssignments();
    }
    
    /**
     * Gets assignments for a specific category.
     * 
     * @param studentId The student ID
     * @param courseName The course name
     * @param category The category
     * @return List of assignments in category
     * @throws StudentNotFoundException if student doesn't exist
     * @throws CourseNotFoundException if course not found
     * @throws IllegalArgumentException if category is null
     */
    public List<Assignment> getCategoryAssignments(String studentId, String courseName, Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        Course course = getStudentCourse(studentId, courseName);
        return course.getAssignmentsByCategory(category);
    }
    
    // ========== Grade Calculations ==========
    
    /**
     * Calculates the grade for a student's course.
     * 
     * @param studentId The student ID
     * @param courseName The course name
     * @return The course grade
     * @throws StudentNotFoundException if student doesn't exist
     * @throws CourseNotFoundException if course not found
     */
    public CourseGrade calculateCourseGrade(String studentId, String courseName) {
        Course course = getStudentCourse(studentId, courseName);
        return gradeCalculator.calculateCourseGrade(course);
    }
    
    /**
     * Calculates grades for all of a student's courses.
     * 
     * @param studentId The student ID
     * @return List of course grades
     * @throws StudentNotFoundException if student doesn't exist
     */
    public List<CourseGrade> calculateAllCourseGrades(String studentId) {
        Student student = getStudent(studentId);
        return gpaCalculator.calculateAllCourseGrades(student);
    }
    
    /**
     * Calculates a student's GPA.
     * 
     * @param studentId The student ID
     * @return The GPA (0.0-4.0)
     * @throws StudentNotFoundException if student doesn't exist
     */
    public double calculateGPA(String studentId) {
        Student student = getStudent(studentId);
        return gpaCalculator.calculateGPA(student);
    }
    
    /**
     * Calculates the average for a specific category in a course.
     * 
     * @param studentId The student ID
     * @param courseName The course name
     * @param category The category
     * @return The category average (0-100)
     * @throws StudentNotFoundException if student doesn't exist
     * @throws CourseNotFoundException if course not found
     * @throws IllegalArgumentException if category is null
     */
    public double calculateCategoryAverage(String studentId, String courseName, Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        Course course = getStudentCourse(studentId, courseName);
        return gradeCalculator.calculateCategoryAverage(course, category);
    }
    
    // ========== Academic Standing ==========
    
    /**
     * Checks if a student qualifies for Honor Roll (GPA >= 3.5).
     * 
     * @param studentId The student ID
     * @return true if student is on Honor Roll
     * @throws StudentNotFoundException if student doesn't exist
     */
    public boolean isHonorRoll(String studentId) {
        Student student = getStudent(studentId);
        return gpaCalculator.isHonorRoll(student);
    }
    
    /**
     * Gets all students on Honor Roll.
     * 
     * @return List of Honor Roll students
     */
    public List<Student> getHonorRollStudents() {
        List<Student> honorRoll = new ArrayList<>();
        
        for (Student student : students.values()) {
            if (gpaCalculator.isHonorRoll(student)) {
                honorRoll.add(student);
            }
        }
        
        return honorRoll;
    }
    
    /**
     * Checks if a student is passing all courses (D or better).
     * 
     * @param studentId The student ID
     * @return true if all courses have passing grades, false if no courses or any failing grades
     * @throws StudentNotFoundException if student doesn't exist
     */
    public boolean isPassingAllCourses(String studentId) {
        List<CourseGrade> grades = calculateAllCourseGrades(studentId);
        
        // Student with no courses is not "passing all courses"
        if (grades.isEmpty()) {
            return false;
        }
        
        for (CourseGrade grade : grades) {
            if (!grade.isPassing()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets students who are failing at least one course.
     * 
     * @return List of students with failing grades
     */
    public List<Student> getStudentsWithFailingGrades() {
        List<Student> failing = new ArrayList<>();
        
        for (Student student : students.values()) {
            if (!isPassingAllCourses(student.getStudentId())) {
                failing.add(student);
            }
        }
        
        return failing;
    }
}
