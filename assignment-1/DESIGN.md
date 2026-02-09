# Banking System - Architecture & Design

## Table of Contents
1. [System Architecture](#system-architecture)
2. [Design Patterns](#design-patterns)
3. [Class Design](#class-design)
4. [Data Flow](#data-flow)
5. [Design Decisions](#design-decisions)
6. [Trade-offs](#trade-offs)

---

## System Architecture

### Layered Architecture

The system follows a clean layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│          Presentation Layer             │
│         (Main.java - Demo CLI)          │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│          Service Layer                  │
│              (Bank)                     │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│          Strategy Layer                 │
│  (AccountStrategy implementations)      │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│          Domain Model Layer             │
│  (Account, Transaction, Enums)          │
└─────────────────────────────────────────┘
                    │
┌─────────────────────────────────────────┐
│          Utility Layer                  │
│  (IdGenerator, ConsoleFormatter)        │
└─────────────────────────────────────────┘
```

### Package Structure

```
com.prathamesh.banking/
├── domain/             # Domain models (entities)
│   ├── Account
│   ├── Transaction
│   ├── AccountType (enum)
│   ├── TransactionType (enum)
│   └── TransactionStatus (enum)
├── service/            # Business logic
│   └── Bank
├── strategy/           # Account-specific rules
│   ├── AccountStrategy (interface)
│   ├── CheckingAccountStrategy
│   ├── SavingsAccountStrategy
│   └── AccountStrategyFactory
├── exception/          # Domain exceptions
│   ├── BankingException
│   ├── AccountNotFoundException
│   ├── InsufficientFundsException
│   ├── MinimumBalanceViolationException
│   └── WithdrawalLimitExceededException
├── util/               # Cross-cutting utilities
│   ├── IdGenerator
│   └── ConsoleFormatter
└── Main                # Application entry point
```

---

## Design Patterns

### 1. Strategy Pattern

**Used In**: `AccountStrategy` interface with `CheckingAccountStrategy` and `SavingsAccountStrategy`

**Purpose**: Encapsulate account-specific business rules

**Implementation**:
```java
public interface AccountStrategy {
    void validateWithdrawal(Account account, BigDecimal amount);
    void applyMonthlyAdjustments(Account account);
}

// Usage in Bank
AccountStrategy strategy = AccountStrategyFactory.getStrategy(account.getAccountType());
strategy.validateWithdrawal(account, amount);
```

**Benefits**:
- Easy to add new account types (just add new strategy)
- Business rules isolated by account type
- Open/Closed principle adherence
- Single Responsibility - each strategy handles one account type

### 2. Builder Pattern

**Used In**: `Transaction` class

**Purpose**: Construct immutable transaction objects with many fields

**Implementation**:
```java
Transaction transaction = Transaction.builder()
    .transactionId(IdGenerator.generateTransactionId())
    .type(TransactionType.DEPOSIT)
    .amount(amount)
    .status(TransactionStatus.SUCCESS)
    .balanceAfter(newBalance)
    .build();
```

**Benefits**:
- Ensures transaction immutability (no setters)
- Validates all required fields during construction
- Provides fluent, readable API
- Handles optional fields cleanly (failureReason)

### 3. Factory Pattern

**Used In**: `AccountStrategyFactory` and `IdGenerator`

**Purpose**: Centralize object creation logic

**Implementation**:
```java
public class AccountStrategyFactory {
    public static AccountStrategy getStrategy(AccountType type) {
        switch (type) {
            case CHECKING: return new CheckingAccountStrategy();
            case SAVINGS: return new SavingsAccountStrategy();
            default: throw new IllegalArgumentException("Unknown type");
        }
    }
}
```

**Benefits**:
- Decouples Bank from concrete strategy classes
- Centralized strategy creation
- Easy to extend with new account types

### 4. Repository Pattern

**Used In**: `Bank` class acts as an in-memory repository

**Purpose**: Abstract data access and provide centralized account management

**Implementation**:
```java
public class Bank {
    private final Map<String, Account> accounts = new HashMap<>();
    
    public Account getAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }
}
```

**Benefits**:
- Single source of truth for account data
- Easy to replace with database later
- Centralized validation and business logic

### 5. Value Object Pattern

**Used In**: `Transaction` class

**Purpose**: Immutable records representing domain events

**Implementation**:
- All fields are `final`
- No setter methods
- Created only through Builder
- Equals/HashCode based on transactionId

**Benefits**:
- Thread-safe by design
- Audit trail cannot be tampered
- Clear domain semantics

---

## Class Design

### Core Classes

#### 1. Account (Entity)

**Responsibilities**:
- Store account state (balance, counters)
- Maintain transaction history
- Track monthly counters

**Key Fields**:
| Field | Type | Purpose |
|-------|------|---------|
| accountNumber | String | Unique identifier (ACC-XXX) |
| customerName | String | Account holder name |
| accountType | AccountType | CHECKING or SAVINGS |
| balance | BigDecimal | Current balance |
| status | AccountStatus | ACTIVE or CLOSED |
| transactionHistory | List<Transaction> | All transactions |
| monthlyTransactionCount | int | Transactions this month |
| monthlyWithdrawalCount | int | Withdrawals this month (SAVINGS) |

**Key Design Decisions**:
- Mutable state (balance changes over time)
- Defensive copying for transaction history
- Counters managed internally, reset by strategy

#### 2. Transaction (Value Object)

**Responsibilities**:
- Record banking operation details
- Provide immutable audit record
- Store success/failure status with reasons

**Key Fields**:
| Field | Type | Purpose |
|-------|------|---------|
| transactionId | String | Unique ID (TXN-XXX) |
| type | TransactionType | DEPOSIT, WITHDRAWAL, etc. |
| amount | BigDecimal | Transaction amount |
| status | TransactionStatus | SUCCESS or FAILED |
| timestamp | LocalDateTime | When recorded |
| balanceAfter | BigDecimal | Balance after transaction |
| failureReason | String | Optional reason for FAILED |

**Key Design Decisions**:
- Immutable (all final fields, no setters)
- Builder pattern for construction
- Optional failureReason for FAILED transactions

#### 3. Bank (Service)

**Responsibilities**:
- Orchestrate all banking operations
- Maintain account repository
- Delegate to strategies for validation
- Ensure atomicity of transfers

**Key Operations**:
| Method | Description |
|--------|-------------|
| `openAccount()` | Create and register new account |
| `closeAccount()` | Close account (requires zero balance) |
| `deposit()` | Add funds to account |
| `withdraw()` | Remove funds (validated by strategy) |
| `transfer()` | Atomic transfer between accounts |
| `processMonthlyAdjustments()` | Apply fees/interest bank-wide |

#### 4. AccountStrategy (Interface)

**Responsibilities**:
- Define contract for account-type rules
- Validate withdrawals against business rules
- Apply monthly adjustments (fees/interest)

**Implementations**:
| Strategy | Withdrawal Rules | Monthly Processing |
|----------|-----------------|-------------------|
| CheckingAccountStrategy | balance >= amount | $2.50 fee per transaction after 10 |
| SavingsAccountStrategy | balance >= amount AND balance - amount >= $100 AND withdrawals < 5 | 2% interest credited |

---

## Data Flow

### Deposit Flow

```
User Request → Bank.deposit()
    → Validate account exists
    → Validate amount positive
    → Account.deposit()
        → Update balance
        → Increment transaction count
        → Record SUCCESS transaction
    → Return Transaction
```

### Withdrawal Flow

```
User Request → Bank.withdraw()
    → Validate account exists
    → Get strategy from factory
    → Strategy.validateWithdrawal()
        → Check sufficient funds
        → (SAVINGS) Check minimum balance
        → (SAVINGS) Check withdrawal limit
    → If valid:
        → Account.withdraw()
        → Record SUCCESS transaction
    → If invalid:
        → Record FAILED transaction
        → Throw exception
```

### Transfer Flow

```
User Request → Bank.transfer()
    → Validate both accounts exist
    → Validate not same account
    → Get source account strategy
    → Strategy.validateWithdrawal()
    → If valid:
        → Source.withdraw()
        → Destination.deposit()
        → Record TRANSFER on both
    → If invalid:
        → No changes to either account
        → Record FAILED on source
        → Throw exception
```

---

## Design Decisions

### 1. Strategy Pattern for Account Rules

**Decision**: Use Strategy pattern instead of if/else in Bank

**Rationale**:
- Open/Closed Principle - add new account types without modifying Bank
- Single Responsibility - each strategy handles one account type
- Testability - strategies tested independently

### 2. Failed Transactions Recorded

**Decision**: Record FAILED transactions in history (not just throw exceptions)

**Rationale**:
- Complete audit trail of all attempts
- Compliance and debugging support
- Balance remains unchanged on failure

### 3. BigDecimal for Money

**Decision**: Use BigDecimal for all monetary values

**Rationale**:
- Avoids floating-point rounding errors
- Precise decimal arithmetic
- Industry standard for financial applications

### 4. Atomic Transfers

**Decision**: Transfers are all-or-nothing operations

**Rationale**:
- No partial state (money never "in transit")
- Source validation failure prevents any changes
- Simple rollback model

### 5. Strategy Selection via Factory

**Decision**: Use factory for strategy selection instead of storing strategy in Account

**Rationale**:
- Stateless strategies (shareable)
- Decouples Account from strategy implementations
- Factory centralizes selection logic

---

## Trade-offs

### 1. In-Memory Storage

**Choice**: HashMap for account storage

**Trade-off**:
- Pro: Simple, fast, no external dependencies
- Con: Data lost on restart, not thread-safe by default

**Mitigation**: Easy to replace with database later via repository interface

### 2. Synchronous Processing

**Choice**: All operations synchronous

**Trade-off**:
- Pro: Simple flow, easy to reason about
- Con: Blocking for large batch operations

**Mitigation**: Current requirements don't need async processing

### 3. Exception-Based Error Handling

**Choice**: Throw domain exceptions for business rule violations

**Trade-off**:
- Pro: Clear control flow, typed errors with context
- Con: Exceptions can be expensive in hot paths

**Mitigation**: Exceptions are exceptional cases, normal path is fast

---

## SOLID Principles Applied

| Principle | Application |
|-----------|-------------|
| **Single Responsibility** | Account manages state. Strategy enforces rules. Bank orchestrates. |
| **Open/Closed** | New account types = new strategy, no Bank modification. |
| **Liskov Substitution** | All strategies interchangeable via AccountStrategy interface. |
| **Interface Segregation** | AccountStrategy has only methods needed by clients. |
| **Dependency Inversion** | Bank depends on AccountStrategy abstraction, not concrete types. |

---

## OOP Principles Applied

| Principle | Application |
|-----------|-------------|
| **Encapsulation** | Account balance modified only through methods. Internal counters hidden. |
| **Abstraction** | Bank clients use simple API, unaware of strategy selection. |
| **Inheritance** | BankingException is base class for all domain exceptions. |
| **Polymorphism** | Strategy implementations interchangeable. |
| **Composition** | Account HAS-A List<Transaction>. Bank HAS-A Map<Account>. |
│                 balance - amount >= $100            │
│                 withdrawals this month < 5          │
└───────────────────────┬─────────────────────────────┘
                        │
        ┌───────────────┴───────────────┐
        │                               │
┌───────▼───────┐               ┌───────▼───────┐
│ VALIDATION    │               │ VALIDATION    │
│ PASSED        │               │ FAILED        │
└───────┬───────┘               └───────┬───────┘
        │                               │
┌───────▼───────────────┐       ┌───────▼───────────────┐
│ Update balance        │       │ Record FAILED         │
│ Increment counters    │       │ transaction           │
│ Record SUCCESS txn    │       │ Balance unchanged     │
└───────────────────────┘       └───────────────────────┘
```

### Transfer Flow (Atomic)

```
┌─────────────────────────────────────────────────────┐
│ 1. Bank.transfer(from, to, amount)                  │
└───────────────────────┬─────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────┐
│ 2. Lookup both accounts                             │
│    └─> Throws if either missing                     │
└───────────────────────┬─────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────┐
│ 3. Validate source withdrawal (via strategy)        │
└───────────────────────┬─────────────────────────────┘
                        │
        ┌───────────────┴───────────────┐
        │                               │
┌───────▼───────┐               ┌───────▼───────┐
│ VALIDATION    │               │ VALIDATION    │
│ PASSED        │               │ FAILED        │
└───────┬───────┘               └───────┬───────┘
        │                               │
┌───────▼───────────────┐       ┌───────▼───────────────┐
│ 4a. Execute withdraw  │       │ 4b. Record FAILED     │
│     from source       │       │     transfer          │
└───────┬───────────────┘       │     No changes        │
        │                       └───────────────────────┘
┌───────▼───────────────┐
│ 5. Execute deposit    │
│    to destination     │
└───────┬───────────────┘
        │
┌───────▼───────────────┐
│ 6. Record TRANSFER    │
│    on both accounts   │
└───────────────────────┘
```

### Monthly Processing Flow

```
┌─────────────────────────────────────────────────────┐
│ 1. Bank.processMonthlyAdjustments()                 │
└───────────────────────┬─────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────┐
│ 2. For each ACTIVE account:                         │
│    └─> Get strategy via AccountStrategyFactory      │
└───────────────────────┬─────────────────────────────┘
                        │
        ┌───────────────┴───────────────┐
        │                               │
┌───────▼───────────────┐       ┌───────▼───────────────┐
│ CHECKING STRATEGY     │       │ SAVINGS STRATEGY      │
├───────────────────────┤       ├───────────────────────┤
│ If transactions > 10: │       │ Calculate interest:   │
│   Apply $12 fee       │       │   balance * 0.0025    │
│   Record FEE txn      │       │   Credit to account   │
│                       │       │   Record INTEREST txn │
│ Reset counters        │       │ Reset counters        │
└───────────────────────┘       └───────────────────────┘
```

---

## Design Patterns

### 1. Strategy Pattern
**Problem:** Different account types have different business rules for withdrawals and monthly processing.

**Solution:** AccountStrategy interface with CheckingAccountStrategy and SavingsAccountStrategy implementations.

**Benefits:**
- Open/Closed Principle: Add new account types without modifying Bank
- Single Responsibility: Business rules isolated per account type
- Testability: Strategies tested independently

### 2. Builder Pattern
**Problem:** Transaction has many fields, some optional (like failureReason).

**Solution:** Transaction.Builder for fluent construction.

**Benefits:**
- Immutability maintained
- Optional fields handled cleanly
- Readable construction code

### 3. Factory Pattern
**Problem:** Bank needs correct strategy for each account type.

**Solution:** AccountStrategyFactory.getStrategy(AccountType) returns appropriate implementation.

**Benefits:**
- Decouples Bank from concrete strategy classes
- Centralized strategy creation logic

### 4. Repository Pattern
**Problem:** Need centralized storage and retrieval of accounts.

**Solution:** Bank acts as in-memory repository with Map<String, Account>.

**Benefits:**
- Single source of truth for accounts
- Encapsulated storage details

### 5. Template Method (Implicit)
**Problem:** Transfer operation follows consistent steps.

**Solution:** Bank.transfer() follows template: validate → withdraw → deposit → record.

### 6. Value Object Pattern
**Problem:** Transactions should be immutable event records.

**Solution:** Transaction class with final fields, no setters.

**Benefits:**
- Thread-safe by design
- Audit trail cannot be tampered

---

## OOP Principles

| Principle | Application |
|-----------|-------------|
| **Encapsulation** | Account balance modified only through deposit/withdraw methods. Internal counters not exposed. |
| **Abstraction** | Bank clients use simple API without knowing strategy selection or validation internals. |
| **Inheritance** | BankingException is base class for all domain exceptions. |
| **Polymorphism** | AccountStrategy implementations are interchangeable through interface. |
| **Composition** | Account HAS-A List<Transaction>. Bank HAS-A Map<Account>. |

---

## SOLID Principles

| Principle | Application |
|-----------|-------------|
| **Single Responsibility** | Account manages state. Strategy enforces rules. Bank orchestrates workflows. |
| **Open/Closed** | New account types added by creating new strategy, no modification to existing code. |
| **Liskov Substitution** | CheckingAccountStrategy and SavingsAccountStrategy are substitutable via AccountStrategy. |
| **Interface Segregation** | AccountStrategy has only two methods needed by clients. |
| **Dependency Inversion** | Bank depends on AccountStrategy abstraction, not concrete CheckingAccountStrategy. |

---

## Key Design Decisions

### 1. Failed Transactions Recorded, Not Thrown
**Decision:** Business rule violations result in FAILED transaction records rather than just exceptions.

**Rationale:**
- Complete audit trail of all attempted operations
- Balance remains unchanged on failure
- Client can inspect failure reason from transaction

### 2. Atomic Transfers
**Decision:** Transfer is all-or-nothing operation.

**Rationale:**
- No partial state (money not lost in transit)
- Source withdrawal failure prevents any changes
- Rollback mechanism if deposit fails (rare edge case)

### 3. BigDecimal for Money
**Decision:** All monetary values use BigDecimal.

**Rationale:**
- Avoids floating-point rounding errors
- Precise decimal arithmetic for financial calculations
- Industry standard for monetary applications

### 4. Sequential ID Generation
**Decision:** Account and transaction IDs are sequential (ACC-001, TXN-001).

**Rationale:**
- Simple and predictable
- Easy to debug and trace
- Sufficient for single-threaded application

### 5. Strategy Selection via Factory
**Decision:** AccountStrategyFactory returns strategy based on AccountType.

**Rationale:**
- Decouples Bank from strategy implementations
- Centralized strategy creation logic
- Easy to add new account types

---

## Testing Strategy

### Test Categories

| Category | Focus | Example |
|----------|-------|---------|
| **Unit Tests** | Individual class behavior | AccountTest, TransactionTest |
| **Strategy Tests** | Business rule enforcement | CheckingAccountStrategyTest |
| **Service Tests** | Orchestration logic | BankTest |
| **Integration Tests** | End-to-end workflows | BankingIntegrationTest |

### Coverage Focus
- Happy path operations
- Edge cases (zero amounts, exact minimums, limit boundaries)
- Error conditions (insufficient funds, closed accounts)
- State transitions (active → closed)
- Monthly processing effects

See [TEST_COVERAGE_ANALYSIS.md](TEST_COVERAGE_ANALYSIS.md) for detailed breakdown.
