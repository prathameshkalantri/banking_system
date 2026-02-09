# Advanced Banking System

A production-ready banking system implementation in Java demonstrating object-oriented design, transaction management, and comprehensive business rule enforcement.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Tests](https://img.shields.io/badge/tests-125%20passing-brightgreen)]()
[![Coverage](https://img.shields.io/badge/coverage-comprehensive-brightgreen)]()
[![Java](https://img.shields.io/badge/java-11-blue)]()
[![Maven](https://img.shields.io/badge/maven-3.6.3-blue)]()

## Project Overview

This banking system implements a complete account management solution with two account types (CHECKING and SAVINGS), three transaction types (DEPOSIT, WITHDRAWAL, TRANSFER), and comprehensive business rule enforcement. The system includes full audit trails, thread-safe operations, and an interactive demonstration interface.

### Key Features

- **Two Account Types** with distinct business rules
  - CHECKING: No minimum balance, transaction fees after 10 free transactions
  - SAVINGS: $100 minimum balance, 2% monthly interest, 5 withdrawal limit

- **Three Transaction Types** with full validation
  - DEPOSIT: Add funds with validation
  - WITHDRAWAL: Remove funds with business rule checks
  - TRANSFER: Move funds between accounts atomically

- **Complete Audit Trail**
  - Every transaction recorded with 7+ data points
  - Success/failure tracking with detailed reasons
  - Timestamp precision to milliseconds

- **Thread-Safe Operations**
  - Concurrent data structures
  - Synchronized account operations
  - Deadlock prevention for transfers

- **Professional Features**
  - Colored console output
  - Audit logging system
  - Interactive CLI
  - Monthly statement generation

## Requirements

- **Java**: 11 or higher
- **Maven**: 3.6.3 or higher
- **JUnit**: 5.10.0 (included in dependencies)

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd banking-system
mvn clean install
```

### 2. Run Tests

```bash
mvn test
```

**Expected Output:**
```
Tests run: 125, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 3. Run Demo

```bash
mvn exec:java -Dexec.mainClass="com.prathamesh.banking.Main"
```

The demo will automatically:
- Create 4 accounts (2 CHECKING, 2 SAVINGS)
- Perform 27+ transactions
- Demonstrate all business rules
- Show failed transaction handling
- Display monthly processing
- Generate account statements

## Project Structure

```
banking-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/prathamesh/banking/
│   │           ├── model/           # Domain models
│   │           │   ├── Account.java
│   │           │   ├── Transaction.java
│   │           │   ├── AccountType.java
│   │           │   ├── TransactionType.java
│   │           │   └── TransactionStatus.java
│   │           ├── service/         # Business logic
│   │           │   ├── Bank.java
│   │           │   └── TransactionValidator.java
│   │           ├── util/            # Utilities
│   │           │   ├── IdGenerator.java
│   │           │   ├── ConsoleColors.java
│   │           │   └── Logger.java
│   │           └── Main.java        # Demo application
│   └── test/
│       └── java/
│           └── com/prathamesh/banking/
│               ├── model/           # Model tests (52 tests)
│               ├── service/         # Service tests (64 tests)
│               └── util/            # Utility tests (9 tests)
├── pom.xml
├── README.md
├── DESIGN.md
├── TEST_COVERAGE_ANALYSIS.md
├── BUSINESS_RULES.md
└── REQUIREMENTS_VERIFICATION.md
```

## Usage Examples

### Opening an Account

Create a Bank instance and open accounts using the openAccount method with holder name, account type, and initial balance. CHECKING accounts have no minimum balance requirement, while SAVINGS accounts require at least $100.

### Making Deposits

Use the deposit method with account number and amount. The transaction is recorded with timestamp, and the method returns a Transaction object indicating success or failure.

### Making Withdrawals

Call the withdraw method with account number and amount. The system validates business rules (sufficient funds, minimum balance for SAVINGS, withdrawal limits) before processing. Failed transactions are recorded with detailed reasons.

### Transferring Funds

Use the transfer method with source account, destination account, and amount. Transfers are atomic operations that create withdrawal and deposit transactions. Both accounts are locked during transfer to prevent race conditions.

### Monthly Processing

Two methods handle monthly operations:
- applyMonthlyInterest(): Calculates and applies 2% interest to all SAVINGS accounts
- applyMonthlyFeesAndResetCounters(): Charges fees for excess transactions on CHECKING accounts and resets monthly counters

### Generating Statements

Call generateMonthlyStatement with account number to produce a formatted report showing account details, beginning balance, all transactions for the month, and ending balance.

## Interactive Mode

The demo application includes an interactive CLI. Run the Main class and choose 'y' when prompted for interactive mode.

Available commands:
- help: Show available commands
- list: List all accounts
- create: Create a new account
- deposit: Deposit money
- withdraw: Withdraw money
- transfer: Transfer between accounts
- statement: Generate statement
- monthly: Run monthly processing
- exit: Exit interactive mode

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

- **Total Tests**: 125
- **Model Tests**: 52 (Account, Transaction, Enums)
- **Service Tests**: 64 (Bank, Validator)
- **Utility Tests**: 9 (IdGenerator)

### Test Categories

1. **Unit Tests**: Individual class behavior
2. **Integration Tests**: Multi-class interactions
3. **Edge Case Tests**: Boundary conditions
4. **Business Rule Tests**: Validation enforcement
5. **Concurrency Tests**: Thread safety

Run with coverage report:
```bash
mvn clean test jacoco:report
```

## Architecture

### Design Patterns

- **Builder Pattern**: Immutable Transaction construction with fluent API
- **Enum-based Strategy**: AccountType (CHECKING/SAVINGS) determines business rule behavior
- **Repository Pattern**: Bank as central account store with ConcurrentHashMap
- **Factory Pattern**: IdGenerator for unique account and transaction IDs
- **Dependency Injection**: TransactionValidator injected into Bank service

### Design Principles

- **SOLID Principles**: Single responsibility, Open/closed, etc.
- **Immutability**: Transaction objects are immutable
- **Thread Safety**: Concurrent data structures and synchronization
- **Defensive Copying**: Collections protected from external modification
- **BigDecimal**: Financial precision for all monetary values

See [DESIGN.md](DESIGN.md) for detailed architecture documentation.

## Performance Considerations

- **Thread-Safe**: `ConcurrentHashMap` for account storage
- **Deadlock Prevention**: Ordered locking for transfers
- **Memory Efficient**: Transaction history uses ArrayList
- **Time Complexity**: O(1) for account lookups, O(n) for date-filtered history

## Error Handling

All business rule violations and validation errors result in:
- Failed transactions recorded in history
- Descriptive failure reasons
- No state changes to accounts
- Clear error messages returned to callers

Example error scenarios handled:
- Insufficient funds
- Minimum balance violations
- Withdrawal limit exceeded
- Invalid account numbers
- Negative amounts
- Same-account transfers

## Security Considerations

- Input validation on all public APIs
- Thread-safe operations prevent race conditions
- Immutable transactions prevent tampering
- Complete audit trail for compliance
- No sensitive data logged

## Logging

The system includes a custom logging utility with:
- Timestamp precision to milliseconds
- Log levels: DEBUG, INFO, WARN, ERROR
- Color-coded console output
- In-memory log storage for auditing

Use Logger.setEnabled(true) or Logger.setEnabled(false) to control logging output.

## Console Output

The demo uses ANSI color codes for enhanced readability:
- Green: Successful operations
- Red: Failed operations
- Yellow: Warnings
- Cyan: Informational messages

## Testing

Run specific test suites:

```bash
# All tests
mvn test

# Specific test class
mvn -Dtest=BankTest test

# Specific test method
mvn -Dtest=BankTest#testDeposit_ValidDeposit test

# Generate coverage report
mvn clean test jacoco:report
```

## Configuration

The system uses constants for business rules (easily configurable):

**Account.java configuration:**
- SAVINGS_MINIMUM_BALANCE = 100.00
- CHECKING_FEE_AMOUNT = 2.50
- CHECKING_FREE_TRANSACTIONS = 10
- SAVINGS_MAX_WITHDRAWALS = 5
- SAVINGS_INTEREST_RATE = 0.02 (2%)

## Documentation

- **[README.md](README.md)**: This file - project overview
- **[DESIGN.md](DESIGN.md)**: Architecture and design decisions
- **[TEST_COVERAGE_ANALYSIS.md](TEST_COVERAGE_ANALYSIS.md)**: Testing strategy
- **[BUSINESS_RULES.md](BUSINESS_RULES.md)**: Detailed business rules
- **[REQUIREMENTS_VERIFICATION.md](REQUIREMENTS_VERIFICATION.md)**: Compliance checklist

## Contributing

This is a demonstration project. For educational purposes:

1. Fork the repository
2. Create a feature branch
3. Add tests for new features
4. Ensure all tests pass
5. Submit a pull request

## License

This project is created for educational and demonstration purposes.

## Author

**Prathamesh Kalantri**
- Banking System Implementation
- Version 1.0.0
- Date: February 7, 2026

## Acknowledgments

- Built following industry best practices
- Inspired by real-world banking systems
- Designed for Intuit Build Challenge

## Support

For questions or issues:
1. Review the documentation files
2. Check the test suite for examples
3. Run the interactive demo for hands-on learning

---

**Status**: Production Ready | 125 Tests Passing | Comprehensive Coverage | Best Practices Followed
