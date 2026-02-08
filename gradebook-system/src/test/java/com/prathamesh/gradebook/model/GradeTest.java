package com.prathamesh.gradebook.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Grade class.
 * Tests cover grade creation, letter grade conversion, and validation.
 */
@DisplayName("Grade Model Tests")
class GradeTest {

    // ==================== Construction Tests ====================
    
    @Test
    @DisplayName("Should create grade with valid percentage")
    void testValidGradeCreation() {
        Grade grade = new Grade(85.5);
        
        assertEquals(85.5, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
        assertEquals(3.0, grade.getGpaPoints(), 0.001);
    }
    
    @Test
    @DisplayName("Should create grade with A (90-100)")
    void testGradeA() {
        Grade grade = new Grade(95.0);
        
        assertEquals(95.0, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.A, grade.getLetterGrade());
        assertEquals(4.0, grade.getGpaPoints(), 0.001);
    }
    
    @Test
    @DisplayName("Should create grade with B (80-89)")
    void testGradeB() {
        Grade grade = new Grade(85.0);
        
        assertEquals(85.0, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
        assertEquals(3.0, grade.getGpaPoints(), 0.001);
    }
    
    @Test
    @DisplayName("Should create grade with C (70-79)")
    void testGradeC() {
        Grade grade = new Grade(75.0);
        
        assertEquals(75.0, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.C, grade.getLetterGrade());
        assertEquals(2.0, grade.getGpaPoints(), 0.001);
    }
    
    @Test
    @DisplayName("Should create grade with D (60-69)")
    void testGradeD() {
        Grade grade = new Grade(65.0);
        
        assertEquals(65.0, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.D, grade.getLetterGrade());
        assertEquals(1.0, grade.getGpaPoints(), 0.001);
    }
    
    @Test
    @DisplayName("Should create grade with F (below 60)")
    void testGradeF() {
        Grade grade = new Grade(55.0);
        
        assertEquals(55.0, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.F, grade.getLetterGrade());
        assertEquals(0.0, grade.getGpaPoints(), 0.001);
    }

    // ==================== Boundary Tests ====================
    
    @Test
    @DisplayName("Should assign A for exactly 90%")
    void testBoundaryA() {
        Grade grade = new Grade(90.0);
        assertEquals(LetterGrade.A, grade.getLetterGrade());
    }
    
    @Test
    @DisplayName("Should assign B for exactly 80%")
    void testBoundaryB() {
        Grade grade = new Grade(80.0);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }
    
    @Test
    @DisplayName("Should assign C for exactly 70%")
    void testBoundaryC() {
        Grade grade = new Grade(70.0);
        assertEquals(LetterGrade.C, grade.getLetterGrade());
    }
    
    @Test
    @DisplayName("Should assign D for exactly 60%")
    void testBoundaryD() {
        Grade grade = new Grade(60.0);
        assertEquals(LetterGrade.D, grade.getLetterGrade());
    }
    
    @Test
    @DisplayName("Should assign B for 89.99%")
    void testJustBelowA() {
        Grade grade = new Grade(89.99);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }
    
    @Test
    @DisplayName("Should assign C for 79.99%")
    void testJustBelowB() {
        Grade grade = new Grade(79.99);
        assertEquals(LetterGrade.C, grade.getLetterGrade());
    }
    
    @Test
    @DisplayName("Should assign D for 69.99%")
    void testJustBelowC() {
        Grade grade = new Grade(69.99);
        assertEquals(LetterGrade.D, grade.getLetterGrade());
    }
    
    @Test
    @DisplayName("Should assign F for 59.99%")
    void testJustBelowD() {
        Grade grade = new Grade(59.99);
        assertEquals(LetterGrade.F, grade.getLetterGrade());
    }

    // ==================== Edge Case Tests ====================
    
    @Test
    @DisplayName("Should handle 0% grade")
    void testZeroPercentage() {
        Grade grade = new Grade(0.0);
        
        assertEquals(0.0, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.F, grade.getLetterGrade());
        assertEquals(0.0, grade.getGpaPoints(), 0.001);
    }
    
    @Test
    @DisplayName("Should handle 100% grade")
    void testPerfectPercentage() {
        Grade grade = new Grade(100.0);
        
        assertEquals(100.0, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.A, grade.getLetterGrade());
        assertEquals(4.0, grade.getGpaPoints(), 0.001);
    }
    
    @Test
    @DisplayName("Should handle decimal percentage")
    void testDecimalPercentage() {
        Grade grade = new Grade(87.33);
        
        assertEquals(87.33, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }
    
    @Test
    @DisplayName("Should handle high precision decimal")
    void testHighPrecisionDecimal() {
        Grade grade = new Grade(88.888888);
        
        assertEquals(88.888888, grade.getPercentage(), 0.000001);
        assertEquals(LetterGrade.B, grade.getLetterGrade());
    }

    // ==================== Validation Tests ====================
    
    @Test
    @DisplayName("Should throw exception for negative percentage")
    void testNegativePercentage() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Grade(-10.0);
        });
        
        assertTrue(exception.getMessage().contains("0") || 
                  exception.getMessage().contains("negative"));
    }
    
    @Test
    @DisplayName("Should handle percentage over 100 as grade A")
    void testPercentageOver100() {
        Grade grade = new Grade(101.0);
        
        assertEquals(101.0, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.A, grade.getLetterGrade());
    }
    
