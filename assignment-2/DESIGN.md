# Gradebook System - Architecture & Design

## Table of Contents
1. [System Architecture](#system-architecture)
2. [Design Patterns](#design-patterns)
3. [Class Design](#class-design)
4. [Data Flow](#data-flow)
5. [Design Decisions](#design-decisions)
6. [SOLID Principles](#solid-principles)

---

## System Architecture

### Layered Architecture

```
┌─────────────────────────────────────────────────┐
│          Presentation Layer                      │
│  (Main.java, TranscriptFormatter, Colors)       │
└─────────────────────────────────────────────────┘
                     │
┌─────────────────────────────────────────────────┐
│          Service Layer                           │
│        (GradeBookService)                        │
└─────────────────────────────────────────────────┘
                     │
┌─────────────────────────────────────────────────┐
│          Calculator Layer                        │
│    (GradeCalculator, GPACalculator)              │
└─────────────────────────────────────────────────┘
                     │
┌─────────────────────────────────────────────────┐
│          Domain Layer                            │
│  (Student, Course, Assignment, Enums)            │
└─────────────────────────────────────────────────┘
                     │
┌─────────────────────────────────────────────────┐
│          Exception Layer                         │
│    (GradebookException hierarchy)                │
└─────────────────────────────────────────────────┘
```

### Package Structure

```
com.prathamesh.gradebook/
├── domain/              # Core entities
│   ├── Category         # Enum: HOMEWORK, QUIZZES, MIDTERM, FINAL_EXAM
│   ├── LetterGrade      # Enum: A, B, C, D, F with GPA mappings
│   ├── Assignment       # Value Object: Immutable assignment
│   ├── Course           # Entity: Course with assignments
│   └── Student          # Entity: Student with courses
├── exception/           # Domain-specific exceptions (8 classes)
├── calculator/          # Calculation algorithms
│   ├── GradeCalculator  # Category average, course grade
│   ├── GPACalculator    # Credit-weighted GPA
│   └── CourseGrade      # Value Object: numeric/letter/GPA
├── service/             # Business logic orchestration
│   └── GradeBookService # Main application service
└── util/                # Supporting utilities
    ├── ConsoleColors    # ANSI color codes
    └── TranscriptFormatter # Professional transcript formatting
```

---

## Design Patterns

### 1. Value Object Pattern

**Used In**: Assignment, CourseGrade

**Purpose**: Immutable domain objects identified by values

**Benefits**:
- Thread-safety without synchronization
- No defensive copying needed
- Prevents accidental state changes

### 2. Entity Pattern

**Used In**: Student

**Purpose**: Objects with distinct identity

**Key Design**:
- equals() and hashCode() based ONLY on studentId
- Name can change without affecting identity
- Identity persists across attribute changes

### 3. Service Layer Pattern

**Used In**: GradeBookService

**Purpose**: Orchestrate domain operations

**Responsibilities**:
- Manage student registration
- Coordinate enrollment and assignment operations
- Delegate calculations to calculator classes
- Enforce business rules

### 4. Domain Exception Pattern

**Used In**: All custom exceptions (8 types)

**Purpose**: Type-safe error handling

**Hierarchy**:
```
GradebookException (extends RuntimeException)
├── InvalidGradeException
├── InvalidWeightException
├── InvalidCreditHoursException
├── InvalidCategoryException
├── StudentNotFoundException
├── CourseNotFoundException
└── DuplicateEnrollmentException
```

### 5. Enum with Behavior Pattern

**Used In**: Category, LetterGrade

**Purpose**: Enums that encapsulate data and behavior

**Category**: Provides default weights  
**LetterGrade**: Converts percentage to grade

---

## Class Design

### Core Entities

| Class | Type | Key Responsibilities |
|-------|------|---------------------|
| **Student** | Entity | Store ID, name; manage enrollments |
| **Course** | Entity | Store details, weights; manage assignments |
| **Assignment** | Value Object | Store points, calculate percentage |
| **CourseGrade** | Value Object | Store numeric/letter/GPA representation |

### Invariants

| Class | Invariants |
|-------|-----------|
| **Assignment** | pointsEarned ≤ pointsPossible, pointsPossible > 0, immutable |
| **Course** | creditHours ∈ [1,6], weights sum to 100 |
| **Student** | studentId matches S-\\d{8}, no duplicate courses |

### Calculator Classes

| Calculator | Responsibility |
|-----------|---------------|
| **GradeCalculator** | Category average, course grade, weight adjustment |
| **GPACalculator** | Credit-hour weighted GPA calculation |

---

## Data Flow

### Add Assignment Flow

```
GradeBookService.addAssignment()
    │
    ├─→ Lookup student (throws StudentNotFoundException)
    │
    ├─→ Get course from student (throws CourseNotFoundException)
    │
    ├─→ Create Assignment (validates: earned ≤ possible, possible > 0)
    │
    └─→ course.addAssignment(assignment)
```

### Calculate Course Grade Flow

```
GradeBookService.calculateCourseGrade()
    │
    └─→ GradeCalculator.calculateCourseGrade(course)
            │
            ├─→ Group assignments by category
            │
            ├─→ For each non-empty category: calculate average
            │
            ├─→ Calculate adjusted weights (exclude empty)
            │
            ├─→ Calculate weighted sum
            │
            └─→ Return CourseGrade(numeric, letter, gpa)
```

### Calculate GPA Flow

```
GradeBookService.calculateGPA()
    │
    └─→ GPACalculator.calculateGPA(student)
            │
            ├─→ For each course: get GPA value
            │
            ├─→ Calculate quality points = gpa × credits
            │
            └─→ Return totalQualityPoints / totalCredits
```

---

## Design Decisions

### 1. Immutable Assignments

**Decision**: Assignment is final class with final fields

**Rationale**:
- Audit trail: once submitted, grades don't change
- Thread-safe by design
- Clear API intent

**Trade-off**: Cannot update - must delete and recreate

---

### 2. EnumMap for Category Weights

**Decision**: Use EnumMap<Category, Integer>

**Rationale**:
- Type safety (no invalid category names)
- Better performance than HashMap
- Compiler-checked completeness

---

### 3. Service Layer (Not Repository)

**Decision**: Named GradeBookService, not Repository

**Rationale**:
- Accurately describes orchestration role
- No data persistence abstraction
- Contains business logic

---

### 4. Fail-Fast Validation

**Decision**: Validate in constructors, throw immediately

**Rationale**:
- Errors caught at creation time
- Clear error messages
- Invalid state never exists

---

### 5. Credit-Weighted GPA

**Decision**: Weight GPA by credit hours

**Rationale**:
- Academic standard approach
- Fair representation (4-credit ≠ 1-credit)
- Real-world accuracy

---

### 6. Empty Category Exclusion

**Decision**: Exclude empty categories, adjust weights

**Rationale**:
- Flexibility for courses not using all categories
- Fair to students (no penalty for unassigned work)
- Mathematically sound (weights still sum to 100)

---

### 7. Domain Exceptions

**Decision**: Typed exception hierarchy

**Rationale**:
- Type-safe error handling
- Rich context in each exception
- Self-documenting APIs

---

## SOLID Principles

| Principle | Application |
|-----------|-------------|
| **Single Responsibility** | Assignment manages only assignment data. GradeCalculator only calculates grades. |
| **Open/Closed** | Can add new calculators without modifying existing code. |
| **Liskov Substitution** | Calculator implementations are interchangeable through interfaces. |
| **Interface Segregation** | GradeCalculator and GPACalculator have focused, minimal interfaces. |
| **Dependency Inversion** | GradeBookService depends on Calculator abstractions, not concrete implementations. |

---

## OOP Principles

| Principle | Application |
|-----------|-------------|
| **Encapsulation** | Account balance accessible only through methods. Collections returned as unmodifiable. |
| **Abstraction** | Service API hides complexity of calculation and validation logic. |
 **Inheritance** | Exception hierarchy with GradebookException base class. |
| **Polymorphism** | Calculator strategy substitution through interfaces. |
| **Composition** | Student HAS-MANY Courses. Course HAS-MANY Assignments. |

---

## Algorithm Summary

### Category Average

```
totalEarned = sum(assignment.pointsEarned)
totalPossible = sum(assignment.pointsPossible)
return (totalEarned / totalPossible) × 100
```

### Course Grade

```
1. Calculate category averages
2. Identify non-empty categories
3. Adjust weights: (originalWeight / sumOfNonEmptyWeights) × 100
4. Compute: Σ(categoryAvg × adjustedWeight)
5. Convert to letter grade and GPA
```

### GPA

```
totalQualityPoints = Σ(courseGPA × courseCredits)
totalCredits = Σ(courseCredits)
return totalQualityPoints / totalCredits
```

---

## Testing Strategy

### Test Categories

| Category | Focus | Test Count |
|----------|-------|------------|
| **Unit Tests** | Individual class behavior | 106 (domain) |
| **Calculator Tests** | Calculation accuracy | 90 |
| **Service Tests** | Orchestration logic | 54 |
| **Exception Tests** | Error handling | 67 |
| **Utility Tests** | Formatting, colors | 42 |
| **Integration Tests** | End-to-end flows | Embedded |

See [TEST_COVERAGE_ANALYSIS.md](TEST_COVERAGE_ANALYSIS.md) for detailed breakdown.

---

## Key Benefits

| Benefit | Description |
|---------|-------------|
| **Maintainability** | Clear separation of concerns |
| **Testability** | Each component tested independently |
| **Extensibility** | Easy to add new account types or calculators |
| **Correctness** | Fail-fast validation prevents invalid state |
| **Clarity** | Self-documenting code with typed exceptions |
