package com.prathamesh.banking.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a bank account with transaction history and business rule enforcement.
 * 
 * <p>Manages account state including:
 * <ul>
 *   <li>Account number and customer information</li>
 *   <li>Current balance</li>
 *   <li>Complete transaction history</li>
 *   <li>Monthly transaction counters for fee/limit enforcement</li>
 * </ul>
 * 
 * <p><b>Thread Safety:</b> This class is not thread-safe. External synchronization
 * required if accessed by multiple threads.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public class Account {
    
    // Business rule constants
    private static final BigDecimal SAVINGS_MINIMUM_BALANCE = new BigDecimal("100.00");
    private static final BigDecimal CHECKING_FEE_AMOUNT = new BigDecimal("2.50");
    private static final int CHECKING_FREE_TRANSACTIONS = 10;
    private static final int SAVINGS_MAX_WITHDRAWALS = 5;
    private static final BigDecimal SAVINGS_INTEREST_RATE = new BigDecimal("0.02"); // 2%
    
    private final String accountNumber;
    private final AccountType accountType;
    private final String customerName;
    private BigDecimal balance;
    private final List<Transaction> transactionHistory;
    private int monthlyTransactionCount;
    private int monthlyWithdrawalCount;
    
    /**
     * Creates a new bank account.
     * 
     * @param accountNumber unique account identifier (format: ACC-XXXXXXXX)
     * @param accountType type of account (CHECKING or SAVINGS)
     * @param customerName name of account holder
     * @param initialBalance starting balance (must be non-negative)
     * @throws IllegalArgumentException if initial balance is negative or violates account rules
     */
    public Account(String accountNumber, AccountType accountType, 
                   String customerName, BigDecimal initialBalance) {
        // Validate inputs
        Objects.requireNonNull(accountNumber, "Account number cannot be null");
        Objects.requireNonNull(accountType, "Account type cannot be null");
        Objects.requireNonNull(customerName, "Customer name cannot be null");
        Objects.requireNonNull(initialBalance, "Initial balance cannot be null");
        
        if (customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        
        // Validate SAVINGS account minimum balance
        if (accountType == AccountType.SAVINGS && 
            initialBalance.compareTo(SAVINGS_MINIMUM_BALANCE) < 0) {
            throw new IllegalArgumentException(
                String.format("SAVINGS account requires minimum initial balance of $%.2f", 
                            SAVINGS_MINIMUM_BALANCE));
        }
        
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.customerName = customerName;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        this.monthlyTransactionCount = 0;
        this.monthlyWithdrawalCount = 0;
    }
    
    // Getters
    
    /**
     * Returns the unique account number.
     * 
     * @return account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }
    
    /**
     * Returns the account type (CHECKING or SAVINGS).
     * 
     * @return account type
     */
    public AccountType getAccountType() {
        return accountType;
    }
    
    /**
     * Returns the customer name.
     * 
     * @return customer name
     */
    public String getCustomerName() {
        return customerName;
    }
    
    /**
     * Returns the current account balance.
     * 
     * @return current balance
     */
    public BigDecimal getBalance() {
        return balance;
    }
    
    /**
     * Returns an unmodifiable view of the transaction history.
     * 
     * @return list of all transactions
     */
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }
    
    /**
     * Returns the number of transactions this month.
     * Used for fee calculation on CHECKING accounts.
     * 
     * @return monthly transaction count
     */
    public int getMonthlyTransactionCount() {
        return monthlyTransactionCount;
    }
    
    /**
     * Returns the number of withdrawals this month.
     * Used for limit enforcement on SAVINGS accounts.
     * 
     * @return monthly withdrawal count
     */
    public int getMonthlyWithdrawalCount() {
        return monthlyWithdrawalCount;
    }
    
    // Business logic methods
    
    /**
     * Checks if a withdrawal of the specified amount is allowed.
     * 
     * <p>Validation rules:
     * <ul>
     *   <li>Amount must be positive</li>
     *   <li>Sufficient balance must be available</li>
     *   <li>SAVINGS: Must maintain minimum balance of $100</li>
     *   <li>SAVINGS: Maximum 5 withdrawals per month</li>
     * </ul>
     * 
     * @param amount amount to withdraw
     * @return null if allowed, error message if validation fails
     */
    public String canWithdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Withdrawal amount must be positive";
        }
        
        BigDecimal newBalance = balance.subtract(amount);
        
        // Check sufficient funds
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return "Insufficient funds";
        }
        
        // SAVINGS account specific rules
        if (accountType == AccountType.SAVINGS) {
            // Check minimum balance
            if (newBalance.compareTo(SAVINGS_MINIMUM_BALANCE) < 0) {
                return String.format("SAVINGS account must maintain minimum balance of $%.2f", 
                                   SAVINGS_MINIMUM_BALANCE);
            }
            
            // Check withdrawal limit
            if (monthlyWithdrawalCount >= SAVINGS_MAX_WITHDRAWALS) {
                return String.format("SAVINGS account limited to %d withdrawals per month", 
                                   SAVINGS_MAX_WITHDRAWALS);
            }
        }
        
        return null; // Validation passed
    }
    
    /**
     * Processes a successful deposit transaction.
     * 
     * @param amount amount to deposit (must be positive)
     * @return the balance after deposit
     * @throws IllegalArgumentException if amount is not positive
     */
    public BigDecimal deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        balance = balance.add(amount);
        monthlyTransactionCount++;
        
        return balance;
    }
    
    /**
     * Processes a successful withdrawal transaction.
     * Caller must validate using {@link #canWithdraw(BigDecimal)} first.
     * 
     * @param amount amount to withdraw (must be positive and validated)
     * @return the balance after withdrawal
     * @throws IllegalArgumentException if amount is not positive
     */
    public BigDecimal withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        balance = balance.subtract(amount);
        monthlyTransactionCount++;
        
        if (accountType == AccountType.SAVINGS) {
            monthlyWithdrawalCount++;
        }
        
        return balance;
    }
    
    /**
     * Adds a transaction to the account's history.
     * 
     * @param transaction transaction to record
     */
    public void addTransaction(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction cannot be null");
        transactionHistory.add(transaction);
    }
    
    /**
     * Applies monthly transaction fee for CHECKING accounts.
     * Fee is $2.50 for each transaction beyond the 10th in the month.
     * 
     * @return total fees charged, or zero if no fees apply
     */
    public BigDecimal applyMonthlyFee() {
        if (accountType != AccountType.CHECKING) {
            return BigDecimal.ZERO;
        }
        
        if (monthlyTransactionCount <= CHECKING_FREE_TRANSACTIONS) {
            return BigDecimal.ZERO;
        }
        
        int chargeableTransactions = monthlyTransactionCount - CHECKING_FREE_TRANSACTIONS;
        BigDecimal totalFee = CHECKING_FEE_AMOUNT.multiply(new BigDecimal(chargeableTransactions));
        
        balance = balance.subtract(totalFee);
        
        return totalFee;
    }
    
    /**
     * Applies monthly interest for SAVINGS accounts.
     * Interest rate is 2% applied to current balance.
     * 
     * @return interest earned, or zero if not a SAVINGS account
     */
    public BigDecimal applyMonthlyInterest() {
        if (accountType != AccountType.SAVINGS) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal interest = balance.multiply(SAVINGS_INTEREST_RATE)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        
        balance = balance.add(interest);
        
        return interest;
    }
    
    /**
     * Resets monthly counters.
     * Should be called at the start of each month.
     */
    public void resetMonthlyCounters() {
        monthlyTransactionCount = 0;
        monthlyWithdrawalCount = 0;
    }
    
    /**
     * Checks if account can be closed.
     * Account can only be closed if balance is zero.
     * 
     * @return true if balance is zero, false otherwise
     */
    public boolean canClose() {
        return balance.compareTo(BigDecimal.ZERO) == 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
    
    @Override
    public String toString() {
        return String.format("Account{number='%s', type=%s, customer='%s', balance=$%.2f, transactions=%d}",
                accountNumber, accountType, customerName, balance, transactionHistory.size());
    }
}
