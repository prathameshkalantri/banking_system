package com.prathamesh.banking.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple audit logging utility for banking operations.
 * 
 * <p>Maintains an in-memory log of all significant banking events
 * for audit and debugging purposes.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public final class Logger {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private static final List<LogEntry> logs = new ArrayList<>();
    private static boolean enabled = true;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Logger() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Logs an informational message.
     * 
     * @param message the message to log
     */
    public static void info(String message) {
        log(LogLevel.INFO, message);
    }
    
    /**
     * Logs a warning message.
     * 
     * @param message the message to log
     */
    public static void warn(String message) {
        log(LogLevel.WARN, message);
    }
    
    /**
     * Logs an error message.
     * 
     * @param message the message to log
     */
    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }
    
    /**
     * Logs a debug message.
     * 
     * @param message the message to log
     */
    public static void debug(String message) {
        log(LogLevel.DEBUG, message);
    }
    
    /**
     * Internal logging method.
     * 
     * @param level log level
     * @param message message to log
     */
    private static synchronized void log(LogLevel level, String message) {
        if (!enabled) {
            return;
        }
        
        LogEntry entry = new LogEntry(LocalDateTime.now(), level, message);
        logs.add(entry);
        
        // Print to console if INFO or higher
        if (level != LogLevel.DEBUG) {
            System.out.println(entry.format());
        }
    }
    
    /**
     * Returns all log entries.
     * 
     * @return unmodifiable list of log entries
     */
    public static List<LogEntry> getLogs() {
        return Collections.unmodifiableList(new ArrayList<>(logs));
    }
    
    /**
     * Clears all log entries.
     */
    public static synchronized void clear() {
        logs.clear();
    }
    
    /**
     * Enables or disables logging.
     * 
     * @param enabled true to enable, false to disable
     */
    public static void setEnabled(boolean enabled) {
        Logger.enabled = enabled;
    }
    
    /**
     * Log severity levels.
     */
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
    
    /**
     * Represents a single log entry.
     */
    public static class LogEntry {
        private final LocalDateTime timestamp;
        private final LogLevel level;
        private final String message;
        
        public LogEntry(LocalDateTime timestamp, LogLevel level, String message) {
            this.timestamp = timestamp;
            this.level = level;
            this.message = message;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public LogLevel getLevel() {
            return level;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String format() {
            String color;
            switch (level) {
                case ERROR:
                    color = ConsoleColors.RED;
                    break;
                case WARN:
                    color = ConsoleColors.YELLOW;
                    break;
                case INFO:
                    color = ConsoleColors.CYAN;
                    break;
                case DEBUG:
                default:
                    color = ConsoleColors.WHITE;
                    break;
            }
            
            return String.format("%s[%s] [%s] %s%s",
                    color,
                    timestamp.format(TIMESTAMP_FORMAT),
                    level,
                    message,
                    ConsoleColors.RESET);
        }
    }
}
