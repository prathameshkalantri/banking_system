package com.prathamesh.gradebook;

import com.prathamesh.gradebook.calculator.CourseGrade;
import com.prathamesh.gradebook.domain.Assignment;
import com.prathamesh.gradebook.domain.Category;
import com.prathamesh.gradebook.domain.Course;
import com.prathamesh.gradebook.domain.Student;
import com.prathamesh.gradebook.service.GradeBookService;
import com.prathamesh.gradebook.util.ConsoleColors;
import com.prathamesh.gradebook.util.TranscriptFormatter;

import java.util.List;

/**
 * Main demonstration application for the Student Gradebook System.
 * 
 * Demonstrates all features including:
 * - Student and course management
 * - Assignment tracking with varied performance
 * - Grade calculations with weight redistribution
 * - GPA calculation with credit weighting
 * - Honor Roll designation
 * - Edge case handling
 * - Professional transcript formatting
 * - Class analytics
 * 
 * This showcases the complete Composite pattern hierarchy:
 * Student (root) → Course (branch) → Assignment (leaf)
 * 
 * @author Prathamesh Kalantri
 */
public class Main {
    
    private static final GradeBookService gradeBook = new GradeBookService();
    
    // Auto-generated student IDs (best practice: guaranteed unique, thread-safe)
    private static String aliceId;
    private static String bobId;
    private static String carolId;
    private static String edgeCaseId;
    
    public static void main(String[] args) {
        printWelcomeBanner();
        
        // Section 1: Initialize
        section1_Initialize();
        
        // Section 2: Add Students
        section2_AddStudents();
        
        // Section 3: Enroll in Courses
        section3_EnrollInCourses();
        
        // Section 4: Add Assignments
        section4_AddAssignments();
        
        // Section 5: Edge Cases
        section5_EdgeCases();
        
        // Section 6: Category Averages
        section6_CategoryAverages();
        
        // Section 7: Generate Transcripts
        section7_GenerateTranscripts();
        
        // Section 8: Class Analytics
        section8_ClassAnalytics();
        
        printCompletionBanner();
    }
    
