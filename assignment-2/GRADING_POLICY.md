# Gradebook System - Grading Policy

## Table of Contents
1. [Grading Categories](#grading-categories)
2. [Letter Grade Scale](#letter-grade-scale)
3. [Calculation Methods](#calculation-methods)
4. [Edge Cases](#edge-cases)
5. [Business Rules](#business-rules)
6. [Quick Reference](#quick-reference)

---

## Grading Categories

### Default Category Weights

| Category     | Default Weight | Description              |
|--------------|----------------|--------------------------|
| HOMEWORK     | 20%            | Weekly assignments       |
| QUIZZES      | 20%            | Short assessments        |
| MIDTERM      | 25%            | Mid-semester exam        |
| FINAL_EXAM   | 35%            | Comprehensive final      |
| **TOTAL**    | **100%**       |                          |

### Custom Weights

Courses can define custom weights with requirements:
- All four categories must have assigned weights
- Weights must be positive integers
- Weights must sum to exactly 100

---

## Letter Grade Scale

| Letter | Percentage Range | GPA Value | Description   |
|--------|------------------|-----------|---------------|
| A      | 90.00 - 100.00   | 4.0       | Excellent     |
| B      | 80.00 - 89.99    | 3.0       | Good          |
| C      | 70.00 - 79.99    | 2.0       | Satisfactory  |
| D      | 60.00 - 69.69    | 1.0       | Passing       |
| F      | 0.00 - 59.99     | 0.0       | Failing       |

### Boundary Examples

| Percentage | Letter Grade | Rationale |
|-----------|--------------|-----------|
| 90.00%    | A            | Boundary inclusive |
| 89.99%    | B            | Just below boundary |
| 80.00%    | B            | Boundary inclusive |
| 79.99%    | C            | Just below boundary |

### Honor Roll

| Requirement | Value | Display |
|-------------|-------|---------|
| Minimum GPA | 3.50  | Badge on transcript |

---

## Calculation Methods

### Category Average

**Method:** Total points approach (NOT averaging percentages)

**Formula:**
```
Category Average = (Sum of Points Earned / Sum of Points Possible) × 100
```

**Example:**

| Assignment | Points Earned | Points Possible | Individual % |
|-----------|---------------|-----------------|--------------|
| HW1       | 45            | 50              | 90%          |
| HW2       | 48            | 50              | 96%          |
| **Total** | **93**        | **100**         | **93%**      |

**Why This Matters:**

| Approach | Assignment 1 | Assignment 2 | Result |
|----------|-------------|-------------|---------|
| **Wrong (Avg %)** | 5/10 = 50% | 19/20 = 95% | (50+95)/2 = 72.5% |
| **Correct (Total)** | 5/10 | 19/20 | 24/30 = 80% |

---

### Course Grade Calculation

#### All Categories Present

**Formula:**
```
Course Grade = Σ(Category Average × Category Weight)
```

**Example:**

| Category   | Average | Weight | Contribution |
|-----------|---------|--------|--------------|
| HOMEWORK  | 93%     | 20%    | 18.6%        |
| QUIZZES   | 92.5%   | 20%    | 18.5%        |
| MIDTERM   | 85%     | 25%    | 21.25%       |
| FINAL_EXAM| 90%     | 35%    | 31.5%        |
| **Total** |         |        | **89.85% (B)**|

---

#### Empty Category Handling

**Process:**
1. Identify non-empty categories
2. Sum their original weights
3. Calculate adjusted weight = (original / sum) × 100
4. Calculate weighted average with adjusted weights

**Example:** MIDTERM is empty (25% weight)

| Category   | Original | Adjusted | Calculation |
|-----------|----------|----------|-------------|
| HOMEWORK  | 20%      | 26.67%   | 20 / 75 × 100 |
| QUIZZES   | 20%      | 26.67%   | 20 / 75 × 100 |
| MIDTERM   | 25%      | Excluded | Empty category |
| FINAL_EXAM| 35%      | 46.67%   | 35 / 75 × 100 |
| **Total** | **75%**  | **100%** | Weights preserved |

**Course Grade with Adjusted Weights:**

| Category   | Average | Adjusted Weight | Contribution |
|-----------|---------|-----------------|--------------|
| HOMEWORK  | 85%     | 26.67%          | 22.67%       |
| QUIZZES   | 90%     | 26.67%          | 24.00%       |
| FINAL_EXAM| 88%     | 46.67%          | 41.07%       |
| **Total** |         |                 | **87.74% (B)**|

---

### GPA Calculation

**Formula:**
```
GPA = Sum of Quality Points / Sum of Credit Hours

where Quality Points = Course GPA Value × Course Credit Hours
```

**Example:**

| Course    | Credits | Grade  | Letter | GPA Value | Quality Points |
|-----------|---------|--------|--------|-----------|----------------|
| CS101     | 4       | 89.85% | B      | 3.0       | 12.0           |
| MATH201   | 4       | 92.50% | A      | 4.0       | 16.0           |
| ENG101    | 3       | 94.00% | A      | 4.0       | 12.0           |
| **Total** | **11**  |        |        |           | **40.0**       |

**GPA = 40.0 / 11 = 3.636**

---

## Edge Cases

### 1. Missing Assignments

| Policy | Implementation |
|--------|----------------|
| Treatment | Not explicitly added to gradebook |
| Effect | Only submitted assignments count |
| Rationale | No penalty for work not yet assigned |

**Note:** System counts only submitted assignments. To score missing work as zero, explicitly add 0-point assignments.

---

### 2. Empty Categories

| Scenario | Result |
|----------|--------|
| One empty category | Excluded, weights adjusted |
| Two empty categories | Both excluded, weights adjusted |
| All empty categories | Course grade = 0% (F) |

---

### 3. Empty Course

| Category Count | Grade Assigned |
|----------------|----------------|
| No assignments | 0% (F)         |
| Rationale | No work = failing grade |

---

### 4. Perfect Scores

| Scenario | Treatment |
|----------|-----------|
| 100% assignment | Normal processing |
| All perfect scores | Possible to achieve 100% course grade |

---

### 5. Invalid Operations

| Violation | Exception Thrown |
|-----------|------------------|
| Points earned > possible | InvalidGradeException |
| Points possible ≤ 0 | InvalidGradeException |
| Weights sum ≠ 100 | InvalidWeightException |

---

## Business Rules

### Student Rules

| Rule | Requirement | Example |
|------|-------------|---------|
| ID Format | \`S-XXXXXXXX\` | \`S-12345678\` |
| ID Pattern | S- + 8 digits | Valid: \`S-00000001\` |
| Name | Non-null, non-empty | Required |
| Duplicate Prevention | No duplicate IDs | Enforced |

---

### Course Rules

| Rule | Value/Range | Description |
|------|------------|-------------|
| Credit Hours | 1 - 6 | Industry standard range |
| Duplicate Enrollment | Not allowed | Per student |
| Category Weights | Sum to 100 | Exact validation |
| All Categories | Required | All four must be defined |

---

### Assignment Rules

| Validation | Rule |
|------------|------|
| Points Earned | ≥ 0 |
| Points Possible | > 0 |
| Points Relationship | earned ≤ possible |
| Category | Valid enum value |
| Name | Non-null, non-empty |

---

### GPA Rules

| Rule | Value |
|------|-------|
| Honor Roll | GPA ≥ 3.5 |
| Passing Grades | A, B, C, D (≥ 60%) |
| Failing Grade | F (< 60%) |
| No Courses | GPA = 0.0 |
| Calculation | Credit-hour weighted |

---

## Quick Reference

### HOMEWORK vs QUIZZES vs EXAMS

| Aspect | HOMEWORK | QUIZZES | MIDTERM | FINAL_EXAM |
|--------|----------|---------|---------|------------|
| Default Weight | 20% | 20% | 25% | 35% |
| Typical Use | Weekly work | Quick checks | Mid-semester | Comprehensive |
| Number of Items | Many | Several | One | One |

---

### Letter Grade GPA Mapping

| Letter | GPA | Letter | GPA |
|--------|-----|--------|-----|
| A | 4.0 | C | 2.0 |
| B | 3.0 | D | 1.0 |
|   |     | F | 0.0 |

---

### Credit Hour Standards

| Credits | Course Type |
|---------|-------------|
| 1 | Lab/Seminar |
| 2 | Half course |
| 3 | Standard course |
| 4 | Course with lab |
| 5-6 | Extended course |

---

### Exception Quick Reference

| Exception | Trigger |
|-----------|---------|
| InvalidGradeException | Points validation fails |
| InvalidWeightException | Weights ≠ 100 |
| InvalidCreditHoursException | Credits outside [1,6] |
| StudentNotFoundException | Student ID not found |
| CourseNotFoundException | Course not in student's list |
| DuplicateEnrollmentException | Already enrolled |

---

### Empty Category Weight Adjustment

| Empty Categories | Formula | Example |
|------------------|---------|---------|
| 0 | Weights as-is | 20+20+25+35=100 |
| 1 (MIDTERM 25%) | Adjust by 75% sum | 20/75, 20/75, 35/75 |
| 2 (MID+FINAL 60%) | Adjust by 40% sum | 20/40, 20/40 |
| 3 | Use single category | 100% weight |
| 4 | Grade = 0% | No work submitted |

---

For detailed architecture and design patterns, see [DESIGN.md](DESIGN.md).  
For test coverage details, see [TEST_COVERAGE_ANALYSIS.md](TEST_COVERAGE_ANALYSIS.md).
