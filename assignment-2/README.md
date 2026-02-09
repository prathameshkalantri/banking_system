# Student Gradebook System

## Overview
Professional gradebook management system with weighted category grading and credit-hour weighted GPA calculation.

This system provides comprehensive student grade management, including assignment tracking, category-based grading with configurable weights, automatic letter grade conversion, GPA calculation, and professional transcript formatting.

## Features

- **Four Grading Categories**: HOMEWORK, QUIZZES, MIDTERM, FINAL_EXAM with configurable weights
- **Automatic Grade Calculation**: Percentage and letter grade conversion with GPA values
- **Credit-Hour Weighted GPA**: Accurate GPA computation across multiple courses
- **Professional Transcripts**: Color-coded, formatted transcripts with Honor Roll designation
- **Missing Assignment Handling**: Missing assignments automatically scored as zero
- **Empty Category Exclusion**: Categories with no assignments excluded with proportional weight adjustment
- **Class Analytics**: Honor Roll identification, class averages, course statistics
- **Comprehensive Validation**: Student ID format, credit hours, points, weights
- **Rich Error Handling**: Domain-specific exceptions with detailed context
- **Defensive Programming**: Immutable value objects, defensive copying, fail-fast design

## Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Build & Test
```bash
# Navigate to project directory
cd assignment-2

# Compile the project
mvn clean compile

# Run all tests (492 comprehensive tests)
mvn test

# Run the demo application
mvn exec:java
```

### Expected Output
The demo will display:
1. Student registration and course enrollment
2. Assignment submissions with varied performance
3. Edge case demonstrations (missing categories, weight redistribution)
4. Category averages by student
5. Professional transcripts for all students
6. Class analytics including Honor Roll and statistics

## Project Structure

```
assignment-2/
├── pom.xml                     # Maven configuration
├── README.md                   # This file
├── DESIGN.md                   # Architecture and design patterns
├── GRADING_POLICY.md           # Grading rules and calculations
├── TEST_COVERAGE_ANALYSIS.md   # Test coverage details
└── src/
    ├── main/java/com/prathamesh/gradebook/
    │   ├── Main.java                          # Demo application
    │   ├── domain/                            # Core domain models
    │   │   ├── Category.java                  # Grading categories enum
    │   │   ├── LetterGrade.java              # Letter grades (A-F) with GPA
    │   │   ├── Assignment.java               # Immutable assignment value object
    │   │   ├── Course.java                   # Course with assignments
    │   │   └── Student.java                  # Student entity
    │   ├── exception/                         # Domain exceptions
    │   │   ├── GradebookException.java       # Base exception
    │   │   ├── InvalidGradeException.java
    │   │   ├── InvalidWeightException.java
    │   │   ├── InvalidCreditHoursException.java
    │   │   ├── InvalidCategoryException.java
    │   │   ├── StudentNotFoundException.java
    │   │   ├── CourseNotFoundException.java
    │   │   └── DuplicateEnrollmentException.java
    │   ├── calculator/                        # Calculation logic
    │   │   ├── GradeCalculator.java          # Category and course grade calculation
    │   │   ├── GPACalculator.java            # Credit-weighted GPA calculation
    │   │   └── CourseGrade.java              # Course grade value object
    │   ├── service/                           # Service layer
    │   │   └── GradeBookService.java         # Orchestration and business logic
    │   └── util/                              # Utilities
    │       ├── ConsoleColors.java            # ANSI color codes
    │       └── TranscriptFormatter.java      # Professional transcript formatting
    └── test/java/com/prathamesh/gradebook/
        └── [20 comprehensive test files]
```

## Usage Examples

### Creating a GradeBook and Registering Students

Create a GradeBookService instance and register students using the registerStudent method. Students are created with a valid ID (format: S-XXXXXXXX) and name. The service maintains all student records and coordinates operations.

### Enrolling in Courses

Enroll students in courses using the enrollInCourse method with student ID, course code, course name, and credit hours. Courses can use default category weights (HW:20%, Quiz:20%, Mid:25%, Final:35%) or custom weights. Custom weights must be provided for all four categories and sum to exactly 100.

### Adding Assignments

Add assignments to courses using the addAssignment method with student ID, course code, category, assignment name, points earned, and points possible. The system validates that points earned does not exceed points possible. All assignments are recorded and contribute to category averages.

### Calculating Grades and GPA

Calculate individual course grades using the calculateCourseGrade method, which returns a CourseGrade object containing numeric grade, letter grade, and GPA value. Calculate overall student GPA using the calculateGPA method, which computes credit-hour weighted GPA across all enrolled courses.

### Generating Transcripts

Generate professional formatted transcripts using the getTranscript method with student ID. Transcripts include student information, GPA, Honor Roll designation (if applicable), and a detailed table of all courses with grades. Output is color-coded with ANSI codes for terminal display.

### Accessing Class Analytics

Retrieve Honor Roll students (GPA >= 3.5) using getHonorRollStudents method. Calculate class average GPA using getClassAverageGPA. Get course-specific statistics including minimum, maximum, average, and standard deviation using getCourseStatistics with the course code.

## Sample Output

```
╔══════════════════════════════════════════════════════════════════╗
║                     ACADEMIC TRANSCRIPT                          ║
╠══════════════════════════════════════════════════════════════════╣
║  Student: Alice Johnson (S-12345678)                            ║
║  GPA: 4.0                                          ** HONOR ROLL **
╠══════════════════════════════════════════════════════════════════╣
║  Course Code  Course Name           Credits  Grade  Letter  GPA  ║
╠══════════════════════════════════════════════════════════════════╣
║  CS101        Data Structures       4        92.5%  A       4.0  ║
║  MATH201      Calculus II           3        91.2%  A       4.0  ║
║  ENG101       English Composition   3        94.8%  A       4.0  ║
╠══════════════════════════════════════════════════════════════════╣
║  Total Credits: 10                                               ║
╚══════════════════════════════════════════════════════════════════╝
```

