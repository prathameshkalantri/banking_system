package com.prathamesh.gradebook.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidGradeExceptionTest {
    
    @Test
    void testExceptionWithMessage() {
        String message = "Points cannot be negative";
        InvalidGradeException exception = new InvalidGradeException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getPointsEarned());
        assertNull(exception.getPointsPossible());
    }
    
    @Test
    void testExceptionWithGradeDetails() {
        double pointsEarned = 95.0;
        double pointsPossible = 90.0;
        String reason = "Points earned cannot exceed points possible";
        InvalidGradeException exception = new InvalidGradeException(pointsEarned, pointsPossible, reason);
        
        assertEquals(95.0, exception.getPointsEarned(), 0.001);
        assertEquals(90.0, exception.getPointsPossible(), 0.001);
        assertTrue(exception.getMessage().contains("95.00"));
        assertTrue(exception.getMessage().contains("90.00"));
        assertTrue(exception.getMessage().contains(reason));
    }
    
    @Test
    void testNegativePointsEarned() {
        double pointsEarned = -10.0;
        double pointsPossible = 100.0;
        String reason = "Points earned must be non-negative";
        InvalidGradeException exception = new InvalidGradeException(pointsEarned, pointsPossible, reason);
        
        assertEquals(-10.0, exception.getPointsEarned(), 0.001);
        assertTrue(exception.getMessage().contains("-10.00"));
    }
    
    @Test
    void testNegativePointsPossible() {
        double pointsEarned = 50.0;
        double pointsPossible = -100.0;
        String reason = "Points possible must be positive";
        InvalidGradeException exception = new InvalidGradeException(pointsEarned, pointsPossible, reason);
        
        assertEquals(-100.0, exception.getPointsPossible(), 0.001);
        assertTrue(exception.getMessage().contains("-100.00"));
    }
    
    @Test
    void testZeroPointsPossible() {
        double pointsEarned = 0.0;
        double pointsPossible = 0.0;
        String reason = "Points possible must be greater than zero";
        InvalidGradeException exception = new InvalidGradeException(pointsEarned, pointsPossible, reason);
        
        assertEquals(0.0, exception.getPointsPossible(), 0.001);
        assertTrue(exception.getMessage().contains("0.00"));
    }
    
    @Test
    void testInheritsFromGradebookException() {
        InvalidGradeException exception = new InvalidGradeException("Test");
        assertTrue(exception instanceof GradebookException);
        assertNotNull(exception.getTimestamp());
    }
}
