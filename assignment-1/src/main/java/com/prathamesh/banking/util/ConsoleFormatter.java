package com.prathamesh.banking.util;

import com.prathamesh.banking.domain.Account;
import com.prathamesh.banking.domain.Transaction;
import com.prathamesh.banking.domain.TransactionStatus;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Console formatter utility for professional terminal output.
 * 
 * OOP Principles Applied:
 * 1. Utility Class Pattern - Static methods only, no state
 * 2. Single Responsibility - Only handles console formatting
 * 3. Encapsulation - ANSI codes hidden from clients
 * 
 * Features:
 * - ANSI color codes for visual distinction
 * - Formatted output for transactions, accounts, statements
 * - Professional section headers and dividers
 * 
 * Usage:
 * <pre>
 * ConsoleFormatter.printSuccess("Account created successfully");
 * ConsoleFormatter.printError("Insufficient funds");
 * ConsoleFormatter.printTransaction(transaction);
 * </pre>
 */
public class ConsoleFormatter {
    
    // ANSI Color Codes
    private static final String RESET = "\u001B[0m";
    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    
    // ANSI Text Styles
    private static final String BOLD = "\u001B[1m";
    private static final String UNDERLINE = "\u001B[4m";
    
    // Date/Time Formatter
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Private constructor to prevent instantiation.
     * OOP Principle: Utility class should not be instantiated
     */
    private ConsoleFormatter() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    // ==================== SECTION HEADERS ====================
    
    /**
     * Prints a formatted section header.
     * 
     * Format:
     * ========================================
     *           SECTION TITLE
     * ========================================
     *
     * @param title the section title
     */
    public static void printSectionHeader(String title) {
        if (title == null) title = "";
        System.out.println(CYAN + BOLD);
        System.out.println("========================================");
        System.out.println(centerText(title, 40));
        System.out.println("========================================");
        System.out.println(RESET);
    }
    
    /**
     * Prints a subsection divider.
     *
     * @param title the subsection title
     */
    public static void printSubsectionHeader(String title) {
        if (title == null) title = "";
        System.out.println(BLUE + BOLD + "\n--- " + title + " ---" + RESET);
    }
    
    /**
     * Prints a simple divider line.
     */
    public static void printDivider() {
        System.out.println(CYAN + "----------------------------------------" + RESET);
    }
    
    // ==================== MESSAGE PRINTING ====================
    
    /**
     * Prints a success message in green.
     * 
     * OOP Principle: Single Responsibility - Handles success formatting
     *
     * @param message the success message
     */
    public static void printSuccess(String message) {
        if (message == null) message = "";
        System.out.println(GREEN + "✓ " + message + RESET);
    }
    
    /**
     * Prints an error message in red.
     * 
     * OOP Principle: Single Responsibility - Handles error formatting
     *
     * @param message the error message
     */
    public static void printError(String message) {
        if (message == null) message = "";
        System.out.println(RED + "✗ " + message + RESET);
    }
    
    /**
     * Prints a warning message in yellow.
     *
     * @param message the warning message
     */
    public static void printWarning(String message) {
        if (message == null) message = "";
        System.out.println(YELLOW + "⚠ " + message + RESET);
    }
    
    /**
     * Prints an info message in blue.
     *
     * @param message the info message
     */
    public static void printInfo(String message) {
        if (message == null) message = "";
        System.out.println(BLUE + "ℹ " + message + RESET);
    }
    
    /**
     * Prints a highlighted message in magenta.
     *
     * @param message the message to highlight
     */
    public static void printHighlight(String message) {
        if (message == null) message = "";
        System.out.println(MAGENTA + BOLD + message + RESET);
    }
    
    // ==================== TRANSACTION FORMATTING ====================
    
    /**
     * Prints a formatted transaction details.
     * 
     * Format:
     * [SUCCESS/FAILED] DEPOSIT - $100.00
     * Transaction ID: TXN-000000000001
     * Time: 2024-01-15 10:30:45
     * Balance: $500.00 → $600.00
     * 
     * OOP Principle: Single Responsibility - Formats transaction display
     *
     * @param transaction the transaction to print
     */
    public static void printTransaction(Transaction transaction) {
        if (transaction == null) {
            printError("Transaction is null");
            return;
        }
        
        // Status color
        String statusColor = transaction.isSuccessful() ? GREEN : RED;
        String statusSymbol = transaction.isSuccessful() ? "✓" : "✗";
        
        // Transaction header
        System.out.println(statusColor + BOLD + 
            statusSymbol + " [" + transaction.getStatus() + "] " + 
            transaction.getType() + " - $" + transaction.getAmount() + 
            RESET);
        
        // Transaction details
        System.out.println("  Transaction ID: " + CYAN + transaction.getTransactionId() + RESET);
        System.out.println("  Time: " + transaction.getTimestamp().format(DATE_TIME_FORMATTER));
        System.out.println("  Account: " + transaction.getAccountNumber());
        
        if (transaction.isSuccessful()) {
            System.out.println("  Balance: $" + transaction.getBalanceBefore() + 
                             " → " + GREEN + "$" + transaction.getBalanceAfter() + RESET);
        } else {
            System.out.println("  " + RED + "Reason: " + transaction.getFailureReason() + RESET);
            System.out.println("  Balance: $" + transaction.getBalanceBefore() + 
                             " (unchanged)");
        }
        System.out.println();
    }
    
