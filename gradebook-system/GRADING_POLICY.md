# Grading Policy Specification

## Student Gradebook System - Grading and GPA Calculation Standards

### Document Version: 1.0
### Last Updated: February 8, 2026

---

## Table of Contents

1. Overview
2. Assignment Categories and Weights
3. Letter Grade Scale
4. Grade Point Average (GPA) System
5. Academic Standing Classifications
6. Grade Calculation Process
7. GPA Calculation Process
8. Special Cases and Edge Scenarios
9. Rounding and Precision Rules
10. Academic Recognition Programs

---

## 1. Overview

This document defines the official grading policy, calculation methods, and academic standing criteria for the Student Gradebook System. All grade calculations follow these standardized procedures to ensure consistency, fairness, and transparency.

### Purpose

The grading policy establishes:
- How course grades are calculated from individual assignments
- How letter grades are assigned based on percentages
- How cumulative GPAs are computed across multiple courses
- How academic standing is determined

### Applicable Scope

This policy applies to:
- All courses managed within the system
- All students enrolled in courses
- All assignment submissions and grade calculations
- Academic standing determinations

---

## 2. Assignment Categories and Weights

### Category Structure

Every course organizes assignments into four standardized categories, each with a fixed weight contributing to the final course grade.

#### Category Definitions

**Homework (weight: 20%)**
- Regular assignments completed outside of class
- Multiple homework assignments per course
- Contributes one-fifth of the final grade
- Assesses ongoing understanding and practice

**Quizzes (weight: 20%)**
- Short assessments administered during the term
- Multiple quiz opportunities per course
- Contributes one-fifth of the final grade
- Measures incremental learning and retention

**Midterm Exam (weight: 25%)**
- Comprehensive assessment at mid-point of course
- Typically one midterm examination per course
- Contributes one-quarter of the final grade
- Evaluates cumulative understanding to midterm

**Final Exam (weight: 35%)**
- Comprehensive assessment at end of course
- One final examination per course
- Contributes over one-third of the final grade
- Evaluates complete course mastery

### Weight Distribution

Total weight allocation sums to 100%:
- Homework: 20%
- Quizzes: 20%
- Midterm: 25%
- Final Exam: 35%
- Combined: 100%

### Weight Rationale

The weight distribution reflects:
- Greater emphasis on comprehensive assessments (Midterm + Final = 60%)
- Balanced consideration of ongoing work (Homework + Quizzes = 40%)
- Increased weight for final exam recognizing complete course mastery
- Standard academic weighting practices

---

## 3. Letter Grade Scale

### Grade Ranges

Letter grades are assigned based on final percentage scores using standard academic thresholds.

**A Grade: 90.0% to 100.0%**
- Represents excellent performance
- Demonstrates comprehensive mastery
- GPA equivalent: 4.0 points

**B Grade: 80.0% to 89.9%**
- Represents good performance
- Demonstrates solid understanding
- GPA equivalent: 3.0 points

**C Grade: 70.0% to 79.9%**
- Represents satisfactory performance
- Demonstrates adequate understanding
- GPA equivalent: 2.0 points

**D Grade: 60.0% to 69.9%**
- Represents minimal passing performance
- Demonstrates basic understanding
- GPA equivalent: 1.0 point

**F Grade: Below 60.0%**
- Represents failing performance
- Does not demonstrate sufficient understanding
- GPA equivalent: 0.0 points

### Boundary Handling

When a percentage falls exactly on a boundary:
- 90.0% is an A (inclusive lower bound)
- 80.0% is a B (inclusive lower bound)
- 70.0% is a C (inclusive lower bound)
- 60.0% is a D (inclusive lower bound)
- Below 60.0% is an F

### Grade Characteristics

**No Plus/Minus Grades**:
- System uses five-letter grades only (A, B, C, D, F)
- No A+, A-, B+, B-, etc.
- Simplifies GPA calculation
- Provides clear grade boundaries

**Percentage Precision**:
- Percentages calculated to arbitrary decimal precision
- Letter grade assignment uses exact decimal values
- No rounding before letter grade conversion

---

## 4. Grade Point Average (GPA) System

### GPA Scale

The system uses the standard 4.0 GPA scale:
- A = 4.0 grade points
- B = 3.0 grade points
- C = 2.0 grade points
- D = 1.0 grade point
- F = 0.0 grade points

### GPA Purpose

Grade Point Average serves multiple purposes:
- Summarizes overall academic performance
- Enables comparison across students
- Determines academic standing
- Identifies students for recognition or intervention

### GPA Calculation Method

GPA is calculated using credit-hour weighting:
1. Each course contributes grade points multiplied by credit hours
2. Total grade points summed across all courses
3. Divided by total credit hours attempted
4. Result rounded to two decimal places

### GPA Range

Possible GPA values range from 0.00 to 4.00:
- 4.00: Perfect (all A grades)
- 3.00: All B grades
- 2.00: All C grades
- 1.00: All D grades
- 0.00: All F grades
- Values between these represent mixed performance

---

## 5. Academic Standing Classifications

### Classification System

Students are classified into academic standing categories based on cumulative GPA.