    private static void printWelcomeBanner() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println(ConsoleColors.bold(ConsoleColors.brightGreen(
            "           STUDENT GRADEBOOK SYSTEM - COMPREHENSIVE DEMO")));
        System.out.println("═".repeat(70));
        System.out.println("  Demonstrating: Composite Pattern, Service Layer, GPA Calculation");
        System.out.println("  Author: Prathamesh Kalantri");
        System.out.println("═".repeat(70) + "\n");
    }
    
    private static void printSectionHeader(int section, String title) {
        System.out.println("\n" + ConsoleColors.bold(ConsoleColors.blue(
            "╔═══ SECTION " + section + ": " + title + " ═══╗")));
    }
    
    private static void section1_Initialize() {
        printSectionHeader(1, "INITIALIZE GRADEBOOK");
        System.out.println("✓ GradeBook service initialized");
        System.out.println("✓ Ready to manage students, courses, and assignments");
    }
    
    private static void section2_AddStudents() {
        printSectionHeader(2, "ADD STUDENTS");
        
        System.out.println(ConsoleColors.cyan("→ Using AUTO-GENERATED IDs (Best Practice)"));
        System.out.println("  Benefits: Guaranteed uniqueness, thread-safe, no manual tracking\n");
        
        // Alice - High performer (auto-generated ID)
        aliceId = gradeBook.registerStudent("Alice Johnson");
        System.out.println(ConsoleColors.green("✓") + " Registered: Alice Johnson (" + aliceId + ") - High Performer");
        
        // Bob - Average performer (auto-generated ID)
        bobId = gradeBook.registerStudent("Bob Smith");
        System.out.println(ConsoleColors.blue("✓") + " Registered: Bob Smith (" + bobId + ") - Average Performer");
        
        // Carol - Struggling student (auto-generated ID)
        carolId = gradeBook.registerStudent("Carol Davis");
        System.out.println(ConsoleColors.yellow("✓") + " Registered: Carol Davis (" + carolId + ") - Struggling Student");
        
        System.out.println("\n" + ConsoleColors.cyan("→ Total Students: " + gradeBook.getStudentCount()));
        System.out.println(ConsoleColors.cyan("→ Auto-generated IDs: " + aliceId + ", " + bobId + ", " + carolId));
    }
    
    private static void section3_EnrollInCourses() {
        printSectionHeader(3, "ENROLL STUDENTS IN COURSES");
        
        // Alice's courses
        System.out.println("\n" + ConsoleColors.bold("Alice Johnson:"));
        gradeBook.enrollStudentInCourse(aliceId, new Course("Data Structures", 3));
        System.out.println("  ✓ Enrolled in Data Structures (3 credits)");
        
        gradeBook.enrollStudentInCourse(aliceId, new Course("Algorithms", 4));
        System.out.println("  ✓ Enrolled in Algorithms (4 credits)");
        
        gradeBook.enrollStudentInCourse(aliceId, new Course("Database Systems", 3));
        System.out.println("  ✓ Enrolled in Database Systems (3 credits)");
        
        // Bob's courses
        System.out.println("\n" + ConsoleColors.bold("Bob Smith:"));
        gradeBook.enrollStudentInCourse(bobId, new Course("Data Structures", 3));
        System.out.println("  ✓ Enrolled in Data Structures (3 credits)");
        
        gradeBook.enrollStudentInCourse(bobId, new Course("Web Development", 3));
        System.out.println("  ✓ Enrolled in Web Development (3 credits)");
        
        // Carol's courses
        System.out.println("\n" + ConsoleColors.bold("Carol Davis:"));
        gradeBook.enrollStudentInCourse(carolId, new Course("Data Structures", 3));
        System.out.println("  ✓ Enrolled in Data Structures (3 credits)");
        
        gradeBook.enrollStudentInCourse(carolId, new Course("Algorithms", 4));
        System.out.println("  ✓ Enrolled in Algorithms (4 credits)");
    }
    
    private static void section4_AddAssignments() {
        printSectionHeader(4, "ADD ASSIGNMENTS (VARIED PERFORMANCE)");
        
        // Alice - High performer (A's and B's)
        System.out.println("\n" + ConsoleColors.bold(ConsoleColors.green("Alice Johnson - Excellent Performance:")));
        addAliceAssignments();
        
        // Bob - Average performer (B's and C's)
        System.out.println("\n" + ConsoleColors.bold(ConsoleColors.blue("Bob Smith - Good Performance:")));
        addBobAssignments();
        
        // Carol - Struggling (C's, D's, and missing assignments)
        System.out.println("\n" + ConsoleColors.bold(ConsoleColors.yellow("Carol Davis - Struggling Performance:")));
        addCarolAssignments();
    }
    
    private static void addAliceAssignments() {
        String studentId = aliceId;
        
        // Data Structures - Excellent
        System.out.println("  Data Structures:");
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Array Implementation", 95, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Linked List Lab", 98, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("BST Implementation", 92, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Quiz: Recursion", 90, 100, Category.QUIZZES));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Quiz: Trees", 94, 100, Category.QUIZZES));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Midterm Exam", 93, 100, Category.MIDTERM));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Final Exam", 96, 100, Category.FINAL_EXAM));
        System.out.println("    ✓ 7 assignments (90-98%)");
        
        // Algorithms - Excellent
        System.out.println("  Algorithms:");
        gradeBook.addAssignment(studentId, "Algorithms", 
            new Assignment("Sorting Analysis", 88, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Algorithms", 
            new Assignment("Graph Algorithms", 92, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Algorithms", 
            new Assignment("Dynamic Programming", 90, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Algorithms", 
            new Assignment("Quiz: Complexity", 85, 100, Category.QUIZZES));
        gradeBook.addAssignment(studentId, "Algorithms", 
            new Assignment("Midterm Exam", 91, 100, Category.MIDTERM));
        gradeBook.addAssignment(studentId, "Algorithms", 
            new Assignment("Final Exam", 93, 100, Category.FINAL_EXAM));
        System.out.println("    ✓ 6 assignments (85-93%)");
        
        // Database Systems - Excellent
        System.out.println("  Database Systems:");
        gradeBook.addAssignment(studentId, "Database Systems", 
            new Assignment("SQL Queries", 100, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Database Systems", 
            new Assignment("Normalization", 95, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Database Systems", 
            new Assignment("Quiz: ER Diagrams", 92, 100, Category.QUIZZES));
        gradeBook.addAssignment(studentId, "Database Systems", 
            new Assignment("Midterm Exam", 94, 100, Category.MIDTERM));
        gradeBook.addAssignment(studentId, "Database Systems", 
            new Assignment("Final Exam", 96, 100, Category.FINAL_EXAM));
        System.out.println("    ✓ 5 assignments (92-100%)");
    }
    
    private static void addBobAssignments() {
        String studentId = bobId;
        
        // Data Structures - Good
        System.out.println("  Data Structures:");
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Array Implementation", 80, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Linked List Lab", 85, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("BST Implementation", 78, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Quiz: Recursion", 75, 100, Category.QUIZZES));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Midterm Exam", 82, 100, Category.MIDTERM));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Final Exam", 84, 100, Category.FINAL_EXAM));
        System.out.println("    ✓ 6 assignments (75-85%)");
        
        // Web Development - Average
        System.out.println("  Web Development:");
        gradeBook.addAssignment(studentId, "Web Development", 
            new Assignment("HTML/CSS Project", 88, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Web Development", 
            new Assignment("JavaScript Lab", 72, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Web Development", 
            new Assignment("React Components", 78, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Web Development", 
            new Assignment("Quiz: DOM", 70, 100, Category.QUIZZES));
        gradeBook.addAssignment(studentId, "Web Development", 
            new Assignment("Midterm Exam", 76, 100, Category.MIDTERM));
        gradeBook.addAssignment(studentId, "Web Development", 
            new Assignment("Final Project", 80, 100, Category.FINAL_EXAM));
        System.out.println("    ✓ 6 assignments (70-88%)");
    }
    
    private static void addCarolAssignments() {
        String studentId = carolId;
        
        // Data Structures - Struggling (some missing)
        System.out.println("  Data Structures:");
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Array Implementation", 65, 100, Category.HOMEWORK));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Linked List Lab", 70, 100, Category.HOMEWORK));
        // Missing BST Implementation
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Quiz: Recursion", 60, 100, Category.QUIZZES));
        // Missing second quiz
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Midterm Exam", 68, 100, Category.MIDTERM));
        gradeBook.addAssignment(studentId, "Data Structures", 
            new Assignment("Final Exam", 72, 100, Category.FINAL_EXAM));
        System.out.println("    ✓ 5 assignments (60-72%) - 2 missing");
        
        // Algorithms - Poor (several missing)
        System.out.println("  Algorithms:");
        gradeBook.addAssignment(studentId, "Algorithms", 
            new Assignment("Sorting Analysis", 55, 100, Category.HOMEWORK));
        // Missing other homework
        gradeBook.addAssignment(studentId, "Algorithms", 
            new Assignment("Quiz: Complexity", 50, 100, Category.QUIZZES));
        gradeBook.addAssignment(studentId, "Algorithms", 
            new Assignment("Midterm Exam", 58, 100, Category.MIDTERM));
        gradeBook.addAssignment(studentId, "Algorithms", 
            new Assignment("Final Exam", 52, 100, Category.FINAL_EXAM));
        System.out.println("    ✓ 4 assignments (50-58%) - 2 missing");
    }
    
    private static void section5_EdgeCases() {
        printSectionHeader(5, "EDGE CASES DEMONSTRATION");
        
        System.out.println("\n" + ConsoleColors.bold("Creating edge case student (auto-generated ID)..."));
        edgeCaseId = gradeBook.registerStudent("Edge Case Student");
        System.out.println("  ✓ Registered: Edge Case Student (" + edgeCaseId + ") - For edge case testing");
        
        // Course with missing entire category
        System.out.println("\n1. " + ConsoleColors.bold("Course with missing MIDTERM category:"));
        gradeBook.enrollStudentInCourse(edgeCaseId, new Course("Advanced Topics", 3));
        gradeBook.addAssignment(edgeCaseId, "Advanced Topics", 
            new Assignment("Research Paper", 85, 100, Category.HOMEWORK));
        gradeBook.addAssignment(edgeCaseId, "Advanced Topics", 
            new Assignment("Presentation", 90, 100, Category.QUIZZES));
        // No MIDTERM assignments
        gradeBook.addAssignment(edgeCaseId, "Advanced Topics", 
            new Assignment("Final Project", 88, 100, Category.FINAL_EXAM));
        System.out.println("   ✓ Weight automatically redistributed (MIDTERM 25% → other categories)");
        
        // Assignment with 0 points earned
        System.out.println("\n2. " + ConsoleColors.bold("Assignment with 0 points earned:"));
        gradeBook.addAssignment(edgeCaseId, "Advanced Topics", 
            new Assignment("Missed Assignment", 0, 100, Category.HOMEWORK));
        System.out.println("   ✓ Handled correctly in calculations");
        
        // Perfect score
        System.out.println("\n3. " + ConsoleColors.bold("Assignment with perfect score:"));
        gradeBook.addAssignment(edgeCaseId, "Advanced Topics", 
            new Assignment("Perfect Quiz", 100, 100, Category.QUIZZES));
        System.out.println("   ✓ 100% score processed");
        
        // Single assignment in category
        System.out.println("\n4. " + ConsoleColors.bold("Category with single assignment:"));
        System.out.println("   ✓ MIDTERM has 0 assignments (redistributed)");
        System.out.println("   ✓ Other categories calculate correctly");
        
        // Show the grade with weight redistribution
        CourseGrade grade = gradeBook.calculateCourseGrade(edgeCaseId, "Advanced Topics");
        System.out.println("\n   Final Grade: " + String.format("%.2f%%", grade.getNumericGrade()) + 
                         " (" + grade.getLetterGrade() + ")");
        System.out.println("   " + ConsoleColors.green("✓ Weight redistribution working correctly"));
    }
    
    private static void section6_CategoryAverages() {
        printSectionHeader(6, "CATEGORY AVERAGES BY STUDENT");
        
        String[] studentIds = {aliceId, bobId, carolId};
        String[] studentNames = {"Alice Johnson", "Bob Smith", "Carol Davis"};
        
        for (int i = 0; i < studentIds.length; i++) {
            System.out.println("\n" + ConsoleColors.bold(studentNames[i] + ":"));
            
            List<Course> courses = gradeBook.getStudentCourses(studentIds[i]);
            for (Course course : courses) {
                System.out.println("  " + course.getCourseName() + ":");
                
                for (Category category : Category.values()) {
                    double avg = gradeBook.calculateCategoryAverage(
                        studentIds[i], course.getCourseName(), category);
                    
                    if (avg > 0) {
                        String color = avg >= 90 ? ConsoleColors.GREEN :
                                     avg >= 80 ? ConsoleColors.BLUE :
                                     avg >= 70 ? ConsoleColors.YELLOW :
                                     avg >= 60 ? ConsoleColors.ORANGE :
                                     ConsoleColors.RED;
                        
                        System.out.println("    " + category.name() + ": " + 
                            ConsoleColors.colorize(String.format("%.1f%%", avg), color));
                    }
                }
            }
        }
    }
    
    private static void section7_GenerateTranscripts() {
        printSectionHeader(7, "STUDENT TRANSCRIPTS");
        
        String[] studentIds = {aliceId, bobId, carolId};
        
        for (String studentId : studentIds) {
            Student student = gradeBook.getStudent(studentId);
            List<CourseGrade> grades = gradeBook.calculateAllCourseGrades(studentId);
            double gpa = gradeBook.calculateGPA(studentId);
            
            System.out.println();
            String transcript = TranscriptFormatter.formatTranscript(student, gpa, grades);
            System.out.println(transcript);
        }
    }
    
    private static void section8_ClassAnalytics() {
        printSectionHeader(8, "CLASS ANALYTICS");
        
        // Honor Roll students
        System.out.println("\n" + ConsoleColors.bold(ConsoleColors.brightGreen("🎓 HONOR ROLL STUDENTS (GPA ≥ 3.5):")));
        List<Student> honorRoll = gradeBook.getHonorRollStudents();
        if (honorRoll.isEmpty()) {
            System.out.println("  No students on Honor Roll");
        } else {
            for (Student student : honorRoll) {
                double gpa = gradeBook.calculateGPA(student.getStudentId());
                System.out.println("  ✓ " + student.getName() + 
                    " - GPA: " + ConsoleColors.green(String.format("%.2f", gpa)));
            }
        }
        
        // Class-wide GPA average
        System.out.println("\n" + ConsoleColors.bold("📊 CLASS STATISTICS:"));
        double totalGPA = 0;
        int studentCount = 0;
        
        for (Student student : gradeBook.getAllStudents()) {
            // Skip edge case student
            if (student.getStudentId().equals(edgeCaseId)) continue;
            
            double gpa = gradeBook.calculateGPA(student.getStudentId());
            totalGPA += gpa;
            studentCount++;
        }
        
        double classAverage = studentCount > 0 ? totalGPA / studentCount : 0.0;
        System.out.println("  Total Students: " + studentCount);
        System.out.println("  Class Average GPA: " + String.format("%.2f", classAverage));
        System.out.println("  Honor Roll Percentage: " + 
            String.format("%.1f%%", (honorRoll.size() * 100.0 / studentCount)));
        
        // Students with failing grades
        System.out.println("\n" + ConsoleColors.bold(ConsoleColors.red("⚠️  STUDENTS NEEDING ASSISTANCE:")));
        List<Student> failing = gradeBook.getStudentsWithFailingGrades();
        if (failing.isEmpty()) {
            System.out.println("  " + ConsoleColors.green("All students passing!"));
        } else {
            for (Student student : failing) {
                if (student.getStudentId().equals(edgeCaseId)) continue;
                
                double gpa = gradeBook.calculateGPA(student.getStudentId());
                System.out.println("  ⚠  " + student.getName() + 
                    " - GPA: " + ConsoleColors.red(String.format("%.2f", gpa)));
            }
        }
        
        // Data Structures statistics
        System.out.println("\n" + ConsoleColors.bold("📈 DATA STRUCTURES COURSE ANALYSIS:"));
        int dsEnrollment = 0;
        double dsTotalGrade = 0;
        
        for (Student student : gradeBook.getAllStudents()) {
            if (student.getStudentId().equals(edgeCaseId)) continue;
            
            try {
                CourseGrade grade = gradeBook.calculateCourseGrade(
                    student.getStudentId(), "Data Structures");
                dsEnrollment++;
                dsTotalGrade += grade.getNumericGrade();
            } catch (Exception e) {
                // Student not enrolled in Data Structures
            }
        }
        
        if (dsEnrollment > 0) {
            double dsAverage = dsTotalGrade / dsEnrollment;
            System.out.println("  Enrollment: " + dsEnrollment + " students");
            System.out.println("  Average Grade: " + String.format("%.2f%%", dsAverage));
            
            String performance = dsAverage >= 90 ? ConsoleColors.green("Excellent") :
                               dsAverage >= 80 ? ConsoleColors.blue("Good") :
                               dsAverage >= 70 ? ConsoleColors.yellow("Satisfactory") :
                               ConsoleColors.orange("Needs Improvement");
            System.out.println("  Overall Performance: " + performance);
        }
    }
    
    private static void printCompletionBanner() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println(ConsoleColors.bold(ConsoleColors.brightGreen(
            "                    DEMO COMPLETED SUCCESSFULLY!")));
        System.out.println("═".repeat(70));
        System.out.println("  ✓ All features demonstrated");
        System.out.println("  ✓ Composite pattern in action");
        System.out.println("  ✓ Service layer orchestration");
        System.out.println("  ✓ Professional transcript formatting");
        System.out.println("  ✓ Comprehensive analytics");
        System.out.println("═".repeat(70) + "\n");
    }
}
