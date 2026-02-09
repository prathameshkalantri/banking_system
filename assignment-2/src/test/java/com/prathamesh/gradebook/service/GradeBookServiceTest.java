package com.prathamesh.gradebook.service;

import com.prathamesh.gradebook.calculator.CourseGrade;
import com.prathamesh.gradebook.calculator.GPACalculator;
import com.prathamesh.gradebook.calculator.GradeCalculator;
import com.prathamesh.gradebook.domain.Assignment;
import com.prathamesh.gradebook.domain.Category;
import com.prathamesh.gradebook.domain.Course;
import com.prathamesh.gradebook.domain.LetterGrade;
import com.prathamesh.gradebook.domain.Student;
import com.prathamesh.gradebook.exception.CourseNotFoundException;
import com.prathamesh.gradebook.exception.DuplicateEnrollmentException;
import com.prathamesh.gradebook.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for GradeBookService.
 * 
 * Test Coverage:
 * - Student management (register, retrieve, list, count)
 * - Course enrollment operations
 * - Assignment management
 * - Grade calculations
 * - Academic standing queries (Honor Roll, failing grades)
 * - Edge cases and error handling
 * 
 * @author Prathamesh Kalantri
 */
class GradeBookServiceTest {
    
    private GradeBookService service;
    private Student student1;
    private Student student2;
    private Course course1;
    private Course course2;
    
    @BeforeEach
    void setUp() {
        service = new GradeBookService();
        
        student1 = new Student("S-12345678", "Alice Johnson");
        student2 = new Student("S-87654321", "Bob Smith");
        
        course1 = new Course("Data Structures", 3);
        course2 = new Course("Algorithms", 4);
    }
    
    // ========== Constructor Tests ==========
    
    @Test
    void testCreateServiceWithDefaultConstructor() {
        GradeBookService newService = new GradeBookService();
        
        assertNotNull(newService);
        assertEquals(0, newService.getStudentCount());
    }
    
    @Test
    void testCreateServiceWithCustomCalculators() {
        GradeCalculator gradeCalc = new GradeCalculator();
        GPACalculator gpaCalc = new GPACalculator(gradeCalc);
        
        GradeBookService newService = new GradeBookService(gradeCalc, gpaCalc);
        
        assertNotNull(newService);
    }
    