#### Summa Cum Laude (Highest Honors)
- GPA Requirement: 3.75 or higher
- Recognition: Highest academic distinction
- Represents exceptional academic achievement
- Top tier of academic performance

#### Magna Cum Laude (High Honors)
- GPA Requirement: 3.50 to 3.74 (inclusive)
- Recognition: High academic distinction
- Represents excellent academic achievement
- Second tier of academic honors

#### Cum Laude (Honors)
- GPA Requirement: 3.25 to 3.49 (inclusive)
- Recognition: Academic distinction
- Represents very good academic achievement
- Third tier of academic honors

#### Honors (General Honor Roll)
- GPA Requirement: 3.25 or higher
- Recognition: Honor roll designation
- Includes all three Latin honor categories
- Qualifies for Dean's List recognition

#### Good Standing (Satisfactory Progress)
- GPA Requirement: 2.00 to 3.24 (inclusive)
- Status: Normal academic standing
- Making satisfactory academic progress
- No special recognition or intervention

#### Academic Probation (At Risk)
- GPA Requirement: Below 2.00 (but above 0.00)
- Status: Requires academic intervention
- Indicates risk of academic failure
- May require support services or course load reduction

### Classification Boundaries

Exact GPA boundary values:
- 3.75: Threshold for Summa Cum Laude
- 3.50: Threshold for Magna Cum Laude
- 3.25: Threshold for Cum Laude and Honors
- 2.00: Threshold for Good Standing

When GPA equals boundary value:
- Student receives the higher classification
- Example: 3.75 qualifies for Summa Cum Laude
- Example: 3.25 qualifies for Honors
- Example: 2.00 qualifies for Good Standing

---

## 6. Grade Calculation Process

### Step 1: Calculate Assignment Percentages

For each assignment:
1. Divide earned points by total possible points
2. Multiply by 100 to get percentage
3. Store with arbitrary decimal precision

Example:
- Assignment: 85 points earned out of 100 possible
- Percentage: (85 / 100) * 100 = 85.0%

### Step 2: Calculate Category Averages

For each category with assignments:
1. Sum all assignment percentages in the category
2. Divide by number of assignments in the category
3. Result is the category average percentage

Example:
- Homework assignments: 90%, 85%, 95%
- Homework average: (90 + 85 + 95) / 3 = 90.0%

### Step 3: Apply Category Weights

For each category with assignments:
1. Multiply category average by category weight
2. Sum all weighted category values
3. Track total weight used (sum of weights for categories with assignments)

Example:
- Homework average: 90.0% * 0.20 = 18.0
- Quiz average: 85.0% * 0.20 = 17.0
- Midterm average: 88.0% * 0.25 = 22.0
- Final average: 92.0% * 0.35 = 32.2
- Weighted sum: 18.0 + 17.0 + 22.0 + 32.2 = 89.2
- Total weight: 0.20 + 0.20 + 0.25 + 0.35 = 1.00

### Step 4: Normalize Final Percentage

1. Divide weighted sum by total weight used
2. Result is the final course percentage

Example:
- Weighted sum: 89.2
- Total weight: 1.00
- Final percentage: 89.2 / 1.00 = 89.2%

### Step 5: Assign Letter Grade

1. Compare final percentage to grade scale thresholds
2. Assign corresponding letter grade
3. Map letter grade to GPA points

Example:
- Final percentage: 89.2%
- Grade range: 80.0% to 89.9% = B
- Letter grade: B
- GPA points: 3.0

---

## 7. GPA Calculation Process

### Step 1: Calculate Course Grades

For each enrolled course:
1. Calculate final percentage using grade calculation process
2. Convert percentage to letter grade
3. Map letter grade to GPA points

### Step 2: Apply Credit Hour Weights

For each course:
1. Multiply GPA points by course credit hours
2. Sum all grade points across courses
3. Sum all credit hours across courses

Example:
- Course 1: B grade (3.0 points) * 3 credits = 9.0 grade points
- Course 2: A grade (4.0 points) * 4 credits = 16.0 grade points
- Total grade points: 9.0 + 16.0 = 25.0
- Total credit hours: 3 + 4 = 7

### Step 3: Calculate Cumulative GPA

1. Divide total grade points by total credit hours
2. Round result to two decimal places

Example:
- Total grade points: 25.0
- Total credit hours: 7
- GPA: 25.0 / 7 = 3.571428...
- Rounded GPA: 3.57

### Step 4: Determine Academic Standing

1. Compare GPA to classification thresholds
2. Assign appropriate academic standing
3. Determine honors, probation, or good standing status

Example:
- GPA: 3.57
- Classification: Magna Cum Laude (3.50 to 3.74)
- Status: Honors (>= 3.25)

---

## 8. Special Cases and Edge Scenarios

### Missing Categories

When a category has no assignments:
1. Category is excluded from weighted sum
2. Category weight is excluded from total weight
3. Remaining categories are normalized

