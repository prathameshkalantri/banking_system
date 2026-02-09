package com.prathamesh.banking.util;

import com.prathamesh.banking.domain.Account;
import com.prathamesh.banking.domain.AccountType;
import com.prathamesh.banking.domain.Transaction;
import com.prathamesh.banking.domain.TransactionStatus;
import com.prathamesh.banking.domain.TransactionType;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ConsoleFormatter utility class.
 * 
 * Tests:
 * 1. Utility class pattern (private constructor)
 * 2. All formatting methods execute without errors
 * 3. Output contains expected content
 * 4. Null handling
 * 5. Edge cases
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConsoleFormatterTest {
    
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUpOutput() {
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }
    
    // ==================== UTILITY CLASS PATTERN ====================
    
    @Test
    @Order(1)
    @DisplayName("1. Private constructor prevents instantiation (Utility Pattern)")
    void testPrivateConstructor() {
        try {
            Constructor<ConsoleFormatter> constructor = ConsoleFormatter.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            
            assertThrows(Exception.class, constructor::newInstance,
                "Utility class constructor should throw exception");
        } catch (NoSuchMethodException e) {
            fail("ConsoleFormatter should have a private no-arg constructor");
        }
    }
    
    // ==================== SECTION HEADERS ====================
    
    @Test
    @Order(2)
    @DisplayName("2. printSectionHeader produces formatted output")
    void testPrintSectionHeader() {
        ConsoleFormatter.printSectionHeader("TEST SECTION");
        String output = outputStream.toString();
        
        assertTrue(output.contains("TEST SECTION"), "Should contain section title");
        assertTrue(output.contains("==="), "Should contain divider");
        assertTrue(output.length() > 50, "Should produce formatted output");
    }
    
    @Test
    @Order(3)
    @DisplayName("3. printSubsectionHeader produces formatted output")
    void testPrintSubsectionHeader() {
        ConsoleFormatter.printSubsectionHeader("Test Subsection");
        String output = outputStream.toString();
        
        assertTrue(output.contains("Test Subsection"), "Should contain subsection title");
        assertTrue(output.contains("---"), "Should contain divider");
    }
    
    @Test
    @Order(4)
    @DisplayName("4. printDivider produces line output")
    void testPrintDivider() {
        ConsoleFormatter.printDivider();
        String output = outputStream.toString();
        
        assertTrue(output.contains("---"), "Should contain divider line");
    }
    
    // ==================== MESSAGE PRINTING ====================
    
    @Test
    @Order(5)
    @DisplayName("5. printSuccess displays success message")
    void testPrintSuccess() {
        ConsoleFormatter.printSuccess("Operation successful");
        String output = outputStream.toString();
        
        assertTrue(output.contains("Operation successful"), "Should contain success message");
        assertTrue(output.length() > 0, "Should produce output");
    }
    
    @Test
    @Order(6)
    @DisplayName("6. printError displays error message")
    void testPrintError() {
        ConsoleFormatter.printError("Operation failed");
        String output = outputStream.toString();
        
        assertTrue(output.contains("Operation failed"), "Should contain error message");
        assertTrue(output.length() > 0, "Should produce output");
    }
    
    @Test
    @Order(7)
    @DisplayName("7. printWarning displays warning message")
    void testPrintWarning() {
        ConsoleFormatter.printWarning("Warning message");
        String output = outputStream.toString();
        
        assertTrue(output.contains("Warning message"), "Should contain warning message");
        assertTrue(output.length() > 0, "Should produce output");
    }
    
    @Test
    @Order(8)
    @DisplayName("8. printInfo displays info message")
    void testPrintInfo() {
        ConsoleFormatter.printInfo("Info message");
        String output = outputStream.toString();
        
        assertTrue(output.contains("Info message"), "Should contain info message");
        assertTrue(output.length() > 0, "Should produce output");
    }
    
    @Test
    @Order(9)
    @DisplayName("9. printHighlight displays highlighted message")
    void testPrintHighlight() {
        ConsoleFormatter.printHighlight("Important message");
        String output = outputStream.toString();
        
        assertTrue(output.contains("Important message"), "Should contain highlighted message");
        assertTrue(output.length() > 0, "Should produce output");
    }
    
    // ==================== TRANSACTION FORMATTING ====================
    
    @Test
    @Order(10)
    @DisplayName("10. printTransaction displays successful transaction")
    void testPrintTransactionSuccess() {
        Transaction transaction = new Transaction.Builder(
            "TXN-000000000001",
            TransactionType.DEPOSIT,
            new BigDecimal("100.00"),
            "ACC-00000001"
        )
            .timestamp(LocalDateTime.now())
            .balanceBefore(new BigDecimal("500.00"))
            .balanceAfter(new BigDecimal("600.00"))
            .status(TransactionStatus.SUCCESS)
            .build();
        
        ConsoleFormatter.printTransaction(transaction);
        String output = outputStream.toString();
        
        assertTrue(output.contains("TXN-000000000001"), "Should contain transaction ID");
        assertTrue(output.contains("DEPOSIT"), "Should contain transaction type");
        assertTrue(output.contains("100.00"), "Should contain amount");
        assertTrue(output.contains("SUCCESS"), "Should contain status");
    }
    
    @Test
    @Order(11)
    @DisplayName("11. printTransaction displays failed transaction")
    void testPrintTransactionFailed() {
        Transaction transaction = new Transaction.Builder(
            "TXN-000000000002",
            TransactionType.WITHDRAWAL,
            new BigDecimal("1000.00"),
            "ACC-00000001"
        )
            .timestamp(LocalDateTime.now())
            .balanceBefore(new BigDecimal("500.00"))
            .balanceAfter(new BigDecimal("500.00"))
            .status(TransactionStatus.FAILED)
            .failureReason("Insufficient funds")
            .build();
        
        ConsoleFormatter.printTransaction(transaction);
        String output = outputStream.toString();
        
        assertTrue(output.contains("TXN-000000000002"), "Should contain transaction ID");
        assertTrue(output.contains("WITHDRAWAL"), "Should contain transaction type");
        assertTrue(output.contains("FAILED"), "Should contain status");
        assertTrue(output.contains("Insufficient funds"), "Should contain failure reason");
    }
    
    @Test
    @Order(12)
    @DisplayName("12. printTransaction handles null gracefully")
    void testPrintTransactionNull() {
        assertDoesNotThrow(() -> ConsoleFormatter.printTransaction(null),
            "Should handle null transaction without throwing");
        
        String output = outputStream.toString();
        assertTrue(output.contains("null"), "Should indicate null transaction");
    }
    
    @Test
    @Order(13)
    @DisplayName("13. printTransactionCompact displays compact format")
    void testPrintTransactionCompact() {
        Transaction transaction = new Transaction.Builder(
            "TXN-000000000003",
            TransactionType.TRANSFER,
            new BigDecimal("250.00"),
            "ACC-00000001"
        )
            .timestamp(LocalDateTime.now())
            .balanceBefore(new BigDecimal("1000.00"))
            .balanceAfter(new BigDecimal("750.00"))
            .status(TransactionStatus.SUCCESS)
            .build();
        
        ConsoleFormatter.printTransactionCompact(transaction);
        String output = outputStream.toString();
        
        assertTrue(output.contains("TXN-000000000003"), "Should contain transaction ID");
        assertTrue(output.contains("TRANSFER"), "Should contain type");
    }
    
    // ==================== ACCOUNT FORMATTING ====================
    
    @Test
    @Order(14)
    @DisplayName("14. printAccountSummary displays account details")
    void testPrintAccountSummary() {
        Account account = new Account("ACC-00000001", "John Doe", AccountType.CHECKING, BigDecimal.ZERO);
        account.deposit(new BigDecimal("1000.00"), "TXN-TEST001");
        
        ConsoleFormatter.printAccountSummary(account);
        String output = outputStream.toString();
        
        assertTrue(output.contains("ACC-00000001"), "Should contain account number");
        assertTrue(output.contains("John Doe"), "Should contain customer name");
        assertTrue(output.contains("CHECKING"), "Should contain account type");
        assertTrue(output.contains("1,000.00"), "Should contain formatted balance");
    }
    
    @Test
    @Order(15)
    @DisplayName("15. printAccountSummary handles null gracefully")
    void testPrintAccountSummaryNull() {
        assertDoesNotThrow(() -> ConsoleFormatter.printAccountSummary(null),
            "Should handle null account without throwing");
        
        String output = outputStream.toString();
        assertTrue(output.contains("null"), "Should indicate null account");
    }
    
    @Test
    @Order(16)
    @DisplayName("16. printStatement displays full account statement")
    void testPrintStatement() {
        Account account = new Account("ACC-00000002", "Jane Smith", AccountType.SAVINGS, BigDecimal.ZERO);
        account.deposit(new BigDecimal("5000.00"), "TXN-TEST002");
        account.withdraw(new BigDecimal("500.00"), "TXN-TEST003");
        
        ConsoleFormatter.printStatement(account);
        String output = outputStream.toString();
        
        assertTrue(output.contains("ACCOUNT STATEMENT"), "Should contain statement header");
        assertTrue(output.contains("ACC-00000002"), "Should contain account number");
        assertTrue(output.contains("Jane Smith"), "Should contain customer name");
        assertTrue(output.contains("Transaction History"), "Should contain transaction section");
    }
    
    @Test
    @Order(17)
    @DisplayName("17. printStatement handles account with no transactions")
    void testPrintStatementNoTransactions() {
        Account account = new Account("ACC-00000003", "Empty Account", AccountType.CHECKING, BigDecimal.ZERO);
        
        ConsoleFormatter.printStatement(account);
        String output = outputStream.toString();
        
        assertTrue(output.contains("No transactions yet"), "Should indicate no transactions");
    }
    
    @Test
    @Order(18)
    @DisplayName("18. printAccountCompact displays compact format")
    void testPrintAccountCompact() {
        Account account = new Account("ACC-00000004", "Compact Test", AccountType.CHECKING, BigDecimal.ZERO);
        account.deposit(new BigDecimal("2500.50"), "TXN-TEST004");
        
        ConsoleFormatter.printAccountCompact(account);
        String output = outputStream.toString();
        
        assertTrue(output.contains("ACC-00000004"), "Should contain account number");
        assertTrue(output.contains("Compact Test"), "Should contain customer name");
        assertTrue(output.contains("2,500.50"), "Should contain formatted balance");
    }
    
    // ==================== MENU FORMATTING ====================
    
    @Test
    @Order(19)
    @DisplayName("19. printMenu displays numbered menu options")
    void testPrintMenu() {
        String[] options = {"Option 1", "Option 2", "Option 3"};
        ConsoleFormatter.printMenu("Test Menu", options);
        String output = outputStream.toString();
        
        assertTrue(output.contains("Test Menu"), "Should contain menu title");
        assertTrue(output.contains("1."), "Should contain option 1");
        assertTrue(output.contains("2."), "Should contain option 2");
        assertTrue(output.contains("3."), "Should contain option 3");
        assertTrue(output.contains("Option 1"), "Should contain option 1 text");
    }
    
    @Test
    @Order(20)
    @DisplayName("20. printPrompt displays user prompt")
    void testPrintPrompt() {
        ConsoleFormatter.printPrompt("Enter your choice");
        String output = outputStream.toString();
        
        assertTrue(output.contains("Enter your choice"), "Should contain prompt text");
        assertTrue(output.contains(":"), "Should contain colon separator");
    }
    
    // ==================== HELPER METHODS ====================
    
    @Test
    @Order(21)
    @DisplayName("21. printBlankLine produces newline")
    void testPrintBlankLine() {
        ConsoleFormatter.printBlankLine();
        String output = outputStream.toString();
        
        assertEquals("\n", output, "Should produce single newline");
    }
    
    @Test
    @Order(22)
    @DisplayName("22. printBlankLines produces multiple newlines")
    void testPrintBlankLines() {
        ConsoleFormatter.printBlankLines(3);
        String output = outputStream.toString();
        
        assertEquals("\n\n\n", output, "Should produce three newlines");
    }
    
    @Test
    @Order(23)
    @DisplayName("23. clearConsole executes without errors")
    void testClearConsole() {
        assertDoesNotThrow(() -> ConsoleFormatter.clearConsole(),
            "clearConsole should not throw exception");
    }
    
    // ==================== EDGE CASES ====================
    
    @Test
    @Order(24)
    @DisplayName("24. Handles very long customer names with truncation")
    void testLongCustomerName() {
        String longName = "A".repeat(50);
        Account account = new Account("ACC-00000005", longName, AccountType.CHECKING, BigDecimal.ZERO);
        
        assertDoesNotThrow(() -> ConsoleFormatter.printAccountCompact(account),
            "Should handle long names without error");
    }
    
    @Test
    @Order(25)
    @DisplayName("25. Handles very large amounts with currency formatting")
    void testLargeAmounts() {
        Account account = new Account("ACC-00000006", "Rich Person", AccountType.SAVINGS, BigDecimal.ZERO);
        account.deposit(new BigDecimal("9999999.99"), "TXN-TEST005");
        
        ConsoleFormatter.printAccountSummary(account);
        String output = outputStream.toString();
        
        assertTrue(output.contains("9,999,999.99"), "Should format large amounts with commas");
    }
}
