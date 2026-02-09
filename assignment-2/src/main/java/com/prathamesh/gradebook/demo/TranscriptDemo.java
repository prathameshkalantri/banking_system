package com.prathamesh.gradebook.demo;

import com.prathamesh.gradebook.calculator.CourseGrade;
import com.prathamesh.gradebook.calculator.GPACalculator;
import com.prathamesh.gradebook.domain.Assignment;
import com.prathamesh.gradebook.domain.Category;
import com.prathamesh.gradebook.domain.Course;
import com.prathamesh.gradebook.domain.Student;
import com.prathamesh.gradebook.util.TranscriptFormatter;

import java.util.List;

/**
 * Demo application showing transcript formatting.
 * 
 * This demonstrates Phase 8: Transcript Formatting with:
 * - Professional box-drawing borders
 * - Color-coded grades
 * - Honor Roll designation
 * 
 * @author Prathamesh Kalantri
 */
public class TranscriptDemo {
    
    public static void main(String[] args) {
        System.out.println("\n====================================");
        System.out.println("PHASE 8: TRANSCRIPT FORMATTING DEMO");
        System.out.println("====================================\n");
        
        // Create students
        Student alice = new Student("S-12345678", "Alice Johnson");
        Student bob = new Student("S-87654321", "Bob Smith");
        Student carol = new Student("S-11223344", "Carol Davis");
        
        // Set up Alice (Honor Roll student)
        setupAlice(alice);
        
        // Set up Bob (Average student)
        setupBob(bob);
        
        // Set up Carol (Struggling student)
        setupCarol(carol);
        
        // Generate and display transcripts
        displayTranscript(alice);
        displayTranscript(bob);
        displayTranscript(carol);
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Demo completed successfully!");
        System.out.println("=".repeat(50) + "\n");
    }
    
    private static void setupAlice(Student student) {
        // Data Structures - Excellent
        Course ds = new Course("Data Structures", 3);
        ds.addAssignment(new Assignment("HW1: Arrays", 95, 100, Category.HOMEWORK));
        ds.addAssignment(new Assignment("HW2: Linked Lists", 98, 100, Category.HOMEWORK));
        ds.addAssignment(new Assignment("Quiz 1", 92, 100, Category.QUIZZES));
        ds.addAssignment(new Assignment("Midterm", 94, 100, Category.MIDTERM));
        ds.addAssignment(new Assignment("Final Exam", 96, 100, Category.FINAL_EXAM));
        student.enrollInCourse(ds);
        
        // Algorithms - Excellent
        Course algo = new Course("Algorithms", 4);
        algo.addAssignment(new Assignment("HW1: Sorting", 90, 100, Category.HOMEWORK));
        algo.addAssignment(new Assignment("HW2: Graphs", 94, 100, Category.HOMEWORK));
        algo.addAssignment(new Assignment("Quiz 1", 88, 100, Category.QUIZZES));
        algo.addAssignment(new Assignment("Midterm", 92, 100, Category.MIDTERM));
        algo.addAssignment(new Assignment("Final Exam", 93, 100, Category.FINAL_EXAM));
        student.enrollInCourse(algo);
        
        // Database Systems - Excellent
        Course db = new Course("Database Systems", 3);
        db.addAssignment(new Assignment("HW1: SQL", 100, 100, Category.HOMEWORK));
        db.addAssignment(new Assignment("HW2: Normalization", 95, 100, Category.HOMEWORK));
        db.addAssignment(new Assignment("Quiz 1", 90, 100, Category.QUIZZES));
        db.addAssignment(new Assignment("Midterm", 92, 100, Category.MIDTERM));
        db.addAssignment(new Assignment("Final Exam", 94, 100, Category.FINAL_EXAM));
        student.enrollInCourse(db);
    }
    
    private static void setupBob(Student student) {
        // Data Structures - Good
        Course ds = new Course("Data Structures", 3);
        ds.addAssignment(new Assignment("HW1: Arrays", 80, 100, Category.HOMEWORK));
        ds.addAssignment(new Assignment("HW2: Linked Lists", 85, 100, Category.HOMEWORK));
        ds.addAssignment(new Assignment("Quiz 1", 78, 100, Category.QUIZZES));
        ds.addAssignment(new Assignment("Midterm", 82, 100, Category.MIDTERM));
        ds.addAssignment(new Assignment("Final Exam", 84, 100, Category.FINAL_EXAM));
        student.enrollInCourse(ds);
        
        // Web Development - Good
        Course web = new Course("Web Development", 3);
        web.addAssignment(new Assignment("HW1: HTML/CSS", 88, 100, Category.HOMEWORK));
        web.addAssignment(new Assignment("HW2: JavaScript", 82, 100, Category.HOMEWORK));
        web.addAssignment(new Assignment("Quiz 1", 80, 100, Category.QUIZZES));
        web.addAssignment(new Assignment("Midterm", 85, 100, Category.MIDTERM));
        web.addAssignment(new Assignment("Final Exam", 83, 100, Category.FINAL_EXAM));
        student.enrollInCourse(web);
    }
    
    private static void setupCarol(Student student) {
        // Data Structures - Struggling
        Course ds = new Course("Data Structures", 3);
        ds.addAssignment(new Assignment("HW1: Arrays", 65, 100, Category.HOMEWORK));
        ds.addAssignment(new Assignment("HW2: Linked Lists", 70, 100, Category.HOMEWORK));
        ds.addAssignment(new Assignment("Quiz 1", 60, 100, Category.QUIZZES));
        ds.addAssignment(new Assignment("Midterm", 68, 100, Category.MIDTERM));
        ds.addAssignment(new Assignment("Final Exam", 72, 100, Category.FINAL_EXAM));
        student.enrollInCourse(ds);
        
        // Algorithms - Failing
        Course algo = new Course("Algorithms", 4);
        algo.addAssignment(new Assignment("HW1: Sorting", 55, 100, Category.HOMEWORK));
        algo.addAssignment(new Assignment("Quiz 1", 50, 100, Category.QUIZZES));
        algo.addAssignment(new Assignment("Midterm", 58, 100, Category.MIDTERM));
        algo.addAssignment(new Assignment("Final Exam", 52, 100, Category.FINAL_EXAM));
        student.enrollInCourse(algo);
    }
    
    private static void displayTranscript(Student student) {
        GPACalculator gpaCalculator = new GPACalculator();
        
        List<CourseGrade> grades = gpaCalculator.calculateAllCourseGrades(student);
        double gpa = gpaCalculator.calculateGPA(student);
        
        System.out.println();
        String transcript = TranscriptFormatter.formatTranscript(student, gpa, grades);
        System.out.println(transcript);
    }
}