Example:
- Homework: 90.0% * 0.20 = 18.0
- Quizzes: 85.0% * 0.20 = 17.0
- Midterm: missing (excluded)
- Final: 92.0% * 0.35 = 32.2
- Weighted sum: 18.0 + 17.0 + 32.2 = 67.2
- Total weight: 0.20 + 0.20 + 0.35 = 0.75
- Normalized: 67.2 / 0.75 = 89.6%

### Empty Courses

When a course has no assignments:
- Category averages return 0.0%
- Final percentage returns 0.0%
- Letter grade: F (0.0 GPA points)

### Students with No Courses

When a student has no enrolled courses:
- GPA calculation returns 0.00
- Academic standing: Not classified
- Total credit hours: 0

### Perfect Scores

When all assignments are 100%:
- All category averages: 100.0%
- Final percentage: 100.0%
- Letter grade: A (4.0 GPA points)

### Failing Scores

When all assignments are below 60%:
- Category averages below 60.0%
- Final percentage below 60.0%
- Letter grade: F (0.0 GPA points)

### Single Assignment Categories

When a category has one assignment:
- Category average equals that assignment's percentage
- Weight applied normally
- No averaging needed

### Varying Point Values

When assignments have different total points:
- Each assignment percentage calculated independently
- Percentages averaged equally (not weighted by points)
- Category average treats all assignments equally

Example:
- Assignment 1: 45/50 = 90.0%
- Assignment 2: 38/40 = 95.0%
- Category average: (90.0 + 95.0) / 2 = 92.5%

---

## 9. Rounding and Precision Rules

### Calculation Precision

**During Calculations**:
- All intermediate calculations use full decimal precision
- No rounding during computation
- Prevents cumulative rounding errors

**Final GPA Display**:
- GPA rounded to two decimal places
- Uses standard rounding rules (0.005 rounds up)
- Example: 3.454 rounds to 3.45, 3.455 rounds to 3.46

**Percentage Display**:
- Percentages displayed with two decimal places
- Example: 89.234% displayed as 89.23%
- Letter grade assignment uses unrounded value

### Boundary Rounding

**Grade Boundaries**:
- No rounding before letter grade assignment
- 89.9999% is still a B (not rounded to 90% for A)
- 79.9999% is still a C (not rounded to 80% for B)
- Exact boundaries strictly enforced

**GPA Boundaries**:
- Academic standing determined after rounding
- 3.245 rounds to 3.25, qualifies for Honors
- 3.244 rounds to 3.24, does not qualify for Honors

---

## 10. Academic Recognition Programs

### Dean's List

**Eligibility Criteria**:
- Cumulative GPA of 3.50 or higher
- Includes Summa Cum Laude and Magna Cum Laude students
- Updated each time GPA is recalculated

**Recognition**:
- Listed in Dean's List student roster
- Academic achievement acknowledgment
- May receive special communication or certificate

### Honor Roll

**Eligibility Criteria**:
- Cumulative GPA of 3.25 or higher
- Includes all Latin honor categories
- Broader recognition than Dean's List

**Recognition**:
- Listed in honor roll roster
- Academic achievement acknowledgment
- Demonstrates above-average performance

### Academic Probation List

**Criteria**:
- Cumulative GPA below 2.00
- GPA must be greater than 0.00 (enrolled courses)
- Indicates need for academic support

**Intervention**:
- Listed in probation roster
- May receive academic advising
- May have registration restrictions
- Required to improve GPA to good standing

### Recognition Updates

**Automatic Updates**:
- Academic standing recalculated when:
  - New grades are added
  - Course grades are updated
  - GPA changes
- Lists updated automatically
- No manual intervention required

---

## Policy Rationale

### Standard Academic Practices

This grading policy follows widely accepted academic standards:
- 4.0 GPA scale is standard in higher education
- Letter grade ranges are traditional and well-understood
- Category weighting emphasizes comprehensive assessments
- Latin honors reflect common academic recognition

### Fairness and Transparency

The policy ensures fairness through:
- Consistent application across all students and courses
- Clear, documented calculation methods
- Objective criteria with no subjective adjustments
- Automatic calculations eliminate human error

### Flexibility with Missing Data

The normalization approach handles missing categories fairly:
- Students not penalized for missing categories
- Relative importance of present categories maintained
- Flexible enough for various course structures
- Mathematically sound weight redistribution

---

## Policy Review and Updates

### Current Status

This policy document reflects the grading system as implemented in version 1.0.0 of the Student Gradebook System.

### Future Considerations

Potential future policy enhancements:
- Configurable category weights per course
- Plus/minus grading (A+, A-, B+, B-, etc.)
- Alternative GPA scales (5.0 scale with weighted courses)
- Drop lowest assignment per category
- Extra credit handling
- Grade replacement policies

### Policy Changes

Any changes to this grading policy require:
- Documentation of rationale for change
- Update to this policy document
- Update to system implementation
- Communication to all stakeholders
- Version increment

---

## Conclusion

This grading policy provides a comprehensive, fair, and transparent framework for evaluating student academic performance. The standardized calculation methods ensure consistency and accuracy while the classification system recognizes achievement and identifies students needing support. All calculations follow well-established academic practices and are fully automated to eliminate errors and ensure fairness.
