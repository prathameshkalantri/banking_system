package com.prathamesh.banking.service;

import com.prathamesh.banking.model.*;
import com.prathamesh.banking.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Core banking service managing accounts and transactions.
 * 
 * <p>Provides a complete API for banking operations:
 * <ul>
 *   <li>Account management (open, close, query)</li>
 *   <li>Transaction processing (deposit, withdrawal, transfer)</li>
 *   <li>Transaction history and reporting</li>
 *   <li>Monthly processing (fees and interest)</li>
 *   <li>Statement generation</li>
 * </ul>
 * 
 * <p><b>Thread Safety:</b> Uses ConcurrentHashMap for thread-safe account storage.
 * Individual account operations are synchronized to prevent race conditions.
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public class Bank {
    
    private final Map<String, Account> accounts;
    private final TransactionValidator validator;
    
    /**
     * Creates a new Bank instance.
     */
    public Bank() {
        this.accounts = new ConcurrentHashMap<>();
        this.validator = new TransactionValidator();
    }
    
    // ========== Account Management ==========
    
    /**
     * Opens a new bank account.
     * 
     * @param customerName name of the account holder
     * @param accountType type of account (CHECKING or SAVINGS)
     * @param initialDeposit initial deposit amount
     * @return the newly created account
     * @throws IllegalArgumentException if validation fails
     */
    public Account openAccount(String customerName, AccountType accountType, BigDecimal initialDeposit) {
        // Validate inputs
        String nameError = validator.validateCustomerName(customerName);
        if (nameError != null) {
            throw new IllegalArgumentException(nameError);
        }
        
        String depositError = validator.validateInitialDeposit(accountType, initialDeposit);
        if (depositError != null) {
            throw new IllegalArgumentException(depositError);
        }
        
        // Generate account number and create account
        String accountNumber = IdGenerator.generateAccountNumber();
        Account account = new Account(accountNumber, accountType, customerName, initialDeposit);
        
        // Store account
        accounts.put(accountNumber, account);
        
        // Record initial deposit transaction
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            Transaction transaction = new Transaction.Builder()
                    .transactionId(IdGenerator.generateTransactionId())
                    .type(TransactionType.DEPOSIT)
                    .amount(initialDeposit)
                    .balanceBefore(BigDecimal.ZERO)
                    .balanceAfter(initialDeposit)
                    .status(TransactionStatus.SUCCESS)
                    .build();
            account.addTransaction(transaction);
        }
        
        return account;
    }
    
    /**
     * Closes an existing account.
     * 
     * <p>Account can only be closed if balance is zero.
     * 
     * @param accountNumber the account number to close
     * @return true if account was closed, false if not found
     * @throws IllegalStateException if account has non-zero balance
     */
    public boolean closeAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);
        
        if (account == null) {
            return false;
        }
        
        // Validate closure
        String error = validator.validateAccountClosure(account);
        if (error != null) {
            throw new IllegalStateException(error);
        }
        
        // Remove account
        accounts.remove(accountNumber);
        return true;
    }
    
    /**
     * Retrieves an account by account number.
     * 
     * @param accountNumber the account number
     * @return the account, or null if not found
     */
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
    
    /**
     * Returns all accounts in the bank.
     * 
     * @return unmodifiable collection of all accounts
     */
    public Collection<Account> getAllAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
    }
    
    /**
     * Returns the total number of accounts.
     * 
     * @return account count
     */
    public int getAccountCount() {
        return accounts.size();
    }
    
    // ========== Transaction Operations ==========
    
    /**
     * Deposits money into an account.
     * 
     * @param accountNumber the account to deposit into
     * @param amount the amount to deposit
     * @return the transaction record
     * @throws IllegalArgumentException if account not found or validation fails
     */
    public Transaction deposit(String accountNumber, BigDecimal amount) {
        Account account = getAccountOrThrow(accountNumber);
        
        // Validate deposit
        String error = validator.validateDeposit(amount);
        if (error != null) {
            // Record failed transaction
            return recordFailedTransaction(account, TransactionType.DEPOSIT, amount, error);
        }
        
        // Execute deposit
        synchronized (account) {
            BigDecimal balanceBefore = account.getBalance();
            BigDecimal balanceAfter = account.deposit(amount);
            
            // Record successful transaction
            Transaction transaction = new Transaction.Builder()
                    .transactionId(IdGenerator.generateTransactionId())
                    .type(TransactionType.DEPOSIT)
                    .amount(amount)
                    .balanceBefore(balanceBefore)
                    .balanceAfter(balanceAfter)
                    .status(TransactionStatus.SUCCESS)
                    .build();
            
            account.addTransaction(transaction);
            return transaction;
        }
    }
    
    /**
     * Withdraws money from an account.
     * 
     * @param accountNumber the account to withdraw from
     * @param amount the amount to withdraw
     * @return the transaction record
     * @throws IllegalArgumentException if account not found
     */
    public Transaction withdraw(String accountNumber, BigDecimal amount) {
        Account account = getAccountOrThrow(accountNumber);
        
        // Validate withdrawal
        String error = validator.validateWithdrawal(account, amount);
        if (error != null) {
            // Record failed transaction
            return recordFailedTransaction(account, TransactionType.WITHDRAWAL, amount, error);
        }
        
        // Execute withdrawal
        synchronized (account) {
            BigDecimal balanceBefore = account.getBalance();
            BigDecimal balanceAfter = account.withdraw(amount);
            
            // Record successful transaction
            Transaction transaction = new Transaction.Builder()
                    .transactionId(IdGenerator.generateTransactionId())
                    .type(TransactionType.WITHDRAWAL)
                    .amount(amount)
                    .balanceBefore(balanceBefore)
                    .balanceAfter(balanceAfter)
                    .status(TransactionStatus.SUCCESS)
                    .build();
            
            account.addTransaction(transaction);
            return transaction;
        }
    }
    
    /**
     * Transfers money between two accounts.
     * 
     * <p>Creates two transaction records: one withdrawal from source,
     * one deposit to destination. If either operation fails, the entire
     * transfer is rolled back.
     * 
     * @param fromAccountNumber source account
     * @param toAccountNumber destination account
     * @param amount amount to transfer
     * @return array of two transactions [withdrawal, deposit]
     * @throws IllegalArgumentException if either account not found or validation fails
     */
    public Transaction[] transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        Account fromAccount = getAccountOrThrow(fromAccountNumber);
        Account toAccount = getAccountOrThrow(toAccountNumber);
        
        // Validate transfer
        String error = validator.validateTransfer(fromAccount, toAccount, amount);
        if (error != null) {
            // Record failed transaction on source account only
            Transaction failedTxn = recordFailedTransaction(fromAccount, TransactionType.TRANSFER, amount, error);
            return new Transaction[]{failedTxn, null};
        }
        
        // Execute transfer with locking to prevent deadlock
        // Always lock accounts in consistent order (by account number)
        Account firstLock = fromAccountNumber.compareTo(toAccountNumber) < 0 ? fromAccount : toAccount;
        Account secondLock = fromAccountNumber.compareTo(toAccountNumber) < 0 ? toAccount : fromAccount;
        
        synchronized (firstLock) {
            synchronized (secondLock) {
                // Withdraw from source
                BigDecimal fromBalanceBefore = fromAccount.getBalance();
                BigDecimal fromBalanceAfter = fromAccount.withdraw(amount);
                
                Transaction withdrawalTxn = new Transaction.Builder()
                        .transactionId(IdGenerator.generateTransactionId())
                        .type(TransactionType.TRANSFER)
                        .amount(amount)
                        .balanceBefore(fromBalanceBefore)
                        .balanceAfter(fromBalanceAfter)
                        .status(TransactionStatus.SUCCESS)
                        .build();
                
                // Deposit to destination
                BigDecimal toBalanceBefore = toAccount.getBalance();
                BigDecimal toBalanceAfter = toAccount.deposit(amount);
                
                Transaction depositTxn = new Transaction.Builder()
                        .transactionId(IdGenerator.generateTransactionId())
                        .type(TransactionType.TRANSFER)
                        .amount(amount)
                        .balanceBefore(toBalanceBefore)
                        .balanceAfter(toBalanceAfter)
                        .status(TransactionStatus.SUCCESS)
                        .build();
                
                // Record transactions
                fromAccount.addTransaction(withdrawalTxn);
                toAccount.addTransaction(depositTxn);
                
                return new Transaction[]{withdrawalTxn, depositTxn};
            }
        }
    }
    
    // ========== Transaction History ==========
    
    /**
     * Retrieves transaction history for an account within a date range.
     * 
     * @param accountNumber the account number
     * @param startDate start of date range (inclusive)
     * @param endDate end of date range (inclusive)
     * @return list of transactions within the date range
     * @throws IllegalArgumentException if account not found
     */
    public List<Transaction> getTransactionHistory(String accountNumber, LocalDate startDate, LocalDate endDate) {
        Account account = getAccountOrThrow(accountNumber);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        return account.getTransactionHistory().stream()
                .filter(txn -> !txn.getTimestamp().isBefore(startDateTime) && 
                              !txn.getTimestamp().isAfter(endDateTime))
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all transaction history for an account.
     * 
     * @param accountNumber the account number
     * @return list of all transactions
     * @throws IllegalArgumentException if account not found
     */
    public List<Transaction> getTransactionHistory(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        return new ArrayList<>(account.getTransactionHistory());
    }
    
    // ========== Monthly Processing ==========
    
    /**
     * Applies monthly interest to all SAVINGS accounts.
     * 
     * @return total interest paid across all accounts
     */
    public BigDecimal applyMonthlyInterest() {
        BigDecimal totalInterest = BigDecimal.ZERO;
        
        for (Account account : accounts.values()) {
            if (account.getAccountType() == AccountType.SAVINGS) {
                synchronized (account) {
                    BigDecimal interest = account.applyMonthlyInterest();
                    if (interest.compareTo(BigDecimal.ZERO) > 0) {
                        totalInterest = totalInterest.add(interest);
                        
                        // Record interest transaction
                        Transaction transaction = new Transaction.Builder()
                                .transactionId(IdGenerator.generateTransactionId())
                                .type(TransactionType.DEPOSIT)
                                .amount(interest)
                                .balanceBefore(account.getBalance().subtract(interest))
                                .balanceAfter(account.getBalance())
                                .status(TransactionStatus.SUCCESS)
                                .build();
                        account.addTransaction(transaction);
                    }
                }
            }
        }
        
        return totalInterest;
    }
    
    /**
     * Applies monthly fees to all CHECKING accounts and resets counters.
     * 
     * @return total fees collected across all accounts
     */
    public BigDecimal applyMonthlyFeesAndResetCounters() {
        BigDecimal totalFees = BigDecimal.ZERO;
        
        for (Account account : accounts.values()) {
            synchronized (account) {
                // Apply fees for CHECKING accounts
                if (account.getAccountType() == AccountType.CHECKING) {
                    BigDecimal fee = account.applyMonthlyFee();
                    if (fee.compareTo(BigDecimal.ZERO) > 0) {
                        totalFees = totalFees.add(fee);
                        
                        // Record fee transaction
                        Transaction transaction = new Transaction.Builder()
                                .transactionId(IdGenerator.generateTransactionId())
                                .type(TransactionType.WITHDRAWAL)
                                .amount(fee)
                                .balanceBefore(account.getBalance().add(fee))
                                .balanceAfter(account.getBalance())
                                .status(TransactionStatus.SUCCESS)
                                .build();
                        account.addTransaction(transaction);
                    }
                }
                
                // Reset monthly counters for all accounts
                account.resetMonthlyCounters();
            }
        }
        
        return totalFees;
    }
    
    // ========== Statement Generation ==========
    
    /**
     * Generates a monthly statement for an account.
     * 
     * @param accountNumber the account number
     * @return formatted statement string
     * @throws IllegalArgumentException if account not found
     */
    public String generateMonthlyStatement(String accountNumber) {
        Account account = getAccountOrThrow(accountNumber);
        
        StringBuilder statement = new StringBuilder();
        statement.append("=".repeat(70)).append("\n");
        statement.append("                    MONTHLY ACCOUNT STATEMENT\n");
        statement.append("=".repeat(70)).append("\n\n");
        
        statement.append(String.format("Account Number: %s\n", account.getAccountNumber()));
        statement.append(String.format("Account Type:   %s\n", account.getAccountType()));
        statement.append(String.format("Customer Name:  %s\n", account.getCustomerName()));
        statement.append(String.format("Current Balance: $%.2f\n\n", account.getBalance()));
        
        statement.append("Transaction History:\n");
        statement.append("-".repeat(70)).append("\n");
        statement.append(String.format("%-20s %-12s %-10s %-12s %-12s %-10s\n",
                "Date/Time", "Type", "Amount", "Bal. Before", "Bal. After", "Status"));
        statement.append("-".repeat(70)).append("\n");
        
        List<Transaction> transactions = account.getTransactionHistory();
        if (transactions.isEmpty()) {
            statement.append("No transactions this period.\n");
        } else {
            for (Transaction txn : transactions) {
                statement.append(String.format("%-20s %-12s $%-9.2f $%-11.2f $%-11.2f %-10s\n",
                        txn.getTimestamp().toString().substring(0, 19),
                        txn.getType(),
                        txn.getAmount(),
                        txn.getBalanceBefore(),
                        txn.getBalanceAfter(),
                        txn.getStatus()));
                
                if (txn.getStatus() == TransactionStatus.FAILED) {
                    statement.append(String.format("  Reason: %s\n", txn.getFailureReason()));
                }
            }
        }
        
        statement.append("-".repeat(70)).append("\n");
        statement.append(String.format("Total Transactions: %d\n", transactions.size()));
        statement.append(String.format("Ending Balance: $%.2f\n", account.getBalance()));
        statement.append("=".repeat(70)).append("\n");
        
        return statement.toString();
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Gets an account or throws exception if not found.
     * 
     * @param accountNumber account number
     * @return the account
     * @throws IllegalArgumentException if account not found
     */
    private Account getAccountOrThrow(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return account;
    }
    
    /**
     * Records a failed transaction.
     * 
     * @param account the account
     * @param type transaction type
     * @param amount attempted amount
     * @param failureReason reason for failure
     * @return the failed transaction
     */
    private Transaction recordFailedTransaction(Account account, TransactionType type, 
                                               BigDecimal amount, String failureReason) {
        BigDecimal currentBalance = account.getBalance();
        
        Transaction transaction = new Transaction.Builder()
                .transactionId(IdGenerator.generateTransactionId())
                .type(type)
                .amount(amount)
                .balanceBefore(currentBalance)
                .balanceAfter(currentBalance)
                .status(TransactionStatus.FAILED)
                .failureReason(failureReason)
                .build();
        
        account.addTransaction(transaction);
        return transaction;
    }
}
