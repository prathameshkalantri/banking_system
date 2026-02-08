# System Design Document

## Student Gradebook System - Architecture and Implementation

### Document Version: 1.0
### Last Updated: February 8, 2026

---

## Table of Contents

1. System Overview
2. Architecture Design
3. Domain Model
4. Service Layer Design
5. Key Algorithms
6. Design Patterns
7. Data Flow
8. Validation Strategy
9. Error Handling
10. Performance Considerations
11. Design Trade-offs

---

## 1. System Overview

### Purpose

The Student Gradebook System is designed to provide a comprehensive academic management solution for tracking student performance, calculating grades, and generating reports. The system emphasizes accuracy, maintainability, and extensibility.

### Design Goals

- **Accuracy**: Precise grade and GPA calculations with proper rounding and normalization
- **Maintainability**: Clear separation of concerns and well-documented code
- **Extensibility**: Easy to add new features without modifying existing code
- **Testability**: High test coverage with unit and integration tests
- **Robustness**: Comprehensive input validation and error handling

### Technology Stack

- Java 11: Core programming language
- Maven: Build automation and dependency management
- JUnit 5: Testing framework

---

## 2. Architecture Design

### Layered Architecture

The system follows a three-layer architecture:

#### Layer 1: Domain Model (Entity Layer)
Contains core business entities that represent the problem domain. These classes encapsulate data and provide basic domain operations.

**Classes**: Student, Course, Assignment, Grade
**Enumerations**: Category, LetterGrade

#### Layer 2: Service Layer (Business Logic Layer)
Implements business rules and complex operations. Services coordinate between domain objects to perform higher-level tasks.

**Classes**: GradeCalculator, GPACalculator, TranscriptGenerator, GradeBook

#### Layer 3: Application Layer
Entry point for the application that demonstrates system capabilities.

**Classes**: Main

### Dependency Flow

Dependencies flow from outer layers to inner layers:
- Application Layer depends on Service Layer
- Service Layer depends on Domain Model
- Domain Model has no external dependencies

This unidirectional dependency ensures loose coupling and high cohesion.

---

## 3. Domain Model

### Student

