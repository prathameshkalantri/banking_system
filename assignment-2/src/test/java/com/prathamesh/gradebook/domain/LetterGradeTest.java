package com.prathamesh.gradebook.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify LetterGrade enum functionality.
 * Tests all boundary conditions and edge cases.
 */
class LetterGradeTest {
    
    @Test
    void testGpaValues() {
        assertEquals(4.0, LetterGrade.A.getGpaValue());
        assertEquals(3.0, LetterGrade.B.getGpaValue());
        assertEquals(2.0, LetterGrade.C.getGpaValue());
        assertEquals(1.0, LetterGrade.D.getGpaValue());
        assertEquals(0.0, LetterGrade.F.getGpaValue());
    }
    
    @Test
    void testMinThresholds() {
        assertEquals(90.0, LetterGrade.A.getMinThreshold());
        assertEquals(80.0, LetterGrade.B.getMinThreshold());
        assertEquals(70.0, LetterGrade.C.getMinThreshold());
        assertEquals(60.0, LetterGrade.D.getMinThreshold());
        assertEquals(0.0, LetterGrade.F.getMinThreshold());
    }
    
    // Test exact boundaries - CRITICAL for correctness
    @ParameterizedTest
    @CsvSource({
        "100.0, A",
        "90.0, A",   // Exactly 90 should be A
        "89.99, B",
        "89.9, B",
        "80.0, B",   // Exactly 80 should be B
        "79.99, C",
        "79.9, C",
        "70.0, C",   // Exactly 70 should be C
        "69.99, D",
        "69.9, D",
        "60.0, D",   // Exactly 60 should be D
        "59.99, F",
        "59.9, F",
        "50.0, F",
        "0.0, F"
    })
    void testFromPercentage_Boundaries(double percentage, String expectedGrade) {
        LetterGrade expected = LetterGrade.valueOf(expectedGrade);
        assertEquals(expected, LetterGrade.fromPercentage(percentage));
    }
    
    // Test middle values in each range
    @ParameterizedTest
    @CsvSource({
        "95.0, A",
        "92.5, A",
        "85.0, B",
        "82.5, B",
        "75.0, C",
        "72.5, C",
        "65.0, D",
        "62.5, D",
        "30.0, F",
        "10.0, F"
    })
    void testFromPercentage_MiddleValues(double percentage, String expectedGrade) {
        LetterGrade expected = LetterGrade.valueOf(expectedGrade);
        assertEquals(expected, LetterGrade.fromPercentage(percentage));
    }
    
    // Test invalid inputs
    @Test
    void testFromPercentage_NegativeThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> LetterGrade.fromPercentage(-1.0)
        );
        assertTrue(exception.getMessage().contains("between 0 and 100"));
    }
    
    @Test
    void testFromPercentage_Over100ThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> LetterGrade.fromPercentage(100.01)
        );
        assertTrue(exception.getMessage().contains("between 0 and 100"));
    }
    
    @Test
    void testGetDetailedDescription() {
        assertEquals("A (GPA: 4.0)", LetterGrade.A.getDetailedDescription());
        assertEquals("B (GPA: 3.0)", LetterGrade.B.getDetailedDescription());
        assertEquals("F (GPA: 0.0)", LetterGrade.F.getDetailedDescription());
    }
    
    @Test
    void testGetGradingScaleDescription() {
        String description = LetterGrade.getGradingScaleDescription();
        assertNotNull(description);
        assertTrue(description.contains("A: 90-100"));
        assertTrue(description.contains("B: 80-89"));
        assertTrue(description.contains("C: 70-79"));
        assertTrue(description.contains("D: 60-69"));
        assertTrue(description.contains("F: <60"));
    }
}
