package com.prathamesh.banking.service;

import com.prathamesh.banking.domain.*;
import com.prathamesh.banking.exception.*;
import com.prathamesh.banking.strategy.AccountStrategy;
import com.prathamesh.banking.strategy.AccountStrategyFactory;
import com.prathamesh.banking.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bank service class - main orchestration layer for all banking operations.
 * 
 * OOP Principles Applied:
 * 1. Encapsulation - Private accounts map, controlled access through methods
 * 2. Single Responsibility - Manages accounts and orchestrates operations
 * 3. Facade Pattern - Provides simplified interface to complex subsystem
 * 4. Delegation - Delegates validation to Strategy pattern
 * 
 * Design Patterns:
 * 1. Repository Pattern - Acts as in-memory repository for accounts
 * 2. Facade Pattern - Simplifies interaction with Account, Strategy, Transaction
 * 3. Template Method - Transfer follows template (validate → execute → record)
 * 
 * Thread Safety:
 * - Current implementation is NOT thread-safe (single-threaded assumption)
 * - For concurrent access, add synchronized blocks or use ConcurrentHashMap
 * - Transfer atomicity achieved through try-catch rollback mechanism
 */
public class Bank {
    
    // Repository - In-memory storage of accounts
    private final Map<String, Account> accounts;
    
    /**
     * Constructs a new Bank with empty account registry.
     * OOP Principle: Encapsulation - Internal state hidden from clients
     */
    public Bank() {
        this.accounts = new HashMap<>();
    }
    
    // ==================== REQUIRED CORE OPERATIONS ====================
    
    /**
     * Opens a new account.
     * 
     * Process:
     * 1. Generate unique account number
     * 2. Create Account entity
     * 3. Store in repository
     * 
     * OOP Principle: Factory method - Creates and initializes complex objects
     * Business Rule: Initial deposit validated by Account constructor
     *
     * @param customerName name of the account holder
     * @param accountType type of account (CHECKING or SAVINGS)
     * @param initialDeposit initial deposit amount (must be non-negative)
     * @return the newly created account
     * @throws IllegalArgumentException if parameters are invalid
     * @throws MinimumBalanceViolationException if SAVINGS account doesn't meet minimum
     */
    public Account openAccount(String customerName, AccountType accountType, BigDecimal initialDeposit) {
        // Validate inputs
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit must be non-negative");
        }
        
        // Business Rule: SAVINGS requires $100 minimum balance
        if (accountType == AccountType.SAVINGS && initialDeposit.compareTo(new BigDecimal("100.00")) < 0) {
            throw new MinimumBalanceViolationException(
                "NEW-ACCOUNT",
                new BigDecimal("100.00"),
                initialDeposit
            );
        }
        
        // Generate unique account number
        String accountNumber = IdGenerator.generateAccountId();
        
        // Create account entity
        Account account = new Account(accountNumber, customerName, accountType, initialDeposit);
        
        // Store in repository
        accounts.put(accountNumber, account);
        
