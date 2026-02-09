package com.prathamesh.gradebook.util;

import com.prathamesh.gradebook.calculator.CourseGrade;
import com.prathamesh.gradebook.domain.LetterGrade;
import com.prathamesh.gradebook.domain.Student;

import java.util.List;

/**
 * Utility class for formatting academic transcripts.
 * 
 * Formats student transcripts with:
 * - Professional box-drawing borders
 * - Color-coded grades (A=green, B=blue, C=yellow, D=orange, F=red)
 * - Honor Roll designation (GPA >= 3.5)
 * - Clear tabular layout
 * 
 * Design Patterns:
 * - Formatter/Presenter Pattern: Presents domain data in specific format
 * - Utility Class: Private constructor, all static methods
 * 
 * OOP Principles:
 * - Single Responsibility: Only handles transcript formatting
 * - Separation of Concerns: Formatting separated from business logic
 * - Tell Don't Ask: Uses domain object getters for data
 * 
 * @author Prathamesh Kalantri
 */
public final class TranscriptFormatter {
    
    private static final int WIDTH = 67;
    private static final double HONOR_ROLL_THRESHOLD = 3.5;
    
    // Box drawing characters
    private static final String TOP_LEFT = "╔";
    private static final String TOP_RIGHT = "╗";
    private static final String BOTTOM_LEFT = "╚";
    private static final String BOTTOM_RIGHT = "╝";
    private static final String HORIZONTAL = "═";
    private static final String VERTICAL = "║";
    private static final String LEFT_T = "╠";
    private static final String RIGHT_T = "╣";
    private static final String THIN_LINE = "─";
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private TranscriptFormatter() {
        throw new AssertionError("TranscriptFormatter is a utility class and should not be instantiated");
    }
    
    /**
     * Formats a complete academic transcript.
     * 
     * @param student The student
     * @param gpa The calculated GPA
     * @param courseGrades List of course grades
     * @return Formatted transcript as a multi-line string
     * @throws IllegalArgumentException if student or courseGrades is null
     */
    public static String formatTranscript(Student student, double gpa, List<CourseGrade> courseGrades) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (courseGrades == null) {
            throw new IllegalArgumentException("Course grades cannot be null");
        }
        
        StringBuilder transcript = new StringBuilder();
        
        transcript.append(formatHeader(student));
        transcript.append(formatCourseTable(courseGrades));
        transcript.append(formatGPASummary(gpa, calculateTotalCredits(courseGrades)));
        transcript.append(formatFooter());
        
