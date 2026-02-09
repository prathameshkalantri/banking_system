package com.prathamesh.gradebook.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for ConsoleColors utility class.
 * 
 * Test Coverage:
 * - Color code constants
 * - Colorize methods
 * - Color stripping
 * - Utility class instantiation prevention
 * 
 * @author Prathamesh Kalantri
 */
class ConsoleColorsTest {
    
    @Test
    void testColorCodeConstants() {
        assertNotNull(ConsoleColors.RESET);
        assertNotNull(ConsoleColors.RED);
        assertNotNull(ConsoleColors.GREEN);
        assertNotNull(ConsoleColors.YELLOW);
        assertNotNull(ConsoleColors.BLUE);
        assertNotNull(ConsoleColors.ORANGE);
        
        // Verify they are ANSI escape sequences
        assertTrue(ConsoleColors.RESET.startsWith("\u001B["));
        assertTrue(ConsoleColors.RED.startsWith("\u001B["));
        assertTrue(ConsoleColors.GREEN.startsWith("\u001B["));
    }
    
    @Test
    void testColorizeWithValidInput() {
        String text = "Hello";
        String colored = ConsoleColors.colorize(text, ConsoleColors.RED);
        
        assertTrue(colored.startsWith(ConsoleColors.RED));
        assertTrue(colored.endsWith(ConsoleColors.RESET));
        assertTrue(colored.contains(text));
    }
    
    @Test
    void testColorizeWithNullText() {
        String colored = ConsoleColors.colorize(null, ConsoleColors.RED);
        assertEquals("", colored);
    }
    
    @Test
    void testColorizeWithNullColorCode() {
        String text = "Hello";
        String colored = ConsoleColors.colorize(text, null);
        assertEquals(text, colored);
    }
    
    @Test
    void testRedMethod() {
        String text = "Error";
        String colored = ConsoleColors.red(text);
        
        assertTrue(colored.contains(ConsoleColors.RED));
        assertTrue(colored.contains(text));
        assertTrue(colored.endsWith(ConsoleColors.RESET));
    }
    
    @Test
    void testGreenMethod() {
        String text = "Success";
        String colored = ConsoleColors.green(text);
        
        assertTrue(colored.contains(ConsoleColors.GREEN));
        assertTrue(colored.contains(text));
    }
    
    @Test
    void testYellowMethod() {
        String text = "Warning";
        String colored = ConsoleColors.yellow(text);
        
        assertTrue(colored.contains(ConsoleColors.YELLOW));
        assertTrue(colored.contains(text));
    }
    
    @Test
    void testBlueMethod() {
        String text = "Info";
        String colored = ConsoleColors.blue(text);
        
        assertTrue(colored.contains(ConsoleColors.BLUE));
        assertTrue(colored.contains(text));
    }
    
    @Test
    void testOrangeMethod() {
        String text = "Alert";
        String colored = ConsoleColors.orange(text);
        
        assertTrue(colored.contains(ConsoleColors.ORANGE));
        assertTrue(colored.contains(text));
    }
    
    @Test
    void testBrightGreenMethod() {
        String text = "Highlight";
        String colored = ConsoleColors.brightGreen(text);
        
        assertTrue(colored.contains(ConsoleColors.BRIGHT_GREEN));
        assertTrue(colored.contains(text));
    }
    
    @Test
    void testBoldMethod() {
        String text = "Important";
        String bolded = ConsoleColors.bold(text);
        
        assertTrue(bolded.contains(ConsoleColors.BOLD));
        assertTrue(bolded.contains(text));
        assertTrue(bolded.endsWith(ConsoleColors.RESET));
    }
    
    @Test
    void testBoldWithNullText() {
        String bolded = ConsoleColors.bold(null);
        assertEquals("", bolded);
    }
    
    @Test
    void testStripColors() {
        String colored = ConsoleColors.red("Hello") + " " + ConsoleColors.green("World");
        String plain = ConsoleColors.stripColors(colored);
        
        assertEquals("Hello World", plain);
        assertFalse(plain.contains("\u001B["));
    }
    
    @Test
    void testStripColorsWithPlainText() {
        String plain = "Plain text";
        String result = ConsoleColors.stripColors(plain);
        
        assertEquals(plain, result);
    }
    
    @Test
    void testStripColorsWithNullText() {
        String result = ConsoleColors.stripColors(null);
        assertEquals("", result);
    }
    
    @Test
    void testStripColorsWithComplexFormatting() {
        String complex = ConsoleColors.bold(ConsoleColors.red("Error")) + ": " + 
                        ConsoleColors.yellow("Warning");
        String plain = ConsoleColors.stripColors(complex);
        
        assertEquals("Error: Warning", plain);
    }
    
    @Test
    void testUtilityClassCannotBeInstantiated() {
        try {
            // Use reflection to try instantiating
            var constructor = ConsoleColors.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            // Reflection wraps the AssertionError in InvocationTargetException
            assertTrue(e.getCause() instanceof AssertionError);
            assertTrue(e.getCause().getMessage().contains("utility class"));
        }
    }
    
    @Test
    void testColorCodesAreDifferent() {
        // Verify different colors have different codes
        assertNotEquals(ConsoleColors.RED, ConsoleColors.GREEN);
        assertNotEquals(ConsoleColors.BLUE, ConsoleColors.YELLOW);
        assertNotEquals(ConsoleColors.GREEN, ConsoleColors.BRIGHT_GREEN);
    }
    
    @Test
    void testOrangeIsDefinedAsBrightYellow() {
        assertEquals(ConsoleColors.BRIGHT_YELLOW, ConsoleColors.ORANGE);
    }
    
    @Test
    void testColorizeEmptyString() {
        String colored = ConsoleColors.colorize("", ConsoleColors.RED);
        assertEquals(ConsoleColors.RED + ConsoleColors.RESET, colored);
    }
    
    @Test
    void testMultipleColorsInSequence() {
        String line = ConsoleColors.red("A") + ConsoleColors.green("B") + 
                     ConsoleColors.blue("C");
        String plain = ConsoleColors.stripColors(line);
        
        assertEquals("ABC", plain);
    }
}
