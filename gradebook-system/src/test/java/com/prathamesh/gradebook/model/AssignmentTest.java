package com.prathamesh.gradebook.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Assignment class.
 * Tests cover construction, validation, immutability, and calculation methods.
 */
@DisplayName("Assignment Model Tests")
class AssignmentTest {

    // ==================== Construction Tests ====================
    
    @Test
    @DisplayName("Should create valid assignment with all parameters")
    void testValidAssignmentCreation() {
        Assignment assignment = new Assignment(
            "Quiz 1",
            85.0,
            100.0,
            Category.QUIZZES
        );
        
        assertEquals("Quiz 1", assignment.getName());
        assertEquals(Category.QUIZZES, assignment.getCategory());
        assertEquals(85.0, assignment.getPointsEarned(), 0.001);
        assertEquals(100.0, assignment.getPointsPossible(), 0.001);
        assertEquals(85.0, assignment.getPercentage(), 0.001);
    }
    
    @Test
    @DisplayName("Should create perfect score assignment")
    void testPerfectScore() {
        Assignment assignment = new Assignment(
            "Perfect Quiz",
            100.0,
            100.0,
            Category.QUIZZES
        );
        
        assertEquals(100.0, assignment.getPercentage(), 0.001);
    }
    
    @Test
    @DisplayName("Should create zero score assignment")
    void testZeroScore() {
        Assignment assignment = new Assignment(
            "Failed Assignment",
            0.0,
            100.0,
            Category.HOMEWORK
        );
        
        assertEquals(0.0, assignment.getPercentage(), 0.001);
    }
    
    @Test
    @DisplayName("Should create assignment with decimal points")
    void testDecimalPoints() {
        Assignment assignment = new Assignment(
            "Essay",
            87.5,
            100.0,
            Category.HOMEWORK
        );
        
        assertEquals(87.5, assignment.getPercentage(), 0.001);
    }
    
    @Test
    @DisplayName("Should create assignment with non-standard total points")
    void testNonStandardTotalPoints() {
        Assignment assignment = new Assignment(
            "Project",
            45.0,
            50.0,
            Category.HOMEWORK
        );
        
        assertEquals(90.0, assignment.getPercentage(), 0.001);
    }

    // ==================== Validation Tests ====================
    
