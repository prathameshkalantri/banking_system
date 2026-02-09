package com.prathamesh.gradebook.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify Category enum functionality.
 */
class CategoryTest {
    
    @Test
    void testDefaultWeightValues() {
        assertEquals(20.0, Category.HOMEWORK.getDefaultWeight());
        assertEquals(20.0, Category.QUIZZES.getDefaultWeight());
        assertEquals(25.0, Category.MIDTERM.getDefaultWeight());
        assertEquals(35.0, Category.FINAL_EXAM.getDefaultWeight());
    }
    
    @Test
    void testDefaultWeightsSumTo100() {
        double sum = 0;
        for (Category category : Category.values()) {
            sum += category.getDefaultWeight();
        }
        assertEquals(100.0, sum, 0.01);
    }
    
    @Test
    void testGetDefaultWeightsMap() {
        var weights = Category.getDefaultWeights();
        assertNotNull(weights);
        assertEquals(4, weights.size());
        assertTrue(Category.validateWeights(weights));
    }
    
    @Test
    void testValidateWeights_Valid() {
        var weights = Category.getDefaultWeights();
        assertTrue(Category.validateWeights(weights));
    }
    
    @Test
    void testValidateWeights_Invalid() {
        var weights = Category.getDefaultWeights();
        weights.put(Category.HOMEWORK, 30.0); // Changes sum to 110
        assertFalse(Category.validateWeights(weights));
    }
    
    @Test
    void testValidateWeights_Null() {
        assertFalse(Category.validateWeights(null));
    }
}
