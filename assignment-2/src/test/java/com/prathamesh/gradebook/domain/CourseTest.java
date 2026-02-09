package com.prathamesh.gradebook.domain;

import com.prathamesh.gradebook.exception.InvalidCategoryException;
import com.prathamesh.gradebook.exception.InvalidCreditHoursException;
import com.prathamesh.gradebook.exception.InvalidWeightException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
    
    // ========== Constructor Tests - Default Weights ==========
    
    @Test
    void testCreateCourseWithDefaultWeights() {
        Course course = new Course("Data Structures", 4);
        
        assertEquals("Data Structures", course.getCourseName());
        assertEquals(4, course.getCreditHours());
        assertTrue(course.isEmpty());
        assertEquals(0, course.getAssignmentCount());
    }
    
    @Test
    void testDefaultWeightsSumTo100() {
        Course course = new Course("Algorithms", 3);
        Map<Category, Double> weights = course.getCategoryWeights();
        
        double sum = Category.sumWeights(weights);
        assertEquals(100.0, sum, 0.01);
    }
    
    @Test
    void testCourseNameTrimmed() {
        Course course = new Course("  Database Systems  ", 3);
        assertEquals("Database Systems", course.getCourseName());
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6})
    void testValidCreditHours(int creditHours) {
        Course course = new Course("Test Course", creditHours);
        assertEquals(creditHours, course.getCreditHours());
    }
    
    // ========== Constructor Tests - Custom Weights ==========
    
    @Test
    void testCreateCourseWithCustomWeights() {
        Map<Category, Double> customWeights = new EnumMap<>(Category.class);
        customWeights.put(Category.HOMEWORK, 30.0);
        customWeights.put(Category.QUIZZES, 20.0);
        customWeights.put(Category.MIDTERM, 20.0);
        customWeights.put(Category.FINAL_EXAM, 30.0);
        
        Course course = new Course("Operating Systems", 4, customWeights);
        
        assertEquals(30.0, course.getCategoryWeight(Category.HOMEWORK), 0.01);
        assertEquals(20.0, course.getCategoryWeight(Category.QUIZZES), 0.01);
        assertEquals(20.0, course.getCategoryWeight(Category.MIDTERM), 0.01);
        assertEquals(30.0, course.getCategoryWeight(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testCustomWeightsAreDefensivelyCopied() {
        Map<Category, Double> customWeights = new EnumMap<>(Category.class);
        customWeights.put(Category.HOMEWORK, 30.0);
        customWeights.put(Category.QUIZZES, 20.0);
        customWeights.put(Category.MIDTERM, 20.0);
        customWeights.put(Category.FINAL_EXAM, 30.0);
        
        Course course = new Course("Networks", 3, customWeights);
        
        // Modify original map
        customWeights.put(Category.HOMEWORK, 50.0);
        
        // Course should still have original weight
        assertEquals(30.0, course.getCategoryWeight(Category.HOMEWORK), 0.01);
    }
    
    @Test
    void testGetCategoryWeightsReturnsDefensiveCopy() {
        Course course = new Course("Compilers", 3);
        Map<Category, Double> weights1 = course.getCategoryWeights();
        Map<Category, Double> weights2 = course.getCategoryWeights();
        
        // Should be different instances
        assertNotSame(weights1, weights2);
        
        // But equal contents
        assertEquals(weights1, weights2);
        
        // Modifying returned map should not affect course
        weights1.put(Category.HOMEWORK, 99.0);
        assertEquals(20.0, course.getCategoryWeight(Category.HOMEWORK), 0.01);
    }
    
    // ========== Validation Tests ==========
    
    @Test
    void testNullCourseNameThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Course(null, 3));
        
        assertTrue(exception.getMessage().toLowerCase().contains("name"));
        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }
    
    @Test
    void testBlankCourseNameThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Course("   ", 3));
        
        assertTrue(exception.getMessage().toLowerCase().contains("name"));
        assertTrue(exception.getMessage().toLowerCase().contains("blank"));
    }
    
    @Test
    void testEmptyCourseNameThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Course("", 3));
        
        assertTrue(exception.getMessage().toLowerCase().contains("name"));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {-5, -1, 0, 7, 10, 100})
    void testInvalidCreditHoursThrowsException(int invalidCredits) {
        InvalidCreditHoursException exception = assertThrows(InvalidCreditHoursException.class,
            () -> new Course("Test", invalidCredits));
        
        assertEquals(invalidCredits, exception.getCreditHours());
    }
    
    @Test
    void testNullCategoryWeightsThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> new Course("Test", 3, null));
        
        assertTrue(exception.getMessage().toLowerCase().contains("weight"));
        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }
    
    @Test
    void testInvalidWeightSumThrowsException() {
        Map<Category, Double> invalidWeights = new EnumMap<>(Category.class);
        invalidWeights.put(Category.HOMEWORK, 30.0);
        invalidWeights.put(Category.QUIZZES, 30.0);
        invalidWeights.put(Category.MIDTERM, 20.0);
        invalidWeights.put(Category.FINAL_EXAM, 30.0); // Sum = 110
        
        InvalidWeightException exception = assertThrows(InvalidWeightException.class,
            () -> new Course("Test", 3, invalidWeights));
        
        assertEquals(110.0, exception.getActualSum(), 0.01);
    }
    
    // ========== Adding Assignments Tests ==========
    
    @Test
    void testAddSingleAssignment() {
        Course course = new Course("Java Programming", 4);
        Assignment assignment = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        
        course.addAssignment(assignment);
        
        assertEquals(1, course.getAssignmentCount());
        assertFalse(course.isEmpty());
        assertTrue(course.getAssignments().contains(assignment));
    }
    
    @Test
    void testAddMultipleAssignments() {
        Course course = new Course("Algorithms", 3);
        Assignment hw1 = new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK);
        Assignment hw2 = new Assignment("HW2", 48.0, 50.0, Category.HOMEWORK);
        Assignment quiz1 = new Assignment("Quiz 1", 18.0, 20.0, Category.QUIZZES);
        
        course.addAssignment(hw1);
        course.addAssignment(hw2);
        course.addAssignment(quiz1);
        
        assertEquals(3, course.getAssignmentCount());
        assertEquals(2, course.getAssignmentCountByCategory(Category.HOMEWORK));
        assertEquals(1, course.getAssignmentCountByCategory(Category.QUIZZES));
    }
    
    @Test
    void testAddNullAssignmentThrowsException() {
        Course course = new Course("Test", 3);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> course.addAssignment(null));
        
        assertTrue(exception.getMessage().toLowerCase().contains("assignment"));
        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }
    
    @Test
    void testAddAssignmentWithUnconfiguredCategoryThrowsException() {
        Map<Category, Double> customWeights = new EnumMap<>(Category.class);
        customWeights.put(Category.MIDTERM, 50.0);
        customWeights.put(Category.FINAL_EXAM, 50.0);
        // Only MIDTERM and FINAL_EXAM configured
        
        Course course = new Course("Test", 3, customWeights);
        Assignment homework = new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK);
        
        InvalidCategoryException exception = assertThrows(InvalidCategoryException.class,
            () -> course.addAssignment(homework));
        
        assertEquals("HOMEWORK", exception.getCategoryName());
        assertEquals("Test", exception.getCourseName());
    }
    
    @Test
    void testGetAssignmentsReturnsDefensiveCopy() {
        Course course = new Course("Test", 3);
        Assignment assignment = new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK);
        course.addAssignment(assignment);
        
        List<Assignment> assignments1 = course.getAssignments();
        List<Assignment> assignments2 = course.getAssignments();
        
        // Should be different list instances
        assertNotSame(assignments1, assignments2);
        
        // But equal contents
        assertEquals(assignments1, assignments2);
        
        // Modifying returned list should not affect course
        assignments1.clear();
        assertEquals(1, course.getAssignmentCount());
    }
    
    // ========== Filter by Category Tests ==========
    
    @Test
    void testGetAssignmentsByCategory() {
        Course course = new Course("Test", 3);
        Assignment hw1 = new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK);
        Assignment hw2 = new Assignment("HW2", 48.0, 50.0, Category.HOMEWORK);
        Assignment quiz1 = new Assignment("Quiz 1", 18.0, 20.0, Category.QUIZZES);
        
        course.addAssignment(hw1);
        course.addAssignment(hw2);
        course.addAssignment(quiz1);
        
        List<Assignment> homeworks = course.getAssignmentsByCategory(Category.HOMEWORK);
        assertEquals(2, homeworks.size());
        assertTrue(homeworks.contains(hw1));
        assertTrue(homeworks.contains(hw2));
        
        List<Assignment> quizzes = course.getAssignmentsByCategory(Category.QUIZZES);
        assertEquals(1, quizzes.size());
        assertTrue(quizzes.contains(quiz1));
        
        List<Assignment> midterms = course.getAssignmentsByCategory(Category.MIDTERM);
        assertEquals(0, midterms.size());
    }
    
    @Test
    void testGetAssignmentsByCategoryWithNullThrowsException() {
        Course course = new Course("Test", 3);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> course.getAssignmentsByCategory(null));
        
        assertTrue(exception.getMessage().toLowerCase().contains("category"));
        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }
    
    @Test
    void testGetAssignmentCountByCategory() {
        Course course = new Course("Test", 3);
        course.addAssignment(new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 48.0, 50.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz 1", 18.0, 20.0, Category.QUIZZES));
        
        assertEquals(2, course.getAssignmentCountByCategory(Category.HOMEWORK));
        assertEquals(1, course.getAssignmentCountByCategory(Category.QUIZZES));
        assertEquals(0, course.getAssignmentCountByCategory(Category.MIDTERM));
        assertEquals(0, course.getAssignmentCountByCategory(Category.FINAL_EXAM));
    }
    
    @Test
    void testGetAssignmentCountByCategoryWithNullThrowsException() {
        Course course = new Course("Test", 3);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> course.getAssignmentCountByCategory(null));
        
        assertTrue(exception.getMessage().toLowerCase().contains("category"));
    }
    
    // ========== Category Checks Tests ==========
    
    @Test
    void testHasAssignmentsInCategory() {
        Course course = new Course("Test", 3);
        
        assertFalse(course.hasAssignmentsInCategory(Category.HOMEWORK));
        
        course.addAssignment(new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK));
        
        assertTrue(course.hasAssignmentsInCategory(Category.HOMEWORK));
        assertFalse(course.hasAssignmentsInCategory(Category.QUIZZES));
    }
    
    @Test
    void testHasAssignmentsInCategoryWithNullThrowsException() {
        Course course = new Course("Test", 3);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> course.hasAssignmentsInCategory(null));
        
        assertTrue(exception.getMessage().toLowerCase().contains("category"));
    }
    
    @Test
    void testGetCategoriesWithAssignments() {
        Course course = new Course("Test", 3);
        
        Set<Category> categories = course.getCategoriesWithAssignments();
        assertTrue(categories.isEmpty());
        
        course.addAssignment(new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("HW2", 48.0, 50.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz 1", 18.0, 20.0, Category.QUIZZES));
        
        categories = course.getCategoriesWithAssignments();
        assertEquals(2, categories.size());
        assertTrue(categories.contains(Category.HOMEWORK));
        assertTrue(categories.contains(Category.QUIZZES));
        assertFalse(categories.contains(Category.MIDTERM));
    }
    
    @Test
    void testIsEmpty() {
        Course course = new Course("Test", 3);
        assertTrue(course.isEmpty());
        
        course.addAssignment(new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK));
        assertFalse(course.isEmpty());
    }
    
    @Test
    void testGetCategoryWeight() {
        Course course = new Course("Test", 3);
        
        assertEquals(20.0, course.getCategoryWeight(Category.HOMEWORK), 0.01);
        assertEquals(20.0, course.getCategoryWeight(Category.QUIZZES), 0.01);
        assertEquals(25.0, course.getCategoryWeight(Category.MIDTERM), 0.01);
        assertEquals(35.0, course.getCategoryWeight(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testGetCategoryWeightWithNullThrowsException() {
        Course course = new Course("Test", 3);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> course.getCategoryWeight(null));
        
        assertTrue(exception.getMessage().toLowerCase().contains("category"));
    }
    
    @Test
    void testGetCategoryWeightWithUnconfiguredCategoryThrowsException() {
        Map<Category, Double> customWeights = new EnumMap<>(Category.class);
        customWeights.put(Category.MIDTERM, 50.0);
        customWeights.put(Category.FINAL_EXAM, 50.0);
        
        Course course = new Course("Test", 3, customWeights);
        
        InvalidCategoryException exception = assertThrows(InvalidCategoryException.class,
            () -> course.getCategoryWeight(Category.HOMEWORK));
        
        assertEquals("HOMEWORK", exception.getCategoryName());
    }
    
    // ========== Equals and HashCode Tests ==========
    
    @Test
    void testEqualsWithSameNameAndCredits() {
        Course course1 = new Course("Data Structures", 4);
        Course course2 = new Course("Data Structures", 4);
        
        assertEquals(course1, course2);
        assertEquals(course1.hashCode(), course2.hashCode());
    }
    
    @Test
    void testEqualsWithSameObject() {
        Course course = new Course("Algorithms", 3);
        assertEquals(course, course);
    }
    
    @Test
    void testNotEqualsWithDifferentName() {
        Course course1 = new Course("Data Structures", 4);
        Course course2 = new Course("Algorithms", 4);
        
        assertNotEquals(course1, course2);
    }
    
    @Test
    void testNotEqualsWithDifferentCredits() {
        Course course1 = new Course("Data Structures", 3);
        Course course2 = new Course("Data Structures", 4);
        
        assertNotEquals(course1, course2);
    }
    
    @Test
    void testNotEqualsWithNull() {
        Course course = new Course("Test", 3);
        assertNotEquals(course, null);
    }
    
    @Test
    void testNotEqualsWithDifferentClass() {
        Course course = new Course("Test", 3);
        String other = "Not a course";
        assertNotEquals(course, other);
    }
    
    @Test
    void testEqualsIgnoresAssignments() {
        Course course1 = new Course("Test", 3);
        Course course2 = new Course("Test", 3);
        
        course1.addAssignment(new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK));
        
        // Should still be equal (equals doesn't compare assignments)
        assertEquals(course1, course2);
    }
    
    // ========== ToString Tests ==========
    
    @Test
    void testToString() {
        Course course = new Course("Data Structures", 4);
        String result = course.toString();
        
        assertTrue(result.contains("Data Structures"));
        assertTrue(result.contains("4"));
        assertTrue(result.contains("0")); // 0 assignments
    }
    
    @Test
    void testToStringWithAssignments() {
        Course course = new Course("Algorithms", 3);
        course.addAssignment(new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK));
        course.addAssignment(new Assignment("Quiz 1", 18.0, 20.0, Category.QUIZZES));
        
        String result = course.toString();
        assertTrue(result.contains("Algorithms"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("2")); // 2 assignments
    }
    
    // ========== Builder Pattern Tests ==========
    
    @Test
    void testBuilderWithDefaults() {
        Course course = new Course.Builder("Software Engineering", 4).build();
        
        assertEquals("Software Engineering", course.getCourseName());
        assertEquals(4, course.getCreditHours());
        assertEquals(20.0, course.getCategoryWeight(Category.HOMEWORK), 0.01);
    }
    
    @Test
    void testBuilderWithCustomWeights() {
        Map<Category, Double> customWeights = new EnumMap<>(Category.class);
        customWeights.put(Category.HOMEWORK, 30.0);
        customWeights.put(Category.QUIZZES, 20.0);
        customWeights.put(Category.MIDTERM, 20.0);
        customWeights.put(Category.FINAL_EXAM, 30.0);
        
        Course course = new Course.Builder("Networks", 3)
            .withCategoryWeights(customWeights)
            .build();
        
        assertEquals(30.0, course.getCategoryWeight(Category.HOMEWORK), 0.01);
        assertEquals(30.0, course.getCategoryWeight(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testBuilderWithIndividualWeights() {
        Course course = new Course.Builder("Databases", 4)
            .withCategoryWeight(Category.HOMEWORK, 30.0)
            .withCategoryWeight(Category.QUIZZES, 30.0)
            .withCategoryWeight(Category.MIDTERM, 15.0)
            .withCategoryWeight(Category.FINAL_EXAM, 25.0)
            .build();
        
        assertEquals(30.0, course.getCategoryWeight(Category.HOMEWORK), 0.01);
        assertEquals(30.0, course.getCategoryWeight(Category.QUIZZES), 0.01);
        assertEquals(15.0, course.getCategoryWeight(Category.MIDTERM), 0.01);
        assertEquals(25.0, course.getCategoryWeight(Category.FINAL_EXAM), 0.01);
    }
    
    @Test
    void testBuilderChainingMethods() {
        Course course = new Course.Builder("AI", 3)
            .withCategoryWeight(Category.HOMEWORK, 25.0)
            .withCategoryWeight(Category.QUIZZES, 25.0)
            .withCategoryWeight(Category.MIDTERM, 25.0)
            .withCategoryWeight(Category.FINAL_EXAM, 25.0)
            .build();
        
        assertNotNull(course);
        assertEquals("AI", course.getCourseName());
    }
    
    @Test
    void testBuilderValidatesWeights() {
        assertThrows(InvalidWeightException.class, () ->
            new Course.Builder("Test", 3)
                .withCategoryWeight(Category.HOMEWORK, 50.0)
                .withCategoryWeight(Category.QUIZZES, 50.0)
                .withCategoryWeight(Category.MIDTERM, 50.0)
                .withCategoryWeight(Category.FINAL_EXAM, 50.0)
                .build()
        );
    }
    
    @Test
    void testBuilderWithCategoryWeightsDefensiveCopy() {
        Map<Category, Double> customWeights = new EnumMap<>(Category.class);
        customWeights.put(Category.HOMEWORK, 25.0);
        customWeights.put(Category.QUIZZES, 25.0);
        customWeights.put(Category.MIDTERM, 25.0);
        customWeights.put(Category.FINAL_EXAM, 25.0);
        
        Course.Builder builder = new Course.Builder("Test", 3)
            .withCategoryWeights(customWeights);
        
        // Modify original map after passing to builder
        customWeights.put(Category.HOMEWORK, 99.0);
        
        // Builder should have defensive copy - original weights should be used
        Course course = builder.build();
        assertEquals(25.0, course.getCategoryWeight(Category.HOMEWORK), 0.01);
    }
    
    // ========== Edge Cases ==========
    
    @Test
    void testCourseNameWithSpecialCharacters() {
        Course course = new Course("CS 101: Introduction to Programming", 3);
        assertEquals("CS 101: Introduction to Programming", course.getCourseName());
    }
    
    @Test
    void testCourseNameWithUnicode() {
        Course course = new Course("Español: Programación Básica", 3);
        assertEquals("Español: Programación Básica", course.getCourseName());
    }
    
    @Test
    void testVeryLongCourseName() {
        String longName = "Advanced Topics in Computer Science: Machine Learning, Artificial Intelligence, " +
                         "and Deep Learning with Practical Applications";
        Course course = new Course(longName, 4);
        assertEquals(longName, course.getCourseName());
    }
    
    @Test
    void testAddManyAssignments() {
        Course course = new Course("Test", 3);
        
        for (int i = 1; i <= 100; i++) {
            course.addAssignment(new Assignment("HW" + i, 45.0, 50.0, Category.HOMEWORK));
        }
        
        assertEquals(100, course.getAssignmentCount());
        assertEquals(100, course.getAssignmentCountByCategory(Category.HOMEWORK));
    }
    
    @Test
    void testMultipleCoursesIndependent() {
        Course course1 = new Course("Course 1", 3);
        Course course2 = new Course("Course 2", 4);
        
        course1.addAssignment(new Assignment("HW1", 45.0, 50.0, Category.HOMEWORK));
        
        assertEquals(1, course1.getAssignmentCount());
        assertEquals(0, course2.getAssignmentCount());
    }
    
    @Test
    void testHashCodeConsistency() {
        Course course = new Course("Test", 3);
        int hash1 = course.hashCode();
        int hash2 = course.hashCode();
        assertEquals(hash1, hash2);
    }
    
    @Test
    void testUseInHashSet() {
        Set<Course> courses = new HashSet<>();
        Course course1 = new Course("Data Structures", 4);
        Course course2 = new Course("Data Structures", 4);
        Course course3 = new Course("Algorithms", 3);
        
        courses.add(course1);
        assertTrue(courses.contains(course2)); // Equal course
        assertEquals(1, courses.size());
        
        courses.add(course3);
        assertEquals(2, courses.size());
    }
    
    @Test
    void testCasePreservation() {
        Course course1 = new Course("Data Structures", 3);
        Course course2 = new Course("data structures", 3);
        
        assertNotEquals(course1, course2);
        assertEquals("Data Structures", course1.getCourseName());
        assertEquals("data structures", course2.getCourseName());
    }
}
