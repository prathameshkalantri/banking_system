package com.prathamesh.gradebook.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class InvalidCreditHoursExceptionTest {
    
    @Test
    void testExceptionWithDefaultRange() {
        int creditHours = 0;
        InvalidCreditHoursException exception = new InvalidCreditHoursException(creditHours);
        
        assertEquals(0, exception.getCreditHours());
        assertEquals(1, exception.getMinAllowed());
        assertEquals(6, exception.getMaxAllowed());
        assertTrue(exception.getMessage().contains("0"));
        assertTrue(exception.getMessage().contains("between 1 and 6"));
    }
    
    @Test
    void testExceptionWithCustomRange() {
        int creditHours = 10;
        int minAllowed = 1;
        int maxAllowed = 6;
        InvalidCreditHoursException exception = new InvalidCreditHoursException(creditHours, minAllowed, maxAllowed);
        
        assertEquals(10, exception.getCreditHours());
        assertEquals(1, exception.getMinAllowed());
        assertEquals(6, exception.getMaxAllowed());
        assertTrue(exception.getMessage().contains("10"));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {-5, -1, 0, 7, 10, 100})
    void testInvalidCreditHoursValues(int creditHours) {
        InvalidCreditHoursException exception = new InvalidCreditHoursException(creditHours);
        
        assertEquals(creditHours, exception.getCreditHours());
        assertTrue(exception.getMessage().contains(String.valueOf(creditHours)));
    }
    
    @Test
    void testNegativeCreditHours() {
        int creditHours = -3;
        InvalidCreditHoursException exception = new InvalidCreditHoursException(creditHours);
        
        assertEquals(-3, exception.getCreditHours());
        assertTrue(exception.getMessage().contains("-3"));
    }
    
    @Test
    void testCreditHoursTooHigh() {
        int creditHours = 15;
        InvalidCreditHoursException exception = new InvalidCreditHoursException(creditHours);
        
        assertEquals(15, exception.getCreditHours());
        assertTrue(exception.getMessage().contains("15"));
    }
    
    @Test
    void testInheritsFromGradebookException() {
        InvalidCreditHoursException exception = new InvalidCreditHoursException(0);
        assertTrue(exception instanceof GradebookException);
        assertNotNull(exception.getTimestamp());
    }
    
    @Test
    void testMessageContainsAllDetails() {
        int creditHours = 8;
        InvalidCreditHoursException exception = new InvalidCreditHoursException(creditHours);
        String message = exception.getMessage();
        
        assertTrue(message.contains("8"));
        assertTrue(message.contains("1"));
        assertTrue(message.contains("6"));
        assertTrue(message.toLowerCase().contains("invalid"));
    }
}
