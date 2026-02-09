package com.prathamesh.gradebook.domain;

import com.prathamesh.gradebook.exception.CourseNotFoundException;
import com.prathamesh.gradebook.exception.DuplicateEnrollmentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {
    
    // ========== Constructor Tests - Valid Cases ==========
    
    @Test
    void testCreateStudentWithValidData() {
        Student student = new Student("S-12345678", "John Doe");
        
        assertEquals("S-12345678", student.getStudentId());
        assertEquals("John Doe", student.getName());
        assertTrue(student.hasNoCourses());
        assertEquals(0, student.getCourseCount());
    }
    
    @Test
    void testCreateStudentWithWhitespace() {
        Student student = new Student("  S-12345678  ", "  Jane Smith  ");
        
        assertEquals("S-12345678", student.getStudentId());
        assertEquals("Jane Smith", student.getName());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "S-00000000",
        "S-12345678",
        "S-99999999",
        "S-00000001"
    })
    void testValidStudentIdFormats(String studentId) {
        Student student = new Student(studentId, "Test Student");
        assertEquals(studentId, student.getStudentId());
    }
    
    // ========== Constructor Tests - Invalid Student IDs ==========
    
    @Test
    void testNullStudentIdThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Student(null, "John Doe"));
        
        assertTrue(exception.getMessage().toLowerCase().contains("student id"));
        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }
    
    @Test
    void testBlankStudentIdThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Student("   ", "John Doe"));
        
        assertTrue(exception.getMessage().toLowerCase().contains("student id"));
    }
    
    @Test
    void testEmptyStudentIdThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Student("", "John Doe"));
        
        assertTrue(exception.getMessage().toLowerCase().contains("student id"));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "12345678",           // Missing S- prefix
        "S-1234567",          // Only 7 digits
        "S-123456789",        // 9 digits
        "S-ABCD1234",         // Contains letters
        "S-1234-5678",        // Contains hyphen in number
        "S- 12345678",        // Space after hyphen
        "s-12345678",         // Lowercase s
        "S12345678",          // Missing hyphen
        "S--12345678",        // Double hyphen
        "T-12345678",         // Wrong prefix
        "S-1234567X",         // Letter at end
    })
    void testInvalidStudentIdFormats(String invalidId) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Student(invalidId, "Test Student"));
        
        assertTrue(exception.getMessage().contains("S-XXXXXXXX"));
    }
    
    // ========== Constructor Tests - Invalid Names ==========
    
    @Test
    void testNullNameThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Student("S-12345678", null));
        
        assertTrue(exception.getMessage().toLowerCase().contains("name"));
        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }
    
    @Test
    void testBlankNameThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Student("S-12345678", "   "));
        
        assertTrue(exception.getMessage().toLowerCase().contains("name"));
        assertTrue(exception.getMessage().toLowerCase().contains("blank"));
    }
    
    @Test
    void testEmptyNameThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Student("S-12345678", ""));
        
        assertTrue(exception.getMessage().toLowerCase().contains("name"));
    }
    
    // ========== Course Enrollment Tests ==========
    
    @Test
    void testEnrollInSingleCourse() {
        Student student = new Student("S-12345678", "John Doe");
        Course course = new Course("Data Structures", 4);
        
        student.enrollInCourse(course);
        
        assertEquals(1, student.getCourseCount());
        assertFalse(student.hasNoCourses());
        assertTrue(student.isEnrolledIn("Data Structures"));
    }
    
    @Test
    void testEnrollInMultipleCourses() {
        Student student = new Student("S-12345678", "John Doe");
        Course course1 = new Course("Data Structures", 4);
        Course course2 = new Course("Algorithms", 3);
        Course course3 = new Course("Operating Systems", 4);
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        student.enrollInCourse(course3);
        
        assertEquals(3, student.getCourseCount());
        assertTrue(student.isEnrolledIn("Data Structures"));
        assertTrue(student.isEnrolledIn("Algorithms"));
        assertTrue(student.isEnrolledIn("Operating Systems"));
    }
    
    @Test
    void testEnrollNullCourseThrowsException() {
        Student student = new Student("S-12345678", "John Doe");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> student.enrollInCourse(null));
        
        assertTrue(exception.getMessage().toLowerCase().contains("course"));
        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }
    
    @Test
    void testDuplicateEnrollmentThrowsException() {
        Student student = new Student("S-12345678", "John Doe");
        Course course1 = new Course("Data Structures", 4);
        Course course2 = new Course("Data Structures", 3); // Same name, different credits
        
        student.enrollInCourse(course1);
        
        DuplicateEnrollmentException exception = assertThrows(DuplicateEnrollmentException.class,
            () -> student.enrollInCourse(course2));
        
        assertEquals("S-12345678", exception.getStudentId());
        assertEquals("Data Structures", exception.getCourseName());
    }
    
    // ========== Course Retrieval Tests ==========
    
    @Test
    void testGetCourse() {
        Student student = new Student("S-12345678", "John Doe");
        Course course = new Course("Algorithms", 3);
        student.enrollInCourse(course);
        
        Course retrieved = student.getCourse("Algorithms");
        
        assertSame(course, retrieved);
    }
    
    @Test
    void testGetCourseWithWhitespace() {
        Student student = new Student("S-12345678", "John Doe");
        Course course = new Course("Algorithms", 3);
        student.enrollInCourse(course);
        
        Course retrieved = student.getCourse("  Algorithms  ");
        
        assertSame(course, retrieved);
    }
    
    @Test
    void testGetCourseNotFoundThrowsException() {
        Student student = new Student("S-12345678", "John Doe");
        
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class,
            () -> student.getCourse("Nonexistent Course"));
        
        assertEquals("S-12345678", exception.getStudentId());
        assertEquals("Nonexistent Course", exception.getCourseName());
    }
    
    @Test
    void testGetCourseWithNullNameThrowsException() {
        Student student = new Student("S-12345678", "John Doe");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> student.getCourse(null));
        
        assertTrue(exception.getMessage().toLowerCase().contains("course name"));
    }
    
    @Test
    void testGetCourseWithBlankNameThrowsException() {
        Student student = new Student("S-12345678", "John Doe");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> student.getCourse("   "));
        
        assertTrue(exception.getMessage().toLowerCase().contains("course name"));
    }
    
    @Test
    void testGetAllCourses() {
        Student student = new Student("S-12345678", "John Doe");
        Course course1 = new Course("Data Structures", 4);
        Course course2 = new Course("Algorithms", 3);
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        
        Collection<Course> courses = student.getAllCourses();
        
        assertEquals(2, courses.size());
        assertTrue(courses.contains(course1));
        assertTrue(courses.contains(course2));
    }
    
    @Test
    void testGetAllCoursesReturnsDefensiveCopy() {
        Student student = new Student("S-12345678", "John Doe");
        Course course = new Course("Data Structures", 4);
        student.enrollInCourse(course);
        
        Collection<Course> courses1 = student.getAllCourses();
        Collection<Course> courses2 = student.getAllCourses();
        
        // Should be different instances
        assertNotSame(courses1, courses2);
        
        // But equal contents
        assertEquals(courses1, courses2);
        
        // Modifying returned collection should not affect student
        courses1.clear();
        assertEquals(1, student.getCourseCount());
    }
    
    @Test
    void testGetAllCoursesWhenEmpty() {
        Student student = new Student("S-12345678", "John Doe");
        
        Collection<Course> courses = student.getAllCourses();
        
        assertNotNull(courses);
        assertTrue(courses.isEmpty());
    }
    
    @Test
    void testGetCourseNames() {
        Student student = new Student("S-12345678", "John Doe");
        student.enrollInCourse(new Course("Data Structures", 4));
        student.enrollInCourse(new Course("Algorithms", 3));
        student.enrollInCourse(new Course("Operating Systems", 4));
        
        Set<String> courseNames = student.getCourseNames();
        
        assertEquals(3, courseNames.size());
        assertTrue(courseNames.contains("Data Structures"));
        assertTrue(courseNames.contains("Algorithms"));
        assertTrue(courseNames.contains("Operating Systems"));
    }
    
    @Test
    void testGetCourseNamesReturnsDefensiveCopy() {
        Student student = new Student("S-12345678", "John Doe");
        student.enrollInCourse(new Course("Data Structures", 4));
        
        Set<String> names1 = student.getCourseNames();
        Set<String> names2 = student.getCourseNames();
        
        assertNotSame(names1, names2);
        assertEquals(names1, names2);
        
        names1.clear();
        assertEquals(1, student.getCourseCount());
    }
    
    // ========== Query Operations Tests ==========
    
    @Test
    void testIsEnrolledIn() {
        Student student = new Student("S-12345678", "John Doe");
        student.enrollInCourse(new Course("Data Structures", 4));
        
        assertTrue(student.isEnrolledIn("Data Structures"));
        assertFalse(student.isEnrolledIn("Algorithms"));
    }
    
    @Test
    void testIsEnrolledInWithWhitespace() {
        Student student = new Student("S-12345678", "John Doe");
        student.enrollInCourse(new Course("Data Structures", 4));
        
        assertTrue(student.isEnrolledIn("  Data Structures  "));
    }
    
    @Test
    void testIsEnrolledInWithNullThrowsException() {
        Student student = new Student("S-12345678", "John Doe");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> student.isEnrolledIn(null));
        
        assertTrue(exception.getMessage().toLowerCase().contains("course name"));
    }
    
    @Test
    void testIsEnrolledInWithBlankThrowsException() {
        Student student = new Student("S-12345678", "John Doe");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> student.isEnrolledIn("   "));
        
        assertTrue(exception.getMessage().toLowerCase().contains("course name"));
    }
    
    @Test
    void testGetCourseCount() {
        Student student = new Student("S-12345678", "John Doe");
        
        assertEquals(0, student.getCourseCount());
        
        student.enrollInCourse(new Course("Course 1", 3));
        assertEquals(1, student.getCourseCount());
        
        student.enrollInCourse(new Course("Course 2", 4));
        assertEquals(2, student.getCourseCount());
    }
    
    @Test
    void testHasNoCourses() {
        Student student = new Student("S-12345678", "John Doe");
        
        assertTrue(student.hasNoCourses());
        
        student.enrollInCourse(new Course("Data Structures", 4));
        
        assertFalse(student.hasNoCourses());
    }
    
    // ========== Aggregation Operations Tests ==========
    
    @Test
    void testGetTotalCreditHours() {
        Student student = new Student("S-12345678", "John Doe");
        
        assertEquals(0, student.getTotalCreditHours());
        
        student.enrollInCourse(new Course("Course 1", 4));
        assertEquals(4, student.getTotalCreditHours());
        
        student.enrollInCourse(new Course("Course 2", 3));
        assertEquals(7, student.getTotalCreditHours());
        
        student.enrollInCourse(new Course("Course 3", 4));
        assertEquals(11, student.getTotalCreditHours());
    }
    
    @Test
    void testGetTotalAssignmentCount() {
        Student student = new Student("S-12345678", "John Doe");
        Course course1 = new Course("Data Structures", 4);
        Course course2 = new Course("Algorithms", 3);
        
        course1.addAssignment(new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK));
        course1.addAssignment(new Assignment("HW2", 48.0, 50.0, Category.HOMEWORK));
        course2.addAssignment(new Assignment("Quiz 1", 18.0, 20.0, Category.QUIZZES));
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        
        assertEquals(3, student.getTotalAssignmentCount());
    }
    
    @Test
    void testGetTotalAssignmentCountWhenEmpty() {
        Student student = new Student("S-12345678", "John Doe");
        assertEquals(0, student.getTotalAssignmentCount());
    }
    
    @Test
    void testGetAllAssignments() {
        Student student = new Student("S-12345678", "John Doe");
        Course course1 = new Course("Data Structures", 4);
        Course course2 = new Course("Algorithms", 3);
        
        Assignment hw1 = new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK);
        Assignment hw2 = new Assignment("HW2", 48.0, 50.0, Category.HOMEWORK);
        Assignment quiz1 = new Assignment("Quiz 1", 18.0, 20.0, Category.QUIZZES);
        
        course1.addAssignment(hw1);
        course1.addAssignment(hw2);
        course2.addAssignment(quiz1);
        
        student.enrollInCourse(course1);
        student.enrollInCourse(course2);
        
        List<Assignment> allAssignments = student.getAllAssignments();
        
        assertEquals(3, allAssignments.size());
        assertTrue(allAssignments.contains(hw1));
        assertTrue(allAssignments.contains(hw2));
        assertTrue(allAssignments.contains(quiz1));
    }
    
    @Test
    void testGetAllAssignmentsWhenEmpty() {
        Student student = new Student("S-12345678", "John Doe");
        
        List<Assignment> assignments = student.getAllAssignments();
        
        assertNotNull(assignments);
        assertTrue(assignments.isEmpty());
    }
    
    // ========== Equals and HashCode Tests ==========
    
    @Test
    void testEqualsWithSameId() {
        Student student1 = new Student("S-12345678", "John Doe");
        Student student2 = new Student("S-12345678", "Jane Smith"); // Different name, same ID
        
        assertEquals(student1, student2);
        assertEquals(student1.hashCode(), student2.hashCode());
    }
    
    @Test
    void testEqualsWithSameObject() {
        Student student = new Student("S-12345678", "John Doe");
        assertEquals(student, student);
    }
    
    @Test
    void testNotEqualsWithDifferentId() {
        Student student1 = new Student("S-12345678", "John Doe");
        Student student2 = new Student("S-87654321", "John Doe"); // Same name, different ID
        
        assertNotEquals(student1, student2);
    }
    
    @Test
    void testNotEqualsWithNull() {
        Student student = new Student("S-12345678", "John Doe");
        assertNotEquals(student, null);
    }
    
    @Test
    void testNotEqualsWithDifferentClass() {
        Student student = new Student("S-12345678", "John Doe");
        String other = "Not a student";
        assertNotEquals(student, other);
    }
    
    @Test
    void testEqualsIgnoresCourseEnrollments() {
        Student student1 = new Student("S-12345678", "John Doe");
        Student student2 = new Student("S-12345678", "John Doe");
        
        student1.enrollInCourse(new Course("Data Structures", 4));
        
        // Should still be equal (equals doesn't compare courses)
        assertEquals(student1, student2);
    }
    
    @Test
    void testHashCodeConsistency() {
        Student student = new Student("S-12345678", "John Doe");
        int hash1 = student.hashCode();
        int hash2 = student.hashCode();
        assertEquals(hash1, hash2);
    }
    
    @Test
    void testUseInHashSet() {
        Set<Student> students = new HashSet<>();
        Student student1 = new Student("S-12345678", "John Doe");
        Student student2 = new Student("S-12345678", "Jane Smith"); // Same ID
        Student student3 = new Student("S-87654321", "Bob Johnson");
        
        students.add(student1);
        assertTrue(students.contains(student2)); // Equal student
        assertEquals(1, students.size());
        
        students.add(student3);
        assertEquals(2, students.size());
    }
    
    // ========== ToString Tests ==========
    
    @Test
    void testToString() {
        Student student = new Student("S-12345678", "John Doe");
        String result = student.toString();
        
        assertTrue(result.contains("S-12345678"));
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("0")); // 0 courses
    }
    
    @Test
    void testToStringWithCourses() {
        Student student = new Student("S-12345678", "John Doe");
        student.enrollInCourse(new Course("Data Structures", 4));
        student.enrollInCourse(new Course("Algorithms", 3));
        
        String result = student.toString();
        assertTrue(result.contains("S-12345678"));
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("2")); // 2 courses
        assertTrue(result.contains("7")); // 7 total credits
    }
    
    // ========== Edge Cases ==========
    
    @Test
    void testStudentNameWithSpecialCharacters() {
        Student student = new Student("S-12345678", "O'Brien-Smith Jr.");
        assertEquals("O'Brien-Smith Jr.", student.getName());
    }
    
    @Test
    void testStudentNameWithUnicode() {
        Student student = new Student("S-12345678", "José García");
        assertEquals("José García", student.getName());
    }
    
    @Test
    void testVeryLongStudentName() {
        String longName = "Christopher Alexander Montgomery Wellington III";
        Student student = new Student("S-12345678", longName);
        assertEquals(longName, student.getName());
    }
    
    @Test
    void testEnrollInManyCourses() {
        Student student = new Student("S-12345678", "John Doe");
        
        for (int i = 1; i <= 20; i++) {
            student.enrollInCourse(new Course("Course " + i, 3));
        }
        
        assertEquals(20, student.getCourseCount());
        assertEquals(60, student.getTotalCreditHours());
    }
    
    @Test
    void testCourseNamesCaseSensitive() {
        Student student = new Student("S-12345678", "John Doe");
        student.enrollInCourse(new Course("Data Structures", 4));
        
        assertTrue(student.isEnrolledIn("Data Structures"));
        assertFalse(student.isEnrolledIn("data structures"));
        assertFalse(student.isEnrolledIn("DATA STRUCTURES"));
    }
    
    @Test
    void testMultipleStudentsIndependent() {
        Student student1 = new Student("S-11111111", "Student 1");
        Student student2 = new Student("S-22222222", "Student 2");
        
        student1.enrollInCourse(new Course("Course 1", 3));
        
        assertEquals(1, student1.getCourseCount());
        assertEquals(0, student2.getCourseCount());
    }
    
    @Test
    void testStudentWithZeroLengthCourseList() {
        Student student = new Student("S-12345678", "John Doe");
        
        assertEquals(0, student.getCourseCount());
        assertEquals(0, student.getTotalCreditHours());
        assertEquals(0, student.getTotalAssignmentCount());
        assertTrue(student.getAllCourses().isEmpty());
        assertTrue(student.getCourseNames().isEmpty());
        assertTrue(student.getAllAssignments().isEmpty());
    }
    
    @Test
    void testGetCourseFromMultipleCourses() {
        Student student = new Student("S-12345678", "John Doe");
        Course ds = new Course("Data Structures", 4);
        Course algo = new Course("Algorithms", 3);
        Course os = new Course("Operating Systems", 4);
        
        student.enrollInCourse(ds);
        student.enrollInCourse(algo);
        student.enrollInCourse(os);
        
        assertSame(ds, student.getCourse("Data Structures"));
        assertSame(algo, student.getCourse("Algorithms"));
        assertSame(os, student.getCourse("Operating Systems"));
    }
    
    @Test
    void testCompositePatternIntegration() {
        // Test the full Composite pattern: Student → Course → Assignment
        Student student = new Student("S-12345678", "John Doe");
        Course course = new Course("Data Structures", 4);
        Assignment assignment = new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK);
        
        course.addAssignment(assignment);
        student.enrollInCourse(course);
        
        // Verify the hierarchy
        assertEquals(1, student.getCourseCount());
        assertEquals(1, student.getTotalAssignmentCount());
        
        List<Assignment> allAssignments = student.getAllAssignments();
        assertEquals(1, allAssignments.size());
        assertTrue(allAssignments.contains(assignment));
        
        // Verify we can traverse the hierarchy
        Course retrievedCourse = student.getCourse("Data Structures");
        List<Assignment> courseAssignments = retrievedCourse.getAssignments();
        assertEquals(1, courseAssignments.size());
        assertEquals(assignment, courseAssignments.get(0));
    }
}
