package com.prathamesh.banking;

import com.prathamesh.banking.domain.Account;
import com.prathamesh.banking.domain.AccountType;
import com.prathamesh.banking.domain.Transaction;
import com.prathamesh.banking.domain.TransactionStatus;
import com.prathamesh.banking.exception.*;
import com.prathamesh.banking.service.Bank;
import com.prathamesh.banking.util.ConsoleFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Main demonstration application for Banking System.
 * 
 * Demonstrates:
 * 1. Account creation (CHECKING and SAVINGS)
 * 2. Successful transactions (deposits, withdrawals)
 * 3. Failed transactions (edge cases)
 * 4. Transfers (successful and failed)
 * 5. Monthly adjustments (fees and interest)
 * 6. Account statements
 * 7. Bank summary
 * 
 * OOP Principles Demonstrated:
 * - Encapsulation: Client code uses Bank API without knowing internals
 * - Abstraction: Bank hides complexity of strategy pattern
 * - Polymorphism: Different account types handled uniformly
 * - Composition: Main HAS-A Bank, Bank HAS-A collection of Accounts
 */
public class Main {
    
    private static final Bank bank = new Bank();
    
    public static void main(String[] args) {
        try {
            ConsoleFormatter.printSectionHeader("BANKING SYSTEM DEMONSTRATION");
            ConsoleFormatter.printHighlight("Comprehensive Demo of All Features");
            ConsoleFormatter.printBlankLines(2);
            
            // Section 1: Create Accounts
            section1_CreateAccounts();
            pauseForReading();
            
            // Section 2: Successful Transactions
            section2_SuccessfulTransactions();
            pauseForReading();
            
            // Section 3: Failed Transactions (Edge Cases)
            section3_FailedTransactions();
            pauseForReading();
            
            // Section 4: Transfers
            section4_Transfers();
            pauseForReading();
            
            // Section 5: Monthly Adjustments
            section5_MonthlyAdjustments();
            pauseForReading();
            
            // Section 6: Account Statements
            section6_AccountStatements();
            pauseForReading();
            
            // Section 7: Bank Summary
            section7_BankSummary();
            
            ConsoleFormatter.printBlankLines(2);
            ConsoleFormatter.printSectionHeader("DEMONSTRATION COMPLETE");
            ConsoleFormatter.printSuccess("All features demonstrated successfully!");
            
        } catch (Exception e) {
            ConsoleFormatter.printError("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ==================== SECTION 1: CREATE ACCOUNTS ====================
    
    private static void section1_CreateAccounts() {
        ConsoleFormatter.printSectionHeader("SECTION 1: ACCOUNT CREATION");
        ConsoleFormatter.printInfo("Creating 4 accounts: 2 CHECKING, 2 SAVINGS");
        ConsoleFormatter.printBlankLine();
        
        try {
            // Create 2 Checking Accounts
            Account checking1 = bank.openAccount("Alice Johnson", AccountType.CHECKING, new BigDecimal("1000.00"));
            ConsoleFormatter.printSuccess("Created: " + checking1.getAccountNumber() + 
                                        " - " + checking1.getCustomerName() + 
                                        " (CHECKING) - Initial: $1,000.00");
            
            Account checking2 = bank.openAccount("Bob Smith", AccountType.CHECKING, new BigDecimal("500.00"));
            ConsoleFormatter.printSuccess("Created: " + checking2.getAccountNumber() + 
                                        " - " + checking2.getCustomerName() + 
                                        " (CHECKING) - Initial: $500.00");
            
            // Create 2 Savings Accounts
            Account savings1 = bank.openAccount("Carol Davis", AccountType.SAVINGS, new BigDecimal("5000.00"));
            ConsoleFormatter.printSuccess("Created: " + savings1.getAccountNumber() + 
                                        " - " + savings1.getCustomerName() + 
                                        " (SAVINGS) - Initial: $5,000.00");
            
            Account savings2 = bank.openAccount("David Wilson", AccountType.SAVINGS, new BigDecimal("150.00"));
            ConsoleFormatter.printSuccess("Created: " + savings2.getAccountNumber() + 
                                        " - " + savings2.getCustomerName() + 
                                        " (SAVINGS) - Initial: $150.00");
            
            ConsoleFormatter.printBlankLine();
            ConsoleFormatter.printHighlight("Total Accounts Created: 4");
            
        } catch (Exception e) {
            ConsoleFormatter.printError("Account creation failed: " + e.getMessage());
        }
    }
    
    // ==================== SECTION 2: SUCCESSFUL TRANSACTIONS ====================
    
    private static void section2_SuccessfulTransactions() {
        ConsoleFormatter.printSectionHeader("SECTION 2: SUCCESSFUL TRANSACTIONS");
        ConsoleFormatter.printInfo("Executing 10+ successful transactions");
        ConsoleFormatter.printBlankLine();
        
        List<Account> accounts = new ArrayList<>(bank.getAllAccounts());
        if (accounts.size() < 4) {
            ConsoleFormatter.printError("Not enough accounts for demo");
            return;
        }
        
        Account checking1 = accounts.get(0);
        Account checking2 = accounts.get(1);
        Account savings1 = accounts.get(2);
        Account savings2 = accounts.get(3);
        
        try {
            // Deposits
            ConsoleFormatter.printSubsectionHeader("Deposits");
            Transaction tx1 = bank.deposit(checking1.getAccountNumber(), new BigDecimal("250.00"));
            ConsoleFormatter.printSuccess("Deposited $250.00 to " + checking1.getAccountNumber() + 
                                        " - New Balance: $" + checking1.getBalance());
            
            Transaction tx2 = bank.deposit(savings1.getAccountNumber(), new BigDecimal("1000.00"));
            ConsoleFormatter.printSuccess("Deposited $1,000.00 to " + savings1.getAccountNumber() + 
                                        " - New Balance: $" + savings1.getBalance());
            
            Transaction tx3 = bank.deposit(checking2.getAccountNumber(), new BigDecimal("500.00"));
            ConsoleFormatter.printSuccess("Deposited $500.00 to " + checking2.getAccountNumber() + 
                                        " - New Balance: $" + checking2.getBalance());
            
            // Withdrawals
            ConsoleFormatter.printSubsectionHeader("Withdrawals");
            Transaction tx4 = bank.withdraw(checking1.getAccountNumber(), new BigDecimal("100.00"));
            ConsoleFormatter.printSuccess("Withdrew $100.00 from " + checking1.getAccountNumber() + 
                                        " - New Balance: $" + checking1.getBalance());
            
            Transaction tx5 = bank.withdraw(savings1.getAccountNumber(), new BigDecimal("500.00"));
            ConsoleFormatter.printSuccess("Withdrew $500.00 from " + savings1.getAccountNumber() + 
                                        " - New Balance: $" + savings1.getBalance());
            
            Transaction tx6 = bank.withdraw(checking2.getAccountNumber(), new BigDecimal("200.00"));
            ConsoleFormatter.printSuccess("Withdrew $200.00 from " + checking2.getAccountNumber() + 
                                        " - New Balance: $" + checking2.getBalance());
            
            // More deposits
            ConsoleFormatter.printSubsectionHeader("Additional Deposits");
            Transaction tx7 = bank.deposit(savings2.getAccountNumber(), new BigDecimal("50.00"));
            ConsoleFormatter.printSuccess("Deposited $50.00 to " + savings2.getAccountNumber() + 
                                        " - New Balance: $" + savings2.getBalance());
            
            Transaction tx8 = bank.deposit(checking1.getAccountNumber(), new BigDecimal("300.00"));
            ConsoleFormatter.printSuccess("Deposited $300.00 to " + checking1.getAccountNumber() + 
                                        " - New Balance: $" + checking1.getBalance());
            
            // More withdrawals
            ConsoleFormatter.printSubsectionHeader("Additional Withdrawals");
            Transaction tx9 = bank.withdraw(checking1.getAccountNumber(), new BigDecimal("50.00"));
            ConsoleFormatter.printSuccess("Withdrew $50.00 from " + checking1.getAccountNumber() + 
                                        " - New Balance: $" + checking1.getBalance());
            
            Transaction tx10 = bank.withdraw(savings1.getAccountNumber(), new BigDecimal("200.00"));
            ConsoleFormatter.printSuccess("Withdrew $200.00 from " + savings1.getAccountNumber() + 
                                        " - New Balance: $" + savings1.getBalance());
            
            Transaction tx11 = bank.deposit(checking2.getAccountNumber(), new BigDecimal("150.00"));
            ConsoleFormatter.printSuccess("Deposited $150.00 to " + checking2.getAccountNumber() + 
                                        " - New Balance: $" + checking2.getBalance());
            
            ConsoleFormatter.printBlankLine();
            ConsoleFormatter.printHighlight("Total Successful Transactions: 11");
            
        } catch (Exception e) {
            ConsoleFormatter.printError("Transaction failed: " + e.getMessage());
        }
    }
    
    // ==================== SECTION 3: FAILED TRANSACTIONS ====================
    
    private static void section3_FailedTransactions() {
        ConsoleFormatter.printSectionHeader("SECTION 3: FAILED TRANSACTIONS (Edge Cases)");
        ConsoleFormatter.printInfo("Demonstrating business rule violations");
        ConsoleFormatter.printBlankLine();
        
        List<Account> accounts = new ArrayList<>(bank.getAllAccounts());
        if (accounts.size() < 4) {
            ConsoleFormatter.printError("Not enough accounts for demo");
            return;
        }
        
        Account checking1 = accounts.get(0);
        Account savings1 = accounts.get(2);
        Account savings2 = accounts.get(3);
        
        // Test 1: Insufficient Funds
        ConsoleFormatter.printSubsectionHeader("Test 1: Insufficient Funds");
        BigDecimal currentBalance = checking1.getBalance();
        BigDecimal excessiveAmount = currentBalance.add(new BigDecimal("100.00"));
        ConsoleFormatter.printInfo("Attempting to withdraw $" + excessiveAmount + 
                                 " from account with balance $" + currentBalance);
        Transaction txn1 = bank.withdraw(checking1.getAccountNumber(), excessiveAmount);
        if (txn1.getStatus() == TransactionStatus.FAILED) {
            ConsoleFormatter.printWarning("EXPECTED FAILURE: " + txn1.getFailureReason());
            ConsoleFormatter.printSuccess("Business rule enforced: Cannot overdraw account");
        } else {
            ConsoleFormatter.printError("Should have failed but didn't!");
        }
        ConsoleFormatter.printBlankLine();
        
        // Test 2: Minimum Balance Violation (Savings)
        ConsoleFormatter.printSubsectionHeader("Test 2: Minimum Balance Violation");
        BigDecimal savingsBalance = savings2.getBalance();
        BigDecimal amountThatViolatesMin = savingsBalance.subtract(new BigDecimal("90.00"));
        ConsoleFormatter.printInfo("Attempting to withdraw $" + amountThatViolatesMin + 
                                 " from savings account with $" + savingsBalance + 
                                 " (would leave $90, minimum is $100)");
        Transaction txn2 = bank.withdraw(savings2.getAccountNumber(), amountThatViolatesMin);
        if (txn2.getStatus() == TransactionStatus.FAILED) {
            ConsoleFormatter.printWarning("EXPECTED FAILURE: " + txn2.getFailureReason());
            ConsoleFormatter.printSuccess("Business rule enforced: Savings must maintain $100 minimum");
        } else {
            ConsoleFormatter.printError("Should have failed but didn't!");
        }
        ConsoleFormatter.printBlankLine();
        
        // Test 3: Withdrawal Limit Exceeded (Savings)
        ConsoleFormatter.printSubsectionHeader("Test 3: Monthly Withdrawal Limit");
        
        // Create a fresh savings account for this test to ensure clean withdrawal count
        Account freshSavings = bank.openAccount("Limit Test User", AccountType.SAVINGS, new BigDecimal("500.00"));
        ConsoleFormatter.printInfo("Created fresh savings account " + freshSavings.getAccountNumber() + " with $500.00");
        ConsoleFormatter.printInfo("Testing 6 withdrawals (limit is 5 per month)");
        ConsoleFormatter.printBlankLine();
        
        // First 5 withdrawals should succeed
        for (int i = 1; i <= 5; i++) {
            Transaction txn = bank.withdraw(freshSavings.getAccountNumber(), new BigDecimal("10.00"));
            if (txn.getStatus() == TransactionStatus.SUCCESS) {
                ConsoleFormatter.printInfo("Withdrawal " + i + " of 5: Success (Balance: $" + freshSavings.getBalance() + ")");
            } else {
                ConsoleFormatter.printInfo("Withdrawal " + i + " of 5: Failed - " + txn.getFailureReason());
            }
        }
        
        // 6th withdrawal should fail
        ConsoleFormatter.printBlankLine();
        ConsoleFormatter.printInfo("Attempting 6th withdrawal...");
        Transaction txn3 = bank.withdraw(freshSavings.getAccountNumber(), new BigDecimal("10.00"));
        if (txn3.getStatus() == TransactionStatus.FAILED) {
            ConsoleFormatter.printWarning("EXPECTED FAILURE: " + txn3.getFailureReason());
            ConsoleFormatter.printSuccess("Business rule enforced: Max 5 withdrawals per month for savings");
        } else {
            ConsoleFormatter.printError("Should have failed but didn't!");
        }
        ConsoleFormatter.printBlankLine();
        
        // Test 4: Account Not Found
        ConsoleFormatter.printSubsectionHeader("Test 4: Account Not Found");
        try {
            ConsoleFormatter.printInfo("Attempting to deposit to non-existent account");
            bank.deposit("ACC-99999999", new BigDecimal("100.00"));
            ConsoleFormatter.printError("Should have failed but didn't!");
        } catch (AccountNotFoundException e) {
            ConsoleFormatter.printWarning("EXPECTED FAILURE: " + e.getMessage());
            ConsoleFormatter.printSuccess("Business rule enforced: Account must exist");
        }
        ConsoleFormatter.printBlankLine();
        
        ConsoleFormatter.printHighlight("Total Failed Transactions Tested: 4");
        ConsoleFormatter.printSuccess("All business rules properly enforced!");
    }
    
    // ==================== SECTION 4: TRANSFERS ====================
    
    private static void section4_Transfers() {
        ConsoleFormatter.printSectionHeader("SECTION 4: ACCOUNT TRANSFERS");
        ConsoleFormatter.printInfo("Testing successful and failed transfers");
        ConsoleFormatter.printBlankLine();
        
        List<Account> accounts = new ArrayList<>(bank.getAllAccounts());
        if (accounts.size() < 4) {
            ConsoleFormatter.printError("Not enough accounts for demo");
            return;
        }
        
        Account checking1 = accounts.get(0);
        Account checking2 = accounts.get(1);
        Account savings1 = accounts.get(2);
        
        // Successful Transfer
        ConsoleFormatter.printSubsectionHeader("Successful Transfer");
        try {
            BigDecimal transferAmount = new BigDecimal("100.00");
            BigDecimal fromBalanceBefore = checking1.getBalance();
            BigDecimal toBalanceBefore = checking2.getBalance();
            
            ConsoleFormatter.printInfo("Transferring $" + transferAmount + 
                                     " from " + checking1.getAccountNumber() + 
                                     " to " + checking2.getAccountNumber());
            ConsoleFormatter.printInfo("From Balance: $" + fromBalanceBefore);
            ConsoleFormatter.printInfo("To Balance: $" + toBalanceBefore);
            
            bank.transfer(checking1.getAccountNumber(), checking2.getAccountNumber(), transferAmount);
            
            ConsoleFormatter.printSuccess("Transfer completed successfully!");
            ConsoleFormatter.printSuccess("From Balance: $" + fromBalanceBefore + " → $" + checking1.getBalance());
            ConsoleFormatter.printSuccess("To Balance: $" + toBalanceBefore + " → $" + checking2.getBalance());
        } catch (Exception e) {
            ConsoleFormatter.printError("Transfer failed: " + e.getMessage());
        }
        ConsoleFormatter.printBlankLine();
        
        // Failed Transfer - Insufficient Funds
        ConsoleFormatter.printSubsectionHeader("Failed Transfer: Insufficient Funds");
        BigDecimal excessiveAmount = checking1.getBalance().add(new BigDecimal("500.00"));
        ConsoleFormatter.printInfo("Attempting to transfer $" + excessiveAmount + 
                                 " from account with balance $" + checking1.getBalance());
        
        Transaction failedTransfer = bank.transfer(checking1.getAccountNumber(), checking2.getAccountNumber(), excessiveAmount);
        if (failedTransfer.getStatus() == TransactionStatus.FAILED) {
            ConsoleFormatter.printWarning("EXPECTED FAILURE: " + failedTransfer.getFailureReason());
            ConsoleFormatter.printSuccess("Atomic transfer: No changes made to either account");
        } else {
            ConsoleFormatter.printError("Should have failed but didn't!");
        }
        ConsoleFormatter.printBlankLine();
        
        // Additional Successful Transfer
        ConsoleFormatter.printSubsectionHeader("Cross-Type Transfer (Checking to Savings)");
        try {
            BigDecimal transferAmount = new BigDecimal("200.00");
            BigDecimal fromBalanceBefore = checking2.getBalance();
            BigDecimal toBalanceBefore = savings1.getBalance();
            
            ConsoleFormatter.printInfo("Transferring $" + transferAmount + 
                                     " from CHECKING " + checking2.getAccountNumber() + 
                                     " to SAVINGS " + savings1.getAccountNumber());
            
            bank.transfer(checking2.getAccountNumber(), savings1.getAccountNumber(), transferAmount);
            
            ConsoleFormatter.printSuccess("Cross-type transfer completed!");
            ConsoleFormatter.printSuccess("Checking: $" + fromBalanceBefore + " → $" + checking2.getBalance());
            ConsoleFormatter.printSuccess("Savings: $" + toBalanceBefore + " → $" + savings1.getBalance());
        } catch (Exception e) {
            ConsoleFormatter.printError("Transfer failed: " + e.getMessage());
        }
        
        ConsoleFormatter.printBlankLine();
        ConsoleFormatter.printHighlight("Transfers Demonstrated: 3 (2 successful, 1 failed)");
    }
    
    // ==================== SECTION 5: MONTHLY ADJUSTMENTS ====================
    
    private static void section5_MonthlyAdjustments() {
        ConsoleFormatter.printSectionHeader("SECTION 5: MONTHLY ADJUSTMENTS");
        ConsoleFormatter.printInfo("Applying fees (Checking) and interest (Savings)");
        ConsoleFormatter.printBlankLine();
        
        List<Account> accounts = new ArrayList<>(bank.getAllAccounts());
        
        ConsoleFormatter.printSubsectionHeader("Before Monthly Adjustments");
        for (Account account : accounts) {
            ConsoleFormatter.printAccountCompact(account);
        }
        ConsoleFormatter.printBlankLine();
        
        // Apply monthly interest to all savings accounts
        ConsoleFormatter.printSubsectionHeader("Applying Monthly Adjustments");
        try {
            // Apply interest to all savings accounts at once
            bank.applyMonthlyInterest();
            
            ConsoleFormatter.printSuccess("Applied 2% monthly interest to all savings accounts");
            
            // Show checking account transaction counts
            for (Account account : accounts) {
                if (account.getAccountType() == AccountType.CHECKING) {
                    int txCount = account.getMonthlyTransactionCount();
                    
                    if (txCount > 10) {
                        int excessTx = txCount - 10;
                        BigDecimal expectedFee = new BigDecimal("2.50").multiply(new BigDecimal(excessTx));
                        
                        ConsoleFormatter.printInfo("Account " + account.getAccountNumber() + 
                                                 " has " + txCount + " transactions (" + excessTx + 
                                                 " over free limit)");
                        ConsoleFormatter.printInfo("Fee would be charged: $" + expectedFee + " ($2.50 per excess transaction)");
                    } else {
                        ConsoleFormatter.printInfo("Account " + account.getAccountNumber() + 
                                                 " has " + txCount + " transactions (no fees - under limit)");
                    }
                }
            }
            
            ConsoleFormatter.printBlankLine();
            ConsoleFormatter.printHighlight("Monthly interest applied successfully");
            ConsoleFormatter.printInfo("Note: Checking fees charged automatically for transactions > 10");
            
        } catch (Exception e) {
            ConsoleFormatter.printError("Monthly adjustment failed: " + e.getMessage());
        }
        
        ConsoleFormatter.printBlankLine();
        ConsoleFormatter.printSubsectionHeader("After Monthly Adjustments");
        for (Account account : accounts) {
            ConsoleFormatter.printAccountCompact(account);
        }
    }
    
    // ==================== SECTION 6: ACCOUNT STATEMENTS ====================
    
    private static void section6_AccountStatements() {
        ConsoleFormatter.printSectionHeader("SECTION 6: ACCOUNT STATEMENTS");
        ConsoleFormatter.printInfo("Detailed statements for all accounts");
        ConsoleFormatter.printBlankLine();
        
        List<Account> accounts = new ArrayList<>(bank.getAllAccounts());
        
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            
            ConsoleFormatter.printSubsectionHeader("Statement " + (i + 1) + " of " + accounts.size());
            String statement = bank.generateMonthlyStatement(account.getAccountNumber());
            System.out.println(statement);
            
            if (i < accounts.size() - 1) {
                ConsoleFormatter.printDivider();
                ConsoleFormatter.printBlankLine();
            }
        }
    }
    
    // ==================== SECTION 7: BANK SUMMARY ====================
    
    private static void section7_BankSummary() {
        ConsoleFormatter.printSectionHeader("SECTION 7: BANK SUMMARY");
        ConsoleFormatter.printInfo("Overall statistics and account overview");
        ConsoleFormatter.printBlankLine();
        
        List<Account> accounts = new ArrayList<>(bank.getAllAccounts());
        
        // Count by type
        long checkingCount = accounts.stream()
            .filter(a -> a.getAccountType() == AccountType.CHECKING)
            .count();
        long savingsCount = accounts.stream()
            .filter(a -> a.getAccountType() == AccountType.SAVINGS)
            .count();
        
        // Calculate total balances
        BigDecimal totalCheckingBalance = accounts.stream()
            .filter(a -> a.getAccountType() == AccountType.CHECKING)
            .map(Account::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalSavingsBalance = accounts.stream()
            .filter(a -> a.getAccountType() == AccountType.SAVINGS)
            .map(Account::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalBalance = totalCheckingBalance.add(totalSavingsBalance);
        
        // Count total transactions
        int totalTransactions = accounts.stream()
            .mapToInt(a -> a.getTransactionHistory().size())
            .sum();
        
        // Display summary
        ConsoleFormatter.printSubsectionHeader("Account Overview");
        ConsoleFormatter.printHighlight("Total Accounts: " + accounts.size());
        ConsoleFormatter.printInfo("  - Checking Accounts: " + checkingCount);
        ConsoleFormatter.printInfo("  - Savings Accounts: " + savingsCount);
        ConsoleFormatter.printBlankLine();
        
        ConsoleFormatter.printSubsectionHeader("Balance Summary");
        ConsoleFormatter.printHighlight("Total Balance: $" + String.format("%,.2f", totalBalance));
        ConsoleFormatter.printInfo("  - Checking Total: $" + String.format("%,.2f", totalCheckingBalance));
        ConsoleFormatter.printInfo("  - Savings Total: $" + String.format("%,.2f", totalSavingsBalance));
        ConsoleFormatter.printBlankLine();
        
        ConsoleFormatter.printSubsectionHeader("Transaction Summary");
        ConsoleFormatter.printHighlight("Total Transactions: " + totalTransactions);
        ConsoleFormatter.printBlankLine();
        
        ConsoleFormatter.printSubsectionHeader("All Accounts");
        System.out.println();
        for (Account account : accounts) {
            ConsoleFormatter.printAccountCompact(account);
        }
    }
    
    // ==================== HELPER ====================
    
    private static void pauseForReading() {
        ConsoleFormatter.printBlankLines(2);
        ConsoleFormatter.printDivider();
        ConsoleFormatter.printBlankLines(2);
    }
}
