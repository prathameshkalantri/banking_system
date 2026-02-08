package com.prathamesh.banking;

import com.prathamesh.banking.model.Account;
import com.prathamesh.banking.model.AccountType;
import com.prathamesh.banking.model.Transaction;
import com.prathamesh.banking.service.Bank;
import com.prathamesh.banking.util.ConsoleColors;
import com.prathamesh.banking.util.Logger;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Interactive banking system demonstration.
 * 
 * <p>Showcases all features of the banking system including:
 * <ul>
 *   <li>Account management (CHECKING and SAVINGS)</li>
 *   <li>Transaction processing (deposits, withdrawals, transfers)</li>
 *   <li>Business rule enforcement (fees, limits, minimum balances)</li>
 *   <li>Monthly processing (interest, fees, counter resets)</li>
 *   <li>Statement generation</li>
 * </ul>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public class Main {
    
    private static final Bank bank = new Bank();
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        printWelcomeBanner();
        
        // Run automated demo
        runAutomatedDemo();
        
        // Offer interactive mode
        System.out.println("\n" + ConsoleColors.header("═".repeat(70)));
        System.out.println(ConsoleColors.info("Demo completed! Would you like to try interactive mode? (y/n)"));
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.equals("y") || response.equals("yes")) {
            runInteractiveMode();
        }
        
        System.out.println("\n" + ConsoleColors.success("Thank you for using the Banking System!"));
        scanner.close();
    }
    
    /**
     * Prints the welcome banner.
     */
    private static void printWelcomeBanner() {
        System.out.println(ConsoleColors.BLUE_BOLD + "\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                    ║");
        System.out.println("║              ADVANCED BANKING SYSTEM DEMONSTRATION                 ║");
        System.out.println("║                                                                    ║");
        System.out.println("║  Author: Prathamesh Kalantri                                       ║");
        System.out.println("║  Version: 1.0.0                                                    ║");
        System.out.println("║                                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝" + ConsoleColors.RESET);
    }
    
    /**
     * Runs an automated demonstration of all banking features.
     */
    private static void runAutomatedDemo() {
        System.out.println("\n" + ConsoleColors.header("▶ Starting Automated Demo...") + "\n");
        
        try {
            // Phase 1: Account Creation
            demonstrateAccountCreation();
            pause(1000);
            
            // Phase 2: Basic Transactions
            demonstrateBasicTransactions();
            pause(1000);
            
            // Phase 3: Transfer Operations
            demonstrateTransfers();
            pause(1000);
            
            // Phase 4: Business Rules
            demonstrateBusinessRules();
            pause(1000);
            
            // Phase 5: Monthly Processing
            demonstrateMonthlyProcessing();
            pause(1000);
            
            // Phase 6: Statements
            demonstrateStatements();
            
        } catch (Exception e) {
            Logger.error("Demo error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates account creation.
     */
    private static void demonstrateAccountCreation() {
        printSection("Phase 1: Account Creation");
        
        Logger.info("Opening CHECKING account for John Doe with $1000 initial deposit");
        Account checking = bank.openAccount("John Doe", AccountType.CHECKING, new BigDecimal("1000.00"));
        System.out.println(ConsoleColors.success("Created " + checking.getAccountType() + " account: " + checking.getAccountNumber()));
        System.out.println("  Balance: " + ConsoleColors.amount(checking.getBalance().doubleValue()));
        
        Logger.info("Opening SAVINGS account for Jane Smith with $500 initial deposit");
        Account savings = bank.openAccount("Jane Smith", AccountType.SAVINGS, new BigDecimal("500.00"));
        System.out.println(ConsoleColors.success("Created " + savings.getAccountType() + " account: " + savings.getAccountNumber()));
        System.out.println("  Balance: " + ConsoleColors.amount(savings.getBalance().doubleValue()));
        
        Logger.info("Opening CHECKING account for Bob Johnson with zero balance");
        Account checking2 = bank.openAccount("Bob Johnson", AccountType.CHECKING, BigDecimal.ZERO);
        System.out.println(ConsoleColors.success("Created " + checking2.getAccountType() + " account: " + checking2.getAccountNumber()));
        System.out.println("  Balance: " + ConsoleColors.amount(checking2.getBalance().doubleValue()));
        
        Logger.info("Opening SAVINGS account for Alice Williams with $200 initial deposit");
        Account savings2 = bank.openAccount("Alice Williams", AccountType.SAVINGS, new BigDecimal("200.00"));
        System.out.println(ConsoleColors.success("Created " + savings2.getAccountType() + " account: " + savings2.getAccountNumber()));
        System.out.println("  Balance: " + ConsoleColors.amount(savings2.getBalance().doubleValue()));
    }
    
    /**
     * Demonstrates basic deposit and withdrawal transactions.
     */
    private static void demonstrateBasicTransactions() {
        printSection("Phase 2: Basic Transactions");
        
        Account checking = bank.getAllAccounts().stream()
                .filter(a -> a.getCustomerName().equals("John Doe"))
                .findFirst()
                .orElseThrow();
        
        Logger.info("Depositing $500 into John's checking account");
        Transaction deposit = bank.deposit(checking.getAccountNumber(), new BigDecimal("500.00"));
        printTransaction(deposit, checking);
        
        Logger.info("Withdrawing $200 from John's checking account");
        Transaction withdrawal = bank.withdraw(checking.getAccountNumber(), new BigDecimal("200.00"));
        printTransaction(withdrawal, checking);
        
        Logger.info("Attempting to withdraw $10,000 (insufficient funds)");
        Transaction failed = bank.withdraw(checking.getAccountNumber(), new BigDecimal("10000.00"));
        printTransaction(failed, checking);
    }
    
    /**
     * Demonstrates transfer operations.
     */
    private static void demonstrateTransfers() {
        printSection("Phase 3: Transfer Operations");
        
        Account checking = bank.getAllAccounts().stream()
                .filter(a -> a.getCustomerName().equals("John Doe"))
                .findFirst()
                .orElseThrow();
        
        Account savings = bank.getAllAccounts().stream()
                .filter(a -> a.getCustomerName().equals("Jane Smith"))
                .findFirst()
                .orElseThrow();
        
        Logger.info("Transferring $300 from John's checking to Jane's savings");
        Transaction[] transfers = bank.transfer(checking.getAccountNumber(), 
                                               savings.getAccountNumber(), 
                                               new BigDecimal("300.00"));
        
        System.out.println(ConsoleColors.success("Transfer completed successfully"));
        System.out.println("  From: " + checking.getAccountNumber() + " - New balance: " + 
                          ConsoleColors.amount(bank.getAccount(checking.getAccountNumber()).getBalance().doubleValue()));
        System.out.println("  To:   " + savings.getAccountNumber() + " - New balance: " + 
                          ConsoleColors.amount(bank.getAccount(savings.getAccountNumber()).getBalance().doubleValue()));
    }
    
    /**
     * Demonstrates business rule enforcement.
     */
    private static void demonstrateBusinessRules() {
        printSection("Phase 4: Business Rules Enforcement");
        
        // Demonstrate CHECKING account transaction fees
        System.out.println(ConsoleColors.info("Testing CHECKING account transaction fees (free limit: 10)"));
        Account checking = bank.getAllAccounts().stream()
                .filter(a -> a.getCustomerName().equals("John Doe"))
                .findFirst()
                .orElseThrow();
        
        System.out.println("  Current transactions this month: " + checking.getMonthlyTransactionCount());
        System.out.println("  Making 8 more deposits to exceed free limit...");
        
        for (int i = 0; i < 8; i++) {
            bank.deposit(checking.getAccountNumber(), new BigDecimal("10.00"));
        }
        
        Account updatedChecking = bank.getAccount(checking.getAccountNumber());
        System.out.println("  Transactions after deposits: " + updatedChecking.getMonthlyTransactionCount());
        System.out.println(ConsoleColors.warning("Note: Transactions beyond 10 will incur $2.50 fee during monthly processing"));
        
        // Demonstrate SAVINGS account withdrawal limit
        System.out.println("\n" + ConsoleColors.info("Testing SAVINGS account withdrawal limit (max: 5)"));
        Account savings = bank.getAllAccounts().stream()
                .filter(a -> a.getCustomerName().equals("Jane Smith"))
                .findFirst()
                .orElseThrow();
        
        System.out.println("  Making 5 withdrawals...");
        for (int i = 0; i < 5; i++) {
            Transaction txn = bank.withdraw(savings.getAccountNumber(), new BigDecimal("10.00"));
            if (i == 4) {
                System.out.println(ConsoleColors.success("Withdrawal " + (i + 1) + ": SUCCESS"));
            }
        }
        
        System.out.println("  Attempting 6th withdrawal...");
        Transaction failed = bank.withdraw(savings.getAccountNumber(), new BigDecimal("10.00"));
        System.out.println(ConsoleColors.error("Withdrawal 6: FAILED - " + failed.getFailureReason()));
        
        // Demonstrate SAVINGS minimum balance
        System.out.println("\n" + ConsoleColors.info("Testing SAVINGS minimum balance requirement ($100)"));
        Account updatedSavings = bank.getAccount(savings.getAccountNumber());
        System.out.println("  Current balance: " + ConsoleColors.amount(updatedSavings.getBalance().doubleValue()));
        
        BigDecimal attemptedWithdrawal = updatedSavings.getBalance().subtract(new BigDecimal("50.00"));
        System.out.println("  Attempting to withdraw $" + attemptedWithdrawal + " (would leave $50)...");
        Transaction minBalanceFail = bank.withdraw(savings.getAccountNumber(), attemptedWithdrawal);
        System.out.println(ConsoleColors.error("Withdrawal FAILED - " + minBalanceFail.getFailureReason()));
    }
    
    /**
     * Demonstrates monthly processing.
     */
    private static void demonstrateMonthlyProcessing() {
        printSection("Phase 5: Monthly Processing");
        
        System.out.println(ConsoleColors.info("Applying monthly interest to SAVINGS accounts (2% rate)"));
        BigDecimal totalInterest = bank.applyMonthlyInterest();
        System.out.println(ConsoleColors.success("Total interest paid: " + ConsoleColors.amount(totalInterest.doubleValue())));
        
        System.out.println("\n" + ConsoleColors.info("Applying monthly fees and resetting counters"));
        bank.applyMonthlyFeesAndResetCounters();
        System.out.println(ConsoleColors.success("Monthly processing complete"));
        
        // Show updated balances
        System.out.println("\n" + ConsoleColors.header("Updated Account Balances:"));
        for (Account account : bank.getAllAccounts()) {
            System.out.println(String.format("  %s (%s - %s): %s",
                    account.getAccountNumber(),
                    account.getCustomerName(),
                    account.getAccountType(),
                    ConsoleColors.amount(account.getBalance().doubleValue())));
        }
    }
    
    /**
     * Demonstrates statement generation.
     */
    private static void demonstrateStatements() {
        printSection("Phase 6: Monthly Statements");
        
        Account checking = bank.getAllAccounts().stream()
                .filter(a -> a.getCustomerName().equals("John Doe"))
                .findFirst()
                .orElseThrow();
        
        Logger.info("Generating monthly statement for " + checking.getCustomerName());
        String statement = bank.generateMonthlyStatement(checking.getAccountNumber());
        System.out.println("\n" + ConsoleColors.CYAN + statement + ConsoleColors.RESET);
    }
    
    /**
     * Runs interactive mode where users can perform operations.
     */
    private static void runInteractiveMode() {
        System.out.println("\n" + ConsoleColors.header("Interactive Mode - Enter commands (type 'help' for options)"));
        
        boolean running = true;
        while (running) {
            System.out.print("\n" + ConsoleColors.BLUE + "banking> " + ConsoleColors.RESET);
            String input = scanner.nextLine().trim().toLowerCase();
            
            try {
                switch (input) {
                    case "help":
                        printHelp();
                        break;
                    case "list":
                        listAccounts();
                        break;
                    case "create":
                        createAccountInteractive();
                        break;
                    case "deposit":
                        depositInteractive();
                        break;
                    case "withdraw":
                        withdrawInteractive();
                        break;
                    case "transfer":
                        transferInteractive();
                        break;
                    case "statement":
                        statementInteractive();
                        break;
                    case "monthly":
                        performMonthlyProcessing();
                        break;
                    case "exit":
                    case "quit":
                        running = false;
                        break;
                    default:
                        System.out.println(ConsoleColors.error("Unknown command. Type 'help' for available commands."));
                }
            } catch (Exception e) {
                System.out.println(ConsoleColors.error("Error: " + e.getMessage()));
            }
        }
    }
    
    /**
     * Prints help information.
     */
    private static void printHelp() {
        System.out.println("\n" + ConsoleColors.header("Available Commands:"));
        System.out.println("  help      - Show this help message");
        System.out.println("  list      - List all accounts");
        System.out.println("  create    - Create a new account");
        System.out.println("  deposit   - Deposit money");
        System.out.println("  withdraw  - Withdraw money");
        System.out.println("  transfer  - Transfer money between accounts");
        System.out.println("  statement - Generate account statement");
        System.out.println("  monthly   - Run monthly processing");
        System.out.println("  exit      - Exit interactive mode");
    }
    
    /**
     * Lists all accounts.
     */
    private static void listAccounts() {
        System.out.println("\n" + ConsoleColors.header("All Accounts:"));
        for (Account account : bank.getAllAccounts()) {
            System.out.println(String.format("  %s | %s | %s | Balance: %s | Transactions: %d",
                    account.getAccountNumber(),
                    account.getCustomerName(),
                    account.getAccountType(),
                    ConsoleColors.amount(account.getBalance().doubleValue()),
                    account.getTransactionHistory().size()));
        }
    }
    
    /**
     * Creates an account interactively.
     */
    private static void createAccountInteractive() {
        System.out.print("Customer name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Account type (CHECKING/SAVINGS): ");
        String typeStr = scanner.nextLine().trim().toUpperCase();
        AccountType type = AccountType.valueOf(typeStr);
        
        System.out.print("Initial deposit: $");
        BigDecimal deposit = new BigDecimal(scanner.nextLine().trim());
        
        Account account = bank.openAccount(name, type, deposit);
        System.out.println(ConsoleColors.success("Account created: " + account.getAccountNumber()));
    }
    
    /**
     * Performs a deposit interactively.
     */
    private static void depositInteractive() {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();
        
        System.out.print("Amount: $");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        
        Transaction txn = bank.deposit(accountNumber, amount);
        printTransaction(txn, bank.getAccount(accountNumber));
    }
    
    /**
     * Performs a withdrawal interactively.
     */
    private static void withdrawInteractive() {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();
        
        System.out.print("Amount: $");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        
        Transaction txn = bank.withdraw(accountNumber, amount);
        printTransaction(txn, bank.getAccount(accountNumber));
    }
    
    /**
     * Performs a transfer interactively.
     */
    private static void transferInteractive() {
        System.out.print("From account: ");
        String fromAccount = scanner.nextLine().trim();
        
        System.out.print("To account: ");
        String toAccount = scanner.nextLine().trim();
        
        System.out.print("Amount: $");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        
        Transaction[] txns = bank.transfer(fromAccount, toAccount, amount);
        if (txns[0].isSuccessful()) {
            System.out.println(ConsoleColors.success("Transfer completed"));
            System.out.println("  From balance: " + ConsoleColors.amount(bank.getAccount(fromAccount).getBalance().doubleValue()));
            System.out.println("  To balance: " + ConsoleColors.amount(bank.getAccount(toAccount).getBalance().doubleValue()));
        } else {
            System.out.println(ConsoleColors.error("Transfer failed: " + txns[0].getFailureReason()));
        }
    }
    
    /**
     * Generates a statement interactively.
     */
    private static void statementInteractive() {
        System.out.print("Account number: ");
        String accountNumber = scanner.nextLine().trim();
        
        String statement = bank.generateMonthlyStatement(accountNumber);
        System.out.println("\n" + statement);
    }
    
    /**
     * Performs monthly processing.
     */
    private static void performMonthlyProcessing() {
        System.out.println(ConsoleColors.info("Running monthly processing..."));
        BigDecimal interest = bank.applyMonthlyInterest();
        bank.applyMonthlyFeesAndResetCounters();
        System.out.println(ConsoleColors.success("Monthly processing complete. Total interest: " + 
                          ConsoleColors.amount(interest.doubleValue())));
    }
    
    /**
     * Prints a transaction result.
     */
    private static void printTransaction(Transaction txn, Account account) {
        if (txn.isSuccessful()) {
            System.out.println(ConsoleColors.success(txn.getType() + " successful"));
            System.out.println("  Amount: " + ConsoleColors.amount(txn.getAmount().doubleValue()));
            System.out.println("  New balance: " + ConsoleColors.amount(account.getBalance().doubleValue()));
        } else {
            System.out.println(ConsoleColors.error(txn.getType() + " failed: " + txn.getFailureReason()));
        }
    }
    
    /**
     * Prints a section header.
     */
    private static void printSection(String title) {
        System.out.println("\n" + ConsoleColors.header("═".repeat(70)));
        System.out.println(ConsoleColors.header(title));
        System.out.println(ConsoleColors.header("═".repeat(70)));
    }
    
    /**
     * Pauses execution for the specified duration.
     */
    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
