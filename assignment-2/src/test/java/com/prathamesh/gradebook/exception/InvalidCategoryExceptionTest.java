package com.prathamesh.gradebook.exception;

import com.prathamesh.gradebook.domain.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidCategoryExceptionTest {
    
    @Test
    void testExceptionWithCategoryName() {
        String categoryName = "INVALID_CATEGORY";
        String courseName = "Data Structures";
        InvalidCategoryException exception = new InvalidCategoryException(categoryName, courseName);
        
        assertEquals(categoryName, exception.getCategoryName());
        assertEquals(courseName, exception.getCourseName());
        assertTrue(exception.getMessage().contains(categoryName));
        assertTrue(exception.getMessage().contains(courseName));
    }
    
    @Test
    void testExceptionWithCategoryEnum() {
        Category category = Category.HOMEWORK;
        String courseName = "Algorithms";
        String reason = "Course does not have homework assignments";
        InvalidCategoryException exception = new InvalidCategoryException(category, courseName, reason);
        
        assertEquals(category.name(), exception.getCategoryName());
        assertEquals(courseName, exception.getCourseName());
        assertTrue(exception.getMessage().contains(category.name()));
        assertTrue(exception.getMessage().contains(courseName));
        assertTrue(exception.getMessage().contains(reason));
    }
    
    @Test
    void testDifferentCategories() {
        String courseName = "Database Systems";
        
        InvalidCategoryException exception1 = new InvalidCategoryException(
            Category.HOMEWORK, courseName, "No weight configured");
        InvalidCategoryException exception2 = new InvalidCategoryException(
            Category.FINAL_EXAM, courseName, "No weight configured");
        
        assertEquals("HOMEWORK", exception1.getCategoryName());
        assertEquals("FINAL_EXAM", exception2.getCategoryName());
        assertNotEquals(exception1.getCategoryName(), exception2.getCategoryName());
    }
    
    @Test
    void testInheritsFromGradebookException() {
        InvalidCategoryException exception = new InvalidCategoryException("TEST", "CS101");
        assertTrue(exception instanceof GradebookException);
        assertNotNull(exception.getTimestamp());
    }
    
    @Test
    void testMessageWithAllCategories() {
        String courseName = "Operating Systems";
        
        for (Category category : Category.values()) {
            String reason = "Weight not configured for this category";
            InvalidCategoryException exception = new InvalidCategoryException(category, courseName, reason);
            
            assertTrue(exception.getMessage().contains(category.name()));
            assertTrue(exception.getMessage().contains(courseName));
            assertTrue(exception.getMessage().contains(reason));
        }
    }
    
    @Test
    void testMessageClarity() {
        Category category = Category.QUIZZES;
        String courseName = "Computer Networks";
        String reason = "This course uses projects instead of quizzes";
        InvalidCategoryException exception = new InvalidCategoryException(category, courseName, reason);
        String message = exception.getMessage();
        
        assertTrue(message.toLowerCase().contains("invalid"));
        assertTrue(message.contains(category.name()));
        assertTrue(message.contains(courseName));
        assertTrue(message.contains(reason));
    }
}
