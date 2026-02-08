# Banking System - Architecture & Design

## Table of Contents
1. [System Architecture](#system-architecture)
2. [Design Patterns](#design-patterns)
3. [Class Design](#class-design)
4. [Data Flow](#data-flow)
5. [Thread Safety](#thread-safety)
6. [Design Decisions](#design-decisions)
7. [Trade-offs](#trade-offs)

---

## System Architecture

### Layered Architecture

The system follows a clean layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│          Presentation Layer             │
│         (Main.java - CLI)               │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│          Service Layer                  │
│    (Bank, TransactionValidator)         │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│          Domain Model Layer             │
│  (Account, Transaction, Enums)          │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│          Utility Layer                  │
│  (IdGenerator, Logger, ConsoleColors)   │
└─────────────────────────────────────────┘
```

### Package Structure

```
com.prathamesh.banking/
├── model/              # Domain models (entities)
│   ├── Account
│   ├── Transaction
│   ├── AccountType (enum)
│   ├── TransactionType (enum)
│   └── TransactionStatus (enum)
├── service/            # Business logic
│   ├── Bank
│   └── TransactionValidator
├── util/               # Cross-cutting utilities
│   ├── IdGenerator
│   ├── Logger
│   └── ConsoleColors
└── Main                # Application entry point
```

---

## Design Patterns

### 1. Builder Pattern

**Used In**: `Transaction` class

**Purpose**: Construct immutable transaction objects with many optional fields

**Implementation**:
```java
Transaction transaction = new Transaction.Builder()
    .transactionId(id)
    .type(TransactionType.DEPOSIT)
    .amount(amount)
    .balanceBefore(balanceBefore)
    .balanceAfter(balanceAfter)
    .status(TransactionStatus.SUCCESS)
    .build();
```

**Benefits**:
- Ensures transaction immutability (no setters)
- Validates all fields during construction
- Provides fluent, readable API
- Mandatory validation in build() method

### 2. Strategy Pattern

**Used In**: `AccountType` enum with polymorphic behavior in `Account`

**Purpose**: Encapsulate account-specific business rules

**Implementation**:
```java
public enum AccountType {
    CHECKING,  // Strategy: fee after 10 transactions
    SAVINGS    // Strategy: minimum balance, interest, withdrawal limit
}
```

**Benefits**:
- Easy to add new account types
- Business rules isolated by type
- Open/Closed principle adherence
- Type-safe enumeration

### 3. Repository Pattern

**Used In**: `Bank` class acts as an in-memory repository

**Purpose**: Abstract data access and provide centralized account management

**Implementation**:
```java
public class Bank {
    private final Map<String, Account> accounts;
    
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
}
```

**Benefits**:
- Single source of truth for account data
- Easy to replace with database later
- Centralized validation and business logic
- Thread-safe operations

### 4. Factory Pattern

**Used In**: `IdGenerator` utility class

**Purpose**: Centralize ID generation logic with consistent formats

**Implementation**:
```java
public static String generateAccountNumber() {
    return String.format("ACC-%08d", accountCounter.incrementAndGet());
}

public static String generateTransactionId() {
    return String.format("TXN-%d-%s", 
        System.currentTimeMillis(), 
        UUID.randomUUID().toString().substring(0, 8));
}
```

**Benefits**:
- Guaranteed unique IDs
- Thread-safe generation
- Consistent format across system
- Easy to modify generation strategy

### 5. Dependency Injection

**Used In**: `Bank` class with `TransactionValidator`

**Purpose**: Decouple validation logic from business logic

**Implementation**:
```java
public class Bank {
    private final TransactionValidator validator;
    
    public Bank() {
        this.validator = new TransactionValidator();
    }
}
```

**Benefits**:
- Testability (can inject mock validator)
- Single Responsibility Principle
- Easy to extend validation rules
- Clear separation of concerns

---

## Class Design

### Core Classes

#### 1. Account

**Responsibilities**:
- Store account state (balance, counters)
- Enforce business rules (minimum balance, limits)
- Maintain transaction history
- Calculate fees and interest

**Key Design Decisions**:
- Mutable state (balance changes over time)
- Defensive copying for transaction history
- Business rule constants as static finals
- Package-private mutators (only Bank can modify)

**Methods**:
```java
// Query methods (public)
+ getAccountNumber(): String
+ getBalance(): BigDecimal
+ getTransactionHistory(): List<Transaction>

// Command methods (package-private)
~ deposit(amount): BigDecimal
~ withdraw(amount): BigDecimal
~ applyMonthlyFee(): BigDecimal
~ applyMonthlyInterest(): BigDecimal
~ resetMonthlyCounters(): void

// Validation
+ canWithdraw(amount): boolean
```

#### 2. Transaction

**Responsibilities**:
- Immutable record of a banking transaction
- Complete audit trail with all details
- Validation of required fields

**Key Design Decisions**:
- Fully immutable (no setters)
- Builder pattern for construction
- Validation in builder's build() method
- Equals/hashCode based on transactionId only

**Fields**:
```java
- transactionId: String (unique)
- timestamp: LocalDateTime
- type: TransactionType
- amount: BigDecimal
- balanceBefore: BigDecimal
- balanceAfter: BigDecimal
- status: TransactionStatus
- failureReason: String (nullable)
```

#### 3. Bank

**Responsibilities**:
- Manage account lifecycle (create, close)
- Process transactions (deposit, withdraw, transfer)
- Coordinate monthly processing
- Generate statements

**Key Design Decisions**:
- ConcurrentHashMap for thread-safe storage
- Synchronized operations on accounts
- Ordered locking for transfers (deadlock prevention)
- Failed transactions still recorded for audit

**Critical Methods**:
```java
// Account management
+ openAccount(name, type, initialDeposit): Account
+ closeAccount(accountNumber): boolean
+ getAccount(accountNumber): Account

// Transactions
+ deposit(accountNumber, amount): Transaction
+ withdraw(accountNumber, amount): Transaction
+ transfer(from, to, amount): Transaction[]

// Reporting
+ getTransactionHistory(accountNumber, startDate, endDate): List<Transaction>
+ generateMonthlyStatement(accountNumber): String

// Monthly processing
+ applyMonthlyInterest(): BigDecimal
+ applyMonthlyFeesAndResetCounters(): void
```

#### 4. TransactionValidator

**Responsibilities**:
- Centralize all validation logic
- Return descriptive error messages
- No state (pure functions)

**Key Design Decisions**:
- Stateless (all methods static-like)
- Returns null for valid, String message for invalid
- Comprehensive validation for each operation
- Reusable across different contexts

**Methods**:
```java
+ validateDeposit(amount): String
+ validateWithdrawal(account, amount): String
+ validateTransfer(from, to, amount): String
+ validateAccountClosure(account): String
+ validateInitialDeposit(type, amount): String
+ validateCustomerName(name): String
```

---

## Data Flow

### 1. Deposit Transaction Flow

```
User Request (amount)
    ↓
Bank.deposit()
    ↓
TransactionValidator.validateDeposit()
    ↓
[Valid?] → Yes → synchronized(account)
    ↓               ↓
    No              Account.deposit()
    ↓               ↓
Record Failed     Update balance
Transaction       ↓
    ↓            Create Transaction
    ↓               ↓
    └──────→    Add to history
                    ↓
                Return Transaction
```

### 2. Transfer Transaction Flow

```
User Request (from, to, amount)
    ↓
Bank.transfer()
    ↓
Get both accounts (or throw)
    ↓
TransactionValidator.validateTransfer()
    ↓
[Valid?] → Yes → Order accounts (deadlock prevention)
    ↓               ↓
    No          synchronized(firstAccount)
    ↓               ↓
Record Failed   synchronized(secondAccount)
Transaction         ↓
    ↓           Withdraw from source
    ↓               ↓
    └──────→    Deposit to destination
                    ↓
                Create 2 transactions
                    ↓
                Add to both histories
                    ↓
                Return Transaction[]
```

### 3. Monthly Processing Flow

```
Bank.applyMonthlyInterest()
    ↓
For each SAVINGS account
    ↓
synchronized(account)
    ↓
Calculate interest (balance * 0.02)
    ↓
Account.applyMonthlyInterest()
    ↓
Record interest transaction
    ↓
Accumulate total interest
    ↓
Return total

Bank.applyMonthlyFeesAndResetCounters()
    ↓
For each CHECKING account
    ↓
synchronized(account)
    ↓
Calculate fees (transactions > 10)
    ↓
Account.applyMonthlyFee()
    ↓
Account.resetMonthlyCounters()
    ↓
For each SAVINGS account
    ↓
Account.resetMonthlyCounters()
```

---

## Thread Safety

### Concurrency Strategy

#### 1. ConcurrentHashMap for Accounts
```java
private final Map<String, Account> accounts = new ConcurrentHashMap<>();
```
**Why**: 
- Thread-safe reads without locks
- Safe concurrent modifications
- Better performance than synchronized HashMap

#### 2. Synchronized Account Operations
```java
public Transaction deposit(String accountNumber, BigDecimal amount) {
    Account account = getAccountOrThrow(accountNumber);
    synchronized (account) {
        // Modify account state
    }
}
```
**Why**:
- Prevents concurrent modifications to same account
- Ensures atomic balance updates
- Maintains transaction history consistency

#### 3. Deadlock Prevention in Transfers
```java
// Always lock in same order regardless of transfer direction
Account firstLock = fromAccount.compareTo(toAccount) < 0 
    ? fromAccount : toAccount;
Account secondLock = fromAccount.compareTo(toAccount) < 0 
    ? toAccount : fromAccount;

synchronized (firstLock) {
    synchronized (secondLock) {
        // Execute transfer
    }
}
```
**Why**:
- Prevents circular wait condition
- Ensures consistent lock ordering
- Allows concurrent transfers between different account pairs

#### 4. AtomicLong for ID Generation
```java
private static final AtomicLong accountCounter = new AtomicLong(0);

public static String generateAccountNumber() {
    return String.format("ACC-%08d", accountCounter.incrementAndGet());
}
```
**Why**:
- Lock-free thread-safe increment
- No contention for ID generation
- Better performance than synchronized

### Thread Safety Analysis

| Component | Strategy | Reason |
|-----------|----------|--------|
| Bank.accounts | ConcurrentHashMap | Thread-safe read/write |
| Account operations | synchronized(account) | Atomic state changes |
| Transfer | Ordered locking | Deadlock prevention |
| ID generation | AtomicLong | Lock-free increments |
| Transaction | Immutable | Inherently thread-safe |

---

## Design Decisions

### 1. BigDecimal for Monetary Values

**Decision**: Use `BigDecimal` instead of `double` or `float`

**Rationale**:
- Precise decimal arithmetic (no rounding errors)
- Required for financial applications
- Industry standard for banking

**Example**:
```java
// Wrong: float balance = 100.10f + 0.20f; // May be 100.30000001
BigDecimal balance = new BigDecimal("100.10")
    .add(new BigDecimal("0.20")); // Exactly 100.30
```

### 2. Immutable Transactions

**Decision**: Make Transaction objects immutable

**Rationale**:
- Audit trail must never change
- Thread-safe by design
- Prevents accidental modifications
- Clear intent in API design

**Implementation**:
- All fields `final`
- No setters
- Defensive copies for mutable fields
- Builder pattern for construction

### 3. Enums for Type Safety

**Decision**: Use enums for AccountType, TransactionType, TransactionStatus

**Rationale**:
- Type-safe at compile time
- Exhaustive switch statements
- Clear domain vocabulary
- Self-documenting code

### 4. Failed Transactions Recorded

**Decision**: Record failed transactions in history

**Rationale**:
- Complete audit trail
- Debugging and analysis
- Fraud detection patterns
- Regulatory compliance

### 5. Date Filtering in Repository

**Decision**: Filter transaction history by date in Bank, not Account

**Rationale**:
- Single Responsibility (Account stores, Bank queries)
- Easier to optimize in future
- Consistent with repository pattern
- Allows for database query optimization later

### 6. Package-Private Mutators

**Decision**: Account's deposit()/withdraw() are package-private

**Rationale**:
- Only Bank should modify accounts
- Prevents circumventing validation
- Clear API boundaries
- Enforces proper transaction recording

---

## Trade-offs

### 1. In-Memory Storage vs Database

**Current**: `ConcurrentHashMap<String, Account>`

**Pros**:
- Simple implementation
- Fast operations (O(1) lookups)
- No external dependencies
- Easy to test

**Cons**:
- Data lost on restart
- Limited by memory
- No ACID guarantees across JVM restarts
- No query capabilities

**Future**: Easy to replace with JPA/Hibernate repository

### 2. Synchronization Granularity

**Current**: Synchronized on individual accounts

**Pros**:
- Allows concurrent operations on different accounts
- Low contention for independent transactions
- Simple to reason about

**Cons**:
- Potential for deadlock in transfers (mitigated with ordered locking)
- Slightly more complex than global lock
- Object lock overhead

**Alternative**: Could use ReadWriteLock for better read concurrency

### 3. Transaction History Storage

**Current**: ArrayList in each Account

**Pros**:
- Simple implementation
- Fast insertion (O(1) amortized)
- Direct access to history

**Cons**:
- Unbounded growth
- Memory usage increases over time
- No pagination

**Future**: Could implement circular buffer or move to database

### 4. Validation Strategy

**Current**: Separate TransactionValidator class

**Pros**:
- Centralized validation logic
- Easy to test independently
- Reusable across operations
- Clear separation of concerns

**Cons**:
- Extra object creation
- Indirect relationship with Account
- Validation rules spread across validator and account

**Alternative**: Could use JSR-303 Bean Validation

### 5. Error Handling

**Current**: Failed transactions returned, not exceptions thrown

**Pros**:
- Control flow remains clear
- Easy to record failed attempts
- Caller chooses how to handle
- Consistent API

**Cons**:
- Caller must check status
- Mix of exceptions (account not found) and status (insufficient funds)
- More verbose calling code

**Alternative**: Could throw custom exceptions for all failures

---

## Extensibility Points

### 1. New Account Types

To add a new account type (e.g., BUSINESS):

1. Add enum value to `AccountType`
2. Add business rule constants in `Account`
3. Update validation in `TransactionValidator`
4. Add specific logic in Account methods (if needed)

### 2. New Transaction Types

To add a new transaction type (e.g., FEE_REVERSAL):

1. Add enum value to `TransactionType`
2. Add method in `Bank` class
3. Update statement generation
4. Add tests

### 3. Persistence Layer

To add database persistence:

1. Create `AccountRepository` interface
2. Implement with JPA/JDBC
3. Inject into `Bank` constructor
4. Replace `ConcurrentHashMap` with repository calls

### 4. Notification System

To add transaction notifications:

1. Create `NotificationService` interface
2. Inject into `Bank` constructor
3. Call after successful transactions
4. Implement email/SMS/push strategies

---

## Performance Characteristics

| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| openAccount | O(1) | O(1) | HashMap insertion |
| deposit/withdraw | O(1) | O(1) | Direct account access |
| transfer | O(1) | O(1) | Two account operations |
| getAccount | O(1) | O(1) | HashMap lookup |
| getTransactionHistory (all) | O(n) | O(n) | Copy n transactions |
| getTransactionHistory (date) | O(n) | O(m) | Filter n, return m |
| applyMonthlyInterest | O(n) | O(1) | Iterate all accounts |
| generateStatement | O(m) | O(m) | Format m transactions |

Where:
- n = total number of accounts
- m = number of transactions

---

## Code Quality Measures

### 1. SOLID Principles

- **S**ingle Responsibility: Each class has one reason to change
- **O**pen/Closed: Easy to extend account types without modifying existing code
- **L**iskov Substitution: All AccountType values can be used interchangeably
- **I**nterface Segregation: Small, focused interfaces (not heavily used due to simplicity)
- **D**ependency Inversion: Bank depends on abstraction (validator), not concretions

### 2. Clean Code Practices

- Meaningful names (getTransactionHistory, not getTH)
- Small methods (each method does one thing)
- Clear intent (self-documenting code)
- Comprehensive JavaDoc
- No magic numbers (constants with names)

### 3. Defensive Programming

- Null checks on all inputs
- Validation before state changes
- Immutable where appropriate
- Defensive copying of collections
- Clear error messages

---

## Future Enhancements

### Potential Improvements

1. **Database Integration**
   - Replace in-memory storage with JPA
   - Add caching layer
   - Implement query optimization

2. **REST API**
   - Add Spring Boot controllers
   - OpenAPI documentation
   - JWT authentication

3. **Event Sourcing**
   - Store all events (not just current state)
   - Event replay capability
   - Temporal queries

4. **Metrics & Monitoring**
   - Prometheus metrics
   - Health checks
   - Performance monitoring

5. **Advanced Features**
   - Overdraft protection
   - Scheduled transactions
   - Multiple currencies
   - Account linking

---

## Conclusion

This design balances:
-  Simplicity with extensibility
-  Performance with correctness
-  Thread safety with concurrency
-  Clean code with practical implementation

The architecture is production-ready for the current requirements and designed to evolve with future needs.
