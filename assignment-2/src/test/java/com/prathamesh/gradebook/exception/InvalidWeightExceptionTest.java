package com.prathamesh.gradebook.exception;

import com.prathamesh.gradebook.domain.Category;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InvalidWeightExceptionTest {
    
    @Test
    void testExceptionWithActualSum() {
        double actualSum = 95.5;
        InvalidWeightException exception = new InvalidWeightException(actualSum);
        
        assertEquals(actualSum, exception.getActualSum(), 0.001);
        assertEquals(100.0, exception.getExpectedSum(), 0.001);
        assertTrue(exception.getMessage().contains("95.50"));
        assertTrue(exception.getMessage().contains("100%"));
    }
    
    @Test
    void testExceptionWithWeightsMap() {
        Map<Category, Double> weights = new EnumMap<>(Category.class);
        weights.put(Category.HOMEWORK, 30.0);
        weights.put(Category.QUIZZES, 25.0);
        weights.put(Category.MIDTERM, 20.0);
        weights.put(Category.FINAL_EXAM, 30.0);
        double actualSum = 105.0;
        
        InvalidWeightException exception = new InvalidWeightException(weights, actualSum);
        
        assertEquals(actualSum, exception.getActualSum(), 0.001);
        assertEquals(100.0, exception.getExpectedSum(), 0.001);
        assertTrue(exception.getMessage().contains("105.00"));
        assertTrue(exception.getMessage().contains("HOMEWORK"));
        assertTrue(exception.getMessage().contains("30.0%"));
    }
    
    @Test
    void testExceptionWithWeightsTooLow() {
        Map<Category, Double> weights = new EnumMap<>(Category.class);
        weights.put(Category.HOMEWORK, 15.0);
        weights.put(Category.QUIZZES, 15.0);
        weights.put(Category.MIDTERM, 20.0);
        weights.put(Category.FINAL_EXAM, 30.0);
        double actualSum = 80.0;
        
        InvalidWeightException exception = new InvalidWeightException(weights, actualSum);
        
        assertEquals(80.0, exception.getActualSum(), 0.001);
        assertTrue(exception.getMessage().contains("80.00"));
    }
    
    @Test
    void testInheritsFromGradebookException() {
        InvalidWeightException exception = new InvalidWeightException(95.0);
        assertTrue(exception instanceof GradebookException);
        assertNotNull(exception.getTimestamp());
    }
    
    @Test
    void testFormatWeightsWithEmptyMap() {
        Map<Category, Double> weights = new EnumMap<>(Category.class);
        InvalidWeightException exception = new InvalidWeightException(weights, 0.0);
        
        assertTrue(exception.getMessage().contains("null or empty"));
    }
    
    @Test
    void testFormatWeightsWithAllCategories() {
        Map<Category, Double> weights = Category.getDefaultWeights();
        double actualSum = 100.0;
        InvalidWeightException exception = new InvalidWeightException(weights, actualSum);
        
        for (Category category : Category.values()) {
            assertTrue(exception.getMessage().contains(category.name()));
        }
    }
}