        return transcript.toString();
    }
    
    /**
     * Formats the transcript header with student information.
     * 
     * @param student The student
     * @return Formatted header
     */
    private static String formatHeader(Student student) {
        StringBuilder header = new StringBuilder();
        
        // Top border
        header.append(TOP_LEFT);
        header.append(HORIZONTAL.repeat(WIDTH - 2));
        header.append(TOP_RIGHT);
        header.append("\n");
        
        // Title
        String title = "ACADEMIC TRANSCRIPT";
        int padding = (WIDTH - 2 - title.length()) / 2;
        header.append(VERTICAL);
        header.append(" ".repeat(padding));
        header.append(ConsoleColors.bold(title));
        header.append(" ".repeat(WIDTH - 2 - padding - title.length()));
        header.append(VERTICAL);
        header.append("\n");
        
        // Divider
        header.append(LEFT_T);
        header.append(HORIZONTAL.repeat(WIDTH - 2));
        header.append(RIGHT_T);
        header.append("\n");
        
        // Student info
        header.append(formatLine("  Student: " + student.getName()));
        header.append(formatLine("  Student ID: " + student.getStudentId()));
        
        // Divider before courses
        header.append(LEFT_T);
        header.append(HORIZONTAL.repeat(WIDTH - 2));
        header.append(RIGHT_T);
        header.append("\n");
        
        return header.toString();
    }
    
    /**
     * Formats the course table with grades.
     * 
     * @param courseGrades List of course grades
     * @return Formatted course table
     */
    private static String formatCourseTable(List<CourseGrade> courseGrades) {
        if (courseGrades.isEmpty()) {
            return formatLine("  No courses enrolled") + "\n";
        }
        
        StringBuilder table = new StringBuilder();
        
        // Column headers
        table.append(formatLine(String.format("  %-35s %7s  %5s  %10s", 
            "Course Name", "Credits", "Grade", "GPA Points")));
        
        // Header separator
        table.append(formatLine("  " + THIN_LINE.repeat(WIDTH - 4)));
        
        // Course rows
        for (CourseGrade grade : courseGrades) {
            String courseName = grade.getCourseName();
            int credits = grade.getCreditHours();
            LetterGrade letterGrade = grade.getLetterGrade();
            double gpaValue = grade.getGpaValue();
            
            // Truncate long course names
            if (courseName.length() > 35) {
                courseName = courseName.substring(0, 32) + "...";
            }
            
            // Color-code the grade
            String coloredGrade = colorizeGrade(letterGrade.name(), letterGrade);
            
            String row = String.format("  %-35s %7d  %5s  %10.1f", 
                courseName, credits, coloredGrade, gpaValue);
            
            table.append(formatLine(row));
        }
        
        // Bottom separator
        table.append(formatLine("  " + THIN_LINE.repeat(WIDTH - 4)));
        
        return table.toString();
    }
    
    /**
     * Formats the GPA summary section.
     * 
     * @param gpa The GPA
     * @param totalCredits Total credit hours
     * @return Formatted GPA summary
     */
    private static String formatGPASummary(double gpa, int totalCredits) {
        StringBuilder summary = new StringBuilder();
        
        // Divider
        summary.append(LEFT_T);
        summary.append(HORIZONTAL.repeat(WIDTH - 2));
        summary.append(RIGHT_T);
        summary.append("\n");
        
        // Total credits
        summary.append(formatLine("  Total Credits: " + totalCredits));
        
        // GPA with honor roll designation
        String gpaText = String.format("  Cumulative GPA: %.2f", gpa);
        
        if (gpa >= HONOR_ROLL_THRESHOLD) {
            gpaText += "    " + ConsoleColors.brightGreen("🎓 HONOR ROLL");
        }
        
        // Color-code GPA
        String coloredGPA;
        if (gpa >= HONOR_ROLL_THRESHOLD) {
            coloredGPA = gpaText.replace(String.format("%.2f", gpa), 
                ConsoleColors.green(String.format("%.2f", gpa)));
        } else if (gpa >= 3.0) {
            coloredGPA = gpaText.replace(String.format("%.2f", gpa), 
                ConsoleColors.blue(String.format("%.2f", gpa)));
        } else if (gpa >= 2.0) {
            coloredGPA = gpaText.replace(String.format("%.2f", gpa), 
                ConsoleColors.yellow(String.format("%.2f", gpa)));
        } else if (gpa >= 1.0) {
            coloredGPA = gpaText.replace(String.format("%.2f", gpa), 
                ConsoleColors.orange(String.format("%.2f", gpa)));
        } else {
            coloredGPA = gpaText.replace(String.format("%.2f", gpa), 
                ConsoleColors.red(String.format("%.2f", gpa)));
        }
        
        summary.append(formatLine(coloredGPA));
        
        return summary.toString();
    }
    
    /**
     * Formats the transcript footer (closing border).
     * 
     * @return Formatted footer
     */
    private static String formatFooter() {
        return BOTTOM_LEFT + HORIZONTAL.repeat(WIDTH - 2) + BOTTOM_RIGHT + "\n";
    }
    
    /**
     * Formats a single line with borders.
     * Handles ANSI color codes by stripping them for length calculation.
     * 
     * @param content The line content
     * @return Formatted line with borders
     */
    private static String formatLine(String content) {
        // Calculate visible length (without ANSI codes)
        String plainContent = ConsoleColors.stripColors(content);
        int visibleLength = plainContent.length();
        int padding = WIDTH - 2 - visibleLength;
        
        // Ensure non-negative padding
        if (padding < 0) {
            padding = 0;
        }
        
        return VERTICAL + content + " ".repeat(padding) + VERTICAL + "\n";
    }
    
    /**
     * Colorizes a grade based on letter grade.
     * 
     * @param gradeText The grade text (e.g., "A", "B")
     * @param letterGrade The letter grade enum
     * @return Colorized grade text
     */
    private static String colorizeGrade(String gradeText, LetterGrade letterGrade) {
        switch (letterGrade) {
            case A:
                return ConsoleColors.green(gradeText);
            case B:
                return ConsoleColors.blue(gradeText);
            case C:
                return ConsoleColors.yellow(gradeText);
            case D:
                return ConsoleColors.orange(gradeText);
            case F:
                return ConsoleColors.red(gradeText);
            default:
                return gradeText;
        }
    }
    
    /**
     * Calculates total credit hours from course grades.
     * 
     * @param courseGrades List of course grades
     * @return Total credit hours
     */
    private static int calculateTotalCredits(List<CourseGrade> courseGrades) {
        int total = 0;
        for (CourseGrade grade : courseGrades) {
            total += grade.getCreditHours();
        }
        return total;
    }
    
    /**
     * Formats a plain transcript without color codes.
     * Useful for file output or systems that don't support ANSI colors.
     * 
     * @param student The student
     * @param gpa The calculated GPA
     * @param courseGrades List of course grades
     * @return Plain formatted transcript
     */
    public static String formatPlainTranscript(Student student, double gpa, List<CourseGrade> courseGrades) {
        String coloredTranscript = formatTranscript(student, gpa, courseGrades);
        return ConsoleColors.stripColors(coloredTranscript);
    }
}
