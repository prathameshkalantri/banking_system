package com.prathamesh.gradebook.util;

/**
 * Utility class for ANSI console color codes.
 * 
 * Provides constants and helper methods for colorizing console output.
 * Uses ANSI escape sequences that work in most modern terminals.
 * 
 * Design Patterns:
 * - Utility Class: Private constructor, all static methods
 * 
 * OOP Principles:
 * - Single Responsibility: Only handles color formatting
 * - Encapsulation: Color codes centralized in one place
 * 
 * @author Prathamesh Kalantri
 */
public final class ConsoleColors {
    
    // Color codes
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    // Bright/bold variants
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_PURPLE = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    
    // Text styles
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";
    
    // Orange approximation (bright yellow works well)
    public static final String ORANGE = BRIGHT_YELLOW;
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private ConsoleColors() {
        throw new AssertionError("ConsoleColors is a utility class and should not be instantiated");
    }
    
    /**
     * Wraps text in the specified color.
     * 
     * @param text The text to colorize
     * @param colorCode The ANSI color code
     * @return Colorized text with reset code appended
     */
    public static String colorize(String text, String colorCode) {
        if (text == null) {
            return "";
        }
        if (colorCode == null) {
            return text;
        }
        return colorCode + text + RESET;
    }
    
    /**
     * Colors text in red.
     * 
     * @param text The text to colorize
     * @return Red-colored text
     */
    public static String red(String text) {
        return colorize(text, RED);
    }
    
    /**
     * Colors text in green.
     * 
     * @param text The text to colorize
     * @return Green-colored text
     */
    public static String green(String text) {
        return colorize(text, GREEN);
    }
    
    /**
     * Colors text in yellow.
     * 
     * @param text The text to colorize
     * @return Yellow-colored text
     */
    public static String yellow(String text) {
        return colorize(text, YELLOW);
    }
    
    /**
     * Colors text in blue.
     * 
     * @param text The text to colorize
     * @return Blue-colored text
     */
    public static String blue(String text) {
        return colorize(text, BLUE);
    }
    
    /**
     * Colors text in cyan.
     * 
     * @param text The text to colorize
     * @return Cyan-colored text
     */
    public static String cyan(String text) {
        return colorize(text, CYAN);
    }
    
    /**
     * Colors text in orange (bright yellow).
     * 
     * @param text The text to colorize
     * @return Orange-colored text
     */
    public static String orange(String text) {
        return colorize(text, ORANGE);
    }
    
    /**
     * Colors text in bright green (for emphasis).
     * 
     * @param text The text to colorize
     * @return Bright green-colored text
     */
    public static String brightGreen(String text) {
        return colorize(text, BRIGHT_GREEN);
    }
    
    /**
     * Makes text bold.
     * 
     * @param text The text to make bold
     * @return Bold text
     */
    public static String bold(String text) {
        if (text == null) {
            return "";
        }
        return BOLD + text + RESET;
    }
    
    /**
     * Removes all ANSI color codes from text.
     * Useful for testing or plain text output.
     * 
     * @param text The text with color codes
     * @return Plain text without color codes
     */
    public static String stripColors(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
