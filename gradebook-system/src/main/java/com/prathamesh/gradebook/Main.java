package com.prathamesh.gradebook;

import com.prathamesh.gradebook.model.*;
import com.prathamesh.gradebook.service.GradeBook;

/**
 * Main demonstration class for the Student Gradebook System.
 * <p>
 * This class demonstrates all core functionality of the gradebook system including:
 * - Student enrollment
 * - Course registration
 * - Assignment tracking across multiple categories
 * - Grade calculation (category averages, weighted final grades)
 * - GPA computation (credit-hour weighted)
 * - Transcript generation
 * - Academic standing determination
 * </p>
 * 
 * <p>
 * The demo creates 3 students with varying performance levels:
 * - Student 1: High performer (A/B grades, Dean's List)
 * - Student 2: Average performer (B/C grades, Good Standing)
 * - Student 3: Struggling performer (C/D/F grades, At Risk)
 * </p>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 * @since 2026-02-08
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("           STUDENT GRADEBOOK SYSTEM - COMPREHENSIVE DEMONSTRATION");
        System.out.println("================================================================================\n");
        
        // Initialize the gradebook
        GradeBook gradeBook = new GradeBook();
        
        // Create and populate students with varied performance
        createStudent1_HighPerformer(gradeBook);
        createStudent2_AveragePerformer(gradeBook);
        createStudent3_AtRiskStudent(gradeBook);
        
        // Display comprehensive results
        displayAllTranscripts(gradeBook);
        displayGradeBookStatistics(gradeBook);
        displayDeansListAndProbation(gradeBook);
        
        System.out.println("\n================================================================================");
        System.out.println("                    DEMONSTRATION COMPLETE");
        System.out.println("================================================================================");
    }
    
    /**
     * Creates Student 1: High Performer (GPA ~3.6)
     * - Mathematics (3 credits): 92% (A)
     * - Computer Science (4 credits): 88% (B)
     * - English Literature (3 credits): 95% (A)
     */
    private static void createStudent1_HighPerformer(GradeBook gradeBook) {
        System.out.println("Creating Student 1: Sarah Johnson (High Performer)...");
        
        Student student1 = gradeBook.addStudent("S-10001001", "Sarah Johnson");
        
        // Course 1: Mathematics (3 credits)
        Course math = gradeBook.enrollInCourse("S-10001001", "Calculus I", 3);
        gradeBook.addAssignment("S-10001001", "Calculus I", 
            new Assignment("HW1: Limits", 95, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10001001", "Calculus I", 
            new Assignment("HW2: Derivatives", 88, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10001001", "Calculus I", 
            new Assignment("HW3: Integration", 92, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10001001", "Calculus I", 
            new Assignment("Quiz 1", 90, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10001001", "Calculus I", 
            new Assignment("Quiz 2", 95, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10001001", "Calculus I", 
            new Assignment("Midterm Exam", 89, 100, Category.MIDTERM));
        gradeBook.addAssignment("S-10001001", "Calculus I", 
            new Assignment("Final Exam", 94, 100, Category.FINAL_EXAM));
        
        // Course 2: Computer Science (4 credits)
        Course cs = gradeBook.enrollInCourse("S-10001001", "Data Structures", 4);
        gradeBook.addAssignment("S-10001001", "Data Structures", 
            new Assignment("Lab 1: Arrays", 100, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10001001", "Data Structures", 
            new Assignment("Lab 2: Linked Lists", 85, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10001001", "Data Structures", 
            new Assignment("Lab 3: Trees", 90, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10001001", "Data Structures", 
            new Assignment("Quiz 1", 82, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10001001", "Data Structures", 
            new Assignment("Quiz 2", 88, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10001001", "Data Structures", 
            new Assignment("Midterm Exam", 85, 100, Category.MIDTERM));
        gradeBook.addAssignment("S-10001001", "Data Structures", 
            new Assignment("Final Exam", 91, 100, Category.FINAL_EXAM));
        
        // Course 3: English Literature (3 credits)
        Course english = gradeBook.enrollInCourse("S-10001001", "English Literature", 3);
        gradeBook.addAssignment("S-10001001", "English Literature", 
            new Assignment("Essay 1", 92, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10001001", "English Literature", 
            new Assignment("Essay 2", 95, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10001001", "English Literature", 
            new Assignment("Reading Quiz 1", 100, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10001001", "English Literature", 
            new Assignment("Reading Quiz 2", 98, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10001001", "English Literature", 
            new Assignment("Midterm Essay", 94, 100, Category.MIDTERM));
        gradeBook.addAssignment("S-10001001", "English Literature", 
            new Assignment("Final Essay", 96, 100, Category.FINAL_EXAM));
        
        System.out.println("  ✓ Enrolled in 3 courses (10 credits total)");
        System.out.println("  ✓ Completed 20 assignments\n");
    }
    
    /**
     * Creates Student 2: Average Performer (GPA ~2.7)
     * - History (3 credits): 82% (B)
     * - Biology (4 credits): 75% (C)
     */
    private static void createStudent2_AveragePerformer(GradeBook gradeBook) {
        System.out.println("Creating Student 2: Michael Chen (Average Performer)...");
        
        Student student2 = gradeBook.addStudent("S-10002002", "Michael Chen");
        
        // Course 1: History (3 credits)
        Course history = gradeBook.enrollInCourse("S-10002002", "World History", 3);
        gradeBook.addAssignment("S-10002002", "World History", 
            new Assignment("Reading HW 1", 78, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10002002", "World History", 
            new Assignment("Reading HW 2", 85, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10002002", "World History", 
            new Assignment("Reading HW 3", 80, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10002002", "World History", 
            new Assignment("Quiz 1", 82, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10002002", "World History", 
            new Assignment("Quiz 2", 88, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10002002", "World History", 
            new Assignment("Midterm Exam", 80, 100, Category.MIDTERM));
        gradeBook.addAssignment("S-10002002", "World History", 
            new Assignment("Final Exam", 84, 100, Category.FINAL_EXAM));
        
        // Course 2: Biology (4 credits)
        Course biology = gradeBook.enrollInCourse("S-10002002", "General Biology", 4);
        gradeBook.addAssignment("S-10002002", "General Biology", 
            new Assignment("Lab Report 1", 70, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10002002", "General Biology", 
            new Assignment("Lab Report 2", 75, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10002002", "General Biology", 
            new Assignment("Lab Report 3", 72, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10002002", "General Biology", 
            new Assignment("Quiz 1", 78, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10002002", "General Biology", 
            new Assignment("Quiz 2", 73, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10002002", "General Biology", 
            new Assignment("Midterm Exam", 75, 100, Category.MIDTERM));
        gradeBook.addAssignment("S-10002002", "General Biology", 
            new Assignment("Final Exam", 76, 100, Category.FINAL_EXAM));
        
        System.out.println("  ✓ Enrolled in 2 courses (7 credits total)");
        System.out.println("  ✓ Completed 14 assignments\n");
    }
    
    /**
     * Creates Student 3: At-Risk Student (GPA ~1.5)
     * - Algebra (3 credits): 68% (D)
     * - Chemistry (4 credits): 58% (F)
     * - Physics (3 credits): 72% (C)
     */
    private static void createStudent3_AtRiskStudent(GradeBook gradeBook) {
        System.out.println("Creating Student 3: Alex Rivera (At-Risk Student)...");
        
        Student student3 = gradeBook.addStudent("S-10003003", "Alex Rivera");
        
        // Course 1: Algebra (3 credits) - Barely passing
        Course algebra = gradeBook.enrollInCourse("S-10003003", "College Algebra", 3);
        gradeBook.addAssignment("S-10003003", "College Algebra", 
            new Assignment("HW1", 60, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10003003", "College Algebra", 
            new Assignment("HW2", 65, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10003003", "College Algebra", 
            new Assignment("Quiz 1", 70, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10003003", "College Algebra", 
            new Assignment("Quiz 2", 68, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10003003", "College Algebra", 
            new Assignment("Midterm Exam", 72, 100, Category.MIDTERM));
        gradeBook.addAssignment("S-10003003", "College Algebra", 
            new Assignment("Final Exam", 66, 100, Category.FINAL_EXAM));
        
        // Course 2: Chemistry (4 credits) - Failing
        Course chemistry = gradeBook.enrollInCourse("S-10003003", "General Chemistry", 4);
        gradeBook.addAssignment("S-10003003", "General Chemistry", 
            new Assignment("Lab 1", 55, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10003003", "General Chemistry", 
            new Assignment("Lab 2", 58, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10003003", "General Chemistry", 
            new Assignment("Lab 3", 52, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10003003", "General Chemistry", 
            new Assignment("Quiz 1", 60, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10003003", "General Chemistry", 
            new Assignment("Quiz 2", 55, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10003003", "General Chemistry", 
            new Assignment("Midterm Exam", 58, 100, Category.MIDTERM));
        gradeBook.addAssignment("S-10003003", "General Chemistry", 
            new Assignment("Final Exam", 62, 100, Category.FINAL_EXAM));
        
        // Course 3: Physics (3 credits) - Passing
        Course physics = gradeBook.enrollInCourse("S-10003003", "Physics I", 3);
        gradeBook.addAssignment("S-10003003", "Physics I", 
            new Assignment("HW1", 75, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10003003", "Physics I", 
            new Assignment("HW2", 70, 100, Category.HOMEWORK));
        gradeBook.addAssignment("S-10003003", "Physics I", 
            new Assignment("Quiz 1", 72, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10003003", "Physics I", 
            new Assignment("Quiz 2", 68, 100, Category.QUIZZES));
        gradeBook.addAssignment("S-10003003", "Physics I", 
            new Assignment("Midterm Exam", 74, 100, Category.MIDTERM));
        gradeBook.addAssignment("S-10003003", "Physics I", 
            new Assignment("Final Exam", 73, 100, Category.FINAL_EXAM));
        
        System.out.println("  ✓ Enrolled in 3 courses (10 credits total)");
        System.out.println("  ✓ Completed 18 assignments\n");
    }
    
    /**
     * Displays transcripts for all students.
     */
    private static void displayAllTranscripts(GradeBook gradeBook) {
        System.out.println("\n================================================================================");
        System.out.println("                         STUDENT TRANSCRIPTS");
        System.out.println("================================================================================\n");
        
        for (Student student : gradeBook.getAllStudents()) {
            String transcript = gradeBook.generateTranscript(student.getStudentId());
            System.out.println(transcript);
            System.out.println("\n");
        }
    }
    
    /**
     * Displays gradebook-wide statistics.
     */
    private static void displayGradeBookStatistics(GradeBook gradeBook) {
        System.out.println("\n================================================================================");
        System.out.println("                      GRADEBOOK STATISTICS");
        System.out.println("================================================================================\n");
        
        System.out.println(gradeBook.getStatistics());
        
        System.out.println("\n\nStudent Rankings by GPA:");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-5s %-20s %-15s %10s\n", "Rank", "Student Name", "Student ID", "GPA");
        System.out.println("--------------------------------------------------------------------------------");
        
        int rank = 1;
        for (Student student : gradeBook.getStudentsByGPA()) {
            double gpa = gradeBook.calculateGPA(student.getStudentId());
            System.out.printf("%-5d %-20s %-15s %10.2f\n", 
                rank++, student.getName(), student.getStudentId(), gpa);
        }
        System.out.println("--------------------------------------------------------------------------------\n");
    }
    
    /**
     * Displays Dean's List and Academic Probation lists.
     */
    private static void displayDeansListAndProbation(GradeBook gradeBook) {
        System.out.println("\n================================================================================");
        System.out.println("                      ACADEMIC RECOGNITION & ALERTS");
        System.out.println("================================================================================\n");
        
        // Dean's List
        System.out.println("DEAN'S LIST (GPA >= 3.5, >= 12 credits):");
        System.out.println("--------------------------------------------------------------------------------");
        var deansList = gradeBook.getDeansListStudents();
        if (deansList.isEmpty()) {
            System.out.println("  No students currently qualify for Dean's List.");
        } else {
            for (Student student : deansList) {
                double gpa = gradeBook.calculateGPA(student.getStudentId());
                System.out.printf("  * %s (%s) - GPA: %.2f\n", 
                    student.getName(), student.getStudentId(), gpa);
            }
        }
        
        // Academic Probation
        System.out.println("\n\nACADEMIC PROBATION (GPA < 2.0):");
        System.out.println("--------------------------------------------------------------------------------");
        var probationList = gradeBook.getProbationStudents();
        if (probationList.isEmpty()) {
            System.out.println("  No students currently on academic probation.");
        } else {
            for (Student student : probationList) {
                double gpa = gradeBook.calculateGPA(student.getStudentId());
                System.out.printf("  ! %s (%s) - GPA: %.2f - NEEDS ACADEMIC SUPPORT\n", 
                    student.getName(), student.getStudentId(), gpa);
            }
        }
        System.out.println("--------------------------------------------------------------------------------\n");
    }
}
