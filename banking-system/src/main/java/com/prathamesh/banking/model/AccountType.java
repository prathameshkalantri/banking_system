package com.prathamesh.banking.model;

/**
 * sDefines the types of bank accounts supported by the system.
 * 
 * <p>Each account type has distinct business rules:
 * <ul>
 *   <li><b>CHECKING:</b> No minimum balance required, $2.50 fee after 10 free transactions per month</li>
 *   <li><b>SAVINGS:</b> $100 minimum balance required, earns 2% monthly interest, max 5 withdrawals per month</li>
 * </ul>
 * 
 * @author Prathamesh Kalantri
 * @version 1.0.0
 */
public enum AccountType {
    
    /**
     * Checking account with no minimum balance requirement.
     * 
     * <p><b>Business Rules:</b>
     * <ul>
     *   <li>First 10 transactions per month are free</li>
     *   <li>$2.50 fee charged for each transaction beyond the 10th</li>
     *   <li>No interest earned</li>
     *   <li>Unlimited transactions allowed</li>
     * </ul>
     */
    CHECKING,
    
    /**
     * Savings account with minimum balance and withdrawal restrictions.
     * 
     * <p><b>Business Rules:</b>
     * <ul>
     *   <li>Minimum balance of $100 must be maintained</li>
     *   <li>Maximum 5 withdrawals per month</li>
     *   <li>Earns 2% interest applied monthly</li>
     *   <li>No transaction fees</li>
     * </ul>
     */
    SAVINGS
}
