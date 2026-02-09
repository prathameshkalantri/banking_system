package com.prathamesh.banking.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Account entity representing a bank account.
 * 
 * OOP Principles Applied:
 * 1. Encapsulation - Private fields with controlled access through methods
 * 2. Data Hiding - Balance cannot be directly modified, only through deposit/withdraw
 * 3. Composition - Account HAS-A list of Transactions
 * 4. Entity Pattern - Identified by accountNumber (identity, not values)
 * 
 * Design Decision:
 * - Account is a SIMPLE domain entity
 * - Does NOT contain business rule logic (no if accountType == CHECKING logic)
 * - Business rules delegated to Strategy pattern (CheckingAccountStrategy, SavingsAccountStrategy)
 * - Maintains state and provides basic operations only
 * 
 * This follows Single Responsibility Principle - Account manages state, Strategies enforce rules.
 */
public class Account {
    
    // Immutable fields - Account identity
    private final String accountNumber;
    private final AccountType accountType;
    private final String customerName;
    
    // Mutable state - Managed through controlled methods
    private BigDecimal balance;
    private final List<Transaction> transactionHistory;
    private int monthlyTransactionCount;
    private int monthlyWithdrawalCount;
    
    /**
     * Constructs a new Account.
     * 
     * OOP Principle: Constructor validation ensures object is always in valid state
     * Fail-fast: Validates all inputs at construction time
     *
     * @param accountNumber unique account identifier
     * @param customerName name of the account holder
     * @param accountType type of account (CHECKING or SAVINGS)
     * @param initialDeposit initial deposit amount
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Account(String accountNumber, String customerName, AccountType accountType, BigDecimal initialDeposit) {
        // Validation - OOP: Defensive programming
        this.accountNumber = Objects.requireNonNull(accountNumber, "Account number cannot be null");
        this.customerName = Objects.requireNonNull(customerName, "Customer name cannot be null");
        this.accountType = Objects.requireNonNull(accountType, "Account type cannot be null");
        
        if (customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        
        if (accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit must be non-negative");
        }
        
        // Initialize state
        this.balance = initialDeposit.setScale(2, RoundingMode.HALF_UP);
        this.transactionHistory = new ArrayList<>();
        this.monthlyTransactionCount = 0;
        this.monthlyWithdrawalCount = 0;
    }
    
    // Getters - OOP Principle: Encapsulation (controlled read access)
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public AccountType getAccountType() {
        return accountType;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    /**
     * Returns the current balance.
     * OOP: Returns BigDecimal directly (immutable, safe to share)
     *
     * @return current account balance
     */
    public BigDecimal getBalance() {
        return balance;
    }
    
