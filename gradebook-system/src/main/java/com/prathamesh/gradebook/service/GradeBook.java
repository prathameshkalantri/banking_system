package com.prathamesh.gradebook.service;

import com.prathamesh.gradebook.model.*;

import java.util.*;

/**
 * Central service class for managing student grades and academic records.
 * <p>
 * The GradeBook provides a complete API for managing students, courses, assignments,
 * and grade calculations. It serves as the main entry point for all gradebook operations.
 * </p>
 * 
 * <h3>Core Operations:</h3>
 * <ul>
 *   <li>Student management (add, retrieve)</li>
 *   <li>Course enrollment</li>
 *   <li>Assignment tracking</li>
 *   <li>Grade calculation (category, course, GPA)</li>
 *   <li>Transcript generation</li>
 * </ul>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public class GradeBook {
    
    /** Map of student ID to Student objects */
    private final Map<String, Student> students;
    
    /** Grade calculator for computing averages and final grades */
    private final GradeCalculator gradeCalculator;
    
    /** GPA calculator for computing cumulative GPAs */
    private final GPACalculator gpaCalculator;
    
    /** Transcript generator for creating formatted reports */
    private final TranscriptGenerator transcriptGenerator;
    
    /**
     * Constructs a new GradeBook with empty student records.
     * <p>
     * Initializes all internal services (calculators and generator) and
     * prepares the gradebook for student management operations.
     * </p>
     */
    public GradeBook() {
        this.students = new LinkedHashMap<>(); // Maintain insertion order
        this.gradeCalculator = new GradeCalculator();
        this.gpaCalculator = new GPACalculator();
        this.transcriptGenerator = new TranscriptGenerator();
    }
    
    /**
     * Adds a new student to the gradebook.
     * <p>
     * The student ID must be unique. If a student with the same ID already exists,
     * an exception is thrown.
     * </p>
     * 
     * @param studentId The unique student ID (format: S-XXXXXXXX)
     * @param name The student's full name
     * @return The newly created Student object
     * @throws IllegalArgumentException if student ID already exists or validation fails
     */
    public Student addStudent(String studentId, String name) {
        if (students.containsKey(studentId)) {
            throw new IllegalArgumentException(
                "Student with ID " + studentId + " already exists");
        }
        
        Student student = new Student(studentId, name);
        students.put(studentId, student);
        return student;
    }
    
    /**
     * Retrieves a student by their ID.
     * 
     * @param studentId The student ID to look up
     * @return The Student object, or null if not found
     */
    public Student getStudent(String studentId) {
        return students.get(studentId);
    }
    
    /**
     * Returns an unmodifiable collection of all students in the gradebook.
     * 
     * @return An unmodifiable collection of students
     */
    public Collection<Student> getAllStudents() {
        return Collections.unmodifiableCollection(students.values());
    }
    
    /**
     * Returns the total number of students in the gradebook.
     * 
     * @return The student count
     */
    public int getStudentCount() {
        return students.size();
    }
    
    /**
     * Checks if a student exists in the gradebook.
     * 
     * @param studentId The student ID to check
     * @return true if the student exists, false otherwise
     */
    public boolean hasStudent(String studentId) {
        return students.containsKey(studentId);
    }
    
    /**
     * Enrolls a student in a course.
     * <p>
     * Creates a new course with the specified name and credit hours, then
     * enrolls the student in that course. The student must exist in the gradebook.
     * </p>
     * 
     * @param studentId The student ID
     * @param courseName The name of the course
     * @param creditHours The credit hours for the course
     * @return The newly created Course object
     * @throws IllegalArgumentException if student not found or already enrolled
     */
    public Course enrollInCourse(String studentId, String courseName, int creditHours) {
        Student student = getStudent(studentId);
        
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        
        Course course = new Course(courseName, creditHours);
        student.enrollInCourse(course);
        return course;
    }
    
    /**
     * Adds an assignment to a student's course.
     * <p>
     * The student must be enrolled in the specified course. The assignment
     * is added to the course's assignment list.
     * </p>
     * 
     * @param studentId The student ID
     * @param courseName The course name
     * @param assignment The assignment to add
     * @throws IllegalArgumentException if student or course not found
     */
    public void addAssignment(String studentId, String courseName, Assignment assignment) {
        Student student = getStudent(studentId);
        
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        
        Course course = student.getCourse(courseName);
        
        if (course == null) {
            throw new IllegalArgumentException(
                "Student " + studentId + " is not enrolled in course: " + courseName);
        }
        
        course.addAssignment(assignment);
    }
    
    /**
     * Calculates the category average for a student in a specific course.
     * <p>
     * Returns the average percentage for all assignments in the specified category.
     * If the category has no assignments, returns 0.0.
     * </p>
     * 
     * @param studentId The student ID
     * @param courseName The course name
     * @param category The grading category
     * @return The category average percentage (0.0 to 100.0)
     * @throws IllegalArgumentException if student or course not found
     */
    public double getCategoryAverage(String studentId, String courseName, Category category) {
        Student student = getStudent(studentId);
        
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        
        Course course = student.getCourse(courseName);
        
        if (course == null) {
            throw new IllegalArgumentException(
                "Student " + studentId + " is not enrolled in course: " + courseName);
        }
        
        return gradeCalculator.calculateCategoryAverage(course, category);
    }
    
    /**
     * Calculates the final course grade for a student.
     * <p>
     * Returns a Grade object containing both the percentage and letter grade
     * for the specified course. The grade is calculated using weighted category averages.
     * </p>
     * 
     * @param studentId The student ID
     * @param courseName The course name
     * @return The final course Grade
     * @throws IllegalArgumentException if student or course not found
     */
    public Grade getCourseGrade(String studentId, String courseName) {
        Student student = getStudent(studentId);
        
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        
        Course course = student.getCourse(courseName);
        
        if (course == null) {
            throw new IllegalArgumentException(
                "Student " + studentId + " is not enrolled in course: " + courseName);
        }
        
        return gradeCalculator.calculateFinalGrade(course);
    }
    
    /**
     * Calculates the cumulative GPA for a student.
     * <p>
     * The GPA is weighted by credit hours and includes all courses with assignments.
     * Uses a 4.0 scale (A=4.0, B=3.0, C=2.0, D=1.0, F=0.0).
     * </p>
     * 
     * @param studentId The student ID
     * @return The cumulative GPA (0.0 to 4.0)
     * @throws IllegalArgumentException if student not found
     */
    public double calculateGPA(String studentId) {
        Student student = getStudent(studentId);
        
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        
        return gpaCalculator.calculateGPA(student);
    }
    
    /**
     * Generates a formatted academic transcript for a student.
     * <p>
     * The transcript includes:
     * - Student information
     * - List of all courses with grades
     * - Category breakdowns for each course
     * - Cumulative GPA
     * - Academic standing (Dean's List, Probation, etc.)
     * </p>
     * 
     * @param studentId The student ID
     * @return A formatted transcript as a String
     * @throws IllegalArgumentException if student not found
     */
    public String generateTranscript(String studentId) {
        Student student = getStudent(studentId);
        
        if (student == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
        
        return transcriptGenerator.generateTranscript(student);
    }
    
    /**
     * Returns a summary of all students with their GPAs.
     * <p>
     * This is useful for generating class rankings or overview reports.
     * </p>
     * 
     * @return A map of student ID to GPA
     */
    public Map<String, Double> getGPASummary() {
        Map<String, Double> summary = new LinkedHashMap<>();
        
        for (Student student : students.values()) {
            double gpa = gpaCalculator.calculateGPA(student);
            summary.put(student.getStudentId(), gpa);
        }
        
        return summary;
    }
    
    /**
     * Returns a list of students sorted by GPA in descending order.
     * 
     * @return A list of students ordered by GPA (highest first)
     */
    public List<Student> getStudentsByGPA() {
        List<Student> studentList = new ArrayList<>(students.values());
        
        studentList.sort((s1, s2) -> {
            double gpa1 = gpaCalculator.calculateGPA(s1);
            double gpa2 = gpaCalculator.calculateGPA(s2);
            return Double.compare(gpa2, gpa1); // Descending order
        });
        
        return studentList;
    }
    
    /**
     * Returns students who qualify for the Dean's List.
     * <p>
     * Dean's List qualification: GPA >= 3.5 and at least 12 graded credit hours.
     * </p>
     * 
     * @return A list of students on the Dean's List
     */
    public List<Student> getDeansListStudents() {
        List<Student> deansList = new ArrayList<>();
        
        for (Student student : students.values()) {
            if (student.isDeansListQualified()) {
                deansList.add(student);
            }
        }
        
        return deansList;
    }
    
    /**
     * Returns students on academic probation.
     * <p>
     * Academic probation: GPA < 2.0
     * </p>
     * 
     * @return A list of students on probation
     */
    public List<Student> getProbationStudents() {
        List<Student> probationList = new ArrayList<>();
        
        for (Student student : students.values()) {
            if (student.isOnProbation()) {
                probationList.add(student);
            }
        }
        
        return probationList;
    }
    
    /**
     * Returns statistics about the gradebook.
     * 
     * @return A formatted string with gradebook statistics
     */
    public String getStatistics() {
        int totalStudents = students.size();
        int deansListCount = getDeansListStudents().size();
        int probationCount = getProbationStudents().size();
        
        double avgGPA = students.values().stream()
            .mapToDouble(s -> gpaCalculator.calculateGPA(s))
            .filter(gpa -> gpa > 0) // Exclude students with no grades
            .average()
            .orElse(0.0);
        
        return String.format(
            "GradeBook Statistics:\n" +
            "  Total Students: %d\n" +
            "  Average GPA: %.2f\n" +
            "  Dean's List: %d\n" +
            "  Academic Probation: %d",
            totalStudents, avgGPA, deansListCount, probationCount);
    }
}