    /**
     * Prints a compact transaction line (for transaction lists).
     *
     * @param transaction the transaction to print
     */
    public static void printTransactionCompact(Transaction transaction) {
        if (transaction == null) return;
        
        String statusColor = transaction.isSuccessful() ? GREEN : RED;
        String statusSymbol = transaction.isSuccessful() ? "✓" : "✗";
        
        System.out.printf("%s%s %-10s | %-10s | $%-8s | %s%s\n",
            statusColor,
            statusSymbol,
            transaction.getType(),
            transaction.getTransactionId(),
            transaction.getAmount(),
            transaction.getTimestamp().format(DATE_TIME_FORMATTER),
            RESET
        );
    }
    
    // ==================== ACCOUNT FORMATTING ====================
    
    /**
     * Prints account summary with key details.
     * 
     * Format:
     * Account: ACC-00000001
     * Customer: John Doe
     * Type: CHECKING
     * Balance: $1,234.56
     * 
     * OOP Principle: Single Responsibility - Formats account display
     *
     * @param account the account to print
     */
    public static void printAccountSummary(Account account) {
        if (account == null) {
            printError("Account is null");
            return;
        }
        
        System.out.println(BOLD + "Account Summary:" + RESET);
        System.out.println("  Account Number: " + CYAN + account.getAccountNumber() + RESET);
        System.out.println("  Customer Name: " + account.getCustomerName());
        System.out.println("  Account Type: " + YELLOW + account.getAccountType() + RESET);
        System.out.println("  Current Balance: " + GREEN + BOLD + "$" + 
                         formatCurrency(account.getBalance()) + RESET);
        System.out.println("  Monthly Transactions: " + account.getMonthlyTransactionCount());
        System.out.println("  Monthly Withdrawals: " + account.getMonthlyWithdrawalCount());
        System.out.println();
    }
    
    /**
     * Prints a detailed account statement with transaction history.
     * 
     * OOP Principle: Single Responsibility - Formats full statement
     *
     * @param account the account to print statement for
     */
    public static void printStatement(Account account) {
        if (account == null) {
            printError("Account is null");
            return;
        }
        
        printSectionHeader("ACCOUNT STATEMENT");
        
        // Account Information
        System.out.println(BOLD + "Account Information:" + RESET);
        System.out.println("  Account Number: " + CYAN + account.getAccountNumber() + RESET);
        System.out.println("  Customer Name: " + account.getCustomerName());
        System.out.println("  Account Type: " + YELLOW + account.getAccountType() + RESET);
        System.out.println("  Current Balance: " + GREEN + BOLD + "$" + 
                         formatCurrency(account.getBalance()) + RESET);
        
        // Monthly Statistics
        printSubsectionHeader("Monthly Statistics");
        System.out.println("  Total Transactions: " + account.getMonthlyTransactionCount());
        System.out.println("  Total Withdrawals: " + account.getMonthlyWithdrawalCount());
        
        // Transaction History
        printSubsectionHeader("Transaction History");
        if (account.getTransactionHistory().isEmpty()) {
            printInfo("No transactions yet.");
        } else {
            System.out.println();
            for (Transaction tx : account.getTransactionHistory()) {
                printTransactionCompact(tx);
            }
        }
        
        printDivider();
        System.out.println();
    }
    
    /**
     * Prints a compact account line (for account lists).
     *
     * @param account the account to print
     */
    public static void printAccountCompact(Account account) {
        if (account == null) return;
        
        System.out.printf(CYAN + "%-12s" + RESET + " | %-20s | %-10s | " + 
                         GREEN + "$%-10s" + RESET + "\n",
            account.getAccountNumber(),
            truncate(account.getCustomerName(), 20),
            account.getAccountType(),
            formatCurrency(account.getBalance())
        );
    }
    
    // ==================== MENU FORMATTING ====================
    
    /**
     * Prints a menu with numbered options.
     *
     * @param title menu title
     * @param options array of menu options
     */
    public static void printMenu(String title, String[] options) {
        if (title == null) title = "Menu";
        if (options == null || options.length == 0) {
            printSectionHeader(title);
            printWarning("No options available");
            return;
        }
        printSectionHeader(title);
        for (int i = 0; i < options.length; i++) {
            String option = (options[i] == null) ? "" : options[i];
            System.out.printf(CYAN + "%d." + RESET + " %s\n", i + 1, option);
        }
        System.out.println();
    }
    
    /**
     * Prints a prompt for user input.
     *
     * @param prompt the prompt message
     */
    public static void printPrompt(String prompt) {
        if (prompt == null) prompt = "Enter";
        System.out.print(YELLOW + prompt + ": " + RESET);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Centers text within a given width.
     *
     * @param text the text to center
     * @param width the total width
     * @return centered text
     */
    private static String centerText(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) {
            return text;
        }
        
        int leftPadding = (width - text.length()) / 2;
        int rightPadding = width - text.length() - leftPadding;
        
        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }
    
    /**
     * Formats a BigDecimal as currency with commas.
     *
     * @param amount the amount to format
     * @return formatted currency string
     */
    private static String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%,.2f", amount);
    }
    
    /**
     * Truncates text to a maximum length with ellipsis.
     *
     * @param text the text to truncate
     * @param maxLength maximum length
     * @return truncated text
     */
    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Prints a blank line for spacing.
     */
    public static void printBlankLine() {
        System.out.println();
    }
    
    /**
     * Prints multiple blank lines.
     *
     * @param count number of blank lines
     */
    public static void printBlankLines(int count) {
        if (count < 0) count = 0;
        for (int i = 0; i < count; i++) {
            System.out.println();
        }
    }
    
    /**
     * Clears the console (Unix/Linux/Mac only).
     * Note: This may not work in all terminals.
     */
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
