# Student Gradebook System

A production-ready gradebook management system implementation in Java demonstrating object-oriented design, weighted grade calculations, and comprehensive academic performance tracking.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Tests](https://img.shields.io/badge/tests-216%20passing-brightgreen)]()
[![Coverage](https://img.shields.io/badge/coverage-comprehensive-brightgreen)]()
[![Java](https://img.shields.io/badge/java-11-blue)]()
[![Maven](https://img.shields.io/badge/maven-3.6.0-blue)]()

## Project Overview

This gradebook system implements a complete academic management solution with four assignment categories (HOMEWORK, QUIZZES, MIDTERM, FINAL_EXAM), weighted grade calculations, and comprehensive GPA tracking. The system includes full academic standing classifications, automatic normalization for missing categories, and detailed transcript generation.

### Key Features

- **Four Assignment Categories** with fixed weights
  - HOMEWORK: 20% of final grade
  - QUIZZES: 20% of final grade
  - MIDTERM: 25% of final grade
  - FINAL_EXAM: 35% of final grade

- **Three Core Calculations** with proper validation
  - Category Averages: Arithmetic mean of assignment percentages
  - Weighted Final Grades: Normalized weighted sum with missing category handling
  - Credit-Hour Weighted GPA: Standard 4.0 scale with proper weighting

- **Complete Academic Standing System**
  - Six-tier GPA classifications (Summa/Magna/Cum Laude, Honors, Good Standing, Probation)
  - Dean's List identification (GPA >= 3.5)
  - Academic probation tracking (GPA < 2.0)
  - Automatic honor roll designation

- **Thread-Safe Operations**
  - Immutable domain models for core entities
  - Stateless service layer
  - Defensive copying for collections

- **Professional Features**
  - Multiple report types (transcripts, grade reports, course breakdowns)
  - Comprehensive validation with descriptive error messages
  - Student ranking and analytics
  - Statistical summaries

## Requirements

- **Java**: 11 or higher
- **Maven**: 3.6.0 or higher
- **JUnit**: 5.10.0 (included in dependencies)

## Quick Start

### 1. Clone and Build

Navigate to the gradebook-system directory and run Maven build.

### 2. Run Tests

Execute Maven test command to run all 216 unit tests.

**Expected Output:**
Tests run: 216, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS

### 3. Run Demo

Execute the Main class using Maven exec plugin.

The demo will automatically:
- Create 3 students (high performer, average, at-risk)
- Enroll students in 2-3 courses each (8 total courses)
- Add 52 assignments across all courses
- Demonstrate all grading categories
- Calculate weighted grades with normalization
- Compute credit-hour weighted GPAs
- Generate complete transcripts
- Display Dean's List and probation students
- Show gradebook statistics

## Project Structure

All source code located in standard Maven directory structure:
- src/main/java: Production code (11 classes)
  - model: Domain entities (Student, Course, Assignment, Grade)
  - enums: Type enumerations (Category, LetterGrade)
  - service: Business logic (GradeCalculator, GPACalculator, TranscriptGenerator, GradeBook)
- src/test/java: Test code (8 test classes, 216 tests)
- pom.xml: Maven configuration
- Documentation: README, DESIGN, TEST_COVERAGE, GRADING_POLICY, REQUIREMENTS_VERIFICATION

## Usage Guide

### Opening Accounts and Enrollment

The GradeBook class serves as the main entry point for all operations. Students must have IDs in format S-XXXXXXXX (8 digits).

Basic workflow:
1. Add student to gradebook with addStudent(studentId, name)
2. Enroll student in courses with enrollInCourse(studentId, courseName, creditHours)
3. Add assignments with addAssignment(studentId, courseName, assignment)
4. Retrieve grades with getCourseGrade(studentId, courseName)
5. Calculate GPA with calculateGPA(studentId)
6. Generate reports with generateTranscript(studentId)

### Assignment Management

Assignments require four pieces of information:
- Name (string, non-empty)
- Points earned (double, non-negative)
- Points possible (double, positive)
- Category (enum: HOMEWORK, QUIZZES, MIDTERM, FINAL_EXAM)

System automatically:
- Calculates assignment percentage
- Groups assignments by category
- Computes category averages
- Applies category weights
- Normalizes when categories are missing

### Grade Calculation Process

Multi-step calculation:
1. Calculate percentage for each assignment (earned / possible * 100)
2. Compute category average (sum of percentages / count)
3. Apply category weight (average * weight)
4. Sum weighted values and total weights used
5. Normalize final grade (weighted sum / total weight)
6. Convert to letter grade using standard scale
7. Map to GPA points (A=4.0, B=3.0, C=2.0, D=1.0, F=0.0)

### GPA Calculation Process

Credit-hour weighted calculation:
1. For each course, get final percentage
2. Convert percentage to letter grade
3. Convert letter grade to GPA points
4. Multiply GPA points by credit hours
5. Sum all grade points and credit hours
6. Divide total grade points by total credit hours
7. Round to 2 decimal places

### Report Generation

Multiple report types available:
- **generateTranscript()**: Complete academic record with all courses, grades, GPA, and academic standing
- **generateGradeReport()**: Current grades for all enrolled courses
- **generateCourseBreakdown()**: Detailed analysis with category-wise performance
- **getStatistics()**: Gradebook-wide summary with average GPA and student counts

### Academic Standing Features

Automatic classification:
- **getDeansListStudents()**: Returns students with GPA >= 3.5
- **getProbationStudents()**: Returns students with GPA < 2.0
- **getStudentsByGPA()**: Returns all students ranked by GPA
- **getGPAClassification()**: Returns Latin honor designation or academic status

## Business Rules

### Category Weight Rules

Fixed weights totaling 100%:
- HOMEWORK: 20% (0.20)
- QUIZZES: 20% (0.20)
- MIDTERM: 25% (0.25)
- FINAL_EXAM: 35% (0.35)

Weight normalization:
- If category has no assignments, it's excluded from calculation
- Remaining weights sum to less than 100%
- Final grade = weighted sum / actual total weight used
- Ensures fair grading regardless of missing categories

### Letter Grade Scale

Standard academic scale:
- A: 90.0 to 100.0 (inclusive)
- B: 80.0 to 89.9
- C: 70.0 to 79.9
- D: 60.0 to 69.9
- F: Below 60.0

Boundary handling:
- Exactly 90.0 is an A
- Exactly 80.0 is a B
- No rounding before grade assignment

### GPA Rules

Credit-hour weighted calculation:
- Each course contributes: (grade points * credit hours)
- Total GPA = sum of grade points / sum of credit hours
- Rounded to 2 decimal places for display

Academic standing thresholds:
- Summa Cum Laude: >= 3.75
- Magna Cum Laude: 3.50 to 3.74
- Cum Laude: 3.25 to 3.49
- Good Standing: 2.00 to 3.24
- Academic Probation: < 2.00

### Validation Rules

Student validation:
- Student ID must match pattern S-XXXXXXXX (exactly 8 digits)
- Name cannot be null or empty

Assignment validation:
- Name cannot be null or empty
- Points earned must be non-negative
- Points possible must be positive
- Points earned cannot exceed points possible
- Category must be non-null

Course validation:
- Course name cannot be null or empty
- Credit hours must be positive

## Test Coverage

- **Total Tests**: 216 (100% passing)
- **Model Tests**: 120 (Assignment: 29, Grade: 29, Course: 32, Student: 30)
- **Service Tests**: 96 (GradeCalculator: 22, GPACalculator: 29, GradeBook: 29, TranscriptGenerator: 16)

### Test Categories

1. **Unit Tests**: Individual method behavior for all classes
2. **Integration Tests**: Multi-class workflow validation
3. **Edge Case Tests**: Boundary conditions and special scenarios
4. **Business Rule Tests**: Grading policy enforcement
5. **Validation Tests**: Input validation and error handling

All tests execute in under 1 second total.

## Architecture

### Design Patterns

- **Immutable Value Objects**: Assignment and Grade
- **Service Layer Pattern**: Separate business logic from domain models
- **Repository Pattern**: GradeBook as central student store
- **Strategy Pattern**: Category-specific weight application
- **Builder Pattern**: Complex object construction with validation

### Design Principles

- **SOLID Principles**: Single responsibility, dependency inversion
- **Immutability**: Critical entities cannot be modified after creation
- **Separation of Concerns**: Clear boundaries between layers
- **Defensive Programming**: Comprehensive input validation
- **BigDecimal Precision**: Accurate decimal calculations (not used due to simplicity, double sufficient)

See DESIGN.md for detailed architecture documentation.

## Performance Considerations

- **Efficient Lookups**: O(1) student and course retrieval using HashMap/LinkedHashMap
- **Memory Efficient**: ArrayList for assignment storage
- **Stateless Services**: No instance state, thread-safe by design
- **Lazy Calculation**: Grades computed on-demand, not pre-calculated
- **Time Complexity**: O(1) lookups, O(n) for calculations where n = assignments/courses

## Error Handling

All validation failures result in:
- IllegalArgumentException with descriptive message
- No state changes to domain objects
- Clear indication of what failed and why

Example error scenarios handled:
- Invalid student ID format
- Null or empty names
- Negative point values
- Points earned exceeding points possible
- Duplicate student enrollment
- Non-existent student or course lookup

## Security Considerations

- Input validation on all public APIs
- Immutable domain objects prevent tampering
- Defensive copying of collections
- No sensitive data stored or logged
- Format validation prevents injection attacks

## Console Output

The demo includes formatted console output with:
- Clear section headers and separators
- Tabular data presentation
- Hierarchical information display
- Summary statistics

No ANSI colors used for maximum compatibility.

## Testing

Run all tests, specific test classes, or generate coverage reports using Maven commands.

Test execution strategies:
- Run full suite for verification
- Run specific test classes during development
- Use test-driven development for new features

## Configuration

Business rules defined as constants (easily configurable):

**Category.java:**
- HOMEWORK_WEIGHT = 0.20
- QUIZZES_WEIGHT = 0.20
- MIDTERM_WEIGHT = 0.25
- FINAL_EXAM_WEIGHT = 0.35

**LetterGrade.java:**
- Grade boundaries: 90/80/70/60
- GPA points: 4.0/3.0/2.0/1.0/0.0

**GPACalculator.java:**
- Academic standing thresholds
- Dean's List minimum: 3.5
- Probation threshold: 2.0

## Documentation

- **README.md**: This file - project overview and usage
- **DESIGN.md**: Architecture decisions and design patterns
- **TEST_COVERAGE.md**: Comprehensive testing analysis
- **GRADING_POLICY.md**: Complete grading system specification
- **REQUIREMENTS_VERIFICATION.md**: Requirements compliance checklist

## Contributing

This is a demonstration project. For educational purposes:

1. Fork the repository
2. Create a feature branch
3. Add comprehensive tests for new features
4. Ensure all 216 tests pass
5. Update documentation
6. Submit a pull request

## License

This project is created for educational and demonstration purposes.

## Author

**Prathamesh Kalantri**
- Student Gradebook System Implementation
- Version 1.0.0
- Date: February 8, 2026

## Acknowledgments

- Built following SOLID principles and clean architecture
- Implements standard academic grading practices
- Designed for Intuit Build Challenge
- Comprehensive test-driven development approach

## Support

For questions or issues:
1. Review the comprehensive documentation files
2. Examine the 216 test cases for usage examples
3. Run the interactive demo for hands-on learning
4. Check REQUIREMENTS_VERIFICATION.md for compliance details

---

**Status**: Production Ready | 216 Tests Passing | Comprehensive Coverage | Best Practices Followed
