# Banking System

A production-ready banking system implementation in Java demonstrating domain-driven design, strategy pattern, and comprehensive business rule enforcement.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Tests](https://img.shields.io/badge/tests-232%20passing-brightgreen)]()
[![Coverage](https://img.shields.io/badge/coverage-comprehensive-brightgreen)]()
[![Java](https://img.shields.io/badge/java-11-blue)]()
[![Maven](https://img.shields.io/badge/maven-3.6+-blue)]()

## Project Overview

This banking system implements a complete account management solution with two account types (CHECKING and SAVINGS), full transaction support (DEPOSIT, WITHDRAWAL, TRANSFER), and comprehensive business rule enforcement. The system includes complete audit trails, atomic transfers, and monthly processing for fees and interest.

### Key Features

- **Two Account Types** with distinct business rules
  - CHECKING: No minimum balance, $2.50 fee per transaction after 10 free
  - SAVINGS: $100 minimum balance, 2% monthly interest, 5 withdrawal limit

- **Full Transaction Support** with validation
  - DEPOSIT: Add funds with positive amount validation
  - WITHDRAWAL: Remove funds with business rule enforcement
  - TRANSFER: Move funds between accounts atomically

- **Complete Audit Trail**
  - Every transaction recorded with SUCCESS/FAILED status
  - Failed transactions include detailed failure reasons
  - Full transaction history per account

- **Strategy Pattern Architecture**
  - Account-type-specific rules encapsulated in strategies
  - Easy to extend with new account types
  - Clean separation of concerns

- **Monthly Processing**
  - Automatic fee calculation for CHECKING accounts
  - Interest application for SAVINGS accounts
  - Counter reset for new billing cycle

## Requirements

- **Java**: 11 or higher
- **Maven**: 3.6 or higher
- **JUnit**: 5.10.1 (included in dependencies)

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd assignment-1
mvn clean install
```

### 2. Run Tests

```bash
mvn test
```

**Expected Output:**
```
Tests run: 232, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 3. Run Demo

```bash
mvn exec:java -Dexec.mainClass="com.prathamesh.banking.Main"
```

The demo will automatically:
- Create 4 accounts (2 CHECKING, 2 SAVINGS)
- Perform 10+ successful transactions
- Demonstrate failed transaction handling
- Execute transfers between accounts
- Apply monthly processing (fees and interest)
- Generate account statements

## Project Structure

```
assignment-1/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/prathamesh/banking/
│   │           ├── domain/          # Core entities
│   │           │   ├── Account.java
│   │           │   ├── Transaction.java
│   │           │   ├── AccountType.java
│   │           │   ├── TransactionType.java
│   │           │   └── TransactionStatus.java
│   │           ├── service/         # Business logic
│   │           │   └── Bank.java
│   │           ├── strategy/        # Account strategies
│   │           │   ├── AccountStrategy.java
│   │           │   ├── CheckingAccountStrategy.java
│   │           │   └── SavingsAccountStrategy.java
│   │           ├── exception/       # Domain exceptions
│   │           │   └── ... (5 exception classes)
│   │           ├── util/            # Utilities
│   │           │   ├── IdGenerator.java
│   │           │   └── ConsoleFormatter.java
│   │           └── Main.java        # Demo application
│   └── test/
│       └── java/
│           └── com/prathamesh/banking/
│               ├── model/           # Domain tests (90 tests)
│               ├── service/         # Service tests (36 tests)
│               ├── strategy/        # Strategy tests (49 tests)
│               ├── util/            # Utility tests (45 tests)
│               └── integration/     # Integration tests (12 tests)
├── pom.xml
├── README.md
├── DESIGN.md
├── BUSINESS_RULES.md
└── TEST_COVERAGE_ANALYSIS.md
```

## Usage Examples

### Opening an Account

Create a Bank instance and open accounts using the openAccount method with customer name, account type, and initial deposit. CHECKING accounts have no minimum balance requirement, while SAVINGS accounts require at least $100.

### Making Deposits

Use the deposit method with account number and amount. The transaction is recorded with timestamp, and the method returns a Transaction object indicating success or failure.

### Making Withdrawals

Call the withdraw method with account number and amount. The system validates business rules (sufficient funds, minimum balance for SAVINGS, withdrawal limits) before processing. Failed transactions are recorded with detailed reasons.

### Transferring Funds

Use the transfer method with source account, destination account, and amount. Transfers are atomic operations that validate the source account before executing. Both accounts record TRANSFER transactions.

### Monthly Processing

Call processMonthlyAdjustments to apply fees and interest to all accounts. CHECKING accounts are charged for excess transactions, SAVINGS accounts receive interest, and all monthly counters are reset.

## Business Rules

### CHECKING Account Rules

- **No minimum balance** requirement
- **First 10 transactions** per month are free
- **$2.50 fee** charged for each transaction after the 10th
- No interest earned
- Unlimited transactions allowed

### SAVINGS Account Rules

- **$100 minimum balance** must be maintained
- **2% monthly interest** applied to the balance
- **Maximum 5 withdrawals** per month
- No transaction fees
- Withdrawals that would drop balance below $100 are rejected

### Transaction Validation

- All amounts must be positive
- Withdrawals require sufficient funds
- Transfers cannot be to the same account
- Account closure requires zero balance
- SAVINGS initial deposit must be >= $100

## Test Coverage

- **Total Tests**: 232
- **Model Tests**: 90 (Account, Transaction, Enums)
- **Service Tests**: 36 (Bank)
- **Strategy Tests**: 49 (Checking, Savings)
- **Utility Tests**: 45 (IdGenerator, ConsoleFormatter)
- **Integration Tests**: 12 (End-to-end workflows)

### Test Categories

1. **Unit Tests**: Individual class behavior
2. **Strategy Tests**: Business rule enforcement
3. **Integration Tests**: Multi-class interactions
4. **Edge Case Tests**: Boundary conditions
5. **Failure Tests**: Error handling verification

Run tests:
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=BankTest

# Specific test method
mvn test -Dtest=BankTest#testOpenCheckingAccount
```

## Architecture

### Design Patterns

- **Strategy Pattern**: AccountStrategy interface with Checking/Savings implementations
- **Builder Pattern**: Immutable Transaction construction with fluent API
- **Factory Pattern**: AccountStrategyFactory for strategy selection
- **Repository Pattern**: Bank as central account store
- **Value Object Pattern**: Transaction as immutable event record

### Design Principles

- **SOLID Principles**: Single responsibility, Open/closed, Liskov substitution, Interface segregation, Dependency inversion
- **Encapsulation**: State changes through controlled methods only
- **Immutability**: Transaction objects are immutable
- **Composition**: Account owns transaction history
- **BigDecimal**: Financial precision for all monetary values

See [DESIGN.md](DESIGN.md) for detailed architecture documentation.

## Error Handling

All business rule violations result in:
- Failed transactions recorded in history
- Descriptive failure reasons
- No state changes to accounts
- Clear exceptions returned to callers

Example error scenarios handled:
- Insufficient funds
- Minimum balance violations
- Withdrawal limit exceeded
- Invalid account numbers
- Negative amounts
- Same-account transfers
- Operations on closed accounts

## Documentation

- **[README.md](README.md)**: This file - project overview
- **[DESIGN.md](DESIGN.md)**: Architecture and design decisions
- **[BUSINESS_RULES.md](BUSINESS_RULES.md)**: Detailed business rules
- **[TEST_COVERAGE_ANALYSIS.md](TEST_COVERAGE_ANALYSIS.md)**: Testing strategy and coverage

## Author

**Prathamesh Kalantri**
- Banking System Implementation
- Version 1.0.0
- Date: February 8, 2026

---

**Status**: Production Ready | 232 Tests Passing | Comprehensive Coverage | Best Practices Followed