## Grading Scale

| Letter Grade | Percentage Range | GPA Value |
|--------------|------------------|-----------|
| A            | 90.00 - 100.00   | 4.0       |
| B            | 80.00 - 89.99    | 3.0       |
| C            | 70.00 - 79.99    | 2.0       |
| D            | 60.00 - 69.99    | 1.0       |
| F            | 0.00 - 59.99     | 0.0       |

## Default Category Weights

| Category     | Default Weight | Description              |
|--------------|----------------|--------------------------|
| HOMEWORK     | 20%            | Weekly assignments       |
| QUIZZES      | 20%            | Short assessments        |
| MIDTERM      | 25%            | Mid-semester exam        |
| FINAL_EXAM   | 35%            | Comprehensive final exam |

## Key Assumptions & Design Decisions

### 1. Missing Assignments
**Decision**: Missing assignments are scored as 0%  
**Rationale**: Encourages completion, standard academic policy

### 2. Empty Categories
**Decision**: Categories with no assignments are excluded from calculation  
**Implementation**: Weights are redistributed proportionally among non-empty categories  
**Example**: If MIDTERM is missing (25% weight), the remaining 75% is scaled to 100%

### 3. Student ID Format
**Decision**: Must follow pattern `S-XXXXXXXX` (S- followed by 8 digits)  
**Rationale**: Standardized identification, easy validation

### 4. Credit Hours
**Decision**: Must be between 1 and 6 inclusive  
**Rationale**: Standard academic credit range per course

### 5. Honor Roll Eligibility
**Decision**: GPA >= 3.5  
**Rationale**: Common academic honor threshold

### 6. Immutable Assignments
**Decision**: Assignments are immutable value objects  
**Rationale**: Thread-safety, prevents accidental modification, clear audit trail

### 7. No Duplicate Enrollments
**Decision**: Students cannot enroll in the same course twice  
**Rationale**: Business rule preventing logical errors

### 8. Custom Weights Must Sum to 100%
**Decision**: Strict validation that all weights sum exactly to 100  
**Rationale**: Mathematical correctness, prevents calculation errors

## Testing

The system includes 492 comprehensive tests across 20 test files:

- **Domain Tests**: 106 tests covering Assignment, Course, Student
- **Calculator Tests**: 90 tests for grade and GPA calculations
- **Service Tests**: 54 tests for GradeBookService
- **Exception Tests**: 67 tests for exception hierarchy
- **Utility Tests**: 42 tests for formatting and colors
- **Model Tests**: 14 tests for enums (Category, LetterGrade)
- **Integration Tests**: 119 tests for end-to-end scenarios

**Test Coverage**: 90%+ line coverage, 85%+ branch coverage

See [TEST_COVERAGE_ANALYSIS.md](TEST_COVERAGE_ANALYSIS.md) for detailed metrics.

## Architecture

The system follows a layered architecture with clear separation of concerns:

1. **Domain Layer**: Core business entities and value objects
2. **Calculator Layer**: Calculation algorithms (grades, GPA)
3. **Service Layer**: Business logic orchestration
4. **Exception Layer**: Domain-specific error handling
5. **Utility Layer**: Formatting and presentation

See [DESIGN.md](DESIGN.md) for detailed architecture and design patterns.

## Grading Policy

For comprehensive grading rules, calculation methodology, and examples, see [GRADING_POLICY.md](GRADING_POLICY.md).

## Development

### Code Style
- Java 11 language features
- Immutable value objects where appropriate
- Defensive copying for collections
- Fail-fast validation
- Rich domain exceptions

### Design Patterns
- **Value Object**: Assignment, CourseGrade
- **Entity**: Student
- **Service Layer**: GradeBookService
- **Domain Exceptions**: Type-safe error handling

### SOLID Principles
All SOLID principles are applied:
- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Extensible through interfaces
- **Liskov Substitution**: Calculator implementations are interchangeable
- **Interface Segregation**: Focused, minimal interfaces
- **Dependency Inversion**: Depends on abstractions, not concretions

## Performance Characteristics

- **Student Registration**: O(1) average
- **Course Enrollment**: O(1) average
- **Assignment Addition**: O(1)
- **Grade Calculation**: O(n) where n = number of assignments
- **GPA Calculation**: O(m) where m = number of courses
- **Honor Roll Query**: O(s) where s = number of students

## Limitations & Future Enhancements

### Current Limitations
- In-memory storage only (no persistence)
- Single-threaded design
- No assignment update capability (immutable by design)
- No grade curving support

### Potential Future Enhancements
- Database persistence layer
- Multi-threaded support with concurrent collections
- Assignment revision history
- Grade curving algorithms
- Weighted GPA calculation with advanced placement courses
- Semester/term support
- Attendance tracking
- Grade distribution analytics
- Export to PDF/Excel

## License

This is a demonstration project created for Intuit hiring process evaluation.

## Author

Prathamesh Kalantri

## Support

For questions or issues, please refer to the comprehensive documentation:
- [DESIGN.md](DESIGN.md) - Architecture and design patterns
- [GRADING_POLICY.md](GRADING_POLICY.md) - Grading rules and calculations
- [TEST_COVERAGE_ANALYSIS.md](TEST_COVERAGE_ANALYSIS.md) - Test coverage details