**Responsibilities**:
- Store student identification and personal information
- Maintain enrollment in courses
- Track academic standing (GPA, Dean's List, probation status)

**Key Design Decisions**:
- Student ID validation enforces format S-XXXXXXXX for consistency
- Course enrollment is managed internally with duplicate prevention
- GPA calculation is delegated to specialized calculator services
- Immutable after course modifications to ensure thread safety

**Invariants**:
- Student ID must be non-null and match required format
- Name must be non-null and non-empty
- Course collection cannot contain null entries

### Course

**Responsibilities**:
- Store course information (name, credit hours)
- Manage assignments organized by category
- Calculate category averages and final percentages

**Key Design Decisions**:
- Credit hours are validated to be positive
- Assignments are stored in a list to preserve order
- Category-based organization allows for flexible grading
- Final percentage calculation includes normalization for missing categories

**Invariants**:
- Course name must be unique per student
- Credit hours must be positive
- All assignments must have a category
- Total points for assignments must be positive

### Assignment

**Responsibilities**:
- Store assignment data (name, points, category)
- Calculate percentage score

**Key Design Decisions**:
- Immutable design prevents accidental modifications after creation
- Category is required and enforced through enumeration
- Validation ensures earned points do not exceed total points
- Percentage calculation is built-in for convenience

**Invariants**:
- Name must be non-null and non-empty
- Earned points must be non-negative
- Total points must be positive
- Earned points cannot exceed total points
- Category must be non-null

### Grade

**Responsibilities**:
- Convert percentage to letter grade
- Map letter grade to GPA points
- Provide grade classification

**Key Design Decisions**:
- Immutable design ensures grade consistency
- Letter grade conversion uses standard cutoffs (90/80/70/60)
- GPA point mapping uses 4.0 scale
- Plus/minus grades are not supported for simplicity

**Invariants**:
- Percentage must be in range 0.0 to 100.0
- Letter grade must be valid enum value
- GPA points must match letter grade

---

## 4. Service Layer Design

### GradeCalculator

**Purpose**: Calculates grades at the category and course level.

**Key Methods**:
- calculateCategoryAverage(Course, Category): Computes average percentage for a category
- calculateFinalPercentage(Course): Computes weighted final grade with normalization

**Algorithm**: Weighted average with dynamic normalization
1. Calculate average for each category
2. Multiply by category weight
3. Sum weighted values
4. Divide by total weight used (handles missing categories)

**Design Rationale**:
- Normalization ensures fair grading when categories are missing
- Separation of category and final calculations enables reusability
- Stateless design allows concurrent usage

### GPACalculator

**Purpose**: Calculates GPA and academic standing classifications.

**Key Methods**:
- calculateGPA(Student): Computes credit-hour weighted GPA
- calculateGPA(Collection<Course>): Computes GPA for course subset
- getGPAClassification(double): Returns academic standing label
- qualifiesForHonors(Student): Checks honor roll eligibility
- isOnProbation(Student): Checks probation status

**Algorithm**: Credit-hour weighted average
1. For each course, convert grade to GPA points
2. Multiply GPA points by credit hours
3. Sum all grade points and credit hours
4. Divide total grade points by total credit hours

**Design Rationale**:
- Credit-hour weighting ensures accurate representation of academic achievement
- Classification logic is centralized for consistency
- Multiple calculation methods support different use cases

### TranscriptGenerator

**Purpose**: Generates formatted academic reports.

**Key Methods**:
- generateTranscript(Student): Creates full academic transcript
- generateGradeReport(Student): Creates current grade report
- generateCourseBreakdown(Course): Creates detailed course analysis

**Design Rationale**:
- Separation of report types allows for focused formatting
- Text-based output is simple and portable
- Integration with calculator services ensures data consistency

### GradeBook

**Purpose**: Central management system for all students and operations.

**Key Methods**:
- addStudent(String, String): Creates and registers new student
- enrollInCourse(String, String, int): Enrolls student in course
- addAssignment(String, String, Assignment): Adds assignment to student's course
- getCourseGrade(String, String): Retrieves grade for specific course
- calculateGPA(String): Computes student GPA
- getDeansListStudents(): Returns honor roll students
- getProbationStudents(): Returns at-risk students

**Design Rationale**:
- Facade pattern simplifies client interaction
- Centralized student management prevents duplication
- Integration with all services provides one-stop functionality

---

## 5. Key Algorithms

### Weighted Grade Calculation with Normalization

**Problem**: Calculate final grade when some categories may be missing assignments.

**Solution**:
1. For each category that has assignments:
   - Calculate category average (sum of percentages / count)
   - Multiply by category weight
2. Sum all weighted values
3. Calculate total weight used (sum of weights for categories with assignments)
4. Normalize: divide sum by total weight used
5. Result is final percentage

**Example**:
Given: HW=90% (weight 0.20), Quiz=85% (weight 0.20), Final=92% (weight 0.35)
Missing: Midterm (weight 0.25)

Calculation:
- Weighted sum = 90 * 0.20 + 85 * 0.20 + 92 * 0.35 = 67.2
- Total weight = 0.20 + 0.20 + 0.35 = 0.75
- Normalized = 67.2 / 0.75 = 89.6%

**Rationale**: This approach ensures students are not penalized for missing categories while maintaining fair relative weighting.

### GPA Calculation with Credit Hours

**Problem**: Calculate cumulative GPA considering course credit hours.

**Solution**:
1. For each course:
   - Get final percentage
   - Convert to letter grade
   - Convert letter grade to GPA points
   - Multiply GPA points by course credit hours
2. Sum all grade points
3. Sum all credit hours
4. Divide total grade points by total credit hours
5. Round to 2 decimal places

**Example**:
Course 1: A (4.0 points) * 3 credits = 12.0 grade points
Course 2: B (3.0 points) * 4 credits = 12.0 grade points
Total: 24.0 grade points / 7 credits = 3.43 GPA

**Rationale**: Credit-hour weighting gives more weight to courses with higher credit values, reflecting their greater contribution to overall academic achievement.

### Letter Grade Conversion

**Problem**: Convert numerical percentage to letter grade.

**Solution**: Use fixed thresholds with boundary handling
- A: 90.0 <= percentage <= 100.0
- B: 80.0 <= percentage < 90.0
- C: 70.0 <= percentage < 80.0
- D: 60.0 <= percentage < 70.0
- F: percentage < 60.0

**Edge Cases**:
- Exactly 90.0 is an A
- Exactly 80.0 is a B
- Values above 100.0 are capped at 100.0 (considered as A)

**Rationale**: Simple, deterministic conversion with clear boundaries eliminates ambiguity.

---

## 6. Design Patterns

### Builder Pattern (Implicit)

Used in model classes where objects are constructed with validated parameters. Validation occurs during construction to ensure invalid objects are never created.

### Facade Pattern

The GradeBook class acts as a facade, providing a simplified interface to the complex subsystem of students, courses, grades, and calculations.

### Strategy Pattern (Implicit)

Different calculator services (GradeCalculator, GPACalculator) implement different calculation strategies while maintaining consistent interfaces.

### Immutable Object Pattern

Assignment and Grade classes are immutable after creation, preventing accidental modifications and ensuring thread safety.

### Null Object Pattern

The system returns empty collections instead of null when no results are found, reducing null pointer exceptions.

---

## 7. Data Flow

### Student Enrollment Flow

1. Client calls GradeBook.addStudent()
2. GradeBook creates new Student object with validation
3. Student is stored in internal HashMap
4. Student object is returned to client

### Assignment Submission Flow

1. Client calls GradeBook.addAssignment()
2. GradeBook retrieves student by ID
3. GradeBook retrieves course from student
4. Course adds assignment to internal list
5. Assignment is categorized automatically

### Grade Calculation Flow

1. Client calls GradeBook.getCourseGrade()
2. GradeBook retrieves student and course
3. GradeCalculator computes category averages
4. GradeCalculator computes weighted final percentage
5. Grade object created from percentage
6. Letter grade assigned based on thresholds
7. Grade returned to client

### GPA Calculation Flow

1. Client calls GradeBook.calculateGPA()
2. GradeBook retrieves student
3. GPACalculator iterates through all courses
4. For each course, calculate final percentage
5. Convert percentage to letter grade
6. Convert letter grade to GPA points
7. Apply credit-hour weighting
8. Return cumulative GPA

---

## 8. Validation Strategy

### Input Validation

**Model Layer Validation**:
- All model constructors validate inputs
- Illegal arguments throw IllegalArgumentException with descriptive messages
- Null checks prevent null pointer exceptions

**Service Layer Validation**:
- Services validate method parameters
- Business rule violations throw IllegalArgumentException
- Validation is performed before any state changes

### Validation Rules

**Student**:
- Student ID must match format S-XXXXXXXX (8 digits)
- Name must be non-null and non-empty
- Cannot enroll in duplicate courses

**Course**:
- Name must be non-null and non-empty
- Credit hours must be positive (> 0)
- Cannot add duplicate assignments

**Assignment**:
- Name must be non-null and non-empty
- Earned points must be non-negative
- Total points must be positive
- Earned points cannot exceed total points
- Category must be non-null

**Grade**:
- Percentage must be in range 0.0 to 100.0

---

## 9. Error Handling

### Exception Strategy

The system uses unchecked exceptions (IllegalArgumentException) for validation failures. This is appropriate because:

1. Validation failures indicate programming errors, not recoverable conditions
2. Clients should ensure valid input before calling methods
3. Checked exceptions would clutter the API with unnecessary exception handling

### Error Messages

All exceptions include descriptive messages indicating:
- What validation failed
- Expected format or value range
- Actual value that was rejected

Example: "Student ID must follow format S-XXXXXXXX, got: S001"

### Fail-Fast Principle

The system validates inputs immediately upon receipt. Invalid data is rejected before any processing occurs, preventing corrupted state.

---

## 10. Performance Considerations

### Data Structure Choices

**HashMap for Student Storage**:
- O(1) average-case lookup by student ID
- Efficient for large numbers of students
- Trade-off: Additional memory overhead

**ArrayList for Assignments**:
- O(1) access by index
- Efficient iteration for grade calculations
- Preserves assignment order
- Trade-off: O(n) insertion/deletion (acceptable for typical use)

**LinkedHashMap for Courses per Student**:
- Maintains insertion order for consistent output
- O(1) average-case lookup by course name
- Trade-off: Slightly higher memory than HashMap

### Calculation Optimization

**Stateless Services**:
- No instance state reduces memory usage
- Enables concurrent usage without synchronization

**Lazy Calculation**:
- Grades calculated on demand, not stored
- Reduces memory usage
- Ensures calculations always use current data

### Scalability Considerations

**Current Design**:
- Efficient for typical classroom sizes (up to hundreds of students)
- All data stored in memory
- No database overhead

**Limitations**:
- Not suitable for very large institutions (thousands of students)
- No persistence between application runs
- Limited by available heap memory

---

## 11. Design Trade-offs

### Immutability vs. Flexibility

**Decision**: Make Assignment and Grade immutable

**Advantages**:
- Thread-safe by design
- Prevents accidental modifications
- Simplifies reasoning about state

**Disadvantages**:
- Cannot modify assignment after creation
- Must create new object for grade changes

**Rationale**: Immutability provides safety and clarity, and the disadvantage is minimal since assignments rarely need modification.

### Fixed Category Weights vs. Configurable Weights

**Decision**: Use fixed category weights (HW 20%, Quiz 20%, Mid 25%, Final 35%)

**Advantages**:
- Simpler implementation
- Consistent grading across all courses
- No additional configuration needed

**Disadvantages**:
- Cannot customize weights per course
- Cannot accommodate different grading schemes

**Rationale**: Fixed weights simplify the initial implementation. Future versions can add configurability if needed.

### In-Memory Storage vs. Database Persistence

**Decision**: Store all data in memory using collections

**Advantages**:
- Simpler implementation
- Faster access times
- No database setup required
- Easier testing

**Disadvantages**:
- Data lost when application exits
- Limited by available memory
- No concurrent multi-user support

**Rationale**: In-memory storage is appropriate for a demonstration system and educational project. Production systems would require database integration.

### Text-Based Reports vs. Formatted Documents

**Decision**: Generate plain text reports

**Advantages**:
- Simple implementation
- Portable output
- Easy to test
- No external dependencies

**Disadvantages**:
- Limited formatting options
- Not suitable for professional documents

**Rationale**: Text-based reports demonstrate functionality without requiring complex formatting libraries. Future versions could add PDF or HTML export.

### Validation in Constructors vs. Setters

**Decision**: Validate all inputs in constructors, minimize setters

**Advantages**:
- Objects are always valid after construction
- Cannot create invalid objects
- Simplifies testing

**Disadvantages**:
- Slightly less flexible
- Must create new objects for modifications

**Rationale**: Constructor validation enforces invariants and prevents invalid state throughout the object's lifetime.

---

## Conclusion

The Student Gradebook System demonstrates clean architecture principles with clear separation between domain models and business logic. The design emphasizes correctness, maintainability, and testability while making pragmatic trade-offs appropriate for an educational project. The layered architecture and comprehensive validation ensure the system produces accurate results while remaining easy to understand and extend.