        return account;
    }
    
    /**
     * Closes an account.
     * 
     * Business Rule: Account can only be closed if balance is zero
     * 
     * OOP Principle: Delegation - Uses Account.canBeClosed() for business logic
     *
     * @param accountNumber the account number to close
     * @throws AccountNotFoundException if account doesn't exist
     * @throws InvalidOperationException if balance is not zero
     */
    public void closeAccount(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        
        if (!account.canBeClosed()) {
            throw new InvalidOperationException(
                "Cannot close account " + accountNumber + 
                " with non-zero balance: $" + account.getBalance()
            );
        }
        
        accounts.remove(accountNumber);
    }
    
    /**
     * Deposits money into an account.
     * 
     * Process:
     * 1. Validate account exists
     * 2. Validate amount
     * 3. Execute deposit
     * 4. Return transaction record
     * 
     * OOP Principle: Delegation - Account handles state change, Bank orchestrates
     *
     * @param accountNumber the account to deposit into
     * @param amount the amount to deposit (must be positive)
     * @return the transaction record
     * @throws AccountNotFoundException if account doesn't exist
     * @throws IllegalArgumentException if amount is invalid
     */
    public Transaction deposit(String accountNumber, BigDecimal amount) {
        Account account = getAccountOrThrow(accountNumber);
        
        // Validate amount (defensive)
        validatePositiveAmount(amount);
        
        // Generate transaction ID
        String transactionId = IdGenerator.generateTransactionId();
        
        // Execute deposit (Account handles state change)
        return account.deposit(amount, transactionId);
    }
    
    /**
     * Withdraws money from an account.
     * 
     * Process:
     * 1. Validate account exists
     * 2. Get strategy for account type
     * 3. Validate withdrawal using strategy (business rules)
     * 4. Execute withdrawal
     * 5. Return transaction record OR record failed transaction
     * 
     * OOP Principle: Strategy Pattern - Delegates validation to account-specific strategy
     * Design Pattern: Template Method - Fixed sequence of steps
     *
     * @param accountNumber the account to withdraw from
     * @param amount the amount to withdraw (must be positive)
     * @return the transaction record (SUCCESS or FAILED)
     * @throws AccountNotFoundException if account doesn't exist
     * @throws IllegalArgumentException if amount is invalid
     */
    public Transaction withdraw(String accountNumber, BigDecimal amount) {
        Account account = getAccountOrThrow(accountNumber);
        
        // Validate amount (defensive)
        validatePositiveAmount(amount);
        
        // Get strategy for this account type
        AccountStrategy strategy = AccountStrategyFactory.getStrategy(account.getAccountType());
        
        // Generate transaction ID
        String transactionId = IdGenerator.generateTransactionId();
        
        try {
            // Validate using strategy (throws exception if invalid)
            strategy.validateWithdrawal(account, amount);
            
            // Execute withdrawal (Account handles state change)
            return account.withdraw(amount, transactionId);
            
        } catch (BankingException e) {
            // Business Rule: Record failed transactions
            return account.recordFailedTransaction(
                TransactionType.WITHDRAWAL,
                amount,
                e.getMessage(),
                transactionId
            );
        }
    }
    
    /**
     * Transfers money between two accounts.
     * 
     * CRITICAL: This operation is ATOMIC - both succeed or both fail.
     * 
     * Process:
     * 1. Validate both accounts exist
     * 2. Validate amount
     * 3. Get strategy for source account
     * 4. Validate withdrawal using strategy
     * 5. Execute withdrawal from source (creates TRANSFER transaction)
     * 6. Execute deposit to destination (creates TRANSFER transaction)
     * 7. If deposit fails, ROLLBACK withdrawal
     * 
     * OOP Principles:
     * - Atomicity through try-catch rollback
     * - Transaction integrity maintained
     * - Failed operations recorded
     * 
     * Design Pattern: Template Method - Fixed sequence with rollback capability
     *
     * @param fromAccountNumber the account to transfer from
     * @param toAccountNumber the account to transfer to
     * @param amount the amount to transfer (must be positive)
     * @return the transaction record from source account (SUCCESS or FAILED)
     * @throws AccountNotFoundException if either account doesn't exist
     * @throws IllegalArgumentException if amount is invalid or accounts are same
     */
    public Transaction transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        // Validate accounts exist
        Account fromAccount = getAccountOrThrow(fromAccountNumber);
        Account toAccount = getAccountOrThrow(toAccountNumber);
        
        // Validate not same account
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        // Validate amount (defensive)
        validatePositiveAmount(amount);
        
        // Get strategy for source account
        AccountStrategy strategy = AccountStrategyFactory.getStrategy(fromAccount.getAccountType());
        
        // Generate transaction ID
        String transactionId = IdGenerator.generateTransactionId();
        
        try {
            // Step 1: Validate withdrawal using strategy
            strategy.validateWithdrawal(fromAccount, amount);
            
            // Step 2: Execute withdrawal from source (uses transferOut to NOT increment withdrawal counter)
            Transaction withdrawalTx = fromAccount.transferOut(amount, transactionId);
            
            // Step 3: Execute deposit to destination
            try {
                String depositTxId = IdGenerator.generateTransactionId();
                toAccount.deposit(amount, depositTxId);
                
                // SUCCESS - both operations completed
                return withdrawalTx;
                
            } catch (Exception depositException) {
                // ROLLBACK: Deposit failed, reverse the withdrawal
                String rollbackTxId = IdGenerator.generateTransactionId();
                fromAccount.deposit(amount, rollbackTxId); // Put money back
                
                // Record failed transfer
                return fromAccount.recordFailedTransaction(
                    TransactionType.TRANSFER,
                    amount,
                    "Transfer failed during deposit phase: " + depositException.getMessage(),
                    transactionId
                );
            }
            
        } catch (BankingException e) {
            // Validation failed - record failed transaction
            return fromAccount.recordFailedTransaction(
                TransactionType.TRANSFER,
                amount,
                e.getMessage(),
                transactionId
            );
        }
    }
    
    /**
     * Returns transaction history for an account.
     * 
     * OOP Principle: Delegation - Account maintains history, Bank provides access
     *
     * @param accountNumber the account number
     * @return unmodifiable list of all transactions
     * @throws AccountNotFoundException if account doesn't exist
     */
    public List<Transaction> getTransactionHistory(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        return account.getTransactionHistory();
    }
    
    /**
     * Applies monthly interest to all accounts.
     * 
     * Process:
     * 1. Iterate through all accounts
     * 2. Get strategy for each account type
     * 3. Delegate to strategy.applyMonthlyAdjustments()
     * 
     * OOP Principle: Strategy Pattern - Each account type handles its own adjustments
     * Design Pattern: Facade - Simplifies complex operation across multiple accounts
     */
    public void applyMonthlyInterest() {
        for (Account account : accounts.values()) {
            AccountStrategy strategy = AccountStrategyFactory.getStrategy(account.getAccountType());
            strategy.applyMonthlyAdjustments(account);
        }
    }
    
    /**
     * Generates a monthly statement for an account.
     * 
     * Format:
     * - Account summary
     * - Transaction list
     * - Monthly statistics
     * 
     * OOP Principle: Information Expert - Bank has access to all data needed
     *
     * @param accountNumber the account number
     * @return formatted monthly statement
     * @throws AccountNotFoundException if account doesn't exist
     */
    public String generateMonthlyStatement(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        AccountStrategy strategy = AccountStrategyFactory.getStrategy(account.getAccountType());
        
        StringBuilder statement = new StringBuilder();
        statement.append("========================================\n");
        statement.append("        MONTHLY ACCOUNT STATEMENT       \n");
        statement.append("========================================\n\n");
        
        // Account Information
        statement.append("Account Number: ").append(account.getAccountNumber()).append("\n");
        statement.append("Customer Name: ").append(account.getCustomerName()).append("\n");
        statement.append("Account Type: ").append(account.getAccountType()).append("\n");
        statement.append("Current Balance: $").append(account.getBalance()).append("\n\n");
        
        // Monthly Statistics
        statement.append("--- Monthly Statistics ---\n");
        statement.append("Total Transactions: ").append(account.getMonthlyTransactionCount()).append("\n");
        statement.append("Withdrawals This Month: ").append(account.getMonthlyWithdrawalCount()).append("\n\n");
        
        // Business Rules
        statement.append("--- Account Rules ---\n");
        statement.append(strategy.getBusinessRulesDescription()).append("\n\n");
        
        // Transaction History
        statement.append("--- Transaction History ---\n");
        List<Transaction> transactions = account.getTransactionHistory();
        if (transactions.isEmpty()) {
            statement.append("No transactions this period.\n");
        } else {
            for (Transaction tx : transactions) {
                statement.append(String.format("%s | %s | %s | $%s | Status: %s\n",
                    tx.getTimestamp().toString(),
                    tx.getType(),
                    tx.getTransactionId(),
                    tx.getAmount(),
                    tx.getStatus()
                ));
            }
        }
        
        statement.append("\n========================================\n");
        return statement.toString();
    }
    
    // ==================== ADDITIONAL METHODS (OVER-DELIVERY) ====================
    
    /**
     * Returns filtered transaction history for an account.
     * 
     * OOP Principle: Information Expert + Filtering logic
     * Design Pattern: Specification pattern (via Stream API)
     *
     * @param accountNumber the account number
     * @param startDate start of date range (inclusive, null = no lower bound)
     * @param endDate end of date range (inclusive, null = no upper bound)
     * @param type transaction type filter (null = all types)
     * @return filtered list of transactions
     * @throws AccountNotFoundException if account doesn't exist
     */
    public List<Transaction> getTransactionHistory(String accountNumber, 
                                                    LocalDateTime startDate, 
                                                    LocalDateTime endDate, 
                                                    TransactionType type) {
        Account account = getAccountOrThrow(accountNumber);
        
        return account.getTransactionHistory().stream()
            .filter(tx -> startDate == null || !tx.getTimestamp().isBefore(startDate))
            .filter(tx -> endDate == null || !tx.getTimestamp().isAfter(endDate))
            .filter(tx -> type == null || tx.getType() == type)
            .collect(Collectors.toList());
    }
    
    /**
     * Returns only failed transactions for an account.
     * 
     * OOP Principle: Single Responsibility - Bank provides different views of data
     *
     * @param accountNumber the account number
     * @return list of failed transactions
     * @throws AccountNotFoundException if account doesn't exist
     */
    public List<Transaction> getFailedTransactions(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        
        return account.getTransactionHistory().stream()
            .filter(Transaction::isFailed)
            .collect(Collectors.toList());
    }
    
    /**
     * Applies monthly fees to all accounts.
     * Alias for applyMonthlyInterest() - strategies handle both fees and interest.
     */
    public void applyMonthlyFees() {
        applyMonthlyInterest(); // Strategies handle both fees and interest
    }
    
    /**
     * Applies monthly adjustments (fees + interest) to all accounts.
     * This is the main end-of-month operation.
     * 
     * OOP Principle: Facade - Single method for complex end-of-month processing
     */
    public void applyMonthlyAdjustments() {
        applyMonthlyInterest(); // Strategy pattern handles all account-specific logic
    }
    
    /**
     * Returns all accounts in the bank.
     * 
     * OOP Principle: Encapsulation - Returns unmodifiable collection
     *
     * @return unmodifiable collection of all accounts
     */
    public Collection<Account> getAllAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
    }
    
    /**
     * Returns accounts of a specific type.
     * 
     * OOP Principle: Information Expert - Bank can filter its own data
     *
     * @param type the account type to filter by
     * @return list of accounts of specified type
     * @throws IllegalArgumentException if type is null
     */
    public List<Account> getAccountsByType(AccountType type) {
        if (type == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        
        return accounts.values().stream()
            .filter(account -> account.getAccountType() == type)
            .collect(Collectors.toList());
    }
    
    /**
     * Returns the number of accounts in the bank.
     * 
     * @return total account count
     */
    public int getAccountCount() {
        return accounts.size();
    }
    
    /**
     * Checks if an account exists.
     * 
     * @param accountNumber the account number to check
     * @return true if account exists, false otherwise
     */
    public boolean accountExists(String accountNumber) {
        return accountNumber != null && accounts.containsKey(accountNumber);
    }
    
    /**
     * Returns an account by account number.
     * 
     * @param accountNumber the account number
     * @return the account
     * @throws AccountNotFoundException if account doesn't exist
     */
    public Account getAccount(String accountNumber) {
        return getAccountOrThrow(accountNumber);
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    /**
     * Retrieves account or throws exception if not found.
     * OOP Principle: DRY (Don't Repeat Yourself) - Centralized validation
     *
     * @param accountNumber the account number
     * @return the account
     * @throws AccountNotFoundException if account doesn't exist
     */
    private Account getAccountOrThrow(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        
        return account;
    }
    
    /**
     * Validates that amount is positive.
     * OOP Principle: DRY - Centralized validation logic
     *
     * @param amount the amount to validate
     * @throws IllegalArgumentException if amount is null, negative, or zero
     */
    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive: " + amount);
        }
    }
}
