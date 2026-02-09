# Banking System - Business Rules

## Table of Contents
1. [Account Type Rules](#account-type-rules)
2. [Transaction Rules](#transaction-rules)
3. [Account Lifecycle](#account-lifecycle)
4. [Monthly Processing](#monthly-processing)
5. [Exception Handling](#exception-handling)
6. [Quick Reference](#quick-reference)

---

## Account Type Rules

### CHECKING Account

| Rule | Value | Description |
|------|-------|-------------|
| Minimum Balance | $0.00 | Can withdraw entire balance |
| Opening Deposit | $0.00+ | Any non-negative amount |
| Interest Rate | 0% | No interest earned |
| Withdrawal Limit | Unlimited | No monthly restriction |
| Free Transactions | 10/month | First 10 are free |
| Transaction Fee | $2.50 | Per transaction after 10 |

#### Fee Calculation

**Formula:**
```
fee = max(0, transactionCount - 10) × $2.50
```

**Examples:**
| Transactions | Calculation | Fee |
|-------------|-------------|-----|
| 8 | (8 - 10) × $2.50 = 0 | $0.00 |
| 10 | (10 - 10) × $2.50 = 0 | $0.00 |
| 11 | (11 - 10) × $2.50 | $2.50 |
| 15 | (15 - 10) × $2.50 | $12.50 |
| 25 | (25 - 10) × $2.50 | $37.50 |

---

### SAVINGS Account

| Rule | Value | Description |
|------|-------|-------------|
| Minimum Balance | $100.00 | Must remain after withdrawal |
| Opening Deposit | $100.00+ | At least $100 required |
| Interest Rate | 2%/month | Applied to current balance |
| Withdrawal Limit | 5/month | Direct withdrawals only |
| Transaction Fee | $0.00 | No fees |

#### Interest Calculation

**Formula:**
```
interest = balance × 0.02
```

**Examples:**
| Balance | Calculation | Interest |
|---------|-------------|----------|
| $100.00 | $100.00 × 0.02 | $2.00 |
| $500.00 | $500.00 × 0.02 | $10.00 |
| $5,000.00 | $5,000.00 × 0.02 | $100.00 |
| $10,000.00 | $10,000.00 × 0.02 | $200.00 |

#### Withdrawal Validation

All three conditions must pass:

| Check | Rule | Example |
|-------|------|---------|
| ✓ Sufficient Funds | `balance >= amount` | $500 balance, $200 withdrawal → ✓ |
| ✓ Minimum Balance | `balance - amount >= $100` | $500 - $200 = $300 → ✓ |
| ✓ Withdrawal Limit | `withdrawalCount < 5` | 3 withdrawals this month → ✓ |

**Failure Scenarios:**
| Balance | Amount | Withdrawals | Result |
|---------|--------|-------------|--------|
| $200 | $150 | 2 | ❌ MinimumBalanceViolation ($50 < $100) |
| $500 | $600 | 1 | ❌ InsufficientFunds |
| $500 | $100 | 5 | ❌ WithdrawalLimitExceeded |
| $500 | $400 | 3 | ✓ Success ($100 remains) |

---

## Transaction Rules

### DEPOSIT

| Validation | Rule |
|------------|------|
| Amount | Must be > $0.00 |
| Account Status | Must be ACTIVE |

| On Success | Effect |
|------------|--------|
| Balance | Increases by amount |
| Transaction Count | Incremented |
| Transaction Record | SUCCESS |

---

### WITHDRAWAL

| Validation | CHECKING | SAVINGS |
|------------|----------|---------|
| Positive Amount | ✓ Required | ✓ Required |
| Active Account | ✓ Required | ✓ Required |
| Sufficient Funds | ✓ Required | ✓ Required |
| Minimum Balance | ✗ Not required | ✓ $100.00 must remain |
| Monthly Limit | ✗ Unlimited | ✓ Max 5/month |

| On Success | Effect |
|------------|--------|
| Balance | Decreases by amount |
| Transaction Count | Incremented |
| Withdrawal Count | Incremented (SAVINGS only) |
| Transaction Record | SUCCESS |

| On Failure | Effect |
|------------|--------|
| Balance | Unchanged |
| Counters | Unchanged |
| Transaction Record | FAILED with reason |
| Exception | Thrown to caller |

---

### TRANSFER

| Validation | Rule |
|------------|------|
| Amount | Must be > $0.00 |
| Source Account | Must exist and be ACTIVE |
| Destination Account | Must exist and be ACTIVE |
| Self-Transfer | NOT allowed (must be different accounts) |
| Source Validation | Must pass withdrawal rules |

**Key Behaviors:**
- ✓ Atomic: All-or-nothing (no partial transfers)
- ✓ Audit: Both accounts record TRANSFER transaction
- ✓ Cross-type: CHECKING ↔ SAVINGS allowed
- ✗ Does NOT count toward SAVINGS withdrawal limit

| Scenario | Source Effect | Destination Effect |
|----------|---------------|-------------------|
| Success | Balance decreases | Balance increases |
| Failure | No change | No change |

---

## Account Lifecycle

### Opening an Account

| Field | CHECKING | SAVINGS |
|-------|----------|---------|
| Customer Name | Required (non-empty) | Required (non-empty) |
| Account Type | Required | Required |
| Initial Deposit | $0.00 minimum | $100.00 minimum |

| On Success | Effect |
|------------|--------|
| Account Number | Generated (ACC-XXX format) |
| Status | Set to ACTIVE |
| Balance | Set to initial deposit |
| Transaction | DEPOSIT recorded |

---

### Closing an Account

| Validation | Rule |
|------------|------|
| Account Exists | Must exist in system |
| Balance | Must be exactly $0.00 |
| Status | Must be ACTIVE |

| On Success | Effect |
|------------|--------|
| Status | Changed to CLOSED |
| Future Operations | All rejected |

---

## Monthly Processing

### Processing Flow

```
For each ACTIVE account:
  1. Get AccountStrategy for account type
  2. Apply adjustments (fees OR interest)
  3. Reset monthly counters to 0
```

### CHECKING Processing

```
IF monthlyTransactionCount > 10:
    fee = (count - 10) × $2.50
    Deduct fee from balance
    Record FEE transaction
ENDIF
Reset monthlyTransactionCount = 0
```

### SAVINGS Processing

```
interest = balance × 0.02
Add interest to balance
Record INTEREST transaction
Reset monthlyWithdrawalCount = 0
Reset monthlyTransactionCount = 0
```

---

## Exception Handling

### Exception Hierarchy

| Exception | Trigger | Example |
|-----------|---------|---------|
| `AccountNotFoundException` | Account not found | `getAccount("ACC-999")` when not exists |
| `InsufficientFundsException` | Balance < withdrawal | Withdraw $500 from $200 balance |
| `MinimumBalanceViolationException` | Would leave < $100 (SAVINGS) | Withdraw $450 from $500 SAVINGS |
| `WithdrawalLimitExceededException` | 6th withdrawal (SAVINGS) | Withdraw when `withdrawalCount = 5` |
| `AccountClosedException` | Operation on closed account | Deposit to CLOSED account |
| `InvalidOperationException` | General invalid operation | Transfer to same account |

### Audit Trail Policy

| Transaction Attempt | Status | Balance | Transaction Record |
|---------------------|--------|---------|-------------------|
| Valid operation | SUCCESS | Modified | ✓ Recorded |
| Business rule violation | FAILED | Unchanged | ✓ Recorded with reason |

**FAILED transactions include:**
- Original intended amount
- Timestamp of attempt
- Human-readable failure reason
- Balance snapshot (unchanged)

---

## Quick Reference

### CHECKING vs SAVINGS

| Feature | CHECKING | SAVINGS |
|---------|----------|---------|
| Minimum Balance | $0.00 | $100.00 |
| Opening Deposit | $0.00+ | $100.00+ |
| Monthly Fee | $2.50/txn after 10 | None |
| Interest Rate | 0% | 2%/month |
| Withdrawal Limit | Unlimited | 5/month |

### Transfer Rules

| Rule | Value |
|------|-------|
| Atomicity | All-or-nothing |
| Counts as SAVINGS Withdrawal | No |
| Transaction Recording | Both accounts |
| Cross-Account Type | Allowed |

### Monthly Counter Behavior

| Counter | Incremented By | Reset By |
|---------|---------------|----------|
| `monthlyTransactionCount` | All transactions | Monthly processing |
| `monthlyWithdrawalCount` | Direct withdrawals only | Monthly processing |

### Transaction Types

| Type | Triggered By |
|------|--------------|
| DEPOSIT | Direct deposit or initial balance |
| WITHDRAWAL | Direct withdrawal |
| TRANSFER | Transfer (recorded on both accounts) |
| FEE | Monthly CHECKING fee application |
| INTEREST | Monthly SAVINGS interest credit |
