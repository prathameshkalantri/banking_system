package com.prathamesh.gradebook.domain;

import com.prathamesh.gradebook.exception.InvalidGradeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentTest {
    
    // ========== Constructor and Basic Creation Tests ==========
    
    @Test
    void testCreateValidAssignment() {
        Assignment assignment = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        
        assertEquals("Homework 1", assignment.getName());
        assertEquals(45.0, assignment.getPointsEarned(), 0.001);
        assertEquals(50.0, assignment.getPointsPossible(), 0.001);
        assertEquals(Category.HOMEWORK, assignment.getCategory());
    }
    
    @Test
    void testCreateAssignmentWithPerfectScore() {
        Assignment assignment = new Assignment("Quiz 1", 100.0, 100.0, Category.QUIZZES);
        
        assertEquals(100.0, assignment.getPointsEarned(), 0.001);
        assertEquals(100.0, assignment.getPointsPossible(), 0.001);
        assertTrue(assignment.isPerfectScore());
    }
    
    @Test
    void testCreateAssignmentWithZeroScore() {
        Assignment assignment = new Assignment("Missed Quiz", 0.0, 50.0, Category.QUIZZES);
        
        assertEquals(0.0, assignment.getPointsEarned(), 0.001);
        assertEquals(50.0, assignment.getPointsPossible(), 0.001);
    }
    
    @Test
    void testAssignmentNameTrimmed() {
        Assignment assignment = new Assignment("  Quiz 1  ", 45.0, 50.0, Category.QUIZZES);
        
        assertEquals("Quiz 1", assignment.getName());
    }
    
    // ========== Validation Tests - Invalid Inputs ==========
    
    @Test
    void testNullNameThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> new Assignment(null, 45.0, 50.0, Category.HOMEWORK));
        
        assertTrue(exception.getMessage().contains("name"));
        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }
    
    @Test
    void testBlankNameThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> new Assignment("   ", 45.0, 50.0, Category.HOMEWORK));
        
        assertTrue(exception.getMessage().contains("name"));
        assertTrue(exception.getMessage().toLowerCase().contains("blank"));
    }
    
    @Test
    void testEmptyNameThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> new Assignment("", 45.0, 50.0, Category.HOMEWORK));
        
        assertTrue(exception.getMessage().contains("name"));
    }
    
    @Test
    void testNullCategoryThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> new Assignment("Homework 1", 45.0, 50.0, null));
        
        assertTrue(exception.getMessage().contains("Category"));
        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }
    
    @Test
    void testNegativePointsEarnedThrowsException() {
        InvalidGradeException exception = assertThrows(InvalidGradeException.class, 
            () -> new Assignment("Homework 1", -10.0, 50.0, Category.HOMEWORK));
        
        assertEquals(-10.0, exception.getPointsEarned(), 0.001);
        assertEquals(50.0, exception.getPointsPossible(), 0.001);
        assertTrue(exception.getMessage().toLowerCase().contains("negative"));
    }
    
    @Test
    void testZeroPointsPossibleThrowsException() {
        InvalidGradeException exception = assertThrows(InvalidGradeException.class, 
            () -> new Assignment("Homework 1", 0.0, 0.0, Category.HOMEWORK));
        
        assertTrue(exception.getMessage().toLowerCase().contains("greater than zero"));
    }
    
    @Test
    void testNegativePointsPossibleThrowsException() {
        InvalidGradeException exception = assertThrows(InvalidGradeException.class, 
            () -> new Assignment("Homework 1", 10.0, -50.0, Category.HOMEWORK));
        
        assertTrue(exception.getMessage().toLowerCase().contains("greater than zero"));
    }
    
    @Test
    void testPointsEarnedExceedsPointsPossibleThrowsException() {
        InvalidGradeException exception = assertThrows(InvalidGradeException.class, 
            () -> new Assignment("Homework 1", 60.0, 50.0, Category.HOMEWORK));
        
        assertEquals(60.0, exception.getPointsEarned(), 0.001);
        assertEquals(50.0, exception.getPointsPossible(), 0.001);
        assertTrue(exception.getMessage().toLowerCase().contains("exceed"));
    }
    
    // ========== Percentage Calculation Tests ==========
    
    @ParameterizedTest
    @CsvSource({
        "45.0, 50.0, 90.0",
        "50.0, 50.0, 100.0",
        "0.0, 50.0, 0.0",
        "25.0, 50.0, 50.0",
        "37.5, 50.0, 75.0",
        "80.0, 100.0, 80.0",
        "95.5, 100.0, 95.5",
        "1.0, 2.0, 50.0",
        "99.0, 100.0, 99.0"
    })
    void testGetPercentage(double earned, double possible, double expectedPercentage) {
        Assignment assignment = new Assignment("Test", earned, possible, Category.HOMEWORK);
        assertEquals(expectedPercentage, assignment.getPercentage(), 0.001);
    }
    
    @Test
    void testGetPercentageWithSmallNumbers() {
        Assignment assignment = new Assignment("Small Test", 0.5, 1.0, Category.QUIZZES);
        assertEquals(50.0, assignment.getPercentage(), 0.001);
    }
    
    @Test
    void testGetPercentageWithLargeNumbers() {
        Assignment assignment = new Assignment("Large Test", 950.0, 1000.0, Category.FINAL_EXAM);
        assertEquals(95.0, assignment.getPercentage(), 0.001);
    }
    
    // ========== Perfect Score Tests ==========
    
    @Test
    void testIsPerfectScoreTrue() {
        Assignment assignment = new Assignment("Perfect", 100.0, 100.0, Category.HOMEWORK);
        assertTrue(assignment.isPerfectScore());
    }
    
    @Test
    void testIsPerfectScoreFalse() {
        Assignment assignment = new Assignment("Almost", 99.0, 100.0, Category.HOMEWORK);
        assertFalse(assignment.isPerfectScore());
    }
    
    @Test
    void testIsPerfectScoreWithDifferentValues() {
        Assignment assignment = new Assignment("Perfect", 50.0, 50.0, Category.HOMEWORK);
        assertTrue(assignment.isPerfectScore());
    }
    
    @Test
    void testIsPerfectScoreWithVeryCloseValues() {
        // Within 0.01 tolerance should still be perfect
        Assignment assignment = new Assignment("Nearly Perfect", 49.995, 50.0, Category.HOMEWORK);
        assertTrue(assignment.isPerfectScore());
    }
    
    // ========== Formatted Grade Tests ==========
    
    @Test
    void testGetFormattedGrade() {
        Assignment assignment = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        String formatted = assignment.getFormattedGrade();
        
        assertEquals("45.00/50.00 (90.00%)", formatted);
    }
    
    @Test
    void testGetFormattedGradeWithPerfectScore() {
        Assignment assignment = new Assignment("Quiz 1", 100.0, 100.0, Category.QUIZZES);
        String formatted = assignment.getFormattedGrade();
        
        assertEquals("100.00/100.00 (100.00%)", formatted);
    }
    
    @Test
    void testGetFormattedGradeWithZeroScore() {
        Assignment assignment = new Assignment("Missed", 0.0, 50.0, Category.QUIZZES);
        String formatted = assignment.getFormattedGrade();
        
        assertEquals("0.00/50.00 (0.00%)", formatted);
    }
    
    @Test
    void testGetFormattedGradeWithDecimalPoints() {
        Assignment assignment = new Assignment("Test", 87.5, 100.0, Category.MIDTERM);
        String formatted = assignment.getFormattedGrade();
        
        assertEquals("87.50/100.00 (87.50%)", formatted);
    }
    
    // ========== Equals and HashCode Tests ==========
    
    @Test
    void testEqualsWithSameValues() {
        Assignment assignment1 = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        Assignment assignment2 = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        
        assertEquals(assignment1, assignment2);
        assertEquals(assignment1.hashCode(), assignment2.hashCode());
    }
    
    @Test
    void testEqualsWithSameObject() {
        Assignment assignment = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        
        assertEquals(assignment, assignment);
    }
    
    @Test
    void testNotEqualsWithDifferentName() {
        Assignment assignment1 = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        Assignment assignment2 = new Assignment("Homework 2", 45.0, 50.0, Category.HOMEWORK);
        
        assertNotEquals(assignment1, assignment2);
    }
    
    @Test
    void testNotEqualsWithDifferentPointsEarned() {
        Assignment assignment1 = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        Assignment assignment2 = new Assignment("Homework 1", 46.0, 50.0, Category.HOMEWORK);
        
        assertNotEquals(assignment1, assignment2);
    }
    
    @Test
    void testNotEqualsWithDifferentPointsPossible() {
        Assignment assignment1 = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        Assignment assignment2 = new Assignment("Homework 1", 45.0, 60.0, Category.HOMEWORK);
        
        assertNotEquals(assignment1, assignment2);
    }
    
    @Test
    void testNotEqualsWithDifferentCategory() {
        Assignment assignment1 = new Assignment("Assignment 1", 45.0, 50.0, Category.HOMEWORK);
        Assignment assignment2 = new Assignment("Assignment 1", 45.0, 50.0, Category.QUIZZES);
        
        assertNotEquals(assignment1, assignment2);
    }
    
    @Test
    void testNotEqualsWithNull() {
        Assignment assignment = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        
        assertNotEquals(assignment, null);
    }
    
    @Test
    void testNotEqualsWithDifferentClass() {
        Assignment assignment = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        String other = "Not an assignment";
        
        assertNotEquals(assignment, other);
    }
    
    // ========== ToString Tests ==========
    
    @Test
    void testToString() {
        Assignment assignment = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        String result = assignment.toString();
        
        assertTrue(result.contains("Homework 1"));
        assertTrue(result.contains("45.00/50.00"));
        assertTrue(result.contains("90.00%"));
        assertTrue(result.contains("HOMEWORK"));
    }
    
    @Test
    void testToStringWithDifferentValues() {
        Assignment assignment = new Assignment("Midterm", 85.0, 100.0, Category.MIDTERM);
        String result = assignment.toString();
        
        assertTrue(result.contains("Midterm"));
        assertTrue(result.contains("85.00/100.00"));
        assertTrue(result.contains("85.00%"));
        assertTrue(result.contains("MIDTERM"));
    }
    
    // ========== Immutability Tests ==========
    
    @Test
    void testImmutability() {
        Assignment assignment = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        
        // Verify all getters return the same values
        assertEquals("Homework 1", assignment.getName());
        assertEquals(45.0, assignment.getPointsEarned(), 0.001);
        assertEquals(50.0, assignment.getPointsPossible(), 0.001);
        assertEquals(Category.HOMEWORK, assignment.getCategory());
        
        // Call getters multiple times to ensure consistency
        assertEquals(assignment.getName(), assignment.getName());
        assertEquals(assignment.getPercentage(), assignment.getPercentage(), 0.001);
    }
    
    @Test
    void testFieldsAreFinal() throws NoSuchFieldException {
        // Verify fields are final using reflection
        assertTrue(java.lang.reflect.Modifier.isFinal(
            Assignment.class.getDeclaredField("name").getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isFinal(
            Assignment.class.getDeclaredField("pointsEarned").getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isFinal(
            Assignment.class.getDeclaredField("pointsPossible").getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isFinal(
            Assignment.class.getDeclaredField("category").getModifiers()));
    }
    
    @Test
    void testClassIsFinal() {
        assertTrue(java.lang.reflect.Modifier.isFinal(Assignment.class.getModifiers()));
    }
    
    // ========== Edge Cases ==========
    
    @Test
    void testVerySmallPointsValues() {
        Assignment assignment = new Assignment("Small", 0.01, 0.02, Category.HOMEWORK);
        assertEquals(50.0, assignment.getPercentage(), 0.001);
    }
    
    @Test
    void testVeryLargePointsValues() {
        Assignment assignment = new Assignment("Large", 9500.0, 10000.0, Category.FINAL_EXAM);
        assertEquals(95.0, assignment.getPercentage(), 0.001);
    }
    
    @Test
    void testAllCategories() {
        for (Category category : Category.values()) {
            Assignment assignment = new Assignment("Test", 45.0, 50.0, category);
            assertEquals(category, assignment.getCategory());
        }
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "Homework 1", 
        "Quiz #5", 
        "Midterm Exam", 
        "Final Project", 
        "Lab Assignment 3",
        "Extra Credit",
        "Make-up Quiz"
    })
    void testVariousAssignmentNames(String name) {
        Assignment assignment = new Assignment(name, 45.0, 50.0, Category.HOMEWORK);
        assertEquals(name, assignment.getName());
    }
    
    // ========== Additional Edge Cases ==========
    
    @Test
    void testFloatingPointPrecision() {
        Assignment assignment = new Assignment("Test", 1.0 / 3.0 * 50.0, 50.0, Category.HOMEWORK);
        double percentage = assignment.getPercentage();
        assertTrue(percentage >= 0.0 && percentage <= 100.0);
        assertEquals(33.333, percentage, 0.001);
    }
    
    @Test
    void testVerySmallDifferenceWithinTolerance() {
        Assignment assignment = new Assignment("Close", 49.9999, 50.0, Category.HOMEWORK);
        assertTrue(assignment.isPerfectScore()); // Within 0.01 tolerance
    }
    
    @Test
    void testExactlyAtPerfectScoreBoundary() {
        Assignment assignment = new Assignment("Boundary", 49.995, 50.0, Category.HOMEWORK);
        assertTrue(assignment.isPerfectScore()); // Within 0.01 tolerance
    }
    
    @Test
    void testJustBelowPerfectScoreBoundary() {
        Assignment assignment = new Assignment("Just Below", 49.989, 50.0, Category.HOMEWORK);
        assertFalse(assignment.isPerfectScore()); // Outside 0.01 tolerance
    }
    
    @Test
    void testNameWithUnicode() {
        Assignment assignment = new Assignment("Examen Final ✓", 95.0, 100.0, Category.FINAL_EXAM);
        assertEquals("Examen Final ✓", assignment.getName());
    }
    
    @Test
    void testVeryLongName() {
        String longName = "This is a very long assignment name that might be used for a complex project " +
                         "with multiple parts and detailed requirements";
        Assignment assignment = new Assignment(longName, 95.0, 100.0, Category.HOMEWORK);
        assertEquals(longName, assignment.getName());
    }
    
    @Test
    void testTabsAndNewlinesInName() {
        Assignment assignment = new Assignment("\tHomework 1\n", 45.0, 50.0, Category.HOMEWORK);
        assertEquals("Homework 1", assignment.getName());
    }
    
    @Test
    void testUseInHashSet() {
        Set<Assignment> set = new HashSet<>();
        Assignment a1 = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        Assignment a2 = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        Assignment a3 = new Assignment("Test", 46.0, 50.0, Category.HOMEWORK);
        
        set.add(a1);
        assertTrue(set.contains(a2)); // Should find equal object
        assertEquals(1, set.size());
        
        set.add(a3);
        assertEquals(2, set.size()); // Different assignment
    }
    
    @Test
    void testHashCodeConsistency() {
        Assignment assignment = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        int hash1 = assignment.hashCode();
        int hash2 = assignment.hashCode();
        assertEquals(hash1, hash2);
    }
    
    @Test
    void testEqualsReflexive() {
        Assignment assignment = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        assertEquals(assignment, assignment);
    }
    
    @Test
    void testEqualsSymmetric() {
        Assignment a1 = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        Assignment a2 = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        assertEquals(a1, a2);
        assertEquals(a2, a1);
    }
    
    @Test
    void testEqualsTransitive() {
        Assignment a1 = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        Assignment a2 = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        Assignment a3 = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        assertEquals(a1, a2);
        assertEquals(a2, a3);
        assertEquals(a1, a3);
    }
    
    @Test
    void testEqualsConsistent() {
        Assignment a1 = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        Assignment a2 = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        assertEquals(a1, a2);
        assertEquals(a1, a2); // Second call
    }
    
    @Test
    void testToStringNeverNull() {
        Assignment assignment = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        assertNotNull(assignment.toString());
        assertTrue(assignment.toString().length() > 0);
    }
    
    @Test
    void testGettersNeverReturnNull() {
        Assignment assignment = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        assertNotNull(assignment.getName());
        assertNotNull(assignment.getCategory());
        assertNotNull(assignment.getFormattedGrade());
    }
    
    @Test
    void testPercentageNeverNAN() {
        Assignment assignment = new Assignment("Test", 45.0, 50.0, Category.HOMEWORK);
        double percentage = assignment.getPercentage();
        assertFalse(Double.isNaN(percentage));
        assertFalse(Double.isInfinite(percentage));
    }
    
    @Test
    void testPercentageNeverNegative() {
        Assignment assignment = new Assignment("Test", 0.0, 50.0, Category.HOMEWORK);
        assertTrue(assignment.getPercentage() >= 0.0);
    }
    
    @Test
    void testPercentageNeverExceeds100() {
        Assignment assignment = new Assignment("Test", 50.0, 50.0, Category.HOMEWORK);
        assertTrue(assignment.getPercentage() <= 100.0);
    }
    
    @Test
    void testCasePreservation() {
        Assignment a1 = new Assignment("Homework 1", 45.0, 50.0, Category.HOMEWORK);
        Assignment a2 = new Assignment("homework 1", 45.0, 50.0, Category.HOMEWORK);
        assertNotEquals(a1, a2);
        assertEquals("Homework 1", a1.getName());
        assertEquals("homework 1", a2.getName());
    }
}