    @Test
    @DisplayName("Should throw exception when name is null")
    void testNullName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Assignment(null, 85.0, 100.0, Category.HOMEWORK);
        });
        
        assertTrue(exception.getMessage().contains("name"));
    }
    
    @Test
    @DisplayName("Should throw exception when name is empty")
    void testEmptyName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Assignment("", 85.0, 100.0, Category.HOMEWORK);
        });
        
        assertTrue(exception.getMessage().contains("name"));
    }
    
    @Test
    @DisplayName("Should throw exception when name is blank")
    void testBlankName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Assignment("   ", 85.0, 100.0, Category.HOMEWORK);
        });
        
        assertTrue(exception.getMessage().contains("name"));
    }
    
    @Test
    @DisplayName("Should throw exception when category is null")
    void testNullCategory() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Assignment("Quiz 1", 85.0, 100.0, null);
        });
        
        assertTrue(exception.getMessage().contains("Category") || 
                  exception.getMessage().contains("null"));
    }
    
    @Test
    @DisplayName("Should throw exception when points earned is negative")
    void testNegativePointsEarned() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Assignment("Quiz 1", -10.0, 100.0, Category.QUIZZES);
        });
        
        assertTrue(exception.getMessage().contains("negative"));
    }
    
    @Test
    @DisplayName("Should throw exception when points possible is zero")
    void testZeroPointsPossible() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Assignment("Quiz 1", 85.0, 0.0, Category.QUIZZES);
        });
        
        assertTrue(exception.getMessage().contains("positive"));
    }
    
    @Test
    @DisplayName("Should throw exception when points possible is negative")
    void testNegativePointsPossible() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Assignment("Quiz 1", 85.0, -100.0, Category.QUIZZES);
        });
        
        assertTrue(exception.getMessage().contains("positive"));
    }
    
    @Test
    @DisplayName("Should throw exception when points earned exceeds points possible")
    void testPointsEarnedExceedsPossible() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Assignment("Quiz 1", 110.0, 100.0, Category.QUIZZES);
        });
        
        assertTrue(exception.getMessage().contains("exceed"));
    }

    // ==================== Percentage Calculation Tests ====================
    
    @Test
    @DisplayName("Should calculate percentage correctly for typical score")
    void testPercentageCalculation() {
        Assignment assignment = new Assignment(
            "Test",
            42.0,
            50.0,
            Category.HOMEWORK
        );
        
        assertEquals(84.0, assignment.getPercentage(), 0.001);
    }
    
    @Test
    @DisplayName("Should calculate percentage correctly for decimal values")
    void testPercentageCalculationWithDecimals() {
        Assignment assignment = new Assignment(
            "Test",
            87.5,
            100.0,
            Category.HOMEWORK
        );
        
        assertEquals(87.5, assignment.getPercentage(), 0.001);
    }
    
    @Test
    @DisplayName("Should calculate percentage correctly for complex fraction")
    void testPercentageCalculationComplexFraction() {
        Assignment assignment = new Assignment(
            "Test",
            23.0,
            30.0,
            Category.HOMEWORK
        );
        
        assertEquals(76.66666666666667, assignment.getPercentage(), 0.001);
    }
    
    @Test
    @DisplayName("Should handle percentage calculation with very small numbers")
    void testPercentageWithSmallNumbers() {
        Assignment assignment = new Assignment(
            "Test",
            0.5,
            1.0,
            Category.HOMEWORK
        );
        
        assertEquals(50.0, assignment.getPercentage(), 0.001);
    }

    // ==================== Immutability Tests ====================
    
    @Test
    @DisplayName("Should be immutable - no setters exist")
    void testImmutability() {
        Assignment assignment = new Assignment(
            "Original",
            85.0,
            100.0,
            Category.HOMEWORK
        );
        
        // Verify getters return consistent values
        String name1 = assignment.getName();
        String name2 = assignment.getName();
        assertEquals(name1, name2);
        assertSame(name1, name2); // Should return same object reference
        
        Category category1 = assignment.getCategory();
        Category category2 = assignment.getCategory();
        assertSame(category1, category2);
    }

    // ==================== Category Tests ====================
    
    @Test
    @DisplayName("Should handle homework category")
    void testHomeworkCategory() {
        Assignment assignment = new Assignment(
            "Homework 1",
            90.0,
            100.0,
            Category.HOMEWORK
        );
        
        assertEquals(Category.HOMEWORK, assignment.getCategory());
    }
    
    @Test
    @DisplayName("Should handle quiz category")
    void testQuizCategory() {
        Assignment assignment = new Assignment(
            "Quiz 1",
            85.0,
            100.0,
            Category.QUIZZES
        );
        
        assertEquals(Category.QUIZZES, assignment.getCategory());
    }
    
    @Test
    @DisplayName("Should handle midterm category")
    void testMidtermCategory() {
        Assignment assignment = new Assignment(
            "Midterm",
            88.0,
            100.0,
            Category.MIDTERM
        );
        
        assertEquals(Category.MIDTERM, assignment.getCategory());
    }
    
    @Test
    @DisplayName("Should handle final exam category")
    void testFinalCategory() {
        Assignment assignment = new Assignment(
            "Final",
            92.0,
            100.0,
            Category.FINAL_EXAM
        );
        
        assertEquals(Category.FINAL_EXAM, assignment.getCategory());
    }

    // ==================== Edge Case Tests ====================
    
    @Test
    @DisplayName("Should handle very large point values")
    void testLargePointValues() {
        Assignment assignment = new Assignment(
            "Big Project",
            9500.0,
            10000.0,
            Category.HOMEWORK
        );
        
        assertEquals(95.0, assignment.getPercentage(), 0.001);
    }
    
    @Test
    @DisplayName("Should handle fractional points with high precision")
    void testHighPrecisionPoints() {
        Assignment assignment = new Assignment(
            "Precise Test",
            87.333333,
            100.0,
            Category.QUIZZES
        );
        
        assertEquals(87.333333, assignment.getPercentage(), 0.000001);
    }
    
    @Test
    @DisplayName("Should handle assignment with very long name")
    void testLongName() {
        String longName = "This is a very long assignment name that might appear in some courses " +
                         "where instructors provide detailed descriptions in the assignment title itself";
        
        Assignment assignment = new Assignment(
            longName,
            85.0,
            100.0,
            Category.HOMEWORK
        );
        
        assertEquals(longName, assignment.getName());
    }
    
    @Test
    @DisplayName("Should handle points earned equal to points possible")
    void testEqualPoints() {
        Assignment assignment = new Assignment(
            "Perfect Score",
            50.0,
            50.0,
            Category.HOMEWORK
        );
        
        assertEquals(100.0, assignment.getPercentage(), 0.001);
    }

    // ==================== toString Tests ====================
    
    @Test
    @DisplayName("Should have meaningful toString representation")
    void testToString() {
        Assignment assignment = new Assignment(
            "Quiz 1",
            85.0,
            100.0,
            Category.QUIZZES
        );
        
        String toString = assignment.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Quiz 1"));
        assertTrue(toString.contains("85"));
    }

    // ==================== Boundary Tests ====================
    
    @Test
    @DisplayName("Should accept minimum valid points (0/positive)")
    void testMinimumValidPoints() {
        Assignment assignment = new Assignment(
            "Zero Score",
            0.0,
            0.01,
            Category.HOMEWORK
        );
        
        assertEquals(0.0, assignment.getPercentage(), 0.001);
    }
    
    @Test
    @DisplayName("Should handle boundary percentage values")
    void testBoundaryPercentages() {
        // Test 0%
        Assignment zero = new Assignment("Zero", 0.0, 100.0, Category.HOMEWORK);
        assertEquals(0.0, zero.getPercentage(), 0.001);
        
        // Test 100%
        Assignment hundred = new Assignment("Hundred", 100.0, 100.0, Category.HOMEWORK);
        assertEquals(100.0, hundred.getPercentage(), 0.001);
        
        // Test very close to 100%
        Assignment almostHundred = new Assignment("Almost", 99.99, 100.0, Category.HOMEWORK);
        assertEquals(99.99, almostHundred.getPercentage(), 0.001);
    }
}