    @Test
    void testCreateServiceWithNullGradeCalculatorThrowsException() {
        GPACalculator gpaCalc = new GPACalculator();
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new GradeBookService(null, gpaCalc);
        });
        
        assertEquals("Grade calculator cannot be null", exception.getMessage());
    }
    
    @Test
    void testCreateServiceWithNullGPACalculatorThrowsException() {
        GradeCalculator gradeCalc = new GradeCalculator();
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new GradeBookService(gradeCalc, null);
        });
        
        assertEquals("GPA calculator cannot be null", exception.getMessage());
    }
    
    // ========== Student Registration Tests ==========
    
    @Test
    void testRegisterStudent() {
        service.registerStudentWithId(student1);
        
        assertEquals(1, service.getStudentCount());
        assertTrue(service.isStudentRegistered("S-12345678"));
    }
    
    @Test
    void testRegisterMultipleStudents() {
        service.registerStudentWithId(student1);
        service.registerStudentWithId(student2);
        
        assertEquals(2, service.getStudentCount());
        assertTrue(service.isStudentRegistered("S-12345678"));
        assertTrue(service.isStudentRegistered("S-87654321"));
    }
    
    @Test
    void testRegisterNullStudentThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.registerStudentWithId(null);
        });
        
        assertEquals("Student cannot be null", exception.getMessage());
    }
    
    @Test
    void testRegisterDuplicateStudentThrowsException() {
        service.registerStudentWithId(student1);
        
        Student duplicate = new Student("S-12345678", "Different Name");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.registerStudentWithId(duplicate);
        });
        
        assertTrue(exception.getMessage().contains("S-12345678"));
        assertTrue(exception.getMessage().contains("already registered"));
    }
    
    // ========== Student Retrieval Tests ==========
    
    @Test
    void testGetStudent() {
        service.registerStudentWithId(student1);
        
        Student retrieved = service.getStudent("S-12345678");
        
        assertSame(student1, retrieved);
        assertEquals("Alice Johnson", retrieved.getName());
    }
    
    @Test
    void testGetStudentNotFoundThrowsException() {
        Exception exception = assertThrows(StudentNotFoundException.class, () -> {
            service.getStudent("S-99999999");
        });
        
        assertTrue(exception.getMessage().contains("S-99999999"));
    }
    
    @Test
    void testGetStudentWithNullIdThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getStudent(null);
        });
        
        assertEquals("Student ID cannot be null or blank", exception.getMessage());
    }
    
    @Test
    void testGetStudentWithBlankIdThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getStudent("   ");
        });
        
        assertEquals("Student ID cannot be null or blank", exception.getMessage());
    }
    
    @Test
    void testGetAllStudents() {
        service.registerStudentWithId(student1);
        service.registerStudentWithId(student2);
        
        List<Student> students = service.getAllStudents();
        
        assertEquals(2, students.size());
        assertTrue(students.contains(student1));
        assertTrue(students.contains(student2));
    }
    
    @Test
    void testGetAllStudentsReturnsUnmodifiableList() {
        service.registerStudentWithId(student1);
        
        List<Student> students = service.getAllStudents();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            students.add(student2);
        });
    }
    
    @Test
    void testGetAllStudentsWhenEmpty() {
        List<Student> students = service.getAllStudents();
        
        assertTrue(students.isEmpty());
    }
    
    @Test
    void testGetStudentCount() {
        assertEquals(0, service.getStudentCount());
        
        service.registerStudentWithId(student1);
        assertEquals(1, service.getStudentCount());
        
        service.registerStudentWithId(student2);
        assertEquals(2, service.getStudentCount());
    }
    
    @Test
    void testIsStudentRegistered() {
        assertFalse(service.isStudentRegistered("S-12345678"));
        
        service.registerStudentWithId(student1);
        
        assertTrue(service.isStudentRegistered("S-12345678"));
        assertFalse(service.isStudentRegistered("S-99999999"));
    }
    
    @Test
    void testIsStudentRegisteredWithNullId() {
        assertFalse(service.isStudentRegistered(null));
    }
    
    @Test
    void testIsStudentRegisteredWithBlankId() {
        assertFalse(service.isStudentRegistered("  "));
    }
    
    // ========== Course Enrollment Tests ==========
    
    @Test
    void testEnrollStudentInCourse() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        List<Course> courses = service.getStudentCourses("S-12345678");
        
        assertEquals(1, courses.size());
        assertTrue(courses.contains(course1));
    }
    
    @Test
    void testEnrollStudentInMultipleCourses() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        service.enrollStudentInCourse("S-12345678", course2);
        
        List<Course> courses = service.getStudentCourses("S-12345678");
        
        assertEquals(2, courses.size());
        assertTrue(courses.contains(course1));
        assertTrue(courses.contains(course2));
    }
    
    @Test
    void testEnrollNonExistentStudentThrowsException() {
        Exception exception = assertThrows(StudentNotFoundException.class, () -> {
            service.enrollStudentInCourse("S-99999999", course1);
        });
        
        assertTrue(exception.getMessage().contains("S-99999999"));
    }
    
    @Test
    void testEnrollStudentInNullCourseThrowsException() {
        service.registerStudentWithId(student1);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.enrollStudentInCourse("S-12345678", null);
        });
        
        assertEquals("Course cannot be null", exception.getMessage());
    }
    
    @Test
    void testEnrollStudentInDuplicateCourseThrowsException() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        Exception exception = assertThrows(DuplicateEnrollmentException.class, () -> {
            service.enrollStudentInCourse("S-12345678", course1);
        });
        
        assertTrue(exception.getMessage().contains("Data Structures"));
    }
    
    @Test
    void testGetStudentCourses() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        service.enrollStudentInCourse("S-12345678", course2);
        
        List<Course> courses = service.getStudentCourses("S-12345678");
        
        assertEquals(2, courses.size());
    }
    
    @Test
    void testGetStudentCoursesReturnsDefensiveCopy() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        List<Course> courses = service.getStudentCourses("S-12345678");
        courses.clear();
        
        // Original should be unchanged
        List<Course> coursesAgain = service.getStudentCourses("S-12345678");
        assertEquals(1, coursesAgain.size());
    }
    
    @Test
    void testGetStudentCourse() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        Course retrieved = service.getStudentCourse("S-12345678", "Data Structures");
        
        assertSame(course1, retrieved);
    }
    
    @Test
    void testGetStudentCourseNotFoundThrowsException() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        Exception exception = assertThrows(CourseNotFoundException.class, () -> {
            service.getStudentCourse("S-12345678", "Non-Existent Course");
        });
        
        assertTrue(exception.getMessage().contains("Non-Existent Course"));
    }
    
    @Test
    void testGetStudentCreditHours() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1); // 3 credits
        service.enrollStudentInCourse("S-12345678", course2); // 4 credits
        
        int credits = service.getStudentCreditHours("S-12345678");
        
        assertEquals(7, credits);
    }
    
    @Test
    void testGetStudentCreditHoursWithNoCourses() {
        service.registerStudentWithId(student1);
        
        int credits = service.getStudentCreditHours("S-12345678");
        
        assertEquals(0, credits);
    }
    
    // ========== Assignment Management Tests ==========
    
    @Test
    void testAddAssignment() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        Assignment assignment = new Assignment("HW1", 85, 100, Category.HOMEWORK);
        service.addAssignment("S-12345678", "Data Structures", assignment);
        
        List<Assignment> assignments = service.getCourseAssignments("S-12345678", "Data Structures");
        
        assertEquals(1, assignments.size());
        assertTrue(assignments.contains(assignment));
    }
    
    @Test
    void testAddMultipleAssignments() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        Assignment hw1 = new Assignment("HW1", 85, 100, Category.HOMEWORK);
        Assignment hw2 = new Assignment("HW2", 90, 100, Category.HOMEWORK);
        Assignment quiz = new Assignment("Quiz1", 88, 100, Category.QUIZZES);
        
        service.addAssignment("S-12345678", "Data Structures", hw1);
        service.addAssignment("S-12345678", "Data Structures", hw2);
        service.addAssignment("S-12345678", "Data Structures", quiz);
        
        List<Assignment> assignments = service.getCourseAssignments("S-12345678", "Data Structures");
        
        assertEquals(3, assignments.size());
    }
    
    @Test
    void testAddNullAssignmentThrowsException() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.addAssignment("S-12345678", "Data Structures", null);
        });
        
        assertEquals("Assignment cannot be null", exception.getMessage());
    }
    
    @Test
    void testAddAssignmentToNonExistentCourseThrowsException() {
        service.registerStudentWithId(student1);
        
        Assignment assignment = new Assignment("HW1", 85, 100, Category.HOMEWORK);
        
        Exception exception = assertThrows(CourseNotFoundException.class, () -> {
            service.addAssignment("S-12345678", "Non-Existent", assignment);
        });
        
        assertTrue(exception.getMessage().contains("Non-Existent"));
    }
    
    @Test
    void testGetCourseAssignments() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        Assignment hw1 = new Assignment("HW1", 85, 100, Category.HOMEWORK);
        Assignment quiz = new Assignment("Quiz1", 90, 100, Category.QUIZZES);
        
        service.addAssignment("S-12345678", "Data Structures", hw1);
        service.addAssignment("S-12345678", "Data Structures", quiz);
        
        List<Assignment> assignments = service.getCourseAssignments("S-12345678", "Data Structures");
        
        assertEquals(2, assignments.size());
    }
    
    @Test
    void testGetCategoryAssignments() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        Assignment hw1 = new Assignment("HW1", 85, 100, Category.HOMEWORK);
        Assignment hw2 = new Assignment("HW2", 90, 100, Category.HOMEWORK);
        Assignment quiz = new Assignment("Quiz1", 88, 100, Category.QUIZZES);
        
        service.addAssignment("S-12345678", "Data Structures", hw1);
        service.addAssignment("S-12345678", "Data Structures", hw2);
        service.addAssignment("S-12345678", "Data Structures", quiz);
        
        List<Assignment> homeworks = service.getCategoryAssignments("S-12345678", "Data Structures", Category.HOMEWORK);
        
        assertEquals(2, homeworks.size());
        assertTrue(homeworks.contains(hw1));
        assertTrue(homeworks.contains(hw2));
    }
    
    @Test
    void testGetCategoryAssignmentsWithNullCategoryThrowsException() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getCategoryAssignments("S-12345678", "Data Structures", null);
        });
        
        assertEquals("Category cannot be null", exception.getMessage());
    }
    
    // ========== Grade Calculation Tests ==========
    
    @Test
    void testCalculateCourseGrade() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        course1.addAssignment(new Assignment("HW1", 90, 100, Category.HOMEWORK));
        course1.addAssignment(new Assignment("Quiz1", 85, 100, Category.QUIZZES));
        course1.addAssignment(new Assignment("Midterm", 80, 100, Category.MIDTERM));
        course1.addAssignment(new Assignment("Final", 88, 100, Category.FINAL_EXAM));
        
        CourseGrade grade = service.calculateCourseGrade("S-12345678", "Data Structures");
        
        assertEquals(85.8, grade.getNumericGrade(), 0.01);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }
    
    @Test
    void testCalculateAllCourseGrades() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        service.enrollStudentInCourse("S-12345678", course2);
        
        course1.addAssignment(new Assignment("Final", 85, 100, Category.FINAL_EXAM));
        course2.addAssignment(new Assignment("Final", 92, 100, Category.FINAL_EXAM));
        
        List<CourseGrade> grades = service.calculateAllCourseGrades("S-12345678");
        
        assertEquals(2, grades.size());
    }
    
    @Test
    void testCalculateGPA() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1); // 3 credits
        service.enrollStudentInCourse("S-12345678", course2); // 4 credits
        
        course1.addAssignment(new Assignment("Final", 85, 100, Category.FINAL_EXAM)); // B = 3.0
        course2.addAssignment(new Assignment("Final", 92, 100, Category.FINAL_EXAM)); // A = 4.0
        
        double gpa = service.calculateGPA("S-12345678");
        
        // (3.0*3 + 4.0*4) / 7 = 25/7 = 3.571
        assertEquals(3.571, gpa, 0.01);
    }
    
    @Test
    void testCalculateGPAWithNoCourses() {
        service.registerStudentWithId(student1);
        
        double gpa = service.calculateGPA("S-12345678");
        
        assertEquals(0.0, gpa, 0.01);
    }
    
    @Test
    void testCalculateCategoryAverage() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        course1.addAssignment(new Assignment("HW1", 80, 100, Category.HOMEWORK));
        course1.addAssignment(new Assignment("HW2", 90, 100, Category.HOMEWORK));
        course1.addAssignment(new Assignment("HW3", 85, 100, Category.HOMEWORK));
        
        double average = service.calculateCategoryAverage("S-12345678", "Data Structures", Category.HOMEWORK);
        
        // (80+90+85)/(100+100+100) = 255/300 = 85%
        assertEquals(85.0, average, 0.01);
    }
    
    @Test
    void testCalculateCategoryAverageWithNullCategoryThrowsException() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.calculateCategoryAverage("S-12345678", "Data Structures", null);
        });
        
        assertEquals("Category cannot be null", exception.getMessage());
    }
    
    // ========== Academic Standing Tests ==========
    
    @Test
    void testIsHonorRollWithHighGPA() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        course1.addAssignment(new Assignment("Final", 95, 100, Category.FINAL_EXAM)); // A = 4.0
        
        assertTrue(service.isHonorRoll("S-12345678"));
    }
    
    @Test
    void testIsNotHonorRollWithLowGPA() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        course1.addAssignment(new Assignment("Final", 75, 100, Category.FINAL_EXAM)); // C = 2.0
        
        assertFalse(service.isHonorRoll("S-12345678"));
    }
    
    @Test
    void testIsHonorRollAtBoundary() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        service.enrollStudentInCourse("S-12345678", course2);
        
        // GPA exactly 3.5
        course1.addAssignment(new Assignment("Final", 85, 100, Category.FINAL_EXAM)); // B = 3.0
        course2.addAssignment(new Assignment("Final", 92, 100, Category.FINAL_EXAM)); // A = 4.0
        
        double gpa = service.calculateGPA("S-12345678");
        // (3.0*3 + 4.0*4) / 7 = 25/7 = 3.571 (above 3.5)
        
        assertTrue(service.isHonorRoll("S-12345678"));
    }
    
    @Test
    void testGetHonorRollStudents() {
        service.registerStudentWithId(student1);
        service.registerStudentWithId(student2);
        
        // Student 1: High GPA
        service.enrollStudentInCourse("S-12345678", course1);
        course1.addAssignment(new Assignment("Final", 95, 100, Category.FINAL_EXAM));
        
        // Student 2: Low GPA
        Course course3 = new Course("Calculus", 3);
        service.enrollStudentInCourse("S-87654321", course3);
        course3.addAssignment(new Assignment("Final", 75, 100, Category.FINAL_EXAM));
        
        List<Student> honorRoll = service.getHonorRollStudents();
        
        assertEquals(1, honorRoll.size());
        assertTrue(honorRoll.contains(student1));
        assertFalse(honorRoll.contains(student2));
    }
    
    @Test
    void testGetHonorRollStudentsWhenNone() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        course1.addAssignment(new Assignment("Final", 75, 100, Category.FINAL_EXAM));
        
        List<Student> honorRoll = service.getHonorRollStudents();
        
        assertTrue(honorRoll.isEmpty());
    }
    
    @Test
    void testIsPassingAllCourses() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        service.enrollStudentInCourse("S-12345678", course2);
        
        course1.addAssignment(new Assignment("Final", 75, 100, Category.FINAL_EXAM)); // C
        course2.addAssignment(new Assignment("Final", 65, 100, Category.FINAL_EXAM)); // D
        
        assertTrue(service.isPassingAllCourses("S-12345678"));
    }
    
    @Test
    void testIsNotPassingAllCoursesWithFailingGrade() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        service.enrollStudentInCourse("S-12345678", course2);
        
        course1.addAssignment(new Assignment("Final", 75, 100, Category.FINAL_EXAM)); // C
        course2.addAssignment(new Assignment("Final", 45, 100, Category.FINAL_EXAM)); // F
        
        assertFalse(service.isPassingAllCourses("S-12345678"));
    }
    
    @Test
    void testGetStudentsWithFailingGrades() {
        service.registerStudentWithId(student1);
        service.registerStudentWithId(student2);
        
        // Student 1: Passing all
        service.enrollStudentInCourse("S-12345678", course1);
        course1.addAssignment(new Assignment("Final", 75, 100, Category.FINAL_EXAM));
        
        // Student 2: Failing one
        Course course3 = new Course("Calculus", 3);
        service.enrollStudentInCourse("S-87654321", course3);
        course3.addAssignment(new Assignment("Final", 45, 100, Category.FINAL_EXAM));
        
        List<Student> failing = service.getStudentsWithFailingGrades();
        
        assertEquals(1, failing.size());
        assertTrue(failing.contains(student2));
        assertFalse(failing.contains(student1));
    }
    
    @Test
    void testGetStudentsWithFailingGradesWhenNone() {
        service.registerStudentWithId(student1);
        service.enrollStudentInCourse("S-12345678", course1);
        course1.addAssignment(new Assignment("Final", 75, 100, Category.FINAL_EXAM));
        
        List<Student> failing = service.getStudentsWithFailingGrades();
        
        assertTrue(failing.isEmpty());
    }
    
    @Test
    void testIsPassingAllCoursesWithNoCourses() {
        service.registerStudentWithId(student1);
        
        // Student with no courses should not be considered "passing all courses"
        assertFalse(service.isPassingAllCourses("S-12345678"));
    }
    
    @Test
    void testGetStudentsWithFailingGradesIncludesStudentsWithNoCourses() {
        service.registerStudentWithId(student1);
        service.registerStudentWithId(student2);
        
        // Student 1: No courses
        // Student 2: Passing course
        service.enrollStudentInCourse("S-87654321", course1);
        course1.addAssignment(new Assignment("Final", 75, 100, Category.FINAL_EXAM));
        
        List<Student> failing = service.getStudentsWithFailingGrades();
        
        // Student 1 should be in failing list (no courses = not passing all)
        assertEquals(1, failing.size());
        assertTrue(failing.contains(student1));
        assertFalse(failing.contains(student2));
    }
}
