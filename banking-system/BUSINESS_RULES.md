# Banking System - Business Rules Documentation

## Overview

This document details all business rules implemented in the banking system. These rules govern account behavior, transaction validation, and monthly processing.

---

## Table of Contents

1. [Account Types](#account-types)
2. [Transaction Types](#transaction-types)
3. [Account Management Rules](#account-management-rules)
4. [Transaction Rules](#transaction-rules)
5. [Business Constraints](#business-constraints)
6. [Monthly Processing](#monthly-processing)
7. [Validation Rules](#validation-rules)
8. [Failure Handling](#failure-handling)

---

## Account Types

### 1. CHECKING Account

**Purpose**: Transactional account for daily banking activities

**Characteristics**:
- ✅ No minimum balance requirement
- ✅ Unlimited deposits
- ✅ Unlimited withdrawals
- ✅ No interest earned
- ✅ Transaction fees after free allowance

**Business Rules**:

| Rule | Description | Value |
|------|-------------|-------|
| Minimum Balance | None required | $0.00 |
| Free Transactions | Per month | 10 transactions |
| Transaction Fee | After 10th transaction | $2.50 per transaction |
| Interest Rate | Not applicable | 0% |
| Withdrawal Limit | None | Unlimited |
| Initial Deposit | Minimum | $0.01 |

**Fee Calculation**:
```
Monthly Fee = (Total Transactions - 10) × $2.50 (if > 10 transactions)
            = $0 (if ≤ 10 transactions)
```

**Example**:
- Month with 8 transactions: $0 fee
- Month with 10 transactions: $0 fee
- Month with 15 transactions: (15 - 10) × $2.50 = $12.50 fee

---

### 2. SAVINGS Account

**Purpose**: Long-term savings with interest earnings

**Characteristics**:
- ✅ Minimum balance required
- ✅ Unlimited deposits
- ✅ Limited withdrawals
- ✅ Monthly interest earned
- ✅ No transaction fees

**Business Rules**:

| Rule | Description | Value |
|------|-------------|-------|
| Minimum Balance | Always maintained | $100.00 |
| Free Transactions | All transactions free | Unlimited |
| Transaction Fee | Not applicable | $0.00 |
| Interest Rate | Applied monthly | 2.0% per month |
| Withdrawal Limit | Per month | 5 withdrawals |
| Initial Deposit | Minimum required | $100.00 |

**Interest Calculation**:
```
Monthly Interest = Current Balance × 0.02
```

**Example**:
- Balance: $1,000.00
- Monthly Interest: $1,000.00 × 0.02 = $20.00
- New Balance: $1,020.00

**Withdrawal Limit Enforcement**:
- Withdrawals are counted monthly
- 6th withdrawal in a month will be rejected
- Counter resets at month-end
- Deposits do not count toward limit

---

## Transaction Types

### 1. DEPOSIT

**Purpose**: Add funds to an account

**Rules**:
- ✅ Amount must be positive (> $0.00)
- ✅ No maximum limit
- ✅ Always increases balance
- ✅ Counts toward CHECKING transaction count
- ✅ Does NOT count toward SAVINGS withdrawal count

**Validation**:
```
Valid if:
  amount != null
  AND amount > 0
```

**Effects**:
- `balance += amount`
- `monthlyTransactionCount++` (CHECKING only)
- Transaction recorded with SUCCESS status

---

### 2. WITHDRAWAL

**Purpose**: Remove funds from an account

**Rules**:
- ✅ Amount must be positive (> $0.00)
- ✅ Requires sufficient funds
- ✅ Respects minimum balance (SAVINGS)
- ✅ Respects withdrawal limit (SAVINGS)
- ✅ Counts toward transaction count (CHECKING)
- ✅ Counts toward withdrawal count (SAVINGS)

**Validation**:

**For CHECKING**:
```
Valid if:
  amount != null
  AND amount > 0
  AND amount <= currentBalance
```

**For SAVINGS**:
```
Valid if:
  amount != null
  AND amount > 0
  AND amount <= currentBalance
  AND (currentBalance - amount) >= 100.00
  AND monthlyWithdrawalCount < 5
```

**Effects on Success**:
- `balance -= amount`
- `monthlyTransactionCount++` (CHECKING)
- `monthlyWithdrawalCount++` (SAVINGS)
- Transaction recorded with SUCCESS status

**Effects on Failure**:
- Balance unchanged
- Counters unchanged
- Transaction recorded with FAILED status and reason

---

### 3. TRANSFER

**Purpose**: Move funds between two accounts

**Rules**:
- ✅ Amount must be positive (> $0.00)
- ✅ Source and destination must be different
- ✅ Follows withdrawal rules for source account
- ✅ Follows deposit rules for destination account
- ✅ Atomic operation (both succeed or both fail)
- ✅ Creates two transaction records (one per account)

**Validation**:
```
Valid if:
  fromAccount != toAccount
  AND all withdrawal validations pass for fromAccount
  AND all deposit validations pass for toAccount
```

**Effects on Success**:
- Source account: withdraw(amount) logic applied
- Destination account: deposit(amount) logic applied
- Two transactions created (WITHDRAWAL, DEPOSIT)
- Both transactions have SUCCESS status

**Effects on Failure**:
- Both accounts unchanged
- Two transactions created (WITHDRAWAL, DEPOSIT)
- Both transactions have FAILED status with same reason

**Atomicity Guarantee**:
- Ordered locking prevents deadlock
- Both accounts locked before any changes
- Changes rolled back if any validation fails

---

## Account Management Rules

### Opening an Account

**Rules**:
1. Customer name required (minimum 2 characters)
2. Account type required (CHECKING or SAVINGS)
3. Initial deposit required (>= minimum for type)
4. Unique account number auto-generated

**Validation**:

**Customer Name**:
```
Valid if:
  customerName != null
  AND customerName.trim().length() >= 2
```

**Account Type**:
```
Valid if:
  accountType != null
  AND accountType IN {CHECKING, SAVINGS}
```

**Initial Deposit**:

For CHECKING:
```
Valid if:
  initialDeposit >= 0.01
```

For SAVINGS:
```
Valid if:
  initialDeposit >= 100.00
```

**Process**:
1. Validate all inputs
2. Generate unique account number (ACC-XXXXXXXX)
3. Create Account object
4. Record initial deposit transaction (if > $0)
5. Add account to bank's repository
6. Return Account object

---

### Closing an Account

**Rules**:
1. Account must exist
2. Balance must be exactly $0.00
3. Cannot close account with pending balance

**Validation**:
```
Valid if:
  account.exists()
  AND account.balance == 0.00
```

**Rejection Reasons**:
- Account not found → Exception thrown
- Balance > $0.00 → Return false, account not closed
- Balance < $0.00 → Return false (shouldn't happen)

**Process**:
1. Validate account exists
2. Check balance == $0.00
3. If valid: remove from repository, return true
4. If invalid: return false

---

## Business Constraints

### Balance Constraints

| Account Type | Minimum Balance | Maximum Balance | Negative Allowed |
|--------------|----------------|-----------------|------------------|
| CHECKING | $0.00 | Unlimited | No |
| SAVINGS | $100.00 | Unlimited | No |

**Enforcement**:
- Withdrawals/transfers that would violate minimum balance are rejected
- Negative balances never allowed (validation prevents)
- No maximum balance limit (BigDecimal can handle large values)

---

### Transaction Limits

| Account Type | Deposits | Withdrawals | Transfers (as source) | Transfers (as destination) |
|--------------|----------|-------------|----------------------|---------------------------|
| CHECKING | Unlimited | Unlimited | Unlimited | Unlimited |
| SAVINGS | Unlimited | 5 per month | 5 per month (counts as withdrawal) | Unlimited |

**Enforcement**:
- CHECKING: No limits enforced
- SAVINGS: Withdrawal counter incremented for withdrawals and outbound transfers
- SAVINGS: Counter checked before allowing operation
- SAVINGS: Counter reset monthly

---

### Fee Structure

#### CHECKING Account Fees

| Scenario | Fee Amount | When Applied |
|----------|-----------|---------------|
| First 10 transactions | $0.00 | Never |
| After 10th transaction | $2.50 per transaction | Monthly |
| Account maintenance | $0.00 | N/A |
| Insufficient funds | $0.00 | N/A (transaction just fails) |

**Fee Application**:
- Calculated monthly during `applyMonthlyFeesAndResetCounters()`
- Formula: `(transactionCount - 10) × 2.50` if count > 10
- Fee withdrawn from account balance
- Fee transaction recorded in history

#### SAVINGS Account Fees

| Scenario | Fee Amount |
|----------|-----------|
| All transactions | $0.00 |
| Exceeding withdrawal limit | $0.00 (transaction rejected instead) |
| Account maintenance | $0.00 |

---

### Interest Structure

#### CHECKING Account Interest

**Rate**: 0% (no interest earned)

**Rationale**: Transactional accounts don't earn interest

---

#### SAVINGS Account Interest

**Rate**: 2% per month

**Calculation**:
```
Interest = CurrentBalance × 0.02
NewBalance = CurrentBalance + Interest
```

**Application**:
- Applied monthly during `applyMonthlyInterest()`
- Based on balance at time of calculation
- Interest added to account balance
- Interest transaction recorded in history
- Compounds monthly (previous interest earns interest)

**Example**:
```
Month 1: $1,000.00 → +$20.00 interest → $1,020.00
Month 2: $1,020.00 → +$20.40 interest → $1,040.40
Month 3: $1,040.40 → +$20.81 interest → $1,061.21
```

---

## Monthly Processing

### Process Overview

Monthly processing happens in two phases:

1. **Interest Application** (SAVINGS accounts)
2. **Fee Application & Counter Reset** (All accounts)

---

### Phase 1: Interest Application

**Target**: All SAVINGS accounts

**Process**:
```
For each SAVINGS account:
  1. Get current balance
  2. Calculate interest = balance × 0.02
  3. Add interest to balance
  4. Record transaction:
     - Type: DEPOSIT
     - Amount: interest
     - Status: SUCCESS
  5. Accumulate total interest applied
  
Return: Total interest paid across all accounts
```

**Characteristics**:
- Only SAVINGS accounts receive interest
- Interest rate: 2% flat (not APR)
- Compounds monthly
- No minimum balance requirement for interest
- Interest recorded as DEPOSIT transaction

---

### Phase 2: Fee Application & Counter Reset

**Target**: All accounts

**Process for CHECKING**:
```
For each CHECKING account:
  1. Get monthlyTransactionCount
  2. If count > 10:
     a. Calculate fee = (count - 10) × 2.50
     b. Withdraw fee from balance
     c. Record transaction:
        - Type: WITHDRAWAL
        - Amount: fee
        - Status: SUCCESS
  3. Reset monthlyTransactionCount = 0
```

**Process for SAVINGS**:
```
For each SAVINGS account:
  1. Reset monthlyWithdrawalCount = 0
  (No fees for SAVINGS accounts)
```

**Characteristics**:
- Fees charged only to CHECKING accounts
- Fee calculation based on transaction count
- Counters reset regardless of fees
- All accounts (both types) get counters reset

---

### Monthly Processing Schedule

**When to Run**:
- End of each calendar month
- Before generating monthly statements
- Order matters: Interest first, then fees

**Why Order Matters**:
```
Example: SAVINGS account with $1,000 balance
1. Apply interest: $1,000 × 0.02 = $20 → Balance: $1,020
2. Reset counters: withdrawalCount = 0

If order reversed:
- Counter reset happens but balance hasn't earned interest yet
- No correctness issue, but illogical sequence
```

---

## Validation Rules

### Amount Validation

**Rule**: All monetary amounts must be positive and non-null

```
Valid Amount:
  amount != null
  AND amount.compareTo(BigDecimal.ZERO) > 0
```

**Applies To**:
- Deposits
- Withdrawals
- Transfers
- Initial deposits

**Rejection Reasons**:
- `null` → "Amount is required"
- Zero → "Amount must be positive"
- Negative → "Amount must be positive"

---

### Account Validation

**Rule**: Account must exist for operations

```
Valid Account:
  bank.getAccount(accountNumber) != null
```

**Applies To**:
- Deposit
- Withdrawal
- Transfer (both accounts)
- Closure
- Transaction history queries
- Statement generation

**Rejection**:
- Throws `IllegalArgumentException` with message: "Account not found: {accountNumber}"

---

### Withdrawal Availability

**Rule**: Account must have sufficient funds and respect business rules

**For CHECKING**:
```
canWithdraw(amount):
  return balance >= amount
```

**For SAVINGS**:
```
canWithdraw(amount):
  return balance >= amount
    AND (balance - amount) >= 100.00
    AND monthlyWithdrawalCount < 5
```

**Rejection Reasons**:
- Insufficient funds: "Insufficient funds for withdrawal"
- Below minimum: "Withdrawal would bring SAVINGS account below minimum balance of $100.00"
- Limit exceeded: "Monthly withdrawal limit of 5 exceeded for SAVINGS account"

---

### Transfer Validation

**Rule**: Transfers validated as combined withdrawal + deposit

**Validations Applied**:
1. Amount validation (positive, non-null)
2. Both accounts exist
3. Accounts are different
4. Source account can withdraw (sufficient funds, minimum balance, limits)
5. Destination account can deposit (always valid if amount valid)

**Rejection Reasons**:
- Same account: "Cannot transfer to the same account"
- Source insufficient: "Insufficient funds for withdrawal"
- Source below minimum: "Withdrawal would bring SAVINGS account below minimum balance"
- Source limit exceeded: "Monthly withdrawal limit exceeded"

---

## Failure Handling

### Failed Transaction Recording

**Rule**: ALL transaction attempts are recorded, even failures

**Why**:
- Complete audit trail
- Fraud detection
- Debugging support
- Regulatory compliance
- Customer dispute resolution

**Failed Transaction Structure**:
```java
Transaction failedTx = new Transaction.Builder()
    .transactionId(id)
    .timestamp(LocalDateTime.now())
    .type(type)  // What was attempted
    .amount(amount)  // How much was attempted
    .balanceBefore(balance)  // Unchanged
    .balanceAfter(balance)  // Unchanged
    .status(TransactionStatus.FAILED)
    .failureReason("Detailed reason")  // Why it failed
    .build();
```

**Failure Reasons**:

| Validation Failed | Failure Reason |
|------------------|----------------|
| Null amount | "Amount is required" |
| Zero/negative amount | "Amount must be positive" |
| Insufficient funds | "Insufficient funds for withdrawal" |
| SAVINGS below minimum | "Withdrawal would bring SAVINGS account below minimum balance of $100.00" |
| SAVINGS limit exceeded | "Monthly withdrawal limit of 5 exceeded for SAVINGS account" |
| Same account transfer | "Cannot transfer to the same account" |
| Non-zero balance closure | "Account must have zero balance to close" |

---

### Balance Protection

**Rule**: Balance never changes on failed transactions

**Guarantee**:
```
Before: account.balance = X
Failed Transaction Attempt
After: account.balance = X (unchanged)
```

**Implementation**:
- Validation happens BEFORE any balance modification
- If validation fails, method returns early
- Failed transaction recorded with unchanged balances
- Account state remains consistent

---

### Transaction Atomicity

**Rule**: Transfers are atomic (all-or-nothing)

**Guarantee**:
```
Transfer Success:
  - Both accounts updated
  - Both transactions marked SUCCESS
  - Counters incremented appropriately

Transfer Failure:
  - Neither account updated
  - Both transactions marked FAILED
  - Counters unchanged
  - Same failure reason in both transactions
```

**Implementation**:
- Both accounts validated before any changes
- Ordered locking prevents deadlock
- Both accounts locked during entire operation
- Single failure point affects both transactions

---

## Edge Cases

### Edge Case: Exactly at Minimum Balance

**Scenario**: SAVINGS account with exactly $100.00

**Withdrawal of $0.01**:
```
Before: Balance = $100.00
Attempt: Withdraw $0.01
Result: FAILED
Reason: "Withdrawal would bring SAVINGS account below minimum balance"
After: Balance = $100.00 (unchanged)
```

**Why**: Rule is `(balance - amount) >= 100.00`, not `>`

---

### Edge Case: Exactly 10 Transactions

**Scenario**: CHECKING account with exactly 10 transactions

**Fee Calculation**:
```
Transaction Count: 10
Fee = (10 - 10) × $2.50 = $0 × $2.50 = $0.00
```

**Why**: Fee starts AFTER 10th transaction

**11th Transaction**:
```
Transaction Count: 11
Fee = (11 - 10) × $2.50 = 1 × $2.50 = $2.50
```

---

### Edge Case: Exactly 5 Withdrawals

**Scenario**: SAVINGS account with exactly 5 withdrawals this month

**6th Withdrawal Attempt**:
```
Before: monthlyWithdrawalCount = 5
Attempt: Withdraw $50.00
Validation: monthlyWithdrawalCount < 5 → FALSE
Result: FAILED
Reason: "Monthly withdrawal limit of 5 exceeded for SAVINGS account"
After: monthlyWithdrawalCount = 5 (unchanged)
```

**Why**: Rule is `< 5`, not `<= 5` (allows 0, 1, 2, 3, 4 = five withdrawals)

---

### Edge Case: Transfer Between Same Account

**Scenario**: Attempt to transfer from account A to account A

**Validation**:
```
fromAccount = "ACC-00000001"
toAccount = "ACC-00000001"
Validation: fromAccount.equals(toAccount) → TRUE
Result: FAILED
Reason: "Cannot transfer to the same account"
```

**Why**: Transfers must move money between different accounts

---

### Edge Case: Zero Balance Account Closure

**Scenario**: Account with exactly $0.00

**Closure Attempt**:
```
Balance: $0.00
Attempt: closeAccount()
Validation: balance.compareTo(BigDecimal.ZERO) == 0 → TRUE
Result: SUCCESS
Account: Removed from bank
```

**Why**: Rule requires EXACTLY zero, not approximately zero

---

## Business Rule Summary Table

| Rule | CHECKING | SAVINGS |
|------|----------|---------|
| Minimum Balance | None ($0) | $100.00 |
| Initial Deposit | $0.01+ | $100.00+ |
| Transaction Fee | $2.50 after 10/month | None |
| Interest Rate | 0% | 2% per month |
| Withdrawal Limit | Unlimited | 5 per month |
| Deposit Limit | Unlimited | Unlimited |
| Negative Balance | Never | Never |
| Closure Requirement | $0 balance | $0 balance |

---

## Compliance & Audit

### Audit Trail Requirements

**Every transaction must record**:
1. ✅ Unique transaction ID
2. ✅ Timestamp (millisecond precision)
3. ✅ Transaction type
4. ✅ Amount
5. ✅ Balance before transaction
6. ✅ Balance after transaction
7. ✅ Transaction status (SUCCESS or FAILED)
8. ✅ Failure reason (if applicable)

**Guarantees**:
- Immutable transaction records
- Complete history maintained
- Failed attempts included
- Temporal ordering preserved
- No transaction deletion

---

## Regulatory Considerations

### Financial Precision

**Rule**: Use `BigDecimal` for all monetary calculations

**Rationale**:
- No floating-point rounding errors
- Exact decimal arithmetic
- Regulatory compliance (GAAP, banking standards)
- Auditable calculations

### Negative Balance Protection

**Rule**: Negative balances never allowed

**Enforcement**:
- Pre-transaction validation
- Balance checks before withdrawal
- Minimum balance requirements
- Transfer validation

### Interest Calculation Transparency

**Rule**: Interest calculation must be reproducible

**Implementation**:
- Fixed rate (2%)
- Simple formula (balance × 0.02)
- Monthly compounding
- Transaction recorded
- Traceable through audit trail

---

## Conclusion

These business rules ensure:
- ✅ **Regulatory Compliance**: Proper audit trails and financial precision
- ✅ **Customer Protection**: No unexpected fees, clear limits
- ✅ **Business Viability**: Appropriate fees for CHECKING accounts
- ✅ **Data Integrity**: Immutable transactions, atomic operations
- ✅ **Transparency**: All rules clearly defined and enforced

The system enforces all rules consistently, records all attempts (success and failure), and maintains complete audit trails for compliance and debugging.