    /**
     * Returns unmodifiable view of transaction history.
     * OOP Principle: Defensive copying - prevents external modification of internal state
     *
     * @return unmodifiable list of transactions
     */
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }
    
    public int getMonthlyTransactionCount() {
        return monthlyTransactionCount;
    }
    
    public int getMonthlyWithdrawalCount() {
        return monthlyWithdrawalCount;
    }
    
    /**
     * Deposits money into the account.
     * OOP Principle: Encapsulation - Balance can only increase through this method
     * 
     * Note: This is a BASIC operation without business rules
     * Validation of amounts happens in Bank service layer
     *
     * @param amount amount to deposit (must be positive)
     * @param transactionId unique transaction identifier
     * @return the created transaction
     * @throws IllegalArgumentException if amount is invalid
     */
    public Transaction deposit(BigDecimal amount, String transactionId) {
        validatePositiveAmount(amount);
        
        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.add(amount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal balanceAfter = this.balance;
        
        // Increment transaction counter (used by CHECKING for fee calculation)
        monthlyTransactionCount++;
        
        Transaction transaction = new Transaction.Builder(
                transactionId,
                TransactionType.DEPOSIT,
                amount,
                accountNumber
        )
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .status(TransactionStatus.SUCCESS)
                .build();
        
        transactionHistory.add(transaction);
        return transaction;
    }
    
    /**
     * Withdraws money from the account.
     * OOP Principle: Encapsulation - Balance can only decrease through this method
     * 
     * IMPORTANT: This method does NOT enforce business rules!
     * Business rules (minimum balance, withdrawal limits) are enforced by Strategy pattern
     * Bank service must call strategy.validateWithdrawal() BEFORE calling this method
     *
     * @param amount amount to withdraw (must be positive and <= balance)
     * @param transactionId unique transaction identifier
     * @return the created transaction
     * @throws IllegalArgumentException if amount is invalid or exceeds balance
     */
    public Transaction withdraw(BigDecimal amount, String transactionId) {
        validatePositiveAmount(amount);
        
        // Basic validation: sufficient funds (NOT business rule, just sanity check)
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds: balance=" + balance + ", amount=" + amount);
        }
        
        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal balanceAfter = this.balance;
        
        // Increment counters
        monthlyTransactionCount++;
        monthlyWithdrawalCount++;
        
        Transaction transaction = new Transaction.Builder(
                transactionId,
                TransactionType.WITHDRAWAL,
                amount,
                accountNumber
        )
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .status(TransactionStatus.SUCCESS)
                .build();
        
        transactionHistory.add(transaction);
        return transaction;
    }
    
    /**
     * Records a transfer withdrawal (special case of withdrawal).
     * Used when this account is the SOURCE of a transfer.
     * 
     * IMPORTANT: Transfers count toward monthly transaction count (for CHECKING fees)
     * but do NOT count toward withdrawal limit (for SAVINGS accounts).
     * Business Rule: Only WITHDRAWAL transactions count toward SAVINGS 5 withdrawal/month limit.
     *
     * @param amount amount to transfer
     * @param transactionId unique transaction identifier
     * @return the created transaction
     */
    public Transaction transferOut(BigDecimal amount, String transactionId) {
        validatePositiveAmount(amount);
        
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for transfer");
        }
        
        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal balanceAfter = this.balance;
        
        // Transfers count toward transaction count (for CHECKING fees)
        monthlyTransactionCount++;
        // But do NOT count toward withdrawal limit (SAVINGS rule: only withdrawals, not transfers)
        // monthlyWithdrawalCount NOT incremented
        
        Transaction transaction = new Transaction.Builder(
                transactionId,
                TransactionType.TRANSFER,
                amount,
                accountNumber
        )
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .status(TransactionStatus.SUCCESS)
                .build();
        
        transactionHistory.add(transaction);
        return transaction;
    }
    
    /**
     * Applies a fee to the account.
     * Used for CHECKING accounts after free transaction limit.
     * OOP Principle: Encapsulation - Controlled way to deduct fees
     * 
     * Note: Fee does NOT count toward monthly transaction limit
     *
     * @param feeAmount fee amount to charge
     * @param transactionId unique transaction identifier
     * @return the created transaction
     */
    public Transaction applyFee(BigDecimal feeAmount, String transactionId) {
        validatePositiveAmount(feeAmount);
        
        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.subtract(feeAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal balanceAfter = this.balance;
        
        Transaction transaction = new Transaction.Builder(
                transactionId,
                TransactionType.FEE,
                feeAmount,
                accountNumber
        )
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .status(TransactionStatus.SUCCESS)
                .build();
        
        transactionHistory.add(transaction);
        return transaction;
    }
    
    /**
     * Applies interest to the account.
     * Used for SAVINGS accounts monthly.
     * OOP Principle: Encapsulation - Controlled way to credit interest
     * 
     * Note: Interest does NOT count toward monthly transaction limit
     *
     * @param interestAmount interest amount to credit
     * @param transactionId unique transaction identifier
     * @return the created transaction
     */
    public Transaction applyInterest(BigDecimal interestAmount, String transactionId) {
        validatePositiveAmount(interestAmount);
        
        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.add(interestAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal balanceAfter = this.balance;
        
        Transaction transaction = new Transaction.Builder(
                transactionId,
                TransactionType.INTEREST,
                interestAmount,
                accountNumber
        )
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .status(TransactionStatus.SUCCESS)
                .build();
        
        transactionHistory.add(transaction);
        return transaction;
    }
    
    /**
     * Records a failed transaction to history.
     * OOP Principle: All transaction attempts are recorded, successful or not
     * Provides complete audit trail
     *
     * @param type transaction type that was attempted
     * @param amount amount that was attempted
     * @param failureReason description of why it failed
     * @param transactionId unique transaction identifier
     * @return the created failed transaction
     */
    public Transaction recordFailedTransaction(TransactionType type, BigDecimal amount, 
                                              String failureReason, String transactionId) {
        Transaction transaction = new Transaction.Builder(
                transactionId,
                type,
                amount,
                accountNumber
        )
                .balanceBefore(balance)
                .balanceAfter(balance) // Failed transaction - balance unchanged
                .status(TransactionStatus.FAILED)
                .failureReason(failureReason)
                .build();
        
        transactionHistory.add(transaction);
        return transaction;
    }
    
    /**
     * Resets monthly counters.
     * Called at the beginning of each month by Bank service.
     */
    public void resetMonthlyCounters() {
        this.monthlyTransactionCount = 0;
        this.monthlyWithdrawalCount = 0;
    }
    
    /**
     * Checks if account can be closed.
     * Business Rule: Account can only be closed if balance is zero
     *
     * @return true if balance is zero, false otherwise
     */
    public boolean canBeClosed() {
        return balance.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * Increments monthly transaction counter.
     * Used when transaction is processed outside Account (e.g., failed validations)
     */
    public void incrementTransactionCount() {
        monthlyTransactionCount++;
    }
    
    /**
     * Increments monthly withdrawal counter.
     * Used when withdrawal is processed outside Account (e.g., for tracking)
     */
    public void incrementWithdrawalCount() {
        monthlyWithdrawalCount++;
    }
    
    /**
     * Validates that amount is positive and properly scaled.
     * OOP Principle: Input validation to maintain invariants
     * Private helper - encapsulates validation logic
     *
     * @param amount amount to validate
     * @throws IllegalArgumentException if amount is invalid
     */
    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive, got: " + amount);
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Amount cannot have more than 2 decimal places");
        }
    }
    
    /**
     * Entity equality - based on account number (identity).
     * Two accounts are equal if they have the same account number, regardless of balance
     * OOP Principle: Entity pattern - identity-based equality, not value-based
     *
     * @param o object to compare
     * @return true if same account number
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }
    
    /**
     * Hash code based on account number for consistent hashing.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
    
    /**
     * String representation for debugging and logging.
     * OOP Principle: Good toString() aids debugging
     *
     * @return readable string representation
     */
    @Override
    public String toString() {
        return String.format(
            "Account{accountNumber='%s', type=%s, customer='%s', balance=$%s, transactions=%d}",
            accountNumber, accountType, customerName, balance, transactionHistory.size()
        );
    }
}
