package com.prathamesh.gradebook.service;

import com.prathamesh.gradebook.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Service class for generating formatted academic transcripts.
 * <p>
 * The TranscriptGenerator creates professional, human-readable reports
 * showing a student's complete academic record including courses, grades,
 * and cumulative GPA. Transcripts include proper formatting with borders,
 * alignment, and clear section organization.
 * </p>
 * 
 * <h3>Transcript Components:</h3>
 * <ul>
 *   <li>Student information header</li>
 *   <li>Course listing with grades and credit hours</li>
 *   <li>Category breakdowns for each course</li>
 *   <li>Credit hour totals</li>
 *   <li>Cumulative GPA</li>
 *   <li>Academic standing (honors, probation, etc.)</li>
 *   <li>Generation date</li>
 * </ul>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public class TranscriptGenerator {
    
    private final GPACalculator gpaCalculator;
    private final GradeCalculator gradeCalculator;
    
    private static final String BORDER_LINE = "================================================================================";
    private static final String SECTION_LINE = "--------------------------------------------------------------------------------";
    private static final int LINE_WIDTH = 80;
    
    /**
     * Constructs a new TranscriptGenerator.
     * <p>
     * Initializes internal calculators for grade and GPA computations.
     * </p>
     */
    public TranscriptGenerator() {
        this.gpaCalculator = new GPACalculator();
        this.gradeCalculator = new GradeCalculator();
    }
    
    /**
     * Generates a complete formatted transcript for a student.
     * <p>
     * The transcript includes all courses with detailed grade breakdowns,
     * cumulative statistics, and academic standing.
     * </p>
     * 
     * @param student The student to generate transcript for
     * @return A formatted transcript as a multi-line string
     * @throws IllegalArgumentException if student is null
     */
    public String generateTranscript(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        StringBuilder transcript = new StringBuilder();
        
        // Header
        appendHeader(transcript, student);
        
        // Student Information
        appendStudentInfo(transcript, student);
        
        // Course Listing
        appendCourseListing(transcript, student);
        
        // Summary Statistics
        appendSummary(transcript, student);
        
        // Academic Standing
        appendAcademicStanding(transcript, student);
        
        // Footer
        appendFooter(transcript);
        
        return transcript.toString();
    }
    
    /**
     * Appends the transcript header section.
     */
    private void appendHeader(StringBuilder sb, Student student) {
        sb.append(BORDER_LINE).append("\n");
        sb.append(centerText("OFFICIAL ACADEMIC TRANSCRIPT")).append("\n");
        sb.append(centerText("Student Gradebook System")).append("\n");
        sb.append(BORDER_LINE).append("\n\n");
    }
    
    /**
     * Appends student information section.
     */
    private void appendStudentInfo(StringBuilder sb, Student student) {
        sb.append("STUDENT INFORMATION\n");
        sb.append(SECTION_LINE).append("\n");
        sb.append(String.format("Student ID:    %s\n", student.getStudentId()));
        sb.append(String.format("Student Name:  %s\n", student.getName()));
        sb.append(String.format("Date Generated: %s\n\n", 
            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))));
    }
    
    /**
     * Appends course listing with detailed grade information.
     */
    private void appendCourseListing(StringBuilder sb, Student student) {
        sb.append("COURSE HISTORY\n");
        sb.append(BORDER_LINE).append("\n");
        
        int courseNum = 1;
        for (Course course : student.getCourses()) {
            appendCourseDetails(sb, course, courseNum++);
            sb.append("\n");
        }
    }
    
    /**
     * Appends detailed information for a single course.
     */
    private void appendCourseDetails(StringBuilder sb, Course course, int courseNum) {
        sb.append(String.format("Course %d: %s\n", courseNum, course.getCourseName()));
        sb.append(String.format("  Credit Hours: %d\n", course.getCreditHours()));
        sb.append(String.format("  Assignments:  %d total\n", course.getAssignmentCount()));
        
        // Category breakdown
        if (course.hasAssignments()) {
            sb.append("\n  Category Breakdown:\n");
            
            for (Category category : Category.values()) {
                if (course.hasAssignments(category)) {
                    double avg = gradeCalculator.calculateCategoryAverage(course, category);
                    int count = course.getAssignmentCount(category);
                    sb.append(String.format("    %-15s: %6.2f%% (%d assignment%s, weight: %.0f%%)\n",
                        category.getDisplayName(), avg, count, count == 1 ? "" : "s",
                        category.getWeightPercentage()));
                }
            }
            
            // Final grade
            Grade finalGrade = course.getFinalGrade();
            sb.append(SECTION_LINE).append("\n");
            sb.append(String.format("  Final Grade: %.2f%% (%s) - %s - %.1f GPA Points\n",
                finalGrade.getPercentage(),
                finalGrade.getLetterGrade().name(),
                finalGrade.getDescription(),
                finalGrade.getGpaPoints()));
        } else {
            sb.append("\n  ** No assignments graded yet **\n");
        }
    }
    
    /**
     * Appends summary statistics section.
     */
    private void appendSummary(StringBuilder sb, Student student) {
        sb.append(BORDER_LINE).append("\n");
        sb.append("ACADEMIC SUMMARY\n");
        sb.append(SECTION_LINE).append("\n");
        
        int totalCredits = student.getTotalCreditHours();
        int gradedCredits = student.getGradedCreditHours();
        double gpa = gpaCalculator.calculateGPA(student);
        double gradePoints = gpaCalculator.calculateTotalGradePoints(student);
        
        sb.append(String.format("Total Courses Enrolled:    %d\n", student.getCourseCount()));
        sb.append(String.format("Total Credit Hours:        %d\n", totalCredits));
        sb.append(String.format("Graded Credit Hours:       %d\n", gradedCredits));
        sb.append(String.format("Total Grade Points:        %.2f\n", gradePoints));
        sb.append(SECTION_LINE).append("\n");
        sb.append(String.format("Cumulative GPA:            %.2f / 4.00\n", gpa));
        sb.append(SECTION_LINE).append("\n\n");
    }
    
    /**
     * Appends academic standing section.
     */
    private void appendAcademicStanding(StringBuilder sb, Student student) {
        sb.append("ACADEMIC STANDING\n");
        sb.append(SECTION_LINE).append("\n");
        
        double gpa = gpaCalculator.calculateGPA(student);
        String classification = gpaCalculator.getGPAClassification(gpa);
        
        sb.append(String.format("Classification:    %s\n", classification));
        
        // Special recognitions
        if (student.isDeansListQualified()) {
            sb.append("* DEAN'S LIST - Excellent Academic Performance\n");
        }
        
        if (gpaCalculator.qualifiesForHonors(student)) {
            sb.append("* HONORS ELIGIBLE - Cumulative GPA >= 3.25\n");
        }
        
        if (student.isOnProbation()) {
            sb.append("! ACADEMIC PROBATION - GPA Below 2.0 - Academic Support Recommended\n");
        }
        
        if (gpa >= 3.0 && gpa < 3.25) {
            sb.append("* GOOD ACADEMIC STANDING\n");
        }
        
        sb.append("\n");
    }
    
    /**
     * Appends transcript footer.
     */
    private void appendFooter(StringBuilder sb) {
        sb.append(BORDER_LINE).append("\n");
        sb.append(centerText("*** END OF OFFICIAL TRANSCRIPT ***")).append("\n");
        sb.append(BORDER_LINE).append("\n");
    }
    
    /**
     * Centers text within the line width.
     */
    private String centerText(String text) {
        int padding = (LINE_WIDTH - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        sb.append(text);
        return sb.toString();
    }
    
    /**
     * Generates a simplified grade report for a student.
     * <p>
     * This is a condensed version of the transcript, suitable for quick reference.
     * </p>
     * 
     * @param student The student to generate report for
     * @return A formatted grade report
     */
    public String generateGradeReport(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        StringBuilder report = new StringBuilder();
        
        report.append(String.format("Grade Report for %s (%s)\n", 
            student.getName(), student.getStudentId()));
        report.append(SECTION_LINE).append("\n");
        
        Map<String, Grade> grades = student.getCourseGrades();
        
        if (grades.isEmpty()) {
            report.append("No graded courses.\n");
        } else {
            report.append(String.format("%-30s %10s %10s %10s\n", 
                "Course", "Percentage", "Letter", "GPA Pts"));
            report.append(SECTION_LINE).append("\n");
            
            for (Map.Entry<String, Grade> entry : grades.entrySet()) {
                String courseName = entry.getKey();
                Grade grade = entry.getValue();
                
                report.append(String.format("%-30s %9.2f%% %10s %10.1f\n",
                    courseName.length() > 30 ? courseName.substring(0, 27) + "..." : courseName,
                    grade.getPercentage(),
                    grade.getLetterGrade().name(),
                    grade.getGpaPoints()));
            }
            
            report.append(SECTION_LINE).append("\n");
            report.append(String.format("Cumulative GPA: %.2f\n", gpaCalculator.calculateGPA(student)));
        }
        
        return report.toString();
    }
    
    /**
     * Generates a course-specific grade breakdown.
     * 
     * @param course The course to generate breakdown for
     * @return A formatted breakdown string
     */
    public String generateCourseBreakdown(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        
        StringBuilder breakdown = new StringBuilder();
        
        breakdown.append(String.format("Course: %s\n", course.getCourseName()));
        breakdown.append(SECTION_LINE).append("\n");
        
        for (Category category : Category.values()) {
            if (course.hasAssignments(category)) {
                breakdown.append(String.format("\n%s (%d%%weight)\n", 
                    category.getDisplayName(), (int)category.getWeightPercentage()));
                
                for (Assignment assignment : course.getAssignmentsByCategory(category)) {
                    breakdown.append(String.format("  - %s: %.1f/%.1f (%.1f%%)\n",
                        assignment.getName(),
                        assignment.getPointsEarned(),
                        assignment.getPointsPossible(),
                        assignment.getPercentage()));
                }
                
                double avg = gradeCalculator.calculateCategoryAverage(course, category);
                breakdown.append(String.format("  Category Average: %.2f%%\n", avg));
            }
        }
        
        if (course.hasAssignments()) {
            Grade finalGrade = course.getFinalGrade();
            breakdown.append(SECTION_LINE).append("\n");
            breakdown.append(String.format("Final Grade: %.2f%% (%s)\n",
                finalGrade.getPercentage(), finalGrade.getLetterGrade().name()));
        }
        
        return breakdown.toString();
    }
}