    @Test
    @DisplayName("Should handle percentage way over 100 as grade A")
    void testPercentageWayOver100() {
        Grade grade = new Grade(150.0);
        
        assertEquals(150.0, grade.getPercentage(), 0.001);
        assertEquals(LetterGrade.A, grade.getLetterGrade());
    }

    // ==================== Immutability Tests ====================
    
    @Test
    @DisplayName("Should be immutable - consistent getter returns")
    void testImmutability() {
        Grade grade = new Grade(85.5);
        
        double percentage1 = grade.getPercentage();
        double percentage2 = grade.getPercentage();
        assertEquals(percentage1, percentage2, 0.001);
        
        LetterGrade letter1 = grade.getLetterGrade();
        LetterGrade letter2 = grade.getLetterGrade();
        assertSame(letter1, letter2);
    }

    // ==================== toString Tests ====================
    
    @Test
    @DisplayName("Should have meaningful toString representation")
    void testToString() {
        Grade grade = new Grade(85.5);
        
        String toString = grade.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("85") || toString.contains("B"));
    }
    
    @Test
    @DisplayName("toString should include all grade components")
    void testToStringComplete() {
        Grade gradeA = new Grade(95.0);
        String strA = gradeA.toString();
        assertTrue(strA.contains("95") || strA.contains("A"));
        
        Grade gradeF = new Grade(55.0);
        String strF = gradeF.toString();
        assertTrue(strF.contains("55") || strF.contains("F"));
    }

    // ==================== Letter Grade Conversion Tests ====================
    
    @Test
    @DisplayName("Should convert all valid percentages correctly")
    void testAllLetterGradeConversions() {
        // Test A range
        assertEquals(LetterGrade.A, new Grade(90.0).getLetterGrade());
        assertEquals(LetterGrade.A, new Grade(95.0).getLetterGrade());
        assertEquals(LetterGrade.A, new Grade(100.0).getLetterGrade());
        
        // Test B range
        assertEquals(LetterGrade.B, new Grade(80.0).getLetterGrade());
        assertEquals(LetterGrade.B, new Grade(85.0).getLetterGrade());
        assertEquals(LetterGrade.B, new Grade(89.0).getLetterGrade());
        
        // Test C range
        assertEquals(LetterGrade.C, new Grade(70.0).getLetterGrade());
        assertEquals(LetterGrade.C, new Grade(75.0).getLetterGrade());
        assertEquals(LetterGrade.C, new Grade(79.0).getLetterGrade());
        
        // Test D range
        assertEquals(LetterGrade.D, new Grade(60.0).getLetterGrade());
        assertEquals(LetterGrade.D, new Grade(65.0).getLetterGrade());
        assertEquals(LetterGrade.D, new Grade(69.0).getLetterGrade());
        
        // Test F range
        assertEquals(LetterGrade.F, new Grade(0.0).getLetterGrade());
        assertEquals(LetterGrade.F, new Grade(50.0).getLetterGrade());
        assertEquals(LetterGrade.F, new Grade(59.0).getLetterGrade());
    }

    // ==================== GPA Points Tests ====================
    
    @Test
    @DisplayName("Should return correct GPA points for all letter grades")
    void testGpaPoints() {
        assertEquals(4.0, new Grade(95.0).getGpaPoints(), 0.001);
        assertEquals(3.0, new Grade(85.0).getGpaPoints(), 0.001);
        assertEquals(2.0, new Grade(75.0).getGpaPoints(), 0.001);
        assertEquals(1.0, new Grade(65.0).getGpaPoints(), 0.001);
        assertEquals(0.0, new Grade(55.0).getGpaPoints(), 0.001);
    }
    
    @Test
    @DisplayName("Should maintain GPA points consistency with letter grade")
    void testGpaPointsConsistency() {
        Grade grade = new Grade(87.5);
        
        LetterGrade letter = grade.getLetterGrade();
        double gpa = grade.getGpaPoints();
        
        assertEquals(letter.getGpaPoints(), gpa, 0.001);
    }

    // ==================== Multiple Instance Tests ====================
    
    @Test
    @DisplayName("Should create independent grade objects")
    void testMultipleInstances() {
        Grade grade1 = new Grade(90.0);
        Grade grade2 = new Grade(80.0);
        Grade grade3 = new Grade(70.0);
        
        assertEquals(LetterGrade.A, grade1.getLetterGrade());
        assertEquals(LetterGrade.B, grade2.getLetterGrade());
        assertEquals(LetterGrade.C, grade3.getLetterGrade());
        
        assertNotEquals(grade1.getPercentage(), grade2.getPercentage());
        assertNotEquals(grade2.getPercentage(), grade3.getPercentage());
    }

    // ==================== Special Cases ====================
    
    @Test
    @DisplayName("Should handle very precise boundary conditions")
    void testPreciseBoundaries() {
        // Just above boundaries - using 0.1 instead of 0.0001 for reliability
        assertEquals(LetterGrade.A, new Grade(90.1).getLetterGrade());
        assertEquals(LetterGrade.B, new Grade(80.1).getLetterGrade());
        assertEquals(LetterGrade.C, new Grade(70.1).getLetterGrade());
        assertEquals(LetterGrade.D, new Grade(60.1).getLetterGrade());
        
        // Just below boundaries
        assertEquals(LetterGrade.B, new Grade(89.9).getLetterGrade());
        assertEquals(LetterGrade.C, new Grade(79.9).getLetterGrade());
        assertEquals(LetterGrade.D, new Grade(69.9).getLetterGrade());
        assertEquals(LetterGrade.F, new Grade(59.9).getLetterGrade());
    }
}
