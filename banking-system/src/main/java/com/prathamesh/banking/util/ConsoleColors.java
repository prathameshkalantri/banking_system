package com.prathamesh.banking.util;

/**
 * ANSI color codes for formatted console output.
 * 
 * <p>Provides constants for colored text, backgrounds, and text styles
 * to enhance terminal output readability.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public final class ConsoleColors {
    
    // Reset
    public static final String RESET = "\033[0m";
    
    // Text Colors
    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";
    
    // Bold Text Colors
    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String RED_BOLD = "\033[1;31m";
    public static final String GREEN_BOLD = "\033[1;32m";
    public static final String YELLOW_BOLD = "\033[1;33m";
    public static final String BLUE_BOLD = "\033[1;34m";
    public static final String PURPLE_BOLD = "\033[1;35m";
    public static final String CYAN_BOLD = "\033[1;36m";
    public static final String WHITE_BOLD = "\033[1;37m";
    
    // Background Colors
    public static final String BLACK_BACKGROUND = "\033[40m";
    public static final String RED_BACKGROUND = "\033[41m";
    public static final String GREEN_BACKGROUND = "\033[42m";
    public static final String YELLOW_BACKGROUND = "\033[43m";
    public static final String BLUE_BACKGROUND = "\033[44m";
    public static final String PURPLE_BACKGROUND = "\033[45m";
    public static final String CYAN_BACKGROUND = "\033[46m";
    public static final String WHITE_BACKGROUND = "\033[47m";
    
    // Text Styles
    public static final String BOLD = "\033[1m";
    public static final String UNDERLINE = "\033[4m";
    
    /**
     * Private constructor to prevent instantiation.
     */
    private ConsoleColors() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Formats a success message in green.
     * 
     * @param message the message to format
     * @return formatted message
     */
    public static String success(String message) {
        return GREEN + "✓ " + message + RESET;
    }
    
    /**
     * Formats an error message in red.
     * 
     * @param message the message to format
     * @return formatted message
     */
    public static String error(String message) {
        return RED + "✗ " + message + RESET;
    }
    
    /**
     * Formats a warning message in yellow.
     * 
     * @param message the message to format
     * @return formatted message
     */
    public static String warning(String message) {
        return YELLOW + "⚠ " + message + RESET;
    }
    
    /**
     * Formats an info message in cyan.
     * 
     * @param message the message to format
     * @return formatted message
     */
    public static String info(String message) {
        return CYAN + "ℹ " + message + RESET;
    }
    
    /**
     * Formats a header in bold blue.
     * 
     * @param message the message to format
     * @return formatted message
     */
    public static String header(String message) {
        return BLUE_BOLD + message + RESET;
    }
    
    /**
     * Formats an amount with appropriate color.
     * Green for positive, red for negative, white for zero.
     * 
     * @param amount the amount to format
     * @return formatted amount string
     */
    public static String amount(double amount) {
        if (amount > 0) {
            return GREEN + String.format("$%.2f", amount) + RESET;
        } else if (amount < 0) {
            return RED + String.format("$%.2f", Math.abs(amount)) + RESET;
        } else {
            return String.format("$%.2f", amount);
        }
    }
}
